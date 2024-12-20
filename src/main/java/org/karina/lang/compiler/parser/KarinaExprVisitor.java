package org.karina.lang.compiler.parser;

import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.ObjectPath;
import org.karina.lang.compiler.Span;
import org.karina.lang.compiler.SpanOf;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.objects.SynatxObject;
import org.karina.lang.compiler.parser.gen.KarinaParser;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * Used to convert AST expression objects to the corresponding {@link KExpr}.
 */
public class KarinaExprVisitor {
    private final RegionConverter conv;
    private final KarinaVisitor visitor;
    private final KarinaTypeVisitor typeVisitor;

    public KarinaExprVisitor(KarinaVisitor visitor, KarinaTypeVisitor typeVisitor, RegionConverter converter) {
        this.conv = converter;
        this.typeVisitor = typeVisitor;
        this.visitor = visitor;
    }

    public KExpr visitExpression(KarinaParser.ExpressionContext ctx) {

        var region = this.conv.toRegion(ctx);
        if (ctx.varDef() != null) {
            var varDefContext = ctx.varDef();
            var name = this.conv.span(varDefContext.ID());
            var type = varDefContext.type() == null ? null : this.typeVisitor.visitType(varDefContext.type());
            var expr = visitExprWithBlock(varDefContext.exprWithBlock());
            return new KExpr.VariableDefinition(region, name, type, expr);
        } else if (ctx.RETURN() != null) {
            var expr = ctx.exprWithBlock() == null ? null : visitExprWithBlock(ctx.exprWithBlock());
            return new KExpr.Return(region, expr);
        } else if (ctx.if_() != null) {
            return visitIf(ctx.if_());
        } else if (ctx.for_() != null) {
            return visitFor(ctx.for_());
        } else if (ctx.while_() != null) {
            return visitWhile(ctx.while_());
        } else if (ctx.match() != null) {
            return visitMatch(ctx.match());
        } else if (ctx.closure() != null) {
            return visitClosure(ctx.closure());
        } else if (ctx.conditionalOrExpression() != null) {
            return visitConditionalOrExpression(ctx.conditionalOrExpression());
        } else if (ctx.BREAK() != null) {
            return new KExpr.Break(region);
        } else if (ctx.CONTINUE() != null) {
            return new KExpr.Continue(region);
        } else {
            Log.syntaxError(region, "Invalid expression");
            throw new Log.KarinaException();
        }

    }

    public KExpr visitBlock(KarinaParser.BlockContext ctx) {

        var expressions = ctx.expression().stream().map(this::visitExpression).toList();
        return new KExpr.Block(this.conv.toRegion(ctx), expressions);

    }

    public KExpr visitExprWithBlock(KarinaParser.ExprWithBlockContext ctx) {

        if (ctx.expression() != null) {
            return visitExpression(ctx.expression());
        } else {
            return visitBlock(ctx.block());
        }

    }

    private KExpr visitIf(KarinaParser.IfContext ctx) {

        var region = this.conv.toRegion(ctx);
        var condition = visitExprWithBlock(ctx.exprWithBlock());
        var thenBlock = visitBlock(ctx.block());
        SynatxObject.BranchPattern branchPattern = null;
        if (condition instanceof KExpr.InstanceOf instanceOf) {
            if (ctx.ID() != null) {
                condition = instanceOf.left();
                branchPattern = new SynatxObject.BranchPattern.Cast(instanceOf.type(), this.conv.span(ctx.ID()));
            } else if (ctx.optTypeList() != null) {
                condition = instanceOf.left();
                var values = visitOptTypeList(ctx.optTypeList());
                branchPattern = new SynatxObject.BranchPattern.Destruct(instanceOf.type(), values);
            }
        } else {
            if (ctx.ID() != null || ctx.optTypeList() != null) {
                Log.syntaxError(this.conv.toRegion(ctx), "Invalid pattern match");
                throw new Log.KarinaException();
            }
        }
        KExpr elseBlock;
        if (ctx.elseExpr() != null) {
            var elseExpr = ctx.elseExpr();
            if (elseExpr.if_() != null) {
                elseBlock = visitIf(elseExpr.if_());
            } else if (elseExpr.block() != null) {
                elseBlock = visitBlock(elseExpr.block());
            } else if (elseExpr.match() != null) {
                elseBlock = visitMatch(elseExpr.match());
            } else {
                Log.syntaxError(this.conv.toRegion(elseExpr), "Invalid else expression");
                throw new Log.KarinaException();
            }
        } else {
            elseBlock = null;
        }
        return new KExpr.Branch(region, condition, thenBlock, elseBlock, branchPattern);

    }

