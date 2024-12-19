package org.karina.lang.compiler.stages.imports;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.*;
import org.karina.lang.compiler.errors.ErrorCollector;
import org.karina.lang.compiler.errors.types.ImportError;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.objects.SynatxObject;

import java.util.*;

/**
 * Resolved types by using imports.
 * After this pass every path of a class type should be full qualified.
 */
public class ImportResolver {

    public KTree.KPackage importTree(KTree.KPackage root) throws Log.KarinaException {
        return importPackage(root, root);
    }

    private KTree.KPackage importPackage(KTree.KPackage root, KTree.KPackage pkg) {

        var build = KTree.KPackage.builder();
        build.name(pkg.name());
        build.path(pkg.path());
        try (var collector = new ErrorCollector()) {
            for (var subPackage : pkg.subPackages()) {
                collector.collect(() ->
                    build.subPackage(importPackage(root, subPackage))
                );
            }
            for (var unit : pkg.units()) {
                collector.collect(() ->
                    build.unit(importUnit(root, unit))
                );
            }
        }
        return build.build();

    }

    public KTree.KUnit importUnit(KTree.KPackage root, KTree.KUnit unit) {

        var build = KTree.KUnit.builder();
        build.region(unit.region());
        build.name(unit.name());
        build.path(unit.path());
        build.kImports(unit.kImports());
        var imports = ImportContext.getImports(root, unit);

        build.importedFunctions(imports.functions());
        var ctx = imports.context();

        try (var collector = new ErrorCollector()) {
            for (var item : unit.items()) {
                collector.collect(() -> {
                    switch (item) {
                        case KTree.KFunction kFunction -> {
                            build.item(importFunction(ctx, kFunction));
                        }
                        case KTree.KStruct kStruct -> {
                            build.item(importStruct(ctx, kStruct));
                        }
                        case KTree.KEnum kEnum -> {
                            build.item(importEnum(ctx, kEnum));
                        }
                        case KTree.KInterface kImplBlock -> {
                            build.item(importInterface(ctx, kImplBlock));
                        }
                    }
                });
            }
        }
        return build.build();

    }

