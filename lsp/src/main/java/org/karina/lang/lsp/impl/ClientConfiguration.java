package org.karina.lang.lsp.impl;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import karina.lang.Option;
import karina.lang.Result;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

@ToString
public class ClientConfiguration {
    private static final Gson GSON = new Gson();

    private @Nullable LoggingLevel logLevel = null;

    public Option<LoggingLevel> logLevel() {
        return Option.fromNullable(this.logLevel);
    }


    public static Result<ClientConfiguration, IOException> fromJson(String json) {
        return Result.safeCall(() -> GSON.fromJson(json, ClientConfiguration.class))
                     .mapErr(e -> new IOException("Failed to parse JSON content", e));
    }
    public static Result<ClientConfiguration, IOException> fromJson(JsonElement json) {
        return Result.safeCall(() -> GSON.fromJson(json, ClientConfiguration.class))
                     .mapErr(e -> new IOException("Failed to parse JSON content", e));
    }

    public static ClientConfiguration getDefault() {
        return new ClientConfiguration();
    }



    @RequiredArgsConstructor
    @Getter
    @Accessors(fluent = true)
    public enum LoggingLevel {
        @SerializedName(value = "none", alternate = {"NONE", "None"})
        NONE("none"),
        @SerializedName(value = "basic", alternate = {"BASIC", "Basic"})
        BASIC("none"),
        @SerializedName(value = "verbose", alternate = {"VERBOSE", "Verbose"})
        VERBOSE("basic"),
        @SerializedName(value = "full", alternate = {"FULL", "Full"})
        FULL("verbose");

        private final String internalLogName;
    }
}
