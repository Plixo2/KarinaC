package org.karina.lang.compiler.backend.jvm.jvmRewrite;

import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.objects.FunctionModifier;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.utils.*;

import java.util.ArrayList;
import java.util.List;

public class RewriteForJVM {

    public static KTree.KPackage rewrite(KTree.KPackage root) {
        return rewritePackage(root, root);
    }

    private static KTree.KPackage rewritePackage(KTree.KPackage root, KTree.KPackage kPackage) {
        var build = KTree.KPackage.builder();
        build.path(kPackage.path());
        build.name(kPackage.name());

        for (var unit : kPackage.units()) {
            build.unit(rewriteUnit(root, unit, root.path()));
        }
        for (var subPackage : kPackage.subPackages()) {
            build.subPackage(rewritePackage(root, subPackage));
        }
        if (root == kPackage) {
            //we just choose any unit for a region. If there are non we just skip it, they cant be used anyway
            var allUnits = kPackage.getAllUnitsRecursively();
            if (!allUnits.isEmpty()) {
                var anyUnit = allUnits.getFirst();
                var packagePath = kPackage.path();
                build.unit(generateUnit(packagePath, anyUnit.region()));
            }
        }

        return build.build();
    }

    private static KTree.KUnit rewriteUnit(KTree.KPackage root, KTree.KUnit unit, ObjectPath baseInterfacePath) {

        var build = KTree.KUnit.builder();
        build.region(unit.region());
        build.name(unit.name());
        build.path(unit.path());
        build.kImports(unit.kImports());

        RewriteContext ctx = new RewriteContext(baseInterfacePath, unit.path());
        for (var item : unit.items()) {
            switch (item) {
                case KTree.KFunction kFunction -> {
                    build.item(rewriteFunction(kFunction, ctx));
                }
                case KTree.KEnum kEnum -> {
                    Log.temp(kEnum.region(), "Cannot expression enums, they should be converted to structs and interfaces");
                    throw new Log.KarinaException();
                }
                case KTree.KInterface kInterface -> {
                    build.item(rewriteInterface(kInterface, ctx));
                }
                case KTree.KStruct kStruct -> {
                    build.item(rewriteStruct(kStruct, ctx));
                }
            }
        }
        ctx.getAddedItems().forEach(build::item);

        build.unitScopeSymbolTable(unit.unitScopeSymbolTable());

        return build.build();
    }

    private static KTree.KFunction rewriteFunction(KTree.KFunction function, RewriteContext ctx) {

        KExpr expr = null;
        if (function.expr() != null) {
            expr = RewriteExpr.rewrite(function.expr(), ctx);
        }

        return new KTree.KFunction(
                function.region(),
                function.name(),
                function.path(),
                function.self(),
                function.modifier(),
                function.annotations(),
                function.parameters(),
                function.returnType(),
                function.generics(),
                expr
        );
    }

    private static KTree.KStruct rewriteStruct(KTree.KStruct struct, RewriteContext ctx) {
        var functions = new ArrayList<KTree.KFunction>();
        for (var function : struct.functions()) {
            functions.add(rewriteFunction(function, ctx));
        }
        var implBlocks = new ArrayList<KTree.KImplBlock>();
        for (var implBlock : struct.implBlocks()) {
            var functions2 = new ArrayList<KTree.KFunction>();
            for (var function : implBlock.functions()) {
                functions2.add(rewriteFunction(function, ctx));
            }
            implBlocks.add(new KTree.KImplBlock(implBlock.region(), implBlock.type(), functions2));
        }

        return new KTree.KStruct(
                struct.region(),
                struct.name(),
                struct.path(),
                struct.modifier(),
                struct.generics(),
                struct.annotations(),
                functions,
                struct.fields(),
                implBlocks
        );
    }

    private static KTree.KInterface rewriteInterface(KTree.KInterface interface_, RewriteContext ctx) {
        var functions = new ArrayList<KTree.KFunction>();
        for (var function : interface_.functions()) {
            functions.add(rewriteFunction(function, ctx));
        }

        return new KTree.KInterface(
                interface_.region(),
                interface_.name(),
                interface_.path(),
                interface_.generics(),
                interface_.annotations(),
                functions,
                interface_.superTypes(),
                interface_.permittedStructs()
        );
    }


    private static KTree.KUnit generateUnit(ObjectPath base, Span region) {
        var build = KTree.KUnit.builder();
        build.region(region);
        build.name("FunctionalInterfaces");
        var unitBase = base.append("FunctionalInterfaces");
        build.path(unitBase);
        build.kImports(List.of());
        build.unitScopeSymbolTable(null);
        build.items(generateInterfaces(unitBase, region));
        return build.build();
    }

    private static List<KTree.KInterface> generateInterfaces(ObjectPath base, Span region) {
        var interfaces = new ArrayList<KTree.KInterface>();
        for (var i = 0; i < 16; i++) {
            interfaces.add(generateInterface(base, region, i, true));
            interfaces.add(generateInterface(base, region, i, false));
        }
        return interfaces;
    }

    private static KTree.KInterface generateInterface(ObjectPath base, Span region, int input, boolean output) {
        var name = "$FunctionalInterface" + input + "_" + output;
        var shortName = "$" + input + "_" + output;

        var build = KTree.KInterface.builder();
        build.region(region);
        build.name(SpanOf.span(region, name));
        var interfacePath = base.append(name);
        build.path(interfacePath);

        var inGenerics = new ArrayList<Generic>();
        Generic outGenerics = null;
        for (var i = 0; i < input; i++) {
            var generic = new Generic(region, "P" + i + shortName);
            build.generic(generic);
            inGenerics.add(generic);
        }
        if (output) {
            var generic = new Generic(region, "R" + shortName);
            build.generic(generic);
            outGenerics = generic;
        }
        build.annotations(List.of());
        build.superTypes(List.of());
        build.permittedStructs(List.of());

        KType returnType;
        if (output) {
            returnType = new KType.GenericLink(region, outGenerics);
        } else {
            returnType = new KType.PrimitiveType.VoidType(region);
        }

        var parameters = new ArrayList<KTree.KParameter>();
        for (var i = 0; i < input; i++) {
            var type = new KType.GenericLink(region, inGenerics.get(i));
            var parameter = new KTree.KParameter(
                    region,
                    SpanOf.span(region, "p" + i), type,
                    new Variable(region, "p" + i, type, false, true)
            );
            parameters.add(parameter);
        }


        var applyFunction = new KTree.KFunction(
                region,
                SpanOf.span(region, "apply"),
                interfacePath.append("apply"),
                region,
                new FunctionModifier(),
                List.of(),
                parameters,
                returnType,
                List.of(),
                null
        );
        build.function(applyFunction);

        return build.build();
    }
}
