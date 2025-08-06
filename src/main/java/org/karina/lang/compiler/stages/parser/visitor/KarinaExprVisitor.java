package org.karina.lang.compiler.stages.parser.visitor;

import com.google.common.collect.ImmutableList;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.text.StringEscapeUtils;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.AttribError;
import org.karina.lang.compiler.stages.parser.RegionContext;
import org.karina.lang.compiler.stages.parser.gen.KarinaParser;
import org.karina.lang.compiler.stages.parser.visitor.model.KarinaUnitVisitor;
import org.karina.lang.compiler.utils.*;
import org.karina.lang.compiler.utils.symbols.LiteralSymbol;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.function.Predicate;

/**
 * Used to convert AST expression objects to the corresponding {@link KExpr}.
 */
public class KarinaExprVisitor implements IntoContext {
    private final RegionContext conv;
    private final KarinaUnitVisitor visitor;
    private final KarinaTypeVisitor typeVisitor;

    public KarinaExprVisitor(KarinaUnitVisitor visitor, KarinaTypeVisitor typeVisitor, RegionContext converter) {
        this.conv = converter;
        this.typeVisitor = typeVisitor;
        this.visitor = visitor;
    }

    public KExpr visitExpression(KarinaParser.ExpressionContext ctx) {

        var region = this.conv.toRegion(ctx);
        if (ctx.varDef() != null) {
            var varDefContext = ctx.varDef();
            var name = this.conv.region(varDefContext.id());
            var type = varDefContext.type() == null ? null : this.typeVisitor.visitType(varDefContext.type());
            var expr = visitExprWithBlock(varDefContext.exprWithBlock());
            return new KExpr.VariableDefinition(region, name, type, expr, null);
        } else if (ctx.RETURN() != null) {
            var expr = ctx.exprWithBlock() == null ? null : visitExprWithBlock(ctx.exprWithBlock());
            return new KExpr.Return(region, expr, null);
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
        } else if (ctx.throw_() != null) {
            return new KExpr.Throw(region, visitExprWithBlock(ctx.throw_().exprWithBlock()));
        } else {
            Log.syntaxError(this, region, "Invalid expression");
            throw new Log.KarinaException();
        }

    }

    public KExpr visitBlock(KarinaParser.BlockContext ctx) {

        var expressions = ctx.expression().stream().map(this::visitExpression).toList();
        return new KExpr.Block(this.conv.toRegion(ctx), expressions,  null, false);

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
        BranchPattern branchPattern = null;
        if (condition instanceof KExpr.IsInstanceOf(Region instanceRegion, KExpr left, KType isType)) {
            if (isType.isVoid() || isType.isPrimitive()) {
                Log.error(this, new AttribError.NotSupportedType(instanceRegion, isType));
                throw new Log.KarinaException();
            }
            if (ctx.id() != null) {
                condition = left;
                branchPattern = new BranchPattern.Cast(
                        instanceRegion, isType,
                        this.conv.region(ctx.id()),
                        null
                );
            } else if (ctx.optTypeList() != null) {
                condition = left;
                var values = visitOptTypeList(ctx.optTypeList());
                branchPattern = new BranchPattern.Destruct(instanceRegion, isType, values);
            }
        } else {
            if (ctx.id() != null || ctx.optTypeList() != null) {
                Log.syntaxError(this, this.conv.toRegion(ctx), "Invalid pattern match");
                throw new Log.KarinaException();
            }
        }
        ElsePart elsePart = null;

        if (ctx.elseExpr() != null) {
            var elseExpr = ctx.elseExpr();
            BranchPattern elseBranchPattern = null;
            var shortPattern = elseExpr.isShort();
            if (shortPattern != null) {
                var regionShortPattern = this.conv.toRegion(shortPattern);
                var isType = this.typeVisitor.visitType(shortPattern.type());
                if (isType.isVoid() || isType.isPrimitive()) {
                    var regionInner = this.conv.toRegion(shortPattern.type());
                    Log.error(this, new AttribError.NotSupportedType(regionInner, isType));
                    throw new Log.KarinaException();
                }
                if (shortPattern.id() != null) {
                    elseBranchPattern = new BranchPattern.Cast(
                            regionShortPattern,
                            isType,
                            this.conv.region(shortPattern.id()),
                            null
                    );
                } else if (shortPattern.optTypeList() != null) {
                    var values = visitOptTypeList(shortPattern.optTypeList());
                    elseBranchPattern = new BranchPattern.Destruct(regionShortPattern, isType, values);
                } else {
                    elseBranchPattern = new BranchPattern.JustType(regionShortPattern, isType);
                }
            }


            KExpr elseBlock;
            if (elseExpr.if_() != null) {
                elseBlock = visitIf(elseExpr.if_());
            } else if (elseExpr.block() != null) {
                elseBlock = visitBlock(elseExpr.block());
            } else if (elseExpr.match() != null) {
                elseBlock = visitMatch(elseExpr.match());
            } else {
                Log.syntaxError(this, this.conv.toRegion(elseExpr), "Invalid else expression");
                throw new Log.KarinaException();
            }

            elsePart = new ElsePart(elseBlock, elseBranchPattern);
        }

        return new KExpr.Branch(region, condition, thenBlock, elsePart, branchPattern, null);

    }

