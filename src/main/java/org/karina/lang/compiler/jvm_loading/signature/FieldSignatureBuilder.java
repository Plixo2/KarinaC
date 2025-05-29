package org.karina.lang.compiler.jvm_loading.signature;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model_api.impl.jvm.JClassModel;
import org.karina.lang.compiler.jvm_loading.signature.model.FieldSignature;
import org.karina.lang.compiler.utils.Context;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.utils.Region;

@Getter
@Accessors(fluent = true)
public class FieldSignatureBuilder {
    private final KType type;

    public FieldSignatureBuilder(Context c, String name, Region region, FieldSignature signature, @Nullable JClassModel outer) {
        this.type = SignatureHelper.toType(c, region, name, outer, ImmutableList.of(), signature.inner());
    }
}
