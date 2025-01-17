package org.karina.lang.formatter;

import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.utils.TypeImport;

public class UnitFormatter {
    public FFile file = new FFile();

    public void write(KTree.KUnit unit) {

        for (var kImport : unit.kImports()) {
            this.file.append("import ");
            switch (kImport.importType()) {
                case TypeImport.All all -> {
                    this.file.append("*");
                    this.file.append(" ");
                }
                case TypeImport.JavaAlias javaAlias -> {
                    throw new NullPointerException("");
                }
                case TypeImport.JavaClass javaClass -> {
                    throw new NullPointerException("");
                }
                case TypeImport.Single single -> {
                    this.file.append(single.name().value());
                    this.file.append(" ");
                }
            }
            this.file.append(kImport.path().value().mkString("."));

            this.file.line();
        }
        this.file.line();

    }

}
