package org.karina.lang.lsp.impl.provider;

import com.google.common.collect.ImmutableList;
import karina.lang.Option;
import lombok.RequiredArgsConstructor;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model_api.Generic;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.model_api.impl.ModelBuilder;
import org.karina.lang.compiler.model_api.impl.karina.KClassModel;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.model_api.pointer.FieldPointer;
import org.karina.lang.compiler.stages.imports.ImportHelper;
import org.karina.lang.compiler.stages.imports.ImportItem;
import org.karina.lang.compiler.stages.imports.table.FirstPassImportTable;
import org.karina.lang.compiler.utils.*;
import org.karina.lang.compiler.utils.logging.DiagnosticCollection;
import org.karina.lang.compiler.utils.symbols.*;
import org.karina.lang.lsp.impl.ClassVisitor;
import org.karina.lang.lsp.impl.CodeDiagnosticInformationBuilder;
import org.karina.lang.lsp.lib.provider.DefinitionProvider;
import org.karina.lang.lsp.lib.events.ClientEvent;
import org.karina.lang.lsp.lib.events.EventService;
import org.karina.lang.lsp.test_compiler.ProviderArgs;

import java.math.BigDecimal;
import java.net.URI;
import java.util.*;

@RequiredArgsConstructor
public class DefaultDefinitionProvider implements DefinitionProvider {
    private final EventService eventService;
    private static Option<Prelude> prelude = Option.none();

    @Override
    public @Nullable List<LocationLink> getDefinition(
            ProviderArgs index,
            URI uri,
            Position position,
            boolean typeDefinition
    ) {
        System.out.println("Definition requested in " + uri + "  " + typeDefinition);
        if (!(index.models().languageModel() instanceof Option.Some(var languageModel))) {
            return null;
        }
        var region_position = new Region.Position(position.getLine(), position.getCharacter());

        var pointer = index.getClassPointerOfURI(uri, languageModel);
        if (pointer == null) {
            return null;
        }
        var classModel = languageModel.getClass(pointer);
        if (!(classModel instanceof KClassModel kClassModel)) {
            return null;
        }
        var config = Context.ContextHandling.of(false, true, true);
        config = config.enableMissingMembersSupport();
        var references = new ArrayList<ExprLocation>();

        var errors = new DiagnosticCollection();
        var result = Context.run(
                config, errors, null, null, (c) -> {
                    var prelude = this.prelude.orElseGet(() -> Prelude.fromModel(c, languageModel));
                    this.prelude = Option.some(prelude);
                    var userTable = new FirstPassImportTable(c, languageModel,
                            (unprocessedType, type) -> {
                            var nameRegion = unprocessedType.name().region();
                            if (nameRegion.doesContainPosition(region_position)) {
                                var target = regionOfType(languageModel, type);
                                if (target != null) {
                                    references.add(new ExprLocationSimple(
                                            nameRegion,
                                            target
                                    ));
                                }
                            }
                        }
                    );
                    var withPrelude = ImportHelper.importPrelude(kClassModel, userTable, prelude);
                    ImportItem.importClass(c, kClassModel, null, withPrelude, new ModelBuilder());
                    return "Ok";
                }
        );
        if (index.models().importedModel().isSome()) {
            if (result == null) {
                this.eventService.send(new ClientEvent.Log(
                        "Failed to process imports, despite having an already imported model",
                        MessageType.Error
                ));
                for (var error : errors) {
                    System.err.println(error.mkString(true));
                }
            }
        }

        if (!references.isEmpty()) {
            return makeUnique(intoLinks(
                    region_position,
                    references
            ));
        }

        if (typeDefinition) {
            var typeLinks = getType(
                    index,
                    pointer,
                    region_position
            );
            var filtered = typeLinks.stream().filter(ref -> ref.target != null).toList();
            return makeUnique(intoLinks(region_position, filtered));
        }

        var typedLinks = findLocationOfExpression(
                index,
                pointer,
                region_position
        );
        return makeUnique(typedLinks);
    }

    private static int getSizeOfRegion(Region region) {
        var lineDiff = region.start().line() - region.end().line();
        if (lineDiff == 0) {
            return region.end().column() - region.start().column();
        } else {
            return lineDiff * 10000;
        }
    }

    private static @Nullable Region regionOfType(Model model, KType type) {
        if (type instanceof KType.ClassType(var refPointer, _)) {
            return model.getClass(refPointer).region();
        } else if (type instanceof KType.GenericLink(Generic link)) {
            return link.region();
        } else {
            return null;
        }
    }

    private static Location intoLocation(Region region,  @Nullable Region.Position position) {
        if (position != null && region.doesContainPosition(position)) {
            region = new Region(
                    region.source(),
                    region.start(),
                    region.start()
            );
        }
        return CodeDiagnosticInformationBuilder.regionToLSPLocation(region);
    }


