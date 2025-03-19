package org.karina.lang.compiler.model_api.pointer;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Region;

import java.util.Objects;

/**
 * IMPORTANT:
 * The equals and hashCode methods are overridden to compare the path field,
 * not the region, that is only used for errors
 * ClassPointer should be always valid
 */

@Getter
@Accessors(fluent = true)
public class ClassPointer {
    public static final ObjectPath ROOT_PATH = new ObjectPath("java", "lang", "Object");
    public static final ObjectPath STRING_PATH = new ObjectPath("java", "lang", "String");

    public static final ObjectPath INTEGER_PATH = new ObjectPath("java", "lang", "Integer");
    public static final ObjectPath LONG_PATH = new ObjectPath("java", "lang", "Long");
    public static final ObjectPath DOUBLE_PATH = new ObjectPath("java", "lang", "Double");
    public static final ObjectPath FLOAT_PATH = new ObjectPath("java", "lang", "Float");
    public static final ObjectPath BOOLEAN_PATH = new ObjectPath("java", "lang", "Boolean");

    public static final ObjectPath NUMBER_PATH = new ObjectPath("java", "lang", "Number");
    public static final ObjectPath ITERABLE_PATH = new ObjectPath("java", "lang", "Iterable");
    public static final ObjectPath THROWABLE_PATH = new ObjectPath("java", "lang", "Throwable");
    public static final ObjectPath CLASS_TYPE = new ObjectPath("java", "lang", "Class");


    public static boolean shouldIncludeInPrelude(ObjectPath pointer) {
        if (pointer.length() != 3) {
            return false;
        }
        return pointer.startsWith("java", "lang") || pointer.startsWith("karina", "lang");
    }


    /**
     * Path to a class
     */
    ObjectPath path;

    /**
     * Region for errors.
     * Most likely the region of where is ClassPointer was created
     */
    Region region;

    private ClassPointer(Region region, ObjectPath path) {
        this.path = Objects.requireNonNull(path);
        this.region = Objects.requireNonNull(region);
    }


    public boolean isRoot() {
        return this.path().equals(ROOT_PATH);
    }


    @Override
    public String toString() {
        return this.path.mkString("/");
    }


    @Override
    public boolean equals(Object object) {
        return object instanceof ClassPointer that && Objects.equals(this.path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.path);
    }

    public static ClassPointer of(Region region, ObjectPath path) {
        return new ClassPointer(region, path);
    }
}
