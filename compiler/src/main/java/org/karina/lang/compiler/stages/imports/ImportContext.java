package org.karina.lang.compiler.stages.imports;

import org.karina.lang.compiler.stages.imports.table.ExtendableImportTable;
import org.karina.lang.compiler.utils.logging.Log;
import org.karina.lang.compiler.stages.imports.table.ImportTable;
import org.karina.lang.compiler.utils.*;

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


    public KExpr importStaticPath(KExpr.StaticPath staticPath) {
        if (!(this.table instanceof ExtendableImportTable importTable)) {
            return staticPath;
        }

        var region = staticPath.region();
        var individualRegions = staticPath.individualRegions();
        var path = staticPath.path();
        var pointer = importTable.getClassPointerNullable(region, path);
        if (pointer == null) {
            if (path.size() <= 1) {
                importTable.logUnknownPointerError(region, path);
                throw new Log.KarinaException();
            }
            var potentialFunctionName = path.last();
            var potentialClassName = path.everythingButLast();
            var prevRegions = individualRegions.subList(0, individualRegions.size() - 1);
            var classRegion = prevRegions.stream().reduce(Region::merge).orElse(region);

            var potentialClass = importTable.getClassPointer(classRegion, potentialClassName);


            return new KExpr.GetMember(
                    classRegion,
                    new KExpr.StaticPath(classRegion, prevRegions, potentialClassName, potentialClass),
                    RegionOf.region(individualRegions.getLast(), potentialFunctionName),
                    true,
                    null
            );

        } else {
            return new KExpr.StaticPath(region, individualRegions, path, pointer);
        }
    }
}