    private KExpr visitConditionalOrExpression(KarinaParser.ConditionalOrExpressionContext ctx) {

        var region = this.conv.toRegion(ctx);
        var left = visitConditionalAndExpression(ctx.conditionalAndExpression());
        if (ctx.conditionalOrExpression() != null) {
            var position = this.conv.toRegion(ctx.OR_OR());
            return new KExpr.Binary(
                    region,
                    left,
                    SpanOf.span(position, SynatxObject.BinaryOperator.OR),
                    visitConditionalOrExpression(ctx.conditionalOrExpression())
            );
        } else {
            return left;
        }

    }

    private KExpr visitConditionalAndExpression(KarinaParser.ConditionalAndExpressionContext ctx) {

        var region = this.conv.toRegion(ctx);
        var left = visitEqualityExpression(ctx.equalityExpression());
        if (ctx.conditionalAndExpression() != null) {
            var position = this.conv.toRegion(ctx.AND_AND());
            return new KExpr.Binary(
                    region,
                    left,
                    SpanOf.span(position, SynatxObject.BinaryOperator.AND),
                    visitConditionalAndExpression(ctx.conditionalAndExpression())
            );
        } else {
            return left;
        }

    }

    private KExpr visitEqualityExpression(KarinaParser.EqualityExpressionContext ctx) {

        var region = this.conv.toRegion(ctx);
        var left = visitRelationalExpression(ctx.relationalExpression());
        if (ctx.equalityExpression() != null) {
            Span position;
            SynatxObject.BinaryOperator operator;
            if (ctx.EQUALS() != null) {
                position = this.conv.toRegion(ctx.EQUALS());
                operator = SynatxObject.BinaryOperator.EQUAL;
            } else {
                position = this.conv.toRegion(ctx.NOT_EQUALS());
                operator = SynatxObject.BinaryOperator.NOT_EQUAL;
            }

            return new KExpr.Binary(
                    region,
                    left,
                    SpanOf.span(position, operator),
                    visitEqualityExpression(ctx.equalityExpression())
            );
        } else {
            return left;
        }
    }

    private KExpr visitRelationalExpression(KarinaParser.RelationalExpressionContext ctx) {

        var region = this.conv.toRegion(ctx);
        var left = visitAdditiveExpression(ctx.additiveExpression());
        if (ctx.relationalExpression() != null) {
            Span position;
            SynatxObject.BinaryOperator operator;
            if (ctx.GREATER_EQULAS() != null) {
                position = this.conv.toRegion(ctx.GREATER_EQULAS());
                operator = SynatxObject.BinaryOperator.GREATER_THAN_OR_EQUAL;
            } else if (ctx.CHAR_GREATER() != null) {
                position = this.conv.toRegion(ctx.CHAR_GREATER());
                operator = SynatxObject.BinaryOperator.GREATER_THAN;
            } else if (ctx.SMALLER_EQUALS() != null) {
                position = this.conv.toRegion(ctx.SMALLER_EQUALS());
                operator = SynatxObject.BinaryOperator.LESS_THAN_OR_EQUAL;
            } else if (ctx.CHAR_SMALLER() != null) {
                position = this.conv.toRegion(ctx.CHAR_SMALLER());
                operator = SynatxObject.BinaryOperator.LESS_THAN;
            } else {
                Log.syntaxError(region, "Invalid relational operator");
                throw new Log.KarinaException();
            }

            return new KExpr.Binary(
                    region,
                    left,
                    SpanOf.span(position, operator),
                    visitRelationalExpression(ctx.relationalExpression())
            );
        } else {
            return left;
        }

    }

