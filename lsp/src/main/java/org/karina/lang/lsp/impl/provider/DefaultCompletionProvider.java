package org.karina.lang.lsp.impl.provider;

import karina.lang.Option;
import lombok.AllArgsConstructor;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.Position;
import org.karina.lang.compiler.model_api.ClassModel;
import org.karina.lang.compiler.model_api.FieldModel;
import org.karina.lang.compiler.model_api.MethodModel;
import org.karina.lang.compiler.stages.attrib.AttributionProcessor;
import org.karina.lang.compiler.utils.Context;
import org.karina.lang.compiler.model_api.Generic;
import org.karina.lang.compiler.utils.Region;
import org.karina.lang.compiler.utils.RegionOf;
import org.karina.lang.compiler.utils.logging.DiagnosticCollection;
import org.karina.lang.compiler.utils.logging.errors.AttribError;
import org.karina.lang.lsp.impl.CodeDiagnosticInformationBuilder;
import org.karina.lang.lsp.lib.provider.CompletionProvider;
import org.karina.lang.lsp.lib.events.EventService;
import org.karina.lang.lsp.test_compiler.CompiledModelIndex;
import org.karina.lang.lsp.test_compiler.ProviderArgs;

import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
public class DefaultCompletionProvider implements CompletionProvider {
    private final EventService eventService;

    @Override
    public List<CompletionItem> getItems(
            ProviderArgs index,
            URI uri,
            Position position
    ) {
        var region_position = new Region.Position(
                position.getLine(),
                position.getCharacter()
        );
        var config = Context.ContextHandling.of(
                false,
                true,
                true
        );
        config = config.enableMissingMembersSupport();

        var diagnostics = new DiagnosticCollection();
        if (!(index.models().importedModel() instanceof Option.Some(var importedModel))) {
            return List.of();
        }

        var success = Context.run(
            config,
            diagnostics,
            null,
            null,
                (c ) -> {
                    var attributionProcessor = new AttributionProcessor();
                    attributionProcessor.attribTree(c, importedModel);
                    return "OK";
                }
        );
        if (success != null) {
            return List.of();
        }
        // collect errors in same line

        var items = new ArrayList<CompletionItem>();
        for (var diagnostic : diagnostics) {
            if (diagnostic.entry() instanceof AttribError.UnknownMember(
                    Region region,
                    String className,
                    String name,
                    Set<MethodModel> availableMethods,
                    Set<FieldModel> availableFields,
                    Set<ClassModel> availableClasses,
                    List<RegionOf<String>> protectedMembers,
                    List<RegionOf<String>> otherProtected
            )) {
                var file = CodeDiagnosticInformationBuilder.resourceToFile(region.source().resource());
                if (file == null) {
                    continue;
                }
                if (!file.equals(uri)) {
                    continue;
                }
                if (!region.doesContainPosition(region_position)) {
                    continue;
                }

                for (var classModel : availableClasses) {
                    var item = new CompletionItem(classModel.name());
                    item.setKind(CompletionItemKind.Class);
                    var genericString = String.join(
                            ", ",
                            classModel.generics().stream().map(Generic::name).toList()
                    );
                    item.setDetail(classModel.name() + "<" + genericString + ">");
                    item.setInsertText(classModel.name());
                    item.setSortText(classModel.name());
                    items.add(item);
                }
                for (var field : availableFields) {
                    var item = new CompletionItem(field.name() + ": " + field.type().toString());
                    item.setKind(CompletionItemKind.Field);
                    item.setDetail(field.name() + ": " + field.type().toString());
                    item.setInsertText(field.name());
                    item.setSortText(field.name());
                    items.add(item);
                }
                for (var method : availableMethods) {
                    var signatureString = getSignatureString(method);
                    var item = new CompletionItem(signatureString);
                    item.setKind(CompletionItemKind.Method);
                    item.setDetail(Modifier.toString(method.modifiers()) + " fn " + signatureString);
                    item.setSortText(method.name());

                    var args = String.join(", ", method.parameters());
                    item.setInsertText(method.name() + "(" + args + ")");
                    items.add(item);
                }

            }
        }

        return items;
    }
    private static String getSignatureString(MethodModel method) {
        return method.name() + "(" + getParamString(method) + ") -> " + method.signature().returnType();
    }

    private static String getParamString(MethodModel method) {
        if (method.parameters().size() != method.signature().parameters().size()) {
            return String.join(", ", method.parameters());
        }
        var parts = new ArrayList<String>();
        for (var i = 0; i < method.parameters().size(); i++) {
            var name = method.parameters().get(i);
            var type = method.signature().parameters().get(i);
            parts.add(name + ": " + type);
        }

        return String.join(", ", parts);
    }
}
