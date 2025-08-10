// Generated from C:/Users/Morit/Documents/Projects/Java/ttyl/compiler/grammar/language/KarinaParser.g4 by ANTLR 4.13.2
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
		EXPR=1, ANY=2, TYPE=3, FN=4, IS=5, IN=6, AS=7, NULL=8, IMPORT=9, EXTENDS=10, 
		EXTEND=11, MATCH=12, OVERRIDE=13, NATIVE=14, TRUE=15, FALSE=16, VIRTUAL=17, 
		BREAK=18, RETURN=19, YIELD=20, STRUCT=21, STATIC=22, THROW=23, TRAIT=24, 
		IMPL=25, ENUM=26, CLASS=27, LET=28, IF=29, CONST=30, ELSE=31, WHILE=32, 
		FOR=33, SUPER=34, WHERE=35, INTERFACE=36, SELF=37, INT=38, MUT=39, LONG=40, 
		BYTE=41, CHAR=42, DOUBLE=43, SHORT=44, STRING=45, FLOAT=46, BOOL=47, VOID=48, 
		JSON=49, CONTINUE=50, ARROW_RIGHT=51, ARROW_RIGHT_BOLD=52, GREATER_EQULAS=53, 
		SMALLER_EQUALS=54, EQUALS=55, STRICT_EQUALS=56, STRICT_NOT_EQUALS=57, 
		NOT_EQUALS=58, AND_AND=59, OR_OR=60, CHAR_PLIS=61, CHAR_MINUS=62, CHAR_STAR=63, 
		CHAR_R_SLASH=64, CHAR_PERCENT=65, CHAR_OR=66, CHAR_XOR=67, CHAR_TILDE=68, 
		CHAR_GREATER=69, CHAR_SMALLER=70, CHAR_EXCLAMATION=71, CHAR_COLON=72, 
		CHAR_COLON_COLON=73, CHAR_EQUAL=74, CHAR_L_PAREN=75, CHAR_R_PAREN=76, 
		CHAR_L_BRACE=77, CHAR_R_BRACE=78, CHAR_L_BRACKET=79, CHAR_R_BRACKET=80, 
		CHAR_AT=81, CHAR_COMMA=82, CHAR_UNDER=83, CHAR_AND=84, CHAR_DOT=85, CHAR_QUESTION=86, 
		CHAR_SEMICOLON=87, CHAR_ESCAPE=88, STRING_LITERAL=89, CHAR_LITERAL=90, 
		NUMBER=91, INTEGER_NUMBER=92, FLOAT_NUMBER=93, ID=94, WS=95, COMMENT=96, 
		LINE_COMMENT=97;
	public static final int
		RULE_unit = 0, RULE_import_ = 1, RULE_commaWordChain = 2, RULE_item = 3, 
		RULE_function = 4, RULE_const = 5, RULE_struct = 6, RULE_implementation = 7, 
		RULE_boundWhere = 8, RULE_genericWithBounds = 9, RULE_genericWithBound = 10, 
		RULE_bounds = 11, RULE_implBounds = 12, RULE_extendsBounds = 13, RULE_boundList = 14, 
		RULE_field = 15, RULE_enum = 16, RULE_enumMember = 17, RULE_interface = 18, 
		RULE_interfaceExtension = 19, RULE_selfParameterList = 20, RULE_parameterList = 21, 
		RULE_parameter = 22, RULE_type = 23, RULE_typePostFix = 24, RULE_typeInner = 25, 
		RULE_structType = 26, RULE_arrayType = 27, RULE_functionType = 28, RULE_typeList = 29, 
		RULE_genericHint = 30, RULE_genericHintDefinition = 31, RULE_dotWordChain = 32, 
		RULE_annotation = 33, RULE_jsonObj = 34, RULE_jsonPair = 35, RULE_jsonArray = 36, 
		RULE_jsonExpression = 37, RULE_jsonType = 38, RULE_jsonMethod = 39, RULE_jsonValue = 40, 
		RULE_block = 41, RULE_exprWithBlock = 42, RULE_expression = 43, RULE_varDef = 44, 
		RULE_closure = 45, RULE_interfaceImpl = 46, RULE_structTypeList = 47, 
		RULE_match = 48, RULE_matchCase = 49, RULE_matchInstance = 50, RULE_matchDefault = 51, 
		RULE_if = 52, RULE_elseExpr = 53, RULE_isShort = 54, RULE_while = 55, 
		RULE_for = 56, RULE_throw = 57, RULE_conditionalOrExpression = 58, RULE_conditionalAndExpression = 59, 
		RULE_equalityExpression = 60, RULE_relationalExpression = 61, RULE_additiveExpression = 62, 
		RULE_multiplicativeExpression = 63, RULE_unaryExpression = 64, RULE_factor = 65, 
		RULE_postFix = 66, RULE_object = 67, RULE_array = 68, RULE_superCall = 69, 
		RULE_expressionList = 70, RULE_initList = 71, RULE_memberInit = 72, RULE_isInstanceOf = 73, 
		RULE_optTypeList = 74, RULE_optTypeName = 75, RULE_id = 76, RULE_escaped = 77;
	private static String[] makeRuleNames() {
		return new String[] {
			"unit", "import_", "commaWordChain", "item", "function", "const", "struct", 
			"implementation", "boundWhere", "genericWithBounds", "genericWithBound", 
			"bounds", "implBounds", "extendsBounds", "boundList", "field", "enum", 
			"enumMember", "interface", "interfaceExtension", "selfParameterList", 
			"parameterList", "parameter", "type", "typePostFix", "typeInner", "structType", 
			"arrayType", "functionType", "typeList", "genericHint", "genericHintDefinition", 
			"dotWordChain", "annotation", "jsonObj", "jsonPair", "jsonArray", "jsonExpression", 
			"jsonType", "jsonMethod", "jsonValue", "block", "exprWithBlock", "expression", 
			"varDef", "closure", "interfaceImpl", "structTypeList", "match", "matchCase", 
			"matchInstance", "matchDefault", "if", "elseExpr", "isShort", "while", 
			"for", "throw", "conditionalOrExpression", "conditionalAndExpression", 
			"equalityExpression", "relationalExpression", "additiveExpression", "multiplicativeExpression", 
			"unaryExpression", "factor", "postFix", "object", "array", "superCall", 
			"expressionList", "initList", "memberInit", "isInstanceOf", "optTypeList", 
			"optTypeName", "id", "escaped"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'expr'", "'any'", "'type'", "'fn'", "'is'", "'in'", "'as'", "'null'", 
			"'import'", "'extends'", "'extend'", "'match'", "'override'", "'native'", 
			"'true'", "'false'", "'virtual'", "'break'", "'return'", "'yield'", "'struct'", 
			"'static'", "'throw'", "'trait'", "'impl'", "'enum'", "'class'", "'let'", 
			"'if'", "'const'", "'else'", "'while'", "'for'", "'super'", "'where'", 
			"'interface'", "'self'", "'int'", "'mut'", "'long'", "'byte'", "'char'", 
			"'double'", "'short'", "'string'", "'float'", "'bool'", "'void'", "'json'", 
			"'continue'", "'->'", "'=>'", "'>='", "'<='", "'=='", "'==='", "'!=='", 
			"'!='", "'&&'", "'||'", "'+'", "'-'", "'*'", "'/'", "'%'", "'|'", "'^'", 
			"'~'", "'>'", "'<'", "'!'", "':'", "'::'", "'='", "'('", "')'", "'{'", 
			"'}'", "'['", "']'", "'@'", "','", "'_'", "'&'", "'.'", "'?'", "';'", 
			"'\\'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "EXPR", "ANY", "TYPE", "FN", "IS", "IN", "AS", "NULL", "IMPORT", 
			"EXTENDS", "EXTEND", "MATCH", "OVERRIDE", "NATIVE", "TRUE", "FALSE", 
			"VIRTUAL", "BREAK", "RETURN", "YIELD", "STRUCT", "STATIC", "THROW", "TRAIT", 
			"IMPL", "ENUM", "CLASS", "LET", "IF", "CONST", "ELSE", "WHILE", "FOR", 
			"SUPER", "WHERE", "INTERFACE", "SELF", "INT", "MUT", "LONG", "BYTE", 
			"CHAR", "DOUBLE", "SHORT", "STRING", "FLOAT", "BOOL", "VOID", "JSON", 
			"CONTINUE", "ARROW_RIGHT", "ARROW_RIGHT_BOLD", "GREATER_EQULAS", "SMALLER_EQUALS", 
			"EQUALS", "STRICT_EQUALS", "STRICT_NOT_EQUALS", "NOT_EQUALS", "AND_AND", 
			"OR_OR", "CHAR_PLIS", "CHAR_MINUS", "CHAR_STAR", "CHAR_R_SLASH", "CHAR_PERCENT", 
			"CHAR_OR", "CHAR_XOR", "CHAR_TILDE", "CHAR_GREATER", "CHAR_SMALLER", 
			"CHAR_EXCLAMATION", "CHAR_COLON", "CHAR_COLON_COLON", "CHAR_EQUAL", "CHAR_L_PAREN", 
			"CHAR_R_PAREN", "CHAR_L_BRACE", "CHAR_R_BRACE", "CHAR_L_BRACKET", "CHAR_R_BRACKET", 
			"CHAR_AT", "CHAR_COMMA", "CHAR_UNDER", "CHAR_AND", "CHAR_DOT", "CHAR_QUESTION", 
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
			setState(159);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==IMPORT) {
				{
				{
				setState(156);
				import_();
				}
				}
				setState(161);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(165);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 68792877072L) != 0) || _la==CHAR_AT) {
				{
				{
				setState(162);
				item();
				}
				}
				setState(167);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(168);
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
		public TerminalNode AS() { return getToken(KarinaParser.AS, 0); }
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
			setState(170);
			match(IMPORT);
			setState(171);
			dotWordChain();
			setState(180);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CHAR_STAR:
				{
				setState(172);
				match(CHAR_STAR);
				}
				break;
			case EXPR:
			case TYPE:
			case CHAR_UNDER:
			case CHAR_ESCAPE:
			case ID:
				{
				setState(173);
				id();
				}
				break;
			case CHAR_L_BRACE:
				{
				setState(174);
				match(CHAR_L_BRACE);
				setState(175);
				commaWordChain();
				setState(176);
				match(CHAR_R_BRACE);
				}
				break;
			case AS:
				{
				setState(178);
				match(AS);
				setState(179);
				id();
				}
				break;
			case EOF:
			case FN:
			case IMPORT:
			case STRUCT:
			case STATIC:
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
			setState(182);
			id();
			setState(187);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==CHAR_COMMA) {
				{
				{
				setState(183);
				match(CHAR_COMMA);
				setState(184);
				id();
				}
				}
				setState(189);
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
		public ConstContext const_() {
			return getRuleContext(ConstContext.class,0);
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
			setState(193);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==CHAR_AT) {
				{
				{
				setState(190);
				annotation();
				}
				}
				setState(195);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(201);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case FN:
				{
				setState(196);
				function();
				}
				break;
			case STRUCT:
				{
				setState(197);
				struct();
				}
				break;
			case ENUM:
				{
				setState(198);
				enum_();
				}
				break;
			case INTERFACE:
				{
				setState(199);
				interface_();
				}
				break;
			case STATIC:
				{
				setState(200);
				const_();
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
		public TerminalNode CHAR_L_PAREN() { return getToken(KarinaParser.CHAR_L_PAREN, 0); }
		public SelfParameterListContext selfParameterList() {
			return getRuleContext(SelfParameterListContext.class,0);
		}
		public TerminalNode CHAR_R_PAREN() { return getToken(KarinaParser.CHAR_R_PAREN, 0); }
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
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
			setState(203);
			match(FN);
			setState(205);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EXPR || _la==TYPE || ((((_la - 83)) & ~0x3f) == 0 && ((1L << (_la - 83)) & 2081L) != 0)) {
				{
				setState(204);
				id();
				}
			}

			setState(208);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CHAR_SMALLER) {
				{
				setState(207);
				genericHintDefinition();
				}
			}

			setState(210);
			match(CHAR_L_PAREN);
			setState(211);
			selfParameterList();
			setState(212);
			match(CHAR_R_PAREN);
			setState(215);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ARROW_RIGHT) {
				{
				setState(213);
				match(ARROW_RIGHT);
				setState(214);
				type();
				}
			}

			setState(220);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CHAR_EQUAL:
				{
				setState(217);
				match(CHAR_EQUAL);
				setState(218);
				expression();
				}
				break;
			case CHAR_L_BRACE:
				{
				setState(219);
				block();
				}
				break;
			case EOF:
			case FN:
			case STRUCT:
			case STATIC:
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
	public static class ConstContext extends ParserRuleContext {
		public TerminalNode STATIC() { return getToken(KarinaParser.STATIC, 0); }
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public TerminalNode CHAR_COLON() { return getToken(KarinaParser.CHAR_COLON, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode CHAR_EQUAL() { return getToken(KarinaParser.CHAR_EQUAL, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode MUT() { return getToken(KarinaParser.MUT, 0); }
		public ConstContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_const; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterConst(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitConst(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitConst(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConstContext const_() throws RecognitionException {
		ConstContext _localctx = new ConstContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_const);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(222);
			match(STATIC);
			setState(223);
			id();
			setState(224);
			match(CHAR_COLON);
			setState(226);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==MUT) {
				{
				setState(225);
				match(MUT);
				}
			}

			setState(228);
			type();
			setState(229);
			match(CHAR_EQUAL);
			setState(230);
			expression();
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
		public GenericHintDefinitionContext genericHintDefinition() {
			return getRuleContext(GenericHintDefinitionContext.class,0);
		}
		public TerminalNode CHAR_L_BRACE() { return getToken(KarinaParser.CHAR_L_BRACE, 0); }
		public TerminalNode CHAR_R_BRACE() { return getToken(KarinaParser.CHAR_R_BRACE, 0); }
		public List<ConstContext> const_() {
			return getRuleContexts(ConstContext.class);
		}
		public ConstContext const_(int i) {
			return getRuleContext(ConstContext.class,i);
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
		enterRule(_localctx, 12, RULE_struct);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(232);
			match(STRUCT);
			setState(233);
			id();
			setState(235);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CHAR_SMALLER) {
				{
				setState(234);
				genericHintDefinition();
				}
			}

			setState(269);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CHAR_L_BRACE) {
				{
				setState(237);
				match(CHAR_L_BRACE);
				setState(241);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==STATIC) {
					{
					{
					setState(238);
					const_();
					}
					}
					setState(243);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(247);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==EXPR || _la==TYPE || ((((_la - 83)) & ~0x3f) == 0 && ((1L << (_la - 83)) & 2081L) != 0)) {
					{
					{
					setState(244);
					field();
					}
					}
					setState(249);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(253);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==FN) {
					{
					{
					setState(250);
					function();
					}
					}
					setState(255);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(259);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==IMPL) {
					{
					{
					setState(256);
					implementation();
					}
					}
					setState(261);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(265);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==WHERE) {
					{
					{
					setState(262);
					boundWhere();
					}
					}
					setState(267);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(268);
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
		enterRule(_localctx, 14, RULE_implementation);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(271);
			match(IMPL);
			setState(272);
			structType();
			setState(281);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CHAR_L_BRACE) {
				{
				setState(273);
				match(CHAR_L_BRACE);
				setState(277);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==FN) {
					{
					{
					setState(274);
					function();
					}
					}
					setState(279);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(280);
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
		public TerminalNode CHAR_L_BRACE() { return getToken(KarinaParser.CHAR_L_BRACE, 0); }
		public TerminalNode CHAR_R_BRACE() { return getToken(KarinaParser.CHAR_R_BRACE, 0); }
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
		enterRule(_localctx, 16, RULE_boundWhere);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(283);
			match(WHERE);
			setState(284);
			genericWithBounds();
			{
			setState(285);
			match(CHAR_L_BRACE);
			setState(289);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FN) {
				{
				{
				setState(286);
				function();
				}
				}
				setState(291);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(292);
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
		enterRule(_localctx, 18, RULE_genericWithBounds);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(302);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EXPR || _la==TYPE || ((((_la - 83)) & ~0x3f) == 0 && ((1L << (_la - 83)) & 2081L) != 0)) {
				{
				setState(294);
				genericWithBound();
				setState(299);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==CHAR_COMMA) {
					{
					{
					setState(295);
					match(CHAR_COMMA);
					setState(296);
					genericWithBound();
					}
					}
					setState(301);
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
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public TerminalNode CHAR_COLON() { return getToken(KarinaParser.CHAR_COLON, 0); }
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
		enterRule(_localctx, 20, RULE_genericWithBound);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(304);
			id();
			setState(305);
			match(CHAR_COLON);
			setState(306);
			bounds();
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
		public ExtendsBoundsContext extendsBounds() {
			return getRuleContext(ExtendsBoundsContext.class,0);
		}
		public ImplBoundsContext implBounds() {
			return getRuleContext(ImplBoundsContext.class,0);
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
		enterRule(_localctx, 22, RULE_bounds);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(309);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EXTENDS) {
				{
				setState(308);
				extendsBounds();
				}
			}

			setState(312);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IMPL) {
				{
				setState(311);
				implBounds();
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
	public static class ImplBoundsContext extends ParserRuleContext {
		public TerminalNode IMPL() { return getToken(KarinaParser.IMPL, 0); }
		public BoundListContext boundList() {
			return getRuleContext(BoundListContext.class,0);
		}
		public ImplBoundsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_implBounds; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterImplBounds(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitImplBounds(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitImplBounds(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ImplBoundsContext implBounds() throws RecognitionException {
		ImplBoundsContext _localctx = new ImplBoundsContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_implBounds);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(314);
			match(IMPL);
			setState(315);
			boundList();
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
	public static class ExtendsBoundsContext extends ParserRuleContext {
		public TerminalNode EXTENDS() { return getToken(KarinaParser.EXTENDS, 0); }
		public StructTypeContext structType() {
			return getRuleContext(StructTypeContext.class,0);
		}
		public ExtendsBoundsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_extendsBounds; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterExtendsBounds(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitExtendsBounds(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitExtendsBounds(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExtendsBoundsContext extendsBounds() throws RecognitionException {
		ExtendsBoundsContext _localctx = new ExtendsBoundsContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_extendsBounds);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(317);
			match(EXTENDS);
			setState(318);
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
	public static class BoundListContext extends ParserRuleContext {
		public List<StructTypeContext> structType() {
			return getRuleContexts(StructTypeContext.class);
		}
		public StructTypeContext structType(int i) {
			return getRuleContext(StructTypeContext.class,i);
		}
		public List<TerminalNode> CHAR_PLIS() { return getTokens(KarinaParser.CHAR_PLIS); }
		public TerminalNode CHAR_PLIS(int i) {
			return getToken(KarinaParser.CHAR_PLIS, i);
		}
		public BoundListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_boundList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterBoundList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitBoundList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitBoundList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BoundListContext boundList() throws RecognitionException {
		BoundListContext _localctx = new BoundListContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_boundList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(328);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EXPR || _la==TYPE || ((((_la - 83)) & ~0x3f) == 0 && ((1L << (_la - 83)) & 2081L) != 0)) {
				{
				setState(320);
				structType();
				setState(325);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==CHAR_PLIS) {
					{
					{
					setState(321);
					match(CHAR_PLIS);
					setState(322);
					structType();
					}
					}
					setState(327);
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
	public static class FieldContext extends ParserRuleContext {
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public TerminalNode CHAR_COLON() { return getToken(KarinaParser.CHAR_COLON, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode MUT() { return getToken(KarinaParser.MUT, 0); }
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
		enterRule(_localctx, 30, RULE_field);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(330);
			id();
			setState(331);
			match(CHAR_COLON);
			setState(333);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==MUT) {
				{
				setState(332);
				match(MUT);
				}
			}

			setState(335);
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
		enterRule(_localctx, 32, RULE_enum);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(337);
			match(ENUM);
			setState(338);
			id();
			setState(340);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CHAR_SMALLER) {
				{
				setState(339);
				genericHintDefinition();
				}
			}

			setState(342);
			match(CHAR_L_BRACE);
			setState(346);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==EXPR || _la==TYPE || ((((_la - 83)) & ~0x3f) == 0 && ((1L << (_la - 83)) & 2081L) != 0)) {
				{
				{
				setState(343);
				enumMember();
				}
				}
				setState(348);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(352);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FN) {
				{
				{
				setState(349);
				function();
				}
				}
				setState(354);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(358);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==IMPL) {
				{
				{
				setState(355);
				implementation();
				}
				}
				setState(360);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(364);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==WHERE) {
				{
				{
				setState(361);
				boundWhere();
				}
				}
				setState(366);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(367);
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
		enterRule(_localctx, 34, RULE_enumMember);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(369);
			id();
			setState(374);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CHAR_L_PAREN) {
				{
				setState(370);
				match(CHAR_L_PAREN);
				setState(371);
				parameterList();
				setState(372);
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
		public GenericHintDefinitionContext genericHintDefinition() {
			return getRuleContext(GenericHintDefinitionContext.class,0);
		}
		public TerminalNode CHAR_L_BRACE() { return getToken(KarinaParser.CHAR_L_BRACE, 0); }
		public TerminalNode CHAR_R_BRACE() { return getToken(KarinaParser.CHAR_R_BRACE, 0); }
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
		enterRule(_localctx, 36, RULE_interface);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(376);
			match(INTERFACE);
			setState(377);
			id();
			setState(379);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CHAR_SMALLER) {
				{
				setState(378);
				genericHintDefinition();
				}
			}

			setState(395);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CHAR_L_BRACE) {
				{
				setState(381);
				match(CHAR_L_BRACE);
				setState(385);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==FN) {
					{
					{
					setState(382);
					function();
					}
					}
					setState(387);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(391);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==IMPL) {
					{
					{
					setState(388);
					interfaceExtension();
					}
					}
					setState(393);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(394);
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
		enterRule(_localctx, 38, RULE_interfaceExtension);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(397);
			match(IMPL);
			setState(398);
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
		enterRule(_localctx, 40, RULE_selfParameterList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(411);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 137438953482L) != 0) || ((((_la - 83)) & ~0x3f) == 0 && ((1L << (_la - 83)) & 2081L) != 0)) {
				{
				setState(402);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case EXPR:
				case TYPE:
				case CHAR_UNDER:
				case CHAR_ESCAPE:
				case ID:
					{
					setState(400);
					parameter();
					}
					break;
				case SELF:
					{
					setState(401);
					match(SELF);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(408);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==CHAR_COMMA) {
					{
					{
					setState(404);
					match(CHAR_COMMA);
					setState(405);
					parameter();
					}
					}
					setState(410);
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
		enterRule(_localctx, 42, RULE_parameterList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(421);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EXPR || _la==TYPE || ((((_la - 83)) & ~0x3f) == 0 && ((1L << (_la - 83)) & 2081L) != 0)) {
				{
				setState(413);
				parameter();
				setState(418);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==CHAR_COMMA) {
					{
					{
					setState(414);
					match(CHAR_COMMA);
					setState(415);
					parameter();
					}
					}
					setState(420);
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
		enterRule(_localctx, 44, RULE_parameter);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(423);
			id();
			setState(424);
			match(CHAR_COLON);
			setState(425);
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
		enterRule(_localctx, 46, RULE_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(427);
			typeInner();
			setState(429);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,43,_ctx) ) {
			case 1:
				{
				setState(428);
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
		public TypeInnerContext typeInner() {
			return getRuleContext(TypeInnerContext.class,0);
		}
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
		enterRule(_localctx, 48, RULE_typePostFix);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(431);
			match(CHAR_QUESTION);
			setState(433);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,44,_ctx) ) {
			case 1:
				{
				setState(432);
				typeInner();
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
		public TerminalNode ANY() { return getToken(KarinaParser.ANY, 0); }
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
		enterRule(_localctx, 50, RULE_typeInner);
		try {
			setState(453);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case VOID:
				enterOuterAlt(_localctx, 1);
				{
				setState(435);
				match(VOID);
				}
				break;
			case INT:
				enterOuterAlt(_localctx, 2);
				{
				setState(436);
				match(INT);
				}
				break;
			case DOUBLE:
				enterOuterAlt(_localctx, 3);
				{
				setState(437);
				match(DOUBLE);
				}
				break;
			case SHORT:
				enterOuterAlt(_localctx, 4);
				{
				setState(438);
				match(SHORT);
				}
				break;
			case BYTE:
				enterOuterAlt(_localctx, 5);
				{
				setState(439);
				match(BYTE);
				}
				break;
			case CHAR:
				enterOuterAlt(_localctx, 6);
				{
				setState(440);
				match(CHAR);
				}
				break;
			case LONG:
				enterOuterAlt(_localctx, 7);
				{
				setState(441);
				match(LONG);
				}
				break;
			case FLOAT:
				enterOuterAlt(_localctx, 8);
				{
				setState(442);
				match(FLOAT);
				}
				break;
			case BOOL:
				enterOuterAlt(_localctx, 9);
				{
				setState(443);
				match(BOOL);
				}
				break;
			case STRING:
				enterOuterAlt(_localctx, 10);
				{
				setState(444);
				match(STRING);
				}
				break;
			case ANY:
				enterOuterAlt(_localctx, 11);
				{
				setState(445);
				match(ANY);
				}
				break;
			case EXPR:
			case TYPE:
			case CHAR_UNDER:
			case CHAR_ESCAPE:
			case ID:
				enterOuterAlt(_localctx, 12);
				{
				setState(446);
				structType();
				}
				break;
			case CHAR_L_BRACKET:
				enterOuterAlt(_localctx, 13);
				{
				setState(447);
				arrayType();
				}
				break;
			case FN:
				enterOuterAlt(_localctx, 14);
				{
				setState(448);
				functionType();
				}
				break;
			case CHAR_L_PAREN:
				enterOuterAlt(_localctx, 15);
				{
				setState(449);
				match(CHAR_L_PAREN);
				setState(450);
				type();
				setState(451);
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
		enterRule(_localctx, 52, RULE_structType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(455);
			dotWordChain();
			setState(457);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,46,_ctx) ) {
			case 1:
				{
				setState(456);
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
		enterRule(_localctx, 54, RULE_arrayType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(459);
			match(CHAR_L_BRACKET);
			setState(460);
			type();
			setState(461);
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
		enterRule(_localctx, 56, RULE_functionType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(463);
			match(FN);
			setState(464);
			match(CHAR_L_PAREN);
			setState(465);
			typeList();
			setState(466);
			match(CHAR_R_PAREN);
			setState(469);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ARROW_RIGHT) {
				{
				setState(467);
				match(ARROW_RIGHT);
				setState(468);
				type();
				}
			}

			setState(472);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,48,_ctx) ) {
			case 1:
				{
				setState(471);
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
		enterRule(_localctx, 58, RULE_typeList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(482);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 562125319700510L) != 0) || ((((_la - 75)) & ~0x3f) == 0 && ((1L << (_la - 75)) & 532753L) != 0)) {
				{
				setState(474);
				type();
				setState(479);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==CHAR_COMMA) {
					{
					{
					setState(475);
					match(CHAR_COMMA);
					setState(476);
					type();
					}
					}
					setState(481);
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
		enterRule(_localctx, 60, RULE_genericHint);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(484);
			match(CHAR_SMALLER);
			setState(493);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 562125319700510L) != 0) || ((((_la - 75)) & ~0x3f) == 0 && ((1L << (_la - 75)) & 532753L) != 0)) {
				{
				setState(485);
				type();
				setState(490);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==CHAR_COMMA) {
					{
					{
					setState(486);
					match(CHAR_COMMA);
					setState(487);
					type();
					}
					}
					setState(492);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(495);
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
		enterRule(_localctx, 62, RULE_genericHintDefinition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(497);
			match(CHAR_SMALLER);
			setState(506);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EXPR || _la==TYPE || ((((_la - 83)) & ~0x3f) == 0 && ((1L << (_la - 83)) & 2081L) != 0)) {
				{
				setState(498);
				id();
				setState(503);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==CHAR_COMMA) {
					{
					{
					setState(499);
					match(CHAR_COMMA);
					setState(500);
					id();
					}
					}
					setState(505);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(508);
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
		public List<TerminalNode> CHAR_COLON_COLON() { return getTokens(KarinaParser.CHAR_COLON_COLON); }
		public TerminalNode CHAR_COLON_COLON(int i) {
			return getToken(KarinaParser.CHAR_COLON_COLON, i);
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
		enterRule(_localctx, 64, RULE_dotWordChain);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(510);
			id();
			setState(515);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==CHAR_COLON_COLON) {
				{
				{
				setState(511);
				match(CHAR_COLON_COLON);
				setState(512);
				id();
				}
				}
				setState(517);
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
		enterRule(_localctx, 66, RULE_annotation);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(518);
			match(CHAR_AT);
			setState(519);
			id();
			setState(522);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CHAR_EQUAL) {
				{
				setState(520);
				match(CHAR_EQUAL);
				setState(521);
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
		enterRule(_localctx, 68, RULE_jsonObj);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(524);
			match(CHAR_L_BRACE);
			setState(535);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EXPR || _la==TYPE || ((((_la - 83)) & ~0x3f) == 0 && ((1L << (_la - 83)) & 2145L) != 0)) {
				{
				setState(525);
				jsonPair();
				setState(532);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==EXPR || _la==TYPE || ((((_la - 82)) & ~0x3f) == 0 && ((1L << (_la - 82)) & 4291L) != 0)) {
					{
					{
					setState(527);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==CHAR_COMMA) {
						{
						setState(526);
						match(CHAR_COMMA);
						}
					}

					setState(529);
					jsonPair();
					}
					}
					setState(534);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(537);
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
		enterRule(_localctx, 70, RULE_jsonPair);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(541);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STRING_LITERAL:
				{
				setState(539);
				match(STRING_LITERAL);
				}
				break;
			case EXPR:
			case TYPE:
			case CHAR_UNDER:
			case CHAR_ESCAPE:
			case ID:
				{
				setState(540);
				id();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(543);
			match(CHAR_COLON);
			setState(544);
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
		enterRule(_localctx, 72, RULE_jsonArray);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(546);
			match(CHAR_L_BRACKET);
			setState(557);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 98586L) != 0) || ((((_la - 77)) & ~0x3f) == 0 && ((1L << (_la - 77)) & 20485L) != 0)) {
				{
				setState(547);
				jsonValue();
				setState(554);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 98586L) != 0) || ((((_la - 77)) & ~0x3f) == 0 && ((1L << (_la - 77)) & 20517L) != 0)) {
					{
					{
					setState(549);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==CHAR_COMMA) {
						{
						setState(548);
						match(CHAR_COMMA);
						}
					}

					setState(551);
					jsonValue();
					}
					}
					setState(556);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(559);
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
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
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
		enterRule(_localctx, 74, RULE_jsonExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(561);
			match(EXPR);
			setState(562);
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
		enterRule(_localctx, 76, RULE_jsonType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(564);
			match(TYPE);
			setState(565);
			match(CHAR_L_BRACE);
			setState(566);
			type();
			setState(567);
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
	public static class JsonMethodContext extends ParserRuleContext {
		public TerminalNode FN() { return getToken(KarinaParser.FN, 0); }
		public TerminalNode CHAR_L_BRACE() { return getToken(KarinaParser.CHAR_L_BRACE, 0); }
		public FunctionContext function() {
			return getRuleContext(FunctionContext.class,0);
		}
		public TerminalNode CHAR_R_BRACE() { return getToken(KarinaParser.CHAR_R_BRACE, 0); }
		public JsonMethodContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_jsonMethod; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterJsonMethod(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitJsonMethod(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitJsonMethod(this);
			else return visitor.visitChildren(this);
		}
	}

	public final JsonMethodContext jsonMethod() throws RecognitionException {
		JsonMethodContext _localctx = new JsonMethodContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_jsonMethod);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(569);
			match(FN);
			setState(570);
			match(CHAR_L_BRACE);
			setState(571);
			function();
			setState(572);
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
		public JsonMethodContext jsonMethod() {
			return getRuleContext(JsonMethodContext.class,0);
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
		enterRule(_localctx, 80, RULE_jsonValue);
		try {
			setState(584);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STRING_LITERAL:
				enterOuterAlt(_localctx, 1);
				{
				setState(574);
				match(STRING_LITERAL);
				}
				break;
			case NUMBER:
				enterOuterAlt(_localctx, 2);
				{
				setState(575);
				match(NUMBER);
				}
				break;
			case CHAR_L_BRACE:
				enterOuterAlt(_localctx, 3);
				{
				setState(576);
				jsonObj();
				}
				break;
			case CHAR_L_BRACKET:
				enterOuterAlt(_localctx, 4);
				{
				setState(577);
				jsonArray();
				}
				break;
			case TRUE:
				enterOuterAlt(_localctx, 5);
				{
				setState(578);
				match(TRUE);
				}
				break;
			case FALSE:
				enterOuterAlt(_localctx, 6);
				{
				setState(579);
				match(FALSE);
				}
				break;
			case NULL:
				enterOuterAlt(_localctx, 7);
				{
				setState(580);
				match(NULL);
				}
				break;
			case EXPR:
				enterOuterAlt(_localctx, 8);
				{
				setState(581);
				jsonExpression();
				}
				break;
			case TYPE:
				enterOuterAlt(_localctx, 9);
				{
				setState(582);
				jsonType();
				}
				break;
			case FN:
				enterOuterAlt(_localctx, 10);
				{
				setState(583);
				jsonMethod();
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
		enterRule(_localctx, 82, RULE_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(586);
			match(CHAR_L_BRACE);
			setState(590);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 4612812086652538906L) != 0) || ((((_la - 70)) & ~0x3f) == 0 && ((1L << (_la - 70)) & 20718115L) != 0)) {
				{
				{
				setState(587);
				expression();
				}
				}
				setState(592);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(593);
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
		enterRule(_localctx, 84, RULE_exprWithBlock);
		try {
			setState(597);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CHAR_L_BRACE:
				enterOuterAlt(_localctx, 1);
				{
				setState(595);
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
			case THROW:
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
				setState(596);
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
		enterRule(_localctx, 86, RULE_expression);
		try {
			setState(613);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LET:
				enterOuterAlt(_localctx, 1);
				{
				setState(599);
				varDef();
				}
				break;
			case FN:
				enterOuterAlt(_localctx, 2);
				{
				setState(600);
				closure();
				}
				break;
			case RETURN:
				enterOuterAlt(_localctx, 3);
				{
				setState(601);
				match(RETURN);
				setState(603);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,67,_ctx) ) {
				case 1:
					{
					setState(602);
					exprWithBlock();
					}
					break;
				}
				}
				break;
			case MATCH:
				enterOuterAlt(_localctx, 4);
				{
				setState(605);
				match();
				}
				break;
			case IF:
				enterOuterAlt(_localctx, 5);
				{
				setState(606);
				if_();
				}
				break;
			case WHILE:
				enterOuterAlt(_localctx, 6);
				{
				setState(607);
				while_();
				}
				break;
			case FOR:
				enterOuterAlt(_localctx, 7);
				{
				setState(608);
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
				setState(609);
				conditionalOrExpression();
				}
				break;
			case BREAK:
				enterOuterAlt(_localctx, 9);
				{
				setState(610);
				match(BREAK);
				}
				break;
			case CONTINUE:
				enterOuterAlt(_localctx, 10);
				{
				setState(611);
				match(CONTINUE);
				}
				break;
			case THROW:
				enterOuterAlt(_localctx, 11);
				{
				setState(612);
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
		enterRule(_localctx, 88, RULE_varDef);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(615);
			match(LET);
			setState(616);
			id();
			setState(619);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CHAR_COLON) {
				{
				setState(617);
				match(CHAR_COLON);
				setState(618);
				type();
				}
			}

			setState(621);
			match(CHAR_EQUAL);
			setState(622);
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
		enterRule(_localctx, 90, RULE_closure);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(624);
			match(FN);
			setState(625);
			match(CHAR_L_PAREN);
			setState(626);
			optTypeList();
			setState(627);
			match(CHAR_R_PAREN);
			setState(630);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ARROW_RIGHT) {
				{
				setState(628);
				match(ARROW_RIGHT);
				setState(629);
				type();
				}
			}

			setState(633);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IMPL) {
				{
				setState(632);
				interfaceImpl();
				}
			}

			setState(635);
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
		public TerminalNode CHAR_L_PAREN() { return getToken(KarinaParser.CHAR_L_PAREN, 0); }
		public TerminalNode CHAR_R_PAREN() { return getToken(KarinaParser.CHAR_R_PAREN, 0); }
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
		enterRule(_localctx, 92, RULE_interfaceImpl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(637);
			match(IMPL);
			setState(643);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case EXPR:
			case TYPE:
			case CHAR_UNDER:
			case CHAR_ESCAPE:
			case ID:
				{
				setState(638);
				structTypeList();
				}
				break;
			case CHAR_L_PAREN:
				{
				setState(639);
				match(CHAR_L_PAREN);
				setState(640);
				structTypeList();
				setState(641);
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
		enterRule(_localctx, 94, RULE_structTypeList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(645);
			structType();
			setState(650);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,73,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(646);
					match(CHAR_COMMA);
					setState(647);
					structType();
					}
					} 
				}
				setState(652);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,73,_ctx);
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
		enterRule(_localctx, 96, RULE_match);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(653);
			match(MATCH);
			setState(654);
			exprWithBlock();
			setState(655);
			match(CHAR_L_BRACE);
			setState(659);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==EXPR || _la==TYPE || ((((_la - 83)) & ~0x3f) == 0 && ((1L << (_la - 83)) & 2081L) != 0)) {
				{
				{
				setState(656);
				matchCase();
				}
				}
				setState(661);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(662);
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
		enterRule(_localctx, 98, RULE_matchCase);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(666);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,75,_ctx) ) {
			case 1:
				{
				setState(664);
				matchDefault();
				}
				break;
			case 2:
				{
				setState(665);
				matchInstance();
				}
				break;
			}
			setState(668);
			match(ARROW_RIGHT);
			setState(669);
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
		enterRule(_localctx, 100, RULE_matchInstance);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(671);
			structType();
			setState(677);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case EXPR:
			case TYPE:
			case CHAR_UNDER:
			case CHAR_ESCAPE:
			case ID:
				{
				setState(672);
				id();
				}
				break;
			case CHAR_L_PAREN:
				{
				setState(673);
				match(CHAR_L_PAREN);
				setState(674);
				optTypeList();
				setState(675);
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
		enterRule(_localctx, 102, RULE_matchDefault);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(679);
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
		enterRule(_localctx, 104, RULE_if);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(681);
			match(IF);
			setState(682);
			exprWithBlock();
			setState(688);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case EXPR:
			case TYPE:
			case CHAR_UNDER:
			case CHAR_ESCAPE:
			case ID:
				{
				setState(683);
				id();
				}
				break;
			case CHAR_L_PAREN:
				{
				setState(684);
				match(CHAR_L_PAREN);
				setState(685);
				optTypeList();
				setState(686);
				match(CHAR_R_PAREN);
				}
				break;
			case CHAR_L_BRACE:
				break;
			default:
				break;
			}
			setState(690);
			block();
			setState(692);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ELSE) {
				{
				setState(691);
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
		enterRule(_localctx, 106, RULE_elseExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(694);
			match(ELSE);
			setState(696);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IS) {
				{
				setState(695);
				isShort();
				}
			}

			setState(701);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IF:
				{
				setState(698);
				if_();
				}
				break;
			case CHAR_L_BRACE:
				{
				setState(699);
				block();
				}
				break;
			case MATCH:
				{
				setState(700);
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
		enterRule(_localctx, 108, RULE_isShort);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(703);
			match(IS);
			setState(704);
			type();
			setState(710);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case EXPR:
			case TYPE:
			case CHAR_UNDER:
			case CHAR_ESCAPE:
			case ID:
				{
				setState(705);
				id();
				}
				break;
			case CHAR_L_PAREN:
				{
				setState(706);
				match(CHAR_L_PAREN);
				setState(707);
				optTypeList();
				setState(708);
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
		enterRule(_localctx, 110, RULE_while);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(712);
			match(WHILE);
			setState(713);
			exprWithBlock();
			setState(714);
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
		public OptTypeNameContext optTypeName() {
			return getRuleContext(OptTypeNameContext.class,0);
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
		enterRule(_localctx, 112, RULE_for);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(716);
			match(FOR);
			setState(717);
			optTypeName();
			setState(718);
			match(IN);
			setState(719);
			exprWithBlock();
			setState(720);
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
		public TerminalNode THROW() { return getToken(KarinaParser.THROW, 0); }
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
		enterRule(_localctx, 114, RULE_throw);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(722);
			match(THROW);
			setState(723);
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
		enterRule(_localctx, 116, RULE_conditionalOrExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(725);
			conditionalAndExpression();
			setState(728);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,82,_ctx) ) {
			case 1:
				{
				setState(726);
				match(OR_OR);
				setState(727);
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
		enterRule(_localctx, 118, RULE_conditionalAndExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(730);
			equalityExpression();
			setState(733);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,83,_ctx) ) {
			case 1:
				{
				setState(731);
				match(AND_AND);
				setState(732);
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
		enterRule(_localctx, 120, RULE_equalityExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(735);
			relationalExpression();
			setState(738);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,84,_ctx) ) {
			case 1:
				{
				setState(736);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 540431955284459520L) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(737);
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
		enterRule(_localctx, 122, RULE_relationalExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(740);
			additiveExpression();
			setState(743);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,85,_ctx) ) {
			case 1:
				{
				setState(741);
				_la = _input.LA(1);
				if ( !(((((_la - 53)) & ~0x3f) == 0 && ((1L << (_la - 53)) & 196611L) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(742);
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
		enterRule(_localctx, 124, RULE_additiveExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(745);
			multiplicativeExpression();
			setState(748);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,86,_ctx) ) {
			case 1:
				{
				setState(746);
				_la = _input.LA(1);
				if ( !(((((_la - 61)) & ~0x3f) == 0 && ((1L << (_la - 61)) & 8388611L) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(747);
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
		enterRule(_localctx, 126, RULE_multiplicativeExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(750);
			unaryExpression();
			setState(753);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,87,_ctx) ) {
			case 1:
				{
				setState(751);
				_la = _input.LA(1);
				if ( !(((((_la - 63)) & ~0x3f) == 0 && ((1L << (_la - 63)) & 7L) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(752);
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
		enterRule(_localctx, 128, RULE_unaryExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(756);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CHAR_MINUS || _la==CHAR_EXCLAMATION) {
				{
				setState(755);
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

			setState(758);
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
		enterRule(_localctx, 130, RULE_factor);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(760);
			object();
			setState(764);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,89,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(761);
					postFix();
					}
					} 
				}
				setState(766);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,89,_ctx);
			}
			setState(770);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CHAR_EQUAL:
				{
				{
				setState(767);
				match(CHAR_EQUAL);
				setState(768);
				exprWithBlock();
				}
				}
				break;
			case IS:
				{
				setState(769);
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
			case STATIC:
			case THROW:
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
		public TerminalNode CHAR_QUESTION() { return getToken(KarinaParser.CHAR_QUESTION, 0); }
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
		enterRule(_localctx, 132, RULE_postFix);
		int _la;
		try {
			setState(791);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CHAR_DOT:
				enterOuterAlt(_localctx, 1);
				{
				setState(772);
				match(CHAR_DOT);
				setState(775);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case EXPR:
				case TYPE:
				case CHAR_UNDER:
				case CHAR_ESCAPE:
				case ID:
					{
					setState(773);
					id();
					}
					break;
				case CLASS:
					{
					setState(774);
					match(CLASS);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				break;
			case CHAR_SMALLER:
			case CHAR_L_PAREN:
				enterOuterAlt(_localctx, 2);
				{
				setState(778);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==CHAR_SMALLER) {
					{
					setState(777);
					genericHint();
					}
				}

				setState(780);
				match(CHAR_L_PAREN);
				setState(781);
				expressionList();
				setState(782);
				match(CHAR_R_PAREN);
				}
				break;
			case CHAR_L_BRACKET:
				enterOuterAlt(_localctx, 3);
				{
				setState(784);
				match(CHAR_L_BRACKET);
				setState(785);
				exprWithBlock();
				setState(786);
				match(CHAR_R_BRACKET);
				}
				break;
			case AS:
				enterOuterAlt(_localctx, 4);
				{
				setState(788);
				match(AS);
				setState(789);
				type();
				}
				break;
			case CHAR_QUESTION:
				enterOuterAlt(_localctx, 5);
				{
				setState(790);
				match(CHAR_QUESTION);
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
	public static class ObjectContext extends ParserRuleContext {
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
		public List<TerminalNode> CHAR_COLON_COLON() { return getTokens(KarinaParser.CHAR_COLON_COLON); }
		public TerminalNode CHAR_COLON_COLON(int i) {
			return getToken(KarinaParser.CHAR_COLON_COLON, i);
		}
		public TerminalNode CHAR_L_BRACE() { return getToken(KarinaParser.CHAR_L_BRACE, 0); }
		public InitListContext initList() {
			return getRuleContext(InitListContext.class,0);
		}
		public TerminalNode CHAR_R_BRACE() { return getToken(KarinaParser.CHAR_R_BRACE, 0); }
		public GenericHintContext genericHint() {
			return getRuleContext(GenericHintContext.class,0);
		}
		public TerminalNode STRING_LITERAL() { return getToken(KarinaParser.STRING_LITERAL, 0); }
		public TerminalNode CHAR_LITERAL() { return getToken(KarinaParser.CHAR_LITERAL, 0); }
		public TerminalNode SELF() { return getToken(KarinaParser.SELF, 0); }
		public SuperCallContext superCall() {
			return getRuleContext(SuperCallContext.class,0);
		}
		public TerminalNode TRUE() { return getToken(KarinaParser.TRUE, 0); }
		public TerminalNode FALSE() { return getToken(KarinaParser.FALSE, 0); }
		public ArrayContext array() {
			return getRuleContext(ArrayContext.class,0);
		}
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
		enterRule(_localctx, 134, RULE_object);
		int _la;
		try {
			setState(822);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CHAR_L_PAREN:
				enterOuterAlt(_localctx, 1);
				{
				setState(793);
				match(CHAR_L_PAREN);
				setState(794);
				exprWithBlock();
				setState(795);
				match(CHAR_R_PAREN);
				}
				break;
			case NUMBER:
				enterOuterAlt(_localctx, 2);
				{
				setState(797);
				match(NUMBER);
				}
				break;
			case EXPR:
			case TYPE:
			case CHAR_UNDER:
			case CHAR_ESCAPE:
			case ID:
				enterOuterAlt(_localctx, 3);
				{
				setState(798);
				id();
				setState(803);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==CHAR_COLON_COLON) {
					{
					{
					setState(799);
					match(CHAR_COLON_COLON);
					setState(800);
					id();
					}
					}
					setState(805);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(813);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,96,_ctx) ) {
				case 1:
					{
					setState(807);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==CHAR_SMALLER) {
						{
						setState(806);
						genericHint();
						}
					}

					setState(809);
					match(CHAR_L_BRACE);
					setState(810);
					initList();
					setState(811);
					match(CHAR_R_BRACE);
					}
					break;
				}
				}
				break;
			case STRING_LITERAL:
				enterOuterAlt(_localctx, 4);
				{
				setState(815);
				match(STRING_LITERAL);
				}
				break;
			case CHAR_LITERAL:
				enterOuterAlt(_localctx, 5);
				{
				setState(816);
				match(CHAR_LITERAL);
				}
				break;
			case SELF:
				enterOuterAlt(_localctx, 6);
				{
				setState(817);
				match(SELF);
				}
				break;
			case SUPER:
				enterOuterAlt(_localctx, 7);
				{
				setState(818);
				superCall();
				}
				break;
			case TRUE:
				enterOuterAlt(_localctx, 8);
				{
				setState(819);
				match(TRUE);
				}
				break;
			case FALSE:
				enterOuterAlt(_localctx, 9);
				{
				setState(820);
				match(FALSE);
				}
				break;
			case CHAR_SMALLER:
			case CHAR_L_BRACKET:
				enterOuterAlt(_localctx, 10);
				{
				setState(821);
				array();
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
		enterRule(_localctx, 136, RULE_array);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(828);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CHAR_SMALLER) {
				{
				setState(824);
				match(CHAR_SMALLER);
				setState(825);
				type();
				setState(826);
				match(CHAR_GREATER);
				}
			}

			setState(830);
			match(CHAR_L_BRACKET);
			setState(831);
			expressionList();
			setState(832);
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
	public static class SuperCallContext extends ParserRuleContext {
		public TerminalNode SUPER() { return getToken(KarinaParser.SUPER, 0); }
		public TerminalNode CHAR_SMALLER() { return getToken(KarinaParser.CHAR_SMALLER, 0); }
		public StructTypeContext structType() {
			return getRuleContext(StructTypeContext.class,0);
		}
		public TerminalNode CHAR_GREATER() { return getToken(KarinaParser.CHAR_GREATER, 0); }
		public TerminalNode CHAR_DOT() { return getToken(KarinaParser.CHAR_DOT, 0); }
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public SuperCallContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_superCall; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).enterSuperCall(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof KarinaParserListener ) ((KarinaParserListener)listener).exitSuperCall(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof KarinaParserVisitor ) return ((KarinaParserVisitor<? extends T>)visitor).visitSuperCall(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SuperCallContext superCall() throws RecognitionException {
		SuperCallContext _localctx = new SuperCallContext(_ctx, getState());
		enterRule(_localctx, 138, RULE_superCall);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(834);
			match(SUPER);
			setState(835);
			match(CHAR_SMALLER);
			setState(836);
			structType();
			setState(837);
			match(CHAR_GREATER);
			setState(840);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,99,_ctx) ) {
			case 1:
				{
				setState(838);
				match(CHAR_DOT);
				setState(839);
				id();
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
		enterRule(_localctx, 140, RULE_expressionList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(850);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 4612812086652538906L) != 0) || ((((_la - 70)) & ~0x3f) == 0 && ((1L << (_la - 70)) & 20718243L) != 0)) {
				{
				setState(842);
				exprWithBlock();
				setState(847);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==CHAR_COMMA) {
					{
					{
					setState(843);
					match(CHAR_COMMA);
					setState(844);
					exprWithBlock();
					}
					}
					setState(849);
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
		enterRule(_localctx, 142, RULE_initList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(860);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EXPR || _la==TYPE || ((((_la - 83)) & ~0x3f) == 0 && ((1L << (_la - 83)) & 2081L) != 0)) {
				{
				setState(852);
				memberInit();
				setState(857);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==CHAR_COMMA) {
					{
					{
					setState(853);
					match(CHAR_COMMA);
					setState(854);
					memberInit();
					}
					}
					setState(859);
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
		enterRule(_localctx, 144, RULE_memberInit);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(862);
			id();
			setState(863);
			match(CHAR_COLON);
			setState(864);
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
		enterRule(_localctx, 146, RULE_isInstanceOf);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(866);
			match(IS);
			setState(867);
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
		enterRule(_localctx, 148, RULE_optTypeList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(877);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EXPR || _la==TYPE || ((((_la - 83)) & ~0x3f) == 0 && ((1L << (_la - 83)) & 2081L) != 0)) {
				{
				setState(869);
				optTypeName();
				setState(874);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==CHAR_COMMA) {
					{
					{
					setState(870);
					match(CHAR_COMMA);
					setState(871);
					optTypeName();
					}
					}
					setState(876);
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
		enterRule(_localctx, 150, RULE_optTypeName);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(879);
			id();
			setState(882);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CHAR_COLON) {
				{
				setState(880);
				match(CHAR_COLON);
				setState(881);
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
		enterRule(_localctx, 152, RULE_id);
		try {
			setState(890);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ID:
				enterOuterAlt(_localctx, 1);
				{
				setState(884);
				match(ID);
				}
				break;
			case EXPR:
				enterOuterAlt(_localctx, 2);
				{
				setState(885);
				match(EXPR);
				}
				break;
			case TYPE:
				enterOuterAlt(_localctx, 3);
				{
				setState(886);
				match(TYPE);
				}
				break;
			case CHAR_ESCAPE:
				enterOuterAlt(_localctx, 4);
				{
				setState(887);
				match(CHAR_ESCAPE);
				setState(888);
				escaped();
				}
				break;
			case CHAR_UNDER:
				enterOuterAlt(_localctx, 5);
				{
				setState(889);
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
		public TerminalNode EXTEND() { return getToken(KarinaParser.EXTEND, 0); }
		public TerminalNode MATCH() { return getToken(KarinaParser.MATCH, 0); }
		public TerminalNode OVERRIDE() { return getToken(KarinaParser.OVERRIDE, 0); }
		public TerminalNode VIRTUAL() { return getToken(KarinaParser.VIRTUAL, 0); }
		public TerminalNode YIELD() { return getToken(KarinaParser.YIELD, 0); }
		public TerminalNode STRUCT() { return getToken(KarinaParser.STRUCT, 0); }
		public TerminalNode TRAIT() { return getToken(KarinaParser.TRAIT, 0); }
		public TerminalNode IMPL() { return getToken(KarinaParser.IMPL, 0); }
		public TerminalNode LET() { return getToken(KarinaParser.LET, 0); }
		public TerminalNode SELF() { return getToken(KarinaParser.SELF, 0); }
		public TerminalNode STRING() { return getToken(KarinaParser.STRING, 0); }
		public TerminalNode JSON() { return getToken(KarinaParser.JSON, 0); }
		public TerminalNode BOOL() { return getToken(KarinaParser.BOOL, 0); }
		public TerminalNode WHERE() { return getToken(KarinaParser.WHERE, 0); }
		public TerminalNode CONST() { return getToken(KarinaParser.CONST, 0); }
		public TerminalNode MUT() { return getToken(KarinaParser.MUT, 0); }
		public TerminalNode ANY() { return getToken(KarinaParser.ANY, 0); }
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
		enterRule(_localctx, 154, RULE_escaped);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(892);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 739594764171508L) != 0)) ) {
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
		"\u0004\u0001a\u037f\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
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
		"F\u0007F\u0002G\u0007G\u0002H\u0007H\u0002I\u0007I\u0002J\u0007J\u0002"+
		"K\u0007K\u0002L\u0007L\u0002M\u0007M\u0001\u0000\u0005\u0000\u009e\b\u0000"+
		"\n\u0000\f\u0000\u00a1\t\u0000\u0001\u0000\u0005\u0000\u00a4\b\u0000\n"+
		"\u0000\f\u0000\u00a7\t\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0003\u0001\u00b5\b\u0001\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0005\u0002\u00ba\b\u0002\n\u0002\f\u0002\u00bd\t\u0002"+
		"\u0001\u0003\u0005\u0003\u00c0\b\u0003\n\u0003\f\u0003\u00c3\t\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0003\u0003\u00ca"+
		"\b\u0003\u0001\u0004\u0001\u0004\u0003\u0004\u00ce\b\u0004\u0001\u0004"+
		"\u0003\u0004\u00d1\b\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0003\u0004\u00d8\b\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0003\u0004\u00dd\b\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0003\u0005\u00e3\b\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0006\u0001\u0006\u0001\u0006\u0003\u0006\u00ec\b\u0006\u0001\u0006"+
		"\u0001\u0006\u0005\u0006\u00f0\b\u0006\n\u0006\f\u0006\u00f3\t\u0006\u0001"+
		"\u0006\u0005\u0006\u00f6\b\u0006\n\u0006\f\u0006\u00f9\t\u0006\u0001\u0006"+
		"\u0005\u0006\u00fc\b\u0006\n\u0006\f\u0006\u00ff\t\u0006\u0001\u0006\u0005"+
		"\u0006\u0102\b\u0006\n\u0006\f\u0006\u0105\t\u0006\u0001\u0006\u0005\u0006"+
		"\u0108\b\u0006\n\u0006\f\u0006\u010b\t\u0006\u0001\u0006\u0003\u0006\u010e"+
		"\b\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0005\u0007\u0114"+
		"\b\u0007\n\u0007\f\u0007\u0117\t\u0007\u0001\u0007\u0003\u0007\u011a\b"+
		"\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0005\b\u0120\b\b\n\b\f\b\u0123"+
		"\t\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0005\t\u012a\b\t\n\t\f\t"+
		"\u012d\t\t\u0003\t\u012f\b\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b"+
		"\u0003\u000b\u0136\b\u000b\u0001\u000b\u0003\u000b\u0139\b\u000b\u0001"+
		"\f\u0001\f\u0001\f\u0001\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001"+
		"\u000e\u0005\u000e\u0144\b\u000e\n\u000e\f\u000e\u0147\t\u000e\u0003\u000e"+
		"\u0149\b\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0003\u000f\u014e\b"+
		"\u000f\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0003"+
		"\u0010\u0155\b\u0010\u0001\u0010\u0001\u0010\u0005\u0010\u0159\b\u0010"+
		"\n\u0010\f\u0010\u015c\t\u0010\u0001\u0010\u0005\u0010\u015f\b\u0010\n"+
		"\u0010\f\u0010\u0162\t\u0010\u0001\u0010\u0005\u0010\u0165\b\u0010\n\u0010"+
		"\f\u0010\u0168\t\u0010\u0001\u0010\u0005\u0010\u016b\b\u0010\n\u0010\f"+
		"\u0010\u016e\t\u0010\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0001"+
		"\u0011\u0001\u0011\u0001\u0011\u0003\u0011\u0177\b\u0011\u0001\u0012\u0001"+
		"\u0012\u0001\u0012\u0003\u0012\u017c\b\u0012\u0001\u0012\u0001\u0012\u0005"+
		"\u0012\u0180\b\u0012\n\u0012\f\u0012\u0183\t\u0012\u0001\u0012\u0005\u0012"+
		"\u0186\b\u0012\n\u0012\f\u0012\u0189\t\u0012\u0001\u0012\u0003\u0012\u018c"+
		"\b\u0012\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0014\u0001\u0014\u0003"+
		"\u0014\u0193\b\u0014\u0001\u0014\u0001\u0014\u0005\u0014\u0197\b\u0014"+
		"\n\u0014\f\u0014\u019a\t\u0014\u0003\u0014\u019c\b\u0014\u0001\u0015\u0001"+
		"\u0015\u0001\u0015\u0005\u0015\u01a1\b\u0015\n\u0015\f\u0015\u01a4\t\u0015"+
		"\u0003\u0015\u01a6\b\u0015\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016"+
		"\u0001\u0017\u0001\u0017\u0003\u0017\u01ae\b\u0017\u0001\u0018\u0001\u0018"+
		"\u0003\u0018\u01b2\b\u0018\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019"+
		"\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019"+
		"\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019"+
		"\u0001\u0019\u0001\u0019\u0003\u0019\u01c6\b\u0019\u0001\u001a\u0001\u001a"+
		"\u0003\u001a\u01ca\b\u001a\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b"+
		"\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c"+
		"\u0003\u001c\u01d6\b\u001c\u0001\u001c\u0003\u001c\u01d9\b\u001c\u0001"+
		"\u001d\u0001\u001d\u0001\u001d\u0005\u001d\u01de\b\u001d\n\u001d\f\u001d"+
		"\u01e1\t\u001d\u0003\u001d\u01e3\b\u001d\u0001\u001e\u0001\u001e\u0001"+
		"\u001e\u0001\u001e\u0005\u001e\u01e9\b\u001e\n\u001e\f\u001e\u01ec\t\u001e"+
		"\u0003\u001e\u01ee\b\u001e\u0001\u001e\u0001\u001e\u0001\u001f\u0001\u001f"+
		"\u0001\u001f\u0001\u001f\u0005\u001f\u01f6\b\u001f\n\u001f\f\u001f\u01f9"+
		"\t\u001f\u0003\u001f\u01fb\b\u001f\u0001\u001f\u0001\u001f\u0001 \u0001"+
		" \u0001 \u0005 \u0202\b \n \f \u0205\t \u0001!\u0001!\u0001!\u0001!\u0003"+
		"!\u020b\b!\u0001\"\u0001\"\u0001\"\u0003\"\u0210\b\"\u0001\"\u0005\"\u0213"+
		"\b\"\n\"\f\"\u0216\t\"\u0003\"\u0218\b\"\u0001\"\u0001\"\u0001#\u0001"+
		"#\u0003#\u021e\b#\u0001#\u0001#\u0001#\u0001$\u0001$\u0001$\u0003$\u0226"+
		"\b$\u0001$\u0005$\u0229\b$\n$\f$\u022c\t$\u0003$\u022e\b$\u0001$\u0001"+
		"$\u0001%\u0001%\u0001%\u0001&\u0001&\u0001&\u0001&\u0001&\u0001\'\u0001"+
		"\'\u0001\'\u0001\'\u0001\'\u0001(\u0001(\u0001(\u0001(\u0001(\u0001(\u0001"+
		"(\u0001(\u0001(\u0001(\u0003(\u0249\b(\u0001)\u0001)\u0005)\u024d\b)\n"+
		")\f)\u0250\t)\u0001)\u0001)\u0001*\u0001*\u0003*\u0256\b*\u0001+\u0001"+
		"+\u0001+\u0001+\u0003+\u025c\b+\u0001+\u0001+\u0001+\u0001+\u0001+\u0001"+
		"+\u0001+\u0001+\u0003+\u0266\b+\u0001,\u0001,\u0001,\u0001,\u0003,\u026c"+
		"\b,\u0001,\u0001,\u0001,\u0001-\u0001-\u0001-\u0001-\u0001-\u0001-\u0003"+
		"-\u0277\b-\u0001-\u0003-\u027a\b-\u0001-\u0001-\u0001.\u0001.\u0001.\u0001"+
		".\u0001.\u0001.\u0003.\u0284\b.\u0001/\u0001/\u0001/\u0005/\u0289\b/\n"+
		"/\f/\u028c\t/\u00010\u00010\u00010\u00010\u00050\u0292\b0\n0\f0\u0295"+
		"\t0\u00010\u00010\u00011\u00011\u00031\u029b\b1\u00011\u00011\u00011\u0001"+
		"2\u00012\u00012\u00012\u00012\u00012\u00032\u02a6\b2\u00013\u00013\u0001"+
		"4\u00014\u00014\u00014\u00014\u00014\u00014\u00034\u02b1\b4\u00014\u0001"+
		"4\u00034\u02b5\b4\u00015\u00015\u00035\u02b9\b5\u00015\u00015\u00015\u0003"+
		"5\u02be\b5\u00016\u00016\u00016\u00016\u00016\u00016\u00016\u00036\u02c7"+
		"\b6\u00017\u00017\u00017\u00017\u00018\u00018\u00018\u00018\u00018\u0001"+
		"8\u00019\u00019\u00019\u0001:\u0001:\u0001:\u0003:\u02d9\b:\u0001;\u0001"+
		";\u0001;\u0003;\u02de\b;\u0001<\u0001<\u0001<\u0003<\u02e3\b<\u0001=\u0001"+
		"=\u0001=\u0003=\u02e8\b=\u0001>\u0001>\u0001>\u0003>\u02ed\b>\u0001?\u0001"+
		"?\u0001?\u0003?\u02f2\b?\u0001@\u0003@\u02f5\b@\u0001@\u0001@\u0001A\u0001"+
		"A\u0005A\u02fb\bA\nA\fA\u02fe\tA\u0001A\u0001A\u0001A\u0003A\u0303\bA"+
		"\u0001B\u0001B\u0001B\u0003B\u0308\bB\u0001B\u0003B\u030b\bB\u0001B\u0001"+
		"B\u0001B\u0001B\u0001B\u0001B\u0001B\u0001B\u0001B\u0001B\u0001B\u0003"+
		"B\u0318\bB\u0001C\u0001C\u0001C\u0001C\u0001C\u0001C\u0001C\u0001C\u0005"+
		"C\u0322\bC\nC\fC\u0325\tC\u0001C\u0003C\u0328\bC\u0001C\u0001C\u0001C"+
		"\u0001C\u0003C\u032e\bC\u0001C\u0001C\u0001C\u0001C\u0001C\u0001C\u0001"+
		"C\u0003C\u0337\bC\u0001D\u0001D\u0001D\u0001D\u0003D\u033d\bD\u0001D\u0001"+
		"D\u0001D\u0001D\u0001E\u0001E\u0001E\u0001E\u0001E\u0001E\u0003E\u0349"+
		"\bE\u0001F\u0001F\u0001F\u0005F\u034e\bF\nF\fF\u0351\tF\u0003F\u0353\b"+
		"F\u0001G\u0001G\u0001G\u0005G\u0358\bG\nG\fG\u035b\tG\u0003G\u035d\bG"+
		"\u0001H\u0001H\u0001H\u0001H\u0001I\u0001I\u0001I\u0001J\u0001J\u0001"+
		"J\u0005J\u0369\bJ\nJ\fJ\u036c\tJ\u0003J\u036e\bJ\u0001K\u0001K\u0001K"+
		"\u0003K\u0373\bK\u0001L\u0001L\u0001L\u0001L\u0001L\u0001L\u0003L\u037b"+
		"\bL\u0001M\u0001M\u0001M\u0000\u0000N\u0000\u0002\u0004\u0006\b\n\f\u000e"+
		"\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.02468:<>@BDF"+
		"HJLNPRTVXZ\\^`bdfhjlnprtvxz|~\u0080\u0082\u0084\u0086\u0088\u008a\u008c"+
		"\u008e\u0090\u0092\u0094\u0096\u0098\u009a\u0000\u0006\u0001\u00007:\u0002"+
		"\u000056EF\u0002\u0000=>TT\u0001\u0000?A\u0002\u0000>>GG\u000e\u0000\u0002"+
		"\u0002\u0004\u0007\u000b\r\u0011\u0011\u0014\u0015\u0018\u0019\u001c\u001c"+
		"\u001e\u001e##%%\'\'--//11\u03d3\u0000\u009f\u0001\u0000\u0000\u0000\u0002"+
		"\u00aa\u0001\u0000\u0000\u0000\u0004\u00b6\u0001\u0000\u0000\u0000\u0006"+
		"\u00c1\u0001\u0000\u0000\u0000\b\u00cb\u0001\u0000\u0000\u0000\n\u00de"+
		"\u0001\u0000\u0000\u0000\f\u00e8\u0001\u0000\u0000\u0000\u000e\u010f\u0001"+
		"\u0000\u0000\u0000\u0010\u011b\u0001\u0000\u0000\u0000\u0012\u012e\u0001"+
		"\u0000\u0000\u0000\u0014\u0130\u0001\u0000\u0000\u0000\u0016\u0135\u0001"+
		"\u0000\u0000\u0000\u0018\u013a\u0001\u0000\u0000\u0000\u001a\u013d\u0001"+
		"\u0000\u0000\u0000\u001c\u0148\u0001\u0000\u0000\u0000\u001e\u014a\u0001"+
		"\u0000\u0000\u0000 \u0151\u0001\u0000\u0000\u0000\"\u0171\u0001\u0000"+
		"\u0000\u0000$\u0178\u0001\u0000\u0000\u0000&\u018d\u0001\u0000\u0000\u0000"+
		"(\u019b\u0001\u0000\u0000\u0000*\u01a5\u0001\u0000\u0000\u0000,\u01a7"+
		"\u0001\u0000\u0000\u0000.\u01ab\u0001\u0000\u0000\u00000\u01af\u0001\u0000"+
		"\u0000\u00002\u01c5\u0001\u0000\u0000\u00004\u01c7\u0001\u0000\u0000\u0000"+
		"6\u01cb\u0001\u0000\u0000\u00008\u01cf\u0001\u0000\u0000\u0000:\u01e2"+
		"\u0001\u0000\u0000\u0000<\u01e4\u0001\u0000\u0000\u0000>\u01f1\u0001\u0000"+
		"\u0000\u0000@\u01fe\u0001\u0000\u0000\u0000B\u0206\u0001\u0000\u0000\u0000"+
		"D\u020c\u0001\u0000\u0000\u0000F\u021d\u0001\u0000\u0000\u0000H\u0222"+
		"\u0001\u0000\u0000\u0000J\u0231\u0001\u0000\u0000\u0000L\u0234\u0001\u0000"+
		"\u0000\u0000N\u0239\u0001\u0000\u0000\u0000P\u0248\u0001\u0000\u0000\u0000"+
		"R\u024a\u0001\u0000\u0000\u0000T\u0255\u0001\u0000\u0000\u0000V\u0265"+
		"\u0001\u0000\u0000\u0000X\u0267\u0001\u0000\u0000\u0000Z\u0270\u0001\u0000"+
		"\u0000\u0000\\\u027d\u0001\u0000\u0000\u0000^\u0285\u0001\u0000\u0000"+
		"\u0000`\u028d\u0001\u0000\u0000\u0000b\u029a\u0001\u0000\u0000\u0000d"+
		"\u029f\u0001\u0000\u0000\u0000f\u02a7\u0001\u0000\u0000\u0000h\u02a9\u0001"+
		"\u0000\u0000\u0000j\u02b6\u0001\u0000\u0000\u0000l\u02bf\u0001\u0000\u0000"+
		"\u0000n\u02c8\u0001\u0000\u0000\u0000p\u02cc\u0001\u0000\u0000\u0000r"+
		"\u02d2\u0001\u0000\u0000\u0000t\u02d5\u0001\u0000\u0000\u0000v\u02da\u0001"+
		"\u0000\u0000\u0000x\u02df\u0001\u0000\u0000\u0000z\u02e4\u0001\u0000\u0000"+
		"\u0000|\u02e9\u0001\u0000\u0000\u0000~\u02ee\u0001\u0000\u0000\u0000\u0080"+
		"\u02f4\u0001\u0000\u0000\u0000\u0082\u02f8\u0001\u0000\u0000\u0000\u0084"+
		"\u0317\u0001\u0000\u0000\u0000\u0086\u0336\u0001\u0000\u0000\u0000\u0088"+
		"\u033c\u0001\u0000\u0000\u0000\u008a\u0342\u0001\u0000\u0000\u0000\u008c"+
		"\u0352\u0001\u0000\u0000\u0000\u008e\u035c\u0001\u0000\u0000\u0000\u0090"+
		"\u035e\u0001\u0000\u0000\u0000\u0092\u0362\u0001\u0000\u0000\u0000\u0094"+
		"\u036d\u0001\u0000\u0000\u0000\u0096\u036f\u0001\u0000\u0000\u0000\u0098"+
		"\u037a\u0001\u0000\u0000\u0000\u009a\u037c\u0001\u0000\u0000\u0000\u009c"+
		"\u009e\u0003\u0002\u0001\u0000\u009d\u009c\u0001\u0000\u0000\u0000\u009e"+
		"\u00a1\u0001\u0000\u0000\u0000\u009f\u009d\u0001\u0000\u0000\u0000\u009f"+
		"\u00a0\u0001\u0000\u0000\u0000\u00a0\u00a5\u0001\u0000\u0000\u0000\u00a1"+
		"\u009f\u0001\u0000\u0000\u0000\u00a2\u00a4\u0003\u0006\u0003\u0000\u00a3"+
		"\u00a2\u0001\u0000\u0000\u0000\u00a4\u00a7\u0001\u0000\u0000\u0000\u00a5"+
		"\u00a3\u0001\u0000\u0000\u0000\u00a5\u00a6\u0001\u0000\u0000\u0000\u00a6"+
		"\u00a8\u0001\u0000\u0000\u0000\u00a7\u00a5\u0001\u0000\u0000\u0000\u00a8"+
		"\u00a9\u0005\u0000\u0000\u0001\u00a9\u0001\u0001\u0000\u0000\u0000\u00aa"+
		"\u00ab\u0005\t\u0000\u0000\u00ab\u00b4\u0003@ \u0000\u00ac\u00b5\u0005"+
		"?\u0000\u0000\u00ad\u00b5\u0003\u0098L\u0000\u00ae\u00af\u0005M\u0000"+
		"\u0000\u00af\u00b0\u0003\u0004\u0002\u0000\u00b0\u00b1\u0005N\u0000\u0000"+
		"\u00b1\u00b5\u0001\u0000\u0000\u0000\u00b2\u00b3\u0005\u0007\u0000\u0000"+
		"\u00b3\u00b5\u0003\u0098L\u0000\u00b4\u00ac\u0001\u0000\u0000\u0000\u00b4"+
		"\u00ad\u0001\u0000\u0000\u0000\u00b4\u00ae\u0001\u0000\u0000\u0000\u00b4"+
		"\u00b2\u0001\u0000\u0000\u0000\u00b4\u00b5\u0001\u0000\u0000\u0000\u00b5"+
		"\u0003\u0001\u0000\u0000\u0000\u00b6\u00bb\u0003\u0098L\u0000\u00b7\u00b8"+
		"\u0005R\u0000\u0000\u00b8\u00ba\u0003\u0098L\u0000\u00b9\u00b7\u0001\u0000"+
		"\u0000\u0000\u00ba\u00bd\u0001\u0000\u0000\u0000\u00bb\u00b9\u0001\u0000"+
		"\u0000\u0000\u00bb\u00bc\u0001\u0000\u0000\u0000\u00bc\u0005\u0001\u0000"+
		"\u0000\u0000\u00bd\u00bb\u0001\u0000\u0000\u0000\u00be\u00c0\u0003B!\u0000"+
		"\u00bf\u00be\u0001\u0000\u0000\u0000\u00c0\u00c3\u0001\u0000\u0000\u0000"+
		"\u00c1\u00bf\u0001\u0000\u0000\u0000\u00c1\u00c2\u0001\u0000\u0000\u0000"+
		"\u00c2\u00c9\u0001\u0000\u0000\u0000\u00c3\u00c1\u0001\u0000\u0000\u0000"+
		"\u00c4\u00ca\u0003\b\u0004\u0000\u00c5\u00ca\u0003\f\u0006\u0000\u00c6"+
		"\u00ca\u0003 \u0010\u0000\u00c7\u00ca\u0003$\u0012\u0000\u00c8\u00ca\u0003"+
		"\n\u0005\u0000\u00c9\u00c4\u0001\u0000\u0000\u0000\u00c9\u00c5\u0001\u0000"+
		"\u0000\u0000\u00c9\u00c6\u0001\u0000\u0000\u0000\u00c9\u00c7\u0001\u0000"+
		"\u0000\u0000\u00c9\u00c8\u0001\u0000\u0000\u0000\u00ca\u0007\u0001\u0000"+
		"\u0000\u0000\u00cb\u00cd\u0005\u0004\u0000\u0000\u00cc\u00ce\u0003\u0098"+
		"L\u0000\u00cd\u00cc\u0001\u0000\u0000\u0000\u00cd\u00ce\u0001\u0000\u0000"+
		"\u0000\u00ce\u00d0\u0001\u0000\u0000\u0000\u00cf\u00d1\u0003>\u001f\u0000"+
		"\u00d0\u00cf\u0001\u0000\u0000\u0000\u00d0\u00d1\u0001\u0000\u0000\u0000"+
		"\u00d1\u00d2\u0001\u0000\u0000\u0000\u00d2\u00d3\u0005K\u0000\u0000\u00d3"+
		"\u00d4\u0003(\u0014\u0000\u00d4\u00d7\u0005L\u0000\u0000\u00d5\u00d6\u0005"+
		"3\u0000\u0000\u00d6\u00d8\u0003.\u0017\u0000\u00d7\u00d5\u0001\u0000\u0000"+
		"\u0000\u00d7\u00d8\u0001\u0000\u0000\u0000\u00d8\u00dc\u0001\u0000\u0000"+
		"\u0000\u00d9\u00da\u0005J\u0000\u0000\u00da\u00dd\u0003V+\u0000\u00db"+
		"\u00dd\u0003R)\u0000\u00dc\u00d9\u0001\u0000\u0000\u0000\u00dc\u00db\u0001"+
		"\u0000\u0000\u0000\u00dc\u00dd\u0001\u0000\u0000\u0000\u00dd\t\u0001\u0000"+
		"\u0000\u0000\u00de\u00df\u0005\u0016\u0000\u0000\u00df\u00e0\u0003\u0098"+
		"L\u0000\u00e0\u00e2\u0005H\u0000\u0000\u00e1\u00e3\u0005\'\u0000\u0000"+
		"\u00e2\u00e1\u0001\u0000\u0000\u0000\u00e2\u00e3\u0001\u0000\u0000\u0000"+
		"\u00e3\u00e4\u0001\u0000\u0000\u0000\u00e4\u00e5\u0003.\u0017\u0000\u00e5"+
		"\u00e6\u0005J\u0000\u0000\u00e6\u00e7\u0003V+\u0000\u00e7\u000b\u0001"+
		"\u0000\u0000\u0000\u00e8\u00e9\u0005\u0015\u0000\u0000\u00e9\u00eb\u0003"+
		"\u0098L\u0000\u00ea\u00ec\u0003>\u001f\u0000\u00eb\u00ea\u0001\u0000\u0000"+
		"\u0000\u00eb\u00ec\u0001\u0000\u0000\u0000\u00ec\u010d\u0001\u0000\u0000"+
		"\u0000\u00ed\u00f1\u0005M\u0000\u0000\u00ee\u00f0\u0003\n\u0005\u0000"+
		"\u00ef\u00ee\u0001\u0000\u0000\u0000\u00f0\u00f3\u0001\u0000\u0000\u0000"+
		"\u00f1\u00ef\u0001\u0000\u0000\u0000\u00f1\u00f2\u0001\u0000\u0000\u0000"+
		"\u00f2\u00f7\u0001\u0000\u0000\u0000\u00f3\u00f1\u0001\u0000\u0000\u0000"+
		"\u00f4\u00f6\u0003\u001e\u000f\u0000\u00f5\u00f4\u0001\u0000\u0000\u0000"+
		"\u00f6\u00f9\u0001\u0000\u0000\u0000\u00f7\u00f5\u0001\u0000\u0000\u0000"+
		"\u00f7\u00f8\u0001\u0000\u0000\u0000\u00f8\u00fd\u0001\u0000\u0000\u0000"+
		"\u00f9\u00f7\u0001\u0000\u0000\u0000\u00fa\u00fc\u0003\b\u0004\u0000\u00fb"+
		"\u00fa\u0001\u0000\u0000\u0000\u00fc\u00ff\u0001\u0000\u0000\u0000\u00fd"+
		"\u00fb\u0001\u0000\u0000\u0000\u00fd\u00fe\u0001\u0000\u0000\u0000\u00fe"+
		"\u0103\u0001\u0000\u0000\u0000\u00ff\u00fd\u0001\u0000\u0000\u0000\u0100"+
		"\u0102\u0003\u000e\u0007\u0000\u0101\u0100\u0001\u0000\u0000\u0000\u0102"+
		"\u0105\u0001\u0000\u0000\u0000\u0103\u0101\u0001\u0000\u0000\u0000\u0103"+
		"\u0104\u0001\u0000\u0000\u0000\u0104\u0109\u0001\u0000\u0000\u0000\u0105"+
		"\u0103\u0001\u0000\u0000\u0000\u0106\u0108\u0003\u0010\b\u0000\u0107\u0106"+
		"\u0001\u0000\u0000\u0000\u0108\u010b\u0001\u0000\u0000\u0000\u0109\u0107"+
		"\u0001\u0000\u0000\u0000\u0109\u010a\u0001\u0000\u0000\u0000\u010a\u010c"+
		"\u0001\u0000\u0000\u0000\u010b\u0109\u0001\u0000\u0000\u0000\u010c\u010e"+
		"\u0005N\u0000\u0000\u010d\u00ed\u0001\u0000\u0000\u0000\u010d\u010e\u0001"+
		"\u0000\u0000\u0000\u010e\r\u0001\u0000\u0000\u0000\u010f\u0110\u0005\u0019"+
		"\u0000\u0000\u0110\u0119\u00034\u001a\u0000\u0111\u0115\u0005M\u0000\u0000"+
		"\u0112\u0114\u0003\b\u0004\u0000\u0113\u0112\u0001\u0000\u0000\u0000\u0114"+
		"\u0117\u0001\u0000\u0000\u0000\u0115\u0113\u0001\u0000\u0000\u0000\u0115"+
		"\u0116\u0001\u0000\u0000\u0000\u0116\u0118\u0001\u0000\u0000\u0000\u0117"+
		"\u0115\u0001\u0000\u0000\u0000\u0118\u011a\u0005N\u0000\u0000\u0119\u0111"+
		"\u0001\u0000\u0000\u0000\u0119\u011a\u0001\u0000\u0000\u0000\u011a\u000f"+
		"\u0001\u0000\u0000\u0000\u011b\u011c\u0005#\u0000\u0000\u011c\u011d\u0003"+
		"\u0012\t\u0000\u011d\u0121\u0005M\u0000\u0000\u011e\u0120\u0003\b\u0004"+
		"\u0000\u011f\u011e\u0001\u0000\u0000\u0000\u0120\u0123\u0001\u0000\u0000"+
		"\u0000\u0121\u011f\u0001\u0000\u0000\u0000\u0121\u0122\u0001\u0000\u0000"+
		"\u0000\u0122\u0124\u0001\u0000\u0000\u0000\u0123\u0121\u0001\u0000\u0000"+
		"\u0000\u0124\u0125\u0005N\u0000\u0000\u0125\u0011\u0001\u0000\u0000\u0000"+
		"\u0126\u012b\u0003\u0014\n\u0000\u0127\u0128\u0005R\u0000\u0000\u0128"+
		"\u012a\u0003\u0014\n\u0000\u0129\u0127\u0001\u0000\u0000\u0000\u012a\u012d"+
		"\u0001\u0000\u0000\u0000\u012b\u0129\u0001\u0000\u0000\u0000\u012b\u012c"+
		"\u0001\u0000\u0000\u0000\u012c\u012f\u0001\u0000\u0000\u0000\u012d\u012b"+
		"\u0001\u0000\u0000\u0000\u012e\u0126\u0001\u0000\u0000\u0000\u012e\u012f"+
		"\u0001\u0000\u0000\u0000\u012f\u0013\u0001\u0000\u0000\u0000\u0130\u0131"+
		"\u0003\u0098L\u0000\u0131\u0132\u0005H\u0000\u0000\u0132\u0133\u0003\u0016"+
		"\u000b\u0000\u0133\u0015\u0001\u0000\u0000\u0000\u0134\u0136\u0003\u001a"+
		"\r\u0000\u0135\u0134\u0001\u0000\u0000\u0000\u0135\u0136\u0001\u0000\u0000"+
		"\u0000\u0136\u0138\u0001\u0000\u0000\u0000\u0137\u0139\u0003\u0018\f\u0000"+
		"\u0138\u0137\u0001\u0000\u0000\u0000\u0138\u0139\u0001\u0000\u0000\u0000"+
		"\u0139\u0017\u0001\u0000\u0000\u0000\u013a\u013b\u0005\u0019\u0000\u0000"+
		"\u013b\u013c\u0003\u001c\u000e\u0000\u013c\u0019\u0001\u0000\u0000\u0000"+
		"\u013d\u013e\u0005\n\u0000\u0000\u013e\u013f\u00034\u001a\u0000\u013f"+
		"\u001b\u0001\u0000\u0000\u0000\u0140\u0145\u00034\u001a\u0000\u0141\u0142"+
		"\u0005=\u0000\u0000\u0142\u0144\u00034\u001a\u0000\u0143\u0141\u0001\u0000"+
		"\u0000\u0000\u0144\u0147\u0001\u0000\u0000\u0000\u0145\u0143\u0001\u0000"+
		"\u0000\u0000\u0145\u0146\u0001\u0000\u0000\u0000\u0146\u0149\u0001\u0000"+
		"\u0000\u0000\u0147\u0145\u0001\u0000\u0000\u0000\u0148\u0140\u0001\u0000"+
		"\u0000\u0000\u0148\u0149\u0001\u0000\u0000\u0000\u0149\u001d\u0001\u0000"+
		"\u0000\u0000\u014a\u014b\u0003\u0098L\u0000\u014b\u014d\u0005H\u0000\u0000"+
		"\u014c\u014e\u0005\'\u0000\u0000\u014d\u014c\u0001\u0000\u0000\u0000\u014d"+
		"\u014e\u0001\u0000\u0000\u0000\u014e\u014f\u0001\u0000\u0000\u0000\u014f"+
		"\u0150\u0003.\u0017\u0000\u0150\u001f\u0001\u0000\u0000\u0000\u0151\u0152"+
		"\u0005\u001a\u0000\u0000\u0152\u0154\u0003\u0098L\u0000\u0153\u0155\u0003"+
		">\u001f\u0000\u0154\u0153\u0001\u0000\u0000\u0000\u0154\u0155\u0001\u0000"+
		"\u0000\u0000\u0155\u0156\u0001\u0000\u0000\u0000\u0156\u015a\u0005M\u0000"+
		"\u0000\u0157\u0159\u0003\"\u0011\u0000\u0158\u0157\u0001\u0000\u0000\u0000"+
		"\u0159\u015c\u0001\u0000\u0000\u0000\u015a\u0158\u0001\u0000\u0000\u0000"+
		"\u015a\u015b\u0001\u0000\u0000\u0000\u015b\u0160\u0001\u0000\u0000\u0000"+
		"\u015c\u015a\u0001\u0000\u0000\u0000\u015d\u015f\u0003\b\u0004\u0000\u015e"+
		"\u015d\u0001\u0000\u0000\u0000\u015f\u0162\u0001\u0000\u0000\u0000\u0160"+
		"\u015e\u0001\u0000\u0000\u0000\u0160\u0161\u0001\u0000\u0000\u0000\u0161"+
		"\u0166\u0001\u0000\u0000\u0000\u0162\u0160\u0001\u0000\u0000\u0000\u0163"+
		"\u0165\u0003\u000e\u0007\u0000\u0164\u0163\u0001\u0000\u0000\u0000\u0165"+
		"\u0168\u0001\u0000\u0000\u0000\u0166\u0164\u0001\u0000\u0000\u0000\u0166"+
		"\u0167\u0001\u0000\u0000\u0000\u0167\u016c\u0001\u0000\u0000\u0000\u0168"+
		"\u0166\u0001\u0000\u0000\u0000\u0169\u016b\u0003\u0010\b\u0000\u016a\u0169"+
		"\u0001\u0000\u0000\u0000\u016b\u016e\u0001\u0000\u0000\u0000\u016c\u016a"+
		"\u0001\u0000\u0000\u0000\u016c\u016d\u0001\u0000\u0000\u0000\u016d\u016f"+
		"\u0001\u0000\u0000\u0000\u016e\u016c\u0001\u0000\u0000\u0000\u016f\u0170"+
		"\u0005N\u0000\u0000\u0170!\u0001\u0000\u0000\u0000\u0171\u0176\u0003\u0098"+
		"L\u0000\u0172\u0173\u0005K\u0000\u0000\u0173\u0174\u0003*\u0015\u0000"+
		"\u0174\u0175\u0005L\u0000\u0000\u0175\u0177\u0001\u0000\u0000\u0000\u0176"+
		"\u0172\u0001\u0000\u0000\u0000\u0176\u0177\u0001\u0000\u0000\u0000\u0177"+
		"#\u0001\u0000\u0000\u0000\u0178\u0179\u0005$\u0000\u0000\u0179\u017b\u0003"+
		"\u0098L\u0000\u017a\u017c\u0003>\u001f\u0000\u017b\u017a\u0001\u0000\u0000"+
		"\u0000\u017b\u017c\u0001\u0000\u0000\u0000\u017c\u018b\u0001\u0000\u0000"+
		"\u0000\u017d\u0181\u0005M\u0000\u0000\u017e\u0180\u0003\b\u0004\u0000"+
		"\u017f\u017e\u0001\u0000\u0000\u0000\u0180\u0183\u0001\u0000\u0000\u0000"+
		"\u0181\u017f\u0001\u0000\u0000\u0000\u0181\u0182\u0001\u0000\u0000\u0000"+
		"\u0182\u0187\u0001\u0000\u0000\u0000\u0183\u0181\u0001\u0000\u0000\u0000"+
		"\u0184\u0186\u0003&\u0013\u0000\u0185\u0184\u0001\u0000\u0000\u0000\u0186"+
		"\u0189\u0001\u0000\u0000\u0000\u0187\u0185\u0001\u0000\u0000\u0000\u0187"+
		"\u0188\u0001\u0000\u0000\u0000\u0188\u018a\u0001\u0000\u0000\u0000\u0189"+
		"\u0187\u0001\u0000\u0000\u0000\u018a\u018c\u0005N\u0000\u0000\u018b\u017d"+
		"\u0001\u0000\u0000\u0000\u018b\u018c\u0001\u0000\u0000\u0000\u018c%\u0001"+
		"\u0000\u0000\u0000\u018d\u018e\u0005\u0019\u0000\u0000\u018e\u018f\u0003"+
		"4\u001a\u0000\u018f\'\u0001\u0000\u0000\u0000\u0190\u0193\u0003,\u0016"+
		"\u0000\u0191\u0193\u0005%\u0000\u0000\u0192\u0190\u0001\u0000\u0000\u0000"+
		"\u0192\u0191\u0001\u0000\u0000\u0000\u0193\u0198\u0001\u0000\u0000\u0000"+
		"\u0194\u0195\u0005R\u0000\u0000\u0195\u0197\u0003,\u0016\u0000\u0196\u0194"+
		"\u0001\u0000\u0000\u0000\u0197\u019a\u0001\u0000\u0000\u0000\u0198\u0196"+
		"\u0001\u0000\u0000\u0000\u0198\u0199\u0001\u0000\u0000\u0000\u0199\u019c"+
		"\u0001\u0000\u0000\u0000\u019a\u0198\u0001\u0000\u0000\u0000\u019b\u0192"+
		"\u0001\u0000\u0000\u0000\u019b\u019c\u0001\u0000\u0000\u0000\u019c)\u0001"+
		"\u0000\u0000\u0000\u019d\u01a2\u0003,\u0016\u0000\u019e\u019f\u0005R\u0000"+
		"\u0000\u019f\u01a1\u0003,\u0016\u0000\u01a0\u019e\u0001\u0000\u0000\u0000"+
		"\u01a1\u01a4\u0001\u0000\u0000\u0000\u01a2\u01a0\u0001\u0000\u0000\u0000"+
		"\u01a2\u01a3\u0001\u0000\u0000\u0000\u01a3\u01a6\u0001\u0000\u0000\u0000"+
		"\u01a4\u01a2\u0001\u0000\u0000\u0000\u01a5\u019d\u0001\u0000\u0000\u0000"+
		"\u01a5\u01a6\u0001\u0000\u0000\u0000\u01a6+\u0001\u0000\u0000\u0000\u01a7"+
		"\u01a8\u0003\u0098L\u0000\u01a8\u01a9\u0005H\u0000\u0000\u01a9\u01aa\u0003"+
		".\u0017\u0000\u01aa-\u0001\u0000\u0000\u0000\u01ab\u01ad\u00032\u0019"+
		"\u0000\u01ac\u01ae\u00030\u0018\u0000\u01ad\u01ac\u0001\u0000\u0000\u0000"+
		"\u01ad\u01ae\u0001\u0000\u0000\u0000\u01ae/\u0001\u0000\u0000\u0000\u01af"+
		"\u01b1\u0005V\u0000\u0000\u01b0\u01b2\u00032\u0019\u0000\u01b1\u01b0\u0001"+
		"\u0000\u0000\u0000\u01b1\u01b2\u0001\u0000\u0000\u0000\u01b21\u0001\u0000"+
		"\u0000\u0000\u01b3\u01c6\u00050\u0000\u0000\u01b4\u01c6\u0005&\u0000\u0000"+
		"\u01b5\u01c6\u0005+\u0000\u0000\u01b6\u01c6\u0005,\u0000\u0000\u01b7\u01c6"+
		"\u0005)\u0000\u0000\u01b8\u01c6\u0005*\u0000\u0000\u01b9\u01c6\u0005("+
		"\u0000\u0000\u01ba\u01c6\u0005.\u0000\u0000\u01bb\u01c6\u0005/\u0000\u0000"+
		"\u01bc\u01c6\u0005-\u0000\u0000\u01bd\u01c6\u0005\u0002\u0000\u0000\u01be"+
		"\u01c6\u00034\u001a\u0000\u01bf\u01c6\u00036\u001b\u0000\u01c0\u01c6\u0003"+
		"8\u001c\u0000\u01c1\u01c2\u0005K\u0000\u0000\u01c2\u01c3\u0003.\u0017"+
		"\u0000\u01c3\u01c4\u0005L\u0000\u0000\u01c4\u01c6\u0001\u0000\u0000\u0000"+
		"\u01c5\u01b3\u0001\u0000\u0000\u0000\u01c5\u01b4\u0001\u0000\u0000\u0000"+
		"\u01c5\u01b5\u0001\u0000\u0000\u0000\u01c5\u01b6\u0001\u0000\u0000\u0000"+
		"\u01c5\u01b7\u0001\u0000\u0000\u0000\u01c5\u01b8\u0001\u0000\u0000\u0000"+
		"\u01c5\u01b9\u0001\u0000\u0000\u0000\u01c5\u01ba\u0001\u0000\u0000\u0000"+
		"\u01c5\u01bb\u0001\u0000\u0000\u0000\u01c5\u01bc\u0001\u0000\u0000\u0000"+
		"\u01c5\u01bd\u0001\u0000\u0000\u0000\u01c5\u01be\u0001\u0000\u0000\u0000"+
		"\u01c5\u01bf\u0001\u0000\u0000\u0000\u01c5\u01c0\u0001\u0000\u0000\u0000"+
		"\u01c5\u01c1\u0001\u0000\u0000\u0000\u01c63\u0001\u0000\u0000\u0000\u01c7"+
		"\u01c9\u0003@ \u0000\u01c8\u01ca\u0003<\u001e\u0000\u01c9\u01c8\u0001"+
		"\u0000\u0000\u0000\u01c9\u01ca\u0001\u0000\u0000\u0000\u01ca5\u0001\u0000"+
		"\u0000\u0000\u01cb\u01cc\u0005O\u0000\u0000\u01cc\u01cd\u0003.\u0017\u0000"+
		"\u01cd\u01ce\u0005P\u0000\u0000\u01ce7\u0001\u0000\u0000\u0000\u01cf\u01d0"+
		"\u0005\u0004\u0000\u0000\u01d0\u01d1\u0005K\u0000\u0000\u01d1\u01d2\u0003"+
		":\u001d\u0000\u01d2\u01d5\u0005L\u0000\u0000\u01d3\u01d4\u00053\u0000"+
		"\u0000\u01d4\u01d6\u0003.\u0017\u0000\u01d5\u01d3\u0001\u0000\u0000\u0000"+
		"\u01d5\u01d6\u0001\u0000\u0000\u0000\u01d6\u01d8\u0001\u0000\u0000\u0000"+
		"\u01d7\u01d9\u0003\\.\u0000\u01d8\u01d7\u0001\u0000\u0000\u0000\u01d8"+
		"\u01d9\u0001\u0000\u0000\u0000\u01d99\u0001\u0000\u0000\u0000\u01da\u01df"+
		"\u0003.\u0017\u0000\u01db\u01dc\u0005R\u0000\u0000\u01dc\u01de\u0003."+
		"\u0017\u0000\u01dd\u01db\u0001\u0000\u0000\u0000\u01de\u01e1\u0001\u0000"+
		"\u0000\u0000\u01df\u01dd\u0001\u0000\u0000\u0000\u01df\u01e0\u0001\u0000"+
		"\u0000\u0000\u01e0\u01e3\u0001\u0000\u0000\u0000\u01e1\u01df\u0001\u0000"+
		"\u0000\u0000\u01e2\u01da\u0001\u0000\u0000\u0000\u01e2\u01e3\u0001\u0000"+
		"\u0000\u0000\u01e3;\u0001\u0000\u0000\u0000\u01e4\u01ed\u0005F\u0000\u0000"+
		"\u01e5\u01ea\u0003.\u0017\u0000\u01e6\u01e7\u0005R\u0000\u0000\u01e7\u01e9"+
		"\u0003.\u0017\u0000\u01e8\u01e6\u0001\u0000\u0000\u0000\u01e9\u01ec\u0001"+
		"\u0000\u0000\u0000\u01ea\u01e8\u0001\u0000\u0000\u0000\u01ea\u01eb\u0001"+
		"\u0000\u0000\u0000\u01eb\u01ee\u0001\u0000\u0000\u0000\u01ec\u01ea\u0001"+
		"\u0000\u0000\u0000\u01ed\u01e5\u0001\u0000\u0000\u0000\u01ed\u01ee\u0001"+
		"\u0000\u0000\u0000\u01ee\u01ef\u0001\u0000\u0000\u0000\u01ef\u01f0\u0005"+
		"E\u0000\u0000\u01f0=\u0001\u0000\u0000\u0000\u01f1\u01fa\u0005F\u0000"+
		"\u0000\u01f2\u01f7\u0003\u0098L\u0000\u01f3\u01f4\u0005R\u0000\u0000\u01f4"+
		"\u01f6\u0003\u0098L\u0000\u01f5\u01f3\u0001\u0000\u0000\u0000\u01f6\u01f9"+
		"\u0001\u0000\u0000\u0000\u01f7\u01f5\u0001\u0000\u0000\u0000\u01f7\u01f8"+
		"\u0001\u0000\u0000\u0000\u01f8\u01fb\u0001\u0000\u0000\u0000\u01f9\u01f7"+
		"\u0001\u0000\u0000\u0000\u01fa\u01f2\u0001\u0000\u0000\u0000\u01fa\u01fb"+
		"\u0001\u0000\u0000\u0000\u01fb\u01fc\u0001\u0000\u0000\u0000\u01fc\u01fd"+
		"\u0005E\u0000\u0000\u01fd?\u0001\u0000\u0000\u0000\u01fe\u0203\u0003\u0098"+
		"L\u0000\u01ff\u0200\u0005I\u0000\u0000\u0200\u0202\u0003\u0098L\u0000"+
		"\u0201\u01ff\u0001\u0000\u0000\u0000\u0202\u0205\u0001\u0000\u0000\u0000"+
		"\u0203\u0201\u0001\u0000\u0000\u0000\u0203\u0204\u0001\u0000\u0000\u0000"+
		"\u0204A\u0001\u0000\u0000\u0000\u0205\u0203\u0001\u0000\u0000\u0000\u0206"+
		"\u0207\u0005Q\u0000\u0000\u0207\u020a\u0003\u0098L\u0000\u0208\u0209\u0005"+
		"J\u0000\u0000\u0209\u020b\u0003P(\u0000\u020a\u0208\u0001\u0000\u0000"+
		"\u0000\u020a\u020b\u0001\u0000\u0000\u0000\u020bC\u0001\u0000\u0000\u0000"+
		"\u020c\u0217\u0005M\u0000\u0000\u020d\u0214\u0003F#\u0000\u020e\u0210"+
		"\u0005R\u0000\u0000\u020f\u020e\u0001\u0000\u0000\u0000\u020f\u0210\u0001"+
		"\u0000\u0000\u0000\u0210\u0211\u0001\u0000\u0000\u0000\u0211\u0213\u0003"+
		"F#\u0000\u0212\u020f\u0001\u0000\u0000\u0000\u0213\u0216\u0001\u0000\u0000"+
		"\u0000\u0214\u0212\u0001\u0000\u0000\u0000\u0214\u0215\u0001\u0000\u0000"+
		"\u0000\u0215\u0218\u0001\u0000\u0000\u0000\u0216\u0214\u0001\u0000\u0000"+
		"\u0000\u0217\u020d\u0001\u0000\u0000\u0000\u0217\u0218\u0001\u0000\u0000"+
		"\u0000\u0218\u0219\u0001\u0000\u0000\u0000\u0219\u021a\u0005N\u0000\u0000"+
		"\u021aE\u0001\u0000\u0000\u0000\u021b\u021e\u0005Y\u0000\u0000\u021c\u021e"+
		"\u0003\u0098L\u0000\u021d\u021b\u0001\u0000\u0000\u0000\u021d\u021c\u0001"+
		"\u0000\u0000\u0000\u021e\u021f\u0001\u0000\u0000\u0000\u021f\u0220\u0005"+
		"H\u0000\u0000\u0220\u0221\u0003P(\u0000\u0221G\u0001\u0000\u0000\u0000"+
		"\u0222\u022d\u0005O\u0000\u0000\u0223\u022a\u0003P(\u0000\u0224\u0226"+
		"\u0005R\u0000\u0000\u0225\u0224\u0001\u0000\u0000\u0000\u0225\u0226\u0001"+
		"\u0000\u0000\u0000\u0226\u0227\u0001\u0000\u0000\u0000\u0227\u0229\u0003"+
		"P(\u0000\u0228\u0225\u0001\u0000\u0000\u0000\u0229\u022c\u0001\u0000\u0000"+
		"\u0000\u022a\u0228\u0001\u0000\u0000\u0000\u022a\u022b\u0001\u0000\u0000"+
		"\u0000\u022b\u022e\u0001\u0000\u0000\u0000\u022c\u022a\u0001\u0000\u0000"+
		"\u0000\u022d\u0223\u0001\u0000\u0000\u0000\u022d\u022e\u0001\u0000\u0000"+
		"\u0000\u022e\u022f\u0001\u0000\u0000\u0000\u022f\u0230\u0005P\u0000\u0000"+
		"\u0230I\u0001\u0000\u0000\u0000\u0231\u0232\u0005\u0001\u0000\u0000\u0232"+
		"\u0233\u0003R)\u0000\u0233K\u0001\u0000\u0000\u0000\u0234\u0235\u0005"+
		"\u0003\u0000\u0000\u0235\u0236\u0005M\u0000\u0000\u0236\u0237\u0003.\u0017"+
		"\u0000\u0237\u0238\u0005N\u0000\u0000\u0238M\u0001\u0000\u0000\u0000\u0239"+
		"\u023a\u0005\u0004\u0000\u0000\u023a\u023b\u0005M\u0000\u0000\u023b\u023c"+
		"\u0003\b\u0004\u0000\u023c\u023d\u0005N\u0000\u0000\u023dO\u0001\u0000"+
		"\u0000\u0000\u023e\u0249\u0005Y\u0000\u0000\u023f\u0249\u0005[\u0000\u0000"+
		"\u0240\u0249\u0003D\"\u0000\u0241\u0249\u0003H$\u0000\u0242\u0249\u0005"+
		"\u000f\u0000\u0000\u0243\u0249\u0005\u0010\u0000\u0000\u0244\u0249\u0005"+
		"\b\u0000\u0000\u0245\u0249\u0003J%\u0000\u0246\u0249\u0003L&\u0000\u0247"+
		"\u0249\u0003N\'\u0000\u0248\u023e\u0001\u0000\u0000\u0000\u0248\u023f"+
		"\u0001\u0000\u0000\u0000\u0248\u0240\u0001\u0000\u0000\u0000\u0248\u0241"+
		"\u0001\u0000\u0000\u0000\u0248\u0242\u0001\u0000\u0000\u0000\u0248\u0243"+
		"\u0001\u0000\u0000\u0000\u0248\u0244\u0001\u0000\u0000\u0000\u0248\u0245"+
		"\u0001\u0000\u0000\u0000\u0248\u0246\u0001\u0000\u0000\u0000\u0248\u0247"+
		"\u0001\u0000\u0000\u0000\u0249Q\u0001\u0000\u0000\u0000\u024a\u024e\u0005"+
		"M\u0000\u0000\u024b\u024d\u0003V+\u0000\u024c\u024b\u0001\u0000\u0000"+
		"\u0000\u024d\u0250\u0001\u0000\u0000\u0000\u024e\u024c\u0001\u0000\u0000"+
		"\u0000\u024e\u024f\u0001\u0000\u0000\u0000\u024f\u0251\u0001\u0000\u0000"+
		"\u0000\u0250\u024e\u0001\u0000\u0000\u0000\u0251\u0252\u0005N\u0000\u0000"+
		"\u0252S\u0001\u0000\u0000\u0000\u0253\u0256\u0003R)\u0000\u0254\u0256"+
		"\u0003V+\u0000\u0255\u0253\u0001\u0000\u0000\u0000\u0255\u0254\u0001\u0000"+
		"\u0000\u0000\u0256U\u0001\u0000\u0000\u0000\u0257\u0266\u0003X,\u0000"+
		"\u0258\u0266\u0003Z-\u0000\u0259\u025b\u0005\u0013\u0000\u0000\u025a\u025c"+
		"\u0003T*\u0000\u025b\u025a\u0001\u0000\u0000\u0000\u025b\u025c\u0001\u0000"+
		"\u0000\u0000\u025c\u0266\u0001\u0000\u0000\u0000\u025d\u0266\u0003`0\u0000"+
		"\u025e\u0266\u0003h4\u0000\u025f\u0266\u0003n7\u0000\u0260\u0266\u0003"+
		"p8\u0000\u0261\u0266\u0003t:\u0000\u0262\u0266\u0005\u0012\u0000\u0000"+
		"\u0263\u0266\u00052\u0000\u0000\u0264\u0266\u0003r9\u0000\u0265\u0257"+
		"\u0001\u0000\u0000\u0000\u0265\u0258\u0001\u0000\u0000\u0000\u0265\u0259"+
		"\u0001\u0000\u0000\u0000\u0265\u025d\u0001\u0000\u0000\u0000\u0265\u025e"+
		"\u0001\u0000\u0000\u0000\u0265\u025f\u0001\u0000\u0000\u0000\u0265\u0260"+
		"\u0001\u0000\u0000\u0000\u0265\u0261\u0001\u0000\u0000\u0000\u0265\u0262"+
		"\u0001\u0000\u0000\u0000\u0265\u0263\u0001\u0000\u0000\u0000\u0265\u0264"+
		"\u0001\u0000\u0000\u0000\u0266W\u0001\u0000\u0000\u0000\u0267\u0268\u0005"+
		"\u001c\u0000\u0000\u0268\u026b\u0003\u0098L\u0000\u0269\u026a\u0005H\u0000"+
		"\u0000\u026a\u026c\u0003.\u0017\u0000\u026b\u0269\u0001\u0000\u0000\u0000"+
		"\u026b\u026c\u0001\u0000\u0000\u0000\u026c\u026d\u0001\u0000\u0000\u0000"+
		"\u026d\u026e\u0005J\u0000\u0000\u026e\u026f\u0003T*\u0000\u026fY\u0001"+
		"\u0000\u0000\u0000\u0270\u0271\u0005\u0004\u0000\u0000\u0271\u0272\u0005"+
		"K\u0000\u0000\u0272\u0273\u0003\u0094J\u0000\u0273\u0276\u0005L\u0000"+
		"\u0000\u0274\u0275\u00053\u0000\u0000\u0275\u0277\u0003.\u0017\u0000\u0276"+
		"\u0274\u0001\u0000\u0000\u0000\u0276\u0277\u0001\u0000\u0000\u0000\u0277"+
		"\u0279\u0001\u0000\u0000\u0000\u0278\u027a\u0003\\.\u0000\u0279\u0278"+
		"\u0001\u0000\u0000\u0000\u0279\u027a\u0001\u0000\u0000\u0000\u027a\u027b"+
		"\u0001\u0000\u0000\u0000\u027b\u027c\u0003T*\u0000\u027c[\u0001\u0000"+
		"\u0000\u0000\u027d\u0283\u0005\u0019\u0000\u0000\u027e\u0284\u0003^/\u0000"+
		"\u027f\u0280\u0005K\u0000\u0000\u0280\u0281\u0003^/\u0000\u0281\u0282"+
		"\u0005L\u0000\u0000\u0282\u0284\u0001\u0000\u0000\u0000\u0283\u027e\u0001"+
		"\u0000\u0000\u0000\u0283\u027f\u0001\u0000\u0000\u0000\u0284]\u0001\u0000"+
		"\u0000\u0000\u0285\u028a\u00034\u001a\u0000\u0286\u0287\u0005R\u0000\u0000"+
		"\u0287\u0289\u00034\u001a\u0000\u0288\u0286\u0001\u0000\u0000\u0000\u0289"+
		"\u028c\u0001\u0000\u0000\u0000\u028a\u0288\u0001\u0000\u0000\u0000\u028a"+
		"\u028b\u0001\u0000\u0000\u0000\u028b_\u0001\u0000\u0000\u0000\u028c\u028a"+
		"\u0001\u0000\u0000\u0000\u028d\u028e\u0005\f\u0000\u0000\u028e\u028f\u0003"+
		"T*\u0000\u028f\u0293\u0005M\u0000\u0000\u0290\u0292\u0003b1\u0000\u0291"+
		"\u0290\u0001\u0000\u0000\u0000\u0292\u0295\u0001\u0000\u0000\u0000\u0293"+
		"\u0291\u0001\u0000\u0000\u0000\u0293\u0294\u0001\u0000\u0000\u0000\u0294"+
		"\u0296\u0001\u0000\u0000\u0000\u0295\u0293\u0001\u0000\u0000\u0000\u0296"+
		"\u0297\u0005N\u0000\u0000\u0297a\u0001\u0000\u0000\u0000\u0298\u029b\u0003"+
		"f3\u0000\u0299\u029b\u0003d2\u0000\u029a\u0298\u0001\u0000\u0000\u0000"+
		"\u029a\u0299\u0001\u0000\u0000\u0000\u029b\u029c\u0001\u0000\u0000\u0000"+
		"\u029c\u029d\u00053\u0000\u0000\u029d\u029e\u0003T*\u0000\u029ec\u0001"+
		"\u0000\u0000\u0000\u029f\u02a5\u00034\u001a\u0000\u02a0\u02a6\u0003\u0098"+
		"L\u0000\u02a1\u02a2\u0005K\u0000\u0000\u02a2\u02a3\u0003\u0094J\u0000"+
		"\u02a3\u02a4\u0005L\u0000\u0000\u02a4\u02a6\u0001\u0000\u0000\u0000\u02a5"+
		"\u02a0\u0001\u0000\u0000\u0000\u02a5\u02a1\u0001\u0000\u0000\u0000\u02a6"+
		"e\u0001\u0000\u0000\u0000\u02a7\u02a8\u0005S\u0000\u0000\u02a8g\u0001"+
		"\u0000\u0000\u0000\u02a9\u02aa\u0005\u001d\u0000\u0000\u02aa\u02b0\u0003"+
		"T*\u0000\u02ab\u02b1\u0003\u0098L\u0000\u02ac\u02ad\u0005K\u0000\u0000"+
		"\u02ad\u02ae\u0003\u0094J\u0000\u02ae\u02af\u0005L\u0000\u0000\u02af\u02b1"+
		"\u0001\u0000\u0000\u0000\u02b0\u02ab\u0001\u0000\u0000\u0000\u02b0\u02ac"+
		"\u0001\u0000\u0000\u0000\u02b0\u02b1\u0001\u0000\u0000\u0000\u02b1\u02b2"+
		"\u0001\u0000\u0000\u0000\u02b2\u02b4\u0003R)\u0000\u02b3\u02b5\u0003j"+
		"5\u0000\u02b4\u02b3\u0001\u0000\u0000\u0000\u02b4\u02b5\u0001\u0000\u0000"+
		"\u0000\u02b5i\u0001\u0000\u0000\u0000\u02b6\u02b8\u0005\u001f\u0000\u0000"+
		"\u02b7\u02b9\u0003l6\u0000\u02b8\u02b7\u0001\u0000\u0000\u0000\u02b8\u02b9"+
		"\u0001\u0000\u0000\u0000\u02b9\u02bd\u0001\u0000\u0000\u0000\u02ba\u02be"+
		"\u0003h4\u0000\u02bb\u02be\u0003R)\u0000\u02bc\u02be\u0003`0\u0000\u02bd"+
		"\u02ba\u0001\u0000\u0000\u0000\u02bd\u02bb\u0001\u0000\u0000\u0000\u02bd"+
		"\u02bc\u0001\u0000\u0000\u0000\u02bek\u0001\u0000\u0000\u0000\u02bf\u02c0"+
		"\u0005\u0005\u0000\u0000\u02c0\u02c6\u0003.\u0017\u0000\u02c1\u02c7\u0003"+
		"\u0098L\u0000\u02c2\u02c3\u0005K\u0000\u0000\u02c3\u02c4\u0003\u0094J"+
		"\u0000\u02c4\u02c5\u0005L\u0000\u0000\u02c5\u02c7\u0001\u0000\u0000\u0000"+
		"\u02c6\u02c1\u0001\u0000\u0000\u0000\u02c6\u02c2\u0001\u0000\u0000\u0000"+
		"\u02c6\u02c7\u0001\u0000\u0000\u0000\u02c7m\u0001\u0000\u0000\u0000\u02c8"+
		"\u02c9\u0005 \u0000\u0000\u02c9\u02ca\u0003T*\u0000\u02ca\u02cb\u0003"+
		"R)\u0000\u02cbo\u0001\u0000\u0000\u0000\u02cc\u02cd\u0005!\u0000\u0000"+
		"\u02cd\u02ce\u0003\u0096K\u0000\u02ce\u02cf\u0005\u0006\u0000\u0000\u02cf"+
		"\u02d0\u0003T*\u0000\u02d0\u02d1\u0003R)\u0000\u02d1q\u0001\u0000\u0000"+
		"\u0000\u02d2\u02d3\u0005\u0017\u0000\u0000\u02d3\u02d4\u0003T*\u0000\u02d4"+
		"s\u0001\u0000\u0000\u0000\u02d5\u02d8\u0003v;\u0000\u02d6\u02d7\u0005"+
		"<\u0000\u0000\u02d7\u02d9\u0003t:\u0000\u02d8\u02d6\u0001\u0000\u0000"+
		"\u0000\u02d8\u02d9\u0001\u0000\u0000\u0000\u02d9u\u0001\u0000\u0000\u0000"+
		"\u02da\u02dd\u0003x<\u0000\u02db\u02dc\u0005;\u0000\u0000\u02dc\u02de"+
		"\u0003v;\u0000\u02dd\u02db\u0001\u0000\u0000\u0000\u02dd\u02de\u0001\u0000"+
		"\u0000\u0000\u02dew\u0001\u0000\u0000\u0000\u02df\u02e2\u0003z=\u0000"+
		"\u02e0\u02e1\u0007\u0000\u0000\u0000\u02e1\u02e3\u0003x<\u0000\u02e2\u02e0"+
		"\u0001\u0000\u0000\u0000\u02e2\u02e3\u0001\u0000\u0000\u0000\u02e3y\u0001"+
		"\u0000\u0000\u0000\u02e4\u02e7\u0003|>\u0000\u02e5\u02e6\u0007\u0001\u0000"+
		"\u0000\u02e6\u02e8\u0003z=\u0000\u02e7\u02e5\u0001\u0000\u0000\u0000\u02e7"+
		"\u02e8\u0001\u0000\u0000\u0000\u02e8{\u0001\u0000\u0000\u0000\u02e9\u02ec"+
		"\u0003~?\u0000\u02ea\u02eb\u0007\u0002\u0000\u0000\u02eb\u02ed\u0003|"+
		">\u0000\u02ec\u02ea\u0001\u0000\u0000\u0000\u02ec\u02ed\u0001\u0000\u0000"+
		"\u0000\u02ed}\u0001\u0000\u0000\u0000\u02ee\u02f1\u0003\u0080@\u0000\u02ef"+
		"\u02f0\u0007\u0003\u0000\u0000\u02f0\u02f2\u0003~?\u0000\u02f1\u02ef\u0001"+
		"\u0000\u0000\u0000\u02f1\u02f2\u0001\u0000\u0000\u0000\u02f2\u007f\u0001"+
		"\u0000\u0000\u0000\u02f3\u02f5\u0007\u0004\u0000\u0000\u02f4\u02f3\u0001"+
		"\u0000\u0000\u0000\u02f4\u02f5\u0001\u0000\u0000\u0000\u02f5\u02f6\u0001"+
		"\u0000\u0000\u0000\u02f6\u02f7\u0003\u0082A\u0000\u02f7\u0081\u0001\u0000"+
		"\u0000\u0000\u02f8\u02fc\u0003\u0086C\u0000\u02f9\u02fb\u0003\u0084B\u0000"+
		"\u02fa\u02f9\u0001\u0000\u0000\u0000\u02fb\u02fe\u0001\u0000\u0000\u0000"+
		"\u02fc\u02fa\u0001\u0000\u0000\u0000\u02fc\u02fd\u0001\u0000\u0000\u0000"+
		"\u02fd\u0302\u0001\u0000\u0000\u0000\u02fe\u02fc\u0001\u0000\u0000\u0000"+
		"\u02ff\u0300\u0005J\u0000\u0000\u0300\u0303\u0003T*\u0000\u0301\u0303"+
		"\u0003\u0092I\u0000\u0302\u02ff\u0001\u0000\u0000\u0000\u0302\u0301\u0001"+
		"\u0000\u0000\u0000\u0302\u0303\u0001\u0000\u0000\u0000\u0303\u0083\u0001"+
		"\u0000\u0000\u0000\u0304\u0307\u0005U\u0000\u0000\u0305\u0308\u0003\u0098"+
		"L\u0000\u0306\u0308\u0005\u001b\u0000\u0000\u0307\u0305\u0001\u0000\u0000"+
		"\u0000\u0307\u0306\u0001\u0000\u0000\u0000\u0308\u0318\u0001\u0000\u0000"+
		"\u0000\u0309\u030b\u0003<\u001e\u0000\u030a\u0309\u0001\u0000\u0000\u0000"+
		"\u030a\u030b\u0001\u0000\u0000\u0000\u030b\u030c\u0001\u0000\u0000\u0000"+
		"\u030c\u030d\u0005K\u0000\u0000\u030d\u030e\u0003\u008cF\u0000\u030e\u030f"+
		"\u0005L\u0000\u0000\u030f\u0318\u0001\u0000\u0000\u0000\u0310\u0311\u0005"+
		"O\u0000\u0000\u0311\u0312\u0003T*\u0000\u0312\u0313\u0005P\u0000\u0000"+
		"\u0313\u0318\u0001\u0000\u0000\u0000\u0314\u0315\u0005\u0007\u0000\u0000"+
		"\u0315\u0318\u0003.\u0017\u0000\u0316\u0318\u0005V\u0000\u0000\u0317\u0304"+
		"\u0001\u0000\u0000\u0000\u0317\u030a\u0001\u0000\u0000\u0000\u0317\u0310"+
		"\u0001\u0000\u0000\u0000\u0317\u0314\u0001\u0000\u0000\u0000\u0317\u0316"+
		"\u0001\u0000\u0000\u0000\u0318\u0085\u0001\u0000\u0000\u0000\u0319\u031a"+
		"\u0005K\u0000\u0000\u031a\u031b\u0003T*\u0000\u031b\u031c\u0005L\u0000"+
		"\u0000\u031c\u0337\u0001\u0000\u0000\u0000\u031d\u0337\u0005[\u0000\u0000"+
		"\u031e\u0323\u0003\u0098L\u0000\u031f\u0320\u0005I\u0000\u0000\u0320\u0322"+
		"\u0003\u0098L\u0000\u0321\u031f\u0001\u0000\u0000\u0000\u0322\u0325\u0001"+
		"\u0000\u0000\u0000\u0323\u0321\u0001\u0000\u0000\u0000\u0323\u0324\u0001"+
		"\u0000\u0000\u0000\u0324\u032d\u0001\u0000\u0000\u0000\u0325\u0323\u0001"+
		"\u0000\u0000\u0000\u0326\u0328\u0003<\u001e\u0000\u0327\u0326\u0001\u0000"+
		"\u0000\u0000\u0327\u0328\u0001\u0000\u0000\u0000\u0328\u0329\u0001\u0000"+
		"\u0000\u0000\u0329\u032a\u0005M\u0000\u0000\u032a\u032b\u0003\u008eG\u0000"+
		"\u032b\u032c\u0005N\u0000\u0000\u032c\u032e\u0001\u0000\u0000\u0000\u032d"+
		"\u0327\u0001\u0000\u0000\u0000\u032d\u032e\u0001\u0000\u0000\u0000\u032e"+
		"\u0337\u0001\u0000\u0000\u0000\u032f\u0337\u0005Y\u0000\u0000\u0330\u0337"+
		"\u0005Z\u0000\u0000\u0331\u0337\u0005%\u0000\u0000\u0332\u0337\u0003\u008a"+
		"E\u0000\u0333\u0337\u0005\u000f\u0000\u0000\u0334\u0337\u0005\u0010\u0000"+
		"\u0000\u0335\u0337\u0003\u0088D\u0000\u0336\u0319\u0001\u0000\u0000\u0000"+
		"\u0336\u031d\u0001\u0000\u0000\u0000\u0336\u031e\u0001\u0000\u0000\u0000"+
		"\u0336\u032f\u0001\u0000\u0000\u0000\u0336\u0330\u0001\u0000\u0000\u0000"+
		"\u0336\u0331\u0001\u0000\u0000\u0000\u0336\u0332\u0001\u0000\u0000\u0000"+
		"\u0336\u0333\u0001\u0000\u0000\u0000\u0336\u0334\u0001\u0000\u0000\u0000"+
		"\u0336\u0335\u0001\u0000\u0000\u0000\u0337\u0087\u0001\u0000\u0000\u0000"+
		"\u0338\u0339\u0005F\u0000\u0000\u0339\u033a\u0003.\u0017\u0000\u033a\u033b"+
		"\u0005E\u0000\u0000\u033b\u033d\u0001\u0000\u0000\u0000\u033c\u0338\u0001"+
		"\u0000\u0000\u0000\u033c\u033d\u0001\u0000\u0000\u0000\u033d\u033e\u0001"+
		"\u0000\u0000\u0000\u033e\u033f\u0005O\u0000\u0000\u033f\u0340\u0003\u008c"+
		"F\u0000\u0340\u0341\u0005P\u0000\u0000\u0341\u0089\u0001\u0000\u0000\u0000"+
		"\u0342\u0343\u0005\"\u0000\u0000\u0343\u0344\u0005F\u0000\u0000\u0344"+
		"\u0345\u00034\u001a\u0000\u0345\u0348\u0005E\u0000\u0000\u0346\u0347\u0005"+
		"U\u0000\u0000\u0347\u0349\u0003\u0098L\u0000\u0348\u0346\u0001\u0000\u0000"+
		"\u0000\u0348\u0349\u0001\u0000\u0000\u0000\u0349\u008b\u0001\u0000\u0000"+
		"\u0000\u034a\u034f\u0003T*\u0000\u034b\u034c\u0005R\u0000\u0000\u034c"+
		"\u034e\u0003T*\u0000\u034d\u034b\u0001\u0000\u0000\u0000\u034e\u0351\u0001"+
		"\u0000\u0000\u0000\u034f\u034d\u0001\u0000\u0000\u0000\u034f\u0350\u0001"+
		"\u0000\u0000\u0000\u0350\u0353\u0001\u0000\u0000\u0000\u0351\u034f\u0001"+
		"\u0000\u0000\u0000\u0352\u034a\u0001\u0000\u0000\u0000\u0352\u0353\u0001"+
		"\u0000\u0000\u0000\u0353\u008d\u0001\u0000\u0000\u0000\u0354\u0359\u0003"+
		"\u0090H\u0000\u0355\u0356\u0005R\u0000\u0000\u0356\u0358\u0003\u0090H"+
		"\u0000\u0357\u0355\u0001\u0000\u0000\u0000\u0358\u035b\u0001\u0000\u0000"+
		"\u0000\u0359\u0357\u0001\u0000\u0000\u0000\u0359\u035a\u0001\u0000\u0000"+
		"\u0000\u035a\u035d\u0001\u0000\u0000\u0000\u035b\u0359\u0001\u0000\u0000"+
		"\u0000\u035c\u0354\u0001\u0000\u0000\u0000\u035c\u035d\u0001\u0000\u0000"+
		"\u0000\u035d\u008f\u0001\u0000\u0000\u0000\u035e\u035f\u0003\u0098L\u0000"+
		"\u035f\u0360\u0005H\u0000\u0000\u0360\u0361\u0003T*\u0000\u0361\u0091"+
		"\u0001\u0000\u0000\u0000\u0362\u0363\u0005\u0005\u0000\u0000\u0363\u0364"+
		"\u0003.\u0017\u0000\u0364\u0093\u0001\u0000\u0000\u0000\u0365\u036a\u0003"+
		"\u0096K\u0000\u0366\u0367\u0005R\u0000\u0000\u0367\u0369\u0003\u0096K"+
		"\u0000\u0368\u0366\u0001\u0000\u0000\u0000\u0369\u036c\u0001\u0000\u0000"+
		"\u0000\u036a\u0368\u0001\u0000\u0000\u0000\u036a\u036b\u0001\u0000\u0000"+
		"\u0000\u036b\u036e\u0001\u0000\u0000\u0000\u036c\u036a\u0001\u0000\u0000"+
		"\u0000\u036d\u0365\u0001\u0000\u0000\u0000\u036d\u036e\u0001\u0000\u0000"+
		"\u0000\u036e\u0095\u0001\u0000\u0000\u0000\u036f\u0372\u0003\u0098L\u0000"+
		"\u0370\u0371\u0005H\u0000\u0000\u0371\u0373\u0003.\u0017\u0000\u0372\u0370"+
		"\u0001\u0000\u0000\u0000\u0372\u0373\u0001\u0000\u0000\u0000\u0373\u0097"+
		"\u0001\u0000\u0000\u0000\u0374\u037b\u0005^\u0000\u0000\u0375\u037b\u0005"+
		"\u0001\u0000\u0000\u0376\u037b\u0005\u0003\u0000\u0000\u0377\u0378\u0005"+
		"X\u0000\u0000\u0378\u037b\u0003\u009aM\u0000\u0379\u037b\u0005S\u0000"+
		"\u0000\u037a\u0374\u0001\u0000\u0000\u0000\u037a\u0375\u0001\u0000\u0000"+
		"\u0000\u037a\u0376\u0001\u0000\u0000\u0000\u037a\u0377\u0001\u0000\u0000"+
		"\u0000\u037a\u0379\u0001\u0000\u0000\u0000\u037b\u0099\u0001\u0000\u0000"+
		"\u0000\u037c\u037d\u0007\u0005\u0000\u0000\u037d\u009b\u0001\u0000\u0000"+
		"\u0000l\u009f\u00a5\u00b4\u00bb\u00c1\u00c9\u00cd\u00d0\u00d7\u00dc\u00e2"+
		"\u00eb\u00f1\u00f7\u00fd\u0103\u0109\u010d\u0115\u0119\u0121\u012b\u012e"+
		"\u0135\u0138\u0145\u0148\u014d\u0154\u015a\u0160\u0166\u016c\u0176\u017b"+
		"\u0181\u0187\u018b\u0192\u0198\u019b\u01a2\u01a5\u01ad\u01b1\u01c5\u01c9"+
		"\u01d5\u01d8\u01df\u01e2\u01ea\u01ed\u01f7\u01fa\u0203\u020a\u020f\u0214"+
		"\u0217\u021d\u0225\u022a\u022d\u0248\u024e\u0255\u025b\u0265\u026b\u0276"+
		"\u0279\u0283\u028a\u0293\u029a\u02a5\u02b0\u02b4\u02b8\u02bd\u02c6\u02d8"+
		"\u02dd\u02e2\u02e7\u02ec\u02f1\u02f4\u02fc\u0302\u0307\u030a\u0317\u0323"+
		"\u0327\u032d\u0336\u033c\u0348\u034f\u0352\u0359\u035c\u036a\u036d\u0372"+
		"\u037a";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}