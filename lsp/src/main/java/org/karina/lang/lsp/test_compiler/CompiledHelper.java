package org.karina.lang.lsp.test_compiler;

import karina.lang.Option;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.MessageType;
import org.karina.lang.compiler.model_api.MethodModel;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Types;
import org.karina.lang.compiler.utils.logging.DiagnosticCollection;
import org.karina.lang.lsp.impl.CodeDiagnosticInformationBuilder;
import org.karina.lang.lsp.lib.VirtualFile;
import org.karina.lang.lsp.lib.VirtualFileTreeNode;
import org.karina.lang.lsp.lib.events.ClientEvent;
import org.karina.lang.lsp.lib.events.EventService;

import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompiledHelper {

    public static void clearErrors(List<VirtualFile> files, EventService service) {
        for (var file : files) {
            service.send(new ClientEvent.PublishDiagnostic(file.uri(), List.of()));
        }
    }

    public static void pushErrors(DiagnosticCollection logs, EventService service) {
        Map<VirtualFile, List<Diagnostic>> diagnostics = new HashMap<>();

        for (var log : logs) {
            var information = new CodeDiagnosticInformationBuilder();
            log.entry().addInformation(information);

            var diagnosticAndFile = CodeDiagnosticInformationBuilder.toDiagnosticAndFile(information);
            if (diagnosticAndFile == null) {
                service.send(new ClientEvent.Log("Could not convert diagnostic: " + information.getMessageString(), MessageType.Log));
                continue;
            }
            var diagnosticList =
                    diagnostics.computeIfAbsent(diagnosticAndFile.file(), _ -> new ArrayList<>());
            diagnosticList.add(diagnosticAndFile.diagnostic());
        }

        for (var value : diagnostics.entrySet()) {
            var file = value.getKey();
            var diagnosticsList = value.getValue();

            if (diagnosticsList.isEmpty()) {
                continue;
            }
            service.send(new ClientEvent.PublishDiagnostic(file.uri(), diagnosticsList));
        }
    }



    public static Option<ObjectPath> getObjectPathOfURI(VirtualFileTreeNode treeNode, URI uri) {
        var nodes = VirtualFileTreeNode.flatten(treeNode);

        for (var node : nodes) {
            if (node.content().uri().equals(uri)) {
                return Option.some(node.path());
            }
        }
        return Option.none();
    }


    public static Option<MethodModel> findMain(Option<Model> latest, VirtualFileTreeNode treeNode, URI uri, EventService service) {
        if (!(latest instanceof Option.Some(var model))) {
            return Option.none();
        }
        if (!(CompiledHelper.getObjectPathOfURI(treeNode, uri) instanceof Option.Some(var mainPath))) {
            service.send(new ClientEvent.Log(
                    "No path" + uri,
                    MessageType.Log
            ));
            return Option.none();
        }

        var classPointer = model.getClassPointer(KType.KARINA_LIB, mainPath);
        if (classPointer == null) {
            service.send(new ClientEvent.Log(
                    "No class: " + mainPath,
                    MessageType.Log
            ));
            return Option.none();
        }

        var currentClassModel = model.getClass(classPointer);

        for (var method : currentClassModel.methods()) {
            if (!Modifier.isStatic(method.modifiers()) || !Modifier.isPublic(method.modifiers())) {
                continue;
            }
            if (!method.name().equals("main")) {
                continue;
            }
            if (method.erasedParameters().size() != 1) {
                continue;
            }
            if (!method.signature().returnType().isVoid()) {
                continue;
            }
            var firstParam = method.erasedParameters().getFirst();
            if (!Types.erasedEquals(new KType.ArrayType(KType.STRING), firstParam)) {
                continue;
            }
            return Option.some(method);
        }
        return Option.none();
    }

}
