package org.karina.lang.compiler.stages.imports;

import org.karina.lang.compiler.utils.Context;
import org.karina.lang.compiler.utils.IntoContext;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.utils.Region;

/**
 * Only used as a wrapper for the ImportTable, no need for this calls in the future
 */
public record ImportContext(Context c, ImportTable table) implements IntoContext {

    public KType resolveType(Region region, KType type) {
        return resolveType(region, type, ImportTable.ImportGenericBehavior.DEFAULT);
    }

    public KType resolveType(Region region, KType type, ImportTable.ImportGenericBehavior flags) {
        return this.table.importType(region, type, flags);
    }

    public ImportContext withNewContext(Context c) {
        return new ImportContext(c, this.table);
    }

    @Override
    public Context intoContext() {
        return this.c;
    }
}
