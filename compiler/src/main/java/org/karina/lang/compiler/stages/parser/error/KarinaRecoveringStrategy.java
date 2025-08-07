package org.karina.lang.compiler.stages.parser.error;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.Pair;
import org.karina.lang.compiler.utils.Context;
import org.karina.lang.compiler.utils.TextSource;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.stages.parser.gen.KarinaParser;
import org.karina.lang.compiler.utils.Region;

public class KarinaRecoveringStrategy extends DefaultErrorStrategy {
    public static final String MISSING_FIELD = "<auto>";

    private final TextSource source;
    private final Context c;
    public KarinaRecoveringStrategy(Context c, TextSource source) {
        this.source = source;
        this.c = c;
    }


    @Override
    public void recover(Parser recognizer, RecognitionException e) {
        if (!(e instanceof NoViableAltException noViableAlt)) {
            super.recover(recognizer, e);
            return;
        }
        if (!isPostFixDotError(recognizer, noViableAlt)) {
            super.recover(recognizer, e);
            return;
        }


        TokenStream tokens = recognizer.getInputStream();
        Token offendingToken = noViableAlt.getOffendingToken();

        // Create the replacement ID token
        CommonToken newToken = createIdToken(recognizer, offendingToken);

        if (tokens instanceof BufferedTokenStream bufferedTokens) {
            bufferedTokens.seek(offendingToken.getTokenIndex());
            bufferedTokens.getTokens().add(offendingToken.getTokenIndex(), newToken);

            // Update token indices for all subsequent tokens
            for (int i = offendingToken.getTokenIndex() + 1; i < bufferedTokens.getTokens().size(); i++) {
                CommonToken token = (CommonToken) bufferedTokens.getTokens().get(i);
                token.setTokenIndex(i);
            }
        }

        // Move back one token to process our newly inserted token
        recognizer.consume();
        recognizer.getInputStream().seek(offendingToken.getTokenIndex() - 1);
    }

    private CommonToken createIdToken(Parser recognizer, Token offendingToken) {
        CommonToken token = (CommonToken) recognizer.getTokenFactory().create(KarinaParser.ID, MISSING_FIELD);
        token.setLine(offendingToken.getLine());
        token.setCharPositionInLine(offendingToken.getCharPositionInLine());
        token.setStartIndex(offendingToken.getStartIndex());
        token.setStopIndex(offendingToken.getStopIndex());

        return token;
    }




    //From DefaultErrorStrategy
    @Override
    protected boolean singleTokenInsertion(Parser recognizer) {
        int currentSymbolType = recognizer.getInputStream().LA(1);
        ATNState currentState = recognizer.getInterpreter().atn.states.get(recognizer.getState());
        ATNState next = currentState.transition(0).target;
        ATN atn = recognizer.getInterpreter().atn;
        IntervalSet expectingAtLL2 = atn.nextTokens(next, recognizer.getContext());
        var contains = expectingAtLL2.contains(currentSymbolType);
        if (contains) {
            Token t = recognizer.getCurrentToken();
            var line = t.getLine();
            var column = t.getCharPositionInLine();
            var region = new Region(
                    this.source,
                    new Region.Position(line - 1, column),
                    new Region.Position(line - 1, column + 1)
            );
            if (t instanceof CommonToken token && recognizer.getInputStream() instanceof TokenStream tokenStream) {
                var previous = tokenStream.get(token.getTokenIndex() - 1);
                if (previous != null) {
                    var previousLine = previous.getLine();
                    var previousCharPositionInLine = previous.getCharPositionInLine();
                    region = new Region(
                            this.source,
                            new Region.Position(previousLine - 1, previousCharPositionInLine + 1),
                            new Region.Position(previousLine - 1, previousCharPositionInLine + 2)
                    );
                }
            }
            var missingSymbol = getMissingSymbol(recognizer);
            Log.warn(this.c, region, "Inserted " + missingSymbol.getText());
        }
        return contains;
    }

    @Override
    public void sync(Parser recognizer) throws RecognitionException {
        super.sync(recognizer);
    }

    //From DefaultErrorStrategy
    @Override
    public Token recoverInline(Parser recognizer)
            throws RecognitionException
    {
        // SINGLE TOKEN DELETION
        Token matchedSymbol = singleTokenDeletion(recognizer);
        if ( matchedSymbol !=null) {
            // we have deleted the extra token.
            // now, move past ttype token as if all were ok
            recognizer.consume();
            return matchedSymbol;
        }

        // SINGLE TOKEN INSERTION
        if (singleTokenInsertion(recognizer) ) {
            return getMissingSymbol(recognizer);
        }

        // even that didn't work; must throw the exception
        InputMismatchException e;
        if (this.nextTokensContext == null) {
            e = new InputMismatchException(recognizer);
        } else {
            e = new InputMismatchException(recognizer, this.nextTokensState, this.nextTokensContext);
        }

        throw e;
    }


    @Override
    protected Token getMissingSymbol(Parser recognizer) {
        Token currentSymbol = recognizer.getCurrentToken();
        IntervalSet expecting = getExpectedTokens(recognizer);
        int expectedTokenType = Token.INVALID_TYPE;
        if ( !expecting.isNil() ) {
            expectedTokenType = expecting.getMinElement(); // get any element
        }
        String tokenText;
        if ( expectedTokenType == Token.EOF ) tokenText = "<missing EOF>";
        else tokenText = recognizer.getVocabulary().getDisplayName(expectedTokenType);
        Token current = currentSymbol;
        Token lookback = recognizer.getInputStream().LT(-1);
        if ( current.getType() == Token.EOF && lookback!=null ) {
            current = lookback;
        }
        return
                recognizer.getTokenFactory().create(new Pair<TokenSource, CharStream>(current.getTokenSource(), current.getTokenSource().getInputStream()), expectedTokenType, tokenText,
                        Token.DEFAULT_CHANNEL,
                        -1, -1,
                        current.getLine(), current.getCharPositionInLine());
    }

    public static boolean isRecoverableError(Recognizer<?, ?> recognizer, RecognitionException e) {
        if (!(e instanceof NoViableAltException noViableAlt)) {
            return false;
        }
        return isPostFixDotError(recognizer, noViableAlt);
    }

    private static boolean isPostFixDotError(Recognizer<?, ?> recognizer, NoViableAltException noViableAlt) {
        if (noViableAlt.getCtx().isEmpty()) {
            return false;
        }

        if (noViableAlt.getCtx().getRuleIndex() != KarinaParser.RULE_postFix) {
            return false;
        }

        var tokens = (TokenStream) recognizer.getInputStream();
        Token offendingToken = noViableAlt.getOffendingToken();
        Token previousToken = tokens.get(offendingToken.getTokenIndex() - 1);

        return previousToken.getText().equals(".");
    }
}