    private KExpr visitConditionalOrExpression(KarinaParser.ConditionalOrExpressionContext ctx) {

        var region = this.conv.toRegion(ctx);
        var left = visitConditionalAndExpression(ctx.conditionalAndExpression());
        if (ctx.conditionalOrExpression() != null) {
            var position = this.conv.toRegion(ctx.OR_OR());
            return new KExpr.Binary(
                    region,
                    left,
                    RegionOf.region(position, BinaryOperator.OR),
                    visitConditionalOrExpression(ctx.conditionalOrExpression()),
                    null
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
                    RegionOf.region(position, BinaryOperator.AND),
                    visitConditionalAndExpression(ctx.conditionalAndExpression()),
                    null
            );
        } else {
            return left;
        }

    }

    private KExpr visitEqualityExpression(KarinaParser.EqualityExpressionContext ctx) {

        var region = this.conv.toRegion(ctx);
        var left = visitRelationalExpression(ctx.relationalExpression());
        if (ctx.equalityExpression() != null) {
            Region position;
            BinaryOperator operator;
            if (ctx.EQUALS() != null) {
                position = this.conv.toRegion(ctx.EQUALS());
                operator = BinaryOperator.EQUAL;
            } else if (ctx.STRICT_NOT_EQUALS() != null) {
                position = this.conv.toRegion(ctx.STRICT_NOT_EQUALS());
                operator = BinaryOperator.STRICT_NOT_EQUAL;
            } else if (ctx.STRICT_EQUALS() != null) {
                position = this.conv.toRegion(ctx.STRICT_EQUALS());
                operator = BinaryOperator.STRICT_EQUAL;
            }else {
                position = this.conv.toRegion(ctx.NOT_EQUALS());
                operator = BinaryOperator.NOT_EQUAL;
            }

            return new KExpr.Binary(
                    region,
                    left,
                    RegionOf.region(position, operator),
                    visitEqualityExpression(ctx.equalityExpression()),
                    null
            );
        } else {
            return left;
        }
    }

    private KExpr visitRelationalExpression(KarinaParser.RelationalExpressionContext ctx) {

        var region = this.conv.toRegion(ctx);
        var left = visitAdditiveExpression(ctx.additiveExpression());
        if (ctx.relationalExpression() != null) {
            Region position;
            BinaryOperator operator;
            if (ctx.GREATER_EQULAS() != null) {
                position = this.conv.toRegion(ctx.GREATER_EQULAS());
                operator = BinaryOperator.GREATER_THAN_OR_EQUAL;
            } else if (ctx.CHAR_GREATER() != null) {
                position = this.conv.toRegion(ctx.CHAR_GREATER());
                operator = BinaryOperator.GREATER_THAN;
            } else if (ctx.SMALLER_EQUALS() != null) {
                position = this.conv.toRegion(ctx.SMALLER_EQUALS());
                operator = BinaryOperator.LESS_THAN_OR_EQUAL;
            } else if (ctx.CHAR_SMALLER() != null) {
                position = this.conv.toRegion(ctx.CHAR_SMALLER());
                operator = BinaryOperator.LESS_THAN;
            } else {
                Log.syntaxError(this, region, "Invalid relational operator");
                throw new Log.KarinaException();
            }

            return new KExpr.Binary(
                    region,
                    left,
                    RegionOf.region(position, operator),
                    visitRelationalExpression(ctx.relationalExpression()),
                    null
            );
        } else {
            return left;
        }

    }

