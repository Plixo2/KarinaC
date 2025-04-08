package karina.lang.grammar;

import karina.lang.Result;

import java.io.InputStream;

public interface GrammarNotationParser {

    static GrammarNotationParser of(TokenSet tokenSet) {
        return extend(RuleSet.empty(), tokenSet);
    }
    static GrammarNotationParser extend(RuleSet extend, TokenSet tokenSet) {
        return new GrammarNotationParserImpl(extend, tokenSet);
    }


    Result<RuleSet, Object> parse(InputStream inputStream);


    class GrammarNotationParserImpl implements GrammarNotationParser {
        private final RuleSet ruleSet;
        private final TokenSet tokenSet;

        public GrammarNotationParserImpl(RuleSet ruleSet, TokenSet tokenSet) {
            this.ruleSet = ruleSet;
            this.tokenSet = tokenSet;
        }

        @Override
        public Result<RuleSet, Object> parse(InputStream inputStream) {
            return null;
        }
    }

}
