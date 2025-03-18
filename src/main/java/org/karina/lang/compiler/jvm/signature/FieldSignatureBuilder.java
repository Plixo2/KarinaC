package org.karina.lang.compiler.jvm.signature;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.jvm.model.jvm.JClassModel;
import org.karina.lang.compiler.jvm.signature.model.FieldSignature;
import org.karina.lang.compiler.jvm.signature.model.MethodSignature;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.utils.Generic;
import org.karina.lang.compiler.utils.Region;

@Getter
@Accessors(fluent = true)
public class FieldSignatureBuilder {
    private final KType type;

    public FieldSignatureBuilder(String name, Region region, FieldSignature signature, @Nullable JClassModel outer) {
        this.type = SignatureHelper.toType(region, name, outer, ImmutableList.of(), signature.inner());
    }
}
