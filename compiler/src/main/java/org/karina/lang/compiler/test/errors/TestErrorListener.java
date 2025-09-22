package org.karina.lang.compiler.test.errors;

import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

import java.util.BitSet;

@RequiredArgsConstructor
public class TestErrorListener implements ANTLRErrorListener {
    private final String prefix;
    @Override
    public void syntaxError(
            Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
            String msg, RecognitionException e
    ) {
        System.err.println(this.prefix + ": line " + line + ":" + charPositionInLine + " " + msg);
    }

    @Override
    public void reportAmbiguity(
            Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact,
            BitSet ambigAlts, ATNConfigSet configs
    ) {

    }

    @Override
    public void reportAttemptingFullContext(
            Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet conflictingAlts,
            ATNConfigSet configs
    ) {

    }

    @Override
    public void reportContextSensitivity(
            Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction,
            ATNConfigSet configs
    ) {

    }
}
