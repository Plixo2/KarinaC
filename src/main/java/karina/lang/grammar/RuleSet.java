package karina.lang.grammar;


import java.util.Collection;

public interface RuleSet {
    Rule get(String name);
    boolean contains(String name);
    Collection<Rule> rules();


}
