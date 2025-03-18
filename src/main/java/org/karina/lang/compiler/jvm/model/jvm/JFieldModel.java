package org.karina.lang.compiler.jvm.model.jvm;

import lombok.AllArgsConstructor;
import org.karina.lang.compiler.model_api.FieldModel;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.model_api.pointer.FieldPointer;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.utils.Region;

import java.util.Objects;

@AllArgsConstructor
public class JFieldModel implements FieldModel {
    private final String name;
    private final KType type;
    private final int modifiers;
    private final Region region;
    private final ClassPointer classPointer;

    @Override
    public Region region() {
        return this.region;
    }

    @Override
    public FieldPointer pointer() {
        return FieldPointer.of(this.region, this.classPointer, this.name);
    }

    @Override
    public int modifiers() {
        return this.modifiers;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public KType type() {
        return this.type;
    }

    @Override
    public ClassPointer classPointer() {
        return this.classPointer;
    }


    public int hashCodeExpensive() {
        return Objects.hash(this.name, this.type, this.modifiers, this.region, this.classPointer);
    }
}
