// Generated from C:/Users/Morit/Documents/Projects/Java/ttyl/resources/grammar/language/KarinaParser.g4 by ANTLR 4.13.2
package org.karina.lang.compiler.stages.parser.gen;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class KarinaParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		EXPR=1, TYPE=2, FN=3, IS=4, IN=5, AS=6, OF=7, NULL=8, IMPORT=9, EXTENDS=10, 
		EXTEND=11, MATCH=12, OVERRIDE=13, NATIVE=14, TRUE=15, FALSE=16, VIRTUAL=17, 
		BREAK=18, RETURN=19, YIELD=20, STRUCT=21, STATIC=22, RAISE=23, TRAIT=24, 
		IMPL=25, ENUM=26, CLASS=27, LET=28, IF=29, ELSE=30, MATCHES=31, WHILE=32, 
		FOR=33, SUPER=34, WHERE=35, INTERFACE=36, SELF=37, INT=38, LONG=39, BYTE=40, 
		CHAR=41, DOUBLE=42, SHORT=43, STRING=44, FLOAT=45, BOOL=46, VOID=47, JSON=48, 
		CONTINUE=49, ARROW_RIGHT=50, ARROW_RIGHT_BOLD=51, GREATER_EQULAS=52, SMALLER_EQUALS=53, 
		EQUALS=54, STRICT_EQUALS=55, STRICT_NOT_EQUALS=56, NOT_EQUALS=57, AND_AND=58, 
		OR_OR=59, CHAR_PLIS=60, CHAR_MINUS=61, CHAR_STAR=62, CHAR_R_SLASH=63, 
		CHAR_PERCENT=64, CHAR_OR=65, CHAR_XOR=66, CHAR_TILDE=67, CHAR_GREATER=68, 
		CHAR_SMALLER=69, CHAR_EXCLAMATION=70, CHAR_COLON=71, CHAR_EQUAL=72, CHAR_L_PAREN=73, 
		CHAR_R_PAREN=74, CHAR_L_BRACE=75, CHAR_R_BRACE=76, CHAR_L_BRACKET=77, 
		CHAR_R_BRACKET=78, CHAR_AT=79, CHAR_COMMA=80, CHAR_UNDER=81, CHAR_AND=82, 
		CHAR_DOT=83, CHAR_QUESTION=84, CHAR_SEMICOLON=85, CHAR_ESCAPE=86, STRING_LITERAL=87, 
		CHAR_LITERAL=88, NUMBER=89, INTEGER_NUMBER=90, FLOAT_NUMBER=91, ID=92, 
		WS=93, COMMENT=94, LINE_COMMENT=95;
	public static final int
		RULE_unit = 0, RULE_import_ = 1, RULE_commaWordChain = 2, RULE_item = 3, 
		RULE_function = 4, RULE_struct = 5, RULE_implementation = 6, RULE_boundWhere = 7, 
		RULE_genericWithBounds = 8, RULE_genericWithBound = 9, RULE_bounds = 10, 
		RULE_bound = 11, RULE_field = 12, RULE_enum = 13, RULE_enumMember = 14, 
		RULE_interface = 15, RULE_interfaceExtension = 16, RULE_selfParameterList = 17, 
		RULE_parameterList = 18, RULE_parameter = 19, RULE_type = 20, RULE_typePostFix = 21, 
		RULE_typeInner = 22, RULE_structType = 23, RULE_arrayType = 24, RULE_functionType = 25, 
		RULE_typeList = 26, RULE_genericHint = 27, RULE_genericHintDefinition = 28, 
		RULE_dotWordChain = 29, RULE_annotation = 30, RULE_jsonObj = 31, RULE_jsonPair = 32, 
		RULE_jsonArray = 33, RULE_jsonExpression = 34, RULE_jsonType = 35, RULE_jsonValue = 36, 
		RULE_block = 37, RULE_exprWithBlock = 38, RULE_expression = 39, RULE_varDef = 40, 
		RULE_closure = 41, RULE_interfaceImpl = 42, RULE_structTypeList = 43, 
		RULE_match = 44, RULE_matchCase = 45, RULE_matchInstance = 46, RULE_matchDefault = 47, 
		RULE_if = 48, RULE_elseExpr = 49, RULE_isShort = 50, RULE_while = 51, 
		RULE_for = 52, RULE_throw = 53, RULE_conditionalOrExpression = 54, RULE_conditionalAndExpression = 55, 
		RULE_equalityExpression = 56, RULE_relationalExpression = 57, RULE_additiveExpression = 58, 
		RULE_multiplicativeExpression = 59, RULE_unaryExpression = 60, RULE_factor = 61, 
		RULE_postFix = 62, RULE_object = 63, RULE_array = 64, RULE_expressionList = 65, 
		RULE_initList = 66, RULE_memberInit = 67, RULE_isInstanceOf = 68, RULE_optTypeList = 69, 
		RULE_optTypeName = 70, RULE_id = 71, RULE_escaped = 72;
	private static String[] makeRuleNames() {
		return new String[] {
			"unit", "import_", "commaWordChain", "item", "function", "struct", "implementation", 
			"boundWhere", "genericWithBounds", "genericWithBound", "bounds", "bound", 
			"field", "enum", "enumMember", "interface", "interfaceExtension", "selfParameterList", 
			"parameterList", "parameter", "type", "typePostFix", "typeInner", "structType", 
			"arrayType", "functionType", "typeList", "genericHint", "genericHintDefinition", 
			"dotWordChain", "annotation", "jsonObj", "jsonPair", "jsonArray", "jsonExpression", 
			"jsonType", "jsonValue", "block", "exprWithBlock", "expression", "varDef", 
			"closure", "interfaceImpl", "structTypeList", "match", "matchCase", "matchInstance", 
			"matchDefault", "if", "elseExpr", "isShort", "while", "for", "throw", 
			"conditionalOrExpression", "conditionalAndExpression", "equalityExpression", 
			"relationalExpression", "additiveExpression", "multiplicativeExpression", 
			"unaryExpression", "factor", "postFix", "object", "array", "expressionList", 
			"initList", "memberInit", "isInstanceOf", "optTypeList", "optTypeName", 
			"id", "escaped"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'expr'", "'type'", "'fn'", "'is'", "'in'", "'as'", "'of'", "'null'", 
			"'import'", "'extends'", "'extend'", "'match'", "'override'", "'native'", 
			"'true'", "'false'", "'virtual'", "'break'", "'return'", "'yield'", "'struct'", 
			"'static'", "'raise'", "'trait'", "'impl'", "'enum'", "'class'", "'let'", 
			"'if'", "'else'", "'matches'", "'while'", "'for'", "'super'", "'where'", 
			"'interface'", "'self'", "'int'", "'long'", "'byte'", "'char'", "'double'", 
			"'short'", "'string'", "'float'", "'bool'", "'void'", "'json'", "'continue'", 
			"'->'", "'=>'", "'>='", "'<='", "'=='", "'==='", "'!=='", "'!='", "'&&'", 
			"'||'", "'+'", "'-'", "'*'", "'/'", "'%'", "'|'", "'^'", "'~'", "'>'", 
			"'<'", "'!'", "':'", "'='", "'('", "')'", "'{'", "'}'", "'['", "']'", 
			"'@'", "','", "'_'", "'&'", "'.'", "'?'", "';'", "'\\'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "EXPR", "TYPE", "FN", "IS", "IN", "AS", "OF", "NULL", "IMPORT", 
			"EXTENDS", "EXTEND", "MATCH", "OVERRIDE", "NATIVE", "TRUE", "FALSE", 
			"VIRTUAL", "BREAK", "RETURN", "YIELD", "STRUCT", "STATIC", "RAISE", "TRAIT", 
			"IMPL", "ENUM", "CLASS", "LET", "IF", "ELSE", "MATCHES", "WHILE", "FOR", 
			"SUPER", "WHERE", "INTERFACE", "SELF", "INT", "LONG", "BYTE", "CHAR", 
			"DOUBLE", "SHORT", "STRING", "FLOAT", "BOOL", "VOID", "JSON", "CONTINUE", 
			"ARROW_RIGHT", "ARROW_RIGHT_BOLD", "GREATER_EQULAS", "SMALLER_EQUALS", 
			"EQUALS", "STRICT_EQUALS", "STRICT_NOT_EQUALS", "NOT_EQUALS", "AND_AND", 
			"OR_OR", "CHAR_PLIS", "CHAR_MINUS", "CHAR_STAR", "CHAR_R_SLASH", "CHAR_PERCENT", 
			"CHAR_OR", "CHAR_XOR", "CHAR_TILDE", "CHAR_GREATER", "CHAR_SMALLER", 
			"CHAR_EXCLAMATION", "CHAR_COLON", "CHAR_EQUAL", "CHAR_L_PAREN", "CHAR_R_PAREN", 
			"CHAR_L_BRACE", "CHAR_R_BRACE", "CHAR_L_BRACKET", "CHAR_R_BRACKET", "CHAR_AT", 
			"CHAR_COMMA", "CHAR_UNDER", "CHAR_AND", "CHAR_DOT", "CHAR_QUESTION", 
			"CHAR_SEMICOLON", "CHAR_ESCAPE", "STRING_LITERAL", "CHAR_LITERAL", "NUMBER", 
			"INTEGER_NUMBER", "FLOAT_NUMBER", "ID", "WS", "COMMENT", "LINE_COMMENT"
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

	@Override
	public String getGrammarFileName() { return "KarinaParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public KarinaParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class UnitContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(KarinaParser.EOF, 0); }
		public List<Import_Context> import_() {
			return getRuleContexts(Import_Context.class);
		}
		public Import_Context import_(int i) {
			return getRuleContext(Import_Context.class,i);
		}
		public List<ItemContext> item() {
			return getRuleContexts(ItemContext.class);
		}
		public ItemContext item(int i) {
			return getRuleContext(ItemContext.class,i);
		}
		public UnitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterUnit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitUnit(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitUnit(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UnitContext unit() throws RecognitionException {
		UnitContext _localctx = new UnitContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_unit);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(149);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==IMPORT) {
				{
				{
				setState(146);
				import_();
				}
				}
				setState(151);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(155);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 68788682760L) != 0) || _la==CHAR_AT) {
				{
				{
				setState(152);
				item();
				}
				}
				setState(157);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(158);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Import_Context extends ParserRuleContext {
		public TerminalNode IMPORT() { return getToken(KarinaParser.IMPORT, 0); }
		public DotWordChainContext dotWordChain() {
			return getRuleContext(DotWordChainContext.class,0);
		}
		public TerminalNode CHAR_STAR() { return getToken(KarinaParser.CHAR_STAR, 0); }
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public TerminalNode CHAR_L_BRACE() { return getToken(KarinaParser.CHAR_L_BRACE, 0); }
		public CommaWordChainContext commaWordChain() {
			return getRuleContext(CommaWordChainContext.class,0);
		}
		public TerminalNode CHAR_R_BRACE() { return getToken(KarinaParser.CHAR_R_BRACE, 0); }
		public Import_Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_import_; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterImport_(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitImport_(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitImport_(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Import_Context import_() throws RecognitionException {
		Import_Context _localctx = new Import_Context(_ctx, getState());
		enterRule(_localctx, 2, RULE_import_);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(160);
			match(IMPORT);
			setState(161);
			dotWordChain();
			setState(168);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CHAR_STAR:
				{
				setState(162);
				match(CHAR_STAR);
				}
				break;
			case EXPR:
			case TYPE:
			case CHAR_UNDER:
			case CHAR_ESCAPE:
			case ID:
				{
				setState(163);
				id();
				}
				break;
			case CHAR_L_BRACE:
				{
				setState(164);
				match(CHAR_L_BRACE);
				setState(165);
				commaWordChain();
				setState(166);
				match(CHAR_R_BRACE);
				}
				break;
			case EOF:
			case FN:
			case IMPORT:
			case STRUCT:
			case ENUM:
			case INTERFACE:
			case CHAR_AT:
				break;
			default:
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CommaWordChainContext extends ParserRuleContext {
		public List<IdContext> id() {
			return getRuleContexts(IdContext.class);
		}
		public IdContext id(int i) {
			return getRuleContext(IdContext.class,i);
		}
		public List<TerminalNode> CHAR_COMMA() { return getTokens(KarinaParser.CHAR_COMMA); }
		public TerminalNode CHAR_COMMA(int i) {
			return getToken(KarinaParser.CHAR_COMMA, i);
		}
		public CommaWordChainContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_commaWordChain; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterCommaWordChain(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitCommaWordChain(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitCommaWordChain(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CommaWordChainContext commaWordChain() throws RecognitionException {
		CommaWordChainContext _localctx = new CommaWordChainContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_commaWordChain);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(170);
			id();
			setState(175);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==CHAR_COMMA) {
				{
				{
				setState(171);
				match(CHAR_COMMA);
				setState(172);
				id();
				}
				}
				setState(177);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ItemContext extends ParserRuleContext {
		public FunctionContext function() {
			return getRuleContext(FunctionContext.class,0);
		}
		public StructContext struct() {
			return getRuleContext(StructContext.class,0);
		}
		public EnumContext enum_() {
			return getRuleContext(EnumContext.class,0);
		}
		public InterfaceContext interface_() {
			return getRuleContext(InterfaceContext.class,0);
		}
		public List<AnnotationContext> annotation() {
			return getRuleContexts(AnnotationContext.class);
		}
		public AnnotationContext annotation(int i) {
			return getRuleContext(AnnotationContext.class,i);
		}
		public ItemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_item; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterItem(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitItem(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitItem(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ItemContext item() throws RecognitionException {
		ItemContext _localctx = new ItemContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_item);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(181);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==CHAR_AT) {
				{
				{
				setState(178);
				annotation();
				}
				}
				setState(183);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(188);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case FN:
				{
				setState(184);
				function();
				}
				break;
			case STRUCT:
				{
				setState(185);
				struct();
				}
				break;
			case ENUM:
				{
				setState(186);
				enum_();
				}
				break;
			case INTERFACE:
				{
				setState(187);
				interface_();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FunctionContext extends ParserRuleContext {
		public TerminalNode FN() { return getToken(KarinaParser.FN, 0); }
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public TerminalNode CHAR_L_PAREN() { return getToken(KarinaParser.CHAR_L_PAREN, 0); }
		public SelfParameterListContext selfParameterList() {
			return getRuleContext(SelfParameterListContext.class,0);
		}
		public TerminalNode CHAR_R_PAREN() { return getToken(KarinaParser.CHAR_R_PAREN, 0); }
		public GenericHintDefinitionContext genericHintDefinition() {
			return getRuleContext(GenericHintDefinitionContext.class,0);
		}
		public TerminalNode ARROW_RIGHT() { return getToken(KarinaParser.ARROW_RIGHT, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode CHAR_EQUAL() { return getToken(KarinaParser.CHAR_EQUAL, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public FunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_function; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterFunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitFunction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionContext function() throws RecognitionException {
		FunctionContext _localctx = new FunctionContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_function);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(190);
			match(FN);
			setState(191);
			id();
			setState(193);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CHAR_SMALLER) {
				{
				setState(192);
				genericHintDefinition();
				}
			}

			setState(195);
			match(CHAR_L_PAREN);
			setState(196);
			selfParameterList();
			setState(197);
			match(CHAR_R_PAREN);
			setState(200);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ARROW_RIGHT) {
				{
				setState(198);
				match(ARROW_RIGHT);
				setState(199);
				type();
				}
			}

			setState(205);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CHAR_EQUAL:
				{
				setState(202);
				match(CHAR_EQUAL);
				setState(203);
				expression();
				}
				break;
			case CHAR_L_BRACE:
				{
				setState(204);
				block();
				}
				break;
			case EOF:
			case FN:
			case STRUCT:
			case IMPL:
			case ENUM:
			case WHERE:
			case INTERFACE:
			case CHAR_R_BRACE:
			case CHAR_AT:
				break;
			default:
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class StructContext extends ParserRuleContext {
		public TerminalNode STRUCT() { return getToken(KarinaParser.STRUCT, 0); }
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public TerminalNode CHAR_L_BRACE() { return getToken(KarinaParser.CHAR_L_BRACE, 0); }
		public TerminalNode CHAR_R_BRACE() { return getToken(KarinaParser.CHAR_R_BRACE, 0); }
		public GenericHintDefinitionContext genericHintDefinition() {
			return getRuleContext(GenericHintDefinitionContext.class,0);
		}
		public List<FieldContext> field() {
			return getRuleContexts(FieldContext.class);
		}
		public FieldContext field(int i) {
			return getRuleContext(FieldContext.class,i);
		}
		public List<FunctionContext> function() {
			return getRuleContexts(FunctionContext.class);
		}
		public FunctionContext function(int i) {
			return getRuleContext(FunctionContext.class,i);
		}
		public List<ImplementationContext> implementation() {
			return getRuleContexts(ImplementationContext.class);
		}
		public ImplementationContext implementation(int i) {
			return getRuleContext(ImplementationContext.class,i);
		}
		public List<BoundWhereContext> boundWhere() {
			return getRuleContexts(BoundWhereContext.class);
		}
		public BoundWhereContext boundWhere(int i) {
			return getRuleContext(BoundWhereContext.class,i);
		}
		public StructContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_struct; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterStruct(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitStruct(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitStruct(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StructContext struct() throws RecognitionException {
		StructContext _localctx = new StructContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_struct);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(207);
			match(STRUCT);
			setState(208);
			id();
			setState(210);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CHAR_SMALLER) {
				{
				setState(209);
				genericHintDefinition();
				}
			}

			setState(212);
			match(CHAR_L_BRACE);
			setState(216);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==EXPR || _la==TYPE || ((((_la - 81)) & ~0x3f) == 0 && ((1L << (_la - 81)) & 2081L) != 0)) {
				{
				{
				setState(213);
				field();
				}
				}
				setState(218);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(222);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FN) {
				{
				{
				setState(219);
				function();
				}
				}
				setState(224);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(228);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==IMPL) {
				{
				{
				setState(225);
				implementation();
				}
				}
				setState(230);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(234);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==WHERE) {
				{
				{
				setState(231);
				boundWhere();
				}
				}
				setState(236);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(237);
			match(CHAR_R_BRACE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ImplementationContext extends ParserRuleContext {
		public TerminalNode IMPL() { return getToken(KarinaParser.IMPL, 0); }
		public StructTypeContext structType() {
			return getRuleContext(StructTypeContext.class,0);
		}
		public TerminalNode CHAR_L_BRACE() { return getToken(KarinaParser.CHAR_L_BRACE, 0); }
		public TerminalNode CHAR_R_BRACE() { return getToken(KarinaParser.CHAR_R_BRACE, 0); }
		public List<FunctionContext> function() {
			return getRuleContexts(FunctionContext.class);
		}
		public FunctionContext function(int i) {
			return getRuleContext(FunctionContext.class,i);
		}
		public ImplementationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_implementation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterImplementation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitImplementation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitImplementation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ImplementationContext implementation() throws RecognitionException {
		ImplementationContext _localctx = new ImplementationContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_implementation);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(239);
			match(IMPL);
			setState(240);
			structType();
			setState(249);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CHAR_L_BRACE) {
				{
				setState(241);
				match(CHAR_L_BRACE);
				setState(245);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==FN) {
					{
					{
					setState(242);
					function();
					}
					}
					setState(247);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(248);
				match(CHAR_R_BRACE);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class BoundWhereContext extends ParserRuleContext {
		public TerminalNode WHERE() { return getToken(KarinaParser.WHERE, 0); }
		public GenericWithBoundsContext genericWithBounds() {
			return getRuleContext(GenericWithBoundsContext.class,0);
		}
		public List<TerminalNode> CHAR_L_BRACE() { return getTokens(KarinaParser.CHAR_L_BRACE); }
		public TerminalNode CHAR_L_BRACE(int i) {
			return getToken(KarinaParser.CHAR_L_BRACE, i);
		}
		public List<TerminalNode> CHAR_R_BRACE() { return getTokens(KarinaParser.CHAR_R_BRACE); }
		public TerminalNode CHAR_R_BRACE(int i) {
			return getToken(KarinaParser.CHAR_R_BRACE, i);
		}
		public List<FunctionContext> function() {
			return getRuleContexts(FunctionContext.class);
		}
		public FunctionContext function(int i) {
			return getRuleContext(FunctionContext.class,i);
		}
		public BoundWhereContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_boundWhere; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterBoundWhere(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitBoundWhere(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitBoundWhere(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BoundWhereContext boundWhere() throws RecognitionException {
		BoundWhereContext _localctx = new BoundWhereContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_boundWhere);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(251);
			match(WHERE);
			setState(257);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,16,_ctx) ) {
			case 1:
				{
				{
				setState(252);
				match(CHAR_L_BRACE);
				setState(253);
				genericWithBounds();
				setState(254);
				match(CHAR_R_BRACE);
				}
				}
				break;
			case 2:
				{
				setState(256);
				genericWithBounds();
				}
				break;
			}
			{
			setState(259);
			match(CHAR_L_BRACE);
			setState(263);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FN) {
				{
				{
				setState(260);
				function();
				}
				}
				setState(265);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(266);
			match(CHAR_R_BRACE);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class GenericWithBoundsContext extends ParserRuleContext {
		public List<GenericWithBoundContext> genericWithBound() {
			return getRuleContexts(GenericWithBoundContext.class);
		}
		public GenericWithBoundContext genericWithBound(int i) {
			return getRuleContext(GenericWithBoundContext.class,i);
		}
		public List<TerminalNode> CHAR_COMMA() { return getTokens(KarinaParser.CHAR_COMMA); }
		public TerminalNode CHAR_COMMA(int i) {
			return getToken(KarinaParser.CHAR_COMMA, i);
		}
		public GenericWithBoundsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_genericWithBounds; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterGenericWithBounds(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitGenericWithBounds(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitGenericWithBounds(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GenericWithBoundsContext genericWithBounds() throws RecognitionException {
		GenericWithBoundsContext _localctx = new GenericWithBoundsContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_genericWithBounds);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(276);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ID) {
				{
				setState(268);
				genericWithBound();
				setState(273);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==CHAR_COMMA) {
					{
					{
					setState(269);
					match(CHAR_COMMA);
					setState(270);
					genericWithBound();
					}
					}
					setState(275);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class GenericWithBoundContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(KarinaParser.ID, 0); }
		public BoundsContext bounds() {
			return getRuleContext(BoundsContext.class,0);
		}
		public GenericWithBoundContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_genericWithBound; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterGenericWithBound(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitGenericWithBound(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitGenericWithBound(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GenericWithBoundContext genericWithBound() throws RecognitionException {
		GenericWithBoundContext _localctx = new GenericWithBoundContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_genericWithBound);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(278);
			match(ID);
			setState(280);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CHAR_COLON) {
				{
				setState(279);
				bounds();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class BoundsContext extends ParserRuleContext {
		public TerminalNode CHAR_COLON() { return getToken(KarinaParser.CHAR_COLON, 0); }
		public List<BoundContext> bound() {
			return getRuleContexts(BoundContext.class);
		}
		public BoundContext bound(int i) {
			return getRuleContext(BoundContext.class,i);
		}
		public List<TerminalNode> CHAR_AND() { return getTokens(KarinaParser.CHAR_AND); }
		public TerminalNode CHAR_AND(int i) {
			return getToken(KarinaParser.CHAR_AND, i);
		}
		public BoundsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bounds; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterBounds(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitBounds(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitBounds(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BoundsContext bounds() throws RecognitionException {
		BoundsContext _localctx = new BoundsContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_bounds);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(282);
			match(CHAR_COLON);
			setState(291);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EXTEND || _la==IMPL) {
				{
				setState(283);
				bound();
				setState(288);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==CHAR_AND) {
					{
					{
					setState(284);
					match(CHAR_AND);
					setState(285);
					bound();
					}
					}
					setState(290);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class BoundContext extends ParserRuleContext {
		public TerminalNode IMPL() { return getToken(KarinaParser.IMPL, 0); }
		public StructTypeContext structType() {
			return getRuleContext(StructTypeContext.class,0);
		}
		public TerminalNode EXTEND() { return getToken(KarinaParser.EXTEND, 0); }
		public BoundContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bound; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterBound(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitBound(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitBound(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BoundContext bound() throws RecognitionException {
		BoundContext _localctx = new BoundContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_bound);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(297);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IMPL:
				{
				setState(293);
				match(IMPL);
				setState(294);
				structType();
				}
				break;
			case EXTEND:
				{
				setState(295);
				match(EXTEND);
				setState(296);
				structType();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FieldContext extends ParserRuleContext {
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public TerminalNode CHAR_COLON() { return getToken(KarinaParser.CHAR_COLON, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public FieldContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_field; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterField(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitField(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitField(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FieldContext field() throws RecognitionException {
		FieldContext _localctx = new FieldContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_field);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(299);
			id();
			setState(300);
			match(CHAR_COLON);
			setState(301);
			type();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class EnumContext extends ParserRuleContext {
		public TerminalNode ENUM() { return getToken(KarinaParser.ENUM, 0); }
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public TerminalNode CHAR_L_BRACE() { return getToken(KarinaParser.CHAR_L_BRACE, 0); }
		public TerminalNode CHAR_R_BRACE() { return getToken(KarinaParser.CHAR_R_BRACE, 0); }
		public GenericHintDefinitionContext genericHintDefinition() {
			return getRuleContext(GenericHintDefinitionContext.class,0);
		}
		public List<EnumMemberContext> enumMember() {
			return getRuleContexts(EnumMemberContext.class);
		}
		public EnumMemberContext enumMember(int i) {
			return getRuleContext(EnumMemberContext.class,i);
		}
		public List<FunctionContext> function() {
			return getRuleContexts(FunctionContext.class);
		}
		public FunctionContext function(int i) {
			return getRuleContext(FunctionContext.class,i);
		}
		public List<ImplementationContext> implementation() {
			return getRuleContexts(ImplementationContext.class);
		}
		public ImplementationContext implementation(int i) {
			return getRuleContext(ImplementationContext.class,i);
		}
		public List<BoundWhereContext> boundWhere() {
			return getRuleContexts(BoundWhereContext.class);
		}
		public BoundWhereContext boundWhere(int i) {
			return getRuleContext(BoundWhereContext.class,i);
		}
		public EnumContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_enum; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterEnum(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitEnum(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitEnum(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EnumContext enum_() throws RecognitionException {
		EnumContext _localctx = new EnumContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_enum);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(303);
			match(ENUM);
			setState(304);
			id();
			setState(306);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CHAR_SMALLER) {
				{
				setState(305);
				genericHintDefinition();
				}
			}

			setState(308);
			match(CHAR_L_BRACE);
			setState(312);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==EXPR || _la==TYPE || ((((_la - 81)) & ~0x3f) == 0 && ((1L << (_la - 81)) & 2081L) != 0)) {
				{
				{
				setState(309);
				enumMember();
				}
				}
				setState(314);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(318);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FN) {
				{
				{
				setState(315);
				function();
				}
				}
				setState(320);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(324);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==IMPL) {
				{
				{
				setState(321);
				implementation();
				}
				}
				setState(326);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(330);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==WHERE) {
				{
				{
				setState(327);
				boundWhere();
				}
				}
				setState(332);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(333);
			match(CHAR_R_BRACE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class EnumMemberContext extends ParserRuleContext {
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public TerminalNode CHAR_L_PAREN() { return getToken(KarinaParser.CHAR_L_PAREN, 0); }
		public ParameterListContext parameterList() {
			return getRuleContext(ParameterListContext.class,0);
		}
		public TerminalNode CHAR_R_PAREN() { return getToken(KarinaParser.CHAR_R_PAREN, 0); }
		public EnumMemberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_enumMember; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterEnumMember(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitEnumMember(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitEnumMember(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EnumMemberContext enumMember() throws RecognitionException {
		EnumMemberContext _localctx = new EnumMemberContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_enumMember);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(335);
			id();
			setState(340);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CHAR_L_PAREN) {
				{
				setState(336);
				match(CHAR_L_PAREN);
				setState(337);
				parameterList();
				setState(338);
				match(CHAR_R_PAREN);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class InterfaceContext extends ParserRuleContext {
		public TerminalNode INTERFACE() { return getToken(KarinaParser.INTERFACE, 0); }
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public TerminalNode CHAR_L_BRACE() { return getToken(KarinaParser.CHAR_L_BRACE, 0); }
		public TerminalNode CHAR_R_BRACE() { return getToken(KarinaParser.CHAR_R_BRACE, 0); }
		public GenericHintDefinitionContext genericHintDefinition() {
			return getRuleContext(GenericHintDefinitionContext.class,0);
		}
		public List<FunctionContext> function() {
			return getRuleContexts(FunctionContext.class);
		}
		public FunctionContext function(int i) {
			return getRuleContext(FunctionContext.class,i);
		}
		public List<InterfaceExtensionContext> interfaceExtension() {
			return getRuleContexts(InterfaceExtensionContext.class);
		}
		public InterfaceExtensionContext interfaceExtension(int i) {
			return getRuleContext(InterfaceExtensionContext.class,i);
		}
		public InterfaceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_interface; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterInterface(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitInterface(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitInterface(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InterfaceContext interface_() throws RecognitionException {
		InterfaceContext _localctx = new InterfaceContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_interface);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(342);
			match(INTERFACE);
			setState(343);
			id();
			setState(345);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CHAR_SMALLER) {
				{
				setState(344);
				genericHintDefinition();
				}
			}

			setState(347);
			match(CHAR_L_BRACE);
			setState(351);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FN) {
				{
				{
				setState(348);
				function();
				}
				}
				setState(353);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(357);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==IMPL) {
				{
				{
				setState(354);
				interfaceExtension();
				}
				}
				setState(359);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(360);
			match(CHAR_R_BRACE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class InterfaceExtensionContext extends ParserRuleContext {
		public TerminalNode IMPL() { return getToken(KarinaParser.IMPL, 0); }
		public StructTypeContext structType() {
			return getRuleContext(StructTypeContext.class,0);
		}
		public InterfaceExtensionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_interfaceExtension; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterInterfaceExtension(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitInterfaceExtension(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitInterfaceExtension(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InterfaceExtensionContext interfaceExtension() throws RecognitionException {
		InterfaceExtensionContext _localctx = new InterfaceExtensionContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_interfaceExtension);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(362);
			match(IMPL);
			setState(363);
			structType();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SelfParameterListContext extends ParserRuleContext {
		public List<ParameterContext> parameter() {
			return getRuleContexts(ParameterContext.class);
		}
		public ParameterContext parameter(int i) {
			return getRuleContext(ParameterContext.class,i);
		}
		public TerminalNode SELF() { return getToken(KarinaParser.SELF, 0); }
		public List<TerminalNode> CHAR_COMMA() { return getTokens(KarinaParser.CHAR_COMMA); }
		public TerminalNode CHAR_COMMA(int i) {
			return getToken(KarinaParser.CHAR_COMMA, i);
		}
		public SelfParameterListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selfParameterList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterSelfParameterList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitSelfParameterList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitSelfParameterList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SelfParameterListContext selfParameterList() throws RecognitionException {
		SelfParameterListContext _localctx = new SelfParameterListContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_selfParameterList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(376);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 137438953478L) != 0) || ((((_la - 81)) & ~0x3f) == 0 && ((1L << (_la - 81)) & 2081L) != 0)) {
				{
				setState(367);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case EXPR:
				case TYPE:
				case CHAR_UNDER:
				case CHAR_ESCAPE:
				case ID:
					{
					setState(365);
					parameter();
					}
					break;
				case SELF:
					{
					setState(366);
					match(SELF);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(373);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==CHAR_COMMA) {
					{
					{
					setState(369);
					match(CHAR_COMMA);
					setState(370);
					parameter();
					}
					}
					setState(375);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ParameterListContext extends ParserRuleContext {
		public List<ParameterContext> parameter() {
			return getRuleContexts(ParameterContext.class);
		}
		public ParameterContext parameter(int i) {
			return getRuleContext(ParameterContext.class,i);
		}
		public List<TerminalNode> CHAR_COMMA() { return getTokens(KarinaParser.CHAR_COMMA); }
		public TerminalNode CHAR_COMMA(int i) {
			return getToken(KarinaParser.CHAR_COMMA, i);
		}
		public ParameterListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameterList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterParameterList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitParameterList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitParameterList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParameterListContext parameterList() throws RecognitionException {
		ParameterListContext _localctx = new ParameterListContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_parameterList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(386);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EXPR || _la==TYPE || ((((_la - 81)) & ~0x3f) == 0 && ((1L << (_la - 81)) & 2081L) != 0)) {
				{
				setState(378);
				parameter();
				setState(383);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==CHAR_COMMA) {
					{
					{
					setState(379);
					match(CHAR_COMMA);
					setState(380);
					parameter();
					}
					}
					setState(385);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ParameterContext extends ParserRuleContext {
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public TerminalNode CHAR_COLON() { return getToken(KarinaParser.CHAR_COLON, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public ParameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterParameter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitParameter(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitParameter(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParameterContext parameter() throws RecognitionException {
		ParameterContext _localctx = new ParameterContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_parameter);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(388);
			id();
			setState(389);
			match(CHAR_COLON);
			setState(390);
			type();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TypeContext extends ParserRuleContext {
		public TypeInnerContext typeInner() {
			return getRuleContext(TypeInnerContext.class,0);
		}
		public TypePostFixContext typePostFix() {
			return getRuleContext(TypePostFixContext.class,0);
		}
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		TypeContext _localctx = new TypeContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(392);
			typeInner();
			setState(394);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,38,_ctx) ) {
			case 1:
				{
				setState(393);
				typePostFix();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TypePostFixContext extends ParserRuleContext {
		public TerminalNode CHAR_QUESTION() { return getToken(KarinaParser.CHAR_QUESTION, 0); }
		public TypePostFixContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typePostFix; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterTypePostFix(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitTypePostFix(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitTypePostFix(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypePostFixContext typePostFix() throws RecognitionException {
		TypePostFixContext _localctx = new TypePostFixContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_typePostFix);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(396);
			match(CHAR_QUESTION);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TypeInnerContext extends ParserRuleContext {
		public TerminalNode VOID() { return getToken(KarinaParser.VOID, 0); }
		public TerminalNode INT() { return getToken(KarinaParser.INT, 0); }
		public TerminalNode DOUBLE() { return getToken(KarinaParser.DOUBLE, 0); }
		public TerminalNode SHORT() { return getToken(KarinaParser.SHORT, 0); }
		public TerminalNode BYTE() { return getToken(KarinaParser.BYTE, 0); }
		public TerminalNode CHAR() { return getToken(KarinaParser.CHAR, 0); }
		public TerminalNode LONG() { return getToken(KarinaParser.LONG, 0); }
		public TerminalNode FLOAT() { return getToken(KarinaParser.FLOAT, 0); }
		public TerminalNode BOOL() { return getToken(KarinaParser.BOOL, 0); }
		public TerminalNode STRING() { return getToken(KarinaParser.STRING, 0); }
		public TerminalNode CHAR_QUESTION() { return getToken(KarinaParser.CHAR_QUESTION, 0); }
		public StructTypeContext structType() {
			return getRuleContext(StructTypeContext.class,0);
		}
		public ArrayTypeContext arrayType() {
			return getRuleContext(ArrayTypeContext.class,0);
		}
		public FunctionTypeContext functionType() {
			return getRuleContext(FunctionTypeContext.class,0);
		}
		public TerminalNode CHAR_L_PAREN() { return getToken(KarinaParser.CHAR_L_PAREN, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode CHAR_R_PAREN() { return getToken(KarinaParser.CHAR_R_PAREN, 0); }
		public TypeInnerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeInner; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterTypeInner(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitTypeInner(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitTypeInner(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeInnerContext typeInner() throws RecognitionException {
		TypeInnerContext _localctx = new TypeInnerContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_typeInner);
		try {
			setState(416);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case VOID:
				enterOuterAlt(_localctx, 1);
				{
				setState(398);
				match(VOID);
				}
				break;
			case INT:
				enterOuterAlt(_localctx, 2);
				{
				setState(399);
				match(INT);
				}
				break;
			case DOUBLE:
				enterOuterAlt(_localctx, 3);
				{
				setState(400);
				match(DOUBLE);
				}
				break;
			case SHORT:
				enterOuterAlt(_localctx, 4);
				{
				setState(401);
				match(SHORT);
				}
				break;
			case BYTE:
				enterOuterAlt(_localctx, 5);
				{
				setState(402);
				match(BYTE);
				}
				break;
			case CHAR:
				enterOuterAlt(_localctx, 6);
				{
				setState(403);
				match(CHAR);
				}
				break;
			case LONG:
				enterOuterAlt(_localctx, 7);
				{
				setState(404);
				match(LONG);
				}
				break;
			case FLOAT:
				enterOuterAlt(_localctx, 8);
				{
				setState(405);
				match(FLOAT);
				}
				break;
			case BOOL:
				enterOuterAlt(_localctx, 9);
				{
				setState(406);
				match(BOOL);
				}
				break;
			case STRING:
				enterOuterAlt(_localctx, 10);
				{
				setState(407);
				match(STRING);
				}
				break;
			case CHAR_QUESTION:
				enterOuterAlt(_localctx, 11);
				{
				setState(408);
				match(CHAR_QUESTION);
				}
				break;
			case EXPR:
			case TYPE:
			case CHAR_UNDER:
			case CHAR_ESCAPE:
			case ID:
				enterOuterAlt(_localctx, 12);
				{
				setState(409);
				structType();
				}
				break;
			case CHAR_L_BRACKET:
				enterOuterAlt(_localctx, 13);
				{
				setState(410);
				arrayType();
				}
				break;
			case FN:
				enterOuterAlt(_localctx, 14);
				{
				setState(411);
				functionType();
				}
				break;
			case CHAR_L_PAREN:
				enterOuterAlt(_localctx, 15);
				{
				setState(412);
				match(CHAR_L_PAREN);
				setState(413);
				type();
				setState(414);
				match(CHAR_R_PAREN);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class StructTypeContext extends ParserRuleContext {
		public DotWordChainContext dotWordChain() {
			return getRuleContext(DotWordChainContext.class,0);
		}
		public GenericHintContext genericHint() {
			return getRuleContext(GenericHintContext.class,0);
		}
		public StructTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_structType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterStructType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitStructType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitStructType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StructTypeContext structType() throws RecognitionException {
		StructTypeContext _localctx = new StructTypeContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_structType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(418);
			dotWordChain();
			setState(420);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,40,_ctx) ) {
			case 1:
				{
				setState(419);
				genericHint();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ArrayTypeContext extends ParserRuleContext {
		public TerminalNode CHAR_L_BRACKET() { return getToken(KarinaParser.CHAR_L_BRACKET, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode CHAR_R_BRACKET() { return getToken(KarinaParser.CHAR_R_BRACKET, 0); }
		public ArrayTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arrayType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterArrayType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitArrayType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitArrayType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArrayTypeContext arrayType() throws RecognitionException {
		ArrayTypeContext _localctx = new ArrayTypeContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_arrayType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(422);
			match(CHAR_L_BRACKET);
			setState(423);
			type();
			setState(424);
			match(CHAR_R_BRACKET);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FunctionTypeContext extends ParserRuleContext {
		public TerminalNode FN() { return getToken(KarinaParser.FN, 0); }
		public TerminalNode CHAR_L_PAREN() { return getToken(KarinaParser.CHAR_L_PAREN, 0); }
		public TypeListContext typeList() {
			return getRuleContext(TypeListContext.class,0);
		}
		public TerminalNode CHAR_R_PAREN() { return getToken(KarinaParser.CHAR_R_PAREN, 0); }
		public TerminalNode ARROW_RIGHT() { return getToken(KarinaParser.ARROW_RIGHT, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public InterfaceImplContext interfaceImpl() {
			return getRuleContext(InterfaceImplContext.class,0);
		}
		public FunctionTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterFunctionType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitFunctionType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitFunctionType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionTypeContext functionType() throws RecognitionException {
		FunctionTypeContext _localctx = new FunctionTypeContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_functionType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(426);
			match(FN);
			setState(427);
			match(CHAR_L_PAREN);
			setState(428);
			typeList();
			setState(429);
			match(CHAR_R_PAREN);
			setState(432);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ARROW_RIGHT) {
				{
				setState(430);
				match(ARROW_RIGHT);
				setState(431);
				type();
				}
			}

			setState(435);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,42,_ctx) ) {
			case 1:
				{
				setState(434);
				interfaceImpl();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TypeListContext extends ParserRuleContext {
		public List<TypeContext> type() {
			return getRuleContexts(TypeContext.class);
		}
		public TypeContext type(int i) {
			return getRuleContext(TypeContext.class,i);
		}
		public List<TerminalNode> CHAR_COMMA() { return getTokens(KarinaParser.CHAR_COMMA); }
		public TerminalNode CHAR_COMMA(int i) {
			return getToken(KarinaParser.CHAR_COMMA, i);
		}
		public TypeListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterTypeList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitTypeList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitTypeList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeListContext typeList() throws RecognitionException {
		TypeListContext _localctx = new TypeListContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_typeList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(445);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 281200098803726L) != 0) || ((((_la - 73)) & ~0x3f) == 0 && ((1L << (_la - 73)) & 534801L) != 0)) {
				{
				setState(437);
				type();
				setState(442);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==CHAR_COMMA) {
					{
					{
					setState(438);
					match(CHAR_COMMA);
					setState(439);
					type();
					}
					}
					setState(444);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class GenericHintContext extends ParserRuleContext {
		public TerminalNode CHAR_SMALLER() { return getToken(KarinaParser.CHAR_SMALLER, 0); }
		public TerminalNode CHAR_GREATER() { return getToken(KarinaParser.CHAR_GREATER, 0); }
		public List<TypeContext> type() {
			return getRuleContexts(TypeContext.class);
		}
		public TypeContext type(int i) {
			return getRuleContext(TypeContext.class,i);
		}
		public List<TerminalNode> CHAR_COMMA() { return getTokens(KarinaParser.CHAR_COMMA); }
		public TerminalNode CHAR_COMMA(int i) {
			return getToken(KarinaParser.CHAR_COMMA, i);
		}
		public GenericHintContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_genericHint; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterGenericHint(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitGenericHint(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitGenericHint(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GenericHintContext genericHint() throws RecognitionException {
		GenericHintContext _localctx = new GenericHintContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_genericHint);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(447);
			match(CHAR_SMALLER);
			setState(456);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 281200098803726L) != 0) || ((((_la - 73)) & ~0x3f) == 0 && ((1L << (_la - 73)) & 534801L) != 0)) {
				{
				setState(448);
				type();
				setState(453);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==CHAR_COMMA) {
					{
					{
					setState(449);
					match(CHAR_COMMA);
					setState(450);
					type();
					}
					}
					setState(455);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(458);
			match(CHAR_GREATER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class GenericHintDefinitionContext extends ParserRuleContext {
		public TerminalNode CHAR_SMALLER() { return getToken(KarinaParser.CHAR_SMALLER, 0); }
		public TerminalNode CHAR_GREATER() { return getToken(KarinaParser.CHAR_GREATER, 0); }
		public List<IdContext> id() {
			return getRuleContexts(IdContext.class);
		}
		public IdContext id(int i) {
			return getRuleContext(IdContext.class,i);
		}
		public List<TerminalNode> CHAR_COMMA() { return getTokens(KarinaParser.CHAR_COMMA); }
		public TerminalNode CHAR_COMMA(int i) {
			return getToken(KarinaParser.CHAR_COMMA, i);
		}
		public GenericHintDefinitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_genericHintDefinition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterGenericHintDefinition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitGenericHintDefinition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitGenericHintDefinition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GenericHintDefinitionContext genericHintDefinition() throws RecognitionException {
		GenericHintDefinitionContext _localctx = new GenericHintDefinitionContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_genericHintDefinition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(460);
			match(CHAR_SMALLER);
			setState(469);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EXPR || _la==TYPE || ((((_la - 81)) & ~0x3f) == 0 && ((1L << (_la - 81)) & 2081L) != 0)) {
				{
				setState(461);
				id();
				setState(466);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==CHAR_COMMA) {
					{
					{
					setState(462);
					match(CHAR_COMMA);
					setState(463);
					id();
					}
					}
					setState(468);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(471);
			match(CHAR_GREATER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DotWordChainContext extends ParserRuleContext {
		public List<IdContext> id() {
			return getRuleContexts(IdContext.class);
		}
		public IdContext id(int i) {
			return getRuleContext(IdContext.class,i);
		}
		public List<TerminalNode> CHAR_DOT() { return getTokens(KarinaParser.CHAR_DOT); }
		public TerminalNode CHAR_DOT(int i) {
			return getToken(KarinaParser.CHAR_DOT, i);
		}
		public DotWordChainContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dotWordChain; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterDotWordChain(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitDotWordChain(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitDotWordChain(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DotWordChainContext dotWordChain() throws RecognitionException {
		DotWordChainContext _localctx = new DotWordChainContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_dotWordChain);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(473);
			id();
			setState(478);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,49,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(474);
					match(CHAR_DOT);
					setState(475);
					id();
					}
					} 
				}
				setState(480);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,49,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AnnotationContext extends ParserRuleContext {
		public TerminalNode CHAR_AT() { return getToken(KarinaParser.CHAR_AT, 0); }
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public TerminalNode CHAR_EQUAL() { return getToken(KarinaParser.CHAR_EQUAL, 0); }
		public JsonValueContext jsonValue() {
			return getRuleContext(JsonValueContext.class,0);
		}
		public AnnotationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_annotation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterAnnotation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitAnnotation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitAnnotation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AnnotationContext annotation() throws RecognitionException {
		AnnotationContext _localctx = new AnnotationContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_annotation);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(481);
			match(CHAR_AT);
			setState(482);
			id();
			setState(485);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CHAR_EQUAL) {
				{
				setState(483);
				match(CHAR_EQUAL);
				setState(484);
				jsonValue();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class JsonObjContext extends ParserRuleContext {
		public TerminalNode CHAR_L_BRACE() { return getToken(KarinaParser.CHAR_L_BRACE, 0); }
		public TerminalNode CHAR_R_BRACE() { return getToken(KarinaParser.CHAR_R_BRACE, 0); }
		public List<JsonPairContext> jsonPair() {
			return getRuleContexts(JsonPairContext.class);
		}
		public JsonPairContext jsonPair(int i) {
			return getRuleContext(JsonPairContext.class,i);
		}
		public List<TerminalNode> CHAR_COMMA() { return getTokens(KarinaParser.CHAR_COMMA); }
		public TerminalNode CHAR_COMMA(int i) {
			return getToken(KarinaParser.CHAR_COMMA, i);
		}
		public JsonObjContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_jsonObj; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterJsonObj(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitJsonObj(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitJsonObj(this);
			else return visitor.visitChildren(this);
		}
	}

	public final JsonObjContext jsonObj() throws RecognitionException {
		JsonObjContext _localctx = new JsonObjContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_jsonObj);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(487);
			match(CHAR_L_BRACE);
			setState(498);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EXPR || _la==TYPE || ((((_la - 81)) & ~0x3f) == 0 && ((1L << (_la - 81)) & 2145L) != 0)) {
				{
				setState(488);
				jsonPair();
				setState(495);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==EXPR || _la==TYPE || ((((_la - 80)) & ~0x3f) == 0 && ((1L << (_la - 80)) & 4291L) != 0)) {
					{
					{
					setState(490);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==CHAR_COMMA) {
						{
						setState(489);
						match(CHAR_COMMA);
						}
					}

					setState(492);
					jsonPair();
					}
					}
					setState(497);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(500);
			match(CHAR_R_BRACE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class JsonPairContext extends ParserRuleContext {
		public TerminalNode CHAR_COLON() { return getToken(KarinaParser.CHAR_COLON, 0); }
		public JsonValueContext jsonValue() {
			return getRuleContext(JsonValueContext.class,0);
		}
		public TerminalNode STRING_LITERAL() { return getToken(KarinaParser.STRING_LITERAL, 0); }
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public JsonPairContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_jsonPair; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterJsonPair(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitJsonPair(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitJsonPair(this);
			else return visitor.visitChildren(this);
		}
	}

	public final JsonPairContext jsonPair() throws RecognitionException {
		JsonPairContext _localctx = new JsonPairContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_jsonPair);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(504);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STRING_LITERAL:
				{
				setState(502);
				match(STRING_LITERAL);
				}
				break;
			case EXPR:
			case TYPE:
			case CHAR_UNDER:
			case CHAR_ESCAPE:
			case ID:
				{
				setState(503);
				id();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(506);
			match(CHAR_COLON);
			setState(507);
			jsonValue();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class JsonArrayContext extends ParserRuleContext {
		public TerminalNode CHAR_L_BRACKET() { return getToken(KarinaParser.CHAR_L_BRACKET, 0); }
		public TerminalNode CHAR_R_BRACKET() { return getToken(KarinaParser.CHAR_R_BRACKET, 0); }
		public List<JsonValueContext> jsonValue() {
			return getRuleContexts(JsonValueContext.class);
		}
		public JsonValueContext jsonValue(int i) {
			return getRuleContext(JsonValueContext.class,i);
		}
		public List<TerminalNode> CHAR_COMMA() { return getTokens(KarinaParser.CHAR_COMMA); }
		public TerminalNode CHAR_COMMA(int i) {
			return getToken(KarinaParser.CHAR_COMMA, i);
		}
		public JsonArrayContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_jsonArray; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterJsonArray(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitJsonArray(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitJsonArray(this);
			else return visitor.visitChildren(this);
		}
	}

	public final JsonArrayContext jsonArray() throws RecognitionException {
		JsonArrayContext _localctx = new JsonArrayContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_jsonArray);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(509);
			match(CHAR_L_BRACKET);
			setState(520);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 98566L) != 0) || ((((_la - 75)) & ~0x3f) == 0 && ((1L << (_la - 75)) & 20485L) != 0)) {
				{
				setState(510);
				jsonValue();
				setState(517);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 98566L) != 0) || ((((_la - 75)) & ~0x3f) == 0 && ((1L << (_la - 75)) & 20517L) != 0)) {
					{
					{
					setState(512);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==CHAR_COMMA) {
						{
						setState(511);
						match(CHAR_COMMA);
						}
					}

					setState(514);
					jsonValue();
					}
					}
					setState(519);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(522);
			match(CHAR_R_BRACKET);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class JsonExpressionContext extends ParserRuleContext {
		public TerminalNode EXPR() { return getToken(KarinaParser.EXPR, 0); }
		public TerminalNode CHAR_L_BRACE() { return getToken(KarinaParser.CHAR_L_BRACE, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode CHAR_R_BRACE() { return getToken(KarinaParser.CHAR_R_BRACE, 0); }
		public JsonExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_jsonExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterJsonExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitJsonExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitJsonExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final JsonExpressionContext jsonExpression() throws RecognitionException {
		JsonExpressionContext _localctx = new JsonExpressionContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_jsonExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(524);
			match(EXPR);
			setState(525);
			match(CHAR_L_BRACE);
			setState(526);
			expression();
			setState(527);
			match(CHAR_R_BRACE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class JsonTypeContext extends ParserRuleContext {
		public TerminalNode TYPE() { return getToken(KarinaParser.TYPE, 0); }
		public TerminalNode CHAR_L_BRACE() { return getToken(KarinaParser.CHAR_L_BRACE, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode CHAR_R_BRACE() { return getToken(KarinaParser.CHAR_R_BRACE, 0); }
		public JsonTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_jsonType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterJsonType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitJsonType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitJsonType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final JsonTypeContext jsonType() throws RecognitionException {
		JsonTypeContext _localctx = new JsonTypeContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_jsonType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(529);
			match(TYPE);
			setState(530);
			match(CHAR_L_BRACE);
			setState(531);
			type();
			setState(532);
			match(CHAR_R_BRACE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class JsonValueContext extends ParserRuleContext {
		public TerminalNode STRING_LITERAL() { return getToken(KarinaParser.STRING_LITERAL, 0); }
		public TerminalNode NUMBER() { return getToken(KarinaParser.NUMBER, 0); }
		public JsonObjContext jsonObj() {
			return getRuleContext(JsonObjContext.class,0);
		}
		public JsonArrayContext jsonArray() {
			return getRuleContext(JsonArrayContext.class,0);
		}
		public TerminalNode TRUE() { return getToken(KarinaParser.TRUE, 0); }
		public TerminalNode FALSE() { return getToken(KarinaParser.FALSE, 0); }
		public TerminalNode NULL() { return getToken(KarinaParser.NULL, 0); }
		public JsonExpressionContext jsonExpression() {
			return getRuleContext(JsonExpressionContext.class,0);
		}
		public JsonTypeContext jsonType() {
			return getRuleContext(JsonTypeContext.class,0);
		}
		public JsonValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_jsonValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterJsonValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitJsonValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitJsonValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final JsonValueContext jsonValue() throws RecognitionException {
		JsonValueContext _localctx = new JsonValueContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_jsonValue);
		try {
			setState(543);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STRING_LITERAL:
				enterOuterAlt(_localctx, 1);
				{
				setState(534);
				match(STRING_LITERAL);
				}
				break;
			case NUMBER:
				enterOuterAlt(_localctx, 2);
				{
				setState(535);
				match(NUMBER);
				}
				break;
			case CHAR_L_BRACE:
				enterOuterAlt(_localctx, 3);
				{
				setState(536);
				jsonObj();
				}
				break;
			case CHAR_L_BRACKET:
				enterOuterAlt(_localctx, 4);
				{
				setState(537);
				jsonArray();
				}
				break;
			case TRUE:
				enterOuterAlt(_localctx, 5);
				{
				setState(538);
				match(TRUE);
				}
				break;
			case FALSE:
				enterOuterAlt(_localctx, 6);
				{
				setState(539);
				match(FALSE);
				}
				break;
			case NULL:
				enterOuterAlt(_localctx, 7);
				{
				setState(540);
				match(NULL);
				}
				break;
			case EXPR:
				enterOuterAlt(_localctx, 8);
				{
				setState(541);
				jsonExpression();
				}
				break;
			case TYPE:
				enterOuterAlt(_localctx, 9);
				{
				setState(542);
				jsonType();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class BlockContext extends ParserRuleContext {
		public TerminalNode CHAR_L_BRACE() { return getToken(KarinaParser.CHAR_L_BRACE, 0); }
		public TerminalNode CHAR_R_BRACE() { return getToken(KarinaParser.CHAR_R_BRACE, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<TerminalNode> CHAR_SEMICOLON() { return getTokens(KarinaParser.CHAR_SEMICOLON); }
		public TerminalNode CHAR_SEMICOLON(int i) {
			return getToken(KarinaParser.CHAR_SEMICOLON, i);
		}
		public BlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BlockContext block() throws RecognitionException {
		BlockContext _localctx = new BlockContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(545);
			match(CHAR_L_BRACE);
			setState(552);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 2306406127485423630L) != 0) || ((((_la - 69)) & ~0x3f) == 0 && ((1L << (_la - 69)) & 10359059L) != 0)) {
				{
				{
				setState(546);
				expression();
				setState(548);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==CHAR_SEMICOLON) {
					{
					setState(547);
					match(CHAR_SEMICOLON);
					}
				}

				}
				}
				setState(554);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(555);
			match(CHAR_R_BRACE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExprWithBlockContext extends ParserRuleContext {
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ExprWithBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exprWithBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterExprWithBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitExprWithBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitExprWithBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprWithBlockContext exprWithBlock() throws RecognitionException {
		ExprWithBlockContext _localctx = new ExprWithBlockContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_exprWithBlock);
		try {
			setState(559);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CHAR_L_BRACE:
				enterOuterAlt(_localctx, 1);
				{
				setState(557);
				block();
				}
				break;
			case EXPR:
			case TYPE:
			case FN:
			case MATCH:
			case TRUE:
			case FALSE:
			case BREAK:
			case RETURN:
			case RAISE:
			case LET:
			case IF:
			case WHILE:
			case FOR:
			case SUPER:
			case SELF:
			case CONTINUE:
			case CHAR_MINUS:
			case CHAR_SMALLER:
			case CHAR_EXCLAMATION:
			case CHAR_L_PAREN:
			case CHAR_L_BRACKET:
			case CHAR_UNDER:
			case CHAR_ESCAPE:
			case STRING_LITERAL:
			case CHAR_LITERAL:
			case NUMBER:
			case ID:
				enterOuterAlt(_localctx, 2);
				{
				setState(558);
				expression();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExpressionContext extends ParserRuleContext {
		public VarDefContext varDef() {
			return getRuleContext(VarDefContext.class,0);
		}
		public ClosureContext closure() {
			return getRuleContext(ClosureContext.class,0);
		}
		public TerminalNode RETURN() { return getToken(KarinaParser.RETURN, 0); }
		public ExprWithBlockContext exprWithBlock() {
			return getRuleContext(ExprWithBlockContext.class,0);
		}
		public MatchContext match() {
			return getRuleContext(MatchContext.class,0);
		}
		public IfContext if_() {
			return getRuleContext(IfContext.class,0);
		}
		public WhileContext while_() {
			return getRuleContext(WhileContext.class,0);
		}
		public ForContext for_() {
			return getRuleContext(ForContext.class,0);
		}
		public ConditionalOrExpressionContext conditionalOrExpression() {
			return getRuleContext(ConditionalOrExpressionContext.class,0);
		}
		public TerminalNode BREAK() { return getToken(KarinaParser.BREAK, 0); }
		public TerminalNode CONTINUE() { return getToken(KarinaParser.CONTINUE, 0); }
		public ThrowContext throw_() {
			return getRuleContext(ThrowContext.class,0);
		}
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		ExpressionContext _localctx = new ExpressionContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_expression);
		try {
			setState(575);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LET:
				enterOuterAlt(_localctx, 1);
				{
				setState(561);
				varDef();
				}
				break;
			case FN:
				enterOuterAlt(_localctx, 2);
				{
				setState(562);
				closure();
				}
				break;
			case RETURN:
				enterOuterAlt(_localctx, 3);
				{
				setState(563);
				match(RETURN);
				setState(565);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,62,_ctx) ) {
				case 1:
					{
					setState(564);
					exprWithBlock();
					}
					break;
				}
				}
				break;
			case MATCH:
				enterOuterAlt(_localctx, 4);
				{
				setState(567);
				match();
				}
				break;
			case IF:
				enterOuterAlt(_localctx, 5);
				{
				setState(568);
				if_();
				}
				break;
			case WHILE:
				enterOuterAlt(_localctx, 6);
				{
				setState(569);
				while_();
				}
				break;
			case FOR:
				enterOuterAlt(_localctx, 7);
				{
				setState(570);
				for_();
				}
				break;
			case EXPR:
			case TYPE:
			case TRUE:
			case FALSE:
			case SUPER:
			case SELF:
			case CHAR_MINUS:
			case CHAR_SMALLER:
			case CHAR_EXCLAMATION:
			case CHAR_L_PAREN:
			case CHAR_L_BRACKET:
			case CHAR_UNDER:
			case CHAR_ESCAPE:
			case STRING_LITERAL:
			case CHAR_LITERAL:
			case NUMBER:
			case ID:
				enterOuterAlt(_localctx, 8);
				{
				setState(571);
				conditionalOrExpression();
				}
				break;
			case BREAK:
				enterOuterAlt(_localctx, 9);
				{
				setState(572);
				match(BREAK);
				}
				break;
			case CONTINUE:
				enterOuterAlt(_localctx, 10);
				{
				setState(573);
				match(CONTINUE);
				}
				break;
			case RAISE:
				enterOuterAlt(_localctx, 11);
				{
				setState(574);
				throw_();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class VarDefContext extends ParserRuleContext {
		public TerminalNode LET() { return getToken(KarinaParser.LET, 0); }
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public TerminalNode CHAR_EQUAL() { return getToken(KarinaParser.CHAR_EQUAL, 0); }
		public ExprWithBlockContext exprWithBlock() {
			return getRuleContext(ExprWithBlockContext.class,0);
		}
		public TerminalNode CHAR_COLON() { return getToken(KarinaParser.CHAR_COLON, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public VarDefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_varDef; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterVarDef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitVarDef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitVarDef(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VarDefContext varDef() throws RecognitionException {
		VarDefContext _localctx = new VarDefContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_varDef);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(577);
			match(LET);
			setState(578);
			id();
			setState(581);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CHAR_COLON) {
				{
				setState(579);
				match(CHAR_COLON);
				setState(580);
				type();
				}
			}

			setState(583);
			match(CHAR_EQUAL);
			{
			setState(584);
			exprWithBlock();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ClosureContext extends ParserRuleContext {
		public TerminalNode FN() { return getToken(KarinaParser.FN, 0); }
		public TerminalNode CHAR_L_PAREN() { return getToken(KarinaParser.CHAR_L_PAREN, 0); }
		public OptTypeListContext optTypeList() {
			return getRuleContext(OptTypeListContext.class,0);
		}
		public TerminalNode CHAR_R_PAREN() { return getToken(KarinaParser.CHAR_R_PAREN, 0); }
		public ExprWithBlockContext exprWithBlock() {
			return getRuleContext(ExprWithBlockContext.class,0);
		}
		public TerminalNode ARROW_RIGHT() { return getToken(KarinaParser.ARROW_RIGHT, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public InterfaceImplContext interfaceImpl() {
			return getRuleContext(InterfaceImplContext.class,0);
		}
		public ClosureContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_closure; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterClosure(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitClosure(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitClosure(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ClosureContext closure() throws RecognitionException {
		ClosureContext _localctx = new ClosureContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_closure);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(586);
			match(FN);
			setState(587);
			match(CHAR_L_PAREN);
			setState(588);
			optTypeList();
			setState(589);
			match(CHAR_R_PAREN);
			setState(592);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ARROW_RIGHT) {
				{
				setState(590);
				match(ARROW_RIGHT);
				setState(591);
				type();
				}
			}

			setState(595);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IMPL) {
				{
				setState(594);
				interfaceImpl();
				}
			}

			setState(597);
			exprWithBlock();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class InterfaceImplContext extends ParserRuleContext {
		public TerminalNode IMPL() { return getToken(KarinaParser.IMPL, 0); }
		public StructTypeListContext structTypeList() {
			return getRuleContext(StructTypeListContext.class,0);
		}
		public InterfaceImplContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_interfaceImpl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterInterfaceImpl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitInterfaceImpl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitInterfaceImpl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InterfaceImplContext interfaceImpl() throws RecognitionException {
		InterfaceImplContext _localctx = new InterfaceImplContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_interfaceImpl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(599);
			match(IMPL);
			setState(600);
			structTypeList();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class StructTypeListContext extends ParserRuleContext {
		public List<StructTypeContext> structType() {
			return getRuleContexts(StructTypeContext.class);
		}
		public StructTypeContext structType(int i) {
			return getRuleContext(StructTypeContext.class,i);
		}
		public List<TerminalNode> CHAR_COMMA() { return getTokens(KarinaParser.CHAR_COMMA); }
		public TerminalNode CHAR_COMMA(int i) {
			return getToken(KarinaParser.CHAR_COMMA, i);
		}
		public StructTypeListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_structTypeList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterStructTypeList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitStructTypeList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitStructTypeList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StructTypeListContext structTypeList() throws RecognitionException {
		StructTypeListContext _localctx = new StructTypeListContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_structTypeList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(602);
			structType();
			setState(607);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,67,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(603);
					match(CHAR_COMMA);
					setState(604);
					structType();
					}
					} 
				}
				setState(609);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,67,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MatchContext extends ParserRuleContext {
		public TerminalNode MATCH() { return getToken(KarinaParser.MATCH, 0); }
		public ExprWithBlockContext exprWithBlock() {
			return getRuleContext(ExprWithBlockContext.class,0);
		}
		public TerminalNode CHAR_L_BRACE() { return getToken(KarinaParser.CHAR_L_BRACE, 0); }
		public TerminalNode CHAR_R_BRACE() { return getToken(KarinaParser.CHAR_R_BRACE, 0); }
		public List<MatchCaseContext> matchCase() {
			return getRuleContexts(MatchCaseContext.class);
		}
		public MatchCaseContext matchCase(int i) {
			return getRuleContext(MatchCaseContext.class,i);
		}
		public MatchContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_match; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterMatch(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitMatch(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitMatch(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MatchContext match() throws RecognitionException {
		MatchContext _localctx = new MatchContext(_ctx, getState());
		enterRule(_localctx, 88, RULE_match);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(610);
			match(MATCH);
			setState(611);
			exprWithBlock();
			setState(612);
			match(CHAR_L_BRACE);
			setState(616);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==EXPR || _la==TYPE || ((((_la - 81)) & ~0x3f) == 0 && ((1L << (_la - 81)) & 2081L) != 0)) {
				{
				{
				setState(613);
				matchCase();
				}
				}
				setState(618);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(619);
			match(CHAR_R_BRACE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MatchCaseContext extends ParserRuleContext {
		public TerminalNode ARROW_RIGHT() { return getToken(KarinaParser.ARROW_RIGHT, 0); }
		public ExprWithBlockContext exprWithBlock() {
			return getRuleContext(ExprWithBlockContext.class,0);
		}
		public MatchDefaultContext matchDefault() {
			return getRuleContext(MatchDefaultContext.class,0);
		}
		public MatchInstanceContext matchInstance() {
			return getRuleContext(MatchInstanceContext.class,0);
		}
		public MatchCaseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_matchCase; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterMatchCase(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitMatchCase(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitMatchCase(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MatchCaseContext matchCase() throws RecognitionException {
		MatchCaseContext _localctx = new MatchCaseContext(_ctx, getState());
		enterRule(_localctx, 90, RULE_matchCase);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(623);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,69,_ctx) ) {
			case 1:
				{
				setState(621);
				matchDefault();
				}
				break;
			case 2:
				{
				setState(622);
				matchInstance();
				}
				break;
			}
			setState(625);
			match(ARROW_RIGHT);
			setState(626);
			exprWithBlock();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MatchInstanceContext extends ParserRuleContext {
		public StructTypeContext structType() {
			return getRuleContext(StructTypeContext.class,0);
		}
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public TerminalNode CHAR_L_PAREN() { return getToken(KarinaParser.CHAR_L_PAREN, 0); }
		public OptTypeListContext optTypeList() {
			return getRuleContext(OptTypeListContext.class,0);
		}
		public TerminalNode CHAR_R_PAREN() { return getToken(KarinaParser.CHAR_R_PAREN, 0); }
		public MatchInstanceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_matchInstance; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterMatchInstance(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitMatchInstance(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitMatchInstance(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MatchInstanceContext matchInstance() throws RecognitionException {
		MatchInstanceContext _localctx = new MatchInstanceContext(_ctx, getState());
		enterRule(_localctx, 92, RULE_matchInstance);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(628);
			structType();
			setState(634);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case EXPR:
			case TYPE:
			case CHAR_UNDER:
			case CHAR_ESCAPE:
			case ID:
				{
				setState(629);
				id();
				}
				break;
			case CHAR_L_PAREN:
				{
				setState(630);
				match(CHAR_L_PAREN);
				setState(631);
				optTypeList();
				setState(632);
				match(CHAR_R_PAREN);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MatchDefaultContext extends ParserRuleContext {
		public TerminalNode CHAR_UNDER() { return getToken(KarinaParser.CHAR_UNDER, 0); }
		public MatchDefaultContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_matchDefault; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterMatchDefault(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitMatchDefault(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitMatchDefault(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MatchDefaultContext matchDefault() throws RecognitionException {
		MatchDefaultContext _localctx = new MatchDefaultContext(_ctx, getState());
		enterRule(_localctx, 94, RULE_matchDefault);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(636);
			match(CHAR_UNDER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class IfContext extends ParserRuleContext {
		public TerminalNode IF() { return getToken(KarinaParser.IF, 0); }
		public ExprWithBlockContext exprWithBlock() {
			return getRuleContext(ExprWithBlockContext.class,0);
		}
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public TerminalNode CHAR_L_PAREN() { return getToken(KarinaParser.CHAR_L_PAREN, 0); }
		public OptTypeListContext optTypeList() {
			return getRuleContext(OptTypeListContext.class,0);
		}
		public TerminalNode CHAR_R_PAREN() { return getToken(KarinaParser.CHAR_R_PAREN, 0); }
		public ElseExprContext elseExpr() {
			return getRuleContext(ElseExprContext.class,0);
		}
		public IfContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_if; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterIf(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitIf(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitIf(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IfContext if_() throws RecognitionException {
		IfContext _localctx = new IfContext(_ctx, getState());
		enterRule(_localctx, 96, RULE_if);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(638);
			match(IF);
			setState(639);
			exprWithBlock();
			setState(645);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case EXPR:
			case TYPE:
			case CHAR_UNDER:
			case CHAR_ESCAPE:
			case ID:
				{
				setState(640);
				id();
				}
				break;
			case CHAR_L_PAREN:
				{
				setState(641);
				match(CHAR_L_PAREN);
				setState(642);
				optTypeList();
				setState(643);
				match(CHAR_R_PAREN);
				}
				break;
			case CHAR_L_BRACE:
				break;
			default:
				break;
			}
			setState(647);
			block();
			setState(649);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ELSE) {
				{
				setState(648);
				elseExpr();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ElseExprContext extends ParserRuleContext {
		public TerminalNode ELSE() { return getToken(KarinaParser.ELSE, 0); }
		public IfContext if_() {
			return getRuleContext(IfContext.class,0);
		}
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public MatchContext match() {
			return getRuleContext(MatchContext.class,0);
		}
		public IsShortContext isShort() {
			return getRuleContext(IsShortContext.class,0);
		}
		public ElseExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_elseExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterElseExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitElseExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitElseExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ElseExprContext elseExpr() throws RecognitionException {
		ElseExprContext _localctx = new ElseExprContext(_ctx, getState());
		enterRule(_localctx, 98, RULE_elseExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(651);
			match(ELSE);
			setState(653);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IS) {
				{
				setState(652);
				isShort();
				}
			}

			setState(658);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IF:
				{
				setState(655);
				if_();
				}
				break;
			case CHAR_L_BRACE:
				{
				setState(656);
				block();
				}
				break;
			case MATCH:
				{
				setState(657);
				match();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class IsShortContext extends ParserRuleContext {
		public TerminalNode IS() { return getToken(KarinaParser.IS, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public TerminalNode CHAR_L_PAREN() { return getToken(KarinaParser.CHAR_L_PAREN, 0); }
		public OptTypeListContext optTypeList() {
			return getRuleContext(OptTypeListContext.class,0);
		}
		public TerminalNode CHAR_R_PAREN() { return getToken(KarinaParser.CHAR_R_PAREN, 0); }
		public IsShortContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_isShort; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterIsShort(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitIsShort(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitIsShort(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IsShortContext isShort() throws RecognitionException {
		IsShortContext _localctx = new IsShortContext(_ctx, getState());
		enterRule(_localctx, 100, RULE_isShort);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(660);
			match(IS);
			setState(661);
			type();
			setState(667);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case EXPR:
			case TYPE:
			case CHAR_UNDER:
			case CHAR_ESCAPE:
			case ID:
				{
				setState(662);
				id();
				}
				break;
			case CHAR_L_PAREN:
				{
				setState(663);
				match(CHAR_L_PAREN);
				setState(664);
				optTypeList();
				setState(665);
				match(CHAR_R_PAREN);
				}
				break;
			case MATCH:
			case IF:
			case CHAR_L_BRACE:
				break;
			default:
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class WhileContext extends ParserRuleContext {
		public TerminalNode WHILE() { return getToken(KarinaParser.WHILE, 0); }
		public ExprWithBlockContext exprWithBlock() {
			return getRuleContext(ExprWithBlockContext.class,0);
		}
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public WhileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_while; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterWhile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitWhile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitWhile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WhileContext while_() throws RecognitionException {
		WhileContext _localctx = new WhileContext(_ctx, getState());
		enterRule(_localctx, 102, RULE_while);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(669);
			match(WHILE);
			setState(670);
			exprWithBlock();
			setState(671);
			block();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ForContext extends ParserRuleContext {
		public TerminalNode FOR() { return getToken(KarinaParser.FOR, 0); }
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public TerminalNode IN() { return getToken(KarinaParser.IN, 0); }
		public ExprWithBlockContext exprWithBlock() {
			return getRuleContext(ExprWithBlockContext.class,0);
		}
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public ForContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_for; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterFor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitFor(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitFor(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ForContext for_() throws RecognitionException {
		ForContext _localctx = new ForContext(_ctx, getState());
		enterRule(_localctx, 104, RULE_for);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(673);
			match(FOR);
			setState(674);
			id();
			setState(675);
			match(IN);
			setState(676);
			exprWithBlock();
			setState(677);
			block();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ThrowContext extends ParserRuleContext {
		public TerminalNode RAISE() { return getToken(KarinaParser.RAISE, 0); }
		public ExprWithBlockContext exprWithBlock() {
			return getRuleContext(ExprWithBlockContext.class,0);
		}
		public ThrowContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_throw; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterThrow(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitThrow(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitThrow(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ThrowContext throw_() throws RecognitionException {
		ThrowContext _localctx = new ThrowContext(_ctx, getState());
		enterRule(_localctx, 106, RULE_throw);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(679);
			match(RAISE);
			setState(680);
			exprWithBlock();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ConditionalOrExpressionContext extends ParserRuleContext {
		public ConditionalAndExpressionContext conditionalAndExpression() {
			return getRuleContext(ConditionalAndExpressionContext.class,0);
		}
		public TerminalNode OR_OR() { return getToken(KarinaParser.OR_OR, 0); }
		public ConditionalOrExpressionContext conditionalOrExpression() {
			return getRuleContext(ConditionalOrExpressionContext.class,0);
		}
		public ConditionalOrExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conditionalOrExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterConditionalOrExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitConditionalOrExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitConditionalOrExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConditionalOrExpressionContext conditionalOrExpression() throws RecognitionException {
		ConditionalOrExpressionContext _localctx = new ConditionalOrExpressionContext(_ctx, getState());
		enterRule(_localctx, 108, RULE_conditionalOrExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(682);
			conditionalAndExpression();
			setState(685);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,76,_ctx) ) {
			case 1:
				{
				setState(683);
				match(OR_OR);
				setState(684);
				conditionalOrExpression();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ConditionalAndExpressionContext extends ParserRuleContext {
		public EqualityExpressionContext equalityExpression() {
			return getRuleContext(EqualityExpressionContext.class,0);
		}
		public TerminalNode AND_AND() { return getToken(KarinaParser.AND_AND, 0); }
		public ConditionalAndExpressionContext conditionalAndExpression() {
			return getRuleContext(ConditionalAndExpressionContext.class,0);
		}
		public ConditionalAndExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conditionalAndExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterConditionalAndExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitConditionalAndExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitConditionalAndExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConditionalAndExpressionContext conditionalAndExpression() throws RecognitionException {
		ConditionalAndExpressionContext _localctx = new ConditionalAndExpressionContext(_ctx, getState());
		enterRule(_localctx, 110, RULE_conditionalAndExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(687);
			equalityExpression();
			setState(690);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,77,_ctx) ) {
			case 1:
				{
				setState(688);
				match(AND_AND);
				setState(689);
				conditionalAndExpression();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class EqualityExpressionContext extends ParserRuleContext {
		public RelationalExpressionContext relationalExpression() {
			return getRuleContext(RelationalExpressionContext.class,0);
		}
		public EqualityExpressionContext equalityExpression() {
			return getRuleContext(EqualityExpressionContext.class,0);
		}
		public TerminalNode EQUALS() { return getToken(KarinaParser.EQUALS, 0); }
		public TerminalNode NOT_EQUALS() { return getToken(KarinaParser.NOT_EQUALS, 0); }
		public TerminalNode STRICT_EQUALS() { return getToken(KarinaParser.STRICT_EQUALS, 0); }
		public TerminalNode STRICT_NOT_EQUALS() { return getToken(KarinaParser.STRICT_NOT_EQUALS, 0); }
		public EqualityExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_equalityExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterEqualityExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitEqualityExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitEqualityExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EqualityExpressionContext equalityExpression() throws RecognitionException {
		EqualityExpressionContext _localctx = new EqualityExpressionContext(_ctx, getState());
		enterRule(_localctx, 112, RULE_equalityExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(692);
			relationalExpression();
			setState(695);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,78,_ctx) ) {
			case 1:
				{
				setState(693);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 270215977642229760L) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(694);
				equalityExpression();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class RelationalExpressionContext extends ParserRuleContext {
		public AdditiveExpressionContext additiveExpression() {
			return getRuleContext(AdditiveExpressionContext.class,0);
		}
		public RelationalExpressionContext relationalExpression() {
			return getRuleContext(RelationalExpressionContext.class,0);
		}
		public TerminalNode CHAR_SMALLER() { return getToken(KarinaParser.CHAR_SMALLER, 0); }
		public TerminalNode CHAR_GREATER() { return getToken(KarinaParser.CHAR_GREATER, 0); }
		public TerminalNode SMALLER_EQUALS() { return getToken(KarinaParser.SMALLER_EQUALS, 0); }
		public TerminalNode GREATER_EQULAS() { return getToken(KarinaParser.GREATER_EQULAS, 0); }
		public RelationalExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relationalExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterRelationalExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitRelationalExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitRelationalExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RelationalExpressionContext relationalExpression() throws RecognitionException {
		RelationalExpressionContext _localctx = new RelationalExpressionContext(_ctx, getState());
		enterRule(_localctx, 114, RULE_relationalExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(697);
			additiveExpression();
			setState(700);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,79,_ctx) ) {
			case 1:
				{
				setState(698);
				_la = _input.LA(1);
				if ( !(((((_la - 52)) & ~0x3f) == 0 && ((1L << (_la - 52)) & 196611L) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(699);
				relationalExpression();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AdditiveExpressionContext extends ParserRuleContext {
		public MultiplicativeExpressionContext multiplicativeExpression() {
			return getRuleContext(MultiplicativeExpressionContext.class,0);
		}
		public AdditiveExpressionContext additiveExpression() {
			return getRuleContext(AdditiveExpressionContext.class,0);
		}
		public TerminalNode CHAR_PLIS() { return getToken(KarinaParser.CHAR_PLIS, 0); }
		public TerminalNode CHAR_MINUS() { return getToken(KarinaParser.CHAR_MINUS, 0); }
		public TerminalNode CHAR_AND() { return getToken(KarinaParser.CHAR_AND, 0); }
		public AdditiveExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_additiveExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterAdditiveExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitAdditiveExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitAdditiveExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AdditiveExpressionContext additiveExpression() throws RecognitionException {
		AdditiveExpressionContext _localctx = new AdditiveExpressionContext(_ctx, getState());
		enterRule(_localctx, 116, RULE_additiveExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(702);
			multiplicativeExpression();
			setState(705);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,80,_ctx) ) {
			case 1:
				{
				setState(703);
				_la = _input.LA(1);
				if ( !(((((_la - 60)) & ~0x3f) == 0 && ((1L << (_la - 60)) & 4194307L) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(704);
				additiveExpression();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MultiplicativeExpressionContext extends ParserRuleContext {
		public UnaryExpressionContext unaryExpression() {
			return getRuleContext(UnaryExpressionContext.class,0);
		}
		public MultiplicativeExpressionContext multiplicativeExpression() {
			return getRuleContext(MultiplicativeExpressionContext.class,0);
		}
		public TerminalNode CHAR_STAR() { return getToken(KarinaParser.CHAR_STAR, 0); }
		public TerminalNode CHAR_R_SLASH() { return getToken(KarinaParser.CHAR_R_SLASH, 0); }
		public TerminalNode CHAR_PERCENT() { return getToken(KarinaParser.CHAR_PERCENT, 0); }
		public MultiplicativeExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_multiplicativeExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterMultiplicativeExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitMultiplicativeExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitMultiplicativeExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MultiplicativeExpressionContext multiplicativeExpression() throws RecognitionException {
		MultiplicativeExpressionContext _localctx = new MultiplicativeExpressionContext(_ctx, getState());
		enterRule(_localctx, 118, RULE_multiplicativeExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(707);
			unaryExpression();
			setState(710);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,81,_ctx) ) {
			case 1:
				{
				setState(708);
				_la = _input.LA(1);
				if ( !(((((_la - 62)) & ~0x3f) == 0 && ((1L << (_la - 62)) & 7L) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(709);
				multiplicativeExpression();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class UnaryExpressionContext extends ParserRuleContext {
		public FactorContext factor() {
			return getRuleContext(FactorContext.class,0);
		}
		public TerminalNode CHAR_MINUS() { return getToken(KarinaParser.CHAR_MINUS, 0); }
		public TerminalNode CHAR_EXCLAMATION() { return getToken(KarinaParser.CHAR_EXCLAMATION, 0); }
		public UnaryExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unaryExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterUnaryExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitUnaryExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitUnaryExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UnaryExpressionContext unaryExpression() throws RecognitionException {
		UnaryExpressionContext _localctx = new UnaryExpressionContext(_ctx, getState());
		enterRule(_localctx, 120, RULE_unaryExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(713);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CHAR_MINUS || _la==CHAR_EXCLAMATION) {
				{
				setState(712);
				_la = _input.LA(1);
				if ( !(_la==CHAR_MINUS || _la==CHAR_EXCLAMATION) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
			}

			setState(715);
			factor();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FactorContext extends ParserRuleContext {
		public ObjectContext object() {
			return getRuleContext(ObjectContext.class,0);
		}
		public List<PostFixContext> postFix() {
			return getRuleContexts(PostFixContext.class);
		}
		public PostFixContext postFix(int i) {
			return getRuleContext(PostFixContext.class,i);
		}
		public IsInstanceOfContext isInstanceOf() {
			return getRuleContext(IsInstanceOfContext.class,0);
		}
		public TerminalNode CHAR_EQUAL() { return getToken(KarinaParser.CHAR_EQUAL, 0); }
		public ExprWithBlockContext exprWithBlock() {
			return getRuleContext(ExprWithBlockContext.class,0);
		}
		public FactorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_factor; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterFactor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitFactor(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitFactor(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FactorContext factor() throws RecognitionException {
		FactorContext _localctx = new FactorContext(_ctx, getState());
		enterRule(_localctx, 122, RULE_factor);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(717);
			object();
			setState(721);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,83,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(718);
					postFix();
					}
					} 
				}
				setState(723);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,83,_ctx);
			}
			setState(727);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CHAR_EQUAL:
				{
				{
				setState(724);
				match(CHAR_EQUAL);
				setState(725);
				exprWithBlock();
				}
				}
				break;
			case IS:
				{
				setState(726);
				isInstanceOf();
				}
				break;
			case EOF:
			case EXPR:
			case TYPE:
			case FN:
			case MATCH:
			case TRUE:
			case FALSE:
			case BREAK:
			case RETURN:
			case STRUCT:
			case RAISE:
			case IMPL:
			case ENUM:
			case LET:
			case IF:
			case WHILE:
			case FOR:
			case SUPER:
			case WHERE:
			case INTERFACE:
			case SELF:
			case CONTINUE:
			case GREATER_EQULAS:
			case SMALLER_EQUALS:
			case EQUALS:
			case STRICT_EQUALS:
			case STRICT_NOT_EQUALS:
			case NOT_EQUALS:
			case AND_AND:
			case OR_OR:
			case CHAR_PLIS:
			case CHAR_MINUS:
			case CHAR_STAR:
			case CHAR_R_SLASH:
			case CHAR_PERCENT:
			case CHAR_GREATER:
			case CHAR_SMALLER:
			case CHAR_EXCLAMATION:
			case CHAR_L_PAREN:
			case CHAR_R_PAREN:
			case CHAR_L_BRACE:
			case CHAR_R_BRACE:
			case CHAR_L_BRACKET:
			case CHAR_R_BRACKET:
			case CHAR_AT:
			case CHAR_COMMA:
			case CHAR_UNDER:
			case CHAR_AND:
			case CHAR_SEMICOLON:
			case CHAR_ESCAPE:
			case STRING_LITERAL:
			case CHAR_LITERAL:
			case NUMBER:
			case ID:
				break;
			default:
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class PostFixContext extends ParserRuleContext {
		public TerminalNode CHAR_DOT() { return getToken(KarinaParser.CHAR_DOT, 0); }
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public TerminalNode CLASS() { return getToken(KarinaParser.CLASS, 0); }
		public TerminalNode CHAR_L_PAREN() { return getToken(KarinaParser.CHAR_L_PAREN, 0); }
		public ExpressionListContext expressionList() {
			return getRuleContext(ExpressionListContext.class,0);
		}
		public TerminalNode CHAR_R_PAREN() { return getToken(KarinaParser.CHAR_R_PAREN, 0); }
		public GenericHintContext genericHint() {
			return getRuleContext(GenericHintContext.class,0);
		}
		public TerminalNode CHAR_L_BRACKET() { return getToken(KarinaParser.CHAR_L_BRACKET, 0); }
		public ExprWithBlockContext exprWithBlock() {
			return getRuleContext(ExprWithBlockContext.class,0);
		}
		public TerminalNode CHAR_R_BRACKET() { return getToken(KarinaParser.CHAR_R_BRACKET, 0); }
		public TerminalNode AS() { return getToken(KarinaParser.AS, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public PostFixContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_postFix; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterPostFix(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitPostFix(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitPostFix(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PostFixContext postFix() throws RecognitionException {
		PostFixContext _localctx = new PostFixContext(_ctx, getState());
		enterRule(_localctx, 124, RULE_postFix);
		int _la;
		try {
			setState(746);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,86,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(729);
				match(CHAR_DOT);
				setState(730);
				id();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(731);
				match(CHAR_DOT);
				setState(732);
				match(CLASS);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(734);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==CHAR_SMALLER) {
					{
					setState(733);
					genericHint();
					}
				}

				setState(736);
				match(CHAR_L_PAREN);
				setState(737);
				expressionList();
				setState(738);
				match(CHAR_R_PAREN);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(740);
				match(CHAR_L_BRACKET);
				setState(741);
				exprWithBlock();
				setState(742);
				match(CHAR_R_BRACKET);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(744);
				match(AS);
				setState(745);
				type();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ObjectContext extends ParserRuleContext {
		public ArrayContext array() {
			return getRuleContext(ArrayContext.class,0);
		}
		public TerminalNode CHAR_L_PAREN() { return getToken(KarinaParser.CHAR_L_PAREN, 0); }
		public ExprWithBlockContext exprWithBlock() {
			return getRuleContext(ExprWithBlockContext.class,0);
		}
		public TerminalNode CHAR_R_PAREN() { return getToken(KarinaParser.CHAR_R_PAREN, 0); }
		public TerminalNode NUMBER() { return getToken(KarinaParser.NUMBER, 0); }
		public List<IdContext> id() {
			return getRuleContexts(IdContext.class);
		}
		public IdContext id(int i) {
			return getRuleContext(IdContext.class,i);
		}
		public TerminalNode CHAR_L_BRACE() { return getToken(KarinaParser.CHAR_L_BRACE, 0); }
		public InitListContext initList() {
			return getRuleContext(InitListContext.class,0);
		}
		public TerminalNode CHAR_R_BRACE() { return getToken(KarinaParser.CHAR_R_BRACE, 0); }
		public List<TerminalNode> CHAR_DOT() { return getTokens(KarinaParser.CHAR_DOT); }
		public TerminalNode CHAR_DOT(int i) {
			return getToken(KarinaParser.CHAR_DOT, i);
		}
		public GenericHintContext genericHint() {
			return getRuleContext(GenericHintContext.class,0);
		}
		public TerminalNode STRING_LITERAL() { return getToken(KarinaParser.STRING_LITERAL, 0); }
		public TerminalNode CHAR_LITERAL() { return getToken(KarinaParser.CHAR_LITERAL, 0); }
		public TerminalNode SELF() { return getToken(KarinaParser.SELF, 0); }
		public TerminalNode SUPER() { return getToken(KarinaParser.SUPER, 0); }
		public TerminalNode TRUE() { return getToken(KarinaParser.TRUE, 0); }
		public TerminalNode FALSE() { return getToken(KarinaParser.FALSE, 0); }
		public ObjectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_object; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterObject(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitObject(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitObject(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ObjectContext object() throws RecognitionException {
		ObjectContext _localctx = new ObjectContext(_ctx, getState());
		enterRule(_localctx, 126, RULE_object);
		int _la;
		try {
			setState(777);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CHAR_SMALLER:
			case CHAR_L_BRACKET:
				enterOuterAlt(_localctx, 1);
				{
				setState(748);
				array();
				}
				break;
			case CHAR_L_PAREN:
				enterOuterAlt(_localctx, 2);
				{
				setState(749);
				match(CHAR_L_PAREN);
				setState(750);
				exprWithBlock();
				setState(751);
				match(CHAR_R_PAREN);
				}
				break;
			case NUMBER:
				enterOuterAlt(_localctx, 3);
				{
				setState(753);
				match(NUMBER);
				}
				break;
			case EXPR:
			case TYPE:
			case CHAR_UNDER:
			case CHAR_ESCAPE:
			case ID:
				enterOuterAlt(_localctx, 4);
				{
				setState(754);
				id();
				setState(769);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,89,_ctx) ) {
				case 1:
					{
					setState(759);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==CHAR_DOT) {
						{
						{
						setState(755);
						match(CHAR_DOT);
						setState(756);
						id();
						}
						}
						setState(761);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(763);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==CHAR_SMALLER) {
						{
						setState(762);
						genericHint();
						}
					}

					setState(765);
					match(CHAR_L_BRACE);
					setState(766);
					initList();
					setState(767);
					match(CHAR_R_BRACE);
					}
					break;
				}
				}
				break;
			case STRING_LITERAL:
				enterOuterAlt(_localctx, 5);
				{
				setState(771);
				match(STRING_LITERAL);
				}
				break;
			case CHAR_LITERAL:
				enterOuterAlt(_localctx, 6);
				{
				setState(772);
				match(CHAR_LITERAL);
				}
				break;
			case SELF:
				enterOuterAlt(_localctx, 7);
				{
				setState(773);
				match(SELF);
				}
				break;
			case SUPER:
				enterOuterAlt(_localctx, 8);
				{
				setState(774);
				match(SUPER);
				}
				break;
			case TRUE:
				enterOuterAlt(_localctx, 9);
				{
				setState(775);
				match(TRUE);
				}
				break;
			case FALSE:
				enterOuterAlt(_localctx, 10);
				{
				setState(776);
				match(FALSE);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ArrayContext extends ParserRuleContext {
		public TerminalNode CHAR_L_BRACKET() { return getToken(KarinaParser.CHAR_L_BRACKET, 0); }
		public ExpressionListContext expressionList() {
			return getRuleContext(ExpressionListContext.class,0);
		}
		public TerminalNode CHAR_R_BRACKET() { return getToken(KarinaParser.CHAR_R_BRACKET, 0); }
		public TerminalNode CHAR_SMALLER() { return getToken(KarinaParser.CHAR_SMALLER, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode CHAR_GREATER() { return getToken(KarinaParser.CHAR_GREATER, 0); }
		public ArrayContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_array; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterArray(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitArray(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitArray(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArrayContext array() throws RecognitionException {
		ArrayContext _localctx = new ArrayContext(_ctx, getState());
		enterRule(_localctx, 128, RULE_array);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(783);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CHAR_SMALLER) {
				{
				setState(779);
				match(CHAR_SMALLER);
				setState(780);
				type();
				setState(781);
				match(CHAR_GREATER);
				}
			}

			setState(785);
			match(CHAR_L_BRACKET);
			setState(786);
			expressionList();
			setState(787);
			match(CHAR_R_BRACKET);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExpressionListContext extends ParserRuleContext {
		public List<ExprWithBlockContext> exprWithBlock() {
			return getRuleContexts(ExprWithBlockContext.class);
		}
		public ExprWithBlockContext exprWithBlock(int i) {
			return getRuleContext(ExprWithBlockContext.class,i);
		}
		public List<TerminalNode> CHAR_COMMA() { return getTokens(KarinaParser.CHAR_COMMA); }
		public TerminalNode CHAR_COMMA(int i) {
			return getToken(KarinaParser.CHAR_COMMA, i);
		}
		public ExpressionListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressionList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterExpressionList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitExpressionList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitExpressionList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionListContext expressionList() throws RecognitionException {
		ExpressionListContext _localctx = new ExpressionListContext(_ctx, getState());
		enterRule(_localctx, 130, RULE_expressionList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(797);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 2306406127485423630L) != 0) || ((((_la - 69)) & ~0x3f) == 0 && ((1L << (_la - 69)) & 10359123L) != 0)) {
				{
				setState(789);
				exprWithBlock();
				setState(794);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==CHAR_COMMA) {
					{
					{
					setState(790);
					match(CHAR_COMMA);
					setState(791);
					exprWithBlock();
					}
					}
					setState(796);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class InitListContext extends ParserRuleContext {
		public List<MemberInitContext> memberInit() {
			return getRuleContexts(MemberInitContext.class);
		}
		public MemberInitContext memberInit(int i) {
			return getRuleContext(MemberInitContext.class,i);
		}
		public List<TerminalNode> CHAR_COMMA() { return getTokens(KarinaParser.CHAR_COMMA); }
		public TerminalNode CHAR_COMMA(int i) {
			return getToken(KarinaParser.CHAR_COMMA, i);
		}
		public InitListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_initList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterInitList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitInitList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitInitList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InitListContext initList() throws RecognitionException {
		InitListContext _localctx = new InitListContext(_ctx, getState());
		enterRule(_localctx, 132, RULE_initList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(807);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EXPR || _la==TYPE || ((((_la - 81)) & ~0x3f) == 0 && ((1L << (_la - 81)) & 2081L) != 0)) {
				{
				setState(799);
				memberInit();
				setState(804);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==CHAR_COMMA) {
					{
					{
					setState(800);
					match(CHAR_COMMA);
					setState(801);
					memberInit();
					}
					}
					setState(806);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MemberInitContext extends ParserRuleContext {
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public TerminalNode CHAR_COLON() { return getToken(KarinaParser.CHAR_COLON, 0); }
		public ExprWithBlockContext exprWithBlock() {
			return getRuleContext(ExprWithBlockContext.class,0);
		}
		public MemberInitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_memberInit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterMemberInit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitMemberInit(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitMemberInit(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MemberInitContext memberInit() throws RecognitionException {
		MemberInitContext _localctx = new MemberInitContext(_ctx, getState());
		enterRule(_localctx, 134, RULE_memberInit);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(809);
			id();
			setState(810);
			match(CHAR_COLON);
			setState(811);
			exprWithBlock();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class IsInstanceOfContext extends ParserRuleContext {
		public TerminalNode IS() { return getToken(KarinaParser.IS, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public IsInstanceOfContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_isInstanceOf; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterIsInstanceOf(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitIsInstanceOf(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitIsInstanceOf(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IsInstanceOfContext isInstanceOf() throws RecognitionException {
		IsInstanceOfContext _localctx = new IsInstanceOfContext(_ctx, getState());
		enterRule(_localctx, 136, RULE_isInstanceOf);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(813);
			match(IS);
			setState(814);
			type();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class OptTypeListContext extends ParserRuleContext {
		public List<OptTypeNameContext> optTypeName() {
			return getRuleContexts(OptTypeNameContext.class);
		}
		public OptTypeNameContext optTypeName(int i) {
			return getRuleContext(OptTypeNameContext.class,i);
		}
		public List<TerminalNode> CHAR_COMMA() { return getTokens(KarinaParser.CHAR_COMMA); }
		public TerminalNode CHAR_COMMA(int i) {
			return getToken(KarinaParser.CHAR_COMMA, i);
		}
		public OptTypeListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_optTypeList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterOptTypeList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitOptTypeList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitOptTypeList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OptTypeListContext optTypeList() throws RecognitionException {
		OptTypeListContext _localctx = new OptTypeListContext(_ctx, getState());
		enterRule(_localctx, 138, RULE_optTypeList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(824);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EXPR || _la==TYPE || ((((_la - 81)) & ~0x3f) == 0 && ((1L << (_la - 81)) & 2081L) != 0)) {
				{
				setState(816);
				optTypeName();
				setState(821);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==CHAR_COMMA) {
					{
					{
					setState(817);
					match(CHAR_COMMA);
					setState(818);
					optTypeName();
					}
					}
					setState(823);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class OptTypeNameContext extends ParserRuleContext {
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public TerminalNode CHAR_COLON() { return getToken(KarinaParser.CHAR_COLON, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public OptTypeNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_optTypeName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterOptTypeName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitOptTypeName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitOptTypeName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OptTypeNameContext optTypeName() throws RecognitionException {
		OptTypeNameContext _localctx = new OptTypeNameContext(_ctx, getState());
		enterRule(_localctx, 140, RULE_optTypeName);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(826);
			id();
			setState(829);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CHAR_COLON) {
				{
				setState(827);
				match(CHAR_COLON);
				setState(828);
				type();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class IdContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(KarinaParser.ID, 0); }
		public TerminalNode EXPR() { return getToken(KarinaParser.EXPR, 0); }
		public TerminalNode TYPE() { return getToken(KarinaParser.TYPE, 0); }
		public TerminalNode CHAR_ESCAPE() { return getToken(KarinaParser.CHAR_ESCAPE, 0); }
		public EscapedContext escaped() {
			return getRuleContext(EscapedContext.class,0);
		}
		public TerminalNode CHAR_UNDER() { return getToken(KarinaParser.CHAR_UNDER, 0); }
		public IdContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_id; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterId(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitId(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitId(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IdContext id() throws RecognitionException {
		IdContext _localctx = new IdContext(_ctx, getState());
		enterRule(_localctx, 142, RULE_id);
		try {
			setState(837);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ID:
				enterOuterAlt(_localctx, 1);
				{
				setState(831);
				match(ID);
				}
				break;
			case EXPR:
				enterOuterAlt(_localctx, 2);
				{
				setState(832);
				match(EXPR);
				}
				break;
			case TYPE:
				enterOuterAlt(_localctx, 3);
				{
				setState(833);
				match(TYPE);
				}
				break;
			case CHAR_ESCAPE:
				enterOuterAlt(_localctx, 4);
				{
				setState(834);
				match(CHAR_ESCAPE);
				setState(835);
				escaped();
				}
				break;
			case CHAR_UNDER:
				enterOuterAlt(_localctx, 5);
				{
				setState(836);
				match(CHAR_UNDER);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class EscapedContext extends ParserRuleContext {
		public TerminalNode FN() { return getToken(KarinaParser.FN, 0); }
		public TerminalNode IS() { return getToken(KarinaParser.IS, 0); }
		public TerminalNode IN() { return getToken(KarinaParser.IN, 0); }
		public TerminalNode AS() { return getToken(KarinaParser.AS, 0); }
		public TerminalNode OF() { return getToken(KarinaParser.OF, 0); }
		public TerminalNode EXTEND() { return getToken(KarinaParser.EXTEND, 0); }
		public TerminalNode MATCH() { return getToken(KarinaParser.MATCH, 0); }
		public TerminalNode OVERRIDE() { return getToken(KarinaParser.OVERRIDE, 0); }
		public TerminalNode VIRTUAL() { return getToken(KarinaParser.VIRTUAL, 0); }
		public TerminalNode YIELD() { return getToken(KarinaParser.YIELD, 0); }
		public TerminalNode STRUCT() { return getToken(KarinaParser.STRUCT, 0); }
		public TerminalNode RAISE() { return getToken(KarinaParser.RAISE, 0); }
		public TerminalNode TRAIT() { return getToken(KarinaParser.TRAIT, 0); }
		public TerminalNode IMPL() { return getToken(KarinaParser.IMPL, 0); }
		public TerminalNode LET() { return getToken(KarinaParser.LET, 0); }
		public TerminalNode MATCHES() { return getToken(KarinaParser.MATCHES, 0); }
		public TerminalNode SELF() { return getToken(KarinaParser.SELF, 0); }
		public TerminalNode STRING() { return getToken(KarinaParser.STRING, 0); }
		public TerminalNode JSON() { return getToken(KarinaParser.JSON, 0); }
		public TerminalNode BOOL() { return getToken(KarinaParser.BOOL, 0); }
		public TerminalNode WHERE() { return getToken(KarinaParser.WHERE, 0); }
		public EscapedContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_escaped; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterEscaped(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitEscaped(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitEscaped(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EscapedContext escaped() throws RecognitionException {
		EscapedContext _localctx = new EscapedContext(_ctx, getState());
		enterRule(_localctx, 144, RULE_escaped);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(839);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 369610183555320L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\u0004\u0001_\u034a\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b"+
		"\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007\u001e"+
		"\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0002\"\u0007\"\u0002"+
		"#\u0007#\u0002$\u0007$\u0002%\u0007%\u0002&\u0007&\u0002\'\u0007\'\u0002"+
		"(\u0007(\u0002)\u0007)\u0002*\u0007*\u0002+\u0007+\u0002,\u0007,\u0002"+
		"-\u0007-\u0002.\u0007.\u0002/\u0007/\u00020\u00070\u00021\u00071\u0002"+
		"2\u00072\u00023\u00073\u00024\u00074\u00025\u00075\u00026\u00076\u0002"+
		"7\u00077\u00028\u00078\u00029\u00079\u0002:\u0007:\u0002;\u0007;\u0002"+
		"<\u0007<\u0002=\u0007=\u0002>\u0007>\u0002?\u0007?\u0002@\u0007@\u0002"+
		"A\u0007A\u0002B\u0007B\u0002C\u0007C\u0002D\u0007D\u0002E\u0007E\u0002"+
		"F\u0007F\u0002G\u0007G\u0002H\u0007H\u0001\u0000\u0005\u0000\u0094\b\u0000"+
		"\n\u0000\f\u0000\u0097\t\u0000\u0001\u0000\u0005\u0000\u009a\b\u0000\n"+
		"\u0000\f\u0000\u009d\t\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0003\u0001\u00a9\b\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0005"+
		"\u0002\u00ae\b\u0002\n\u0002\f\u0002\u00b1\t\u0002\u0001\u0003\u0005\u0003"+
		"\u00b4\b\u0003\n\u0003\f\u0003\u00b7\t\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0003\u0003\u00bd\b\u0003\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0003\u0004\u00c2\b\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0003\u0004\u00c9\b\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0003\u0004\u00ce\b\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0003"+
		"\u0005\u00d3\b\u0005\u0001\u0005\u0001\u0005\u0005\u0005\u00d7\b\u0005"+
		"\n\u0005\f\u0005\u00da\t\u0005\u0001\u0005\u0005\u0005\u00dd\b\u0005\n"+
		"\u0005\f\u0005\u00e0\t\u0005\u0001\u0005\u0005\u0005\u00e3\b\u0005\n\u0005"+
		"\f\u0005\u00e6\t\u0005\u0001\u0005\u0005\u0005\u00e9\b\u0005\n\u0005\f"+
		"\u0005\u00ec\t\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0005\u0006\u00f4\b\u0006\n\u0006\f\u0006\u00f7\t\u0006"+
		"\u0001\u0006\u0003\u0006\u00fa\b\u0006\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0003\u0007\u0102\b\u0007\u0001\u0007"+
		"\u0001\u0007\u0005\u0007\u0106\b\u0007\n\u0007\f\u0007\u0109\t\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0005\b\u0110\b\b\n\b\f\b\u0113"+
		"\t\b\u0003\b\u0115\b\b\u0001\t\u0001\t\u0003\t\u0119\b\t\u0001\n\u0001"+
		"\n\u0001\n\u0001\n\u0005\n\u011f\b\n\n\n\f\n\u0122\t\n\u0003\n\u0124\b"+
		"\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000b\u012a\b"+
		"\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\r\u0001\r\u0001\r\u0003\r"+
		"\u0133\b\r\u0001\r\u0001\r\u0005\r\u0137\b\r\n\r\f\r\u013a\t\r\u0001\r"+
		"\u0005\r\u013d\b\r\n\r\f\r\u0140\t\r\u0001\r\u0005\r\u0143\b\r\n\r\f\r"+
		"\u0146\t\r\u0001\r\u0005\r\u0149\b\r\n\r\f\r\u014c\t\r\u0001\r\u0001\r"+
		"\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0003\u000e"+
		"\u0155\b\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0003\u000f\u015a\b"+
		"\u000f\u0001\u000f\u0001\u000f\u0005\u000f\u015e\b\u000f\n\u000f\f\u000f"+
		"\u0161\t\u000f\u0001\u000f\u0005\u000f\u0164\b\u000f\n\u000f\f\u000f\u0167"+
		"\t\u000f\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0001"+
		"\u0011\u0001\u0011\u0003\u0011\u0170\b\u0011\u0001\u0011\u0001\u0011\u0005"+
		"\u0011\u0174\b\u0011\n\u0011\f\u0011\u0177\t\u0011\u0003\u0011\u0179\b"+
		"\u0011\u0001\u0012\u0001\u0012\u0001\u0012\u0005\u0012\u017e\b\u0012\n"+
		"\u0012\f\u0012\u0181\t\u0012\u0003\u0012\u0183\b\u0012\u0001\u0013\u0001"+
		"\u0013\u0001\u0013\u0001\u0013\u0001\u0014\u0001\u0014\u0003\u0014\u018b"+
		"\b\u0014\u0001\u0015\u0001\u0015\u0001\u0016\u0001\u0016\u0001\u0016\u0001"+
		"\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001"+
		"\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001"+
		"\u0016\u0001\u0016\u0001\u0016\u0003\u0016\u01a1\b\u0016\u0001\u0017\u0001"+
		"\u0017\u0003\u0017\u01a5\b\u0017\u0001\u0018\u0001\u0018\u0001\u0018\u0001"+
		"\u0018\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001"+
		"\u0019\u0003\u0019\u01b1\b\u0019\u0001\u0019\u0003\u0019\u01b4\b\u0019"+
		"\u0001\u001a\u0001\u001a\u0001\u001a\u0005\u001a\u01b9\b\u001a\n\u001a"+
		"\f\u001a\u01bc\t\u001a\u0003\u001a\u01be\b\u001a\u0001\u001b\u0001\u001b"+
		"\u0001\u001b\u0001\u001b\u0005\u001b\u01c4\b\u001b\n\u001b\f\u001b\u01c7"+
		"\t\u001b\u0003\u001b\u01c9\b\u001b\u0001\u001b\u0001\u001b\u0001\u001c"+
		"\u0001\u001c\u0001\u001c\u0001\u001c\u0005\u001c\u01d1\b\u001c\n\u001c"+
		"\f\u001c\u01d4\t\u001c\u0003\u001c\u01d6\b\u001c\u0001\u001c\u0001\u001c"+
		"\u0001\u001d\u0001\u001d\u0001\u001d\u0005\u001d\u01dd\b\u001d\n\u001d"+
		"\f\u001d\u01e0\t\u001d\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e"+
		"\u0003\u001e\u01e6\b\u001e\u0001\u001f\u0001\u001f\u0001\u001f\u0003\u001f"+
		"\u01eb\b\u001f\u0001\u001f\u0005\u001f\u01ee\b\u001f\n\u001f\f\u001f\u01f1"+
		"\t\u001f\u0003\u001f\u01f3\b\u001f\u0001\u001f\u0001\u001f\u0001 \u0001"+
		" \u0003 \u01f9\b \u0001 \u0001 \u0001 \u0001!\u0001!\u0001!\u0003!\u0201"+
		"\b!\u0001!\u0005!\u0204\b!\n!\f!\u0207\t!\u0003!\u0209\b!\u0001!\u0001"+
		"!\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001#\u0001#\u0001#\u0001#"+
		"\u0001#\u0001$\u0001$\u0001$\u0001$\u0001$\u0001$\u0001$\u0001$\u0001"+
		"$\u0003$\u0220\b$\u0001%\u0001%\u0001%\u0003%\u0225\b%\u0005%\u0227\b"+
		"%\n%\f%\u022a\t%\u0001%\u0001%\u0001&\u0001&\u0003&\u0230\b&\u0001\'\u0001"+
		"\'\u0001\'\u0001\'\u0003\'\u0236\b\'\u0001\'\u0001\'\u0001\'\u0001\'\u0001"+
		"\'\u0001\'\u0001\'\u0001\'\u0003\'\u0240\b\'\u0001(\u0001(\u0001(\u0001"+
		"(\u0003(\u0246\b(\u0001(\u0001(\u0001(\u0001)\u0001)\u0001)\u0001)\u0001"+
		")\u0001)\u0003)\u0251\b)\u0001)\u0003)\u0254\b)\u0001)\u0001)\u0001*\u0001"+
		"*\u0001*\u0001+\u0001+\u0001+\u0005+\u025e\b+\n+\f+\u0261\t+\u0001,\u0001"+
		",\u0001,\u0001,\u0005,\u0267\b,\n,\f,\u026a\t,\u0001,\u0001,\u0001-\u0001"+
		"-\u0003-\u0270\b-\u0001-\u0001-\u0001-\u0001.\u0001.\u0001.\u0001.\u0001"+
		".\u0001.\u0003.\u027b\b.\u0001/\u0001/\u00010\u00010\u00010\u00010\u0001"+
		"0\u00010\u00010\u00030\u0286\b0\u00010\u00010\u00030\u028a\b0\u00011\u0001"+
		"1\u00031\u028e\b1\u00011\u00011\u00011\u00031\u0293\b1\u00012\u00012\u0001"+
		"2\u00012\u00012\u00012\u00012\u00032\u029c\b2\u00013\u00013\u00013\u0001"+
		"3\u00014\u00014\u00014\u00014\u00014\u00014\u00015\u00015\u00015\u0001"+
		"6\u00016\u00016\u00036\u02ae\b6\u00017\u00017\u00017\u00037\u02b3\b7\u0001"+
		"8\u00018\u00018\u00038\u02b8\b8\u00019\u00019\u00019\u00039\u02bd\b9\u0001"+
		":\u0001:\u0001:\u0003:\u02c2\b:\u0001;\u0001;\u0001;\u0003;\u02c7\b;\u0001"+
		"<\u0003<\u02ca\b<\u0001<\u0001<\u0001=\u0001=\u0005=\u02d0\b=\n=\f=\u02d3"+
		"\t=\u0001=\u0001=\u0001=\u0003=\u02d8\b=\u0001>\u0001>\u0001>\u0001>\u0001"+
		">\u0003>\u02df\b>\u0001>\u0001>\u0001>\u0001>\u0001>\u0001>\u0001>\u0001"+
		">\u0001>\u0001>\u0003>\u02eb\b>\u0001?\u0001?\u0001?\u0001?\u0001?\u0001"+
		"?\u0001?\u0001?\u0001?\u0005?\u02f6\b?\n?\f?\u02f9\t?\u0001?\u0003?\u02fc"+
		"\b?\u0001?\u0001?\u0001?\u0001?\u0003?\u0302\b?\u0001?\u0001?\u0001?\u0001"+
		"?\u0001?\u0001?\u0003?\u030a\b?\u0001@\u0001@\u0001@\u0001@\u0003@\u0310"+
		"\b@\u0001@\u0001@\u0001@\u0001@\u0001A\u0001A\u0001A\u0005A\u0319\bA\n"+
		"A\fA\u031c\tA\u0003A\u031e\bA\u0001B\u0001B\u0001B\u0005B\u0323\bB\nB"+
		"\fB\u0326\tB\u0003B\u0328\bB\u0001C\u0001C\u0001C\u0001C\u0001D\u0001"+
		"D\u0001D\u0001E\u0001E\u0001E\u0005E\u0334\bE\nE\fE\u0337\tE\u0003E\u0339"+
		"\bE\u0001F\u0001F\u0001F\u0003F\u033e\bF\u0001G\u0001G\u0001G\u0001G\u0001"+
		"G\u0001G\u0003G\u0346\bG\u0001H\u0001H\u0001H\u0000\u0000I\u0000\u0002"+
		"\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e"+
		" \"$&(*,.02468:<>@BDFHJLNPRTVXZ\\^`bdfhjlnprtvxz|~\u0080\u0082\u0084\u0086"+
		"\u0088\u008a\u008c\u008e\u0090\u0000\u0006\u0001\u000069\u0002\u00004"+
		"5DE\u0002\u0000<=RR\u0001\u0000>@\u0002\u0000==FF\f\u0000\u0003\u0007"+
		"\u000b\r\u0011\u0011\u0014\u0015\u0017\u0019\u001c\u001c\u001f\u001f#"+
		"#%%,,..00\u0398\u0000\u0095\u0001\u0000\u0000\u0000\u0002\u00a0\u0001"+
		"\u0000\u0000\u0000\u0004\u00aa\u0001\u0000\u0000\u0000\u0006\u00b5\u0001"+
		"\u0000\u0000\u0000\b\u00be\u0001\u0000\u0000\u0000\n\u00cf\u0001\u0000"+
		"\u0000\u0000\f\u00ef\u0001\u0000\u0000\u0000\u000e\u00fb\u0001\u0000\u0000"+
		"\u0000\u0010\u0114\u0001\u0000\u0000\u0000\u0012\u0116\u0001\u0000\u0000"+
		"\u0000\u0014\u011a\u0001\u0000\u0000\u0000\u0016\u0129\u0001\u0000\u0000"+
		"\u0000\u0018\u012b\u0001\u0000\u0000\u0000\u001a\u012f\u0001\u0000\u0000"+
		"\u0000\u001c\u014f\u0001\u0000\u0000\u0000\u001e\u0156\u0001\u0000\u0000"+
		"\u0000 \u016a\u0001\u0000\u0000\u0000\"\u0178\u0001\u0000\u0000\u0000"+
		"$\u0182\u0001\u0000\u0000\u0000&\u0184\u0001\u0000\u0000\u0000(\u0188"+
		"\u0001\u0000\u0000\u0000*\u018c\u0001\u0000\u0000\u0000,\u01a0\u0001\u0000"+
		"\u0000\u0000.\u01a2\u0001\u0000\u0000\u00000\u01a6\u0001\u0000\u0000\u0000"+
		"2\u01aa\u0001\u0000\u0000\u00004\u01bd\u0001\u0000\u0000\u00006\u01bf"+
		"\u0001\u0000\u0000\u00008\u01cc\u0001\u0000\u0000\u0000:\u01d9\u0001\u0000"+
		"\u0000\u0000<\u01e1\u0001\u0000\u0000\u0000>\u01e7\u0001\u0000\u0000\u0000"+
		"@\u01f8\u0001\u0000\u0000\u0000B\u01fd\u0001\u0000\u0000\u0000D\u020c"+
		"\u0001\u0000\u0000\u0000F\u0211\u0001\u0000\u0000\u0000H\u021f\u0001\u0000"+
		"\u0000\u0000J\u0221\u0001\u0000\u0000\u0000L\u022f\u0001\u0000\u0000\u0000"+
		"N\u023f\u0001\u0000\u0000\u0000P\u0241\u0001\u0000\u0000\u0000R\u024a"+
		"\u0001\u0000\u0000\u0000T\u0257\u0001\u0000\u0000\u0000V\u025a\u0001\u0000"+
		"\u0000\u0000X\u0262\u0001\u0000\u0000\u0000Z\u026f\u0001\u0000\u0000\u0000"+
		"\\\u0274\u0001\u0000\u0000\u0000^\u027c\u0001\u0000\u0000\u0000`\u027e"+
		"\u0001\u0000\u0000\u0000b\u028b\u0001\u0000\u0000\u0000d\u0294\u0001\u0000"+
		"\u0000\u0000f\u029d\u0001\u0000\u0000\u0000h\u02a1\u0001\u0000\u0000\u0000"+
		"j\u02a7\u0001\u0000\u0000\u0000l\u02aa\u0001\u0000\u0000\u0000n\u02af"+
		"\u0001\u0000\u0000\u0000p\u02b4\u0001\u0000\u0000\u0000r\u02b9\u0001\u0000"+
		"\u0000\u0000t\u02be\u0001\u0000\u0000\u0000v\u02c3\u0001\u0000\u0000\u0000"+
		"x\u02c9\u0001\u0000\u0000\u0000z\u02cd\u0001\u0000\u0000\u0000|\u02ea"+
		"\u0001\u0000\u0000\u0000~\u0309\u0001\u0000\u0000\u0000\u0080\u030f\u0001"+
		"\u0000\u0000\u0000\u0082\u031d\u0001\u0000\u0000\u0000\u0084\u0327\u0001"+
		"\u0000\u0000\u0000\u0086\u0329\u0001\u0000\u0000\u0000\u0088\u032d\u0001"+
		"\u0000\u0000\u0000\u008a\u0338\u0001\u0000\u0000\u0000\u008c\u033a\u0001"+
		"\u0000\u0000\u0000\u008e\u0345\u0001\u0000\u0000\u0000\u0090\u0347\u0001"+
		"\u0000\u0000\u0000\u0092\u0094\u0003\u0002\u0001\u0000\u0093\u0092\u0001"+
		"\u0000\u0000\u0000\u0094\u0097\u0001\u0000\u0000\u0000\u0095\u0093\u0001"+
		"\u0000\u0000\u0000\u0095\u0096\u0001\u0000\u0000\u0000\u0096\u009b\u0001"+
		"\u0000\u0000\u0000\u0097\u0095\u0001\u0000\u0000\u0000\u0098\u009a\u0003"+
		"\u0006\u0003\u0000\u0099\u0098\u0001\u0000\u0000\u0000\u009a\u009d\u0001"+
		"\u0000\u0000\u0000\u009b\u0099\u0001\u0000\u0000\u0000\u009b\u009c\u0001"+
		"\u0000\u0000\u0000\u009c\u009e\u0001\u0000\u0000\u0000\u009d\u009b\u0001"+
		"\u0000\u0000\u0000\u009e\u009f\u0005\u0000\u0000\u0001\u009f\u0001\u0001"+
		"\u0000\u0000\u0000\u00a0\u00a1\u0005\t\u0000\u0000\u00a1\u00a8\u0003:"+
		"\u001d\u0000\u00a2\u00a9\u0005>\u0000\u0000\u00a3\u00a9\u0003\u008eG\u0000"+
		"\u00a4\u00a5\u0005K\u0000\u0000\u00a5\u00a6\u0003\u0004\u0002\u0000\u00a6"+
		"\u00a7\u0005L\u0000\u0000\u00a7\u00a9\u0001\u0000\u0000\u0000\u00a8\u00a2"+
		"\u0001\u0000\u0000\u0000\u00a8\u00a3\u0001\u0000\u0000\u0000\u00a8\u00a4"+
		"\u0001\u0000\u0000\u0000\u00a8\u00a9\u0001\u0000\u0000\u0000\u00a9\u0003"+
		"\u0001\u0000\u0000\u0000\u00aa\u00af\u0003\u008eG\u0000\u00ab\u00ac\u0005"+
		"P\u0000\u0000\u00ac\u00ae\u0003\u008eG\u0000\u00ad\u00ab\u0001\u0000\u0000"+
		"\u0000\u00ae\u00b1\u0001\u0000\u0000\u0000\u00af\u00ad\u0001\u0000\u0000"+
		"\u0000\u00af\u00b0\u0001\u0000\u0000\u0000\u00b0\u0005\u0001\u0000\u0000"+
		"\u0000\u00b1\u00af\u0001\u0000\u0000\u0000\u00b2\u00b4\u0003<\u001e\u0000"+
		"\u00b3\u00b2\u0001\u0000\u0000\u0000\u00b4\u00b7\u0001\u0000\u0000\u0000"+
		"\u00b5\u00b3\u0001\u0000\u0000\u0000\u00b5\u00b6\u0001\u0000\u0000\u0000"+
		"\u00b6\u00bc\u0001\u0000\u0000\u0000\u00b7\u00b5\u0001\u0000\u0000\u0000"+
		"\u00b8\u00bd\u0003\b\u0004\u0000\u00b9\u00bd\u0003\n\u0005\u0000\u00ba"+
		"\u00bd\u0003\u001a\r\u0000\u00bb\u00bd\u0003\u001e\u000f\u0000\u00bc\u00b8"+
		"\u0001\u0000\u0000\u0000\u00bc\u00b9\u0001\u0000\u0000\u0000\u00bc\u00ba"+
		"\u0001\u0000\u0000\u0000\u00bc\u00bb\u0001\u0000\u0000\u0000\u00bd\u0007"+
		"\u0001\u0000\u0000\u0000\u00be\u00bf\u0005\u0003\u0000\u0000\u00bf\u00c1"+
		"\u0003\u008eG\u0000\u00c0\u00c2\u00038\u001c\u0000\u00c1\u00c0\u0001\u0000"+
		"\u0000\u0000\u00c1\u00c2\u0001\u0000\u0000\u0000\u00c2\u00c3\u0001\u0000"+
		"\u0000\u0000\u00c3\u00c4\u0005I\u0000\u0000\u00c4\u00c5\u0003\"\u0011"+
		"\u0000\u00c5\u00c8\u0005J\u0000\u0000\u00c6\u00c7\u00052\u0000\u0000\u00c7"+
		"\u00c9\u0003(\u0014\u0000\u00c8\u00c6\u0001\u0000\u0000\u0000\u00c8\u00c9"+
		"\u0001\u0000\u0000\u0000\u00c9\u00cd\u0001\u0000\u0000\u0000\u00ca\u00cb"+
		"\u0005H\u0000\u0000\u00cb\u00ce\u0003N\'\u0000\u00cc\u00ce\u0003J%\u0000"+
		"\u00cd\u00ca\u0001\u0000\u0000\u0000\u00cd\u00cc\u0001\u0000\u0000\u0000"+
		"\u00cd\u00ce\u0001\u0000\u0000\u0000\u00ce\t\u0001\u0000\u0000\u0000\u00cf"+
		"\u00d0\u0005\u0015\u0000\u0000\u00d0\u00d2\u0003\u008eG\u0000\u00d1\u00d3"+
		"\u00038\u001c\u0000\u00d2\u00d1\u0001\u0000\u0000\u0000\u00d2\u00d3\u0001"+
		"\u0000\u0000\u0000\u00d3\u00d4\u0001\u0000\u0000\u0000\u00d4\u00d8\u0005"+
		"K\u0000\u0000\u00d5\u00d7\u0003\u0018\f\u0000\u00d6\u00d5\u0001\u0000"+
		"\u0000\u0000\u00d7\u00da\u0001\u0000\u0000\u0000\u00d8\u00d6\u0001\u0000"+
		"\u0000\u0000\u00d8\u00d9\u0001\u0000\u0000\u0000\u00d9\u00de\u0001\u0000"+
		"\u0000\u0000\u00da\u00d8\u0001\u0000\u0000\u0000\u00db\u00dd\u0003\b\u0004"+
		"\u0000\u00dc\u00db\u0001\u0000\u0000\u0000\u00dd\u00e0\u0001\u0000\u0000"+
		"\u0000\u00de\u00dc\u0001\u0000\u0000\u0000\u00de\u00df\u0001\u0000\u0000"+
		"\u0000\u00df\u00e4\u0001\u0000\u0000\u0000\u00e0\u00de\u0001\u0000\u0000"+
		"\u0000\u00e1\u00e3\u0003\f\u0006\u0000\u00e2\u00e1\u0001\u0000\u0000\u0000"+
		"\u00e3\u00e6\u0001\u0000\u0000\u0000\u00e4\u00e2\u0001\u0000\u0000\u0000"+
		"\u00e4\u00e5\u0001\u0000\u0000\u0000\u00e5\u00ea\u0001\u0000\u0000\u0000"+
		"\u00e6\u00e4\u0001\u0000\u0000\u0000\u00e7\u00e9\u0003\u000e\u0007\u0000"+
		"\u00e8\u00e7\u0001\u0000\u0000\u0000\u00e9\u00ec\u0001\u0000\u0000\u0000"+
		"\u00ea\u00e8\u0001\u0000\u0000\u0000\u00ea\u00eb\u0001\u0000\u0000\u0000"+
		"\u00eb\u00ed\u0001\u0000\u0000\u0000\u00ec\u00ea\u0001\u0000\u0000\u0000"+
		"\u00ed\u00ee\u0005L\u0000\u0000\u00ee\u000b\u0001\u0000\u0000\u0000\u00ef"+
		"\u00f0\u0005\u0019\u0000\u0000\u00f0\u00f9\u0003.\u0017\u0000\u00f1\u00f5"+
		"\u0005K\u0000\u0000\u00f2\u00f4\u0003\b\u0004\u0000\u00f3\u00f2\u0001"+
		"\u0000\u0000\u0000\u00f4\u00f7\u0001\u0000\u0000\u0000\u00f5\u00f3\u0001"+
		"\u0000\u0000\u0000\u00f5\u00f6\u0001\u0000\u0000\u0000\u00f6\u00f8\u0001"+
		"\u0000\u0000\u0000\u00f7\u00f5\u0001\u0000\u0000\u0000\u00f8\u00fa\u0005"+
		"L\u0000\u0000\u00f9\u00f1\u0001\u0000\u0000\u0000\u00f9\u00fa\u0001\u0000"+
		"\u0000\u0000\u00fa\r\u0001\u0000\u0000\u0000\u00fb\u0101\u0005#\u0000"+
		"\u0000\u00fc\u00fd\u0005K\u0000\u0000\u00fd\u00fe\u0003\u0010\b\u0000"+
		"\u00fe\u00ff\u0005L\u0000\u0000\u00ff\u0102\u0001\u0000\u0000\u0000\u0100"+
		"\u0102\u0003\u0010\b\u0000\u0101\u00fc\u0001\u0000\u0000\u0000\u0101\u0100"+
		"\u0001\u0000\u0000\u0000\u0102\u0103\u0001\u0000\u0000\u0000\u0103\u0107"+
		"\u0005K\u0000\u0000\u0104\u0106\u0003\b\u0004\u0000\u0105\u0104\u0001"+
		"\u0000\u0000\u0000\u0106\u0109\u0001\u0000\u0000\u0000\u0107\u0105\u0001"+
		"\u0000\u0000\u0000\u0107\u0108\u0001\u0000\u0000\u0000\u0108\u010a\u0001"+
		"\u0000\u0000\u0000\u0109\u0107\u0001\u0000\u0000\u0000\u010a\u010b\u0005"+
		"L\u0000\u0000\u010b\u000f\u0001\u0000\u0000\u0000\u010c\u0111\u0003\u0012"+
		"\t\u0000\u010d\u010e\u0005P\u0000\u0000\u010e\u0110\u0003\u0012\t\u0000"+
		"\u010f\u010d\u0001\u0000\u0000\u0000\u0110\u0113\u0001\u0000\u0000\u0000"+
		"\u0111\u010f\u0001\u0000\u0000\u0000\u0111\u0112\u0001\u0000\u0000\u0000"+
		"\u0112\u0115\u0001\u0000\u0000\u0000\u0113\u0111\u0001\u0000\u0000\u0000"+
		"\u0114\u010c\u0001\u0000\u0000\u0000\u0114\u0115\u0001\u0000\u0000\u0000"+
		"\u0115\u0011\u0001\u0000\u0000\u0000\u0116\u0118\u0005\\\u0000\u0000\u0117"+
		"\u0119\u0003\u0014\n\u0000\u0118\u0117\u0001\u0000\u0000\u0000\u0118\u0119"+
		"\u0001\u0000\u0000\u0000\u0119\u0013\u0001\u0000\u0000\u0000\u011a\u0123"+
		"\u0005G\u0000\u0000\u011b\u0120\u0003\u0016\u000b\u0000\u011c\u011d\u0005"+
		"R\u0000\u0000\u011d\u011f\u0003\u0016\u000b\u0000\u011e\u011c\u0001\u0000"+
		"\u0000\u0000\u011f\u0122\u0001\u0000\u0000\u0000\u0120\u011e\u0001\u0000"+
		"\u0000\u0000\u0120\u0121\u0001\u0000\u0000\u0000\u0121\u0124\u0001\u0000"+
		"\u0000\u0000\u0122\u0120\u0001\u0000\u0000\u0000\u0123\u011b\u0001\u0000"+
		"\u0000\u0000\u0123\u0124\u0001\u0000\u0000\u0000\u0124\u0015\u0001\u0000"+
		"\u0000\u0000\u0125\u0126\u0005\u0019\u0000\u0000\u0126\u012a\u0003.\u0017"+
		"\u0000\u0127\u0128\u0005\u000b\u0000\u0000\u0128\u012a\u0003.\u0017\u0000"+
		"\u0129\u0125\u0001\u0000\u0000\u0000\u0129\u0127\u0001\u0000\u0000\u0000"+
		"\u012a\u0017\u0001\u0000\u0000\u0000\u012b\u012c\u0003\u008eG\u0000\u012c"+
		"\u012d\u0005G\u0000\u0000\u012d\u012e\u0003(\u0014\u0000\u012e\u0019\u0001"+
		"\u0000\u0000\u0000\u012f\u0130\u0005\u001a\u0000\u0000\u0130\u0132\u0003"+
		"\u008eG\u0000\u0131\u0133\u00038\u001c\u0000\u0132\u0131\u0001\u0000\u0000"+
		"\u0000\u0132\u0133\u0001\u0000\u0000\u0000\u0133\u0134\u0001\u0000\u0000"+
		"\u0000\u0134\u0138\u0005K\u0000\u0000\u0135\u0137\u0003\u001c\u000e\u0000"+
		"\u0136\u0135\u0001\u0000\u0000\u0000\u0137\u013a\u0001\u0000\u0000\u0000"+
		"\u0138\u0136\u0001\u0000\u0000\u0000\u0138\u0139\u0001\u0000\u0000\u0000"+
		"\u0139\u013e\u0001\u0000\u0000\u0000\u013a\u0138\u0001\u0000\u0000\u0000"+
		"\u013b\u013d\u0003\b\u0004\u0000\u013c\u013b\u0001\u0000\u0000\u0000\u013d"+
		"\u0140\u0001\u0000\u0000\u0000\u013e\u013c\u0001\u0000\u0000\u0000\u013e"+
		"\u013f\u0001\u0000\u0000\u0000\u013f\u0144\u0001\u0000\u0000\u0000\u0140"+
		"\u013e\u0001\u0000\u0000\u0000\u0141\u0143\u0003\f\u0006\u0000\u0142\u0141"+
		"\u0001\u0000\u0000\u0000\u0143\u0146\u0001\u0000\u0000\u0000\u0144\u0142"+
		"\u0001\u0000\u0000\u0000\u0144\u0145\u0001\u0000\u0000\u0000\u0145\u014a"+
		"\u0001\u0000\u0000\u0000\u0146\u0144\u0001\u0000\u0000\u0000\u0147\u0149"+
		"\u0003\u000e\u0007\u0000\u0148\u0147\u0001\u0000\u0000\u0000\u0149\u014c"+
		"\u0001\u0000\u0000\u0000\u014a\u0148\u0001\u0000\u0000\u0000\u014a\u014b"+
		"\u0001\u0000\u0000\u0000\u014b\u014d\u0001\u0000\u0000\u0000\u014c\u014a"+
		"\u0001\u0000\u0000\u0000\u014d\u014e\u0005L\u0000\u0000\u014e\u001b\u0001"+
		"\u0000\u0000\u0000\u014f\u0154\u0003\u008eG\u0000\u0150\u0151\u0005I\u0000"+
		"\u0000\u0151\u0152\u0003$\u0012\u0000\u0152\u0153\u0005J\u0000\u0000\u0153"+
		"\u0155\u0001\u0000\u0000\u0000\u0154\u0150\u0001\u0000\u0000\u0000\u0154"+
		"\u0155\u0001\u0000\u0000\u0000\u0155\u001d\u0001\u0000\u0000\u0000\u0156"+
		"\u0157\u0005$\u0000\u0000\u0157\u0159\u0003\u008eG\u0000\u0158\u015a\u0003"+
		"8\u001c\u0000\u0159\u0158\u0001\u0000\u0000\u0000\u0159\u015a\u0001\u0000"+
		"\u0000\u0000\u015a\u015b\u0001\u0000\u0000\u0000\u015b\u015f\u0005K\u0000"+
		"\u0000\u015c\u015e\u0003\b\u0004\u0000\u015d\u015c\u0001\u0000\u0000\u0000"+
		"\u015e\u0161\u0001\u0000\u0000\u0000\u015f\u015d\u0001\u0000\u0000\u0000"+
		"\u015f\u0160\u0001\u0000\u0000\u0000\u0160\u0165\u0001\u0000\u0000\u0000"+
		"\u0161\u015f\u0001\u0000\u0000\u0000\u0162\u0164\u0003 \u0010\u0000\u0163"+
		"\u0162\u0001\u0000\u0000\u0000\u0164\u0167\u0001\u0000\u0000\u0000\u0165"+
		"\u0163\u0001\u0000\u0000\u0000\u0165\u0166\u0001\u0000\u0000\u0000\u0166"+
		"\u0168\u0001\u0000\u0000\u0000\u0167\u0165\u0001\u0000\u0000\u0000\u0168"+
		"\u0169\u0005L\u0000\u0000\u0169\u001f\u0001\u0000\u0000\u0000\u016a\u016b"+
		"\u0005\u0019\u0000\u0000\u016b\u016c\u0003.\u0017\u0000\u016c!\u0001\u0000"+
		"\u0000\u0000\u016d\u0170\u0003&\u0013\u0000\u016e\u0170\u0005%\u0000\u0000"+
		"\u016f\u016d\u0001\u0000\u0000\u0000\u016f\u016e\u0001\u0000\u0000\u0000"+
		"\u0170\u0175\u0001\u0000\u0000\u0000\u0171\u0172\u0005P\u0000\u0000\u0172"+
		"\u0174\u0003&\u0013\u0000\u0173\u0171\u0001\u0000\u0000\u0000\u0174\u0177"+
		"\u0001\u0000\u0000\u0000\u0175\u0173\u0001\u0000\u0000\u0000\u0175\u0176"+
		"\u0001\u0000\u0000\u0000\u0176\u0179\u0001\u0000\u0000\u0000\u0177\u0175"+
		"\u0001\u0000\u0000\u0000\u0178\u016f\u0001\u0000\u0000\u0000\u0178\u0179"+
		"\u0001\u0000\u0000\u0000\u0179#\u0001\u0000\u0000\u0000\u017a\u017f\u0003"+
		"&\u0013\u0000\u017b\u017c\u0005P\u0000\u0000\u017c\u017e\u0003&\u0013"+
		"\u0000\u017d\u017b\u0001\u0000\u0000\u0000\u017e\u0181\u0001\u0000\u0000"+
		"\u0000\u017f\u017d\u0001\u0000\u0000\u0000\u017f\u0180\u0001\u0000\u0000"+
		"\u0000\u0180\u0183\u0001\u0000\u0000\u0000\u0181\u017f\u0001\u0000\u0000"+
		"\u0000\u0182\u017a\u0001\u0000\u0000\u0000\u0182\u0183\u0001\u0000\u0000"+
		"\u0000\u0183%\u0001\u0000\u0000\u0000\u0184\u0185\u0003\u008eG\u0000\u0185"+
		"\u0186\u0005G\u0000\u0000\u0186\u0187\u0003(\u0014\u0000\u0187\'\u0001"+
		"\u0000\u0000\u0000\u0188\u018a\u0003,\u0016\u0000\u0189\u018b\u0003*\u0015"+
		"\u0000\u018a\u0189\u0001\u0000\u0000\u0000\u018a\u018b\u0001\u0000\u0000"+
		"\u0000\u018b)\u0001\u0000\u0000\u0000\u018c\u018d\u0005T\u0000\u0000\u018d"+
		"+\u0001\u0000\u0000\u0000\u018e\u01a1\u0005/\u0000\u0000\u018f\u01a1\u0005"+
		"&\u0000\u0000\u0190\u01a1\u0005*\u0000\u0000\u0191\u01a1\u0005+\u0000"+
		"\u0000\u0192\u01a1\u0005(\u0000\u0000\u0193\u01a1\u0005)\u0000\u0000\u0194"+
		"\u01a1\u0005\'\u0000\u0000\u0195\u01a1\u0005-\u0000\u0000\u0196\u01a1"+
		"\u0005.\u0000\u0000\u0197\u01a1\u0005,\u0000\u0000\u0198\u01a1\u0005T"+
		"\u0000\u0000\u0199\u01a1\u0003.\u0017\u0000\u019a\u01a1\u00030\u0018\u0000"+
		"\u019b\u01a1\u00032\u0019\u0000\u019c\u019d\u0005I\u0000\u0000\u019d\u019e"+
		"\u0003(\u0014\u0000\u019e\u019f\u0005J\u0000\u0000\u019f\u01a1\u0001\u0000"+
		"\u0000\u0000\u01a0\u018e\u0001\u0000\u0000\u0000\u01a0\u018f\u0001\u0000"+
		"\u0000\u0000\u01a0\u0190\u0001\u0000\u0000\u0000\u01a0\u0191\u0001\u0000"+
		"\u0000\u0000\u01a0\u0192\u0001\u0000\u0000\u0000\u01a0\u0193\u0001\u0000"+
		"\u0000\u0000\u01a0\u0194\u0001\u0000\u0000\u0000\u01a0\u0195\u0001\u0000"+
		"\u0000\u0000\u01a0\u0196\u0001\u0000\u0000\u0000\u01a0\u0197\u0001\u0000"+
		"\u0000\u0000\u01a0\u0198\u0001\u0000\u0000\u0000\u01a0\u0199\u0001\u0000"+
		"\u0000\u0000\u01a0\u019a\u0001\u0000\u0000\u0000\u01a0\u019b\u0001\u0000"+
		"\u0000\u0000\u01a0\u019c\u0001\u0000\u0000\u0000\u01a1-\u0001\u0000\u0000"+
		"\u0000\u01a2\u01a4\u0003:\u001d\u0000\u01a3\u01a5\u00036\u001b\u0000\u01a4"+
		"\u01a3\u0001\u0000\u0000\u0000\u01a4\u01a5\u0001\u0000\u0000\u0000\u01a5"+
		"/\u0001\u0000\u0000\u0000\u01a6\u01a7\u0005M\u0000\u0000\u01a7\u01a8\u0003"+
		"(\u0014\u0000\u01a8\u01a9\u0005N\u0000\u0000\u01a91\u0001\u0000\u0000"+
		"\u0000\u01aa\u01ab\u0005\u0003\u0000\u0000\u01ab\u01ac\u0005I\u0000\u0000"+
		"\u01ac\u01ad\u00034\u001a\u0000\u01ad\u01b0\u0005J\u0000\u0000\u01ae\u01af"+
		"\u00052\u0000\u0000\u01af\u01b1\u0003(\u0014\u0000\u01b0\u01ae\u0001\u0000"+
		"\u0000\u0000\u01b0\u01b1\u0001\u0000\u0000\u0000\u01b1\u01b3\u0001\u0000"+
		"\u0000\u0000\u01b2\u01b4\u0003T*\u0000\u01b3\u01b2\u0001\u0000\u0000\u0000"+
		"\u01b3\u01b4\u0001\u0000\u0000\u0000\u01b43\u0001\u0000\u0000\u0000\u01b5"+
		"\u01ba\u0003(\u0014\u0000\u01b6\u01b7\u0005P\u0000\u0000\u01b7\u01b9\u0003"+
		"(\u0014\u0000\u01b8\u01b6\u0001\u0000\u0000\u0000\u01b9\u01bc\u0001\u0000"+
		"\u0000\u0000\u01ba\u01b8\u0001\u0000\u0000\u0000\u01ba\u01bb\u0001\u0000"+
		"\u0000\u0000\u01bb\u01be\u0001\u0000\u0000\u0000\u01bc\u01ba\u0001\u0000"+
		"\u0000\u0000\u01bd\u01b5\u0001\u0000\u0000\u0000\u01bd\u01be\u0001\u0000"+
		"\u0000\u0000\u01be5\u0001\u0000\u0000\u0000\u01bf\u01c8\u0005E\u0000\u0000"+
		"\u01c0\u01c5\u0003(\u0014\u0000\u01c1\u01c2\u0005P\u0000\u0000\u01c2\u01c4"+
		"\u0003(\u0014\u0000\u01c3\u01c1\u0001\u0000\u0000\u0000\u01c4\u01c7\u0001"+
		"\u0000\u0000\u0000\u01c5\u01c3\u0001\u0000\u0000\u0000\u01c5\u01c6\u0001"+
		"\u0000\u0000\u0000\u01c6\u01c9\u0001\u0000\u0000\u0000\u01c7\u01c5\u0001"+
		"\u0000\u0000\u0000\u01c8\u01c0\u0001\u0000\u0000\u0000\u01c8\u01c9\u0001"+
		"\u0000\u0000\u0000\u01c9\u01ca\u0001\u0000\u0000\u0000\u01ca\u01cb\u0005"+
		"D\u0000\u0000\u01cb7\u0001\u0000\u0000\u0000\u01cc\u01d5\u0005E\u0000"+
		"\u0000\u01cd\u01d2\u0003\u008eG\u0000\u01ce\u01cf\u0005P\u0000\u0000\u01cf"+
		"\u01d1\u0003\u008eG\u0000\u01d0\u01ce\u0001\u0000\u0000\u0000\u01d1\u01d4"+
		"\u0001\u0000\u0000\u0000\u01d2\u01d0\u0001\u0000\u0000\u0000\u01d2\u01d3"+
		"\u0001\u0000\u0000\u0000\u01d3\u01d6\u0001\u0000\u0000\u0000\u01d4\u01d2"+
		"\u0001\u0000\u0000\u0000\u01d5\u01cd\u0001\u0000\u0000\u0000\u01d5\u01d6"+
		"\u0001\u0000\u0000\u0000\u01d6\u01d7\u0001\u0000\u0000\u0000\u01d7\u01d8"+
		"\u0005D\u0000\u0000\u01d89\u0001\u0000\u0000\u0000\u01d9\u01de\u0003\u008e"+
		"G\u0000\u01da\u01db\u0005S\u0000\u0000\u01db\u01dd\u0003\u008eG\u0000"+
		"\u01dc\u01da\u0001\u0000\u0000\u0000\u01dd\u01e0\u0001\u0000\u0000\u0000"+
		"\u01de\u01dc\u0001\u0000\u0000\u0000\u01de\u01df\u0001\u0000\u0000\u0000"+
		"\u01df;\u0001\u0000\u0000\u0000\u01e0\u01de\u0001\u0000\u0000\u0000\u01e1"+
		"\u01e2\u0005O\u0000\u0000\u01e2\u01e5\u0003\u008eG\u0000\u01e3\u01e4\u0005"+
		"H\u0000\u0000\u01e4\u01e6\u0003H$\u0000\u01e5\u01e3\u0001\u0000\u0000"+
		"\u0000\u01e5\u01e6\u0001\u0000\u0000\u0000\u01e6=\u0001\u0000\u0000\u0000"+
		"\u01e7\u01f2\u0005K\u0000\u0000\u01e8\u01ef\u0003@ \u0000\u01e9\u01eb"+
		"\u0005P\u0000\u0000\u01ea\u01e9\u0001\u0000\u0000\u0000\u01ea\u01eb\u0001"+
		"\u0000\u0000\u0000\u01eb\u01ec\u0001\u0000\u0000\u0000\u01ec\u01ee\u0003"+
		"@ \u0000\u01ed\u01ea\u0001\u0000\u0000\u0000\u01ee\u01f1\u0001\u0000\u0000"+
		"\u0000\u01ef\u01ed\u0001\u0000\u0000\u0000\u01ef\u01f0\u0001\u0000\u0000"+
		"\u0000\u01f0\u01f3\u0001\u0000\u0000\u0000\u01f1\u01ef\u0001\u0000\u0000"+
		"\u0000\u01f2\u01e8\u0001\u0000\u0000\u0000\u01f2\u01f3\u0001\u0000\u0000"+
		"\u0000\u01f3\u01f4\u0001\u0000\u0000\u0000\u01f4\u01f5\u0005L\u0000\u0000"+
		"\u01f5?\u0001\u0000\u0000\u0000\u01f6\u01f9\u0005W\u0000\u0000\u01f7\u01f9"+
		"\u0003\u008eG\u0000\u01f8\u01f6\u0001\u0000\u0000\u0000\u01f8\u01f7\u0001"+
		"\u0000\u0000\u0000\u01f9\u01fa\u0001\u0000\u0000\u0000\u01fa\u01fb\u0005"+
		"G\u0000\u0000\u01fb\u01fc\u0003H$\u0000\u01fcA\u0001\u0000\u0000\u0000"+
		"\u01fd\u0208\u0005M\u0000\u0000\u01fe\u0205\u0003H$\u0000\u01ff\u0201"+
		"\u0005P\u0000\u0000\u0200\u01ff\u0001\u0000\u0000\u0000\u0200\u0201\u0001"+
		"\u0000\u0000\u0000\u0201\u0202\u0001\u0000\u0000\u0000\u0202\u0204\u0003"+
		"H$\u0000\u0203\u0200\u0001\u0000\u0000\u0000\u0204\u0207\u0001\u0000\u0000"+
		"\u0000\u0205\u0203\u0001\u0000\u0000\u0000\u0205\u0206\u0001\u0000\u0000"+
		"\u0000\u0206\u0209\u0001\u0000\u0000\u0000\u0207\u0205\u0001\u0000\u0000"+
		"\u0000\u0208\u01fe\u0001\u0000\u0000\u0000\u0208\u0209\u0001\u0000\u0000"+
		"\u0000\u0209\u020a\u0001\u0000\u0000\u0000\u020a\u020b\u0005N\u0000\u0000"+
		"\u020bC\u0001\u0000\u0000\u0000\u020c\u020d\u0005\u0001\u0000\u0000\u020d"+
		"\u020e\u0005K\u0000\u0000\u020e\u020f\u0003N\'\u0000\u020f\u0210\u0005"+
		"L\u0000\u0000\u0210E\u0001\u0000\u0000\u0000\u0211\u0212\u0005\u0002\u0000"+
		"\u0000\u0212\u0213\u0005K\u0000\u0000\u0213\u0214\u0003(\u0014\u0000\u0214"+
		"\u0215\u0005L\u0000\u0000\u0215G\u0001\u0000\u0000\u0000\u0216\u0220\u0005"+
		"W\u0000\u0000\u0217\u0220\u0005Y\u0000\u0000\u0218\u0220\u0003>\u001f"+
		"\u0000\u0219\u0220\u0003B!\u0000\u021a\u0220\u0005\u000f\u0000\u0000\u021b"+
		"\u0220\u0005\u0010\u0000\u0000\u021c\u0220\u0005\b\u0000\u0000\u021d\u0220"+
		"\u0003D\"\u0000\u021e\u0220\u0003F#\u0000\u021f\u0216\u0001\u0000\u0000"+
		"\u0000\u021f\u0217\u0001\u0000\u0000\u0000\u021f\u0218\u0001\u0000\u0000"+
		"\u0000\u021f\u0219\u0001\u0000\u0000\u0000\u021f\u021a\u0001\u0000\u0000"+
		"\u0000\u021f\u021b\u0001\u0000\u0000\u0000\u021f\u021c\u0001\u0000\u0000"+
		"\u0000\u021f\u021d\u0001\u0000\u0000\u0000\u021f\u021e\u0001\u0000\u0000"+
		"\u0000\u0220I\u0001\u0000\u0000\u0000\u0221\u0228\u0005K\u0000\u0000\u0222"+
		"\u0224\u0003N\'\u0000\u0223\u0225\u0005U\u0000\u0000\u0224\u0223\u0001"+
		"\u0000\u0000\u0000\u0224\u0225\u0001\u0000\u0000\u0000\u0225\u0227\u0001"+
		"\u0000\u0000\u0000\u0226\u0222\u0001\u0000\u0000\u0000\u0227\u022a\u0001"+
		"\u0000\u0000\u0000\u0228\u0226\u0001\u0000\u0000\u0000\u0228\u0229\u0001"+
		"\u0000\u0000\u0000\u0229\u022b\u0001\u0000\u0000\u0000\u022a\u0228\u0001"+
		"\u0000\u0000\u0000\u022b\u022c\u0005L\u0000\u0000\u022cK\u0001\u0000\u0000"+
		"\u0000\u022d\u0230\u0003J%\u0000\u022e\u0230\u0003N\'\u0000\u022f\u022d"+
		"\u0001\u0000\u0000\u0000\u022f\u022e\u0001\u0000\u0000\u0000\u0230M\u0001"+
		"\u0000\u0000\u0000\u0231\u0240\u0003P(\u0000\u0232\u0240\u0003R)\u0000"+
		"\u0233\u0235\u0005\u0013\u0000\u0000\u0234\u0236\u0003L&\u0000\u0235\u0234"+
		"\u0001\u0000\u0000\u0000\u0235\u0236\u0001\u0000\u0000\u0000\u0236\u0240"+
		"\u0001\u0000\u0000\u0000\u0237\u0240\u0003X,\u0000\u0238\u0240\u0003`"+
		"0\u0000\u0239\u0240\u0003f3\u0000\u023a\u0240\u0003h4\u0000\u023b\u0240"+
		"\u0003l6\u0000\u023c\u0240\u0005\u0012\u0000\u0000\u023d\u0240\u00051"+
		"\u0000\u0000\u023e\u0240\u0003j5\u0000\u023f\u0231\u0001\u0000\u0000\u0000"+
		"\u023f\u0232\u0001\u0000\u0000\u0000\u023f\u0233\u0001\u0000\u0000\u0000"+
		"\u023f\u0237\u0001\u0000\u0000\u0000\u023f\u0238\u0001\u0000\u0000\u0000"+
		"\u023f\u0239\u0001\u0000\u0000\u0000\u023f\u023a\u0001\u0000\u0000\u0000"+
		"\u023f\u023b\u0001\u0000\u0000\u0000\u023f\u023c\u0001\u0000\u0000\u0000"+
		"\u023f\u023d\u0001\u0000\u0000\u0000\u023f\u023e\u0001\u0000\u0000\u0000"+
		"\u0240O\u0001\u0000\u0000\u0000\u0241\u0242\u0005\u001c\u0000\u0000\u0242"+
		"\u0245\u0003\u008eG\u0000\u0243\u0244\u0005G\u0000\u0000\u0244\u0246\u0003"+
		"(\u0014\u0000\u0245\u0243\u0001\u0000\u0000\u0000\u0245\u0246\u0001\u0000"+
		"\u0000\u0000\u0246\u0247\u0001\u0000\u0000\u0000\u0247\u0248\u0005H\u0000"+
		"\u0000\u0248\u0249\u0003L&\u0000\u0249Q\u0001\u0000\u0000\u0000\u024a"+
		"\u024b\u0005\u0003\u0000\u0000\u024b\u024c\u0005I\u0000\u0000\u024c\u024d"+
		"\u0003\u008aE\u0000\u024d\u0250\u0005J\u0000\u0000\u024e\u024f\u00052"+
		"\u0000\u0000\u024f\u0251\u0003(\u0014\u0000\u0250\u024e\u0001\u0000\u0000"+
		"\u0000\u0250\u0251\u0001\u0000\u0000\u0000\u0251\u0253\u0001\u0000\u0000"+
		"\u0000\u0252\u0254\u0003T*\u0000\u0253\u0252\u0001\u0000\u0000\u0000\u0253"+
		"\u0254\u0001\u0000\u0000\u0000\u0254\u0255\u0001\u0000\u0000\u0000\u0255"+
		"\u0256\u0003L&\u0000\u0256S\u0001\u0000\u0000\u0000\u0257\u0258\u0005"+
		"\u0019\u0000\u0000\u0258\u0259\u0003V+\u0000\u0259U\u0001\u0000\u0000"+
		"\u0000\u025a\u025f\u0003.\u0017\u0000\u025b\u025c\u0005P\u0000\u0000\u025c"+
		"\u025e\u0003.\u0017\u0000\u025d\u025b\u0001\u0000\u0000\u0000\u025e\u0261"+
		"\u0001\u0000\u0000\u0000\u025f\u025d\u0001\u0000\u0000\u0000\u025f\u0260"+
		"\u0001\u0000\u0000\u0000\u0260W\u0001\u0000\u0000\u0000\u0261\u025f\u0001"+
		"\u0000\u0000\u0000\u0262\u0263\u0005\f\u0000\u0000\u0263\u0264\u0003L"+
		"&\u0000\u0264\u0268\u0005K\u0000\u0000\u0265\u0267\u0003Z-\u0000\u0266"+
		"\u0265\u0001\u0000\u0000\u0000\u0267\u026a\u0001\u0000\u0000\u0000\u0268"+
		"\u0266\u0001\u0000\u0000\u0000\u0268\u0269\u0001\u0000\u0000\u0000\u0269"+
		"\u026b\u0001\u0000\u0000\u0000\u026a\u0268\u0001\u0000\u0000\u0000\u026b"+
		"\u026c\u0005L\u0000\u0000\u026cY\u0001\u0000\u0000\u0000\u026d\u0270\u0003"+
		"^/\u0000\u026e\u0270\u0003\\.\u0000\u026f\u026d\u0001\u0000\u0000\u0000"+
		"\u026f\u026e\u0001\u0000\u0000\u0000\u0270\u0271\u0001\u0000\u0000\u0000"+
		"\u0271\u0272\u00052\u0000\u0000\u0272\u0273\u0003L&\u0000\u0273[\u0001"+
		"\u0000\u0000\u0000\u0274\u027a\u0003.\u0017\u0000\u0275\u027b\u0003\u008e"+
		"G\u0000\u0276\u0277\u0005I\u0000\u0000\u0277\u0278\u0003\u008aE\u0000"+
		"\u0278\u0279\u0005J\u0000\u0000\u0279\u027b\u0001\u0000\u0000\u0000\u027a"+
		"\u0275\u0001\u0000\u0000\u0000\u027a\u0276\u0001\u0000\u0000\u0000\u027b"+
		"]\u0001\u0000\u0000\u0000\u027c\u027d\u0005Q\u0000\u0000\u027d_\u0001"+
		"\u0000\u0000\u0000\u027e\u027f\u0005\u001d\u0000\u0000\u027f\u0285\u0003"+
		"L&\u0000\u0280\u0286\u0003\u008eG\u0000\u0281\u0282\u0005I\u0000\u0000"+
		"\u0282\u0283\u0003\u008aE\u0000\u0283\u0284\u0005J\u0000\u0000\u0284\u0286"+
		"\u0001\u0000\u0000\u0000\u0285\u0280\u0001\u0000\u0000\u0000\u0285\u0281"+
		"\u0001\u0000\u0000\u0000\u0285\u0286\u0001\u0000\u0000\u0000\u0286\u0287"+
		"\u0001\u0000\u0000\u0000\u0287\u0289\u0003J%\u0000\u0288\u028a\u0003b"+
		"1\u0000\u0289\u0288\u0001\u0000\u0000\u0000\u0289\u028a\u0001\u0000\u0000"+
		"\u0000\u028aa\u0001\u0000\u0000\u0000\u028b\u028d\u0005\u001e\u0000\u0000"+
		"\u028c\u028e\u0003d2\u0000\u028d\u028c\u0001\u0000\u0000\u0000\u028d\u028e"+
		"\u0001\u0000\u0000\u0000\u028e\u0292\u0001\u0000\u0000\u0000\u028f\u0293"+
		"\u0003`0\u0000\u0290\u0293\u0003J%\u0000\u0291\u0293\u0003X,\u0000\u0292"+
		"\u028f\u0001\u0000\u0000\u0000\u0292\u0290\u0001\u0000\u0000\u0000\u0292"+
		"\u0291\u0001\u0000\u0000\u0000\u0293c\u0001\u0000\u0000\u0000\u0294\u0295"+
		"\u0005\u0004\u0000\u0000\u0295\u029b\u0003(\u0014\u0000\u0296\u029c\u0003"+
		"\u008eG\u0000\u0297\u0298\u0005I\u0000\u0000\u0298\u0299\u0003\u008aE"+
		"\u0000\u0299\u029a\u0005J\u0000\u0000\u029a\u029c\u0001\u0000\u0000\u0000"+
		"\u029b\u0296\u0001\u0000\u0000\u0000\u029b\u0297\u0001\u0000\u0000\u0000"+
		"\u029b\u029c\u0001\u0000\u0000\u0000\u029ce\u0001\u0000\u0000\u0000\u029d"+
		"\u029e\u0005 \u0000\u0000\u029e\u029f\u0003L&\u0000\u029f\u02a0\u0003"+
		"J%\u0000\u02a0g\u0001\u0000\u0000\u0000\u02a1\u02a2\u0005!\u0000\u0000"+
		"\u02a2\u02a3\u0003\u008eG\u0000\u02a3\u02a4\u0005\u0005\u0000\u0000\u02a4"+
		"\u02a5\u0003L&\u0000\u02a5\u02a6\u0003J%\u0000\u02a6i\u0001\u0000\u0000"+
		"\u0000\u02a7\u02a8\u0005\u0017\u0000\u0000\u02a8\u02a9\u0003L&\u0000\u02a9"+
		"k\u0001\u0000\u0000\u0000\u02aa\u02ad\u0003n7\u0000\u02ab\u02ac\u0005"+
		";\u0000\u0000\u02ac\u02ae\u0003l6\u0000\u02ad\u02ab\u0001\u0000\u0000"+
		"\u0000\u02ad\u02ae\u0001\u0000\u0000\u0000\u02aem\u0001\u0000\u0000\u0000"+
		"\u02af\u02b2\u0003p8\u0000\u02b0\u02b1\u0005:\u0000\u0000\u02b1\u02b3"+
		"\u0003n7\u0000\u02b2\u02b0\u0001\u0000\u0000\u0000\u02b2\u02b3\u0001\u0000"+
		"\u0000\u0000\u02b3o\u0001\u0000\u0000\u0000\u02b4\u02b7\u0003r9\u0000"+
		"\u02b5\u02b6\u0007\u0000\u0000\u0000\u02b6\u02b8\u0003p8\u0000\u02b7\u02b5"+
		"\u0001\u0000\u0000\u0000\u02b7\u02b8\u0001\u0000\u0000\u0000\u02b8q\u0001"+
		"\u0000\u0000\u0000\u02b9\u02bc\u0003t:\u0000\u02ba\u02bb\u0007\u0001\u0000"+
		"\u0000\u02bb\u02bd\u0003r9\u0000\u02bc\u02ba\u0001\u0000\u0000\u0000\u02bc"+
		"\u02bd\u0001\u0000\u0000\u0000\u02bds\u0001\u0000\u0000\u0000\u02be\u02c1"+
		"\u0003v;\u0000\u02bf\u02c0\u0007\u0002\u0000\u0000\u02c0\u02c2\u0003t"+
		":\u0000\u02c1\u02bf\u0001\u0000\u0000\u0000\u02c1\u02c2\u0001\u0000\u0000"+
		"\u0000\u02c2u\u0001\u0000\u0000\u0000\u02c3\u02c6\u0003x<\u0000\u02c4"+
		"\u02c5\u0007\u0003\u0000\u0000\u02c5\u02c7\u0003v;\u0000\u02c6\u02c4\u0001"+
		"\u0000\u0000\u0000\u02c6\u02c7\u0001\u0000\u0000\u0000\u02c7w\u0001\u0000"+
		"\u0000\u0000\u02c8\u02ca\u0007\u0004\u0000\u0000\u02c9\u02c8\u0001\u0000"+
		"\u0000\u0000\u02c9\u02ca\u0001\u0000\u0000\u0000\u02ca\u02cb\u0001\u0000"+
		"\u0000\u0000\u02cb\u02cc\u0003z=\u0000\u02ccy\u0001\u0000\u0000\u0000"+
		"\u02cd\u02d1\u0003~?\u0000\u02ce\u02d0\u0003|>\u0000\u02cf\u02ce\u0001"+
		"\u0000\u0000\u0000\u02d0\u02d3\u0001\u0000\u0000\u0000\u02d1\u02cf\u0001"+
		"\u0000\u0000\u0000\u02d1\u02d2\u0001\u0000\u0000\u0000\u02d2\u02d7\u0001"+
		"\u0000\u0000\u0000\u02d3\u02d1\u0001\u0000\u0000\u0000\u02d4\u02d5\u0005"+
		"H\u0000\u0000\u02d5\u02d8\u0003L&\u0000\u02d6\u02d8\u0003\u0088D\u0000"+
		"\u02d7\u02d4\u0001\u0000\u0000\u0000\u02d7\u02d6\u0001\u0000\u0000\u0000"+
		"\u02d7\u02d8\u0001\u0000\u0000\u0000\u02d8{\u0001\u0000\u0000\u0000\u02d9"+
		"\u02da\u0005S\u0000\u0000\u02da\u02eb\u0003\u008eG\u0000\u02db\u02dc\u0005"+
		"S\u0000\u0000\u02dc\u02eb\u0005\u001b\u0000\u0000\u02dd\u02df\u00036\u001b"+
		"\u0000\u02de\u02dd\u0001\u0000\u0000\u0000\u02de\u02df\u0001\u0000\u0000"+
		"\u0000\u02df\u02e0\u0001\u0000\u0000\u0000\u02e0\u02e1\u0005I\u0000\u0000"+
		"\u02e1\u02e2\u0003\u0082A\u0000\u02e2\u02e3\u0005J\u0000\u0000\u02e3\u02eb"+
		"\u0001\u0000\u0000\u0000\u02e4\u02e5\u0005M\u0000\u0000\u02e5\u02e6\u0003"+
		"L&\u0000\u02e6\u02e7\u0005N\u0000\u0000\u02e7\u02eb\u0001\u0000\u0000"+
		"\u0000\u02e8\u02e9\u0005\u0006\u0000\u0000\u02e9\u02eb\u0003(\u0014\u0000"+
		"\u02ea\u02d9\u0001\u0000\u0000\u0000\u02ea\u02db\u0001\u0000\u0000\u0000"+
		"\u02ea\u02de\u0001\u0000\u0000\u0000\u02ea\u02e4\u0001\u0000\u0000\u0000"+
		"\u02ea\u02e8\u0001\u0000\u0000\u0000\u02eb}\u0001\u0000\u0000\u0000\u02ec"+
		"\u030a\u0003\u0080@\u0000\u02ed\u02ee\u0005I\u0000\u0000\u02ee\u02ef\u0003"+
		"L&\u0000\u02ef\u02f0\u0005J\u0000\u0000\u02f0\u030a\u0001\u0000\u0000"+
		"\u0000\u02f1\u030a\u0005Y\u0000\u0000\u02f2\u0301\u0003\u008eG\u0000\u02f3"+
		"\u02f4\u0005S\u0000\u0000\u02f4\u02f6\u0003\u008eG\u0000\u02f5\u02f3\u0001"+
		"\u0000\u0000\u0000\u02f6\u02f9\u0001\u0000\u0000\u0000\u02f7\u02f5\u0001"+
		"\u0000\u0000\u0000\u02f7\u02f8\u0001\u0000\u0000\u0000\u02f8\u02fb\u0001"+
		"\u0000\u0000\u0000\u02f9\u02f7\u0001\u0000\u0000\u0000\u02fa\u02fc\u0003"+
		"6\u001b\u0000\u02fb\u02fa\u0001\u0000\u0000\u0000\u02fb\u02fc\u0001\u0000"+
		"\u0000\u0000\u02fc\u02fd\u0001\u0000\u0000\u0000\u02fd\u02fe\u0005K\u0000"+
		"\u0000\u02fe\u02ff\u0003\u0084B\u0000\u02ff\u0300\u0005L\u0000\u0000\u0300"+
		"\u0302\u0001\u0000\u0000\u0000\u0301\u02f7\u0001\u0000\u0000\u0000\u0301"+
		"\u0302\u0001\u0000\u0000\u0000\u0302\u030a\u0001\u0000\u0000\u0000\u0303"+
		"\u030a\u0005W\u0000\u0000\u0304\u030a\u0005X\u0000\u0000\u0305\u030a\u0005"+
		"%\u0000\u0000\u0306\u030a\u0005\"\u0000\u0000\u0307\u030a\u0005\u000f"+
		"\u0000\u0000\u0308\u030a\u0005\u0010\u0000\u0000\u0309\u02ec\u0001\u0000"+
		"\u0000\u0000\u0309\u02ed\u0001\u0000\u0000\u0000\u0309\u02f1\u0001\u0000"+
		"\u0000\u0000\u0309\u02f2\u0001\u0000\u0000\u0000\u0309\u0303\u0001\u0000"+
		"\u0000\u0000\u0309\u0304\u0001\u0000\u0000\u0000\u0309\u0305\u0001\u0000"+
		"\u0000\u0000\u0309\u0306\u0001\u0000\u0000\u0000\u0309\u0307\u0001\u0000"+
		"\u0000\u0000\u0309\u0308\u0001\u0000\u0000\u0000\u030a\u007f\u0001\u0000"+
		"\u0000\u0000\u030b\u030c\u0005E\u0000\u0000\u030c\u030d\u0003(\u0014\u0000"+
		"\u030d\u030e\u0005D\u0000\u0000\u030e\u0310\u0001\u0000\u0000\u0000\u030f"+
		"\u030b\u0001\u0000\u0000\u0000\u030f\u0310\u0001\u0000\u0000\u0000\u0310"+
		"\u0311\u0001\u0000\u0000\u0000\u0311\u0312\u0005M\u0000\u0000\u0312\u0313"+
		"\u0003\u0082A\u0000\u0313\u0314\u0005N\u0000\u0000\u0314\u0081\u0001\u0000"+
		"\u0000\u0000\u0315\u031a\u0003L&\u0000\u0316\u0317\u0005P\u0000\u0000"+
		"\u0317\u0319\u0003L&\u0000\u0318\u0316\u0001\u0000\u0000\u0000\u0319\u031c"+
		"\u0001\u0000\u0000\u0000\u031a\u0318\u0001\u0000\u0000\u0000\u031a\u031b"+
		"\u0001\u0000\u0000\u0000\u031b\u031e\u0001\u0000\u0000\u0000\u031c\u031a"+
		"\u0001\u0000\u0000\u0000\u031d\u0315\u0001\u0000\u0000\u0000\u031d\u031e"+
		"\u0001\u0000\u0000\u0000\u031e\u0083\u0001\u0000\u0000\u0000\u031f\u0324"+
		"\u0003\u0086C\u0000\u0320\u0321\u0005P\u0000\u0000\u0321\u0323\u0003\u0086"+
		"C\u0000\u0322\u0320\u0001\u0000\u0000\u0000\u0323\u0326\u0001\u0000\u0000"+
		"\u0000\u0324\u0322\u0001\u0000\u0000\u0000\u0324\u0325\u0001\u0000\u0000"+
		"\u0000\u0325\u0328\u0001\u0000\u0000\u0000\u0326\u0324\u0001\u0000\u0000"+
		"\u0000\u0327\u031f\u0001\u0000\u0000\u0000\u0327\u0328\u0001\u0000\u0000"+
		"\u0000\u0328\u0085\u0001\u0000\u0000\u0000\u0329\u032a\u0003\u008eG\u0000"+
		"\u032a\u032b\u0005G\u0000\u0000\u032b\u032c\u0003L&\u0000\u032c\u0087"+
		"\u0001\u0000\u0000\u0000\u032d\u032e\u0005\u0004\u0000\u0000\u032e\u032f"+
		"\u0003(\u0014\u0000\u032f\u0089\u0001\u0000\u0000\u0000\u0330\u0335\u0003"+
		"\u008cF\u0000\u0331\u0332\u0005P\u0000\u0000\u0332\u0334\u0003\u008cF"+
		"\u0000\u0333\u0331\u0001\u0000\u0000\u0000\u0334\u0337\u0001\u0000\u0000"+
		"\u0000\u0335\u0333\u0001\u0000\u0000\u0000\u0335\u0336\u0001\u0000\u0000"+
		"\u0000\u0336\u0339\u0001\u0000\u0000\u0000\u0337\u0335\u0001\u0000\u0000"+
		"\u0000\u0338\u0330\u0001\u0000\u0000\u0000\u0338\u0339\u0001\u0000\u0000"+
		"\u0000\u0339\u008b\u0001\u0000\u0000\u0000\u033a\u033d\u0003\u008eG\u0000"+
		"\u033b\u033c\u0005G\u0000\u0000\u033c\u033e\u0003(\u0014\u0000\u033d\u033b"+
		"\u0001\u0000\u0000\u0000\u033d\u033e\u0001\u0000\u0000\u0000\u033e\u008d"+
		"\u0001\u0000\u0000\u0000\u033f\u0346\u0005\\\u0000\u0000\u0340\u0346\u0005"+
		"\u0001\u0000\u0000\u0341\u0346\u0005\u0002\u0000\u0000\u0342\u0343\u0005"+
		"V\u0000\u0000\u0343\u0346\u0003\u0090H\u0000\u0344\u0346\u0005Q\u0000"+
		"\u0000\u0345\u033f\u0001\u0000\u0000\u0000\u0345\u0340\u0001\u0000\u0000"+
		"\u0000\u0345\u0341\u0001\u0000\u0000\u0000\u0345\u0342\u0001\u0000\u0000"+
		"\u0000\u0345\u0344\u0001\u0000\u0000\u0000\u0346\u008f\u0001\u0000\u0000"+
		"\u0000\u0347\u0348\u0007\u0005\u0000\u0000\u0348\u0091\u0001\u0000\u0000"+
		"\u0000d\u0095\u009b\u00a8\u00af\u00b5\u00bc\u00c1\u00c8\u00cd\u00d2\u00d8"+
		"\u00de\u00e4\u00ea\u00f5\u00f9\u0101\u0107\u0111\u0114\u0118\u0120\u0123"+
		"\u0129\u0132\u0138\u013e\u0144\u014a\u0154\u0159\u015f\u0165\u016f\u0175"+
		"\u0178\u017f\u0182\u018a\u01a0\u01a4\u01b0\u01b3\u01ba\u01bd\u01c5\u01c8"+
		"\u01d2\u01d5\u01de\u01e5\u01ea\u01ef\u01f2\u01f8\u0200\u0205\u0208\u021f"+
		"\u0224\u0228\u022f\u0235\u023f\u0245\u0250\u0253\u025f\u0268\u026f\u027a"+
		"\u0285\u0289\u028d\u0292\u029b\u02ad\u02b2\u02b7\u02bc\u02c1\u02c6\u02c9"+
		"\u02d1\u02d7\u02de\u02ea\u02f7\u02fb\u0301\u0309\u030f\u031a\u031d\u0324"+
		"\u0327\u0335\u0338\u033d\u0345";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}