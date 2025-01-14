package org.karina.lang.compiler.api;

import org.karina.lang.compiler.objects.KTree;

public interface Backend<T> {

    T accept(KTree.KPackage kPackage);
}
