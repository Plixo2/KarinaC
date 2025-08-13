package org.karina.lang.compiler.model_api.pointer;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.karina.lang.compiler.model_api.Signature;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.utils.Types;
import org.karina.lang.compiler.utils.Region;

import java.util.List;
import java.util.Objects;

/**
 * Identifies a Method across different stages of the compiler. It should be always valid
 * @see org.karina.lang.compiler.model_api.Model
 */
@Getter
@Accessors(fluent = true)
public class MethodPointer {
    Region region;
    ClassPointer classPointer;
    String name;
    List<KType> erasedParameters;
    KType erasedReturnType;

    private MethodPointer(Region region, ClassPointer classPointer, String name ,List<KType> erasedParameters, KType erasedReturnType) {
        this.region = region;
        this.classPointer = classPointer;
        this.name = name;
        this.erasedParameters = erasedParameters;
        this.erasedReturnType = erasedReturnType;
    }

    @Override
    public String toString() {
        return "MethodPointer{" + "region=" + this.region + ", classPointer=" + this.classPointer +
                ", name='" + this.name + '\'' + ", erasedParameters=" + this.erasedParameters +
                ", returnType=" + this.erasedReturnType + '}';
    }

    public static MethodPointer of(Region region, ClassPointer classPointer, String name, Signature signature) {
        return new MethodPointer(region, classPointer, name, signature.parametersErased(), Types.erase(signature.returnType()));
    }


    public static MethodPointer of(Region region, ClassPointer classPointer, String name, List<KType> erasedParameters, KType returnType) {
        return new MethodPointer(region, classPointer, name, erasedParameters, Types.erase(returnType));
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof MethodPointer pointer)) {
            return false;
        }
        return Objects.equals(this.classPointer, pointer.classPointer) &&
                Objects.equals(this.name, pointer.name) &&
                Types.signatureEquals(this.erasedParameters, pointer.erasedParameters) &&
                Objects.equals(this.erasedReturnType, pointer.erasedReturnType);
    }



    @Override
    public int hashCode() {
        return Objects.hash(this.classPointer, this.name, this.erasedParameters, this.erasedReturnType);
    }
}
