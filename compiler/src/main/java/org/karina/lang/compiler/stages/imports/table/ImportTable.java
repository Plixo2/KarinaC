package org.karina.lang.compiler.stages.imports.table;

import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.utils.*;

/**
 * ImportTable for transforming and resolving types.
 * @see SecondPassImportTable
 * @see FirstPassImportTable
 */
public interface ImportTable extends IntoContext
{
    Model model();

    KType importType(Region region, KType kType);

    KType importType(Region region, KType kType, ImportGenericBehavior flags);

    ImportTable withNewContext(Context c);

    /**
     * Used to determine how deal with generics when resolving types.
     * <ul>
     * <li>
     *      <b>DEFAULT:</b>
     *      import as is, generics have to be defined, so we check count of generics.
     * </li>
     * <li>
     *      <b>INSTANCE_CHECK:</b>
     *      The type is not allowed to define generics other than KType.ROOT (java.lang.Object).
     *      Replace potential generics with KType.ROOT, when not defined.
     *      Also check the count of generics when defined.
     *      Only used in instance checks (if .. is Object cast) and match expressions.
     * </li>
     * <li>
     *      <b>OBJECT_CREATION:</b>
     *      Generics can be omitted, they can be inferred from the field types.
     *      Check the count of generics, if defined.
     *      Only used for object creation.
     * </li>
     * </ul>
     * @see FirstPassImportTable#importUnprocessedType
     */
    enum ImportGenericBehavior {
        DEFAULT,
        INSTANCE_CHECK,
        OBJECT_CREATION;
    }
}
