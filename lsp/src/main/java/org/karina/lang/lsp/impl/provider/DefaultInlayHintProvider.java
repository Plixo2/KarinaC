package org.karina.lang.lsp.impl.provider;

import karina.lang.Option;
import lombok.RequiredArgsConstructor;
import org.eclipse.lsp4j.*;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.model_api.impl.karina.KClassModel;
import org.karina.lang.compiler.model_api.impl.karina.KMethodModel;
import org.karina.lang.compiler.utils.*;
import org.karina.lang.lsp.impl.ClassVisitor;
import org.karina.lang.lsp.lib.events.EventService;
import org.karina.lang.lsp.lib.provider.InlayHintProvider;
import org.karina.lang.lsp.test_compiler.ProviderArgs;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class DefaultInlayHintProvider implements InlayHintProvider {
    private final EventService eventService;


    @Override
    public List<InlayHint> getHints(
            ProviderArgs index,
            URI uri,
            Range range
    ) {
        System.out.println("-------- INLAY HINTS FOR " + uri);
        if (!(index.models().attributedModel() instanceof Option.Some(var attributedModel))) {
            return List.of();
        }
        var pointer = index.getClassPointerOfURI(uri, attributedModel);
        if (pointer == null) {
            return List.of();
        }
        var classModel = attributedModel.getClass(pointer);
        if (!(classModel instanceof KClassModel kClassModel)) {
            return List.of();
        }
        var hints = new ArrayList<InlayHint>();

        var region = new Region(
                kClassModel.resource(),
                new Region.Position(range.getStart().getLine(), range.getStart().getCharacter()),
                new Region.Position(range.getEnd().getLine(), range.getEnd().getCharacter())
        );

        var visitor = new HintVisitor(hints, attributedModel, region);
        visitor.visitClass(kClassModel);
        return hints;
    }

    private class HintVisitor extends ClassVisitor {
        private final List<InlayHint> hints;
        private final Model model;

        public HintVisitor(List<InlayHint> hints, Model model, Region range) {
            super(false, true, range, true);
            this.hints = hints;
            this.model = model;
        }


        @Override
        public void visitExpr(KExpr expr) {
            if (expr instanceof KExpr.VariableDefinition varDef) {
                if (varDef.varHint() == null) {
                    var symbol = varDef.symbol();
                    if (symbol == null) {
                        DefaultInlayHintProvider.this.eventService.warningMessage(
                                "VariableDefinition without symbol at "
                                        + varDef.region().toString()
                        );
                        return;
                    }
                    var type = symbol.type();
                    addTypeHint(varDef.name().region(), type);
                }
            } else if (expr instanceof KExpr.UsingVariableDefinition usingVarDef) {
                if (usingVarDef.varHint() == null) {
                    var symbol = usingVarDef.symbol();
                    if (symbol == null) {
                        DefaultInlayHintProvider.this.eventService.warningMessage(
                                "UsingVariableDefinition without symbol at "
                                        + usingVarDef.region().toString()
                        );
                        return;
                    }
                    var type = symbol.type();
                    addTypeHint(usingVarDef.name().region(), type);
                }
            } else if (expr instanceof KExpr.For aFor) {
                if (aFor.varPart().type() == null) {
                    var symbol = aFor.varPart().symbol();
                    if (symbol == null) {
                        DefaultInlayHintProvider.this.eventService.warningMessage(
                                "For without symbol at "
                                        + aFor.region().toString()
                        );
                        return;
                    }
                    var type = symbol.type();
                    addTypeHint(aFor.varPart().name().region(), type);
                }
            }
            super.visitExpr(expr);
        }

        private void addTypeHint(Region region, KType type) {
            InlayHint inlayHint = new InlayHint();

            var position = new Position(
                    region.end().line(),
                    region.end().column()
            );

            inlayHint.setPosition(position);

            var prefix = new InlayHintLabelPart(": ");

            var shortString = getShortString(type);
            var label = new InlayHintLabelPart(shortString);
            label.setTooltip(
                    new MarkupContent(
                            MarkupKind.MARKDOWN,
                            "```karina\n" + shortString + "\n```"
                    )
            );

            inlayHint.setLabel(List.of(prefix, label));
            inlayHint.setKind(InlayHintKind.Type);
            inlayHint.setPaddingLeft(false);
            inlayHint.setPaddingRight(false);
            this.hints.add(inlayHint);
        }

        private String getShortString(KType type) {
            switch (type) {
                case KType.ArrayType arrayType -> {
                    return "[" + getShortString(arrayType.elementType()) + "]";
                }
                case KType.ClassType classType -> {
                    var path = this.model.getClass(classType.pointer()).name();
                    String generics;
                    if (classType.generics().isEmpty()) {
                        generics = "";
                    } else {
                        generics = "<" + String.join(", ", classType.generics().stream().map(this::getShortString).toList()) + ">";
                    }

                    return path + generics;
                }
                case KType.FunctionType functionType -> {
                    var returnType = "-> " + getShortString(functionType.returnType());
                    var impls = functionType.interfaces().isEmpty() ? "" : " impl " + String.join(", ", functionType.interfaces().stream().map(this::getShortString).toList());
                    var args = String.join(
                            ", ",
                            functionType.arguments().stream().map(this::getShortString).toList()
                    );
                    return "fn(" + args + ") " + returnType + impls;
                }
                case KType.GenericLink genericLink -> {
                    return genericLink.link().name();
                }
                case KType.PrimitiveType primitiveType -> {
                    return primitiveType.toString();
                }
                case KType.VoidType _ -> {
                    return "void";
                }
                case KType.Resolvable resolvable -> {
                    var resolved = resolvable.get();
                    if (resolved != null) {
                        return getShortString(resolved);
                    } else {
                        return "?";
                    }
                }
                case KType.UnprocessedType unprocessedType -> {
                    return unprocessedType.toString();
                }
            }
        }
    }



}
