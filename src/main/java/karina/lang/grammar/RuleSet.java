package karina.lang.grammar;


import karina.lang.Option;

import java.util.Collection;

public interface RuleSet {
    Option<Rule> get(String name);
    boolean contains(String name);
    Collection<Rule> all();

    static RuleSet empty() {
        throw new NullPointerException("Not implemented");
    }
}
