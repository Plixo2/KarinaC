package org.karina.lang.lsp.features;

import lombok.AllArgsConstructor;
import org.karina.lang.compiler.utils.BranchPattern;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Span;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.symbols.CallSymbol;
import org.karina.lang.compiler.symbols.LiteralSymbol;

import java.util.List;

/**
 * Provides advanced information for fully types files
 */
@AllArgsConstructor
public class AttribProvider {
    private final List<AttribLocation> list;
    private Span rangePredicate;

    public void populateList(KTree.KUnit unit) {
        for (var item : unit.items()) {
            switch (item) {
                case KTree.KStruct typeItem -> {
                    populateStruct(typeItem);
                }
                case KTree.KFunction function -> {
                    populateFunction(function);
                }
                case null, default -> {
                    // TODO later maybe
                }
            }
        }
    }

    private void populateStruct(KTree.KStruct struct) {
        for (var function : struct.functions()) {
            populateFunction(function);
        }
        for (var implBlock : struct.implBlocks()) {
            for (var function : implBlock.functions()) {
                populateFunction(function);
            }
        }
    }

    private void populateFunction(KTree.KFunction function) {
        if (function.expr() != null) {
            populateExpr(function.expr());
        }
    }

    private void populateExpr(KExpr expr) {
        addType(expr.region(), expr.type());
        switch (expr) {
            case KExpr.Assignment assignment -> {
                populateExpr(assignment.left());
                populateExpr(assignment.right());
            }
            case KExpr.Binary binary -> {
                populateExpr(binary.left());
                populateExpr(binary.right());
            }
            case KExpr.Block block -> {
                for (var statement : block.expressions()) {
                    populateExpr(statement);
                }
            }
            case KExpr.Boolean aBoolean -> {
                // nothing to do
            }
            case KExpr.Branch branch -> {
                populateExpr(branch.condition());
                populateExpr(branch.thenArm());
                if (branch.elseArm() != null) {
                    populateExpr(branch.elseArm());
                }
                if (branch.branchPattern() != null) {
                    switch (branch.branchPattern()) {
                        case BranchPattern.Cast cast -> {
                            assert cast.symbol() != null;
                            addType(cast.castedName().region(), cast.symbol().type());
                        }
                        case BranchPattern.Destruct destruct -> {
                            //TODO
                        }
                    }
                }
            }
            case KExpr.Break aBreak -> {
                // nothing to do
            }
            case KExpr.Call call -> {
                populateExpr(call.left());
                for (var arg : call.arguments()) {
                    populateExpr(arg);
                }
                if (call.symbol() instanceof CallSymbol.CallVirtual callVirtual) {
                    addFunction(callVirtual.nameRegion(), callVirtual.path(), true);
                }
            }
            case KExpr.Cast cast -> {
                populateExpr(cast.expression());
            }
            case KExpr.Closure closure -> {
                //TODO variables
                populateExpr(closure.body());
            }
            case KExpr.Continue aContinue -> {
                // nothing to do
            }
            case KExpr.CreateArray createArray -> {
                for (var element : createArray.elements()) {
                    populateExpr(element);
                }
            }
            case KExpr.CreateObject createObject -> {
                for (var member : createObject.parameters()) {
                    populateExpr(member.expr());
                }
            }
            case KExpr.For aFor -> {
                populateExpr(aFor.iter());
                populateExpr(aFor.body());
            }
            case KExpr.GetArrayElement getArrayElement -> {
                populateExpr(getArrayElement.left());
                populateExpr(getArrayElement.index());
            }
            case KExpr.GetMember getMember -> {
                populateExpr(getMember.left());
            }
            case KExpr.IsInstanceOf isInstanceOf -> {
                populateExpr(isInstanceOf.left());
            }
            case KExpr.Literal literal -> {
                if (literal.symbol() instanceof LiteralSymbol.StaticFunction(Span region, ObjectPath path)) {
                    addFunction(region, path, false);
                }
                // nothing to do
            }
            case KExpr.Match match -> {
                populateExpr(match.value());
                for (var arm : match.cases()) {
                    populateExpr(arm.expr());
                }
            }
            case KExpr.Number number -> {
                // nothing to do
            }
            case KExpr.Return aReturn -> {
                if (aReturn.value() != null) {
                    populateExpr(aReturn.value());
                }
            }
            case KExpr.Self self -> {
                // nothing to do
            }
            case KExpr.StringExpr stringExpr -> {
                // nothing to do
            }
            case KExpr.Unary unary -> {
                populateExpr(unary.value());
            }
            case KExpr.VariableDefinition variableDefinition -> {
                assert variableDefinition.symbol() != null;
                if (variableDefinition.hint() == null) {
                    addInlayHint(variableDefinition.name().region(), variableDefinition.symbol().name(), variableDefinition.symbol().type());
                } else {
                    addType(variableDefinition.name().region(), variableDefinition.symbol().type());
                }
                if (variableDefinition.value() != null) {
                    populateExpr(variableDefinition.value());
                }
            }
            case KExpr.While aWhile -> {
                populateExpr(aWhile.condition());
                populateExpr(aWhile.body());
            }
            case KExpr.Throw aThrow -> {
                populateExpr(aThrow.value());
            }
        }
    }

    private void addType(Span region, KType type) {
        if (this.rangePredicate.overlaps(region)) {
            this.list.add(new AttribLocation.AttribType(region, type));
        }
    }

    private void addInlayHint(Span region, String text, KType type) {
        if (this.rangePredicate.overlaps(region)) {
            this.list.add(new AttribLocation.InlayHint(region, text, type));
        }
    }

    private void addFunction(Span region, ObjectPath path, boolean virtual) {
        if (this.rangePredicate.overlaps(region)) {
            this.list.add(new AttribLocation.Function(region, path, virtual));
        }
    }


    public sealed interface AttribLocation {
        Span defined();
        record AttribType(Span defined, KType type) implements AttribLocation {}
        record InlayHint(Span defined, String text, KType type) implements AttribLocation {}
        record Function(Span defined, ObjectPath path, boolean virtual) implements AttribLocation {}
    }
}
