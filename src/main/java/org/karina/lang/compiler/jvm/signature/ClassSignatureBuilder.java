package org.karina.lang.compiler.jvm.signature;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.jvm.model.jvm.JClassModel;
import org.karina.lang.compiler.jvm.signature.model.ClassSignature;
import org.karina.lang.compiler.jvm.signature.model.TypeArgument;
import org.karina.lang.compiler.jvm.signature.model.TypeSignature;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.utils.Generic;
import org.karina.lang.compiler.utils.Region;

import java.util.ArrayList;
import java.util.List;

@Getter
@Accessors(fluent = true)
public class ClassSignatureBuilder {
    private final KType.ClassType superClass;
    private final ImmutableList<KType.ClassType> interfaces;
    private final ImmutableList<Generic> generics;

    public ClassSignatureBuilder(String name, Region region, ClassSignature signature, @Nullable JClassModel outer) {

        var generics = ImmutableList.<Generic>builder();
        for (var generic : signature.generics()) {
            generics.add(new Generic(region, generic.name()));
        }
        this.generics = generics.build();

        this.superClass = SignatureHelper.toClassType(region, name, outer, this.generics, signature.superClass());

        var interfaces = ImmutableList.<KType.ClassType>builder();

        for (var anInterface : signature.interfaces()) {
            interfaces.add(SignatureHelper.toClassType(region, name, outer, this.generics, anInterface));
        }

        this.interfaces = interfaces.build();
    }


}