    private KExpr visitAdditiveExpression(KarinaParser.AdditiveExpressionContext ctx) {

        var region = this.conv.toRegion(ctx);
        var left = visitMultiplicativeExpression(ctx.multiplicativeExpression());
        if (ctx.additiveExpression() != null) {
            Span position;
            SynatxObject.BinaryOperator operator;
            if (ctx.CHAR_PLIS() != null) {
                position = this.conv.toRegion(ctx.CHAR_PLIS());
                operator = SynatxObject.BinaryOperator.ADD;
            } else if (ctx.CHAR_MINUS() != null) {
                position = this.conv.toRegion(ctx.CHAR_MINUS());
                operator = SynatxObject.BinaryOperator.SUBTRACT;
            } else if (ctx.CHAR_AND() != null) {
                position = this.conv.toRegion(ctx.CHAR_AND());
                operator = SynatxObject.BinaryOperator.BIN_AND;
            } else {
                Log.syntaxError(region, "Invalid relational operator");
                throw new Log.KarinaException();
            }

            return new KExpr.Binary(
                    region,
                    left,
                    SpanOf.span(position, operator),
                    visitAdditiveExpression(ctx.additiveExpression())
            );
        } else {
            return left;
        }

    }

    private KExpr visitMultiplicativeExpression(KarinaParser.MultiplicativeExpressionContext ctx) {

        var region = this.conv.toRegion(ctx);
        var left = visitUnaryExpression(ctx.unaryExpression());
        if (ctx.multiplicativeExpression() != null) {
            Span position;
            SynatxObject.BinaryOperator operator;
            if (ctx.CHAR_R_SLASH() != null) {
                position = this.conv.toRegion(ctx.CHAR_R_SLASH());
                operator = SynatxObject.BinaryOperator.DIVIDE;
            } else if (ctx.CHAR_PERCENT() != null) {
                position = this.conv.toRegion(ctx.CHAR_PERCENT());
                operator = SynatxObject.BinaryOperator.MODULUS;
            } else if (ctx.CHAR_STAR() != null) {
                position = this.conv.toRegion(ctx.CHAR_STAR());
                operator = SynatxObject.BinaryOperator.MULTIPLY;
            } else {
                Log.syntaxError(region, "Invalid relational operator");
                throw new Log.KarinaException();
            }

            return new KExpr.Binary(
                    region,
                    left,
                    SpanOf.span(position, operator),
                    visitMultiplicativeExpression(ctx.multiplicativeExpression())
            );
        } else {
            return left;
        }

    }

    private KExpr visitUnaryExpression(KarinaParser.UnaryExpressionContext ctx) {

        var region = this.conv.toRegion(ctx);
        var left = visitFactor(ctx.factor());
        if (ctx.CHAR_MINUS() != null) {
            var signRegion = this.conv.toRegion(ctx.CHAR_MINUS());
            return new KExpr.Unary(region, SpanOf.span(signRegion, SynatxObject.UnaryOperator.NEGATE), left);
        } else if (ctx.CHAR_EXCLAMATION() != null) {
            var signRegion = this.conv.toRegion(ctx.CHAR_EXCLAMATION());
            return new KExpr.Unary(region, SpanOf.span(signRegion, SynatxObject.UnaryOperator.NOT), left);
        } else {
            return left;
        }

    }

