package org.karina.lang.compiler.errors.types;

import org.karina.lang.compiler.*;
import org.karina.lang.compiler.objects.KTree;

import java.util.Set;

public sealed interface ImportError extends Error {

    record NoUnitFound(SpanOf<ObjectPath> path, KTree.KPackage root) implements ImportError {}

    record UnknownImportType(Span region, String name, Set<String> available, KTree.KPackage root)
            implements ImportError {}

    record GenericCountMismatch(Span region, String name, int expected, int found)
            implements ImportError {}

    record DuplicateItem(Span first, Span second, String item) implements ImportError {}

    record NoItemFound(Span region, String item, KTree.KUnit unit, KTree.KPackage root) implements ImportError {}

    record JavaNotSupported(Span region) implements ImportError {}


}
