package karina.lang.json;

import karina.lang.Option;
import karina.lang.Result;
import karina.lang.string.Escape;

import java.math.BigDecimal;
import java.util.*;

public final class Json {
    private Json() {}

    public static final Null NULL = new Null();
    public static final Boolean TRUE = new Boolean(true);
    public static final Boolean FALSE = new Boolean(false);

    public sealed interface Value {
        default java.lang.String mkString() {
            return Json.mkString(JsonFormatter.DEFAULT, this, 0);
        }
        default java.lang.String mkString(JsonFormatter formatter) {
            return Json.mkString(formatter, this, 0);
        }

        default Option<Object> asObject() {
            return Option.instanceOf(Object.class, this);
        }
        default Option<Array> asArray() {
            return Option.instanceOf(Array.class, this);
        }
        default Option<Primitive> asPrimitive() {
            return Option.instanceOf(Primitive.class, this);
        }
        default Option<Boolean> asBoolean() {
            return Option.instanceOf(Boolean.class, this);
        }
        default Option<Number> asNumber() {
            return Option.instanceOf(Number.class, this);
        }
        default Option<String> asString() {
            return Option.instanceOf(String.class, this);
        }
    }

    public record Object(Map<java.lang.String, Value> fields) implements Value {
        public Option<Value> get(java.lang.String key) {
            return Option.fromNullable(this.fields.get(key));
        }
        public void add(java.lang.String key, Value value) {
            this.fields.put(key, value);
        }
    }

    public record Array(List<Value> values) implements Value, Iterable<Value> {
        public Option<Value> get(int index) {
            if (index < 0 || index >= this.values.size()) {
                return Option.none();
            }
            return Option.some(this.values.get(index));
        }

        @Override
        public Iterator<Value> iterator() {
            return this.values.iterator();
        }

        public void add(Value value) {
            this.values.add(value);
        }
    }

    public sealed interface Primitive extends Value {
        default java.lang.String text() {
            return switch (this) {
                case Boolean(var value) -> java.lang.String.valueOf(value);
                case Number(var value) -> java.lang.String.valueOf(value);
                case String(var value) -> value;
                case Null() -> "null";
            };
        }
    }
    public record Boolean(boolean value) implements Primitive {}
    public record Number(java.lang.Number value) implements Primitive { }
    public record String(java.lang.String value) implements Primitive {}
    public record Null() implements Primitive {}

    private static java.lang.String mkString(JsonFormatter formatter, Value element, int indent) {
        return switch (element) {
            case Object(var fields) -> {
                var bob = new StringBuilder();
                var indentStr = formatter.indentation().repeat(indent + 1);
                bob.append("{");
                bob.append(formatter.newline());
                var entryIterator = fields.entrySet().iterator();
                while (entryIterator.hasNext()) {
                    var entry = entryIterator.next();
                    var key = entry.getKey();
                    var value = entry.getValue();
                    bob.append(indentStr);
                    bob.append("\"");
                    bob.append(key);
                    bob.append("\":");
                    bob.append(formatter.fieldValueSeparator());
                    bob.append(mkString(formatter, value, indent + 1));
                    if (entryIterator.hasNext()) {
                        bob.append(",");
                    }
                    bob.append(formatter.newline());
                }

                bob.append(formatter.indentation().repeat(indent)).append("}");

                yield bob.toString();
            }
            case Array(var values) -> {
                var bob = new StringBuilder();
                var indentStr = formatter.indentation().repeat(indent + 1);

                bob.append("[");
                bob.append(formatter.newline());

                var valueIterator = values.iterator();
                while (valueIterator.hasNext()) {
                    var value = valueIterator.next();
                    bob.append(indentStr).append(mkString(formatter, value, indent + 1));
                    if (valueIterator.hasNext()) {
                        bob.append(",");
                    }
                    bob.append(formatter.newline());
                }

                bob.append(formatter.indentation().repeat(indent)).append("]");

                yield bob.toString();
            }
            case String(var value) -> "\"" + Escape.escapeJson(value) + "\"";
            case Number(var value) -> java.lang.String.valueOf(value);
            case Boolean(var value) -> java.lang.String.valueOf(value);
            case Null() -> "null";
        };
    }


    public static Result<Value, JsonParseException> parse(java.lang.String string) {
        var handler = new DefaultHandler();
        var parser = new MinimalJsonParser<>(handler);

        return Result.safeCallExpect(JsonParseException.class, () -> {
            parser.parse(string);
            return handler.value;
        });
    }

    private static class DefaultHandler implements MinimalJsonParser.JsonHandler<Array, Object> {

        Value value;

        @Override
        public Json.Array startArray() {
            return new Json.Array(new ArrayList<>());
        }

        @Override
        public Json.Object startObject() {
            return new Json.Object(new HashMap<>());
        }

        @Override
        public void endNull() {
            this.value = Json.NULL;
        }

        @Override
        public void endBoolean(boolean bool) {
            this.value = bool ? Json.TRUE : Json.FALSE;
        }

        @Override
        public void endString(java.lang.String string) {
            this.value = new Json.String(string);
        }

        @Override
        public void endNumber(java.lang.String string) {
            this.value = new Json.Number(new BigDecimal(string));
        }

        @Override
        public void endArray(Json.Array array) {
            this.value = array;
        }

        @Override
        public void endObject(Json.Object object) {
            this.value = object;
        }

        @Override
        public void endArrayValue(Json.Array array) {
            array.add(this.value);
        }

        @Override
        public void endObjectValue(Json.Object object, java.lang.String name) {
            object.add(name, this.value);
        }

        @Override
        public void startNull() {

        }

        @Override
        public void startBoolean() {

        }

        @Override
        public void startString() {

        }

        @Override
        public void startArrayValue(Array array) {

        }

        @Override
        public void startNumber() {

        }

        @Override
        public void startObjectName(Object object) {

        }

        @Override
        public void endObjectName(Object object, java.lang.String name) {

        }

        @Override
        public void startObjectValue(Object object, java.lang.String name) {

        }

    }
}
