package org.karina.lang.compiler.stages.preprocess;

import org.karina.lang.compiler.utils.Generic;
import org.karina.lang.compiler.utils.NamedExpression;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.SpanOf;
import org.karina.lang.compiler.errors.ErrorCollector;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.errors.Unique;
import org.karina.lang.compiler.errors.types.ImportError;
import org.karina.lang.compiler.objects.*;

import java.util.ArrayList;
import java.util.List;

public class Preprocessor {

    public KTree.KPackage desugarTree(KTree.KPackage root) throws Log.KarinaException {
        return desugarPackage(root, root);
    }

    private KTree.KPackage desugarPackage(KTree.KPackage root, KTree.KPackage pkg) {
        var build = KTree.KPackage.builder();
        build.name(pkg.name());
        build.path(pkg.path());
        try (var collector = new ErrorCollector()) {
            for (var subPackage : pkg.subPackages()) {
                collector.collect(() ->
                        build.subPackage(desugarPackage(root, subPackage))
                );
            }
            for (var unit : pkg.units()) {
                collector.collect(() ->
                        build.unit(desugarUnit(root, unit))
                );
            }
        }
        return build.build();
    }

    public KTree.KUnit desugarUnit(KTree.KPackage root, KTree.KUnit unit) {

        var build = KTree.KUnit.builder();

        build.region(unit.region());
        build.name(unit.name());
        build.path(unit.path());
        build.kImports(unit.kImports());

        try (var collector = new ErrorCollector()){
            for (var item : unit.items()) {
                if (item instanceof KTree.KEnum kEnum) {
                    collector.collect(() -> {
                        var enumInterface = generateEnumInterface(unit.path(), kEnum);
                        build.item(enumInterface);
                        for (var entry : kEnum.entries()) {
                            var enumCaseStruct = generateEnumCaseStruct(
                                    enumInterface.path(), unit.path(),
                                    enumInterface.generics(), entry
                            );
                            build.item(enumCaseStruct);
                        }
                    });

                } else {
                    build.item(item);
                }
            }
        }


        return build.build();
    }

    private KTree.KInterface generateEnumInterface(ObjectPath path, KTree.KEnum kEnum) {
        var build = KTree.KInterface.builder();
        build.region(kEnum.region());
        build.name(kEnum.name());
        var interfacePath = path.append(kEnum.name().value());
        build.path(interfacePath);
        build.generics(kEnum.generics());
        build.annotations(List.of());
        build.superTypes(List.of());

        var duplicate = Unique.testUnique(kEnum.entries(), KTree.KEnumEntry::name);
        if (duplicate != null) {
            Log.importError(new ImportError.DuplicateItem(
                    kEnum.region(),
                    duplicate.duplicate().region(),
                    duplicate.value().value()
            ));
            throw new Log.KarinaException();
        }

        for (var entry : kEnum.entries()) {
            if (entry.name().value().equals(kEnum.name().value())) {
                Log.temp(entry.name().region(), "Enum entry cannot have the same name as the enum itself");
                Log.importError(new ImportError.DuplicateItem(
                        kEnum.region(),
                        entry.region(),
                        entry.name().value()
                ));
                throw new Log.KarinaException();
            }
            build.permittedStruct(path.append(entry.name().value()));

            var enumCaseFunction = generateEnumCaseFunction(interfacePath, path, kEnum.generics(), entry);
            build.function(enumCaseFunction);
        }

        return build.build();
    }
    private KTree.KFunction generateEnumCaseFunction(ObjectPath path, ObjectPath unitPath, List<Generic> generics, KTree.KEnumEntry enumEntry) {
        var build = KTree.KFunction.builder();
        build.region(enumEntry.region());
        build.name(enumEntry.name());
        var functionPath = path.append(enumEntry.name().value());
        build.path(functionPath);
        build.self(null); //static
        var newGenerics =
                generics.stream().map(ref -> new Generic(enumEntry.region(), ref.name())).toList();
        build.modifier(new FunctionModifier());
        build.annotations(List.of());

        var implGenerics = new ArrayList<KType>();

        for (var newGeneric : newGenerics) {
            var genericPath = new ObjectPath(newGeneric.name());
            implGenerics.add(new KType.UnprocessedType(
                    enumEntry.region(),
                    SpanOf.span(enumEntry.region(), genericPath),
                    List.of()
            ));
        }

        var creationParams = new ArrayList<NamedExpression>();
        for (var parameter : enumEntry.parameters()) {
            var field = new KTree.KParameter(
                    parameter.region(),
                    SpanOf.span(parameter.region(), parameter.name().value()),
                    parameter.type(),
                    null
            );
            build.parameter(field);

            creationParams.add(new NamedExpression(
                    parameter.region(),
                    parameter.name(),
                    new KExpr.Literal(
                            parameter.region(),
                            parameter.name().value(),
                            null
                    )
            ));
        }


        build.returnType(new KType.UnprocessedType(
                enumEntry.region(),
                SpanOf.span(enumEntry.region(), path.tail()),
                implGenerics
        ));
        build.generics(newGenerics);
        build.expr(new KExpr.CreateObject(
                enumEntry.region(),
                new KType.UnprocessedType(
                        enumEntry.region(),
                        SpanOf.span(enumEntry.region(), unitPath.append(enumEntry.name().value()).tail()),
                        implGenerics
                ),
                creationParams,
                null
        ));
        return build.build();
    }

    private KTree.KStruct generateEnumCaseStruct(ObjectPath interfacePath, ObjectPath path, List<Generic> generics, KTree.KEnumEntry enumEntry) {
        var build = KTree.KStruct.builder();
        build.region(enumEntry.region());
        build.name(enumEntry.name());

        var structPath = path.append(enumEntry.name().value());
        build.path(structPath);
        build.modifier(new StructModifier());
        build.annotations(List.of());
        var newGenerics =
                generics.stream().map(ref -> new Generic(enumEntry.region(), ref.name())).toList();
        build.generics(newGenerics);
        build.functions(List.of());

        for (var parameter : enumEntry.parameters()) {
            var field = new KTree.KField(
                    parameter.region(),
                    structPath.append(parameter.name().value()),
                    parameter.name(),
                    parameter.type()
            );
            build.field(field);
        }

        var regionImpl = enumEntry.region();
        var implGenerics = new ArrayList<KType>();

        for (var newGeneric : newGenerics) {
            var genericPath = new ObjectPath(newGeneric.name());
            implGenerics.add(new KType.UnprocessedType(
                    regionImpl,
                    SpanOf.span(regionImpl, genericPath),
                    List.of()
            ));
        }

        build.implBlock(new KTree.KImplBlock(
                enumEntry.region(),
                new KType.UnprocessedType(
                        regionImpl,
                        SpanOf.span(regionImpl, interfacePath.tail()),
                        implGenerics
                ),
                List.of()
        ));

        return build.build();
    }
}
