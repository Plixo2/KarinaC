package org.karina.lang.compiler.utils;

import com.google.common.collect.ImmutableList;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.jvm_loading.JavaResource;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


/**
 * Used for debugging parse trees to visualize what changed during parsing with a diff tool.
 */
public class DebugWriter {

    public static void write(Object object, String path) {

        var builder = new GsonBuilder()
                .registerTypeAdapter(FileResource.class, new NullGsonWriter())
                .registerTypeAdapter(TextSource.class, new NullGsonWriter())
                .registerTypeAdapter(Region.class, new NullGsonWriter());
        var gson = builder.setPrettyPrinting().create();

        var file = new java.io.File(path);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (var writer = new FileWriter(path)) {
            gson.toJson(object, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void writeCompressed(Object object, String path) {

        var gson = new Gson();

        var file = new java.io.File(path);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        try (FileOutputStream fos = new FileOutputStream(path);
             GZIPOutputStream gzipOut = new GZIPOutputStream(fos)) {
            var string = gson.toJson(object);
            byte[] jsonBytes = string.getBytes(StandardCharsets.UTF_8);
            gzipOut.write(jsonBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static <T> @Nullable T loadCompressed(String path, Class<T> clz) {

        var gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(ImmutableList.class, new ImmutableListDeserializer());
        gsonBuilder.registerTypeAdapter(Resource.class, new ResourceDeserializer());
        var gson = gsonBuilder.create();

        var file = new java.io.File(path);
        if (!file.exists()) {
            return null;
        }

        try (FileInputStream fis = new FileInputStream(path);
             GZIPInputStream gzipIn = new GZIPInputStream(fis)) {

            // Read bytes from the GZIP stream
            byte[] buffer = gzipIn.readAllBytes();

            // Convert bytes back to a JSON string
            String jsonString = new String(buffer);

            // Deserialize the JSON string back into an object
            return gson.fromJson(jsonString, clz);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class ImmutableListDeserializer implements JsonDeserializer<ImmutableList> {
        @Override
        public ImmutableList deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            // Convert JSON array into a List, then to ImmutableList
            JsonArray jsonArray = json.getAsJsonArray();
            var deserialize = context.deserialize(jsonArray, ArrayList.class);
            return ImmutableList.copyOf((ArrayList)deserialize);
        }
    }

    public static class ResourceDeserializer implements JsonDeserializer<Resource> {
        @Override
        public Resource deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return context.deserialize(json, JavaResource.class);
        }
    }


    private static class NullGsonWriter extends TypeAdapter<Object> {

        @Override
        public void write(JsonWriter out, Object value) throws IOException {

            out.beginObject();
            out.endObject();

        }

        @Override
        public Object read(JsonReader in) throws IOException {

            in.beginObject();
            in.endObject();
            return null;

        }
    }
}
