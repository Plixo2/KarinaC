package org.karina.lang.compiler.jvm.model;

import org.karina.lang.compiler.jvm.model.table.ClassLookup;
import org.karina.lang.compiler.jvm.model.table.LinearLookup;
import org.karina.lang.compiler.jvm.model.table.PrefixTreeLookup;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.ImportError;
import org.karina.lang.compiler.model_api.ClassModel;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Region;

//TODO replace, to slow
/**
 * A Thread Safe builder for a Model
 */
public class ModelBuilder {

    final ClassLookup tree = new LinearLookup();

    public void addClass(ClassModel classModel) {
        synchronized (this.tree) {
            var prev = this.tree.insert(classModel.path(), classModel);
            if (prev != null) {
                testDuplicate(prev, classModel.region(), classModel.path());
                throw new Log.KarinaException();
            }
        }
    }


    public Model build() {
        return new JKModel(this.tree.lock());
    }

    public static Model merge(Model... models) {
        var modelBuilder = new ModelBuilder();

        for (var other : models) {
            for (var allUserClass : other.getUserClasses()) {
                modelBuilder.addClass(allUserClass);
            }
            for (var binaryClass : other.getBinaryClasses()) {
                modelBuilder.addClass(binaryClass);
            }
        }
        return modelBuilder.build();
    }

    private static void testDuplicate(ClassModel prev, Region classRegion, ObjectPath inserted) {
        if (prev != null) {
            Log.importError(new ImportError.DuplicateItem(
                    prev.region(),
                    classRegion,
                    inserted.toString()
            ));
            throw new Log.KarinaException();
        }
    }
}
