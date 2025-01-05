package org.karina.lang.compiler.stages.symbols;

import org.karina.lang.compiler.ObjectPath;
import org.karina.lang.compiler.Span;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.objects.Symbol;

public interface MemberSymbol {
    KType type();

    record FieldSymbol(KType type, ObjectPath fieldPath) implements MemberSymbol {}
    record VirtualFunctionSymbol(Span region, KType.ClassType classType, ObjectPath path) implements MemberSymbol {

        @Override
        public KType type() {
            return new KType.PrimitiveType.VoidType(this.region);
        }
    }

}
