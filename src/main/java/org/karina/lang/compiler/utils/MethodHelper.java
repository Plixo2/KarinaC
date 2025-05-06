package org.karina.lang.compiler.utils;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.jvm.model.karina.KClassModel;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.model_api.MethodModel;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.model_api.pointer.MethodPointer;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.objects.Types;

import java.lang.reflect.Modifier;
import java.util.*;

public class MethodHelper {

//    public static void implementForClass(KClassModel classModel, Model model) {
//
//        if (Modifier.isAbstract(classModel.modifiers())) {
//            return;
//        }
//
//        var classType = classModel.getDefaultClassType();
//        var toImplement = getMethodsToImplementForClass(model, classType, true);
//
//
//    }

    //want a list of all objects:
    //MethodPointer pointing to where the abstract method was defined
    //MethodPointer of the implementation

    //create method to return a List of all methods that need to be implemented when a class implements or extends the given class
    //List of MethodPointer + return type + argument types
    //so archive this:
    //find all methods in the class that are marked abstract
    //Call this methods recursive for the superclass and all interfaces
    //Filter the list when a method is implemented in the current class
//
    public static List<MethodToImplement> getMethodsToImplementForClass(Model model, KType.ClassType classType) {
        return getMethodsToImplementForClass(model, classType, true, true);
    }

    public static List<MethodToImplement> getImplementForClass(Model model, KType.ClassType classType) {
        return getMethodsToImplementForClass(model, classType , true, false);
    }

    //TODO what if a abstract methods defines generics types with bounds???
    private static List<MethodToImplement> getMethodsToImplementForClass(Model model, KType.ClassType classType, boolean first, boolean removeImplementedOnFirst) {

        var currentClassModel = model.getClass(classType.pointer());
        if (!Modifier.isAbstract(currentClassModel.modifiers()) && !first) {
            return List.of();
        }

        var mapped = new HashMap<Generic, KType>();
        if (currentClassModel.generics().size() != classType.generics().size()) {
            Log.temp(currentClassModel.region(), "Class generic count mismatch");
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
            if (Modifier.isStatic(method.modifiers())) {
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
        var superClass = Types.getSuperType(model, classType);
        if (superClass != null) {
            superMethods.addAll(getMethodsToImplementForClass(model, superClass, false, removeImplementedOnFirst));
        }

        var interfaces = Types.getInterfaces(model, classType);
        for (var anInterface : interfaces) {
            superMethods.addAll(getMethodsToImplementForClass(model, anInterface, false, removeImplementedOnFirst));
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

    public static @Nullable MethodToImplement isImplemented(MethodToImplement method, List<MethodToImplement> currentMethods) {
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

    public record MethodToImplement(String name, MethodPointer originalMethodPointer, KType returnType, KType[] argumentTypes, MethodToImplement implementing) {

//        public static MethodToImplement fromDefaultMethod(MethodModel methodModel) {
//            var returnType = methodModel.signature().returnType();
//            var argumentTypes = new KType[methodModel.signature().parameters().size()];
//            for (var i = 0; i < methodModel.signature().parameters().size(); i++) {
//                var parameter = methodModel.signature().parameters().get(i);
//                argumentTypes[i] = parameter;
//            }
//            return new MethodToImplement(methodModel.name(), methodModel.pointer(), returnType, argumentTypes);
//        }

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
