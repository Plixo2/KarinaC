package org.karina.lang.compiler.jvm.model.karina;

import lombok.AllArgsConstructor;
import org.karina.lang.compiler.model.FieldModel;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.utils.Span;

@AllArgsConstructor
public class KFieldModel implements FieldModel {
    private String name;
    private KType type;
    private int modifiers;
    private Span region;

    public Span region() {
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
}
