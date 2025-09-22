package org.karina.lang.lsp.test_compiler;

import karina.lang.Option;
import org.karina.lang.compiler.model_api.MethodModel;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Types;
import org.karina.lang.lsp.lib.VirtualFile;
import org.karina.lang.lsp.lib.VirtualFileElevator;
import org.karina.lang.lsp.lib.VirtualFileSystem;
import org.karina.lang.lsp.lib.VirtualFileTreeNode;
import org.karina.lang.lsp.lib.events.EventService;

import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.List;

public record ProviderArgs(
    VirtualFileSystem vfs,
    VirtualFileTreeNode.NodeMapping fileTree,
    VirtualFileElevator elevator,
    CompiledModelIndex models
) {

    public Option<ObjectPath> getObjectPathOfURI(URI uri) {
        var nodes = VirtualFileTreeNode.flatten(this.fileTree.root());

        for (var node : nodes) {
            if (node.content().uri().equals(uri)) {
                return Option.some(node.path());
            }
        }
        return Option.none();
    }
    public Option<URI> getURIOfPath(ObjectPath path) {
        var nodes = VirtualFileTreeNode.flatten(this.fileTree.root());

        for (var node : nodes) {
            if (node.path().equals(path)) {
                return Option.some(node.content().uri());
            }
        }
        return Option.none();
    }


    public Option<VirtualFile> getFile(URI uri) {
        return this.vfs.getFile(uri);
    }

    public Option<VirtualFile.CompiledFileCacheState> getCache(VirtualFile virtualFile) {
        return this.elevator.awaitCache(this.fileTree, virtualFile);
    }
    public Option<VirtualFile.CompiledFileCacheState> getCache(URI uri) {
        return getFile(uri).flatMap(ref -> this.elevator.awaitCache(this.fileTree, ref));
    }

    public ClassPointer getClassPointerOfURI(URI uri, Model model) {
        if (!(this.getObjectPathOfURI(uri) instanceof Option.Some(var path))) {
            return null;
        }
        return model.getClassPointer(KType.KARINA_LIB, path);
    }


    public Option<MethodModel> findMain(URI uri, EventService service) {
        if (!(models().importedModel() instanceof Option.Some(var model))) {
            return Option.none();
        }
        if (!(getObjectPathOfURI(uri) instanceof Option.Some(var mainPath))) {
            return Option.none();
        }

        var classPointer = model.getClassPointer(KType.KARINA_LIB, mainPath);
        if (classPointer == null) {
            service.warningMessage("No class: " + uri);
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
