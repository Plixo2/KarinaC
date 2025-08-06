package org.karina.lang.compiler.utils;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.karina.lang.compiler.logging.Log;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Represents basically everything with a path.
 * The ObjectPath is just a immutable array of strings with a few utility methods.
 */
public final class ObjectPath {
    @Getter
    @Accessors(fluent = true)
    final String[] elements;
    final int hashCode1;
    public ObjectPath(String... elements) {
        this.elements = Arrays.copyOf(elements, elements.length);
        this.hashCode1 = hashCode(elements);
    }

    public ObjectPath(List<String> list) {
        var elementCount = list.size();
        var elements = new String[elementCount];
        for (var i = 0; i < elementCount; i++) {
            elements[i] = list.get(i);
        }
        this.elements = elements;
        this.hashCode1 = hashCode(elements);
    }



    public ObjectPath join(ObjectPath other) {

        var strings = Arrays.copyOf(this.elements, this.elements.length + other.elements.length);
        System.arraycopy(other.elements, 0, strings, this.elements.length, other.elements.length);

        return new ObjectPath(strings);

    }

    public ObjectPath append(String element) {
        var strings = Arrays.copyOf(this.elements, this.elements.length + 1);
        strings[this.elements.length] = element;
        return new ObjectPath(strings);

    }

    public ObjectPath tail() {

        if (this.isEmpty()) {
            throw new IllegalStateException("Can't take tail of empty path");
        }
        var strings = Arrays.copyOfRange(this.elements, 1, this.elements.length);
        return new ObjectPath(strings);
    }

    public String first() {
        if (this.isEmpty()) {
            throw new IllegalStateException("Can't take first of empty path");
        }
        return this.elements[0];
    }

    public String last() {
        if (this.isEmpty()) {
            throw new IllegalStateException("Can't take last of empty path");
        }
        return this.elements[this.elements.length - 1];
    }

    public ObjectPath everythingButLast() {
        var strings = Arrays.copyOf(this.elements, this.elements.length - 1);
        return new ObjectPath(strings);
    }

    public boolean isEmpty() {
        return this.elements.length == 0;
    }

    public int size() {
        return this.elements.length;
    }

    public String mkString() {
        return mkString("-");
    }

    public String mkString(String delimiter) {
        return String.join(delimiter, this.elements);
    }

    @Override
    public String toString() {
        return mkString();
    }

    public static ObjectPath fromString(String str, String split) {
        return new ObjectPath(str.split(Pattern.quote(split)));
    }

    public static ObjectPath fromJavaPath(String str) {
        return new ObjectPath(str.split("/"));
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        } else if (object == null) {
            return false;
        } else if (object instanceof ObjectPath that) {
            if (that.hashCode1 != this.hashCode1) {
                return false;
            }
            return Arrays.equals(that.elements, this.elements);
        } else if (object instanceof String[] strings) {
            return Arrays.equals(strings, this.elements);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.hashCode1;
    }

    public boolean startsWith(String... prefixes) {
        if (prefixes.length > this.elements.length) {
            return false;
        }
        for (var i = 0; i < prefixes.length; i++) {
            if (!this.elements[i].equals(prefixes[i])) {
                return false;
            }
        }
        return true;
    }

    public Iterator<String> iterator() {
        return Arrays.stream(this.elements).iterator();
    }

    public List<String> asList() {
        return List.of(this.elements);
    }

    private static int hashCode(String[] elements) {
        return Arrays.hashCode(elements);
    }

}