    private static List<LocationLink> findLocationOfExpression(
            ProviderArgs index,
            ClassPointer pointer,
            Region.Position position
    ) {
        if (!(index.models().attributedModel() instanceof Option.Some(var attribModel))) {
            return List.of();
        }
        var attribModelClass = attribModel.getClass(pointer);
        if (!(attribModelClass instanceof KClassModel kAttribClassModel)) {
            return List.of();
        }
        var expressions = new ArrayList<KExpr>();
        var positionToRegion = new Region(
                kAttribClassModel.region().source(),
                position,
                position
        );

        var visitor = new ExpressionVisitor(expressions, positionToRegion, position);
        visitor.visitClass(kAttribClassModel);

        var links = new ArrayList<LocationLink>();
        // lower expressions are added first, so
        // we can just iterate and return the first one that has a location
        for (var expression : expressions) {
            var exprLocations = getLocationOfExpr(attribModel, expression);
            if (exprLocations != null) {
                links.addAll(intoLinks(position, exprLocations));
            }
        }
        return links;
    }

    public static List<ExprLocationType> getType(
            ProviderArgs index,
            ClassPointer pointer,
            Region.Position position
    ) {
        if (!(index.models().attributedModel() instanceof Option.Some(var attribModel))) {
            return List.of();
        }
        var attribModelClass = attribModel.getClass(pointer);
        if (!(attribModelClass instanceof KClassModel kAttribClassModel)) {
            return List.of();
        }
        var expressions = new ArrayList<KExpr>();
        var positionToRegion = new Region(
                kAttribClassModel.region().source(),
                position,
                position
        );

        var visitor = new ExpressionVisitor(expressions, positionToRegion, position);
        visitor.visitClass(kAttribClassModel);


        var locations = new ArrayList<ExprLocationType>();
        for (var kExpr : expressions) {
            if (kExpr instanceof KExpr.Literal(var region, var name, var symbol)) {
                var type = kExpr.type();
                var exprLocation = intoLocation(region, type, attribModel);
                locations.add(exprLocation);
            } else if (kExpr instanceof KExpr.Number(Region region, _, _, _)) {
                var exprLocation = intoLocation(region, kExpr.type(), attribModel);
                locations.add(exprLocation);
            } else if (kExpr instanceof KExpr.VariableDefinition variableDefinition) {
                var type = variableDefinition.value().type();
                if (variableDefinition.varHint() != null) {
                    type = variableDefinition.varHint();
                }
                var exprLocation = intoLocation(variableDefinition.name().region(), type, attribModel);
                locations.add(exprLocation);
            } else if (kExpr instanceof KExpr.UsingVariableDefinition usingVariableDefinition) {
                var type = usingVariableDefinition.value().type();
                if (usingVariableDefinition.varHint() != null) {
                    type = usingVariableDefinition.varHint();
                }
                var exprLocation = intoLocation(usingVariableDefinition.name().region(), type, attribModel);
                locations.add(exprLocation);
            } else if (kExpr instanceof KExpr.GetMember member) {
                var symbol = member.symbol();
                assert symbol != null;
                if (symbol instanceof MemberSymbol.FieldSymbol) {
                    var exprLocation = intoLocation(member.name().region(), member.type(), attribModel);
                    locations.add(exprLocation);
                }
            } else if (kExpr instanceof KExpr.Call call) {
                var symbol = call.symbol();
                assert symbol != null;
                if (symbol instanceof CallSymbol.CallVirtual virtual
                        && virtual.original() != null) {
                    if (virtual.original() instanceof KExpr.GetMember getMember) {
                        if (getMember.name().region().doesContainPosition(position)) {
                            var exprLocations = intoLocation(call.region(), symbol.returnType(), attribModel);
                            locations.add(exprLocations);
                        }
                    }
                } else if (symbol instanceof CallSymbol.CallStatic) {
                    var exprLocations = intoLocation(call.left().region(), symbol.returnType(), attribModel);
                    locations.add(exprLocations);
                }
            }
        }
        return locations;
    }

    private static List<LocationLink> intoLinks(
            Region.Position position,
            List<? extends ExprLocation> exprLocations
    ) {
        var links = new ArrayList<LocationLink>();
        for (var location : exprLocations) {
            if (location.source().start().equals(location.source().end())) {
                continue;
            }
            if (!location.source().doesContainPosition(position)) {
                continue;
            }

            var source = CodeDiagnosticInformationBuilder.regionToLSPRange(location.source());
            var targetURI = location.target().source().resource().identifier();
            var targetRegion = location.target();

            if (targetRegion.doesContainPosition(position)) {
                targetRegion = new Region(
                        targetRegion.source(),
                        targetRegion.start(),
                        targetRegion.start()
                );
            }

            var targetRange = CodeDiagnosticInformationBuilder.regionToLSPRange(targetRegion);

            var link = new LocationLink(
                    targetURI,
                    targetRange,
                    targetRange,
                    source
            );
            links.add(link);
        }
        return links;
    }

