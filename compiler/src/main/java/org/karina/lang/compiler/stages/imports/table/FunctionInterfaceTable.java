package org.karina.lang.compiler.stages.imports.table;

import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.AttribError;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.utils.*;

import java.util.ArrayList;

/**
 * Applies and validates functional interfaces to function types in the model.
 * This is the second importing step.
 * @param model has to be fully imported.
 */
public record FunctionInterfaceTable(Context c, Model model) implements ImportTable, IntoContext {
    @Override
    public KType importType(Region region, KType kType) {
        return importType(region, kType, ImportGenericBehavior.DEFAULT);
    }

    @Override
    public KType importType(Region region, KType kType, ImportGenericBehavior flags) {
        return switch (kType) {
            case KType.ArrayType arrayType -> new KType.ArrayType(importType(region, arrayType.elementType()));
            case KType.FunctionType functionType -> importFunctionType(region, functionType);
            case KType.GenericLink genericLink -> genericLink;
            case KType.PrimitiveType primitiveType -> primitiveType;
            case KType.Resolvable resolvable -> resolvable;
            case KType.ClassType classType -> {
                var generics = classType.generics()
                                        .stream()
                                        .map(ref -> importType(region, ref, flags))
                                        .toList();
                yield  classType.pointer().implement(generics);
            }
            case KType.UnprocessedType unprocessedType -> {
                Log.temp(this, region, "Unprocessed type: " + unprocessedType.name() + " after importing, this should not happen.");
                throw new Log.KarinaException();
            }
            case KType.VoidType _ -> KType.NONE;
        };
    }

    private KType.FunctionType importFunctionType(Region region, KType.FunctionType functionType) {

        var returnType = importType(region, functionType.returnType());
        var arguments = functionType.arguments().stream().map(ref -> importType(region, ref)).toList();

        var interfaces = new ArrayList<KType.ClassType>();
        for (var anInterface : functionType.interfaces()) {
            var imported = importType(region, anInterface);
            if (!(imported instanceof KType.ClassType classType)) {
                Log.error(this, new AttribError.NotAClass(region, imported));
                throw new Log.KarinaException();
            }
            var alreadyAdded = ClosureHelper.isInterfaceAlreadyAdded(classType, interfaces);
            if (alreadyAdded) {
                Log.error(this, new AttribError.DuplicateInterface(region, classType));
                throw new Log.KarinaException();
            }
            interfaces.add(classType);
        }

        var primaryInterface = ClosureHelper.getDefaultInterface(this.intoContext(), region, this.model, arguments, returnType);
        if (primaryInterface != null) {
            var alreadyAddedDefault = ClosureHelper.isInterfaceAlreadyAdded(primaryInterface, interfaces);
            if (!alreadyAddedDefault) {
                interfaces.add(primaryInterface);
            }
        }
        if (interfaces.isEmpty()) {
            Log.temp(this, region,
                    "Cannot find default interface for " + arguments.size() + " arguments and return type " + returnType + ". " +
                            "Please specify an interface via 'impl'"
            );
            throw new Log.KarinaException();
        }

        for (var anInterface : interfaces) {
            if (!ClosureHelper.canUseInterface(region, this.intoContext(), this.model, arguments, returnType, anInterface)) {
                Log.error(this, new AttribError.NotAValidInterface(region, arguments, returnType, anInterface));
                throw new Log.KarinaException();
            }
        }

        Log.recordType(Log.LogTypes.CLOSURE, "Function type with interfaces imported", interfaces);

        return new KType.FunctionType(arguments, returnType, interfaces);
    }

    @Override
    public Context intoContext() {
        return this.c;
    }
}