    private KExpr visitAdditiveExpression(KarinaParser.AdditiveExpressionContext ctx) {

        var region = this.conv.toRegion(ctx);
        var left = visitMultiplicativeExpression(ctx.multiplicativeExpression());
        if (ctx.additiveExpression() != null) {
            Region position;
            BinaryOperator operator;
            if (ctx.CHAR_PLIS() != null) {
                position = this.conv.toRegion(ctx.CHAR_PLIS());
                operator = BinaryOperator.ADD;
            } else if (ctx.CHAR_MINUS() != null) {
                position = this.conv.toRegion(ctx.CHAR_MINUS());
                operator = BinaryOperator.SUBTRACT;
            } else if (ctx.CHAR_AND() != null) {
                position = this.conv.toRegion(ctx.CHAR_AND());
                operator = BinaryOperator.CONCAT;
            } else {
                Log.syntaxError(this, region, "Invalid relational operator");
                throw new Log.KarinaException();
            }

            return new KExpr.Binary(
                    region,
                    left,
                    RegionOf.region(position, operator),
                    visitAdditiveExpression(ctx.additiveExpression()),
                    null
            );
        } else {
            return left;
        }

    }

    private KExpr visitMultiplicativeExpression(KarinaParser.MultiplicativeExpressionContext ctx) {

        var region = this.conv.toRegion(ctx);
        var left = visitUnaryExpression(ctx.unaryExpression());
        if (ctx.multiplicativeExpression() != null) {
            Region position;
            BinaryOperator operator;
            if (ctx.CHAR_R_SLASH() != null) {
                position = this.conv.toRegion(ctx.CHAR_R_SLASH());
                operator = BinaryOperator.DIVIDE;
            } else if (ctx.CHAR_PERCENT() != null) {
                position = this.conv.toRegion(ctx.CHAR_PERCENT());
                operator = BinaryOperator.MODULUS;
            } else if (ctx.CHAR_STAR() != null) {
                position = this.conv.toRegion(ctx.CHAR_STAR());
                operator = BinaryOperator.MULTIPLY;
            } else {
                Log.syntaxError(this, region, "Invalid relational operator");
                throw new Log.KarinaException();
            }

            return new KExpr.Binary(
                    region,
                    left,
                    RegionOf.region(position, operator),
                    visitMultiplicativeExpression(ctx.multiplicativeExpression()),
                    null
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
            return new KExpr.Unary(region, RegionOf.region(signRegion, UnaryOperator.NEGATE), left, null);
        } else if (ctx.CHAR_EXCLAMATION() != null) {
            var signRegion = this.conv.toRegion(ctx.CHAR_EXCLAMATION());
            return new KExpr.Unary(region, RegionOf.region(signRegion, UnaryOperator.NOT), left, null);
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
            return new KExpr.Assignment(region, left, value, null);
        } else if (ctx.isInstanceOf() != null) {
            var type = this.typeVisitor.visitType(ctx.isInstanceOf().type());
            return new KExpr.IsInstanceOf(region, left, type);
        } else {
            return left;
        }

    }

    private KExpr visitPostfix(KExpr prev, KarinaParser.PostFixContext ctx) {

        var region = this.conv.toRegion(ctx);
        var regionMerged = region.merge(prev.region());
        if (ctx.id() != null) {
            var name = this.conv.region(ctx.id());
            return new KExpr.GetMember(regionMerged, prev, name, false, null);
        } else if (ctx.CLASS() != null) {
            var nameRegion = this.conv.toRegion(ctx.CLASS());
            var name = RegionOf.region(nameRegion, "class");
            return new KExpr.GetMember(regionMerged, prev, name, false, null);
        } else if (ctx.CHAR_QUESTION() != null) {
            return new KExpr.Unwrap(regionMerged, prev, null);
        } else if (ctx.expressionList() != null) {
            var expressions = visitExprList(ctx.expressionList());
            List<KType> genHint;
            if (ctx.genericHint() != null) {
                genHint = this.visitor.visitGenericHint(ctx.genericHint());
            } else {
                genHint = List.of();
            }
            if (prev instanceof KExpr.GetMember(var regionInner, var left, var name, _, var symbol)) {
                prev = new KExpr.GetMember(regionInner, left, name, true, symbol);
            }

            return new KExpr.Call(regionMerged, prev, genHint, expressions, null);
        } else if (ctx.exprWithBlock() != null) {
            var index = visitExprWithBlock(ctx.exprWithBlock());
            return new KExpr.GetArrayElement(regionMerged, prev, index, null);
        } else if (ctx.type() != null) {
            var type = this.typeVisitor.visitType(ctx.type());

            CastTo castTo = null;
            if (type instanceof KType.UnprocessedType unprocessedType) {
                if (unprocessedType.name().value().mkString("-").equals("_") && unprocessedType.generics().isEmpty()) {
                    castTo = new CastTo.AutoCast();
                }
            }
            if (castTo == null) {
                castTo = new CastTo.CastToType(type);
            }
            return new KExpr.Cast(regionMerged, prev, castTo, null);
        } else {
            Log.syntaxError(this, region, "Invalid postfix ");
//            Log.syntaxError(region, "Invalid postfix " + ctx.toString(PARSER));
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
            try {
                var text = ctx.NUMBER().getText();
                var decimal = parseNumber(text);
                var decimalStr = text.contains(".") || text.contains("e") || text.contains("E");
                return new KExpr.Number(region, decimal, decimalStr, null);
            } catch (NumberFormatException e) {
                Log.syntaxError(this, region, "Invalid number");
                throw new Log.KarinaException();
            }
        } else if (ctx.id() != null && !ctx.id().isEmpty()) {
            if (ctx.initList() != null) {
                //
                List<KType> generics;
                if (ctx.genericHint() != null) {
                    generics = this.visitor.visitGenericHint(ctx.genericHint());
                } else {
                    generics = List.of();
                }
                var initList = ctx.initList();
                var inits = initList.memberInit().stream().map(ref -> {
                    var initRegion = this.conv.toRegion(ref);
                    var name = this.conv.region(ref.id());
                    var value = visitExprWithBlock(ref.exprWithBlock());
                    return new NamedExpression(initRegion, name, value, null);
                }).toList();
                var names = ctx.id().stream().map(this.conv::region).toList();
                var regionOfName = names.stream().map(RegionOf::region).reduce(Region::merge).orElseThrow(() -> {
                    Log.temp(this, region, "Invalid region");
                    return new Log.KarinaException();
                });

                var path = new ObjectPath(names.stream().map(RegionOf::value).toList());
                var type = new KType.UnprocessedType(regionOfName, RegionOf.region(regionOfName, path), generics);
                return new KExpr.CreateObject(
                        region,
                        type,
                        inits
                );
            } else {
                if (ctx.id().size() > 1) {
                    var elements = ctx.id().stream().map(this.conv::escapeID).toList();
                    return new KExpr.StaticPath(region, new ObjectPath(elements), null);
                } else {
                    var text = this.conv.escapeID(ctx.id().getFirst());
                    return new KExpr.Literal(region, text, null);
                }
            }
        } else if (ctx.STRING_LITERAL() != null) {
            return visitString(ctx.STRING_LITERAL());
        } else if (ctx.CHAR_LITERAL() != null) {
            return visitTickedString(ctx.CHAR_LITERAL());
        } else if (ctx.SELF() != null) {
            return new KExpr.Self(region, null);
        } else if (ctx.superCall() != null) {
            return visitCallExpr(ctx.superCall());
        } else if (ctx.FALSE() != null) {
            return new KExpr.Boolean(region, false);
        } else if (ctx.TRUE() != null) {
            return new KExpr.Boolean(region, true);
        } else {
            Log.syntaxError(this, region, "Invalid object");
            throw new Log.KarinaException();
        }

    }

    private KExpr visitCallExpr(KarinaParser.SuperCallContext ctx) {
        var region = this.conv.toRegion(ctx);
        var structType = this.typeVisitor.visitStructType(ctx.structType());
        String name;
        if (ctx.id() != null) {
            name = this.conv.escapeID(ctx.id());
        } else {
            name = "<init>";
        }

        return new KExpr.SpecialCall(region, new InvocationType.SpecialInvoke(name, structType));
    }

    private KExpr visitString(TerminalNode ctx) {
        //escaping is done later when omitting bytecode
        var region = this.conv.toRegion(ctx);
        var inner = ctx.getText();
        var text = inner.substring(1, inner.length() - 1);
        return new KExpr.StringExpr(region, text, false);
    }

    private KExpr visitTickedString(TerminalNode ctx) {
        var region = this.conv.toRegion(ctx);
        var inner = ctx.getText();
        var text = inner.substring(1, inner.length() - 1);

        //check if the length is one, so it has the be a char
        //we have to check the unescaped string, because the may char is escaped

        //this call should be safe without exception handling
        var escapedText = StringEscapeUtils.unescapeJava(text);

        if (escapedText.length() == 1) {
            return new KExpr.StringExpr(region, text, true);
        }

        //TODO make better regions for better error message, or move logic into grammar if possible

        var components = ImmutableList.<StringComponent>builder();

        var length = text.length();
        var isEscaped = false;
        var previousAddedIndexEnd = 0;
        for (var i = 0; i < length; i++) {
            var c = text.charAt(i);

            //escaping
            if (isEscaped) {
                isEscaped = false;
                continue;
            }
            if (c == '\\') {
                isEscaped = true;
                continue;
            }

            if (c == '$') {
                if (previousAddedIndexEnd < i) {
                    components.add(new StringComponent.StringLiteralComponent(
                            text.substring(previousAddedIndexEnd, i)
                    ));
                }
                //we skip the first character, as it might be a escaped character for karina keywords
                //The grammar ensures that the first character is valid for a literal otherwise
                var next = i + 2;
                //check for a literal after the $
                //this call also does range checking.
                next = continueIdentifier(text, next);
                if (next == -1) {
                    Log.syntaxError(this, region, "Invalid string interpolation, expected identifier after $");
                    throw new Log.KarinaException();
                }
                var name = this.conv.escapeID(text.substring(i+1, next));
                i = next-1;

                var literal = new KExpr.Literal(
                        region,
                        name,
                        null
                );
                components.add(
                        new StringComponent.ExpressionComponent(region, literal)
                );
                previousAddedIndexEnd = next;
            }
        }
        if (previousAddedIndexEnd < length) {
            components.add(new StringComponent.StringLiteralComponent(
                    text.substring(previousAddedIndexEnd)
            ));
        }

        var stringComponents = components.build();
        if (stringComponents.size() == 1 &&
                stringComponents.getFirst() instanceof StringComponent.StringLiteralComponent(
                        var value
                )) {

            return new KExpr.StringExpr(region, value, false);

        }
        return new KExpr.StringInterpolation(region, stringComponents);
    }

    /**
     * Matches a identifier. Also does range checking.
     * @return -1 if the start does not match a keyword, otherwise the end of the keyword
     */
    private int continueIdentifier(String text, int index) {
        Predicate<Character> next =
                c -> c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '_' || c >= '0' && c <= '9';

        var length = text.length();
        if (index < 0 || index > length) {
            return -1;
        }
        if (index == length) {
            return length;
        }
        var i = index;
        while (i < length) {
            var c = text.charAt(i);
            if (!next.test(c)) {
                break;
            }
            i++;
        }

        return i;

    }



    private KExpr visitArrayCreating(KarinaParser.ArrayContext ctx) {

        var region = this.conv.toRegion(ctx);
        var typeHint = ctx.type() == null ? null : this.typeVisitor.visitType(ctx.type());
        var elements = visitExprList(ctx.expressionList());
        return new KExpr.CreateArray(region, typeHint, elements, null);

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
            interfaces = this.typeVisitor.visitInterfaceImpl(ctx.interfaceImpl()).stream().map(ref -> (KType)ref).toList();
        } else {
            interfaces = List.of();
        }

        return new KExpr.Closure(
                region,
                args,
                returnType,
                interfaces,
                body,
                null
        );

    }

