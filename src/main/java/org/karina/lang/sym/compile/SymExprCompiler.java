package org.karina.lang.sym.compile;

import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.stages.attrib.AttribExpr;
import org.karina.lang.compiler.stages.symbols.BinOperatorSymbol;
import org.karina.lang.compiler.stages.symbols.LiteralSymbol;
import org.karina.lang.compiler.stages.symbols.NumberSymbol;
import org.karina.lang.sym.Instruction;

import java.util.ArrayList;
import java.util.List;

public class SymExprCompiler {


    public void compile(KTree.KFunction function) {
        var ctx = new SymCompileCtx(new VariableSet(0));

        assert function.expr() != null;
        compileExpr(function.expr(), ctx);
        if (!AttribExpr.doesReturn(function.expr())) {
            if(function.expr().type().isVoid()) {
                ctx.add(new Instruction.ReturnVoid());
            } else {
                ctx.add(new Instruction.Return());
            }
        }

    }

    private void compileExpr(KExpr expr, SymCompileCtx ctx) {
        switch (expr) {
            case KExpr.Assignment assignment -> {
            }
            case KExpr.Binary binary -> {
                compileExpr(binary.left(), ctx);
                compileExpr(binary.right(), ctx);

                var symbol = binary.symbol();
                assert symbol != null;
                if (symbol instanceof BinOperatorSymbol.BoolOP) {
                    var mathOp = Instruction.BoolMathOp.fromBinaryOp(symbol.operator());
                    ctx.add(new Instruction.BoolMath(mathOp));
                } else {
                    var mathOp = Instruction.NumberMathOp.fromBinaryOp(symbol.operator());

                    ctx.add(switch (symbol) {
                        case BinOperatorSymbol.DoubleOP doubleOP -> new Instruction.FloatMath(mathOp);
                        case BinOperatorSymbol.FloatOP floatOP -> new Instruction.FloatMath(mathOp);
                        case BinOperatorSymbol.IntOP intOP -> new Instruction.LongMath(mathOp);
                        case BinOperatorSymbol.LongOP longOP -> new Instruction.LongMath(mathOp);
                        //unreachable
                        default -> throw new IllegalStateException("Unexpected value: " + symbol);
                    });
                }

            }
            case KExpr.Block block -> {
                var iterator = block.expressions().iterator();

                while (iterator.hasNext()) {
                    var expression = iterator.next();
                    var hasMore = iterator.hasNext();
                    compileExpr(expression, ctx);
                    if (hasMore) {
                        ctx.add(new Instruction.Pop());
                    }
                }
            }
            case KExpr.Boolean aBoolean -> {
                Instruction instruction;
                if (aBoolean.value()) {
                    instruction = new Instruction.PushTrue();
                } else {
                    instruction = new Instruction.PushFalse();
                }
                ctx.add(instruction);
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
            case KExpr.GetArrayElement getArrayElement -> {
            }
            case KExpr.GetMember getMember -> {
            }
            case KExpr.IsInstanceOf isInstanceOf -> {
            }
            case KExpr.Literal literal -> {
                assert literal.symbol() != null;
                switch (literal.symbol()) {
                    case LiteralSymbol.StaticFunction staticFunction -> {
                        throw new NullPointerException("Invalid use of static method");
                    }
                    case LiteralSymbol.VariableReference variableReference -> {
                        var index = ctx.variableIndex(variableReference.variable());
                        ctx.add(new Instruction.LoadVariable(index));
                    }
                }
            }
            case KExpr.Match match -> {
            }
            case KExpr.Number number -> {
                assert number.symbol() != null;
                switch (number.symbol()) {
                    case NumberSymbol.DoubleValue doubleValue -> {
                        ctx.add(new Instruction.PushFloat(doubleValue.value()));
                    }
                    case NumberSymbol.FloatValue floatValue -> {
                        ctx.add(new Instruction.PushFloat(floatValue.value()));
                    }
                    case NumberSymbol.IntegerValue integerValue -> {
                        ctx.add(new Instruction.PushLong(integerValue.value()));
                    }
                    case NumberSymbol.LongValue longValue -> {
                        ctx.add(new Instruction.PushLong(longValue.value()));
                    }
                }
            }
            case KExpr.Return aReturn -> {
                if (aReturn.value() == null) {
                    ctx.add(new Instruction.ReturnVoid());
                } else {
                    compileExpr(aReturn.value(), ctx);
                    ctx.add(new Instruction.Return());
                }
            }
            case KExpr.Self self -> {
                ctx.add(new Instruction.LoadVariable(0));
            }
            case KExpr.StringExpr stringExpr -> {
            }
            case KExpr.Unary unary -> {
            }
            case KExpr.VariableDefinition variableDefinition -> {
            }
            case KExpr.While aWhile -> {
            }
            case KExpr.Throw aThrow -> {
            }
        }
    }

}
