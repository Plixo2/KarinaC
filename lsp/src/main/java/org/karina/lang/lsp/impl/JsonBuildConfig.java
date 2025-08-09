package org.karina.lang.lsp.impl;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.karina.lang.lsp.KarinaLanguageServer;
import org.karina.lang.lsp.lib.BuildConfig;
import org.karina.lang.lsp.lib.IOResult;

import java.net.URI;

public record JsonBuildConfig(URI projectRootUri, URI clientRootUri) implements BuildConfig {
    private static final Gson gson = new Gson();

    public static IOResult<JsonBuildConfig> fromJsonFile(URI clientRootUri, KarinaLanguageServer kls) {
        var config = clientRootUri.resolve("karina-build.json");
        if (!kls.rfs.exists(config)) {
            return new IOResult.Error<>(new IllegalArgumentException("Build configuration file not found: " + config));
        }

        var fileContent = kls.rfs.readFileFromDisk(config);
        var object = fileContent.flatMap(JsonBuildConfig::asJsonObject);
        var source = object.flatMap(ref -> getString(ref, "source"));
        var existingUri = source.flatMap(ref -> asExistingUri(kls, clientRootUri, ref));
        var directory = existingUri.flatMap(ref -> asDirectory(kls, ref));

        return directory.map(ref -> new JsonBuildConfig(ref, clientRootUri));
    }

    private static IOResult<URI> asExistingUri(KarinaLanguageServer kls, URI clientRootUri, String content) {
        var resolvedUri = clientRootUri.resolve(content).normalize();
        if (!kls.rfs.exists(resolvedUri)) {
            return new IOResult.Error<>(new IllegalArgumentException("Source directory does not exist: " + resolvedUri));
        } else {
            return new IOResult.Success<>(resolvedUri);
        }
    }

    private static IOResult<URI> asDirectory(KarinaLanguageServer kls, URI url) {
        if (!kls.rfs.isDirectory(url)) {
            return new IOResult.Error<>(new IllegalArgumentException("Expected a directory at: " + url));
        } else {
            return new IOResult.Success<>(url);
        }
    }

    private static IOResult<JsonObject> asJsonObject(String content) {
        try {
            return new IOResult.Success<>(gson.fromJson(content, JsonObject.class));
        } catch(Exception e) {
            return new IOResult.Error<>(e);
        }
    }

    private static IOResult<String> getString(JsonObject jsonObject, String key) {
        JsonElement element = jsonObject.get(key);
        if (element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            return new IOResult.Success<>(element.getAsString());
        }
        return new IOResult.Error<>(new IllegalArgumentException("Key '" + key + "' not found or is not a string in the JSON object."));
    }


}
