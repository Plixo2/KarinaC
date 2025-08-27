package org.karina.lang.compiler.utils;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.utils.logging.Log;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.model_api.pointer.MethodPointer;
import org.karina.lang.compiler.stages.attrib.AttributionContext;

import java.lang.reflect.Modifier;
import java.util.*;

public class MethodHelper {

    /**
     * @return a list of methods that have not been implemented for a given class
     */
    public static List<MethodToImplement> getMethodsToImplementForClass(Context c, Model model, KType.ClassType classType) {
        return getMethodsToImplementForClass(c, model, classType, true, true);
    }

    /**
     * @return a list of methods that have been implemented for a given class, for bridge method generation
     */
    public static List<MethodToImplement> getMethodForBridgeConstruction(Context c, Model model, KType.ClassType classType) {
        return getMethodsToImplementForClass(c, model, classType , true, false);
    }

    /**
     * Creates a returning expression for a method, always ending with a return statement.
     */
    public static KExpr createRetuningExpression(KExpr expression, KType returnType, AttributionContext contextNew) {
        var isVoid = returnType.isVoid();

        if (!expression.doesReturn()) {
            if (isVoid) {
                //we dont care about the yield type, if the method is void
                var aReturn = new KExpr.Return(expression.region(), null, KType.NONE);
                return new KExpr.Block(
                        expression.region(),
                        List.of(expression, aReturn),
                        KType.NONE,
                        true
                );
            } else {
                expression = contextNew.makeAssignment(expression.region(), returnType, expression);
                expression = new KExpr.Return(expression.region(), expression, expression.type());
            }
        }
        return expression;
    }

    //TODO what if a abstract methods defines generics types with bounds???
    private static List<MethodToImplement> getMethodsToImplementForClass(Context c, Model model, KType.ClassType classType, boolean first, boolean removeImplementedOnFirst) {

        var currentClassModel = model.getClass(classType.pointer());
        if (!Modifier.isAbstract(currentClassModel.modifiers()) && !first) {
            return List.of();
        }

        var mapped = new HashMap<Generic, KType>();
        if (currentClassModel.generics().size() != classType.generics().size()) {
            Log.temp(c, currentClassModel.region(), "Class generic count mismatch");
            throw new Log.KarinaException();
        }
        //this maps the class fieldType generics
        for (var i = 0; i < currentClassModel.generics().size(); i++) {
            var generic = currentClassModel.generics().get(i);
            var type = classType.generics().get(i);
            mapped.put(generic, type);
        }


        var currentAbstractMethods = new ArrayList<MethodToImplement>();
        var currentDefined = new ArrayList<MethodToImplement>();

        for (var method : currentClassModel.methods()) {
            if (Modifier.isStatic(method.modifiers()) || !Modifier.isPublic(method.modifiers())) {
                continue;
            }

            var pointer = method.pointer();
            var returnType = Types.projectGenerics(method.signature().returnType(), mapped);
            var argumentTypes = new KType[method.signature().parameters().size()];

            for (var i = 0; i < method.signature().parameters().size(); i++) {
                var parameter = method.signature().parameters().get(i);
                var type = Types.projectGenerics(parameter, mapped);
                argumentTypes[i] = type;
            }

            var methodToImplement = new MethodToImplement(method.name(), pointer, returnType, argumentTypes, null);

            if (Modifier.isAbstract(method.modifiers())) {
                currentAbstractMethods.add(methodToImplement);
            }
            currentDefined.add(methodToImplement);
        }

        var superMethods = new ArrayList<MethodToImplement>();
        var superClass = Types.getSuperType(c, model, classType);
        if (superClass != null) {
            superMethods.addAll(getMethodsToImplementForClass(c, model, superClass, false, removeImplementedOnFirst));
        }

        var interfaces = Types.getInterfaces(c, model, classType);
        for (var anInterface : interfaces) {
            superMethods.addAll(getMethodsToImplementForClass(c, model, anInterface, false, removeImplementedOnFirst));
        }

        //only add methods, that are not implemented in the current class
        for (var method : superMethods) {
            var implementedMethod = isImplemented(method, currentDefined);
            if (implementedMethod != null) {
                if (first && !removeImplementedOnFirst) {
                    currentAbstractMethods.add(method.project(mapped, implementedMethod));
                }
            } else {
                currentAbstractMethods.add(method.project(mapped, null));
            }

        }


        return currentAbstractMethods;
    }

    /**
     * @param method the method to check
     * @param currentMethods the list of methods to check against
     * @return a MethodToImplement when a the given method is implemented (so is in the currentMethods list)
     */
    private static @Nullable MethodToImplement isImplemented(MethodToImplement method, List<MethodToImplement> currentMethods) {
        outer: for (var currentMethod : currentMethods) {
            if (!method.name.equals(currentMethod.name)) {
                continue;
            }
            var sameReturnType = Types.erasedEquals(method.returnType, currentMethod.returnType);
            if (!sameReturnType) {
                continue;
            }
            if (method.argumentTypes.length != currentMethod.argumentTypes.length) {
                continue;
            }
            for (var i = 0; i < method.argumentTypes.length; i++) {
                var argumentType = method.argumentTypes[i];
                var currentArgumentType = currentMethod.argumentTypes[i];
                if (!Types.erasedEquals(argumentType, currentArgumentType)) {
                    continue outer;
                }
            }
            return currentMethod;
        }

        return null;
    }


    public record MethodToImplement(String name, MethodPointer originalMethodPointer, KType returnType, KType[] argumentTypes, @Nullable MethodToImplement implementing) {

        private MethodToImplement project(Map<Generic, KType> mapping, MethodToImplement implementing) {
            var retProj = Types.projectGenerics(this.returnType, mapping);
            var argumentTypes = new KType[this.argumentTypes.length];
            for (var i = 0; i < this.argumentTypes.length; i++) {
                var argumentType = this.argumentTypes[i];
                var type = Types.projectGenerics(argumentType, mapping);
                argumentTypes[i] = type;
            }

            return new MethodToImplement(this.name, this.originalMethodPointer, retProj, argumentTypes, implementing);
        }

        @Override
        public String toString() {
            return "MethodToImplement{" + "name='" + this.name + '\'' + ", originalMethodPointer=" +
                    this.originalMethodPointer + ", returnType=" + this.returnType + ", argumentTypes=" +
                    Arrays.toString(this.argumentTypes) + '}';
        }

        public String toReadableString() {
            var string = String.join(
                    ", ",
                    Arrays.stream(this.argumentTypes)
                            .map(KType::toString)
                            .toList()
            );
            return this.name + "(" + string + ") -> " + this.returnType;
        }
    }



}
