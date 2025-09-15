package org.karina.lang.compiler.stages.imports.table;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Region;

/**
 * ImportTable for transforming and resolving types.
 * @see FunctionInterfaceTable
 * @see UserImportTable
 */
public sealed interface ImportTable permits FunctionInterfaceTable, UserImportTable
{
    Model model();

    KType importType(Region region, KType kType);

    KType importType(Region region, KType kType, ImportGenericBehavior flags);


    /**
     * Used to determine how deal with generics when resolving types.
     * DEFAULT: import as is, generics have to be defined, so we check count of generics.
     * INSTANCE_CHECK: The type is not allowed to define generics other than KType.ROOT (java.lang.Object).
     * Replace potential generics with KType.ROOT, when not defined.
     * Also check the count of generics when defined.
     * Only used in instance checks (if .. is Object cast) and match expressions.
     * OBJECT_CREATION: Generics can be omitted, they can be inferred from the field types.
     * Check the count of generics, if defined.
     * Only used for object creation.
     *
     * @see UserImportTable#importUnprocessedType
     */
    enum ImportGenericBehavior {
        DEFAULT,
        INSTANCE_CHECK,
        OBJECT_CREATION;
    }
}
