package org.karina.lang.compiler.stages.imports;

import org.karina.lang.compiler.*;
import org.karina.lang.compiler.errors.ErrorCollector;
import org.karina.lang.compiler.errors.types.ImportError;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.objects.SynatxObject;
import org.karina.lang.compiler.SymbolTable;

import java.util.*;


public class ItemImporting {

    static KTree.KInterface importInterface(KTree.KPackage root, SymbolTable.SymbolTableBuilder ctxBuilder, KTree.KInterface kInterface) {
        var build = KTree.KInterface.builder();
        build.region(kInterface.region());
        build.name(kInterface.name());
        build.path(kInterface.path());
        build.generics(kInterface.generics());
        build.annotations(kInterface.annotations());
        for (var generic : kInterface.generics()) {
            ctxBuilder.addGeneric(generic.name(), generic);
        }
        var ctx = new ImportContext(root, ctxBuilder.build());

        try (var collector = new ErrorCollector()) {
            for (var function : kInterface.functions()) {
                collector.collect(() -> {
                    build.function(importFunction(root, ctxBuilder.copy(), function));
                });
            }
            for (var kType : kInterface.superTypes()) {
                collector.collect(() -> {
                    build.superType(ctx.resolveType(kType));
                });
            }
        }

        var uniqueFunctions = Unique.testUnique(kInterface.functions(), KTree.KFunction::name);
        if (uniqueFunctions != null) {
            Log.importError(new ImportError.DuplicateItem(
                    uniqueFunctions.first().name().region(),
                    uniqueFunctions.duplicate().name().region(),
                    uniqueFunctions.value().value()
            ));
            throw new Log.KarinaException();
        }

        return build.build();
    }

    static KTree.KStruct importStruct(KTree.KPackage root, SymbolTable.SymbolTableBuilder ctxBuilder, KTree.KStruct struct) {

        var build = KTree.KStruct.builder();
        build.region(struct.region());
        build.name(struct.name());
        build.path(struct.path());
        build.generics(struct.generics());
        build.annotations(struct.annotations());

        for (var generic : struct.generics()) {
            ctxBuilder.addGeneric(generic.name(), generic);
        }
        var ctx = new ImportContext(root, ctxBuilder.build());

        try (var collector = new ErrorCollector()) {
            for (var field : struct.fields()) {
                collector.collect(() -> {
                    build.field(new KTree.KField(
                            field.region(),
                            field.name(),
                            ctx.resolveType(field.type())
                    ));
                });
            }
            collector.collect(() -> {
                var uniqueFields = Unique.testUnique(struct.fields(), KTree.KField::name);
                if (uniqueFields != null) {
                    Log.importError(new ImportError.DuplicateItem(
                            uniqueFields.first().name().region(),
                            uniqueFields.duplicate().name().region(),
                            uniqueFields.value().value()
                    ));
                    throw new Log.KarinaException();
                }
            });
            for (var function : struct.functions()) {
                collector.collect(() -> {
                    build.function(importFunction(root, ctxBuilder.copy(), function));
                });
            }
            collector.collect(() -> {
                var uniqueFunctions = Unique.testUnique(struct.functions(), KTree.KFunction::name);
                if (uniqueFunctions != null) {
                    Log.importError(new ImportError.DuplicateItem(
                            uniqueFunctions.first().name().region(),
                            uniqueFunctions.duplicate().name().region(),
                            uniqueFunctions.value().value()
                    ));
                    throw new Log.KarinaException();
                }
            });

            for (var implBlock : struct.implBlocks()) {
                collector.collect(() -> {
                    build.implBlock(importImplBlock(root, ctxBuilder.copy(), implBlock));
                });
            }
        }
        var flatFunctions = new ArrayList<>(struct.functions());
        for (var implBlock : struct.implBlocks()) {
            flatFunctions.addAll(implBlock.functions());
        }
        var uniqueFunctions = Unique.testUnique(flatFunctions, KTree.KFunction::name);
        if (uniqueFunctions != null) {
            Log.importError(new ImportError.DuplicateItem(
                    uniqueFunctions.first().name().region(),
                    uniqueFunctions.duplicate().name().region(),
                    uniqueFunctions.value().value()
            ));
            throw new Log.KarinaException();
        }

        return build.build();
    }

