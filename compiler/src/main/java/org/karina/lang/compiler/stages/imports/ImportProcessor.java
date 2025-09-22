package org.karina.lang.compiler.stages.imports;

import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.stages.imports.table.SecondPassImportTable;
import org.karina.lang.compiler.stages.imports.table.FirstPassImportTable;
import org.karina.lang.compiler.utils.*;
import org.karina.lang.compiler.utils.logging.Log;
import org.karina.lang.compiler.model_api.impl.ModelBuilder;


/**
 * This class is responsible for resolving types with some basic class validation.
 * First import all types, then apply functional interfaces to function types.
 * This has to be done in two steps, since we need a imported model to apply functional interfaces.
 * The tree is also validated after the import.
 */
public class ImportProcessor {

    public Model importTree(Context c, Model model) throws Log.KarinaException {

        var prelude = Prelude.fromModel(c, model);

        if (Log.LogTypes.IMPORT_PRELUDE.isVisible()) {
            prelude.log();
        }

        var build = new ModelBuilder();
        try(var importFork = c.fork()) {
            for (var kClassModel : model.getUserClasses()) {
                if (!kClassModel.isTopLevel()) {
                    continue;
                }
                importFork.collect(subC -> {
                    var withPrelude = ImportHelper.importPrelude(kClassModel, new FirstPassImportTable(c, model), prelude);
                    ImportItem.importClass(subC, kClassModel, null, withPrelude.withNewContext(subC), build);
                    //return null and mutate thread-safe ModelBuilder
                    return null;
                });
            }
            var _ = importFork.dispatchParallel();
        }

        for (var bytecodeClass : model.getBinaryClasses()) {
            build.addClass(c, bytecodeClass);
        }

        var newModel = build.build(c);
        newModel = applyFunctionalInterfaces(c, newModel);
        PostImportValidator.validateTree(c, newModel);


        return newModel;
    }

    /**
     * Also validates generic bounds
     * Applies and validates functional interfaces to function types in the model.
     */
    private static Model applyFunctionalInterfaces(Context c, Model model) {
        Log.begin("functional interfaces");
        var build = new ModelBuilder();
        try(var importFork = c.fork()) {
            for (var kClassModel : model.getUserClasses()) {
                if (!kClassModel.isTopLevel()) {
                    continue;
                }
                var importContext = new SecondPassImportTable(c, model);
                importFork.collect(subC -> {
                    // set importTable parameter to null to signal the import stage to only apply and validate functional interfaces
                    ImportItem.importClass(subC, kClassModel, null, importContext.withNewContext(subC), build);
                    //return null and mutate thread-safe ModelBuilder
                    return null;
                });
            }
            var _ = importFork.dispatchParallel();
        }

        for (var bytecodeClass : model.getBinaryClasses()) {
            build.addClass(c, bytecodeClass);
        }
        var newModel = build.build(c);
        Log.end("functional interfaces");
        return newModel;
    }




}
