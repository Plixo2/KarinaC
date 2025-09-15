package org.karina.lang.compiler.model_api.impl;

import org.karina.lang.compiler.model_api.impl.table.ClassLookup;
import org.karina.lang.compiler.model_api.impl.table.LinearLookup;
import org.karina.lang.compiler.utils.logging.Log;
import org.karina.lang.compiler.utils.logging.errors.ImportError;
import org.karina.lang.compiler.model_api.ClassModel;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.utils.Context;
import org.karina.lang.compiler.utils.IntoContext;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Region;

/**
 * A Thread Safe builder for a Model
 */
public class ModelBuilder {

    private final ClassLookup tree = new LinearLookup();


    public synchronized void addClass(IntoContext c, ClassModel classModel) {
        var prev = this.tree.insert(classModel.path(), classModel);
        if (prev != null) {
            testDuplicate(c.intoContext(), prev, classModel.region(), classModel.path());
            throw new Log.KarinaException();
        }
    }


    public Model build(IntoContext c) {
        return new JKModel(c.intoContext(), this.tree.lock());
    }

    public static Model merge(IntoContext c, Model... models) {
        var modelBuilder = new ModelBuilder();

        for (var other : models) {
            for (var allUserClass : other.getUserClasses()) {
                modelBuilder.addClass(c, allUserClass);
            }
            for (var binaryClass : other.getBinaryClasses()) {
                modelBuilder.addClass(c, binaryClass);
            }
        }
        return modelBuilder.build(c);
    }

    private static void testDuplicate(Context c, ClassModel prev, Region classRegion, ObjectPath inserted) {
        if (prev != null) {
            Log.error(c, new ImportError.DuplicateItem(
                    prev.region(),
                    classRegion,
                    inserted.toString()
            ));
            throw new Log.KarinaException();
        }
    }
}
