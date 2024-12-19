package org.karina.lang.compiler.stages.variables;

import org.karina.lang.compiler.errors.ErrorCollector;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.errors.types.LinkError;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KTree;

public class VariableResolver {

    public KTree.KPackage linkTree(KTree.KPackage tree) {
        return linkPackage(tree, tree);
    }

    private KTree.KPackage linkPackage(KTree.KPackage root, KTree.KPackage kPackage) {
        var build =  KTree.KPackage.builder();
        build.path(kPackage.path());
        build.name(kPackage.name());

        try (var collector = new ErrorCollector()){
            collector.collect(() -> {
                for (var subPackage : kPackage.subPackages()) {
                    build.subPackage(linkPackage(root, subPackage));
                }
            });

            collector.collect(() -> {
                for (var unit : kPackage.units()) {
                    build.unit(linkUnit(root, unit));
                }
            });
        }

        return build.build();
    }

    private KTree.KUnit linkUnit(KTree.KPackage root, KTree.KUnit unit) {
        var build = KTree.KUnit.builder();
        build.region(unit.region());
        build.name(unit.name());
        build.path(unit.path());
        build.kImports(unit.kImports());

        try (var collector = new ErrorCollector()) {
            for (var item : unit.items()) {
                collector.collect(() -> {
                    switch (item) {
                        case KTree.KFunction kFunction -> {
                            build.item(kFunction);
                        }
                        case KTree.KEnum kenum -> {
                            build.item(kenum);
                        }
                        case KTree.KStruct kStruct -> {
                            build.item(kStruct);
                        }
                        case KTree.KInterface kInterface -> {
                            build.item(kInterface);
                        }
                    }
                });
            }
        }

        return build.build();
    }

    private KTree.KFunction linkFunction(KTree.KPackage root, KTree.KFunction function) {
        return null;
    }

    private LinkExpr linkExpr(VariableContext context, KExpr expr) {
        if (!(expr instanceof KExpr.ImportedExpr importedExpr)) {
            Log.invalidState(expr.region(), expr.getClass(), "import-state");
            throw new Log.KarinaException();
        }
        switch (importedExpr) {
            case KExpr.Assignment assignment -> {
            }
            case KExpr.Binary binary -> {
            }
            case KExpr.Block block -> {
            }
            case KExpr.Boolean aBoolean -> {
            }
            case KExpr.Branch branch -> {
            }
            case KExpr.Break aBreak -> {
            }
            case KExpr.Call call -> {
            }
            case KExpr.Cast cast -> {
            }
            case KExpr.Closure closure -> {
            }
            case KExpr.Continue aContinue -> {
            }
            case KExpr.CreateArray createArray -> {
            }
            case KExpr.CreateObject createObject -> {
            }
            case KExpr.For aFor -> {
            }
            case KExpr.InstanceOf instanceOf -> {
            }
            case KExpr.IsInstance isInstance -> {
            }
            case KExpr.Literal literal -> {
            }
            case KExpr.Match match -> {
            }
            case KExpr.Number number -> {
            }
            case KExpr.Return aReturn -> {
            }
            case KExpr.Self self -> {
                if (context.selfType() == null) {
                    Log.linkError(new LinkError.UnqualifiedSelf(
                            self.region(), context.methodRegion()
                    ));
                    throw new Log.KarinaException();
                } else {
                    var newSelf = new KExpr.LinkedSelf(
                            self.region(),
                            context.selfType()
                    );
                    return linkExpr(context, newSelf);
                }
            }
            case KExpr.StringExpr stringExpr -> {
            }
            case KExpr.Unary unary -> {
            }
            case KExpr.VariableDefinition variableDefinition -> {
            }
            case KExpr.While aWhile -> {
            }
            case KExpr.getArrayElement getArrayElement -> {
            }
            case KExpr.getMember getMember -> {
            }
        }
        throw new NullPointerException("");
//        return new LinkExpr(expr, context);
    }


    private KExpr.LinkedExpr expr(LinkExpr expr) {
        return expr.expr;
    }
    private VariableContext context(LinkExpr expr) {
        return expr.context;
    }
    private LinkExpr linkExpr(VariableContext context, KExpr.LinkedExpr expr) {
        return new LinkExpr(expr, context);
    }
    private record LinkExpr(KExpr.LinkedExpr expr, VariableContext context) {}
}
