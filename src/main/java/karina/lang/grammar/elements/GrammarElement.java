package karina.lang.grammar.elements;

import karina.lang.grammar.Rule;

public sealed interface GrammarElement {

    record TerminalElement(String value) implements GrammarElement {}
    record SequenceElement(GrammarElement[] elements) implements GrammarElement {}
    record ChoiceElement(GrammarElement[] elements) implements GrammarElement {}
    record OptionalElement(GrammarElement element) implements GrammarElement {}
    record RepeatElement(GrammarElement element) implements GrammarElement {}
    record NecessaryElement(GrammarElement element) implements GrammarElement {}
    record RuleElement(Rule rule) implements GrammarElement {}

}
