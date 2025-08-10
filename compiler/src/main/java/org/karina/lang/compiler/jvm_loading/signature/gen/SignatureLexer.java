// Generated from C:/Users/Morit/Documents/Projects/Java/ttyl/compiler/grammar/signature/Signature.g4 by ANTLR 4.13.2
package org.karina.lang.compiler.jvm_loading.signature.gen;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class SignatureLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, V=14, T=15, L=16, B=17, C=18, D=19, 
		F=20, I=21, J=22, S=23, Z=24, NormalStart=25, NormalRest=26;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
			"T__9", "T__10", "T__11", "T__12", "V", "T", "L", "B", "C", "D", "F", 
			"I", "J", "S", "Z", "NormalStart", "NormalRest"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'('", "')'", "'<'", "'>'", "':'", "';'", "'/'", "'.'", "'*'", 
			"'+'", "'-'", "'['", "'^'", "'V'", "'T'", "'L'", "'B'", "'C'", "'D'", 
			"'F'", "'I'", "'J'", "'S'", "'Z'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, "V", "T", "L", "B", "C", "D", "F", "I", "J", "S", "Z", "NormalStart", 
			"NormalRest"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public SignatureLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Signature.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\u0004\u0000\u001ai\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001"+
		"\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004"+
		"\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007"+
		"\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b"+
		"\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002"+
		"\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002"+
		"\u0012\u0007\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002"+
		"\u0015\u0007\u0015\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002"+
		"\u0018\u0007\u0018\u0002\u0019\u0007\u0019\u0001\u0000\u0001\u0000\u0001"+
		"\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001"+
		"\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001"+
		"\u0007\u0001\u0007\u0001\b\u0001\b\u0001\t\u0001\t\u0001\n\u0001\n\u0001"+
		"\u000b\u0001\u000b\u0001\f\u0001\f\u0001\r\u0001\r\u0001\u000e\u0001\u000e"+
		"\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011"+
		"\u0001\u0012\u0001\u0012\u0001\u0013\u0001\u0013\u0001\u0014\u0001\u0014"+
		"\u0001\u0015\u0001\u0015\u0001\u0016\u0001\u0016\u0001\u0017\u0001\u0017"+
		"\u0001\u0018\u0001\u0018\u0001\u0019\u0001\u0019\u0000\u0000\u001a\u0001"+
		"\u0001\u0003\u0002\u0005\u0003\u0007\u0004\t\u0005\u000b\u0006\r\u0007"+
		"\u000f\b\u0011\t\u0013\n\u0015\u000b\u0017\f\u0019\r\u001b\u000e\u001d"+
		"\u000f\u001f\u0010!\u0011#\u0012%\u0013\'\u0014)\u0015+\u0016-\u0017/"+
		"\u00181\u00193\u001a\u0001\u0000\u0002\u0007\u0000$$AKMSUUWZ__az\u0001"+
		"\u000009h\u0000\u0001\u0001\u0000\u0000\u0000\u0000\u0003\u0001\u0000"+
		"\u0000\u0000\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000"+
		"\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000"+
		"\u0000\u0000\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000"+
		"\u0000\u0011\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000\u0000\u0000"+
		"\u0000\u0015\u0001\u0000\u0000\u0000\u0000\u0017\u0001\u0000\u0000\u0000"+
		"\u0000\u0019\u0001\u0000\u0000\u0000\u0000\u001b\u0001\u0000\u0000\u0000"+
		"\u0000\u001d\u0001\u0000\u0000\u0000\u0000\u001f\u0001\u0000\u0000\u0000"+
		"\u0000!\u0001\u0000\u0000\u0000\u0000#\u0001\u0000\u0000\u0000\u0000%"+
		"\u0001\u0000\u0000\u0000\u0000\'\u0001\u0000\u0000\u0000\u0000)\u0001"+
		"\u0000\u0000\u0000\u0000+\u0001\u0000\u0000\u0000\u0000-\u0001\u0000\u0000"+
		"\u0000\u0000/\u0001\u0000\u0000\u0000\u00001\u0001\u0000\u0000\u0000\u0000"+
		"3\u0001\u0000\u0000\u0000\u00015\u0001\u0000\u0000\u0000\u00037\u0001"+
		"\u0000\u0000\u0000\u00059\u0001\u0000\u0000\u0000\u0007;\u0001\u0000\u0000"+
		"\u0000\t=\u0001\u0000\u0000\u0000\u000b?\u0001\u0000\u0000\u0000\rA\u0001"+
		"\u0000\u0000\u0000\u000fC\u0001\u0000\u0000\u0000\u0011E\u0001\u0000\u0000"+
		"\u0000\u0013G\u0001\u0000\u0000\u0000\u0015I\u0001\u0000\u0000\u0000\u0017"+
		"K\u0001\u0000\u0000\u0000\u0019M\u0001\u0000\u0000\u0000\u001bO\u0001"+
		"\u0000\u0000\u0000\u001dQ\u0001\u0000\u0000\u0000\u001fS\u0001\u0000\u0000"+
		"\u0000!U\u0001\u0000\u0000\u0000#W\u0001\u0000\u0000\u0000%Y\u0001\u0000"+
		"\u0000\u0000\'[\u0001\u0000\u0000\u0000)]\u0001\u0000\u0000\u0000+_\u0001"+
		"\u0000\u0000\u0000-a\u0001\u0000\u0000\u0000/c\u0001\u0000\u0000\u0000"+
		"1e\u0001\u0000\u0000\u00003g\u0001\u0000\u0000\u000056\u0005(\u0000\u0000"+
		"6\u0002\u0001\u0000\u0000\u000078\u0005)\u0000\u00008\u0004\u0001\u0000"+
		"\u0000\u00009:\u0005<\u0000\u0000:\u0006\u0001\u0000\u0000\u0000;<\u0005"+
		">\u0000\u0000<\b\u0001\u0000\u0000\u0000=>\u0005:\u0000\u0000>\n\u0001"+
		"\u0000\u0000\u0000?@\u0005;\u0000\u0000@\f\u0001\u0000\u0000\u0000AB\u0005"+
		"/\u0000\u0000B\u000e\u0001\u0000\u0000\u0000CD\u0005.\u0000\u0000D\u0010"+
		"\u0001\u0000\u0000\u0000EF\u0005*\u0000\u0000F\u0012\u0001\u0000\u0000"+
		"\u0000GH\u0005+\u0000\u0000H\u0014\u0001\u0000\u0000\u0000IJ\u0005-\u0000"+
		"\u0000J\u0016\u0001\u0000\u0000\u0000KL\u0005[\u0000\u0000L\u0018\u0001"+
		"\u0000\u0000\u0000MN\u0005^\u0000\u0000N\u001a\u0001\u0000\u0000\u0000"+
		"OP\u0005V\u0000\u0000P\u001c\u0001\u0000\u0000\u0000QR\u0005T\u0000\u0000"+
		"R\u001e\u0001\u0000\u0000\u0000ST\u0005L\u0000\u0000T \u0001\u0000\u0000"+
		"\u0000UV\u0005B\u0000\u0000V\"\u0001\u0000\u0000\u0000WX\u0005C\u0000"+
		"\u0000X$\u0001\u0000\u0000\u0000YZ\u0005D\u0000\u0000Z&\u0001\u0000\u0000"+
		"\u0000[\\\u0005F\u0000\u0000\\(\u0001\u0000\u0000\u0000]^\u0005I\u0000"+
		"\u0000^*\u0001\u0000\u0000\u0000_`\u0005J\u0000\u0000`,\u0001\u0000\u0000"+
		"\u0000ab\u0005S\u0000\u0000b.\u0001\u0000\u0000\u0000cd\u0005Z\u0000\u0000"+
		"d0\u0001\u0000\u0000\u0000ef\u0007\u0000\u0000\u0000f2\u0001\u0000\u0000"+
		"\u0000gh\u0007\u0001\u0000\u0000h4\u0001\u0000\u0000\u0000\u0001\u0000"+
		"\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}