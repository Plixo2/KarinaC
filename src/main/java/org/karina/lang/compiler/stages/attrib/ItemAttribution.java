package org.karina.lang.compiler.stages.attrib;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.SymbolTable;
import org.karina.lang.compiler.Variable;
import org.karina.lang.compiler.VariableCollection;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.objects.KType;

public class ItemAttribution {

    static KTree.KFunction attribFunction(
            KTree.KPackage root,
            SymbolTable symbolTable,
            @Nullable KType selfType,
            KTree.KFunction function
    ) {
        var build = KTree.KFunction.builder();
        build.region(function.region());
        build.name(function.name());
        build.path(function.path());

        KType returnType = function.returnType();
        if (returnType == null) {
            returnType = new KType.PrimitiveType.VoidType(function.region());
        }
        build.returnType(returnType);

        var ctx = new AttributionContext(root, selfType, function.region(), returnType, VariableCollection.empty(), symbolTable);

        for (var parameter : function.parameters()) {
            var newSymbol = new Variable(
                    parameter.region(),
                    parameter.name().value(),
                    parameter.type(),
                    false,
                    true
            );
            var newParam = new KTree.KParameter(
                    parameter.region(),
                    parameter.name(),
                    parameter.type(),
                    newSymbol
            );
            build.parameter(newParam);
            ctx = ctx.addVariable(newSymbol);
        }

        if (function.expr() != null) {
            var attribExpr = ExprAttribution.attribExpr(returnType, ctx, function.expr());
            build.expr(attribExpr.expr());
        } else {
            build.expr(null);
        }

        build.annotations(function.annotations());
        build.generics(function.generics());
        return build.build();
    }

    static KTree.KStruct attribStruct(KTree.KPackage root, SymbolTable symbolTable, KTree.KStruct struct) {
        return struct;
    }

    static KTree.KInterface attribInterface(KTree.KPackage root, SymbolTable symbolTable, KTree.KInterface anInterface) {
        return anInterface;
    }

    static KTree.KEnum attribEnum(KTree.KPackage root, SymbolTable symbolTable, KTree.KEnum kEnum) {
        return kEnum;
    }



}
