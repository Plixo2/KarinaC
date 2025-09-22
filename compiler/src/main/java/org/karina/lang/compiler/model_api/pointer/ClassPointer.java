package org.karina.lang.compiler.model_api.pointer;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Region;

import java.util.List;
import java.util.Objects;

/**
 * Identifies a class across different stages of the compiler. It should be always valid
 *
 * @see org.karina.lang.compiler.model_api.Model
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
    public static final ObjectPath CHARACTER_PATH = new ObjectPath("java", "lang", "Character");

    public static final ObjectPath NUMBER_PATH = new ObjectPath("java", "lang", "Number");
    public static final ObjectPath ITERABLE_PATH = new ObjectPath("java", "lang", "Iterable");
    public static final ObjectPath ITERATOR_PATH = new ObjectPath("java", "util", "Iterator");
    public static final ObjectPath THROWABLE_PATH = new ObjectPath("java", "lang", "Throwable");
    public static final ObjectPath AUTO_CLOSEABLE_PATH = new ObjectPath("java", "lang", "AutoCloseable");
    public static final ObjectPath CLASS_TYPE_PATH = new ObjectPath("java", "lang", "Class");
    public static final ObjectPath MATCH_EXCEPTION_PATH = new ObjectPath("java", "lang", "MatchException");

    public static final ObjectPath PRELUDE_PATH = new ObjectPath("karina", "lang", "Prelude");
    public static final ObjectPath OPTION_PATH = new ObjectPath("karina", "lang", "Option");
    public static final ObjectPath OPTION_SOME_PATH = new ObjectPath("karina", "lang", "Option$Some");
    public static final ObjectPath OPTION_NONE_PATH = new ObjectPath("karina", "lang", "Option$None");
    public static final ObjectPath RESULT_PATH = new ObjectPath("karina", "lang", "Result");
    public static final ObjectPath RESULT_OK_PATH = new ObjectPath("karina", "lang", "Result$Ok");
    public static final ObjectPath RESULT_ERR_PATH = new ObjectPath("karina", "lang", "Result$Err");

    public static final ObjectPath EXTENSION_PATH = new ObjectPath("karina", "lang", "Extension");
    public static final ObjectPath RANGE_PATH = new ObjectPath("karina", "lang", "Range");
    public static final ObjectPath STRING_INTERPOLATION_PATH = new ObjectPath("karina", "lang", "StringInterpolation");


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

    public KType.ClassType implement(List<KType> generics) {
        return new KType.ClassType(
                this,
                generics
        );
    }
}
