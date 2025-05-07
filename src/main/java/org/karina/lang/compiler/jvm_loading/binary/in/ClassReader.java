package org.karina.lang.compiler.jvm_loading.binary.in;

import com.google.common.collect.ImmutableList;
import org.karina.lang.compiler.utils.TextSource;
import org.karina.lang.compiler.jvm_loading.JavaResource;
import org.karina.lang.compiler.model_api.impl.jvm.JClassModel;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
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
        var currentClass = ClassPointer.of(null, path);
        var source = new TextSource(new JavaResource(this.stream.readString()), List.of());
        var version = this.stream.readInt();
        var modifiers = this.stream.readInt();
        var superClass = this.stream.readClassPointer(source);
        var outerClass = this.stream.readClassPointer(source);
        var interfaces = this.stream.readClassPointerList(source);
        var innerClasses = this.stream.readClassPointerList(source);
        var fields = this.stream.readFieldList(currentClass, source);
        var methods = this.stream.readMethodList(currentClass, source);

        var emptyRegion = source.emptyRegion();
        var generics = ImmutableList.copyOf(this.stream.readStrings().stream().map(ref -> new Generic(emptyRegion, ref)).toList());
        var permittedSubclasses = this.stream.readClassPointerList(source);
//        return new JClassModel(
//                name,
//                path,
//                version,
//                modifiers,
//                superClass,
//                outerClass,
//                interfaces,
//                innerClasses,
//                fields,
//                methods,
//                generics,
//                permittedSubclasses,
//                source,
//                source.emptyRegion()
//        );
        throw new NullPointerException("Not implemented");
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
