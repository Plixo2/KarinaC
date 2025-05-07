package org.karina.lang.compiler.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.model_api.ClassModel;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Types {

    public static boolean signatureEquals(List<KType> a, List<KType> b) {
        if (a.size() != b.size()) {
            return false;
        }
        for (int i = 0; i < a.size(); i++) {
            if (!Types.erasedEquals(a.get(i), b.get(i))) {
                return false;
            }
        }


        return true;
    }

    public static KType erase(KType type) {
        return switch (type) {
            case KType.ArrayType arrayType -> new KType.ArrayType(erase(arrayType.elementType()));
            case KType.ClassType classType -> {
                List<KType> generics = new ArrayList<>();
                for (var ignored : classType.generics()) {
                    generics.add(KType.ROOT);
                }
                yield new KType.ClassType(classType.pointer(), generics);
            }
            case KType.FunctionType functionType -> {

                if (!functionType.interfaces().isEmpty()) {
                    yield functionType.interfaces().getFirst();
                } else {
//                    List<KType> arguments = new ArrayList<>();
//                    for (var ignored : functionType.arguments()) {
//                        arguments.add(KType.ROOT);
//                    }
//                    KType returnType = KType.ROOT;
//                    yield new KType.FunctionType(arguments, returnType, java.util.List.of());
                    yield KType.ROOT;
                }
            }
            case KType.GenericLink genericLink -> {
                yield eraseGeneric(genericLink.link());
            }
            case KType.PrimitiveType primitiveType -> primitiveType;
            case KType.Resolvable resolvable -> {
                if (resolvable.get() != null) {
                    yield erase(resolvable.get());
                } else {
                    yield KType.ROOT;
                }
            }
            case KType.UnprocessedType unprocessedType -> {
                yield KType.ROOT;
            }
            case KType.VoidType _ -> KType.NONE;
        };
    }

    public static KType eraseGeneric(Generic generic) {
        if (generic.superType() != null) {
            return generic.superType();
        } else if (!generic.bounds().isEmpty()) {
            return generic.bounds().getFirst();
        } else {
            return KType.ROOT;
        }
    }

    public static boolean erasedEquals(KType a, KType b) {
        if (a == b) {
            return true;
        }
//        a = a.unpack();
//        b = b.unpack();
        //check for class, so cast is valid
        if (a.getClass() != b.getClass()) {
            return false;
        }
        return switch (a) {
            case KType.GenericLink genericLink -> {
                yield genericLink.link().equals(((KType.GenericLink) b).link());
            }
            case KType.Resolvable resolvable -> false;
            case KType.ArrayType arrayType -> {
                yield erasedEquals(arrayType.elementType(), ((KType.ArrayType) b).elementType());
            }
            case KType.ClassType classType -> {
                var other = (KType.ClassType) b;
                //dont have to check for generic size
                yield classType.pointer().equals(other.pointer());
            }
            case KType.FunctionType functionType -> {
                var other = (KType.FunctionType) b;
                yield functionType.arguments().size() == other.arguments().size();
            }
            case KType.PrimitiveType primitiveType -> {
                yield  primitiveType.primitive() == ((KType.PrimitiveType) b).primitive();
            }
            case KType.UnprocessedType unprocessedType -> {
                Log.temp(unprocessedType.region(), "Unprocessed type " + unprocessedType + " should not exist");
                throw new Log.KarinaException();
            }
            case KType.VoidType _ -> true;
        };

    }



    static void putDependencies(int level, KType type, List<TypeDependency> dependencies) {
        switch (type) {
            case KType.ArrayType arrayType -> {
                dependencies.add(new TypeDependency(arrayType, level));
                putDependencies(level + 1, arrayType.elementType(), dependencies);
            }
            case KType.ClassType classType -> {
                dependencies.add(new TypeDependency(classType, level));
                for (var generic : classType.generics()) {
                    putDependencies(level + 1, generic, dependencies);
                }
            }
            case KType.FunctionType functionType -> {
                dependencies.add(new TypeDependency(functionType, level));
                for (var argument : functionType.arguments()) {
                    putDependencies(level + 1, argument, dependencies);
                }
                putDependencies(level + 1, functionType.returnType(), dependencies);
                for (var impl : functionType.interfaces()) {
                    putDependencies(level + 1, impl, dependencies);
                }
            }
            case KType.GenericLink genericLink -> {
                dependencies.add(new TypeDependency(genericLink, level));
            }
            case KType.PrimitiveType primitiveType -> {
                dependencies.add(new TypeDependency(primitiveType, level));
            }
            case KType.Resolvable resolvable -> {
                dependencies.add(new TypeDependency(resolvable, level));
                if (resolvable.get() != null) {
                    putDependencies(level + 1, resolvable.get(), dependencies);
                }
            }
            case KType.VoidType voidType -> {
                dependencies.add(new TypeDependency(voidType, level));
            }
            case KType.UnprocessedType unprocessedType -> {
                Log.temp(unprocessedType.region(), "Unprocessed type " + unprocessedType + " should not exist");
                throw new Log.KarinaException();
            }
        }
    }

    public static KType erasedClass(ClassModel classModel) {
        var generics = classModel.generics().stream().map(Types::eraseGeneric).toList();
        return new KType.ClassType(classModel.pointer(), generics);
    }

    record TypeDependency(KType type, int level) { }

    static int getTypeDependencyIndex(KType type, List<TypeDependency> dependencies) {
        for (var i = 0; i < dependencies.size(); i++) {
            if (dependencies.get(i).type() == type) {
                return i;
            }
        }
        return -1;
    }


    /**
     * <p>
     * Used to map generics to their actual types
     * </p>
     * See {@link Types#projectGenerics(Model, KType.ClassType, KType.ClassType)}
     * to construct generics from a ClassType, where the generics are not linked to the correct mapping.
     * (only used for interfaces and super classes)
     */
    public static KType projectGenerics(KType original, Map<Generic, KType> generics) {
        var sample = Log.addSuperSample("GENERIC_TYPE_PROJECTION");
        var replaced = switch (original) {
            case KType.ArrayType(var element) -> {
                var newElement = projectGenerics(element, generics);
                yield new KType.ArrayType(newElement);
            }
            case KType.ClassType(var path, var classGenerics) -> {
                var newGenerics = new ArrayList<KType>();
                for (var generic : classGenerics) {
                    var newType = projectGenerics(generic, generics);
                    newGenerics.add(newType);
                }
                yield new KType.ClassType(path, newGenerics);
            }
            case KType.FunctionType functionType -> {
                var returnType = projectGenerics(functionType.returnType(), generics) ;
                var newParameters = new ArrayList<KType>();
                for (var parameter : functionType.arguments()) {
                    var newType = projectGenerics(parameter, generics);
                    newParameters.add(newType);
                }
                var newInternalGenerics = new ArrayList<KType>();
                for (var kInterface : functionType.interfaces()) {
                    var newType = projectGenerics(kInterface, generics);
                    newInternalGenerics.add(newType);
                }

                yield new KType.FunctionType(
                        newParameters,
                        returnType,
                        newInternalGenerics
                );
            }
            case KType.GenericLink genericLink -> {
                var link = genericLink.link();
                var newType = generics.get(link);
                if (newType != null) {
                    if (newType == genericLink) {
                        yield newType;
                    }
                    yield projectGenerics(newType, generics);
                } else {
                    yield genericLink;
                }
            }
            case KType.PrimitiveType primitiveType -> {
                yield primitiveType;
            }

            case KType.Resolvable resolvable -> {
                var resolved = resolvable.get();
                if (resolved == null) {
                    yield resolvable;
                } else {
                    yield projectGenerics(resolved, generics);
                }
            }
            case KType.UnprocessedType unprocessedType -> {
                Log.temp(unprocessedType.region(), "Unprocessed type " + unprocessedType + " should not exist");
                throw new Log.KarinaException();
            }
            case KType.VoidType _ -> KType.NONE;
        };
        sample.endSample();
        return replaced;
    }

    /**
     * Returns the correctly mapped superClass of cls
     */
    public static @Nullable KType.ClassType getSuperType(Model model, KType.ClassType cls) {
        var rightModel = model.getClass(cls.pointer());
        var superClass = rightModel.superClass();

        if (superClass == null) {
            return null;
        }

        return projectGenerics(model, cls, superClass);
    }

    /**
     * Returns the correctly mapped direct interfaces of cls (non recursive)
     */
    public static List<KType.ClassType> getInterfaces(Model model, KType.ClassType cls) {
        var rightModel = model.getClass(cls.pointer());
        var list = new ArrayList<KType.ClassType>();

        for (var interfaceOfRight : rightModel.interfaces()) {
            list.add(projectGenerics(model, cls, interfaceOfRight));
        }

        return list;
    }

    /**
     * This is used to get generic mapped super classes and interfaces, since they are not encoded in the type system directly
     * Replaces generics of the 'classToMap' with the generics provided by the 'owningClass' class,
     * mapped with the information from the classModel
     */
    public static @NotNull KType.ClassType projectGenerics(
            Model model,
            KType.ClassType owningClass,
            KType.ClassType classToMap
    ) {
        var sample = Log.addSuperSample("GENERIC_MODEL_PROJECTION");
        var genericMap = new HashMap<Generic, KType>();

        var testingModelToGetIndexFrom = model.getClass(owningClass.pointer());
        for (var generic : classToMap.generics()) {
            if (!(generic instanceof KType.GenericLink(Generic link))) {
                continue;
            }

            var index = testingModelToGetIndexFrom.generics().indexOf(link);
            if (index == -1) {
                var message = "Generic " + link.name() + " not found"
                        + " of " + classToMap
                        + " in " + testingModelToGetIndexFrom.pointer()
                        + " generics: " + testingModelToGetIndexFrom.generics()
                        ;
                Log.temp(testingModelToGetIndexFrom.region(), message);
                throw new Log.KarinaException();
            }

            var mapped = owningClass.generics().get(index);
            genericMap.put(link, mapped);
            Log.recordType(Log.LogTypes.CHECK_TYPE, "Mapped " + link + " generic to " + mapped);


        }
        //Cast is allowed since Types.projectGenerics returns the a ClassType when provided with a ClassType
        var classType = (KType.ClassType) Types.projectGenerics(classToMap, genericMap);
        sample.endSample();
        return classType;
    }

    public static boolean isSuperTypeOrInterface(Model model, ClassPointer element, ClassPointer toTest) {

        if (element.equals(toTest)) {
            return true;
        }

        var next = model.getClass(element).superClass();
        if (next != null) {
            return isSuperTypeOrInterface(model, next.pointer(), toTest);
        }

        var interfaces = model.getClass(element).interfaces();
        for (var interfaceOf : interfaces) {
            if (isSuperTypeOrInterface(model, interfaceOf.pointer(), toTest)) {
                return true;
            }
        }
        return false;

    }



}
