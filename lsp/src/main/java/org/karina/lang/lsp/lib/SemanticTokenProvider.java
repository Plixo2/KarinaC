package org.karina.lang.lsp.lib;

import java.util.List;

public interface SemanticTokenProvider {

    List<Integer> getTokens(String content);


}