    static KTree.KEnum importEnum(KTree.KPackage root, SymbolTable.SymbolTableBuilder ctxBuilder, KTree.KEnum kEnum) {

        var build = KTree.KEnum.builder();
        build.region(kEnum.region());
        build.name(kEnum.name());
        build.path(kEnum.path());
        build.generics(kEnum.generics());
        build.annotations(kEnum.annotations());

        for (var generic : kEnum.generics()) {
            ctxBuilder.addGeneric(generic.name(), generic);
        }
        var ctx = new ImportContext(root, ctxBuilder.build());

        try (var collector = new ErrorCollector()) {
            for (var entry : kEnum.entries()) {
                collector.collect(() -> {
                    build.entry(importEnumEntry(ctx, entry));
                });
            }
        }
        var uniqueEntries = Unique.testUnique(kEnum.entries(), KTree.KEnumEntry::name);
        if (uniqueEntries != null) {
            Log.importError(new ImportError.DuplicateItem(
                    uniqueEntries.first().name().region(),
                    uniqueEntries.duplicate().name().region(),
                    uniqueEntries.first().name().value()
            ));
            throw new Log.KarinaException();
        }

        return build.build();

    }

    static KTree.KFunction importFunction(KTree.KPackage root, SymbolTable.SymbolTableBuilder ctxBuilder, KTree.KFunction function) {

        var build = KTree.KFunction.builder();
        build.region(function.region());
        build.name(function.name());
        build.path(function.path());
        build.annotations(function.annotations());
        build.generics(function.generics());

        for (var generic : function.generics()) {
            ctxBuilder.addGeneric(generic.name(), generic);
        }
        var ctx = new ImportContext(root, ctxBuilder.build());

        var parameters = new ArrayList<KTree.KParameter>();
        try (var collector = new ErrorCollector()) {
            for (var param : function.parameters()) {
                collector.collect(() -> {
                    parameters.add(new KTree.KParameter(
                            param.region(),
                            param.name(),
                            ctx.resolveType(param.type()),
                            null
                    ));
                });
            }
        }
        var uniqueParams = Unique.testUnique(parameters, KTree.KParameter::name);
        if (uniqueParams != null) {
            Log.importError(new ImportError.DuplicateItem(
                    uniqueParams.first().name().region(),
                    uniqueParams.duplicate().name().region(),
                    uniqueParams.value().value()
            ));
            throw new Log.KarinaException();
        }
        build.parameters(parameters);

        KType returnType;
        if (function.returnType() == null) {
            returnType = new KType.PrimitiveType.VoidType(function.region());
        } else {
            returnType = ctx.resolveType(function.returnType());
        }
        build.returnType(returnType);

        if (function.expr() == null) {
            build.expr(null);
        } else {
            build.expr(ExprImporting.importExpr(ctx, function.expr()));
        }
        return build.build();

    }

    static KTree.KEnumEntry importEnumEntry(ImportContext ctx, KTree.KEnumEntry entry) {
        var build = KTree.KEnumEntry.builder();
        build.region(entry.region());
        build.name(entry.name());
        var parameters = new ArrayList<KTree.KParameter>();
        for (var param : entry.parameters()) {
            parameters.add(new KTree.KParameter(
                    param.region(),
                    param.name(),
                    ctx.resolveType(param.type()),
                    null
            ));
        }
        var uniqueParams = Unique.testUnique(parameters, KTree.KParameter::name);
        if (uniqueParams != null) {
            Log.importError(new ImportError.DuplicateItem(
                    uniqueParams.first().name().region(),
                    uniqueParams.duplicate().name().region(),
                    uniqueParams.value().value()
            ));
            throw new Log.KarinaException();
        }
        build.parameters(parameters);
        return build.build();
    }

    static KTree.KImplBlock importImplBlock(KTree.KPackage root, SymbolTable.SymbolTableBuilder ctxBuilder, KTree.KImplBlock implBlock) {
        var build = KTree.KImplBlock.builder();
        build.region(implBlock.region());

        var ctx = new ImportContext(root, ctxBuilder.build());

        build.type(ctx.resolveType(implBlock.type()));

        for (var function : implBlock.functions()) {
            build.function(importFunction(root, ctxBuilder.copy(), function));
        }

        return build.build();
    }

    static List<SynatxObject.NameAndOptType> importNameAndOptTypeList(ImportContext ctx, List<SynatxObject.NameAndOptType> list, ErrorCollector collector) {
        var result = new ArrayList<SynatxObject.NameAndOptType>();
        for (var item : list) {
            collector.collect(() -> {
                KType type;
                if (item.type() == null) {
                    type = null;
                } else {
                    type = ctx.resolveType(item.type());
                }
                result.add(new SynatxObject.NameAndOptType(
                        item.region(),
                        item.name(),
                        type
                ));
            });
        }
        collector.collect(() -> {
            var uniqueArgs = Unique.testUnique(result, SynatxObject.NameAndOptType::name);
            if (uniqueArgs != null) {
                Log.importError(new ImportError.DuplicateItem(
                        uniqueArgs.first().name().region(),
                        uniqueArgs.duplicate().name().region(),
                        uniqueArgs.value().value()
                ));
                throw new Log.KarinaException();
            }
        });

        return result;

    }
    
}
