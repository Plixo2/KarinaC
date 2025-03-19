package org.karina.lang.compiler.model_api.pointer;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model_api.Signature;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.utils.Region;

import java.util.List;

@Getter
@Accessors(fluent = true)
public class MethodPointer {
    Region region;
    ClassPointer classPointer;
    String name;
    Signature signature;
    List<KType> erasedParameters;

    private MethodPointer(Region region, ClassPointer classPointer, String name, Signature signature,List<KType> erasedParameters) {
        this.region = region;
        this.classPointer = classPointer;
        this.name = name;
        this.erasedParameters = erasedParameters;
    }

    @Override
    public String toString() {
        return "MethodPointer{" + "signature=" + this.signature + ", name='" + this.name + '\'' +
                ", class=" + this.classPointer + '}';
    }

    public static MethodPointer of(Region region, ClassPointer classPointer, String name, Signature signature) {
        return new MethodPointer(region, classPointer, name, signature, signature.parametersErased());
    }


    public static MethodPointer of(Region region, ClassPointer classPointer, String name, Signature signature, List<KType> erasedParameters) {
        return new MethodPointer(region, classPointer, name, signature, erasedParameters);
    }
}
