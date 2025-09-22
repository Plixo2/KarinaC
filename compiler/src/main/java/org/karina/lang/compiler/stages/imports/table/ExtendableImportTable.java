package org.karina.lang.compiler.stages.imports.table;


import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model_api.Generic;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.model_api.pointer.FieldPointer;
import org.karina.lang.compiler.model_api.pointer.MethodPointer;
import org.karina.lang.compiler.utils.Context;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Region;
import org.karina.lang.compiler.utils.StaticImportTable;

import java.util.List;
public interface ExtendableImportTable extends ImportTable {

    ExtendableImportTable removeGenerics();
    ExtendableImportTable addStaticMethod(Region declarationRegion, String name, ClassPointer classPointer, boolean declaredExplicit, boolean prelude);
    ExtendableImportTable addStaticField(Region declarationRegion, String name, FieldPointer reference, boolean declaredExplicit, boolean prelude);
    ExtendableImportTable addClass(Region declarationRegion, String name, ClassPointer pointer, boolean declaredExplicit, boolean prelude);
    ExtendableImportTable addPreludeMethods(Region declarationRegion, String name, List<MethodPointer> methods);
    ExtendableImportTable addGeneric(Region declarationRegion, Generic generic);

    @Override
    ExtendableImportTable withNewContext(Context c);

    boolean containsClass(String name);
    @Nullable ClassPointer getClassPointerNullable(Region region, ObjectPath path);
    ClassPointer getClassPointer(Region region, ObjectPath path);
    void logUnknownPointerError(Region region, ObjectPath path);

    StaticImportTable intoStaticTable(ClassPointer referenceSite, Model model);
}
