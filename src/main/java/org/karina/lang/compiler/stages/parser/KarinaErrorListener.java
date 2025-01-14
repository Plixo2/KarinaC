package org.karina.lang.compiler.stages.parser;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.utils.Span;
import org.karina.lang.compiler.api.TextSource;

import java.util.BitSet;

/**
 * Parser and Lexer error listener.
 */
public class KarinaErrorListener implements ANTLRErrorListener {
    @Getter
    @Accessors(fluent = true)
    private boolean hadError = false;
    private final TextSource source;

    private final boolean throwErrors;

    public KarinaErrorListener(TextSource source, boolean throwErrors) {
        this.source = source;
        this.throwErrors = throwErrors;
    }


    @Override
    public void syntaxError(
            Recognizer<?, ?> recognizer,
            Object offendingSymbol,
            int line,
            int charPositionInLine,
            String msg, RecognitionException e
    ) {

        var span = new Span(
            this.source,
            new Span.Position(line - 1, charPositionInLine),
            new Span.Position(line - 1, charPositionInLine + 1)
        );
        this.hadError = true;

        if (this.throwErrors) {
            Log.syntaxError(span, msg);
            throw new Log.KarinaException();
        }

    }

    //#region Unused methods
    @Override
    public void reportAmbiguity(
            Parser recognizer,
            DFA dfa,
            int startIndex,
            int stopIndex,
            boolean exact,
            BitSet ambigAlts,
            ATNConfigSet configs
    ) {
        // Do nothing
    }

    @Override
    public void reportAttemptingFullContext(
            Parser recognizer,
            DFA dfa,
            int startIndex,
            int stopIndex,
            BitSet conflictingAlts,
            ATNConfigSet configs
    ) {
        // Do nothing
    }

    @Override
    public void reportContextSensitivity(
            Parser recognizer,
            DFA dfa,
            int startIndex,
            int stopIndex,
            int prediction,
            ATNConfigSet configs
    ) {
        // Do nothing
    }
    //#endregion

}
