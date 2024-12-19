package org.karina.lang.lsp.internal;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.eclipse.lsp4j.DidChangeWatchedFilesParams;
import org.eclipse.lsp4j.services.WorkspaceService;
import org.karina.lang.lsp.EventHandler;

import java.net.URI;
import java.util.ArrayList;

class WorkspaceEventProxy implements WorkspaceService {


    @Override
    public void didChangeConfiguration(DidChangeConfigurationParams params) {
        try {
            if (params.getSettings() instanceof JsonObject object) {
                if (object.has("excludeErrorFiles")) {
                    EventHandler.INSTANCE.settings().excludeErrorFiles =
                            object.get("excludeErrorFiles").getAsBoolean();
                }
            } else if (params.getSettings() instanceof String json) {
                var settings = new Gson().fromJson(json, JsonObject.class);
                didChangeConfiguration(new DidChangeConfigurationParams(settings));
            } else {
                EventHandler.INSTANCE.errorMessage("Invalid settings type");
            }
        } catch (Exception e) {
            EventHandler.INSTANCE.errorMessage(e.getMessage());
        }
    }

    @Override
    public void didChangeWatchedFiles(DidChangeWatchedFilesParams params) {
        var changedFiles = params.getChanges();
        var created = new ArrayList<URI>();
        var deleted = new ArrayList<URI>();
        var changed = new ArrayList<URI>();

        for (var changedFile : changedFiles) {
            var uri = EventHandler.INSTANCE.toUri(changedFile.getUri());
            switch (changedFile.getType()) {
                case Created -> created.add(uri);
                case Changed -> changed.add(uri);
                case Deleted -> deleted.add(uri);
            }
        }
        if (!changed.isEmpty())
            EventHandler.INSTANCE.onChangeFull(changed);
        if (!created.isEmpty())
            EventHandler.INSTANCE.onCreate(created);
        if (!deleted.isEmpty())
            EventHandler.INSTANCE.onDelete(deleted);
    }
}
