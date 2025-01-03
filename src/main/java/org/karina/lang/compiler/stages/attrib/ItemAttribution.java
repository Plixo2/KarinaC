package org.karina.lang.compiler.stages.attrib;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.*;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.errors.types.AttribError;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.SymbolTable;

import java.util.List;

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

        var typeChecking = new TypeChecking(root);
        var ctx = new AttributionContext(
                root,
                selfType,
                function.region(),
                returnType,
                VariableCollection.empty(),
                symbolTable,
                typeChecking
        );

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
        var build = KTree.KStruct.builder();
        build.region(struct.region());
        build.name(struct.name());
        build.path(struct.path());
        build.generics(struct.generics());
        build.annotations(struct.annotations());
        build.fields(struct.fields());

        var path = struct.path();
        var selfType = new KType.ClassType(struct.name().region(), SpanOf.span(struct.name().region(),path), List.of());
        for (var function : struct.functions()) {
            var attribFunction = attribFunction(root, symbolTable, selfType, function);
            build.function(attribFunction);
        }

        for (var implBlock : struct.implBlocks()) {
            var attribImplBlock = attribImplBlock(root, symbolTable, selfType, implBlock);
            build.implBlock(attribImplBlock);
        }


        return build.build();
    }

    private static KTree.KImplBlock attribImplBlock(KTree.KPackage root, SymbolTable symbolTable, KType implType, KTree.KImplBlock implBlock) {
        var build = KTree.KImplBlock.builder();
        build.region(implBlock.region());
        build.type(implBlock.type());

        if (!(implBlock.type() instanceof KType.ClassType classType)) {
            Log.temp(implBlock.type().region(), "impl type must be a class type");
            throw new Log.KarinaException();
        }
        var path = KTree.findAbsolutItem(root, classType.path().value());
        if (path == null) {
            Log.temp(implBlock.type().region(), "impl type not found " + classType.path().value());
            throw new Log.KarinaException();
        }
        if (!(path instanceof KTree.KInterface kInterface)) {
            Log.attribError(new AttribError.NotAInterface(implBlock.type().region(), implBlock.type()));
            throw new Log.KarinaException();
        }

        //TODO test if all functions are implemented
//        for (var function : kInterface.functions()) {
//
//        }


        for (var function : implBlock.functions()) {
            var attribFunction = attribFunction(root, symbolTable, implType, function);
            build.function(attribFunction);
        }

        return build.build();
    }

    static KTree.KInterface attribInterface(KTree.KPackage root, SymbolTable symbolTable, KTree.KInterface anInterface) {
        return anInterface;
    }

    static KTree.KEnum attribEnum(KTree.KPackage root, SymbolTable symbolTable, KTree.KEnum kEnum) {
        return kEnum;
    }



}
