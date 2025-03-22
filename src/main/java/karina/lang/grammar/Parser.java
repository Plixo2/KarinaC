package karina.lang.grammar;

import karina.lang.Result;

public interface Parser {

    static Parser of(RuleSet ruleSet) {
        //return new ParserImpl(ruleSet);
        throw new NullPointerException("");
    }

    Result<Object, Object> apply(String rule, TokenStream stream);



}