    private KExpr visitFactor(KarinaParser.FactorContext ctx) {

        var region = this.conv.toRegion(ctx);
        var left = visitObject(ctx.object());
        for (var postFix : ctx.postFix()) {
            left = visitPostfix(left, postFix);
        }
        if (ctx.exprWithBlock() != null) {
            var value = visitExprWithBlock(ctx.exprWithBlock());
            return new KExpr.Assignment(region, left, value);
        } else if (ctx.instanceOf() != null) {
            var type = this.typeVisitor.visitType(ctx.instanceOf().type());
            return new KExpr.InstanceOf(region, left, type);
        } else {
            return left;
        }

    }

    private KExpr visitPostfix(KExpr prev, KarinaParser.PostFixContext ctx) {

        var region = this.conv.toRegion(ctx);
        if (ctx.ID() != null) {
            var name = this.conv.span(ctx.ID());
            return new KExpr.GetMember(region, prev, name);
        } else if (ctx.expressionList() != null) {
            var expressions = visitExprList(ctx.expressionList());
            List<KType> genHint;
            if (ctx.genericHint() != null) {
                genHint = this.visitor.visitGenericHint(ctx.genericHint());
            } else {
                genHint = List.of();
            }
            return new KExpr.Call(region, prev, genHint, expressions);
        } else if (ctx.exprWithBlock() != null) {
            var index = visitExprWithBlock(ctx.exprWithBlock());
            return new KExpr.GetArrayElement(region, prev, index);
        } else if (ctx.type() != null) {
            var type = this.typeVisitor.visitType(ctx.type());
            return new KExpr.Cast(region, prev, type);
        } else {
            Log.syntaxError(region, "Invalid postfix");
            throw new Log.KarinaException();
        }

    }

    private KExpr visitObject(KarinaParser.ObjectContext ctx) {

        var region = this.conv.toRegion(ctx);
        if (ctx.array() != null) {
            return visitArrayCreating(ctx.array());
        } else if (ctx.exprWithBlock() != null) {
            return visitExprWithBlock(ctx.exprWithBlock());
        } else if (ctx.NUMBER() != null) {
            var decimal = parseNumber(ctx.NUMBER().getText());
            return new KExpr.Number(region, decimal);
        } else if (ctx.ID() != null) {
            var text = ctx.ID().getText();
            if (ctx.initList() != null) {
                List<KType> generics;
                if (ctx.genericHint() != null) {
                    generics = this.visitor.visitGenericHint(ctx.genericHint());
                } else {
                    generics = List.of();
                }
                var initList = ctx.initList();
                var inits = initList.memberInit().stream().map(ref -> {
                    var initRegion = this.conv.toRegion(ref);
                    var name = this.conv.span(ref.ID());
                    var value = visitExprWithBlock(ref.exprWithBlock());
                    return new SynatxObject.NamedExpression(initRegion, name, value);
                }).toList();
                var name = this.conv.span(ctx.ID());
                var path = new ObjectPath(name.value());
                var type = new KType.UnprocessedType(name.region(), SpanOf.span(name.region(), path), generics);
                return new KExpr.CreateObject(
                        region,
                        type,
                        inits
                );
            } else {
                return new KExpr.Literal(region, text);
            }
        } else if (ctx.STRING_LITERAL() != null) {
            var inner = ctx.STRING_LITERAL().getText();
            var text = inner.substring(1, inner.length() - 1);
            return new KExpr.StringExpr(region, text);
        } else if (ctx.SELF() != null) {
            return new KExpr.Self(region, null);
        } else if (ctx.FALSE() != null) {
            return new KExpr.Boolean(region, false);
        } else if (ctx.TRUE() != null) {
            return new KExpr.Boolean(region, true);
        } else {
            Log.syntaxError(region, "Invalid object");
            throw new Log.KarinaException();
        }

    }

