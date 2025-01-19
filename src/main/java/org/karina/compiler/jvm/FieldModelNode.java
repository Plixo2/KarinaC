package org.karina.compiler.jvm;

import lombok.AllArgsConstructor;
import org.karina.compiler.model.FieldModel;
import org.karina.lang.compiler.objects.KType;

@AllArgsConstructor
public class FieldModelNode implements FieldModel {
    private String name;
    private KType type;
    private int modifiers;

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
}
