package org.karina.lang.lsp.impl.provider;

import karina.lang.Option;
import lombok.RequiredArgsConstructor;
import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.karina.lang.lsp.lib.events.EventService;
import org.karina.lang.lsp.lib.provider.CodeLensProvider;
import org.karina.lang.lsp.test_compiler.ProviderArgs;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
public class DefaultCodeLensProvider implements CodeLensProvider {
    private final EventService eventService;

    @Override
    public List<CodeLens> getLenses(ProviderArgs index, URI uri) {

        if (index.findMain(uri, this.eventService) instanceof Option.Some(var mainMethod)) {

            var codeLens = new CodeLens();
            var start = mainMethod.region().start();
            codeLens.setRange(new Range(
                    new Position(start.line(), start.column()),
                    new Position(start.line(), start.column())
            ));
            if (mainMethod.classPointer().path().endsWith("main")) {
                codeLens.setCommand(new Command(
                        "Run",
                        "karina.run.main",
                        List.of(uri.toString())
                ));
            } else {
                codeLens.setCommand(new Command(
                        "Compile",
                        "karina.run.file",
                        List.of(uri.toString())
                ));
            }
            return List.of(codeLens);
        }
        return List.of();
    }
}
