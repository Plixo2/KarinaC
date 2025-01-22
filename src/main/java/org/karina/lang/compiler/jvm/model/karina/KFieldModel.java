package org.karina.lang.compiler.jvm.model.karina;

import lombok.AllArgsConstructor;
import org.karina.lang.compiler.model.FieldModel;
import org.karina.lang.compiler.model.pointer.ClassPointer;
import org.karina.lang.compiler.model.pointer.FieldPointer;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.utils.Region;

@AllArgsConstructor
public class KFieldModel implements FieldModel {
    private String name;
    private KType type;
    private int modifiers;
    private Region region;
    private ClassPointer classPointer;

    @Override
    public Region region() {
        return this.region;
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
    public FieldPointer pointer() {
        return FieldPointer.of(this.classPointer, this.name);
    }

    @Override
    public ClassPointer classPointer() {
        return this.classPointer;
    }
}
