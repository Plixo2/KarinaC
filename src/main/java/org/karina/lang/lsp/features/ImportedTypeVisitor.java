package org.karina.lang.lsp.features;

import lombok.AllArgsConstructor;
import org.karina.lang.compiler.*;
import org.karina.lang.compiler.objects.*;
import org.karina.lang.lsp.EventHandler;

import java.util.List;

/**
 * Visitor for an imported tree. Keeps track of types and their locations.
 * Used for GoToDefinition and Hover features.
 */
@AllArgsConstructor
public class ImportedTypeVisitor {
    private final List<SourceLocation> list;

    public void populateTypeList(KTree.KUnit unit) {

        for (var kImport : unit.kImports()) {
            var location = new SourceLocation.ImportPathRegion(
                    kImport.path().region(),
                    kImport.path().value()
            );
            switch (kImport.importType()) {
                case TypeImport.All all -> {
                    this.list.add(location);
                }
                case TypeImport.Single single -> {
                    this.list.add(location);
                    this.list.add(new SourceLocation.ImportNameToken(
                            single.region(),
                            single.name().value(),
                            kImport.path().value()
                    ));
                }
                default -> {
                    // Nothing to do
                }
            }
        }

        for (var item : unit.items()) {
            switch (item) {
                case KTree.KFunction kFunction -> {
                    fromFunction(kFunction);
                }
                case KTree.KEnum kenum -> {
                    fromEnum(kenum);
                }
                case KTree.KStruct kStruct -> {
                    fromStruct(kStruct);
                }
                case KTree.KInterface kInterface -> {
                    fromInterface(kInterface);
                }
            }
        }
    }

    private void fromFunction(KTree.KFunction kFunction) {
        for (var parameter : kFunction.parameters()) {
            fromType(parameter.type());
        }
        if (kFunction.returnType() != null) {
            fromType(kFunction.returnType());
        }
        if (kFunction.expr() != null) {
            fromExpression(kFunction.expr());
        }
    }

    private void fromEnum(KTree.KEnum kEnum) {
        for (var member : kEnum.entries()) {
            for (var parameter : member.parameters()) {
                fromType(parameter.type());
            }
        }
    }

    private void fromStruct(KTree.KStruct kStruct) {
        for (var member : kStruct.functions()) {
            fromFunction(member);
        }
        for (var member : kStruct.fields()) {
            fromType(member.type());
        }
        for (var member : kStruct.implBlocks()) {
            fromType(member.type());
            for (var function : member.functions()) {
                fromFunction(function);
            }
        }
    }

    private void fromInterface(KTree.KInterface kInterface) {
        for (var member : kInterface.functions()) {
            fromFunction(member);
        }
        for (var member : kInterface.superTypes()) {
            fromType(member);
        }
    }

    private void fromType(KType type) {
        switch (type) {
            case KType.ArrayType arrayType -> {
                fromType(arrayType.elementType());
            }
            case KType.ClassType classType -> {
                var definedRegion = classType.path().region();
                var path = classType.path().value();
                this.list.add(new SourceLocation.ClassToken(definedRegion, path, classType));
                for (var generic : classType.generics()) {
                    fromType(generic);
                }
            }
            case KType.FunctionType functionType -> {
                for (var argument : functionType.arguments()) {
                    fromType(argument);
                }
                if (functionType.returnType() != null) {
                    fromType(functionType.returnType());
                }
                for (var interfaceType : functionType.interfaces()) {
                    fromType(interfaceType);
                }
            }
            case KType.GenericLink genericType -> {
                this.list.add(
                        new SourceLocation.GenericLinkToken(genericType.region(), genericType.link().region())
                );
                // Nothing to do
            }
            case KType.PrimitiveType primitiveType -> {
                // Nothing to do
            }
            case KType.Resolvable resolvable -> {
                // Nothing to do
            }
            case KType.UnprocessedType ignored -> {
                EventHandler.INSTANCE.errorMessage("Unprocessed type: " + type);
            }

        }
    }

