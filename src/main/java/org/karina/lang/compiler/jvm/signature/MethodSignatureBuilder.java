package org.karina.lang.compiler.jvm.signature;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.jvm.model.jvm.JClassModel;
import org.karina.lang.compiler.jvm.signature.model.MethodSignature;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.utils.Generic;
import org.karina.lang.compiler.utils.Region;

@Getter
@Accessors(fluent = true)
public class MethodSignatureBuilder {
    private final ImmutableList<Generic> generics;
    private final KType returnType;
    private final ImmutableList<KType> parameters;

    public MethodSignatureBuilder(String name, Region region, MethodSignature signature, @Nullable JClassModel outer) {

        this.generics = SignatureHelper.mapGenerics(
                region,
                name,
                outer,
                signature.generics()
        );

        var returnSignature = signature.returnSignature();
        if (returnSignature != null) {
            this.returnType = SignatureHelper.toType(region, name, outer, this.generics, returnSignature);
        } else {
            this.returnType = KType.NONE;
        }
        var parameters = ImmutableList.<KType>builder();

        for (var anParam : signature.parameters()) {
            parameters.add(SignatureHelper.toType(region, name, outer, this.generics, anParam));
        }

        this.parameters = parameters.build();

    }
}
