package karina.lang.grammar;

import karina.lang.Option;

import java.util.Collection;

public interface TokenSet {
    Option<Token> get(String alias);
    boolean contains(String alias);
    Collection<Token> all();
}
