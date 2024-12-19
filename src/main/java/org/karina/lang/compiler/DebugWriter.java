package org.karina.lang.compiler;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.FileWriter;
import java.io.IOException;


/**
 * Used for debugging parse trees to visualize what changed during parsing with a diff tool.
 */
public class DebugWriter {

    public static void write(Object object, String path) {

        var builder = new GsonBuilder()
            .registerTypeAdapter(TextSource.DefaultFile.class, new NullGsonWriter())
            .registerTypeAdapter(TextSource.class, new NullGsonWriter())
            .registerTypeAdapter(Span.class, new NullGsonWriter());
        var gson = builder.setPrettyPrinting().create();
        try (var writer = new FileWriter(path)) {
            gson.toJson(object, writer);
        } catch (IOException e) {
            e.printStackTrace();
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
