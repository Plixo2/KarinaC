package org.karina.lang.compiler.stages.parser.error;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.karina.lang.compiler.utils.logging.Log;
import org.karina.lang.compiler.stages.parser.gen.KarinaLexer;
import org.karina.lang.compiler.utils.Context;
import org.karina.lang.compiler.utils.Region;
import org.karina.lang.compiler.utils.TextSource;

import java.util.BitSet;

/**
 * Parser and Lexer error listener.
 */
public class KarinaErrorListener implements ANTLRErrorListener {
    private final TextSource source;
    private final Context c;

    public KarinaErrorListener(Context c, TextSource source) {
        this.source = source;
        this.c = c;
    }


    @Override
    public void syntaxError(
            Recognizer<?, ?> recognizer,
            Object offendingSymbol,
            int line,
            int charPositionInLine,
            String msg,
            RecognitionException e
    ) {

        var region = new Region(
            this.source,
            new Region.Position(line - 1, charPositionInLine),
            new Region.Position(line - 1, charPositionInLine + 1)
        );

//        if (KarinaRecoveringStrategy.isRecoverableError(recognizer, e)) {
//            if (offendingSymbol instanceof CommonToken token && recognizer.getInputStream() instanceof TokenStream tokenStream) {
//                var previous = tokenStream.get(token.getTokenIndex() - 1);
//                if (previous != null && previous.getType() == KarinaLexer.CHAR_DOT) {
//                    var previousLine = previous.getLine();
//                    var previousCharPositionInLine = previous.getCharPositionInLine();
//                    region = new Region(
//                            this.source,
//                            new Region.Position(previousLine - 1, previousCharPositionInLine),
//                            new Region.Position(previousLine - 1, previousCharPositionInLine + 1)
//                    );
//                }
//            }
//
//            Log.warn(this.c, region, "Missing Field, replaced with " + KarinaRecoveringStrategy.MISSING_FIELD);
//            return;
//        }


        var name = "?";
        if (e != null) {
            name = e.getClass().getSimpleName();
        }
        Log.syntaxError(this.c, region, name + " -> " + msg);
        throw new Log.KarinaException();

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
