package org.karina.lang.compiler.stages.imports;

import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Region;

/**
 * Only used as a wrapper for the ImportTable, no need for this calls in the future
 */
public record ImportContext(ImportTable table) {

    public KType resolveType(Region region, KType type) {
        return resolveType(region, type, ImportTable.ImportGenericBehavior.DEFAULT);
    }

    public KType resolveType(Region region, KType type, ImportTable.ImportGenericBehavior flags) {
        return this.table.importType(region, type, flags);
    }

    public ClassPointer getClassPointer(Region region, ObjectPath path) {
        return this.table.getClassPointer(region, path);
    }


}
