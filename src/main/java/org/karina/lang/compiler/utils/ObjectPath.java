package org.karina.lang.compiler.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public record ObjectPath(List<String> elements) {

    public ObjectPath {
        //copy to an immutable list
        Objects.requireNonNull(elements);
        elements = List.copyOf(elements);
        if (elements.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Elements must not be null");
        }
    }

    public ObjectPath(String... elements) {
        this(List.of(elements));
    }

    public ObjectPath join(ObjectPath other) {

        var strings = new ArrayList<>(this.elements);
        strings.addAll(other.elements);
        return new ObjectPath(strings);

    }

    public ObjectPath append(String element) {

        var strings = new ArrayList<>(this.elements);
        strings.add(element);
        return new ObjectPath(strings);

    }

    public ObjectPath tail() {

        if (this.elements.isEmpty()) {
            throw new IllegalStateException("Can't take tail of empty path");
        }
        return new ObjectPath(this.elements.subList(1, this.elements.size()));

    }

    public String first() {
        return this.elements.getFirst();
    }

    public String last() {
        return this.elements.getLast();
    }

    public ObjectPath everythingButLast() {
        return new ObjectPath(this.elements.subList(0, this.elements.size() - 1));
    }

    public boolean isEmpty() {
        return this.elements.isEmpty();
    }

    public int size() {
        return this.elements.size();
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


    @Override
    public boolean equals(Object object) {
        if (object instanceof ObjectPath(List<String> elements1)) {
            var thisElements = this.elements;
            if (thisElements.size() != elements1.size()) {
                return false;
            }
            for (var i = 0; i < thisElements.size(); i++) {
                if (!thisElements.get(i).equals(elements1.get(i))) {
                    return false;
                }
            }
            return true;
        } else if (object instanceof String[] strings) {
            if (this.elements.size() != strings.length) {
                return false;
            }
            for (var i = 0; i < strings.length; i++) {
                if (!this.elements.get(i).equals(strings[i])) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.elements);
    }

    public boolean startsWith(String... prefixes) {
        if (prefixes.length > this.elements.size()) {
            return false;
        }
        for (var i = 0; i < prefixes.length; i++) {
            if (!this.elements.get(i).equals(prefixes[i])) {
                return false;
            }
        }
        return true;
    }

    public int length() {
        return this.elements.size();
    }

}
