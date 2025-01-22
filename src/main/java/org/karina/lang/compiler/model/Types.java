package org.karina.lang.compiler.model;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.utils.Region;

public interface Types {

    void assign(Region checkingRegion, KType left, KType right);
    boolean canAssign(Region checkingRegion, KType left, KType right, boolean mutable);
    boolean isVoid(KType type);
    @Nullable KType superType(Region checkingRegion, KType a, KType b);
    //only checks the parameters, not the return type
    boolean isSameSignature(Signature a, Signature b);
}
