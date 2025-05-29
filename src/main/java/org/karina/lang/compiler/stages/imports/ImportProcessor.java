package org.karina.lang.compiler.stages.imports;

import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.utils.*;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.model_api.impl.ModelBuilder;

/**
 * This class is responsible for resolving types with some basic class validation.
 */
public class ImportProcessor {

    public Model importTree(Context c, Model model) throws Log.KarinaException {
        var build = new ModelBuilder();
        Log.begin("import");

        var prelude = Prelude.fromModel(c, model);

        if (Log.LogTypes.IMPORT_PRELUDE.isVisible()) {
            prelude.log();
        }

        try(var importFork = c.fork()) {
            for (var kClassModel : model.getUserClasses()) {
                if (!kClassModel.isTopLevel()) {
                    continue;
                }
                var importContext = new ImportTable(c, model);
                importFork.collect(subC -> {
                    var withPrelude = ImportHelper.importPrelude(kClassModel, importContext, prelude);
                    ImportItem.importClass(subC, kClassModel, null, withPrelude, build);
                    //return null, and mutate thread-safe ModelBuilder
                    return null;
                });
            }
            var _ = importFork.dispatchParallel();
        }

        for (var bytecodeClass : model.getBinaryClasses()) {
            build.addClass(c, bytecodeClass);
        }

        Log.end("import");

        var newModel = build.build(c);

        PostImportValidator.validateTree(c, newModel);

        return newModel;
    }




}