    private void fromExpression(KExpr kExpr) {
        switch (kExpr) {
            case KExpr.Assignment assignment -> {
                fromExpression(assignment.left());
                fromExpression(assignment.right());
            }
            case KExpr.Binary binary -> {
                fromExpression(binary.left());
                fromExpression(binary.right());
            }
            case KExpr.Block block -> {
                for (var expression : block.expressions()) {
                    fromExpression(expression);
                }
            }
            case KExpr.Boolean aBoolean -> {
                // Nothing to do
            }
            case KExpr.Branch branch -> {
                fromExpression(branch.condition());
                fromExpression(branch.thenArm());
                if (branch.elseArm() != null) {
                    fromExpression(branch.elseArm());
                }
                if (branch.branchPattern() != null) {
                    switch (branch.branchPattern()) {
                        case BranchPattern.Cast cast -> {
                            fromType(cast.type());
                        }
                        case BranchPattern.Destruct destruct -> {
                            fromType(destruct.type());
                            for (var variable : destruct.variables()) {
                                if (variable.type() != null) {
                                    fromType(variable.type());
                                }
                            }
                        }
                    }
                }
            }
            case KExpr.Break aBreak -> {
                // Nothing to do
            }
            case KExpr.Call call -> {
                fromExpression(call.left());
                for (var argument : call.arguments()) {
                    fromExpression(argument);
                }
                for (var generic : call.generics()) {
                    fromType(generic);
                }
            }
            case KExpr.Cast cast -> {
                fromExpression(cast.expression());
                fromType(cast.asType());
            }
            case KExpr.Closure closure -> {
                for (var arg : closure.args()) {
                    if (arg.type() != null) {
                        fromType(arg.type());
                    }
                }
                if (closure.returnType() != null) {
                    fromType(closure.returnType());
                }
                for (var interfaceType : closure.interfaces()) {
                    fromType(interfaceType);
                }
                fromExpression(closure.body());
            }
            case KExpr.Continue aContinue -> {
                // Nothing to do
            }
            case KExpr.CreateArray createArray -> {
                for (var element : createArray.elements()) {
                    fromExpression(element);
                }
                if (createArray.hint() != null) {
                    fromType(createArray.hint());
                }
            }
            case KExpr.CreateObject createObject -> {
                for (var parameter : createObject.parameters()) {
                    fromExpression(parameter.expr());
                }
                fromType(createObject.createType());
            }
            case KExpr.For aFor -> {
                fromExpression(aFor.iter());
                fromExpression(aFor.body());
            }
            case KExpr.IsInstanceOf isInstanceOf -> {
                fromExpression(isInstanceOf.left());
                fromType(isInstanceOf.isType());
            }
            case KExpr.Literal literal -> {
                // Nothing to do
            }
            case KExpr.Match match -> {
                fromExpression(match.value());
                for (var pattern : match.cases()) {
                    fromExpression(pattern.expr());
                    switch (pattern) {
                        case MatchPattern.Cast cast -> {
                            fromType(cast.type());
                        }
                        case MatchPattern.Default aDefault -> {
                            // Nothing to do
                        }
                        case MatchPattern.Destruct destruct -> {
                            fromType(destruct.type());
                            for (var variable : destruct.variables()) {
                                if (variable.type() != null) {
                                    fromType(variable.type());
                                }
                            }
                        }
                    }
                }
            }
            case KExpr.Number number -> {
                // Nothing to do
            }
            case KExpr.Return aReturn -> {
                if (aReturn.value() != null) {
                    fromExpression(aReturn.value());
                }
            }
            case KExpr.StringExpr stringExpr -> {
                // Nothing to do
            }
            case KExpr.Unary unary -> {
                fromExpression(unary.value());
            }
            case KExpr.VariableDefinition variableDefinition -> {
                if (variableDefinition.hint() != null) {
                    fromType(variableDefinition.hint());
                }
                if (variableDefinition.value() != null) {
                    fromExpression(variableDefinition.value());
                }
            }
            case KExpr.While aWhile -> {
                fromExpression(aWhile.condition());
                fromExpression(aWhile.body());
            }
            case KExpr.GetArrayElement getArrayElement -> {
                fromExpression(getArrayElement.left());
                fromExpression(getArrayElement.index());
            }
            case KExpr.GetMember getMember -> {
                fromExpression(getMember.left());
            }
            case KExpr.Self self -> {
                // Nothing to do
            }
        }
    }

    public sealed interface SourceLocation {
        Span defined();
        record ImportPathRegion(Span defined, ObjectPath path) implements SourceLocation {

        }
        record ImportNameToken(Span defined, String name, ObjectPath unitPath) implements SourceLocation {

        }
        record ClassToken(Span defined, ObjectPath path, KType.ClassType classType) implements SourceLocation {

        }

        record GenericLinkToken(Span defined, Span linked) implements SourceLocation {

        }

    }
}
