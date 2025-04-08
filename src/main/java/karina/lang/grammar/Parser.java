package karina.lang.grammar;

import karina.lang.Option;
import karina.lang.Result;

public interface Parser {

    static Parser of(RuleSet ruleSet) {
        return new ParserImpl(ruleSet);
    }

    Result<Object, Object> apply(String rule, TokenStream stream);

    class ParserImpl implements Parser {
        private final RuleSet ruleSet;

        private ParserImpl(RuleSet ruleSet) {
            this.ruleSet = ruleSet;
        }

        @Override
        public Result<Object, Object> apply(String ruleName, TokenStream stream) {
            var rule = this.ruleSet.get(ruleName).okOrGet(() -> "Rule not found: " + ruleName);
            if (rule instanceof Result.Err<Rule, String> err) {
                return Result.err(err.error);
            }
            throw new NullPointerException("Not implemented");
        }
    }

}
