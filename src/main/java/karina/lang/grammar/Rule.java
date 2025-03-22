package karina.lang.grammar;

import karina.lang.grammar.elements.GrammarElement;

public interface Rule {
    String name();
    GrammarElement element();
}