    private KExpr visitArrayCreating(KarinaParser.ArrayContext ctx) {

        var region = this.conv.toRegion(ctx);
        var typeHint = ctx.type() == null ? null : this.typeVisitor.visitType(ctx.type());
        var elements = visitExprList(ctx.expressionList());
        return new KExpr.CreateArray(region, typeHint, elements);

    }

    private List<KExpr> visitExprList(KarinaParser.ExpressionListContext ctx) {
        return ctx.exprWithBlock().stream().map(this::visitExprWithBlock).toList();
    }

    private KExpr visitClosure(KarinaParser.ClosureContext ctx) {

        var region = this.conv.toRegion(ctx);
        KType returnType;
        if (ctx.type() != null) {
            returnType = this.typeVisitor.visitType(ctx.type());
        } else {
            returnType = null;
        }
        var args = visitOptTypeList(ctx.optTypeList());
        var body = visitExprWithBlock(ctx.exprWithBlock());
        List<KType> interfaces;
        if (ctx.interfaceImpl() != null) {
            interfaces = this.typeVisitor.visitInterfaceImpl(ctx.interfaceImpl());
        } else {
            interfaces = List.of();
        }

        return new KExpr.Closure(
                region,
                args,
                returnType,
                interfaces,
                body
        );

    }

    private KExpr visitFor(KarinaParser.ForContext ctx) {

        var region = this.conv.toRegion(ctx);
        var name = this.conv.span(ctx.ID());
        var iter = visitExprWithBlock(ctx.exprWithBlock());
        var body = visitBlock(ctx.block());
        return new KExpr.For(
                region,
                name,
                iter,
                body
        );

    }

    private KExpr visitWhile(KarinaParser.WhileContext ctx) {

        var region = this.conv.toRegion(ctx);
        var condition = visitExprWithBlock(ctx.exprWithBlock());
        var body = visitBlock(ctx.block());
        return new KExpr.While(
                region,
                condition,
                body
        );

    }

    private KExpr visitMatch(KarinaParser.MatchContext ctx) {

        var region = this.conv.toRegion(ctx);
        var expr = visitExprWithBlock(ctx.exprWithBlock());
        var branches = ctx.matchCase().stream().map(this::visitMatchCase).toList();
        return new KExpr.Match(
                region,
                expr,
                branches
        );

    }

    private SynatxObject.MatchPattern visitMatchCase(KarinaParser.MatchCaseContext ctx) {

        var region = this.conv.toRegion(ctx);
        var expr = visitExprWithBlock(ctx.exprWithBlock());
        if (ctx.matchDefault() != null) {
            return new SynatxObject.MatchPattern.Default(region, expr);
        } else {
            var instance = ctx.matchInstance();
            var type = this.typeVisitor.visitStructType(instance.structType());
            if (instance.ID() != null) {
                var name = this.conv.span(instance.ID());
                return new SynatxObject.MatchPattern.Cast(region, type, name, expr);
            } else {
                var values = visitOptTypeList(instance.optTypeList());
                return new SynatxObject.MatchPattern.Destruct(region, type, values, expr);
            }
        }

    }

    private List<SynatxObject.NameAndOptType> visitOptTypeList(KarinaParser.OptTypeListContext ctx) {

        return ctx.optTypeName().stream().map(ref -> {
            var region = this.conv.toRegion(ref);
            var name = this.conv.span(ref.ID());
            var type = ref.type() == null ? null : this.typeVisitor.visitType(ref.type());
            return new SynatxObject.NameAndOptType(region, name, type);
        }).toList();

    }

    private BigDecimal parseNumber(String text) throws NumberFormatException {

        text = text.replace("_", "");
        if (text.startsWith("0x")) {
            var numberStr = text.substring(2);
            return new BigDecimal(new BigInteger(numberStr, 16));
        } else if (text.startsWith("0b")) {
            var numberStr = text.substring(2);
            return new BigDecimal(new BigInteger(numberStr, 2));
        } else {
            return new BigDecimal(text);
        }

    }
}
