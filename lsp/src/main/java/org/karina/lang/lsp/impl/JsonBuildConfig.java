package org.karina.lang.lsp.impl;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import karina.lang.Option;
import karina.lang.Result;
import org.karina.lang.lsp.lib.BuildConfig;
import org.karina.lang.lsp.lib.RealFileSystem;

import java.io.IOException;
import java.net.URI;

public record JsonBuildConfig(URI projectRootUri, URI clientRootUri) implements BuildConfig {
    private static final Gson gson = new Gson();

    public static Result<BuildConfig, IOException> fromJsonFile(URI clientRootUri, RealFileSystem rfs) {
        var config = clientRootUri.resolve("karina-build.json");
        if (!rfs.exists(config)) {
            return Result.err(new IOException("Build configuration file not found: " + config));
        }

        var fileContent = rfs.readFileFromDisk(config);
        var object = fileContent.flatMap(JsonBuildConfig::asJsonObject);
        var source = object.flatMap(ref -> getString(ref, "source"));
        var existingUri = source.flatMap(ref -> asExistingUri(rfs, clientRootUri, ref));
        var directory = existingUri.flatMap(ref -> asDirectory(rfs, ref));

        return directory.map(ref -> new JsonBuildConfig(ref, clientRootUri));
    }

    private static Result<URI, IOException> asExistingUri(RealFileSystem rfs, URI clientRootUri, String content) {
        var resolvedUri = clientRootUri.resolve(content).normalize();
        if (rfs.exists(resolvedUri)) {
            return Result.ok(resolvedUri);
        } else {
            return Result.err(new IOException("Source directory does not exist: " + resolvedUri));
        }
    }

    private static Result<URI, IOException> asDirectory(RealFileSystem rfs, URI url) {
        if (rfs.isDirectory(url)) {
            return Result.ok(url);
        } else {
            return Result.err(new IOException("Expected a directory at: " + url));
        }
    }

    private static Result<JsonObject, IOException> asJsonObject(String content) {
        return Result.safeCall(() -> gson.fromJson(content, JsonObject.class))
                     .flatMap(obj -> Option.fromNullable(obj).okOrGet(() -> new IOException("Missing object.")))
                     .mapErr(e -> new IOException("Failed to parse build config: " + e.getMessage()));
    }

    private static Result<String, IOException> getString(JsonObject jsonObject, String key) {
        JsonElement element = jsonObject.get(key);
        if (element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            return Result.ok(element.getAsString());
        }
        return Result.err(new IOException("Key '" + key + "' not found or is not a string in the JSON object."));
    }


}
