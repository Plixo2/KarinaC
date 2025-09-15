package org.karina.lang.compiler.jvm_loading.signature;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model_api.impl.jvm.JClassModel;
import org.karina.lang.compiler.jvm_loading.signature.model.TypeArgument;
import org.karina.lang.compiler.jvm_loading.signature.model.TypeParameter;
import org.karina.lang.compiler.jvm_loading.signature.model.TypeSignature;
import org.karina.lang.compiler.utils.logging.Log;
import org.karina.lang.compiler.utils.Context;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.model_api.Generic;
import org.karina.lang.compiler.utils.Region;

import java.util.ArrayList;
import java.util.List;

public class SignatureHelper {

    public static KType toType(
            Context c,
            Region region,
            String className,
            @Nullable JClassModel outer,
            ImmutableList<Generic> generics,
            TypeArgument argument
    ) {

        return switch (argument) {
            case TypeArgument.Any() -> KType.ROOT;
            case TypeArgument.Base(var sub) -> toType(c, region, className, outer, generics, sub);
            case TypeArgument.Extends(var sub) -> toType(c, region, className, outer, generics, sub);
            case TypeArgument.Super(var sub) -> toType(c, region, className, outer, generics, sub);
        };

    }

    public static KType.ClassType toClassType(
            Context c,
            Region region,
            String symbolName,
            @Nullable JClassModel outer,
            ImmutableList<Generic> generics,
            TypeSignature.ClassTypeSignature signature
    ) {

        var genericsArgs = new ArrayList<KType>();
        for (var typeArgument : signature.typeArguments()) {
            var generic = toType(c, region, symbolName, outer, generics, typeArgument);
            genericsArgs.add(generic);
        }
        return signature.classPointer().implement(genericsArgs);
    }

    public static KType toType(
            Context c,
            Region region,
            String symbolName,
            @Nullable JClassModel outer,
            ImmutableList<Generic> generics,
            TypeSignature signature
    ) {
        switch (signature) {
            case TypeSignature.ArraySignature arraySignature -> {
                return new KType.ArrayType(toType(c, region, symbolName, outer, generics, arraySignature.componentType()));
            }
            case TypeSignature.BaseSignature baseSignature -> {
                return new KType.PrimitiveType(baseSignature.primitive());
            }
            case TypeSignature.ClassTypeSignature classTypeSignature -> {
                return toClassType(c, region, symbolName, outer, generics, classTypeSignature);
            }
            case TypeSignature.TypeVarSignature typeVarSignature -> {
                var name = typeVarSignature.name();
                for (var generic : generics) {
                    if (generic.name().equals(name)) {
                        return new KType.GenericLink(generic);
                    }
                }
                var outerIter = outer;
                while (outerIter != null) {
                    for (var generic : outerIter.generics()) {
                        if (generic.name().equals(name)) {
                            return new KType.GenericLink(generic);
                        }
                    }
                    outerIter = outerIter.outerClass();
                }


                var names = getAll(outer, generics).stream().map(Generic::name).toList();
                var nested = nestNamesA(outer);
                var outerName = outer == null ? "null" : outer.name();
                Log.bytecode(c,
                        region, symbolName,
                        "Type variable not found: " + name + "\n for \n" + names + "\n for \n" + nested +
                                " of outer class " + outerName
                );
                throw new Log.KarinaException();
//                return KType.ROOT;
            }
        }
    }

    public static List<String> nestNamesA(JClassModel outer) {
        var names = new ArrayList<String>();
        while (outer != null) {
            names.add(outer.resource().resource().identifier());
            outer = outer.outerClass();
        }
        return names;
    }

    public static List<Generic> getAll(@Nullable JClassModel outer,  ImmutableList<Generic> generics) {
        var genericsList = new ArrayList<>(generics);

        var outerIter = outer;
        while (outerIter != null) {
            genericsList.addAll(outerIter.generics());
            outerIter = outerIter.outerClass();
        }


        return genericsList;
    }

    public static ImmutableList<Generic> mapGenerics(
            Context c,
            Region region,
            String symbolName,
            @Nullable JClassModel outer,
            List<TypeParameter> varTypes
    ) {
        var generics = ImmutableList.<Generic>builder();
        for (var generic : varTypes) {
            generics.add(new Generic(region, generic.name()));
        }
        var genericsList = generics.build();
        for (var i = 0; i < varTypes.size(); i++) {
            var generic = varTypes.get(i);

            KType superClass = null;
            var genSuperClass = generic.superClass();
            if (genSuperClass != null) {
                superClass = SignatureHelper.toType(c, region, symbolName, outer, genericsList, genSuperClass);
            }

            var bounds = new ArrayList<KType>();
            for (var bound : generic.interfaces()) {
                bounds.add(SignatureHelper.toType(c, region, symbolName, outer, genericsList, bound));
            }
            genericsList.get(i).updateBounds(superClass, bounds);
        }
        return genericsList;
    }

}
