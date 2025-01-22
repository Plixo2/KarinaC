package org.karina.lang.compiler.jvm.binary.in;

import com.google.common.collect.ImmutableList;
import org.karina.lang.compiler.api.TextSource;
import org.karina.lang.compiler.jvm.JavaResource;
import org.karina.lang.compiler.jvm.model.jvm.JClassModel;
import org.karina.lang.compiler.model.pointer.ClassPointer;
import org.karina.lang.compiler.utils.Generic;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ClassReader {
    private final ClassInStream stream;

    public ClassReader(InputStream in) {
        this.stream = new ClassInStream(in);
    }

    public JClassModel read() throws IOException {
        var name = this.stream.readString();
        var path = this.stream.readObjectPath();
        var currentClass = ClassPointer.of(path);
        var source = new TextSource(new JavaResource(this.stream.readString()), List.of());
        var version = this.stream.readInt();
        var modifiers = this.stream.readInt();
        var superClass = this.stream.readClassPointer();
        var outerClass = this.stream.readClassPointer();
        var interfaces = this.stream.readClassPointerList();
        var innerClasses = this.stream.readClassPointerList();
        var fields = this.stream.readFieldList(currentClass, source);
        var methods = this.stream.readMethodList(currentClass, source);

        var emptyRegion = source.emptyRegion();
        var generics = ImmutableList.copyOf(this.stream.readStrings().stream().map(ref -> new Generic(emptyRegion, ref)).toList());
        var permittedSubclasses = this.stream.readClassPointerList();
        return new JClassModel(
                name,
                path,
                version,
                modifiers,
                superClass,
                outerClass,
                interfaces,
                innerClasses,
                fields,
                methods,
                generics,
                permittedSubclasses,
                source,
                source.emptyRegion()
        );
    }



    public int readInt() throws IOException {
        return this.stream.readInt();
    }

    public int[] readIntList() throws IOException {
        return this.stream.readIntList();
    }

    public void assertEmpty() throws IOException {
        this.stream.assertEmpty();
    }
}
