package org.karina.lang.compiler.jvm_loading.binary.in;

import com.google.common.collect.ImmutableList;
import org.karina.lang.compiler.jvm_loading.JavaResource;
import org.karina.lang.compiler.model_api.impl.ModelBuilder;
import org.karina.lang.compiler.model_api.impl.jvm.JClassModel;
import org.karina.lang.compiler.model_api.impl.jvm.JFieldModel;
import org.karina.lang.compiler.model_api.impl.jvm.JMethodModel;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.utils.Context;
import org.karina.lang.compiler.utils.DefaultTextSource;
import org.karina.lang.compiler.utils.KType;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ClassReader {
    private final ClassInStream stream;

    private final ClassInStream.KnownGenerics currentGenerics;

    public ClassReader(ClassInStream in, ClassInStream.KnownGenerics currentGenerics) {
        this.stream = in;
        this.currentGenerics = currentGenerics;
    }

    public ClassReader(InputStream in) {
        this.stream = new ClassInStream(in);
        this.currentGenerics = new ClassInStream.KnownGenerics();
    }

    public JClassModel read(Context c, ModelReader.TTYL ttyl, ModelBuilder builder) throws IOException {

        var startCheck = this.stream.readInt();
        if (startCheck != 768928090) {
            throw new IOException("Invalid class file format, expected magic number 768928090 but got " + startCheck);
        }

        var name = this.stream.readString();
        var path = this.stream.readObjectPath();

        var source = new DefaultTextSource(new JavaResource(this.stream.readString()), "");
        var region = source.emptyRegion();
        var currentClass = ClassPointer.of(region, path);

        var version = this.stream.readInt();
        var modifiers = this.stream.readInt();

        var generics = this.stream.readGenericList(source, this.currentGenerics);

        var hasSuperClass = this.stream.readByte() == 1;
        KType.ClassType superClass = null;
        if (hasSuperClass) {
            superClass = this.stream.readClassType(source, this.currentGenerics);
        }

        var outerClassPtr = this.stream.readClassPointer(source);
        var nestHost = this.stream.readClassPointer(source);


        var interfaceCount = this.stream.readInt();
        var interfaces = ImmutableList.<KType.ClassType>builder();
        for (int i = 0; i < interfaceCount; i++) {
            interfaces.add(this.stream.readClassType(source, this.currentGenerics));
        }

        var innerClasses = new ArrayList<JClassModel>();

        var innerClassCount = this.stream.readInt();
        for (int i = 0; i < innerClassCount; i++) {
            var inner = new ClassReader(this.stream, this.currentGenerics.copy());
            innerClasses.add(inner.read(c, ttyl, builder));
        }

        var fieldCount = this.stream.readInt();
        var fields = new ArrayList<JFieldModel>();

        for (int i = 0; i < fieldCount; i++) {
            var fieldName = this.stream.readString();
            var fieldType = this.stream.readType(source, this.currentGenerics);
            var fieldModifiers = this.stream.readInt();
            fields.add(new JFieldModel(fieldName, fieldType, fieldModifiers, region, currentClass));
        }

        var methodCount = this.stream.readInt();
        var methods = new ArrayList<JMethodModel>();

        for (int i = 0; i < methodCount; i++) {
            var methodName = this.stream.readString();
            var methodGenerics = this.currentGenerics.copy();
            var methodGenericsDef = this.stream.readGenericList(source, methodGenerics);
            var methodModifiers = this.stream.readInt();
            var signature = this.stream.readSignature(source, methodGenerics);
            var parameters = this.stream.readStrings();

            methods.add(new JMethodModel(
                    methodName,
                    methodModifiers,
                    signature,
                    parameters,
                    ImmutableList.copyOf(methodGenericsDef),
                    null, region,
                    currentClass
            ));
        }

        var permittedSubclasses = this.stream.readClassPointerList(source);
        var nestMembers = this.stream.readClassPointerList(source);

        var endCheck = this.stream.readInt();
        if (endCheck != 902939823) {
            throw new IOException("Invalid class file format, expected magic number 902939823 but got " + endCheck);
        }

        var model = new JClassModel(
                name,
                path,
                version,
                modifiers,
                superClass,
                null,
                nestHost,
                interfaces.build(),
                innerClasses,
                fields,
                methods,
                ImmutableList.copyOf(generics),
                permittedSubclasses,
                nestMembers,
                source,
                region
        );
        synchronized (builder) {
            builder.addClass(c, model);
        }
        if (outerClassPtr != null) {
            ttyl.add(m -> {
                var classModel = m.getClass(outerClassPtr);
                assert classModel instanceof JClassModel;
                model.setOuterClass((JClassModel) classModel);
            });
        }

        return model;
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
