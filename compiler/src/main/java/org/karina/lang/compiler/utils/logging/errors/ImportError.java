package org.karina.lang.compiler.utils.logging.errors;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.utils.logging.DidYouMean;
import org.karina.lang.compiler.utils.logging.ErrorInformationBuilder;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Region;
import org.karina.lang.compiler.utils.RegionOf;

import java.util.*;

public sealed interface ImportError extends Error {

    @Override
    default void addInformation(ErrorInformationBuilder builder) {
        switch (this) {
            case ImportError.NoClassFound(var region, var path) -> {
                builder.setTitle("No class found");
                var target = path.mkString("::");
                builder.append("No Class found for path '").append(target).append("'");
                builder.setPrimarySource(region);
            }
            case ImportError.UnknownImportType(var region, var name, var availableNames, var availablePaths) -> {
                builder.setTitle("Unknown type");
                builder.append("Unknown type '").append(name).append("'");
                var pathLookup = new HashMap<String, List<ObjectPath>>();
                var names = new HashSet<String>();
                var simpleNames = new HashSet<>(availableNames);
                for (var objectPath : availablePaths) {
                    if (objectPath.isEmpty()) continue;

                    var simpleName = objectPath.last();
                    pathLookup.computeIfAbsent(simpleName, k -> new ArrayList<>()).add(objectPath);
                    simpleNames.add(objectPath.mkString("::"));
                }
                names.addAll(simpleNames);
                names.addAll(pathLookup.keySet());

                var suggestions = DidYouMean.suggestions(names, name, 10);

                if (!suggestions.isEmpty()) {
                    var bob = builder.append("Did you mean: [");

                    var strings = new ArrayList<String>();
                    for (var suggestion : suggestions) {
                        if (simpleNames.contains(suggestion)) {
                            strings.add("'" + suggestion + "'");
                        }
                        var paths = pathLookup.get(suggestion);
                        if (paths != null) {
                            for (var path : paths) {
                                strings.add("'" + path.mkString("::") + "'");
                            }
                        }
                    }
                    bob.append(String.join(", ", strings));

                    bob.append("]");
                }
                builder.setPrimarySource(region);
            }
            case ImportError.GenericCountMismatch(var region, var name, var expected, var found) -> {
                builder.setTitle("Generic count mismatch");
                builder.append("Generic count mismatch for '").append(name).append("'");
                builder.append("Expected ").append(expected).append(" but found ").append(found);
                builder.setPrimarySource(region);
            }
            case ImportError.DuplicateItem(var first, var second, var item) -> {
                builder.setTitle("Duplicate item");
                builder.append("Duplicate item '").append(item).append("'");
                builder.setPrimarySource(second);
                builder.addSecondarySource(first, "First defined here");
            }
            case ImportError.DuplicateItemWithMessage(var first, var second, var item, var message) -> {
                builder.setTitle("Duplicate item");
                builder.append("Duplicate item '").append(item).append("'");
                builder.append(message);
                builder.setPrimarySource(second);
                builder.addSecondarySource(first, "Defined here");
            }
            case ImportError.NoItemFound(var region, var item, var cls) -> {
                builder.setTitle("No item found");
                builder.append("No item '").append(item).append("' found in class '")
                       .append(cls.mkString(".")).append("'");
                builder.setPrimarySource(region);
            }
            case ImportError.InnerClassImport(Region region, ObjectPath cls) -> {
                builder.setTitle("Inner class import");
                builder.append("Inner class '").append(cls.mkString(".")).append("' cannot be imported directly");
                builder.setPrimarySource(region);
            }
            case ImportError.InvalidAlias(Region region, String givenAlias, String foundClassName) -> {
                builder.setTitle("Invalid alias");
                builder.append("Cannot alias class '").append(foundClassName)
                       .append("' as '").append(givenAlias).append("'");
                builder.append("The alias must contain the class name '").append(foundClassName).append("'");

                builder.setPrimarySource(region);
            }
            case ImportError.UnnecessaryAlias(Region region1, String givenAlias) -> {
                builder.setTitle("Unnecessary alias");
                builder.append("Unnecessary alias '").append(givenAlias).append("'");
                builder.append("Cannot use an alias without conflicts");
                builder.setPrimarySource(region1);
            }

            case ImportError.InvalidName(var region, var name, var msg) -> {
                builder.setTitle("Invalid name");
                builder.append("Invalid name '").append(name).append("'");
                if (msg != null) {
                    builder.append(msg);
                }
                builder.setPrimarySource(region);
            }
            case AccessViolation accessViolation -> {
                builder.setTitle("Access violation");
                builder.append("Access violation for class '").append(accessViolation.className).append("'");
                builder.append("Type: ").append(accessViolation.type.toString());
                if (accessViolation.typeRegion != null) {
                    builder.addSecondarySource(accessViolation.typeRegion.region(), accessViolation.typeRegion.value().path().mkString("::"));
                }
                builder.setPrimarySource(accessViolation.region);
            }
        }
    }
    record AccessViolation(Region region, String className, @Nullable RegionOf<ClassPointer> typeRegion, KType type) implements ImportError {}

    record NoClassFound(Region region, ObjectPath path) implements ImportError {}

    record UnknownImportType(Region region, String name, Set<String> names, Set<ObjectPath> paths) implements ImportError {}

    record GenericCountMismatch(Region region, String name, int expected, int found)
            implements ImportError {}

    record DuplicateItem(Region first, Region second, String item) implements ImportError {}
    record DuplicateItemWithMessage(Region first, Region second, String item, String message) implements ImportError {}

    record NoItemFound(Region region, String item, ObjectPath cls) implements ImportError {}

    record InvalidName(Region region, String name, @Nullable String message) implements ImportError {}

    record InnerClassImport(Region region, ObjectPath cls) implements ImportError {}
    record InvalidAlias(Region region, String givenAlias, String foundClassName) implements ImportError {}
    record UnnecessaryAlias(Region region, String givenAlias) implements ImportError {}


}