    private static List<LocationLink> makeUnique(List<LocationLink> links) {
        record Entry(String uri, Range range) {}
        var seen = new HashSet<Entry>();
        var uniq = new ArrayList<LocationLink>();
        for (var link : links) {
            var key = new Entry(link.getTargetUri(), link.getTargetRange());
            if (seen.contains(key)) {
                continue;
            }
            seen.add(key);
            uniq.add(link);
        }
        return uniq;
    }


    private static @Nullable List<ExprLocation> getLocationOfExpr(Model model, KExpr expr) {
        return switch (expr) {
            case KExpr.GetMember(Region region, KExpr left, RegionOf<String> name, boolean isNextACall, @Nullable @Symbol MemberSymbol symbol) -> {
                assert symbol != null;
                yield switch (symbol) {
                    case MemberSymbol.ArrayLength _ -> null;
                    case MemberSymbol.FieldSymbol fieldSymbol ->
                            List.of(intoLocation(name.region(), fieldSymbol.pointer(), model));
                    case MemberSymbol.VirtualFunctionSymbol virtualFunctionSymbol ->
                            intoLocation(name.region(), virtualFunctionSymbol.collection(), model);
                };
            }
            case KExpr.Literal(Region region, String name, @Nullable @Symbol LiteralSymbol symbol) -> {
                assert symbol != null;
                yield switch (symbol) {
                    case LiteralSymbol.Null _ -> null;
                    case LiteralSymbol.StaticClassReference staticClassReference ->
                        List.of(intoLocation(region, staticClassReference.classPointer(), model));
                    case LiteralSymbol.StaticFieldReference staticFieldReference ->
                        List.of(intoLocation(region, staticFieldReference.fieldPointer(), model));
                    case LiteralSymbol.StaticMethodReference staticMethodReference ->
                        intoLocation(region, staticMethodReference.collection(), model);
                    case LiteralSymbol.VariableReference variableReference ->
                        List.of(intoLocation(region, variableReference.variable()));
                };
            }
            case KExpr.StaticPath(Region region, ImmutableList<Region> individualRegions, ObjectPath path, @Nullable ClassPointer importedPointer) -> {
                assert importedPointer != null;
                if (individualRegions.size() == path.size()) {
                    var pathElements = path.asList();
                    var parts = new ArrayList<ExprLocation>();
                    for (var i = 0; i < individualRegions.size(); i++) {
                        var regionUpTo = individualRegions.subList(0, i + 1)
                                        .stream().reduce(Region::merge).orElseThrow();
                        var pathUpTo = pathElements.subList(0, i + 1);
                        var objectPath = new ObjectPath(pathUpTo);
                        var asClassPointer = model.getClassPointer(region, objectPath);
                        if (asClassPointer != null) {
                            parts.add(intoLocation(regionUpTo, asClassPointer, model));
                        }
                    }
                    System.out.println("Parts: " + parts);
                    yield parts;
                } else {
                    System.out.println("Mismatched regions and path sizes");
                    yield List.of(intoLocation(region, importedPointer, model));
                }
            }
            case KExpr.Self(Region region, @Nullable @Symbol Variable symbol) -> {
                assert symbol != null;
                yield List.of(intoLocation(region, symbol));
            }
            default -> null;
        };
    }
    private interface ExprLocation {
        Region source();
        Region target();
    }
    private record ExprLocationSimple(Region source, Region target) implements ExprLocation { }
    public record ExprLocationType(Region source, @Nullable Region target, KType type) implements ExprLocation { }


    private static ExprLocation intoLocation(Region source, FieldPointer pointer, Model model) {
        return new ExprLocationSimple(
                source,
                model.getField(pointer).region()
        );
    }
    private static ExprLocation intoLocation(Region source, ClassPointer pointer, Model model) {
        return new ExprLocationSimple(
                source,
                model.getClass(pointer).region()
        );
    }
    private static ExprLocation intoLocation(Region source, Variable variable) {
        return new ExprLocationSimple(
                source,
                variable.region()
        );
    }
    private static ExprLocationType intoLocation(Region source, KType type, Model model) {
        var region = regionOfType(model, type);
        return new ExprLocationType(
                source,
                region,
                type
        );
    }
    private static List<ExprLocation> intoLocation(Region source, MethodCollection methods, Model model) {
        var locations = new ArrayList<ExprLocation>();
        for (var method : methods) {
            locations.add(new ExprLocationSimple(
                    source,
                    model.getMethod(method).region()
            ));
        }
        return locations;
    }



    private static class ExpressionVisitor extends ClassVisitor {
        private final List<KExpr> expressions;
        private final Region.Position position;

        public ExpressionVisitor(List<KExpr> expressions, Region region, Region.Position position) {
            super(false, true, region, true);
            this.expressions = expressions;
            this.position = position;
        }

        @Override
        public void visitExpr(KExpr expr) {
            assert this.region != null;
            if (expr.region().doesContainPosition(this.position)) {
                super.visitExpr(expr);
                this.expressions.add(expr);
            }
        }
    }
}
