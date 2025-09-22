package org.karina.lang.lsp.test_compiler;

import com.google.common.collect.ImmutableMap;
import karina.lang.Option;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.lsp.impl.DefaultVirtualFileElevator;
import org.karina.lang.lsp.impl.DefaultVirtualFileSystem;
import org.karina.lang.lsp.impl.DefaultVirtualFileTreeNode;
import org.karina.lang.lsp.lib.VirtualFile;
import org.karina.lang.lsp.lib.VirtualFileElevator;
import org.karina.lang.lsp.lib.VirtualFileSystem;
import org.karina.lang.lsp.lib.VirtualFileTreeNode;
import org.karina.lang.lsp.lib.events.EventService;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * @param languageModel   when existing, userModel also exists
 * @param importedModel   when existing, userModel and languageModel also exists
 * @param attributedModel when existing, userModel, languageModel and importedModel also exist
 * @param loweredModel    when existing, userModel, languageModel and importedModel also exist
 */
public record CompiledModelIndex(
        Option<Model> binaryModel,
        Option<Model> userModel,
        Option<Model> languageModel,
        Option<Model> importedModel,
        Option<Model> attributedModel,
        Option<Model> loweredModel
) {
    public static final CompiledModelIndex EMPTY = new CompiledModelIndex(
            karina.lang.Option.none(),
            karina.lang.Option.none(),
            karina.lang.Option.none(),
            karina.lang.Option.none(),
            karina.lang.Option.none(),
            karina.lang.Option.none()
    );

}