    private KExpr visitFor(KarinaParser.ForContext ctx) {

        var region = this.conv.toRegion(ctx);

        var varPart = ctx.optTypeName();
        var varRegion = this.conv.toRegion(varPart);
        var varName = this.conv.region(varPart.id());
        var varType = varPart.type() == null ? null : this.typeVisitor.visitType(varPart.type());
        var variable = new NameAndOptType(varRegion, varName, varType, null);

        var iter = visitExprWithBlock(ctx.exprWithBlock());
        var body = visitBlock(ctx.block());
        return new KExpr.For(
                region,
                variable,
                iter,
                body,
                null
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

    private MatchPattern visitMatchCase(KarinaParser.MatchCaseContext ctx) {

        var region = this.conv.toRegion(ctx);
        var expr = visitExprWithBlock(ctx.exprWithBlock());
        if (ctx.matchDefault() != null) {
            return new MatchPattern.Default(region, expr);
        } else {
            var instance = ctx.matchInstance();
            var type = this.typeVisitor.visitStructType(instance.structType());
            if (instance.id() != null) {
                var name = this.conv.region(instance.id());
                return new MatchPattern.Cast(region, type, name, expr);
            } else {
                var values = visitOptTypeList(instance.optTypeList());
                return new MatchPattern.Destruct(region, type, values, expr);
            }
        }

    }

    private List<NameAndOptType> visitOptTypeList(KarinaParser.OptTypeListContext ctx) {

        return ctx.optTypeName().stream().map(ref -> {
            var region = this.conv.toRegion(ref);
            var name = this.conv.region(ref.id());
            var type = ref.type() == null ? null : this.typeVisitor.visitType(ref.type());
            return new NameAndOptType(region, name, type, null);
        }).toList();

    }

    public BigDecimal parseNumber(String text) throws NumberFormatException {

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


    @Override
    public Context intoContext() {
        return this.visitor.context();
    }
}
