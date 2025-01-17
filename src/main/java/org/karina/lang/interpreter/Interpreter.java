package org.karina.lang.interpreter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.karina.lang.compiler.symbols.*;
import org.karina.lang.compiler.utils.BranchPattern;
import org.karina.lang.compiler.utils.NameAndOptType;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.utils.Variable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public class Interpreter {
    private final FunctionCollection collection;

    public Object eval(FunctionCollection.RuntimeFunction function, Object self, List<Object> args) {
        try {
            return enterFunction(function, self, args);
        } catch(EvalException e) {
            e.printStackTrace();
            return e.object();
        }
    }

    private Object enterFunction(FunctionCollection.RuntimeFunction function, Object self, List<Object> args) {
        var environment = new Environment(self);

        try {
            switch (function) {
                case FunctionCollection.RuntimeFunction.KarinaFunction karinaFunction -> {
                    if (args.size() != karinaFunction.parameters().size()) {
                        throw new RuntimeException("Expected " + karinaFunction.parameters().size() + " arguments, got " + args.size());
                    }
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
                        var array = (Object[]) eval(arrayElement.array(), env);
                        assert array != null;
                        var indexObj = eval(arrayElement.index(), env);
                        assert indexObj != null;
                        var index = ((BigDecimal) indexObj).intValue();
                        array[index] = eval(assignment.right(), env);
                    }
                    case AssignmentSymbol.Field field -> {
                        var obj = (HashMap<String, Object>) eval(field.object(), env);
                        assert obj != null;
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
                assert left != null;
                assert right != null;
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
                var eval = eval(branch.condition(), env);
                assert eval != null;
                boolean condition;
                if (branch.branchPattern() != null) {
                    if (!(branch.branchPattern() instanceof BranchPattern.Cast cast)) {
                        throw new NullPointerException("Not implemented");
                    }
                    condition = checkCast(eval, cast.type());
                    env.set(cast.symbol(), eval);

                } else {
                    condition = (boolean) eval;
                }

                if (condition) {
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
                    case CallSymbol.CallInterface callInterface -> {
                        var self = eval(call.left(), env);
                        if (!(self instanceof Map<?, ?> map)) {
                            throw new NullPointerException("Not a map: " + toString(self));
                        }
                        var object = map.get("$type");
                        if (!(object instanceof ObjectPath path)) {
                            throw new NullPointerException("No $type in " + toString(self));
                        }

                        var function = this.collection.vTable(path, callInterface.path());
                        yield enterFunction(function, self, args);
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

                yield obj;
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

                var array = (Object[]) eval(aFor.iter(), env);
                assert array != null;

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
                var array = (Object[]) eval(getArrayElement.left(), env);
                assert array != null;
                var indexVal = (BigDecimal) eval(getArrayElement.index(), env);
                assert indexVal != null;
                var index = indexVal.intValue();
                yield array[index];
            }
            case KExpr.GetMember getMember -> {
                assert getMember.symbol() != null;
                yield switch (getMember.symbol()) {
                    case MemberSymbol.FieldSymbol fieldSymbol -> {
                        var obj = (HashMap<?, ?>) eval(getMember.left(), env);
                        assert obj != null;
                        yield obj.get(fieldSymbol.name());
                    }
                    case MemberSymbol.VirtualFunctionSymbol virtualFunctionSymbol -> {
                        throw new NullPointerException("Not Supported");
                    }
                    case MemberSymbol.InterfaceFunctionSymbol interfaceFunctionSymbol -> {
                        throw new NullPointerException("Not Supported");
                    }
                    case MemberSymbol.ArrayLength arrayLength -> {
                        var obj = (Object[]) eval(getMember.left(), env);
                        assert obj != null;
                        yield new BigDecimal(obj.length);
                    }
                };
            }
            case KExpr.IsInstanceOf isInstanceOf -> {
                var obj = eval(isInstanceOf.left(), env);
                yield checkCast(obj, isInstanceOf.isType());
            }
            case KExpr.Literal literal -> {
                assert literal.symbol() != null;
                yield switch (literal.symbol()) {
                    case LiteralSymbol.StaticFunction staticFunction -> {
                        throw new NullPointerException("Not Supported");
                    }
                    case LiteralSymbol.StructReference structReference -> {
                        throw new NullPointerException("Not Supported");
                    }
                    case LiteralSymbol.InterfaceReference interfaceReference -> {
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
                var eval = eval(unary.value(), env);
                assert eval != null;
                yield switch (unary.symbol()) {
                    case UnaryOperatorSymbol.NegateOP negateOP ->
                            ((BigDecimal) eval).negate();
                    case UnaryOperatorSymbol.NotOP notOP ->
                            !((boolean) eval);
                };
            }
            case KExpr.VariableDefinition variableDefinition -> {
                env.set(variableDefinition.symbol(), eval(variableDefinition.value(), env));
                yield null;
            }
            case KExpr.While aWhile -> {
                var eval = eval(aWhile.condition(), env);
                assert eval != null;
                boolean condition = (boolean) eval;

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
                    var rest = eval(aWhile.condition(), env);
                    assert rest != null;
                    condition = (boolean) rest;
                }
                yield null;

            }
            case KExpr.Throw aThrow -> {
                var value = eval(aThrow.value(), env);
                assert value != null;
                throw new EvalException(value);
            }
        };
    }

    public static String toString(Object object) {
        if (object == null) {
            return "null";
        }

        if (object.getClass().isArray()) {
            return Arrays.toString((Object[]) object);
        } else if (object instanceof HashMap<?,?> map) {
            var builder = new StringBuilder();
            builder.append("{");
            for (var entry : map.entrySet()) {
                builder.append(entry.getKey());
                builder.append(": ");
                builder.append(toString(entry.getValue()));
                builder.append(", ");
            }
            builder.append("}");
            return builder.toString();
        } else {
            return object.toString();
        }

    }

    private boolean checkCast(Object object, KType cast) {

        return switch (cast) {
            case KType.ArrayType arrayType -> false;
            case KType.ClassType classType -> {
                if (object instanceof HashMap<?,?> map) {
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
                    case KType.PrimitiveType.BoolType booleanType -> object instanceof Boolean;
                    case KType.PrimitiveType.StringType stringType -> object instanceof String;
                    default -> primitiveType.isNumeric() && object instanceof BigDecimal;
                };
            }
            case KType.Resolvable resolvable -> false;
            case KType.UnprocessedType unprocessedType -> false;
            case KType.AnyClass anyClass -> false;
        };
    }


}
