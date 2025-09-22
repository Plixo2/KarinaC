package org.karina.lang.lsp.impl.provider;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import karina.lang.Option;
import karina.lang.Range;
import lombok.RequiredArgsConstructor;
import org.karina.lang.compiler.model_api.ClassModel;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.model_api.impl.ModelBuilder;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Region;
import org.karina.lang.compiler.utils.Resource;
import org.karina.lang.lsp.lib.VirtualFile;
import org.karina.lang.lsp.lib.VirtualFileElevator;
import org.karina.lang.lsp.lib.VirtualFileSystem;
import org.karina.lang.lsp.lib.VirtualFileTreeNode;
import org.karina.lang.lsp.lib.events.ClientEvent;
import org.karina.lang.lsp.lib.events.EventService;
import org.karina.lang.lsp.lib.provider.CompileProvider;
import org.karina.lang.lsp.lib.provider.ExecuteCommandProvider;
import org.karina.lang.lsp.test_compiler.CompiledModelIndex;
import org.karina.lang.lsp.test_compiler.ProviderArgs;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class DefaultExecuteCommandProvider implements ExecuteCommandProvider {
    private final EventService eventService;

    @Override
    public Object executeCommand(ProviderArgs index, CompileProvider compileProvider, String command, List<Object> args) {
        if (command.equals("karina.run.main")) {
            if (!args.isEmpty()) {
                this.eventService.warningMessage("Too many arguments for command: " + command);
                return null;
            }
            var path = new ObjectPath("main");
            return compileProvider.compile(index, path);
        } else if (command.equals("karina.run.file")) {
            if (args.size() > 1) {
                this.eventService.warningMessage("Too many arguments for command: " + command);
                return null;
            }
            if (args.isEmpty()) {
                this.eventService.warningMessage("Missing argument for command: " + command);
                return null;
            }
            var uriStr = Objects.toString(args.getFirst());
            if (uriStr.startsWith("\"") && uriStr.endsWith("\"")) {
                uriStr = uriStr.substring(1, uriStr.length() - 1);
            }
            var uri = VirtualFileSystem.toUri(uriStr);
            return compileProvider.compile(index, uri);
        } else if (command.equals("karina.classes")) {
            if (!args.isEmpty()) {
                this.eventService.warningMessage("Too many arguments for command: " + command);
                return null;
            }
            return getAllClasses(index);
        } else {
            this.eventService.warningMessage("Unknown command: " + command);
        }
        return null;
    }


    private JsonArray getAllClasses(ProviderArgs index) {
        var result = new JsonArray();

        var allClasses = new ArrayList<ClassModel>();
        if (index.models().languageModel() instanceof Option.Some(var userModel)) {
            allClasses.addAll(userModel.getUserClasses());
        } else {
            allClasses.addAll(classModels(index.fileTree(), index.elevator()));
        }
        allClasses.addAll(index.models().binaryModel().map(Model::getBinaryClasses).orElse(List.of()));

        for (var userClass : allClasses) {
            var region = userClass.region();
            var resource = region.source().resource();
            var path = userClass.path();
            result.add(createClassObject(region, resource, path));
        }

        return result;
    }

    private JsonObject createClassObject(Region region, Resource resource, ObjectPath path) {
        var object = new JsonObject();
        String prefix;
        if (resource instanceof VirtualFile) {
            prefix = "$(symbol-class)";
        } else {
            prefix = "$(file-binary)";
        }

        object.addProperty("name", prefix + path.mkString("::"));
        object.addProperty("file", resource.identifier());
        var range = new JsonObject();
        var start = new JsonObject();
        var end = new JsonObject();
        start.addProperty("line", region.start().line());
        start.addProperty("character", region.start().column());
        end.addProperty("line", region.end().line());
        end.addProperty("character", region.end().column());

        range.add("start", start);
        range.add("end", end);
        object.add("range", range);

        return object;
    }

    private List<ClassModel> classModels(
            VirtualFileTreeNode.NodeMapping fileTree,
            VirtualFileElevator elevator) {
        var result = new ArrayList<ClassModel>();
        for (var virtualFile : fileTree.files()) {
            var cache = elevator.awaitCache(fileTree, virtualFile);
            if (!(cache instanceof Option.Some(var cacheState))) {
                this.eventService.warningMessage("File " + virtualFile.uri() + " has no compiled cache.");
                continue;
            }
            if (cacheState instanceof VirtualFile.CompiledFileCacheState.Success success) {
                result.addAll(success.allClasses().getUserClasses());
            }
        }

        return result;
    }
}
