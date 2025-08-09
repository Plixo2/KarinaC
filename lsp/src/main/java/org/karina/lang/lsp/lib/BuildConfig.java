package org.karina.lang.lsp.lib;

import java.net.URI;

/// Build configuration for a given project
public interface BuildConfig {

    /// Root of the compiled project
    URI projectRootUri();

    URI clientRootUri();



}
