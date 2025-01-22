package org.karina.lang.compiler.errors.types;

import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Region;

import java.util.Set;

public sealed interface ImportError extends Error {

    record NoUnitFound(Region region, ObjectPath path) implements ImportError {}

    record UnknownImportType(Region region, String name, Set<String> available)
            implements ImportError {}

    record GenericCountMismatch(Region region, String name, int expected, int found)
            implements ImportError {}

    record DuplicateItem(Region first, Region second, String item) implements ImportError {}

    record NoItemFound(Region region, String item, ObjectPath cls) implements ImportError {}

    record JavaNotSupported(Region region) implements ImportError {}



}