    //#region items
    private KTree.KInterface importInterface(ImportContext ctx, KTree.KInterface kInterface) {
        var build = KTree.KInterface.builder();
        build.region(kInterface.region());
        build.name(kInterface.name());
        build.path(kInterface.path());
        build.generics(kInterface.generics());
        build.annotations(kInterface.annotations());
        var genericContext = ctx.insertGenerics(kInterface.generics());

        try (var collector = new ErrorCollector()) {
            for (var function : kInterface.functions()) {
                collector.collect(() -> {
                    build.function(importFunction(genericContext, function));
                });
            }
            for (var kType : kInterface.superTypes()) {
                collector.collect(() -> {
                    build.superType(genericContext.resolveType(kType));
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

    private KTree.KStruct importStruct(ImportContext ctx, KTree.KStruct struct) {

        var build = KTree.KStruct.builder();
        build.region(struct.region());
        build.name(struct.name());
        build.path(struct.path());
        build.generics(struct.generics());
        build.annotations(struct.annotations());
        var genericContext = ctx.insertGenerics(struct.generics());

        try (var collector = new ErrorCollector()) {
            for (var field : struct.fields()) {
                collector.collect(() -> {
                    build.field(new KTree.KField(
                            field.region(),
                            field.name(),
                            genericContext.resolveType(field.type())
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
                    build.function(importFunction(genericContext, function));
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
                    build.implBlock(importImplBlock(genericContext, implBlock));
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

    private KTree.KEnum importEnum(ImportContext ctx, KTree.KEnum kEnum) {

        var build = KTree.KEnum.builder();
        build.region(kEnum.region());
        build.name(kEnum.name());
        build.path(kEnum.path());
        build.generics(kEnum.generics());
        build.annotations(kEnum.annotations());

        var genericContext = ctx.insertGenerics(kEnum.generics());

        try (var collector = new ErrorCollector()) {
            for (var entry : kEnum.entries()) {
                collector.collect(() -> {
                    build.entry(importEnumEntry(genericContext, entry));
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

    private KTree.KFunction importFunction(ImportContext ctx, KTree.KFunction function) {

        var build = KTree.KFunction.builder();
        build.region(function.region());
        build.name(function.name());
        build.path(function.path());
        build.annotations(function.annotations());
        build.generics(function.generics());


        var genericContext = ctx.insertGenerics(function.generics());

        var parameters = new ArrayList<KTree.KParameter>();
        try (var collector = new ErrorCollector()) {
            for (var param : function.parameters()) {
                collector.collect(() -> {
                    parameters.add(new KTree.KParameter(
                            param.region(),
                            param.name(),
                            genericContext.resolveType(param.type())
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
            returnType = genericContext.resolveType(function.returnType());
        }
        build.returnType(returnType);

        if (function.expr() == null) {
            build.expr(null);
        } else {
            build.expr(importExpr(genericContext, function.expr()));
        }
        return build.build();

    }

    private KTree.KEnumEntry importEnumEntry(ImportContext ctx, KTree.KEnumEntry entry) {
        var build = KTree.KEnumEntry.builder();
        build.region(entry.region());
        build.name(entry.name());
        var parameters = new ArrayList<KTree.KParameter>();
        for (var param : entry.parameters()) {
            parameters.add(new KTree.KParameter(
                    param.region(),
                    param.name(),
                    ctx.resolveType(param.type())
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

    private KTree.KImplBlock importImplBlock(ImportContext ctx, KTree.KImplBlock implBlock) {
        var build = KTree.KImplBlock.builder();
        build.region(implBlock.region());
        build.type(ctx.resolveType(implBlock.type()));

        for (var function : implBlock.functions()) {
            build.function(importFunction(ctx, function));
        }

        return build.build();
    }

    private List<SynatxObject.NameAndOptType> importNameAndOptTypeList(ImportContext ctx, List<SynatxObject.NameAndOptType> list, ErrorCollector collector) {
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
    //#endregion

    //#region expressions
    private KExpr importExpr(ImportContext ctx, KExpr kExpr) {

        if (!(kExpr instanceof KExpr.ImportedExpr expr) ) {
            Log.invalidState(kExpr.region(), kExpr.getClass() , "import-stage");
            throw new Log.KarinaException();
        } else {
            switch (expr) {
                case KExpr.Assignment assignment -> {
                    var left = importExpr(ctx, assignment.left());
                    var right = importExpr(ctx, assignment.right());
                    return new KExpr.Assignment(assignment.region(), left, right);
                }
                case KExpr.Binary binary -> {
                    var left = importExpr(ctx, binary.left());
                    var right = importExpr(ctx, binary.right());
                    return new KExpr.Binary(binary.region(), left, binary.operator(), right);
                }
                case KExpr.Block block -> {
                    var expressions = new ArrayList<KExpr>();
                    try (var collector = new ErrorCollector()) {
                        for (var sub : block.expressions()) {
                            collector.collect(() -> {
                                expressions.add(importExpr(ctx, sub));
                            });
                        }
                    }
                    return new KExpr.Block(block.region(), expressions);
                }
                case KExpr.Boolean aBoolean -> {
                    return aBoolean;
                }
                case KExpr.Branch branch -> {
                    var condition = importExpr(ctx, branch.condition());
                    var thenArm = importExpr(ctx, branch.thenArm());
                    KExpr elseArm;
                    if (branch.elseArm() == null) {
                        elseArm = null;
                    } else {
                        elseArm = importExpr(ctx, branch.elseArm());
                    }
                    SynatxObject.BranchPattern branchPattern;
                    if (branch.branchPattern() == null) {
                        branchPattern = null;
                    } else {
                        branchPattern = switch (branch.branchPattern()) {
                            case SynatxObject.BranchPattern.Cast(KType type, SpanOf<String> castedName) -> {
                                var resolved = ctx.resolveType(type, true);
                                yield new SynatxObject.BranchPattern.Cast(resolved, castedName);
                            }
                            case SynatxObject.BranchPattern.Destruct destruct -> {
                                var variables = new ArrayList<SynatxObject.NameAndOptType>();
                                for (var variable : destruct.variables()) {
                                    KType variableType;
                                    if (variable.type() == null) {
                                        variableType = null;
                                    } else {
                                        variableType = ctx.resolveType(variable.type());
                                    }
                                    variables.add(new SynatxObject.NameAndOptType(
                                            variable.region(),
                                            variable.name(),
                                            variableType
                                    ));
                                }
                                var uniqueVariables = Unique.testUnique(variables, SynatxObject.NameAndOptType::name);
                                if (uniqueVariables != null) {
                                    Log.importError(new ImportError.DuplicateItem(
                                            uniqueVariables.first().name().region(),
                                            uniqueVariables.duplicate().name().region(),
                                            uniqueVariables.value().value()
                                    ));
                                    throw new Log.KarinaException();
                                }

                                var resolved = ctx.resolveType(destruct.type(), true);
                                yield new SynatxObject.BranchPattern.Destruct(resolved, variables);
                            }
                        };
                    }
                    return new KExpr.Branch(branch.region(), condition, thenArm, elseArm, branchPattern);
                }
                case KExpr.Break aBreak -> {
                    return aBreak;
                }
                case KExpr.Call call -> {
                    var left = importExpr(ctx, call.left());
                    var generics = new ArrayList<KType>();
                    var arguments = new ArrayList<KExpr>();
                    try (var collector = new ErrorCollector()) {
                        for (var argument : call.arguments()) {
                            collector.collect(() -> {
                                arguments.add(importExpr(ctx, argument));
                            });
                        }
                        for (var generic : call.generics()) {
                            collector.collect(() -> {
                                generics.add(ctx.resolveType(generic));
                            });
                        }
                    }

                    return new KExpr.Call(call.region(), left, generics, arguments);
                }
                case KExpr.Cast cast -> {
                    var expression = importExpr(ctx, cast.expression());
                    var type = ctx.resolveType(cast.type());
                    return new KExpr.Cast(cast.region(), expression, type);
                }
                case KExpr.Closure closure -> {
                    List<SynatxObject.NameAndOptType> args;
                    var interfaces = new ArrayList<KType>();
                    var returnType = Reference.<KType>of();
                    var body = Reference.<KExpr>of();
                    try (var collector = new ErrorCollector()) {
                        args = importNameAndOptTypeList(ctx, closure.args(), collector);
                        for (var kType : closure.interfaces()) {
                            collector.collect(() -> {
                                interfaces.add(ctx.resolveType(kType));
                            });
                        }

                        collector.collect(() -> {
                            if (closure.returnType() == null) {
                                returnType.set(null);
                            } else {
                                returnType.set(ctx.resolveType(closure.returnType()));
                            }
                        });

                        collector.collect(() -> {
                            body.set(importExpr(ctx, closure.body()));
                        });
                    }

                    return new KExpr.Closure(closure.region(), args, returnType.value, interfaces,body.value);
                }
                case KExpr.Continue aContinue -> {
                    return aContinue;
                }
                case KExpr.CreateArray createArray -> {
                    var elements = new ArrayList<KExpr>();
                    try (var collector = new ErrorCollector()) {
                        for (var element : createArray.elements()) {
                            collector.collect(() -> {
                                elements.add(importExpr(ctx, element));
                            });
                        }
                    }
                    KType hint;
                    if (createArray.hint() == null) {
                        hint = null;
                    } else {
                        hint = ctx.resolveType(createArray.hint());
                    }
                    return new KExpr.CreateArray(createArray.region(), hint, elements);
                }
                case KExpr.CreateObject createObject -> {
                    var createdType = ctx.resolveType(createObject.type(), true);
                    var parameters = new ArrayList<SynatxObject.NamedExpression>();
                    try (var collector = new ErrorCollector()){
                        for (var parameter : createObject.parameters()) {
                            collector.collect(() -> {
                                var name = parameter.name();
                                var value = importExpr(ctx, parameter.expr());
                                parameters.add(new SynatxObject.NamedExpression(
                                        parameter.region(),
                                        name,
                                        value
                                ));
                            });
                        }
                    }
                    return new KExpr.CreateObject(createObject.region(), createdType, parameters);
                }
                case KExpr.For aFor -> {
                    var iter = importExpr(ctx, aFor.iter());
                    var body = importExpr(ctx, aFor.body());
                    var name = aFor.name();
                    return new KExpr.For(aFor.region(), name, iter, body);
                }
                case KExpr.IsInstance isInstance -> {
                    var left = importExpr(ctx, isInstance.left());
                    var type = ctx.resolveType(isInstance.type(), true);
                    return new KExpr.IsInstance(isInstance.region(), left, type);
                }
                case KExpr.Literal literal -> {
                    return literal;
                }
                case KExpr.Match match -> {
                    var value = importExpr(ctx, match.value());
                    var cases = new ArrayList<SynatxObject.MatchPattern>();
                    try (var collector = new ErrorCollector()) {
                        for (var aCase : match.cases()) {
                            collector.collect(() -> {
                                var newCase = switch (aCase) {
                                    case SynatxObject.MatchPattern.Cast cast -> {
                                        var type = ctx.resolveType(cast.type(), true);
                                        var newName = cast.name();
                                        var body = importExpr(ctx, cast.expr());
                                        yield new SynatxObject.MatchPattern.Cast(
                                                cast.region(),
                                                type,
                                                newName,
                                                body
                                        );
                                    }
                                    case SynatxObject.MatchPattern.Destruct destruct -> {
                                        var body = importExpr(ctx, destruct.expr());
                                        var type = ctx.resolveType(destruct.type(), true);
                                        var args = importNameAndOptTypeList(ctx, destruct.variables(), collector);
                                        var uniqueArgs = Unique.testUnique(args, SynatxObject.NameAndOptType::name);
                                        if (uniqueArgs != null) {
                                            Log.importError(new ImportError.DuplicateItem(
                                                    uniqueArgs.first().name().region(),
                                                    uniqueArgs.duplicate().name().region(),
                                                    uniqueArgs.value().value()
                                            ));
                                            throw new Log.KarinaException();
                                        }
                                        yield new SynatxObject.MatchPattern.Destruct(
                                                destruct.region(),
                                                type,
                                                args,
                                                body);
                                    }
                                    case SynatxObject.MatchPattern.Default aDefault -> {
                                        var body = importExpr(ctx, aDefault.expr());
                                        yield new SynatxObject.MatchPattern.Default(
                                                aDefault.region(),
                                                body
                                        );
                                    }
                                };
                                cases.add(newCase);
                            });
                        }
                    }
                    return new KExpr.Match(match.region(), value, cases);
                }
                case KExpr.Number number -> {
                    return number;
                }
                case KExpr.Return aReturn -> {
                    KExpr value;
                    if (aReturn.value() == null) {
                        value = null;
                    } else {
                        value = importExpr(ctx, aReturn.value());
                    }
                    return new KExpr.Return(aReturn.region(), value);
                }
                case KExpr.StringExpr stringExpr -> {
                    return stringExpr;
                }
                case KExpr.Unary unary -> {
                    var value = importExpr(ctx, unary.value());
                    return new KExpr.Unary(unary.region(), unary.operator(), value);
                }
                case KExpr.VariableDefinition variableDefinition -> {
                    var value = importExpr(ctx, variableDefinition.value());
                    KType hint;
                    if (variableDefinition.hint() == null) {
                        hint = null;
                    } else {
                        hint = ctx.resolveType(variableDefinition.hint());
                    }
                    var name = variableDefinition.name();
                    return new KExpr.VariableDefinition(variableDefinition.region(), name, hint, value);
                }
                case KExpr.While aWhile -> {
                    var condition = importExpr(ctx, aWhile.condition());
                    var body = importExpr(ctx, aWhile.body());
                    return new KExpr.While(aWhile.region(), condition, body);
                }
                case KExpr.getArrayElement getArrayElement -> {
                    var left = importExpr(ctx, getArrayElement.left());
                    var index = importExpr(ctx, getArrayElement.index());
                    return new KExpr.getArrayElement(getArrayElement.region(), left, index);
                }
                case KExpr.getMember getMember -> {
                    var left = importExpr(ctx, getMember.left());
                    return new KExpr.getMember(getMember.region(), left, getMember.name());
                }
                case KExpr.InstanceOf instanceOf -> {
                    var left = importExpr(ctx, instanceOf.left());
                    var type = ctx.resolveType(instanceOf.type(), true);
                    return new KExpr.InstanceOf(instanceOf.region(), left, type);
                }
                case KExpr.Self self-> {
                    return expr;
                }
            }
        }
    }
    //#endregion
}
