package org.karina.lang.eval;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.karina.lang.compiler.NameAndOptType;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.Variable;
import org.karina.lang.compiler.stages.symbols.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public class Solver {
    private final FunctionCollection collection;

    public Object enterFunction(FunctionCollection.RuntimeFunction function, Object self, List<Object> args) {
        var environment = new Environment(self);

        try {
            switch (function) {
                case FunctionCollection.RuntimeFunction.KarinaFunction karinaFunction -> {
                    for (var i = 0; i < karinaFunction.parameters().size(); i++) {
                        environment.set(karinaFunction.parameters().get(i), args.get(i));
                    }
                    return eval(karinaFunction.expr(), environment);
                }
                case FunctionCollection.RuntimeFunction.JavaFunction javaFunction -> {
                    try {
                        return javaFunction.method().invoke(null, args.toArray());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                case FunctionCollection.RuntimeFunction.ClosureFunction closureFunction -> {
                    for (var i = 0; i < closureFunction.parameters().size(); i++) {
                        environment.set(closureFunction.parameters().get(i), args.get(i));
                    }
                    for (var variableObjectEntry : closureFunction.captures().entrySet()) {
                        environment.set(variableObjectEntry.getKey(), variableObjectEntry.getValue());
                    }
                    return eval(closureFunction.expr(), environment);
                }
            }

        } catch (Flow flow) {
            return flow.value();
        }

    }

    private Object eval(KExpr expr, Environment env) throws Flow {
        return switch (expr) {
            case KExpr.Assignment assignment -> {
                assert assignment.symbol() != null;
                switch (assignment.symbol()) {
                    case AssignmentSymbol.ArrayElement arrayElement -> {
                        var array = (List<Object>) eval(arrayElement.array(), env);
                        var index = ((BigDecimal) eval(arrayElement.index(), env)).intValue();
                        assert array != null;
                        array.set(index, eval(assignment.right(), env));
                    }
                    case AssignmentSymbol.Field field -> {
                        var obj = (HashMap<String,Object>) eval(field.object(), env);
                        obj.put(field.name(), eval(assignment.right(), env));
                    }
                    case AssignmentSymbol.LocalVariable localVariable -> {
                        env.set(localVariable.variable(), eval(assignment.right(), env));
                    }
                };
                yield null;

            }
            case KExpr.Binary binary -> {
                var left = eval(binary.left(), env);
                var right = eval(binary.right(), env);

                yield switch (binary.operator().value()) {
                    case ADD -> {
                        yield ((BigDecimal) left).add((BigDecimal) right);
                    }
                    case SUBTRACT -> {
                        yield ((BigDecimal) left).subtract((BigDecimal) right);
                    }
                    case MULTIPLY -> {
                        yield ((BigDecimal) left).multiply((BigDecimal) right);
                    }
                    case DIVIDE -> {
                        yield ((BigDecimal) left).divide((BigDecimal) right, RoundingMode.HALF_UP);
                    }
                    case MODULUS -> {
                        yield ((BigDecimal) left).remainder((BigDecimal) right);
                    }
                    case CONCAT -> {
                        yield left.toString() + right.toString();
                    }
                    case EQUAL -> {
                        yield left.equals(right);
                    }
                    case NOT_EQUAL -> {
                        yield !left.equals(right);
                    }
                    case LESS_THAN -> {
                        yield ((BigDecimal) left).compareTo((BigDecimal) right) < 0;
                    }
                    case LESS_THAN_OR_EQUAL -> {
                        yield ((BigDecimal) left).compareTo((BigDecimal) right) <= 0;
                    }
                    case GREATER_THAN -> {
                        yield ((BigDecimal) left).compareTo((BigDecimal) right) > 0;
                    }
                    case GREATER_THAN_OR_EQUAL -> {
                        yield ((BigDecimal) left).compareTo((BigDecimal) right) >= 0;
                    }
                    case AND -> {
                        yield (boolean) left && (boolean) right;
                    }
                    case OR -> {
                        yield (boolean) left || (boolean) right;
                    }
                };

            }
            case KExpr.Block block -> {
                Object last = null;
                for (var i = 0; i < block.expressions().size(); i++) {
                    last = eval(block.expressions().get(i), env);
                }
                yield last;
            }
            case KExpr.Boolean aBoolean -> {
                yield aBoolean.value();
            }
            case KExpr.Branch branch -> {
                if (branch.branchPattern() != null) {
                    throw new NullPointerException("Not implemented");
                }
                var eval = eval(branch.condition(), env);
                if ((boolean) eval) {
                    yield eval(branch.thenArm(), env);
                } else if (branch.elseArm() != null) {
                    yield eval(branch.elseArm(), env);
                } else {
                    yield null;
                }
            }
            case KExpr.Break aBreak -> {
                throw new Flow(Flow.Type.BREAK, null);
            }
            case KExpr.Call call -> {
                assert call.symbol() != null;

                var args = new ArrayList<>();
                for (var argument : call.arguments()) {
                    args.add(eval(argument, env));
                }

                yield switch (call.symbol()) {
                    case CallSymbol.CallStatic callStatic -> {
                        var function = this.collection.function(callStatic.path());
                        yield enterFunction(function, null, args);
                    }
                    case CallSymbol.CallVirtual callVirtual -> {
                        var function = this.collection.function(callVirtual.path());
                        var self = eval(call.left(), env);
                        yield enterFunction(function, self, args);
                    }
                    case CallSymbol.CallDynamic callDynamic -> {
                        var function = (FunctionCollection.RuntimeFunction.ClosureFunction) eval(call.left(), env);
                        assert function != null;
                        yield enterFunction(function, function.self(), args);
                    }
                };
            }
            case KExpr.Cast cast -> {
                //we only got BigDecimal, so we ignore this
                yield eval(cast.expression(), env);
            }
            case KExpr.Closure closure -> {
                assert closure.symbol() != null;
                var captures = new HashMap<Variable, Object>();
                for (var capture : closure.symbol().captures()) {
                    captures.put(capture, env.get(capture));
                }

                var params = closure.args().stream().map(NameAndOptType::symbol).toList();

                yield new FunctionCollection.RuntimeFunction.ClosureFunction(
                        closure.body(),
                        params,
                        captures,
                        env.self()
                );
            }
            case KExpr.Continue aContinue -> {
                throw new Flow(Flow.Type.CONTINUE, null);
            }
            case KExpr.CreateArray createArray -> {
                var obj = new Object[createArray.elements().size()];

                for (var i = 0; i < createArray.elements().size(); i++) {
                    obj[i] = eval(createArray.elements().get(i), env);
                }

                yield new ArrayList<>(List.of(obj));
            }
            case KExpr.CreateObject createObject -> {

                var map = new HashMap<String, Object>();

                assert createObject.symbol() != null;
                map.put("$type", createObject.symbol().path().value());

                for (var parameter : createObject.parameters()) {
                    map.put(parameter.name().value(), eval(parameter.expr(), env));
                }

                yield map;

            }
            case KExpr.For aFor -> {

                var array = (List<Object>) eval(aFor.iter(), env);

                for (var o : array) {
                    env.set(aFor.symbol(), o);
                    try {
                        eval(aFor.body(), env);
                    } catch (Flow flow) {
                        if (flow.type() == Flow.Type.BREAK) {
                            break;
                        } else if (flow.type() == Flow.Type.CONTINUE) {
                            // do nothing
                        } else {
                            throw flow;
                        }
                    }
                }
                yield null;
            }
            case KExpr.GetArrayElement getArrayElement -> {
                var array = (List<Object>) eval(getArrayElement.left(), env);
                var index = ((BigDecimal) eval(getArrayElement.index(), env)).intValue();
                yield array.get(index);
            }
            case KExpr.GetMember getMember -> {
                assert getMember.symbol() != null;
                yield switch (getMember.symbol()) {
                    case MemberSymbol.FieldSymbol fieldSymbol -> {
                        var obj = (HashMap<?, ?>) eval(getMember.left(), env);
                        yield obj.get(fieldSymbol.name());
                    }
                    case MemberSymbol.VirtualFunctionSymbol virtualFunctionSymbol -> {
                        throw new NullPointerException("Not Supported");
                    }
                };
            }
            case KExpr.IsInstanceOf isInstanceOf -> {
                var obj = eval(isInstanceOf.left(), env);

                yield switch (isInstanceOf.isType()) {
                    case KType.ArrayType arrayType -> false;
                    case KType.ClassType classType -> {
                        if (obj instanceof HashMap<?,?> map) {
                            Object $type = map.get("$type");
                            if ($type == null) {
                                yield false;
                            }
                            yield $type.equals(classType.path().value());
                        } else {
                            yield false;
                        }
                    }
                    case KType.FunctionType functionType -> false;
                    case KType.GenericLink genericLink -> false;
                    case KType.PrimitiveType primitiveType -> {
                        yield switch (primitiveType) {
                            case KType.PrimitiveType.BoolType booleanType -> obj instanceof Boolean;
                            case KType.PrimitiveType.StringType stringType -> obj instanceof String;
                            default -> primitiveType.isNumeric() && obj instanceof BigDecimal;
                        };
                    }
                    case KType.Resolvable resolvable -> false;
                    case KType.UnprocessedType unprocessedType -> false;
                };
            }
            case KExpr.Literal literal -> {
                assert literal.symbol() != null;
                yield switch (literal.symbol()) {
                    case LiteralSymbol.StaticFunction staticFunction -> {
                        throw new NullPointerException("Not Supported");
                    }
                    case LiteralSymbol.VariableReference variableReference -> {
                        yield env.get(variableReference.variable());
                    }
                };
            }
            case KExpr.Match match -> {
                throw new NullPointerException("Not implemented");
            }
            case KExpr.Number number -> {
                yield number.number();
            }
            case KExpr.Return aReturn -> {
                Object value = null;
                if (aReturn.value() != null) {
                    value = eval(aReturn.value(), env);
                }
                throw new Flow(Flow.Type.RETURN, value );
            }
            case KExpr.Self self -> {
                yield env.self();
            }
            case KExpr.StringExpr stringExpr -> {
                yield stringExpr.value();
            }
            case KExpr.Unary unary -> {
                assert unary.symbol() != null;
                yield switch (unary.symbol()) {
                    case UnaryOperatorSymbol.NegateOP negateOP ->
                            ((BigDecimal) eval(unary.value(), env)).negate();
                    case UnaryOperatorSymbol.NotOP notOP ->
                            !((boolean) eval(unary.value(), env));
                };
            }
            case KExpr.VariableDefinition variableDefinition -> {
                env.set(variableDefinition.symbol(), eval(variableDefinition.value(), env));
                yield null;
            }
            case KExpr.While aWhile -> {
                boolean condition = (boolean) eval(aWhile.condition(), env);

                while (condition) {
                    try {
                        eval(aWhile.body(), env);
                    } catch (Flow flow) {
                        if (flow.type() == Flow.Type.BREAK) {
                            break;
                        } else if (flow.type() == Flow.Type.CONTINUE) {
                            // do nothing
                        } else {
                            throw flow;
                        }
                    }
                    condition = (boolean) eval(aWhile.condition(), env);
                }
                yield null;

            }
        };
    }



    public static Solver fromTree(KTree.KPackage kPackage) {
        var functions = new ArrayList<KTree.KFunction>();
        for (var kUnit : kPackage.getAllUnitsRecursively()) {
            for (var item : kUnit.items()) {
                if (item instanceof KTree.KFunction function) {
                    functions.add(function);
                } else if (item instanceof KTree.KStruct struct) {
                    functions.addAll(struct.functions());
                }
            }
        }

        var collection = new FunctionCollection();

        for (var function : functions) {
            var expr = Objects.requireNonNull(function.expr());
            var params = function.parameters().stream().map(KTree.KParameter::symbol).toList();
            if (params.contains(null)) {
                throw new NullPointerException("Parameter is null");
            }
            var func = new FunctionCollection.RuntimeFunction.KarinaFunction(expr, params);
            collection.putFunction(function.path(), func);
        }

        return new Solver(collection);

    }
}
