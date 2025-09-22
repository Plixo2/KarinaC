package org.karina.lang.lsp.impl.provider;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import karina.lang.Option;
import org.eclipse.lsp4j.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model_api.ClassModel;
import org.karina.lang.compiler.model_api.FieldModel;
import org.karina.lang.compiler.model_api.MethodModel;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Region;
import org.karina.lang.compiler.utils.RegionOf;
import org.karina.lang.compiler.utils.logging.DidYouMean;
import org.karina.lang.compiler.utils.logging.errors.AttribError;
import org.karina.lang.compiler.utils.logging.errors.Error;
import org.karina.lang.compiler.utils.logging.errors.ImportError;
import org.karina.lang.lsp.lib.provider.CodeActionProvider;
import org.karina.lang.lsp.test_compiler.CompiledModelIndex;
import org.karina.lang.lsp.test_compiler.ProviderArgs;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

public class DefaultCodeActionProvider implements CodeActionProvider {

    @Override
    public List<CodeAction> getAction(ProviderArgs index, URI ignored, Range range, CodeActionContext context) {
        var edits = new ArrayList<CodeAction>();

        for (var diagnostic : context.getDiagnostics()) {
            if (diagnostic.getData() instanceof JsonObject jsonObject) {
                var fileURI = jsonObject.get("URI");
                if (fileURI == null || !fileURI.isJsonPrimitive()) {
                    continue;
                }
                var uri = fileURI.getAsString();

                if (handleImports(
                        jsonObject,
                        uri
                ) instanceof Option.Some(var actions)) {
                    edits.addAll(actions);
                }

                if (handleReplacements(
                        diagnostic.getRange(),
                        jsonObject,
                        uri
                ) instanceof Option.Some(var actions)) {
                    edits.addAll(actions);
                }

            }
        }
        return edits;
    }


    private static Option<List<CodeAction>> handleImports(
            JsonObject jsonObject,
            String uri
    ) {
        var pathToImport = jsonObject.get("imports");
        if (pathToImport == null || !pathToImport.isJsonArray()) {
            return Option.none();
        }
        var actions = new ArrayList<CodeAction>();
        for (var jsonElement : pathToImport.getAsJsonArray()) {
            if (!jsonElement.isJsonPrimitive()) {
                continue;
            }
            var path = jsonElement.getAsString();

            var textEdit = new TextEdit();
            textEdit.setNewText("import " + path + "\n");
            textEdit.setRange(new Range(new Position(0, 0), new Position(0, 0)));
            var workspaceEdit = new WorkspaceEdit();
            workspaceEdit.setChanges(Map.of(uri, List.of(textEdit)));

            CodeAction action = new CodeAction("Import '" + path + "'");
            action.setKind(CodeActionKind.QuickFix);
            action.setEdit(workspaceEdit);
            actions.add(action);
        }
        return Option.some(actions);
    }

    private static Option<List<CodeAction>> handleReplacements(@Nullable Range range, JsonObject jsonObject, String uri) {
        var candidates = jsonObject.get("replacements");
        if (candidates == null || range == null || !candidates.isJsonArray()) {
            return Option.none();
        }
        var actions = new ArrayList<CodeAction>();

        var isFirst = true;
        for (var jsonElement : candidates.getAsJsonArray()) {
            if (!jsonElement.isJsonPrimitive()) {
                continue;
            }

            var name = jsonElement.getAsString();

            var textEdit = new TextEdit();
            textEdit.setNewText(name);
            textEdit.setRange(range);
            var workspaceEdit = new WorkspaceEdit();
            workspaceEdit.setChanges(Map.of(uri, List.of(textEdit)));

            CodeAction action = new CodeAction("Use '" + name + "'");

            if (isFirst) {
                action.setIsPreferred(true);
            }
            action.setKind(CodeActionKind.QuickFix);
            action.setEdit(workspaceEdit);
            actions.add(action);

            isFirst = false;
        }
        return Option.some(actions);
    }

    @Override
    public JsonObject getCodeActionFromError(Error error, URI file) {
        var object = new JsonObject();
        object.addProperty("URI", file.toString());
        var imports = new ArrayList<ObjectPath>();
        var replacements = new ArrayList<String>();

        switch (error) {
            case ImportError.UnknownImportType(
                    _,
                    String name,
                    Set<String> availableNames,
                    Set<ObjectPath> availablePaths
            ) -> handleUnknownImport(name, availableNames, availablePaths, imports, replacements);
            case AttribError.UnknownMember(
                    _,
                    _,
                    @Nullable String name,
                    Set<MethodModel> availableMethods,
                    Set<FieldModel> availableFields,
                    Set<ClassModel> availableClasses,
                    _,
                    _
            ) -> {
                if (name == null) {
                    break;
                }
                var fieldNames  = availableFields.stream().map(FieldModel::name).collect(Collectors.toSet());
                var methodNames = availableMethods.stream().map(MethodModel::name).collect(Collectors.toSet());
                var classNames  = availableClasses.stream().map(ClassModel::name).collect(Collectors.toSet());

                var allNames = new HashSet<String>();

                allNames.addAll(fieldNames);
                allNames.addAll(methodNames);
                allNames.addAll(classNames);

                replacements.addAll(DidYouMean.sort(allNames, name));
            }
            case AttribError.UnknownIdentifier(
                    _,
                    String name,
                    Set<String> available
            ) -> {
                replacements.addAll(DidYouMean.sort(available, name));
            }
            default -> {
                // Other errors are not handled for code actions

            }
        }




        if (!imports.isEmpty()) {
            var jsonArray = new JsonArray();
            for (var path : imports) {
                jsonArray.add(path.mkString("::"));
            }
            object.add("imports", jsonArray);
        }
        if (!replacements.isEmpty()) {
            var jsonArray = new JsonArray();
            for (var replacement : replacements) {
                jsonArray.add(replacement);
            }
            object.add("replacements", jsonArray);
        }

        return object;
    }

    @Contract(mutates = "param4, param5")
    private static void handleUnknownImport(
            String name,
            Set<String> availableNames,
            Set<ObjectPath> availablePaths,
            ArrayList<ObjectPath> imports,
            List<String> replacements
    ) {
        for (var path : availablePaths) {
            if (path.isEmpty()) {
                continue;
            }
            if (path.last().equals(name)) {
                imports.add(path);
            }
        }

        var pathLookup = new HashMap<String, List<ObjectPath>>();
        var names = new HashSet<String>();
        var simpleNames = new HashSet<>(availableNames);
        for (var objectPath : availablePaths) {
            if (objectPath.isEmpty()) continue;

            var simpleName = objectPath.last();
            pathLookup.computeIfAbsent(simpleName, _ -> new ArrayList<>()).add(objectPath);
            simpleNames.add(objectPath.mkString("::"));
        }
        names.addAll(simpleNames);
        names.addAll(pathLookup.keySet());

        var suggestions = DidYouMean.suggestions(names, name, 10);
        for (var suggestion : suggestions) {
            if (simpleNames.contains(suggestion)) {
                replacements.add(suggestion.replace("$", ""));
            }
            var paths = pathLookup.get(suggestion);
            if (paths != null) {
                for (var path : paths) {
                    replacements.add(path.mkString("::").replace("$", ""));
                }
            }
        }
    }
}
