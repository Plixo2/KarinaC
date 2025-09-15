package org.karina.lang.compiler.jvm_loading.signature;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model_api.impl.jvm.JClassModel;
import org.karina.lang.compiler.jvm_loading.signature.model.ClassSignature;
import org.karina.lang.compiler.utils.Context;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.model_api.Generic;
import org.karina.lang.compiler.utils.Region;

@Getter
@Accessors(fluent = true)
public class ClassSignatureBuilder {
    private final KType.ClassType superClass;
    private final ImmutableList<KType.ClassType> interfaces;
    private final ImmutableList<Generic> generics;

    public ClassSignatureBuilder(Context c, String name, Region region, ClassSignature signature, @Nullable JClassModel outer) {

        this.generics = SignatureHelper.mapGenerics(
                c,
                region,
                name,
                outer,
                signature.generics()
        );

        this.superClass = SignatureHelper.toClassType(c, region, name, outer, this.generics, signature.superClass());

        var interfaces = ImmutableList.<KType.ClassType>builder();

        for (var anInterface : signature.interfaces()) {
            interfaces.add(SignatureHelper.toClassType(c, region, name, outer, this.generics, anInterface));
        }

        this.interfaces = interfaces.build();
    }


}
