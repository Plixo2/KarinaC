package org.karina.lang.lsp.impl;

import karina.lang.Option;
import lombok.AllArgsConstructor;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.MessageType;
import org.eclipse.lsp4j.Position;
import org.karina.lang.compiler.model_api.FieldModel;
import org.karina.lang.compiler.model_api.MethodModel;
import org.karina.lang.compiler.stages.attrib.AttributionProcessor;
import org.karina.lang.compiler.utils.Context;
import org.karina.lang.compiler.utils.Region;
import org.karina.lang.compiler.utils.RegionOf;
import org.karina.lang.compiler.utils.logging.DiagnosticCollection;
import org.karina.lang.compiler.utils.logging.errors.AttribError;
import org.karina.lang.lsp.lib.CompletionProvider;
import org.karina.lang.lsp.lib.events.ClientEvent;
import org.karina.lang.lsp.lib.events.EventService;
import org.karina.lang.lsp.test_compiler.OneShotCompiler;

import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
public class DefaultCompletionProvider implements CompletionProvider {
    private final EventService eventService;

    @Override
    public List<CompletionItem> getItems(
            OneShotCompiler.CompiledModelIndex index,
            URI uri,
            Position position
    ) {
        var config = Context.ContextHandling.of(
                false,
                true,
                true
        );
        config = config.enableMissingMembersSupport();

        var diagnostics = new DiagnosticCollection();
        if (!(index.importedModel instanceof Option.Some(var importedModel))) {
            return List.of();
        }

        var success = Context.run(
            config,
            diagnostics,
            null,
            null,
                (c ) -> {
                    var attributionProcessor = new AttributionProcessor();
                    attributionProcessor.attribTree(c, importedModel);
                    return "OK";
                }
        );
        if (success != null) {
            return List.of();
        }
        // collect errors in same line

        var items = new ArrayList<CompletionItem>();
        for (var diagnostic : diagnostics) {
            if (diagnostic.entry() instanceof AttribError.UnknownMember(
                    Region region,
                    String className,
                    String name,
                    Set<MethodModel> availableMethods,
                    Set<FieldModel> availableFields,
                    List<RegionOf<String>> protectedMembers,
                    List<RegionOf<String>> otherProtected
            )) {
                var file = CodeDiagnosticInformationBuilder.resourceToFile(region.source().resource());
                if (file == null) {
                    continue;
                }
                if (!file.uri().equals(uri)) {
                    continue;
                }
                if (region.start().line() != position.getLine()) {
                    continue;
                }
                this.eventService.send(new ClientEvent.Log("start " + region.start().column(), MessageType.Log));
                this.eventService.send(new ClientEvent.Log("end " +position.getCharacter(), MessageType.Log));
                this.eventService.send(new ClientEvent.Log("a " + (region.start().column() < position.getCharacter()), MessageType.Log));
                this.eventService.send(new ClientEvent.Log("b " + (region.start().column() > position.getCharacter() + 3), MessageType.Log));
                if (region.start().column() < position.getCharacter() - 2) {
                    continue;
                }
                if (region.start().column() > position.getCharacter() + 3) {
                    continue;
                }

                for (var field : availableFields) {
                    var item = new CompletionItem(field.name());
                    item.setKind(CompletionItemKind.Field);
                    item.setDetail(field.name() + ": " + field.type().toString());
                    item.setInsertText(field.name());
                    items.add(item);
                }
                for (var method : availableMethods) {
                    var signatureString = getSignatureString(method);
                    var item = new CompletionItem(signatureString);
                    item.setKind(CompletionItemKind.Method);
                    item.setDetail(Modifier.toString(method.modifiers()) + " fn " + signatureString);

                    var args = String.join(", ", method.parameters());
                    item.setInsertText(method.name() + "(" + args + ")");
                    items.add(item);
                }

            }
        }

        return items;
    }
    private static String getSignatureString(MethodModel method) {
        return method.name() + "(" + getParamString(method) + ") -> " + method.signature().returnType();
    }

    private static String getParamString(MethodModel method) {
        if (method.parameters().size() != method.signature().parameters().size()) {
            return String.join(", ", method.parameters());
        }
        var parts = new ArrayList<String>();
        for (var i = 0; i < method.parameters().size(); i++) {
            var name = method.parameters().get(i);
            var type = method.signature().parameters().get(i);
            parts.add(name + ": " + type);
        }

        return String.join(", ", parts);
    }
}
