// Generated from C:/Users/Morit/Documents/Projects/Java/ttyl/resources/KarinaParser.g4 by ANTLR 4.13.2
package org.karina.lang.compiler.parser.gen;
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
		FN=1, IS=2, IN=3, AS=4, OF=5, NULL=6, IMPORT=7, EXTENDS=8, EXTEND=9, MATCH=10, 
		OVERRIDE=11, NATIVE=12, TRUE=13, FALSE=14, VIRTUAL=15, BREAK=16, RETURN=17, 
		YIELD=18, STRUCT=19, STATIC=20, RAISE=21, TRAIT=22, IMPL=23, ENUM=24, 
		CLASS=25, LET=26, IF=27, ELSE=28, MATCHES=29, WHILE=30, FOR=31, SUPER=32, 
		INTERFACE=33, SELF=34, INT=35, LONG=36, BYTE=37, CHAR=38, DOUBLE=39, SHORT=40, 
		STRING=41, FLOAT=42, BOOL=43, VOID=44, JSON=45, CONTINUE=46, JAVA_IMPORT=47, 
		ARROW_RIGHT=48, ARROW_RIGHT_BOLD=49, GREATER_EQULAS=50, SMALLER_EQUALS=51, 
		EQUALS=52, NOT_EQUALS=53, AND_AND=54, OR_OR=55, CHAR_PLIS=56, CHAR_MINUS=57, 
		CHAR_STAR=58, CHAR_R_SLASH=59, CHAR_PERCENT=60, CHAR_OR=61, CHAR_XOR=62, 
		CHAR_TILDE=63, CHAR_GREATER=64, CHAR_SMALLER=65, CHAR_EXCLAMATION=66, 
		CHAR_COLON=67, CHAR_EQUAL=68, CHAR_L_PAREN=69, CHAR_R_PAREN=70, CHAR_L_BRACE=71, 
		CHAR_R_BRACE=72, CHAR_L_BRACKET=73, CHAR_R_BRACKET=74, CHAR_AT=75, CHAR_COMMA=76, 
		CHAR_UNDER=77, CHAR_AND=78, CHAR_DOT=79, CHAR_QUESTION=80, CHAR_SEMICOLON=81, 
		STRING_LITERAL=82, NUMBER=83, INTEGER_NUMBER=84, FLOAT_NUMBER=85, ID=86, 
		WS=87, COMMENT=88, LINE_COMMENT=89;
	public static final int
		RULE_unit = 0, RULE_import_ = 1, RULE_item = 2, RULE_function = 3, RULE_struct = 4, 
		RULE_implementation = 5, RULE_field = 6, RULE_enum = 7, RULE_enumMember = 8, 
		RULE_interface = 9, RULE_interfaceExtension = 10, RULE_parameterList = 11, 
		RULE_parameter = 12, RULE_type = 13, RULE_structType = 14, RULE_arrayType = 15, 
		RULE_functionType = 16, RULE_typeList = 17, RULE_genericHint = 18, RULE_genericHintDefinition = 19, 
		RULE_dotWordChain = 20, RULE_annotation = 21, RULE_jsonObj = 22, RULE_jsonPair = 23, 
		RULE_jsonArray = 24, RULE_jsonValue = 25, RULE_block = 26, RULE_exprWithBlock = 27, 
		RULE_expression = 28, RULE_varDef = 29, RULE_closure = 30, RULE_interfaceImpl = 31, 
		RULE_structTypeList = 32, RULE_match = 33, RULE_matchCase = 34, RULE_matchInstance = 35, 
		RULE_matchDefault = 36, RULE_if = 37, RULE_elseExpr = 38, RULE_while = 39, 
		RULE_for = 40, RULE_throw = 41, RULE_conditionalOrExpression = 42, RULE_conditionalAndExpression = 43, 
		RULE_equalityExpression = 44, RULE_relationalExpression = 45, RULE_additiveExpression = 46, 
		RULE_multiplicativeExpression = 47, RULE_unaryExpression = 48, RULE_factor = 49, 
		RULE_postFix = 50, RULE_object = 51, RULE_array = 52, RULE_expressionList = 53, 
		RULE_initList = 54, RULE_memberInit = 55, RULE_isInstanceOf = 56, RULE_optTypeList = 57, 
		RULE_optTypeName = 58;
	private static String[] makeRuleNames() {
		return new String[] {
			"unit", "import_", "item", "function", "struct", "implementation", "field", 
			"enum", "enumMember", "interface", "interfaceExtension", "parameterList", 
			"parameter", "type", "structType", "arrayType", "functionType", "typeList", 
			"genericHint", "genericHintDefinition", "dotWordChain", "annotation", 
			"jsonObj", "jsonPair", "jsonArray", "jsonValue", "block", "exprWithBlock", 
			"expression", "varDef", "closure", "interfaceImpl", "structTypeList", 
			"match", "matchCase", "matchInstance", "matchDefault", "if", "elseExpr", 
			"while", "for", "throw", "conditionalOrExpression", "conditionalAndExpression", 
			"equalityExpression", "relationalExpression", "additiveExpression", "multiplicativeExpression", 
			"unaryExpression", "factor", "postFix", "object", "array", "expressionList", 
			"initList", "memberInit", "isInstanceOf", "optTypeList", "optTypeName"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'fn'", "'is'", "'in'", "'as'", "'of'", "'null'", "'import'", "'extends'", 
			"'extend'", "'match'", "'override'", "'native'", "'true'", "'false'", 
			"'virtual'", "'break'", "'return'", "'yield'", "'struct'", "'static'", 
			"'raise'", "'trait'", "'impl'", "'enum'", "'class'", "'let'", "'if'", 
			"'else'", "'matches'", "'while'", "'for'", "'super'", "'interface'", 
			"'self'", "'int'", "'long'", "'byte'", "'char'", "'double'", "'short'", 
			"'string'", "'float'", "'bool'", "'void'", "'json'", "'continue'", "'java::'", 
			"'->'", "'=>'", "'>='", "'<='", "'=='", "'!='", "'&&'", "'||'", "'+'", 
			"'-'", "'*'", "'/'", "'%'", "'|'", "'^'", "'~'", "'>'", "'<'", "'!'", 
			"':'", "'='", "'('", "')'", "'{'", "'}'", "'['", "']'", "'@'", "','", 
			"'_'", "'&'", "'.'", "'?'", "';'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "FN", "IS", "IN", "AS", "OF", "NULL", "IMPORT", "EXTENDS", "EXTEND", 
			"MATCH", "OVERRIDE", "NATIVE", "TRUE", "FALSE", "VIRTUAL", "BREAK", "RETURN", 
			"YIELD", "STRUCT", "STATIC", "RAISE", "TRAIT", "IMPL", "ENUM", "CLASS", 
			"LET", "IF", "ELSE", "MATCHES", "WHILE", "FOR", "SUPER", "INTERFACE", 
			"SELF", "INT", "LONG", "BYTE", "CHAR", "DOUBLE", "SHORT", "STRING", "FLOAT", 
			"BOOL", "VOID", "JSON", "CONTINUE", "JAVA_IMPORT", "ARROW_RIGHT", "ARROW_RIGHT_BOLD", 
			"GREATER_EQULAS", "SMALLER_EQUALS", "EQUALS", "NOT_EQUALS", "AND_AND", 
			"OR_OR", "CHAR_PLIS", "CHAR_MINUS", "CHAR_STAR", "CHAR_R_SLASH", "CHAR_PERCENT", 
			"CHAR_OR", "CHAR_XOR", "CHAR_TILDE", "CHAR_GREATER", "CHAR_SMALLER", 
			"CHAR_EXCLAMATION", "CHAR_COLON", "CHAR_EQUAL", "CHAR_L_PAREN", "CHAR_R_PAREN", 
			"CHAR_L_BRACE", "CHAR_R_BRACE", "CHAR_L_BRACKET", "CHAR_R_BRACKET", "CHAR_AT", 
			"CHAR_COMMA", "CHAR_UNDER", "CHAR_AND", "CHAR_DOT", "CHAR_QUESTION", 
			"CHAR_SEMICOLON", "STRING_LITERAL", "NUMBER", "INTEGER_NUMBER", "FLOAT_NUMBER", 
			"ID", "WS", "COMMENT", "LINE_COMMENT"
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
			setState(121);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==IMPORT) {
				{
				{
				setState(118);
				import_();
				}
				}
				setState(123);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(127);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 8607236098L) != 0) || _la==CHAR_AT) {
				{
				{
				setState(124);
				item();
				}
				}
				setState(129);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(130);
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
		public TerminalNode JAVA_IMPORT() { return getToken(KarinaParser.JAVA_IMPORT, 0); }
		public TerminalNode CHAR_STAR() { return getToken(KarinaParser.CHAR_STAR, 0); }
		public TerminalNode ID() { return getToken(KarinaParser.ID, 0); }
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
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(132);
			match(IMPORT);
			setState(134);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				{
				setState(133);
				_la = _input.LA(1);
				if ( !(_la==CHAR_STAR || _la==ID) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				break;
			}
			setState(137);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==JAVA_IMPORT) {
				{
				setState(136);
				match(JAVA_IMPORT);
				}
			}

			setState(139);
			dotWordChain();
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
		enterRule(_localctx, 4, RULE_item);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(144);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==CHAR_AT) {
				{
				{
				setState(141);
				annotation();
				}
				}
				setState(146);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(151);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case FN:
				{
				setState(147);
				function();
				}
				break;
			case STRUCT:
				{
				setState(148);
				struct();
				}
				break;
			case ENUM:
				{
				setState(149);
				enum_();
				}
				break;
			case INTERFACE:
				{
				setState(150);
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
		public TerminalNode ID() { return getToken(KarinaParser.ID, 0); }
		public TerminalNode CHAR_L_PAREN() { return getToken(KarinaParser.CHAR_L_PAREN, 0); }
		public ParameterListContext parameterList() {
			return getRuleContext(ParameterListContext.class,0);
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
		enterRule(_localctx, 6, RULE_function);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(153);
			match(FN);
			setState(154);
			match(ID);
			setState(156);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CHAR_SMALLER) {
				{
				setState(155);
				genericHintDefinition();
				}
			}

			setState(158);
			match(CHAR_L_PAREN);
			setState(159);
			parameterList();
			setState(160);
			match(CHAR_R_PAREN);
			setState(163);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ARROW_RIGHT) {
				{
				setState(161);
				match(ARROW_RIGHT);
				setState(162);
				type();
				}
			}

			setState(168);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CHAR_EQUAL:
				{
				setState(165);
				match(CHAR_EQUAL);
				setState(166);
				expression();
				}
				break;
			case CHAR_L_BRACE:
				{
				setState(167);
				block();
				}
				break;
			case EOF:
			case FN:
			case STRUCT:
			case IMPL:
			case ENUM:
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
		public TerminalNode ID() { return getToken(KarinaParser.ID, 0); }
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
		enterRule(_localctx, 8, RULE_struct);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(170);
			match(STRUCT);
			setState(171);
			match(ID);
			setState(173);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CHAR_SMALLER) {
				{
				setState(172);
				genericHintDefinition();
				}
			}

			setState(175);
			match(CHAR_L_BRACE);
			setState(179);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==ID) {
				{
				{
				setState(176);
				field();
				}
				}
				setState(181);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(185);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FN) {
				{
				{
				setState(182);
				function();
				}
				}
				setState(187);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(191);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==IMPL) {
				{
				{
				setState(188);
				implementation();
				}
				}
				setState(193);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(194);
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
		enterRule(_localctx, 10, RULE_implementation);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(196);
			match(IMPL);
			setState(197);
			structType();
			setState(198);
			match(CHAR_L_BRACE);
			setState(202);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FN) {
				{
				{
				setState(199);
				function();
				}
				}
				setState(204);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(205);
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
	public static class FieldContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(KarinaParser.ID, 0); }
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
		enterRule(_localctx, 12, RULE_field);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(207);
			match(ID);
			setState(208);
			match(CHAR_COLON);
			setState(209);
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
		public TerminalNode ID() { return getToken(KarinaParser.ID, 0); }
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
		enterRule(_localctx, 14, RULE_enum);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(211);
			match(ENUM);
			setState(212);
			match(ID);
			setState(214);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CHAR_SMALLER) {
				{
				setState(213);
				genericHintDefinition();
				}
			}

			setState(216);
			match(CHAR_L_BRACE);
			setState(220);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==ID) {
				{
				{
				setState(217);
				enumMember();
				}
				}
				setState(222);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(223);
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
		public TerminalNode ID() { return getToken(KarinaParser.ID, 0); }
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
		enterRule(_localctx, 16, RULE_enumMember);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(225);
			match(ID);
			setState(230);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CHAR_L_PAREN) {
				{
				setState(226);
				match(CHAR_L_PAREN);
				setState(227);
				parameterList();
				setState(228);
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
		public TerminalNode ID() { return getToken(KarinaParser.ID, 0); }
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
		enterRule(_localctx, 18, RULE_interface);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(232);
			match(INTERFACE);
			setState(233);
			match(ID);
			setState(235);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CHAR_SMALLER) {
				{
				setState(234);
				genericHintDefinition();
				}
			}

			setState(237);
			match(CHAR_L_BRACE);
			setState(241);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FN) {
				{
				{
				setState(238);
				function();
				}
				}
				setState(243);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(247);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==IMPL) {
				{
				{
				setState(244);
				interfaceExtension();
				}
				}
				setState(249);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(250);
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
		enterRule(_localctx, 20, RULE_interfaceExtension);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(252);
			match(IMPL);
			setState(253);
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
		enterRule(_localctx, 22, RULE_parameterList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(263);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ID) {
				{
				setState(255);
				parameter();
				setState(260);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==CHAR_COMMA) {
					{
					{
					setState(256);
					match(CHAR_COMMA);
					setState(257);
					parameter();
					}
					}
					setState(262);
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
		public TerminalNode ID() { return getToken(KarinaParser.ID, 0); }
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
		enterRule(_localctx, 24, RULE_parameter);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(265);
			match(ID);
			setState(266);
			match(CHAR_COLON);
			setState(267);
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
		enterRule(_localctx, 26, RULE_type);
		try {
			setState(287);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case VOID:
				enterOuterAlt(_localctx, 1);
				{
				setState(269);
				match(VOID);
				}
				break;
			case INT:
				enterOuterAlt(_localctx, 2);
				{
				setState(270);
				match(INT);
				}
				break;
			case DOUBLE:
				enterOuterAlt(_localctx, 3);
				{
				setState(271);
				match(DOUBLE);
				}
				break;
			case SHORT:
				enterOuterAlt(_localctx, 4);
				{
				setState(272);
				match(SHORT);
				}
				break;
			case BYTE:
				enterOuterAlt(_localctx, 5);
				{
				setState(273);
				match(BYTE);
				}
				break;
			case CHAR:
				enterOuterAlt(_localctx, 6);
				{
				setState(274);
				match(CHAR);
				}
				break;
			case LONG:
				enterOuterAlt(_localctx, 7);
				{
				setState(275);
				match(LONG);
				}
				break;
			case FLOAT:
				enterOuterAlt(_localctx, 8);
				{
				setState(276);
				match(FLOAT);
				}
				break;
			case BOOL:
				enterOuterAlt(_localctx, 9);
				{
				setState(277);
				match(BOOL);
				}
				break;
			case STRING:
				enterOuterAlt(_localctx, 10);
				{
				setState(278);
				match(STRING);
				}
				break;
			case CHAR_QUESTION:
				enterOuterAlt(_localctx, 11);
				{
				setState(279);
				match(CHAR_QUESTION);
				}
				break;
			case ID:
				enterOuterAlt(_localctx, 12);
				{
				setState(280);
				structType();
				}
				break;
			case CHAR_L_BRACKET:
				enterOuterAlt(_localctx, 13);
				{
				setState(281);
				arrayType();
				}
				break;
			case FN:
				enterOuterAlt(_localctx, 14);
				{
				setState(282);
				functionType();
				}
				break;
			case CHAR_L_PAREN:
				enterOuterAlt(_localctx, 15);
				{
				setState(283);
				match(CHAR_L_PAREN);
				setState(284);
				type();
				setState(285);
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
		enterRule(_localctx, 28, RULE_structType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(289);
			dotWordChain();
			setState(291);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
			case 1:
				{
				setState(290);
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
		enterRule(_localctx, 30, RULE_arrayType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(293);
			match(CHAR_L_BRACKET);
			setState(294);
			type();
			setState(295);
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
		enterRule(_localctx, 32, RULE_functionType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(297);
			match(FN);
			setState(298);
			match(CHAR_L_PAREN);
			setState(299);
			typeList();
			setState(300);
			match(CHAR_R_PAREN);
			setState(303);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ARROW_RIGHT) {
				{
				setState(301);
				match(ARROW_RIGHT);
				setState(302);
				type();
				}
			}

			setState(306);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,25,_ctx) ) {
			case 1:
				{
				setState(305);
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
		enterRule(_localctx, 34, RULE_typeList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(316);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 35150012350466L) != 0) || ((((_la - 69)) & ~0x3f) == 0 && ((1L << (_la - 69)) & 133137L) != 0)) {
				{
				setState(308);
				type();
				setState(313);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==CHAR_COMMA) {
					{
					{
					setState(309);
					match(CHAR_COMMA);
					setState(310);
					type();
					}
					}
					setState(315);
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
		enterRule(_localctx, 36, RULE_genericHint);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(318);
			match(CHAR_SMALLER);
			setState(327);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 35150012350466L) != 0) || ((((_la - 69)) & ~0x3f) == 0 && ((1L << (_la - 69)) & 133137L) != 0)) {
				{
				setState(319);
				type();
				setState(324);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==CHAR_COMMA) {
					{
					{
					setState(320);
					match(CHAR_COMMA);
					setState(321);
					type();
					}
					}
					setState(326);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(329);
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
		public List<TerminalNode> ID() { return getTokens(KarinaParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(KarinaParser.ID, i);
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
		enterRule(_localctx, 38, RULE_genericHintDefinition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(331);
			match(CHAR_SMALLER);
			setState(340);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ID) {
				{
				setState(332);
				match(ID);
				setState(337);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==CHAR_COMMA) {
					{
					{
					setState(333);
					match(CHAR_COMMA);
					setState(334);
					match(ID);
					}
					}
					setState(339);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(342);
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
		public List<TerminalNode> ID() { return getTokens(KarinaParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(KarinaParser.ID, i);
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
		enterRule(_localctx, 40, RULE_dotWordChain);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(344);
			match(ID);
			setState(349);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,32,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(345);
					match(CHAR_DOT);
					setState(346);
					match(ID);
					}
					} 
				}
				setState(351);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,32,_ctx);
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
		public TerminalNode ID() { return getToken(KarinaParser.ID, 0); }
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
		enterRule(_localctx, 42, RULE_annotation);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(352);
			match(CHAR_AT);
			setState(353);
			match(ID);
			setState(356);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CHAR_EQUAL) {
				{
				setState(354);
				match(CHAR_EQUAL);
				setState(355);
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
		enterRule(_localctx, 44, RULE_jsonObj);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(358);
			match(CHAR_L_BRACE);
			setState(367);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==STRING_LITERAL || _la==ID) {
				{
				setState(359);
				jsonPair();
				setState(364);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==CHAR_COMMA) {
					{
					{
					setState(360);
					match(CHAR_COMMA);
					setState(361);
					jsonPair();
					}
					}
					setState(366);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(369);
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
		public TerminalNode ID() { return getToken(KarinaParser.ID, 0); }
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
		enterRule(_localctx, 46, RULE_jsonPair);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(371);
			_la = _input.LA(1);
			if ( !(_la==STRING_LITERAL || _la==ID) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(372);
			match(CHAR_COLON);
			setState(373);
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
		enterRule(_localctx, 48, RULE_jsonArray);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(375);
			match(CHAR_L_BRACKET);
			setState(384);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 24640L) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & 6149L) != 0)) {
				{
				setState(376);
				jsonValue();
				setState(381);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==CHAR_COMMA) {
					{
					{
					setState(377);
					match(CHAR_COMMA);
					setState(378);
					jsonValue();
					}
					}
					setState(383);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(386);
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
		enterRule(_localctx, 50, RULE_jsonValue);
		try {
			setState(395);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STRING_LITERAL:
				enterOuterAlt(_localctx, 1);
				{
				setState(388);
				match(STRING_LITERAL);
				}
				break;
			case NUMBER:
				enterOuterAlt(_localctx, 2);
				{
				setState(389);
				match(NUMBER);
				}
				break;
			case CHAR_L_BRACE:
				enterOuterAlt(_localctx, 3);
				{
				setState(390);
				jsonObj();
				}
				break;
			case CHAR_L_BRACKET:
				enterOuterAlt(_localctx, 4);
				{
				setState(391);
				jsonArray();
				}
				break;
			case TRUE:
				enterOuterAlt(_localctx, 5);
				{
				setState(392);
				match(TRUE);
				}
				break;
			case FALSE:
				enterOuterAlt(_localctx, 6);
				{
				setState(393);
				match(FALSE);
				}
				break;
			case NULL:
				enterOuterAlt(_localctx, 7);
				{
				setState(394);
				match(NULL);
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
		enterRule(_localctx, 52, RULE_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(397);
			match(CHAR_L_BRACE);
			setState(404);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 144185577424774146L) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & 2490643L) != 0)) {
				{
				{
				setState(398);
				expression();
				setState(400);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==CHAR_SEMICOLON) {
					{
					setState(399);
					match(CHAR_SEMICOLON);
					}
				}

				}
				}
				setState(406);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(407);
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
		enterRule(_localctx, 54, RULE_exprWithBlock);
		try {
			setState(411);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CHAR_L_BRACE:
				enterOuterAlt(_localctx, 1);
				{
				setState(409);
				block();
				}
				break;
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
			case SELF:
			case CONTINUE:
			case CHAR_MINUS:
			case CHAR_SMALLER:
			case CHAR_EXCLAMATION:
			case CHAR_L_PAREN:
			case CHAR_L_BRACKET:
			case STRING_LITERAL:
			case NUMBER:
			case ID:
				enterOuterAlt(_localctx, 2);
				{
				setState(410);
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
		enterRule(_localctx, 56, RULE_expression);
		try {
			setState(427);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LET:
				enterOuterAlt(_localctx, 1);
				{
				setState(413);
				varDef();
				}
				break;
			case FN:
				enterOuterAlt(_localctx, 2);
				{
				setState(414);
				closure();
				}
				break;
			case RETURN:
				enterOuterAlt(_localctx, 3);
				{
				setState(415);
				match(RETURN);
				setState(417);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,42,_ctx) ) {
				case 1:
					{
					setState(416);
					exprWithBlock();
					}
					break;
				}
				}
				break;
			case MATCH:
				enterOuterAlt(_localctx, 4);
				{
				setState(419);
				match();
				}
				break;
			case IF:
				enterOuterAlt(_localctx, 5);
				{
				setState(420);
				if_();
				}
				break;
			case WHILE:
				enterOuterAlt(_localctx, 6);
				{
				setState(421);
				while_();
				}
				break;
			case FOR:
				enterOuterAlt(_localctx, 7);
				{
				setState(422);
				for_();
				}
				break;
			case TRUE:
			case FALSE:
			case SELF:
			case CHAR_MINUS:
			case CHAR_SMALLER:
			case CHAR_EXCLAMATION:
			case CHAR_L_PAREN:
			case CHAR_L_BRACKET:
			case STRING_LITERAL:
			case NUMBER:
			case ID:
				enterOuterAlt(_localctx, 8);
				{
				setState(423);
				conditionalOrExpression();
				}
				break;
			case BREAK:
				enterOuterAlt(_localctx, 9);
				{
				setState(424);
				match(BREAK);
				}
				break;
			case CONTINUE:
				enterOuterAlt(_localctx, 10);
				{
				setState(425);
				match(CONTINUE);
				}
				break;
			case RAISE:
				enterOuterAlt(_localctx, 11);
				{
				setState(426);
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
		public TerminalNode ID() { return getToken(KarinaParser.ID, 0); }
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
		enterRule(_localctx, 58, RULE_varDef);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(429);
			match(LET);
			setState(430);
			match(ID);
			setState(433);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CHAR_COLON) {
				{
				setState(431);
				match(CHAR_COLON);
				setState(432);
				type();
				}
			}

			setState(435);
			match(CHAR_EQUAL);
			{
			setState(436);
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
		enterRule(_localctx, 60, RULE_closure);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(438);
			match(FN);
			setState(439);
			match(CHAR_L_PAREN);
			setState(440);
			optTypeList();
			setState(441);
			match(CHAR_R_PAREN);
			setState(444);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ARROW_RIGHT) {
				{
				setState(442);
				match(ARROW_RIGHT);
				setState(443);
				type();
				}
			}

			setState(447);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IMPL) {
				{
				setState(446);
				interfaceImpl();
				}
			}

			setState(449);
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
		enterRule(_localctx, 62, RULE_interfaceImpl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(451);
			match(IMPL);
			setState(452);
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
		enterRule(_localctx, 64, RULE_structTypeList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(454);
			structType();
			setState(459);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,47,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(455);
					match(CHAR_COMMA);
					setState(456);
					structType();
					}
					} 
				}
				setState(461);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,47,_ctx);
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
		enterRule(_localctx, 66, RULE_match);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(462);
			match(MATCH);
			setState(463);
			exprWithBlock();
			setState(464);
			match(CHAR_L_BRACE);
			setState(468);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==CHAR_UNDER || _la==ID) {
				{
				{
				setState(465);
				matchCase();
				}
				}
				setState(470);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(471);
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
		enterRule(_localctx, 68, RULE_matchCase);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(475);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CHAR_UNDER:
				{
				setState(473);
				matchDefault();
				}
				break;
			case ID:
				{
				setState(474);
				matchInstance();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(477);
			match(ARROW_RIGHT);
			setState(478);
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
		public TerminalNode ID() { return getToken(KarinaParser.ID, 0); }
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
		enterRule(_localctx, 70, RULE_matchInstance);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(480);
			structType();
			setState(486);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ID:
				{
				setState(481);
				match(ID);
				}
				break;
			case CHAR_L_PAREN:
				{
				setState(482);
				match(CHAR_L_PAREN);
				setState(483);
				optTypeList();
				setState(484);
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
		enterRule(_localctx, 72, RULE_matchDefault);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(488);
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
		public TerminalNode ID() { return getToken(KarinaParser.ID, 0); }
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
		enterRule(_localctx, 74, RULE_if);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(490);
			match(IF);
			setState(491);
			exprWithBlock();
			setState(497);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ID:
				{
				setState(492);
				match(ID);
				}
				break;
			case CHAR_L_PAREN:
				{
				setState(493);
				match(CHAR_L_PAREN);
				setState(494);
				optTypeList();
				setState(495);
				match(CHAR_R_PAREN);
				}
				break;
			case CHAR_L_BRACE:
				break;
			default:
				break;
			}
			setState(499);
			block();
			setState(501);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ELSE) {
				{
				setState(500);
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
		enterRule(_localctx, 76, RULE_elseExpr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(503);
			match(ELSE);
			setState(507);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IF:
				{
				setState(504);
				if_();
				}
				break;
			case CHAR_L_BRACE:
				{
				setState(505);
				block();
				}
				break;
			case MATCH:
				{
				setState(506);
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
		enterRule(_localctx, 78, RULE_while);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(509);
			match(WHILE);
			setState(510);
			exprWithBlock();
			setState(511);
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
		public TerminalNode ID() { return getToken(KarinaParser.ID, 0); }
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
		enterRule(_localctx, 80, RULE_for);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(513);
			match(FOR);
			setState(514);
			match(ID);
			setState(515);
			match(IN);
			setState(516);
			exprWithBlock();
			setState(517);
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
		enterRule(_localctx, 82, RULE_throw);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(519);
			match(RAISE);
			setState(520);
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
		enterRule(_localctx, 84, RULE_conditionalOrExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(522);
			conditionalAndExpression();
			setState(525);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,54,_ctx) ) {
			case 1:
				{
				setState(523);
				match(OR_OR);
				setState(524);
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
		enterRule(_localctx, 86, RULE_conditionalAndExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(527);
			equalityExpression();
			setState(530);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,55,_ctx) ) {
			case 1:
				{
				setState(528);
				match(AND_AND);
				setState(529);
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
		enterRule(_localctx, 88, RULE_equalityExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(532);
			relationalExpression();
			setState(535);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,56,_ctx) ) {
			case 1:
				{
				setState(533);
				_la = _input.LA(1);
				if ( !(_la==EQUALS || _la==NOT_EQUALS) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(534);
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
		enterRule(_localctx, 90, RULE_relationalExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(537);
			additiveExpression();
			setState(540);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,57,_ctx) ) {
			case 1:
				{
				setState(538);
				_la = _input.LA(1);
				if ( !(((((_la - 50)) & ~0x3f) == 0 && ((1L << (_la - 50)) & 49155L) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(539);
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
		enterRule(_localctx, 92, RULE_additiveExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(542);
			multiplicativeExpression();
			setState(545);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,58,_ctx) ) {
			case 1:
				{
				setState(543);
				_la = _input.LA(1);
				if ( !(((((_la - 56)) & ~0x3f) == 0 && ((1L << (_la - 56)) & 4194307L) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(544);
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
		enterRule(_localctx, 94, RULE_multiplicativeExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(547);
			unaryExpression();
			setState(550);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,59,_ctx) ) {
			case 1:
				{
				setState(548);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 2017612633061982208L) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(549);
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
		enterRule(_localctx, 96, RULE_unaryExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(553);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CHAR_MINUS || _la==CHAR_EXCLAMATION) {
				{
				setState(552);
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

			setState(555);
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
		enterRule(_localctx, 98, RULE_factor);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(557);
			object();
			setState(561);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,61,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(558);
					postFix();
					}
					} 
				}
				setState(563);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,61,_ctx);
			}
			setState(567);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CHAR_EQUAL:
				{
				{
				setState(564);
				match(CHAR_EQUAL);
				setState(565);
				exprWithBlock();
				}
				}
				break;
			case IS:
				{
				setState(566);
				isInstanceOf();
				}
				break;
			case EOF:
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
			case INTERFACE:
			case SELF:
			case CONTINUE:
			case GREATER_EQULAS:
			case SMALLER_EQUALS:
			case EQUALS:
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
			case STRING_LITERAL:
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
		public TerminalNode ID() { return getToken(KarinaParser.ID, 0); }
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
		enterRule(_localctx, 100, RULE_postFix);
		int _la;
		try {
			setState(586);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,64,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(569);
				match(CHAR_DOT);
				setState(570);
				match(ID);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(571);
				match(CHAR_DOT);
				setState(572);
				match(CLASS);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(574);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==CHAR_SMALLER) {
					{
					setState(573);
					genericHint();
					}
				}

				setState(576);
				match(CHAR_L_PAREN);
				setState(577);
				expressionList();
				setState(578);
				match(CHAR_R_PAREN);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(580);
				match(CHAR_L_BRACKET);
				setState(581);
				exprWithBlock();
				setState(582);
				match(CHAR_R_BRACKET);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(584);
				match(AS);
				setState(585);
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
		public TerminalNode ID() { return getToken(KarinaParser.ID, 0); }
		public TerminalNode CHAR_L_BRACE() { return getToken(KarinaParser.CHAR_L_BRACE, 0); }
		public InitListContext initList() {
			return getRuleContext(InitListContext.class,0);
		}
		public TerminalNode CHAR_R_BRACE() { return getToken(KarinaParser.CHAR_R_BRACE, 0); }
		public GenericHintContext genericHint() {
			return getRuleContext(GenericHintContext.class,0);
		}
		public TerminalNode STRING_LITERAL() { return getToken(KarinaParser.STRING_LITERAL, 0); }
		public TerminalNode SELF() { return getToken(KarinaParser.SELF, 0); }
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
		enterRule(_localctx, 102, RULE_object);
		int _la;
		try {
			setState(608);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CHAR_SMALLER:
			case CHAR_L_BRACKET:
				enterOuterAlt(_localctx, 1);
				{
				setState(588);
				array();
				}
				break;
			case CHAR_L_PAREN:
				enterOuterAlt(_localctx, 2);
				{
				setState(589);
				match(CHAR_L_PAREN);
				setState(590);
				exprWithBlock();
				setState(591);
				match(CHAR_R_PAREN);
				}
				break;
			case NUMBER:
				enterOuterAlt(_localctx, 3);
				{
				setState(593);
				match(NUMBER);
				}
				break;
			case ID:
				enterOuterAlt(_localctx, 4);
				{
				setState(594);
				match(ID);
				setState(602);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,66,_ctx) ) {
				case 1:
					{
					setState(596);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==CHAR_SMALLER) {
						{
						setState(595);
						genericHint();
						}
					}

					setState(598);
					match(CHAR_L_BRACE);
					setState(599);
					initList();
					setState(600);
					match(CHAR_R_BRACE);
					}
					break;
				}
				}
				break;
			case STRING_LITERAL:
				enterOuterAlt(_localctx, 5);
				{
				setState(604);
				match(STRING_LITERAL);
				}
				break;
			case SELF:
				enterOuterAlt(_localctx, 6);
				{
				setState(605);
				match(SELF);
				}
				break;
			case TRUE:
				enterOuterAlt(_localctx, 7);
				{
				setState(606);
				match(TRUE);
				}
				break;
			case FALSE:
				enterOuterAlt(_localctx, 8);
				{
				setState(607);
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
		enterRule(_localctx, 104, RULE_array);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(614);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CHAR_SMALLER) {
				{
				setState(610);
				match(CHAR_SMALLER);
				setState(611);
				type();
				setState(612);
				match(CHAR_GREATER);
				}
			}

			setState(616);
			match(CHAR_L_BRACKET);
			setState(617);
			expressionList();
			setState(618);
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
		enterRule(_localctx, 106, RULE_expressionList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(628);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 144185577424774146L) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & 2490707L) != 0)) {
				{
				setState(620);
				exprWithBlock();
				setState(625);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==CHAR_COMMA) {
					{
					{
					setState(621);
					match(CHAR_COMMA);
					setState(622);
					exprWithBlock();
					}
					}
					setState(627);
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
		enterRule(_localctx, 108, RULE_initList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(638);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ID) {
				{
				setState(630);
				memberInit();
				setState(635);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==CHAR_COMMA) {
					{
					{
					setState(631);
					match(CHAR_COMMA);
					setState(632);
					memberInit();
					}
					}
					setState(637);
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
		public TerminalNode ID() { return getToken(KarinaParser.ID, 0); }
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
		enterRule(_localctx, 110, RULE_memberInit);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(640);
			match(ID);
			setState(641);
			match(CHAR_COLON);
			setState(642);
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
		enterRule(_localctx, 112, RULE_isInstanceOf);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(644);
			match(IS);
			setState(645);
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
		enterRule(_localctx, 114, RULE_optTypeList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(655);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ID) {
				{
				setState(647);
				optTypeName();
				setState(652);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==CHAR_COMMA) {
					{
					{
					setState(648);
					match(CHAR_COMMA);
					setState(649);
					optTypeName();
					}
					}
					setState(654);
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
		public TerminalNode ID() { return getToken(KarinaParser.ID, 0); }
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
		enterRule(_localctx, 116, RULE_optTypeName);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(657);
			match(ID);
			setState(660);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CHAR_COLON) {
				{
				setState(658);
				match(CHAR_COLON);
				setState(659);
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

	public static final String _serializedATN =
		"\u0004\u0001Y\u0297\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
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
		"7\u00077\u00028\u00078\u00029\u00079\u0002:\u0007:\u0001\u0000\u0005\u0000"+
		"x\b\u0000\n\u0000\f\u0000{\t\u0000\u0001\u0000\u0005\u0000~\b\u0000\n"+
		"\u0000\f\u0000\u0081\t\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001"+
		"\u0001\u0003\u0001\u0087\b\u0001\u0001\u0001\u0003\u0001\u008a\b\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0002\u0005\u0002\u008f\b\u0002\n\u0002"+
		"\f\u0002\u0092\t\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0003\u0002\u0098\b\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0003\u0003"+
		"\u009d\b\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0003\u0003\u00a4\b\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0003\u0003"+
		"\u00a9\b\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004\u00ae\b"+
		"\u0004\u0001\u0004\u0001\u0004\u0005\u0004\u00b2\b\u0004\n\u0004\f\u0004"+
		"\u00b5\t\u0004\u0001\u0004\u0005\u0004\u00b8\b\u0004\n\u0004\f\u0004\u00bb"+
		"\t\u0004\u0001\u0004\u0005\u0004\u00be\b\u0004\n\u0004\f\u0004\u00c1\t"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0005\u0005\u00c9\b\u0005\n\u0005\f\u0005\u00cc\t\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0003\u0007\u00d7\b\u0007\u0001\u0007\u0001\u0007"+
		"\u0005\u0007\u00db\b\u0007\n\u0007\f\u0007\u00de\t\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0003\b\u00e7\b\b\u0001"+
		"\t\u0001\t\u0001\t\u0003\t\u00ec\b\t\u0001\t\u0001\t\u0005\t\u00f0\b\t"+
		"\n\t\f\t\u00f3\t\t\u0001\t\u0005\t\u00f6\b\t\n\t\f\t\u00f9\t\t\u0001\t"+
		"\u0001\t\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b\u0005"+
		"\u000b\u0103\b\u000b\n\u000b\f\u000b\u0106\t\u000b\u0003\u000b\u0108\b"+
		"\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\r\u0001\r\u0001\r\u0001\r"+
		"\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001"+
		"\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0003\r\u0120\b\r\u0001\u000e"+
		"\u0001\u000e\u0003\u000e\u0124\b\u000e\u0001\u000f\u0001\u000f\u0001\u000f"+
		"\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0001\u0010\u0003\u0010\u0130\b\u0010\u0001\u0010\u0003\u0010\u0133\b"+
		"\u0010\u0001\u0011\u0001\u0011\u0001\u0011\u0005\u0011\u0138\b\u0011\n"+
		"\u0011\f\u0011\u013b\t\u0011\u0003\u0011\u013d\b\u0011\u0001\u0012\u0001"+
		"\u0012\u0001\u0012\u0001\u0012\u0005\u0012\u0143\b\u0012\n\u0012\f\u0012"+
		"\u0146\t\u0012\u0003\u0012\u0148\b\u0012\u0001\u0012\u0001\u0012\u0001"+
		"\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0005\u0013\u0150\b\u0013\n"+
		"\u0013\f\u0013\u0153\t\u0013\u0003\u0013\u0155\b\u0013\u0001\u0013\u0001"+
		"\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0005\u0014\u015c\b\u0014\n"+
		"\u0014\f\u0014\u015f\t\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0001"+
		"\u0015\u0003\u0015\u0165\b\u0015\u0001\u0016\u0001\u0016\u0001\u0016\u0001"+
		"\u0016\u0005\u0016\u016b\b\u0016\n\u0016\f\u0016\u016e\t\u0016\u0003\u0016"+
		"\u0170\b\u0016\u0001\u0016\u0001\u0016\u0001\u0017\u0001\u0017\u0001\u0017"+
		"\u0001\u0017\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0005\u0018"+
		"\u017c\b\u0018\n\u0018\f\u0018\u017f\t\u0018\u0003\u0018\u0181\b\u0018"+
		"\u0001\u0018\u0001\u0018\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019"+
		"\u0001\u0019\u0001\u0019\u0001\u0019\u0003\u0019\u018c\b\u0019\u0001\u001a"+
		"\u0001\u001a\u0001\u001a\u0003\u001a\u0191\b\u001a\u0005\u001a\u0193\b"+
		"\u001a\n\u001a\f\u001a\u0196\t\u001a\u0001\u001a\u0001\u001a\u0001\u001b"+
		"\u0001\u001b\u0003\u001b\u019c\b\u001b\u0001\u001c\u0001\u001c\u0001\u001c"+
		"\u0001\u001c\u0003\u001c\u01a2\b\u001c\u0001\u001c\u0001\u001c\u0001\u001c"+
		"\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0003\u001c"+
		"\u01ac\b\u001c\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0003\u001d"+
		"\u01b2\b\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001e\u0001\u001e"+
		"\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0003\u001e\u01bd\b\u001e"+
		"\u0001\u001e\u0003\u001e\u01c0\b\u001e\u0001\u001e\u0001\u001e\u0001\u001f"+
		"\u0001\u001f\u0001\u001f\u0001 \u0001 \u0001 \u0005 \u01ca\b \n \f \u01cd"+
		"\t \u0001!\u0001!\u0001!\u0001!\u0005!\u01d3\b!\n!\f!\u01d6\t!\u0001!"+
		"\u0001!\u0001\"\u0001\"\u0003\"\u01dc\b\"\u0001\"\u0001\"\u0001\"\u0001"+
		"#\u0001#\u0001#\u0001#\u0001#\u0001#\u0003#\u01e7\b#\u0001$\u0001$\u0001"+
		"%\u0001%\u0001%\u0001%\u0001%\u0001%\u0001%\u0003%\u01f2\b%\u0001%\u0001"+
		"%\u0003%\u01f6\b%\u0001&\u0001&\u0001&\u0001&\u0003&\u01fc\b&\u0001\'"+
		"\u0001\'\u0001\'\u0001\'\u0001(\u0001(\u0001(\u0001(\u0001(\u0001(\u0001"+
		")\u0001)\u0001)\u0001*\u0001*\u0001*\u0003*\u020e\b*\u0001+\u0001+\u0001"+
		"+\u0003+\u0213\b+\u0001,\u0001,\u0001,\u0003,\u0218\b,\u0001-\u0001-\u0001"+
		"-\u0003-\u021d\b-\u0001.\u0001.\u0001.\u0003.\u0222\b.\u0001/\u0001/\u0001"+
		"/\u0003/\u0227\b/\u00010\u00030\u022a\b0\u00010\u00010\u00011\u00011\u0005"+
		"1\u0230\b1\n1\f1\u0233\t1\u00011\u00011\u00011\u00031\u0238\b1\u00012"+
		"\u00012\u00012\u00012\u00012\u00032\u023f\b2\u00012\u00012\u00012\u0001"+
		"2\u00012\u00012\u00012\u00012\u00012\u00012\u00032\u024b\b2\u00013\u0001"+
		"3\u00013\u00013\u00013\u00013\u00013\u00013\u00033\u0255\b3\u00013\u0001"+
		"3\u00013\u00013\u00033\u025b\b3\u00013\u00013\u00013\u00013\u00033\u0261"+
		"\b3\u00014\u00014\u00014\u00014\u00034\u0267\b4\u00014\u00014\u00014\u0001"+
		"4\u00015\u00015\u00015\u00055\u0270\b5\n5\f5\u0273\t5\u00035\u0275\b5"+
		"\u00016\u00016\u00016\u00056\u027a\b6\n6\f6\u027d\t6\u00036\u027f\b6\u0001"+
		"7\u00017\u00017\u00017\u00018\u00018\u00018\u00019\u00019\u00019\u0005"+
		"9\u028b\b9\n9\f9\u028e\t9\u00039\u0290\b9\u0001:\u0001:\u0001:\u0003:"+
		"\u0295\b:\u0001:\u0000\u0000;\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010"+
		"\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.02468:<>@BDFHJLNPR"+
		"TVXZ\\^`bdfhjlnprt\u0000\u0007\u0002\u0000::VV\u0002\u0000RRVV\u0001\u0000"+
		"45\u0002\u000023@A\u0002\u000089NN\u0001\u0000:<\u0002\u000099BB\u02d1"+
		"\u0000y\u0001\u0000\u0000\u0000\u0002\u0084\u0001\u0000\u0000\u0000\u0004"+
		"\u0090\u0001\u0000\u0000\u0000\u0006\u0099\u0001\u0000\u0000\u0000\b\u00aa"+
		"\u0001\u0000\u0000\u0000\n\u00c4\u0001\u0000\u0000\u0000\f\u00cf\u0001"+
		"\u0000\u0000\u0000\u000e\u00d3\u0001\u0000\u0000\u0000\u0010\u00e1\u0001"+
		"\u0000\u0000\u0000\u0012\u00e8\u0001\u0000\u0000\u0000\u0014\u00fc\u0001"+
		"\u0000\u0000\u0000\u0016\u0107\u0001\u0000\u0000\u0000\u0018\u0109\u0001"+
		"\u0000\u0000\u0000\u001a\u011f\u0001\u0000\u0000\u0000\u001c\u0121\u0001"+
		"\u0000\u0000\u0000\u001e\u0125\u0001\u0000\u0000\u0000 \u0129\u0001\u0000"+
		"\u0000\u0000\"\u013c\u0001\u0000\u0000\u0000$\u013e\u0001\u0000\u0000"+
		"\u0000&\u014b\u0001\u0000\u0000\u0000(\u0158\u0001\u0000\u0000\u0000*"+
		"\u0160\u0001\u0000\u0000\u0000,\u0166\u0001\u0000\u0000\u0000.\u0173\u0001"+
		"\u0000\u0000\u00000\u0177\u0001\u0000\u0000\u00002\u018b\u0001\u0000\u0000"+
		"\u00004\u018d\u0001\u0000\u0000\u00006\u019b\u0001\u0000\u0000\u00008"+
		"\u01ab\u0001\u0000\u0000\u0000:\u01ad\u0001\u0000\u0000\u0000<\u01b6\u0001"+
		"\u0000\u0000\u0000>\u01c3\u0001\u0000\u0000\u0000@\u01c6\u0001\u0000\u0000"+
		"\u0000B\u01ce\u0001\u0000\u0000\u0000D\u01db\u0001\u0000\u0000\u0000F"+
		"\u01e0\u0001\u0000\u0000\u0000H\u01e8\u0001\u0000\u0000\u0000J\u01ea\u0001"+
		"\u0000\u0000\u0000L\u01f7\u0001\u0000\u0000\u0000N\u01fd\u0001\u0000\u0000"+
		"\u0000P\u0201\u0001\u0000\u0000\u0000R\u0207\u0001\u0000\u0000\u0000T"+
		"\u020a\u0001\u0000\u0000\u0000V\u020f\u0001\u0000\u0000\u0000X\u0214\u0001"+
		"\u0000\u0000\u0000Z\u0219\u0001\u0000\u0000\u0000\\\u021e\u0001\u0000"+
		"\u0000\u0000^\u0223\u0001\u0000\u0000\u0000`\u0229\u0001\u0000\u0000\u0000"+
		"b\u022d\u0001\u0000\u0000\u0000d\u024a\u0001\u0000\u0000\u0000f\u0260"+
		"\u0001\u0000\u0000\u0000h\u0266\u0001\u0000\u0000\u0000j\u0274\u0001\u0000"+
		"\u0000\u0000l\u027e\u0001\u0000\u0000\u0000n\u0280\u0001\u0000\u0000\u0000"+
		"p\u0284\u0001\u0000\u0000\u0000r\u028f\u0001\u0000\u0000\u0000t\u0291"+
		"\u0001\u0000\u0000\u0000vx\u0003\u0002\u0001\u0000wv\u0001\u0000\u0000"+
		"\u0000x{\u0001\u0000\u0000\u0000yw\u0001\u0000\u0000\u0000yz\u0001\u0000"+
		"\u0000\u0000z\u007f\u0001\u0000\u0000\u0000{y\u0001\u0000\u0000\u0000"+
		"|~\u0003\u0004\u0002\u0000}|\u0001\u0000\u0000\u0000~\u0081\u0001\u0000"+
		"\u0000\u0000\u007f}\u0001\u0000\u0000\u0000\u007f\u0080\u0001\u0000\u0000"+
		"\u0000\u0080\u0082\u0001\u0000\u0000\u0000\u0081\u007f\u0001\u0000\u0000"+
		"\u0000\u0082\u0083\u0005\u0000\u0000\u0001\u0083\u0001\u0001\u0000\u0000"+
		"\u0000\u0084\u0086\u0005\u0007\u0000\u0000\u0085\u0087\u0007\u0000\u0000"+
		"\u0000\u0086\u0085\u0001\u0000\u0000\u0000\u0086\u0087\u0001\u0000\u0000"+
		"\u0000\u0087\u0089\u0001\u0000\u0000\u0000\u0088\u008a\u0005/\u0000\u0000"+
		"\u0089\u0088\u0001\u0000\u0000\u0000\u0089\u008a\u0001\u0000\u0000\u0000"+
		"\u008a\u008b\u0001\u0000\u0000\u0000\u008b\u008c\u0003(\u0014\u0000\u008c"+
		"\u0003\u0001\u0000\u0000\u0000\u008d\u008f\u0003*\u0015\u0000\u008e\u008d"+
		"\u0001\u0000\u0000\u0000\u008f\u0092\u0001\u0000\u0000\u0000\u0090\u008e"+
		"\u0001\u0000\u0000\u0000\u0090\u0091\u0001\u0000\u0000\u0000\u0091\u0097"+
		"\u0001\u0000\u0000\u0000\u0092\u0090\u0001\u0000\u0000\u0000\u0093\u0098"+
		"\u0003\u0006\u0003\u0000\u0094\u0098\u0003\b\u0004\u0000\u0095\u0098\u0003"+
		"\u000e\u0007\u0000\u0096\u0098\u0003\u0012\t\u0000\u0097\u0093\u0001\u0000"+
		"\u0000\u0000\u0097\u0094\u0001\u0000\u0000\u0000\u0097\u0095\u0001\u0000"+
		"\u0000\u0000\u0097\u0096\u0001\u0000\u0000\u0000\u0098\u0005\u0001\u0000"+
		"\u0000\u0000\u0099\u009a\u0005\u0001\u0000\u0000\u009a\u009c\u0005V\u0000"+
		"\u0000\u009b\u009d\u0003&\u0013\u0000\u009c\u009b\u0001\u0000\u0000\u0000"+
		"\u009c\u009d\u0001\u0000\u0000\u0000\u009d\u009e\u0001\u0000\u0000\u0000"+
		"\u009e\u009f\u0005E\u0000\u0000\u009f\u00a0\u0003\u0016\u000b\u0000\u00a0"+
		"\u00a3\u0005F\u0000\u0000\u00a1\u00a2\u00050\u0000\u0000\u00a2\u00a4\u0003"+
		"\u001a\r\u0000\u00a3\u00a1\u0001\u0000\u0000\u0000\u00a3\u00a4\u0001\u0000"+
		"\u0000\u0000\u00a4\u00a8\u0001\u0000\u0000\u0000\u00a5\u00a6\u0005D\u0000"+
		"\u0000\u00a6\u00a9\u00038\u001c\u0000\u00a7\u00a9\u00034\u001a\u0000\u00a8"+
		"\u00a5\u0001\u0000\u0000\u0000\u00a8\u00a7\u0001\u0000\u0000\u0000\u00a8"+
		"\u00a9\u0001\u0000\u0000\u0000\u00a9\u0007\u0001\u0000\u0000\u0000\u00aa"+
		"\u00ab\u0005\u0013\u0000\u0000\u00ab\u00ad\u0005V\u0000\u0000\u00ac\u00ae"+
		"\u0003&\u0013\u0000\u00ad\u00ac\u0001\u0000\u0000\u0000\u00ad\u00ae\u0001"+
		"\u0000\u0000\u0000\u00ae\u00af\u0001\u0000\u0000\u0000\u00af\u00b3\u0005"+
		"G\u0000\u0000\u00b0\u00b2\u0003\f\u0006\u0000\u00b1\u00b0\u0001\u0000"+
		"\u0000\u0000\u00b2\u00b5\u0001\u0000\u0000\u0000\u00b3\u00b1\u0001\u0000"+
		"\u0000\u0000\u00b3\u00b4\u0001\u0000\u0000\u0000\u00b4\u00b9\u0001\u0000"+
		"\u0000\u0000\u00b5\u00b3\u0001\u0000\u0000\u0000\u00b6\u00b8\u0003\u0006"+
		"\u0003\u0000\u00b7\u00b6\u0001\u0000\u0000\u0000\u00b8\u00bb\u0001\u0000"+
		"\u0000\u0000\u00b9\u00b7\u0001\u0000\u0000\u0000\u00b9\u00ba\u0001\u0000"+
		"\u0000\u0000\u00ba\u00bf\u0001\u0000\u0000\u0000\u00bb\u00b9\u0001\u0000"+
		"\u0000\u0000\u00bc\u00be\u0003\n\u0005\u0000\u00bd\u00bc\u0001\u0000\u0000"+
		"\u0000\u00be\u00c1\u0001\u0000\u0000\u0000\u00bf\u00bd\u0001\u0000\u0000"+
		"\u0000\u00bf\u00c0\u0001\u0000\u0000\u0000\u00c0\u00c2\u0001\u0000\u0000"+
		"\u0000\u00c1\u00bf\u0001\u0000\u0000\u0000\u00c2\u00c3\u0005H\u0000\u0000"+
		"\u00c3\t\u0001\u0000\u0000\u0000\u00c4\u00c5\u0005\u0017\u0000\u0000\u00c5"+
		"\u00c6\u0003\u001c\u000e\u0000\u00c6\u00ca\u0005G\u0000\u0000\u00c7\u00c9"+
		"\u0003\u0006\u0003\u0000\u00c8\u00c7\u0001\u0000\u0000\u0000\u00c9\u00cc"+
		"\u0001\u0000\u0000\u0000\u00ca\u00c8\u0001\u0000\u0000\u0000\u00ca\u00cb"+
		"\u0001\u0000\u0000\u0000\u00cb\u00cd\u0001\u0000\u0000\u0000\u00cc\u00ca"+
		"\u0001\u0000\u0000\u0000\u00cd\u00ce\u0005H\u0000\u0000\u00ce\u000b\u0001"+
		"\u0000\u0000\u0000\u00cf\u00d0\u0005V\u0000\u0000\u00d0\u00d1\u0005C\u0000"+
		"\u0000\u00d1\u00d2\u0003\u001a\r\u0000\u00d2\r\u0001\u0000\u0000\u0000"+
		"\u00d3\u00d4\u0005\u0018\u0000\u0000\u00d4\u00d6\u0005V\u0000\u0000\u00d5"+
		"\u00d7\u0003&\u0013\u0000\u00d6\u00d5\u0001\u0000\u0000\u0000\u00d6\u00d7"+
		"\u0001\u0000\u0000\u0000\u00d7\u00d8\u0001\u0000\u0000\u0000\u00d8\u00dc"+
		"\u0005G\u0000\u0000\u00d9\u00db\u0003\u0010\b\u0000\u00da\u00d9\u0001"+
		"\u0000\u0000\u0000\u00db\u00de\u0001\u0000\u0000\u0000\u00dc\u00da\u0001"+
		"\u0000\u0000\u0000\u00dc\u00dd\u0001\u0000\u0000\u0000\u00dd\u00df\u0001"+
		"\u0000\u0000\u0000\u00de\u00dc\u0001\u0000\u0000\u0000\u00df\u00e0\u0005"+
		"H\u0000\u0000\u00e0\u000f\u0001\u0000\u0000\u0000\u00e1\u00e6\u0005V\u0000"+
		"\u0000\u00e2\u00e3\u0005E\u0000\u0000\u00e3\u00e4\u0003\u0016\u000b\u0000"+
		"\u00e4\u00e5\u0005F\u0000\u0000\u00e5\u00e7\u0001\u0000\u0000\u0000\u00e6"+
		"\u00e2\u0001\u0000\u0000\u0000\u00e6\u00e7\u0001\u0000\u0000\u0000\u00e7"+
		"\u0011\u0001\u0000\u0000\u0000\u00e8\u00e9\u0005!\u0000\u0000\u00e9\u00eb"+
		"\u0005V\u0000\u0000\u00ea\u00ec\u0003&\u0013\u0000\u00eb\u00ea\u0001\u0000"+
		"\u0000\u0000\u00eb\u00ec\u0001\u0000\u0000\u0000\u00ec\u00ed\u0001\u0000"+
		"\u0000\u0000\u00ed\u00f1\u0005G\u0000\u0000\u00ee\u00f0\u0003\u0006\u0003"+
		"\u0000\u00ef\u00ee\u0001\u0000\u0000\u0000\u00f0\u00f3\u0001\u0000\u0000"+
		"\u0000\u00f1\u00ef\u0001\u0000\u0000\u0000\u00f1\u00f2\u0001\u0000\u0000"+
		"\u0000\u00f2\u00f7\u0001\u0000\u0000\u0000\u00f3\u00f1\u0001\u0000\u0000"+
		"\u0000\u00f4\u00f6\u0003\u0014\n\u0000\u00f5\u00f4\u0001\u0000\u0000\u0000"+
		"\u00f6\u00f9\u0001\u0000\u0000\u0000\u00f7\u00f5\u0001\u0000\u0000\u0000"+
		"\u00f7\u00f8\u0001\u0000\u0000\u0000\u00f8\u00fa\u0001\u0000\u0000\u0000"+
		"\u00f9\u00f7\u0001\u0000\u0000\u0000\u00fa\u00fb\u0005H\u0000\u0000\u00fb"+
		"\u0013\u0001\u0000\u0000\u0000\u00fc\u00fd\u0005\u0017\u0000\u0000\u00fd"+
		"\u00fe\u0003\u001c\u000e\u0000\u00fe\u0015\u0001\u0000\u0000\u0000\u00ff"+
		"\u0104\u0003\u0018\f\u0000\u0100\u0101\u0005L\u0000\u0000\u0101\u0103"+
		"\u0003\u0018\f\u0000\u0102\u0100\u0001\u0000\u0000\u0000\u0103\u0106\u0001"+
		"\u0000\u0000\u0000\u0104\u0102\u0001\u0000\u0000\u0000\u0104\u0105\u0001"+
		"\u0000\u0000\u0000\u0105\u0108\u0001\u0000\u0000\u0000\u0106\u0104\u0001"+
		"\u0000\u0000\u0000\u0107\u00ff\u0001\u0000\u0000\u0000\u0107\u0108\u0001"+
		"\u0000\u0000\u0000\u0108\u0017\u0001\u0000\u0000\u0000\u0109\u010a\u0005"+
		"V\u0000\u0000\u010a\u010b\u0005C\u0000\u0000\u010b\u010c\u0003\u001a\r"+
		"\u0000\u010c\u0019\u0001\u0000\u0000\u0000\u010d\u0120\u0005,\u0000\u0000"+
		"\u010e\u0120\u0005#\u0000\u0000\u010f\u0120\u0005\'\u0000\u0000\u0110"+
		"\u0120\u0005(\u0000\u0000\u0111\u0120\u0005%\u0000\u0000\u0112\u0120\u0005"+
		"&\u0000\u0000\u0113\u0120\u0005$\u0000\u0000\u0114\u0120\u0005*\u0000"+
		"\u0000\u0115\u0120\u0005+\u0000\u0000\u0116\u0120\u0005)\u0000\u0000\u0117"+
		"\u0120\u0005P\u0000\u0000\u0118\u0120\u0003\u001c\u000e\u0000\u0119\u0120"+
		"\u0003\u001e\u000f\u0000\u011a\u0120\u0003 \u0010\u0000\u011b\u011c\u0005"+
		"E\u0000\u0000\u011c\u011d\u0003\u001a\r\u0000\u011d\u011e\u0005F\u0000"+
		"\u0000\u011e\u0120\u0001\u0000\u0000\u0000\u011f\u010d\u0001\u0000\u0000"+
		"\u0000\u011f\u010e\u0001\u0000\u0000\u0000\u011f\u010f\u0001\u0000\u0000"+
		"\u0000\u011f\u0110\u0001\u0000\u0000\u0000\u011f\u0111\u0001\u0000\u0000"+
		"\u0000\u011f\u0112\u0001\u0000\u0000\u0000\u011f\u0113\u0001\u0000\u0000"+
		"\u0000\u011f\u0114\u0001\u0000\u0000\u0000\u011f\u0115\u0001\u0000\u0000"+
		"\u0000\u011f\u0116\u0001\u0000\u0000\u0000\u011f\u0117\u0001\u0000\u0000"+
		"\u0000\u011f\u0118\u0001\u0000\u0000\u0000\u011f\u0119\u0001\u0000\u0000"+
		"\u0000\u011f\u011a\u0001\u0000\u0000\u0000\u011f\u011b\u0001\u0000\u0000"+
		"\u0000\u0120\u001b\u0001\u0000\u0000\u0000\u0121\u0123\u0003(\u0014\u0000"+
		"\u0122\u0124\u0003$\u0012\u0000\u0123\u0122\u0001\u0000\u0000\u0000\u0123"+
		"\u0124\u0001\u0000\u0000\u0000\u0124\u001d\u0001\u0000\u0000\u0000\u0125"+
		"\u0126\u0005I\u0000\u0000\u0126\u0127\u0003\u001a\r\u0000\u0127\u0128"+
		"\u0005J\u0000\u0000\u0128\u001f\u0001\u0000\u0000\u0000\u0129\u012a\u0005"+
		"\u0001\u0000\u0000\u012a\u012b\u0005E\u0000\u0000\u012b\u012c\u0003\""+
		"\u0011\u0000\u012c\u012f\u0005F\u0000\u0000\u012d\u012e\u00050\u0000\u0000"+
		"\u012e\u0130\u0003\u001a\r\u0000\u012f\u012d\u0001\u0000\u0000\u0000\u012f"+
		"\u0130\u0001\u0000\u0000\u0000\u0130\u0132\u0001\u0000\u0000\u0000\u0131"+
		"\u0133\u0003>\u001f\u0000\u0132\u0131\u0001\u0000\u0000\u0000\u0132\u0133"+
		"\u0001\u0000\u0000\u0000\u0133!\u0001\u0000\u0000\u0000\u0134\u0139\u0003"+
		"\u001a\r\u0000\u0135\u0136\u0005L\u0000\u0000\u0136\u0138\u0003\u001a"+
		"\r\u0000\u0137\u0135\u0001\u0000\u0000\u0000\u0138\u013b\u0001\u0000\u0000"+
		"\u0000\u0139\u0137\u0001\u0000\u0000\u0000\u0139\u013a\u0001\u0000\u0000"+
		"\u0000\u013a\u013d\u0001\u0000\u0000\u0000\u013b\u0139\u0001\u0000\u0000"+
		"\u0000\u013c\u0134\u0001\u0000\u0000\u0000\u013c\u013d\u0001\u0000\u0000"+
		"\u0000\u013d#\u0001\u0000\u0000\u0000\u013e\u0147\u0005A\u0000\u0000\u013f"+
		"\u0144\u0003\u001a\r\u0000\u0140\u0141\u0005L\u0000\u0000\u0141\u0143"+
		"\u0003\u001a\r\u0000\u0142\u0140\u0001\u0000\u0000\u0000\u0143\u0146\u0001"+
		"\u0000\u0000\u0000\u0144\u0142\u0001\u0000\u0000\u0000\u0144\u0145\u0001"+
		"\u0000\u0000\u0000\u0145\u0148\u0001\u0000\u0000\u0000\u0146\u0144\u0001"+
		"\u0000\u0000\u0000\u0147\u013f\u0001\u0000\u0000\u0000\u0147\u0148\u0001"+
		"\u0000\u0000\u0000\u0148\u0149\u0001\u0000\u0000\u0000\u0149\u014a\u0005"+
		"@\u0000\u0000\u014a%\u0001\u0000\u0000\u0000\u014b\u0154\u0005A\u0000"+
		"\u0000\u014c\u0151\u0005V\u0000\u0000\u014d\u014e\u0005L\u0000\u0000\u014e"+
		"\u0150\u0005V\u0000\u0000\u014f\u014d\u0001\u0000\u0000\u0000\u0150\u0153"+
		"\u0001\u0000\u0000\u0000\u0151\u014f\u0001\u0000\u0000\u0000\u0151\u0152"+
		"\u0001\u0000\u0000\u0000\u0152\u0155\u0001\u0000\u0000\u0000\u0153\u0151"+
		"\u0001\u0000\u0000\u0000\u0154\u014c\u0001\u0000\u0000\u0000\u0154\u0155"+
		"\u0001\u0000\u0000\u0000\u0155\u0156\u0001\u0000\u0000\u0000\u0156\u0157"+
		"\u0005@\u0000\u0000\u0157\'\u0001\u0000\u0000\u0000\u0158\u015d\u0005"+
		"V\u0000\u0000\u0159\u015a\u0005O\u0000\u0000\u015a\u015c\u0005V\u0000"+
		"\u0000\u015b\u0159\u0001\u0000\u0000\u0000\u015c\u015f\u0001\u0000\u0000"+
		"\u0000\u015d\u015b\u0001\u0000\u0000\u0000\u015d\u015e\u0001\u0000\u0000"+
		"\u0000\u015e)\u0001\u0000\u0000\u0000\u015f\u015d\u0001\u0000\u0000\u0000"+
		"\u0160\u0161\u0005K\u0000\u0000\u0161\u0164\u0005V\u0000\u0000\u0162\u0163"+
		"\u0005D\u0000\u0000\u0163\u0165\u00032\u0019\u0000\u0164\u0162\u0001\u0000"+
		"\u0000\u0000\u0164\u0165\u0001\u0000\u0000\u0000\u0165+\u0001\u0000\u0000"+
		"\u0000\u0166\u016f\u0005G\u0000\u0000\u0167\u016c\u0003.\u0017\u0000\u0168"+
		"\u0169\u0005L\u0000\u0000\u0169\u016b\u0003.\u0017\u0000\u016a\u0168\u0001"+
		"\u0000\u0000\u0000\u016b\u016e\u0001\u0000\u0000\u0000\u016c\u016a\u0001"+
		"\u0000\u0000\u0000\u016c\u016d\u0001\u0000\u0000\u0000\u016d\u0170\u0001"+
		"\u0000\u0000\u0000\u016e\u016c\u0001\u0000\u0000\u0000\u016f\u0167\u0001"+
		"\u0000\u0000\u0000\u016f\u0170\u0001\u0000\u0000\u0000\u0170\u0171\u0001"+
		"\u0000\u0000\u0000\u0171\u0172\u0005H\u0000\u0000\u0172-\u0001\u0000\u0000"+
		"\u0000\u0173\u0174\u0007\u0001\u0000\u0000\u0174\u0175\u0005C\u0000\u0000"+
		"\u0175\u0176\u00032\u0019\u0000\u0176/\u0001\u0000\u0000\u0000\u0177\u0180"+
		"\u0005I\u0000\u0000\u0178\u017d\u00032\u0019\u0000\u0179\u017a\u0005L"+
		"\u0000\u0000\u017a\u017c\u00032\u0019\u0000\u017b\u0179\u0001\u0000\u0000"+
		"\u0000\u017c\u017f\u0001\u0000\u0000\u0000\u017d\u017b\u0001\u0000\u0000"+
		"\u0000\u017d\u017e\u0001\u0000\u0000\u0000\u017e\u0181\u0001\u0000\u0000"+
		"\u0000\u017f\u017d\u0001\u0000\u0000\u0000\u0180\u0178\u0001\u0000\u0000"+
		"\u0000\u0180\u0181\u0001\u0000\u0000\u0000\u0181\u0182\u0001\u0000\u0000"+
		"\u0000\u0182\u0183\u0005J\u0000\u0000\u01831\u0001\u0000\u0000\u0000\u0184"+
		"\u018c\u0005R\u0000\u0000\u0185\u018c\u0005S\u0000\u0000\u0186\u018c\u0003"+
		",\u0016\u0000\u0187\u018c\u00030\u0018\u0000\u0188\u018c\u0005\r\u0000"+
		"\u0000\u0189\u018c\u0005\u000e\u0000\u0000\u018a\u018c\u0005\u0006\u0000"+
		"\u0000\u018b\u0184\u0001\u0000\u0000\u0000\u018b\u0185\u0001\u0000\u0000"+
		"\u0000\u018b\u0186\u0001\u0000\u0000\u0000\u018b\u0187\u0001\u0000\u0000"+
		"\u0000\u018b\u0188\u0001\u0000\u0000\u0000\u018b\u0189\u0001\u0000\u0000"+
		"\u0000\u018b\u018a\u0001\u0000\u0000\u0000\u018c3\u0001\u0000\u0000\u0000"+
		"\u018d\u0194\u0005G\u0000\u0000\u018e\u0190\u00038\u001c\u0000\u018f\u0191"+
		"\u0005Q\u0000\u0000\u0190\u018f\u0001\u0000\u0000\u0000\u0190\u0191\u0001"+
		"\u0000\u0000\u0000\u0191\u0193\u0001\u0000\u0000\u0000\u0192\u018e\u0001"+
		"\u0000\u0000\u0000\u0193\u0196\u0001\u0000\u0000\u0000\u0194\u0192\u0001"+
		"\u0000\u0000\u0000\u0194\u0195\u0001\u0000\u0000\u0000\u0195\u0197\u0001"+
		"\u0000\u0000\u0000\u0196\u0194\u0001\u0000\u0000\u0000\u0197\u0198\u0005"+
		"H\u0000\u0000\u01985\u0001\u0000\u0000\u0000\u0199\u019c\u00034\u001a"+
		"\u0000\u019a\u019c\u00038\u001c\u0000\u019b\u0199\u0001\u0000\u0000\u0000"+
		"\u019b\u019a\u0001\u0000\u0000\u0000\u019c7\u0001\u0000\u0000\u0000\u019d"+
		"\u01ac\u0003:\u001d\u0000\u019e\u01ac\u0003<\u001e\u0000\u019f\u01a1\u0005"+
		"\u0011\u0000\u0000\u01a0\u01a2\u00036\u001b\u0000\u01a1\u01a0\u0001\u0000"+
		"\u0000\u0000\u01a1\u01a2\u0001\u0000\u0000\u0000\u01a2\u01ac\u0001\u0000"+
		"\u0000\u0000\u01a3\u01ac\u0003B!\u0000\u01a4\u01ac\u0003J%\u0000\u01a5"+
		"\u01ac\u0003N\'\u0000\u01a6\u01ac\u0003P(\u0000\u01a7\u01ac\u0003T*\u0000"+
		"\u01a8\u01ac\u0005\u0010\u0000\u0000\u01a9\u01ac\u0005.\u0000\u0000\u01aa"+
		"\u01ac\u0003R)\u0000\u01ab\u019d\u0001\u0000\u0000\u0000\u01ab\u019e\u0001"+
		"\u0000\u0000\u0000\u01ab\u019f\u0001\u0000\u0000\u0000\u01ab\u01a3\u0001"+
		"\u0000\u0000\u0000\u01ab\u01a4\u0001\u0000\u0000\u0000\u01ab\u01a5\u0001"+
		"\u0000\u0000\u0000\u01ab\u01a6\u0001\u0000\u0000\u0000\u01ab\u01a7\u0001"+
		"\u0000\u0000\u0000\u01ab\u01a8\u0001\u0000\u0000\u0000\u01ab\u01a9\u0001"+
		"\u0000\u0000\u0000\u01ab\u01aa\u0001\u0000\u0000\u0000\u01ac9\u0001\u0000"+
		"\u0000\u0000\u01ad\u01ae\u0005\u001a\u0000\u0000\u01ae\u01b1\u0005V\u0000"+
		"\u0000\u01af\u01b0\u0005C\u0000\u0000\u01b0\u01b2\u0003\u001a\r\u0000"+
		"\u01b1\u01af\u0001\u0000\u0000\u0000\u01b1\u01b2\u0001\u0000\u0000\u0000"+
		"\u01b2\u01b3\u0001\u0000\u0000\u0000\u01b3\u01b4\u0005D\u0000\u0000\u01b4"+
		"\u01b5\u00036\u001b\u0000\u01b5;\u0001\u0000\u0000\u0000\u01b6\u01b7\u0005"+
		"\u0001\u0000\u0000\u01b7\u01b8\u0005E\u0000\u0000\u01b8\u01b9\u0003r9"+
		"\u0000\u01b9\u01bc\u0005F\u0000\u0000\u01ba\u01bb\u00050\u0000\u0000\u01bb"+
		"\u01bd\u0003\u001a\r\u0000\u01bc\u01ba\u0001\u0000\u0000\u0000\u01bc\u01bd"+
		"\u0001\u0000\u0000\u0000\u01bd\u01bf\u0001\u0000\u0000\u0000\u01be\u01c0"+
		"\u0003>\u001f\u0000\u01bf\u01be\u0001\u0000\u0000\u0000\u01bf\u01c0\u0001"+
		"\u0000\u0000\u0000\u01c0\u01c1\u0001\u0000\u0000\u0000\u01c1\u01c2\u0003"+
		"6\u001b\u0000\u01c2=\u0001\u0000\u0000\u0000\u01c3\u01c4\u0005\u0017\u0000"+
		"\u0000\u01c4\u01c5\u0003@ \u0000\u01c5?\u0001\u0000\u0000\u0000\u01c6"+
		"\u01cb\u0003\u001c\u000e\u0000\u01c7\u01c8\u0005L\u0000\u0000\u01c8\u01ca"+
		"\u0003\u001c\u000e\u0000\u01c9\u01c7\u0001\u0000\u0000\u0000\u01ca\u01cd"+
		"\u0001\u0000\u0000\u0000\u01cb\u01c9\u0001\u0000\u0000\u0000\u01cb\u01cc"+
		"\u0001\u0000\u0000\u0000\u01ccA\u0001\u0000\u0000\u0000\u01cd\u01cb\u0001"+
		"\u0000\u0000\u0000\u01ce\u01cf\u0005\n\u0000\u0000\u01cf\u01d0\u00036"+
		"\u001b\u0000\u01d0\u01d4\u0005G\u0000\u0000\u01d1\u01d3\u0003D\"\u0000"+
		"\u01d2\u01d1\u0001\u0000\u0000\u0000\u01d3\u01d6\u0001\u0000\u0000\u0000"+
		"\u01d4\u01d2\u0001\u0000\u0000\u0000\u01d4\u01d5\u0001\u0000\u0000\u0000"+
		"\u01d5\u01d7\u0001\u0000\u0000\u0000\u01d6\u01d4\u0001\u0000\u0000\u0000"+
		"\u01d7\u01d8\u0005H\u0000\u0000\u01d8C\u0001\u0000\u0000\u0000\u01d9\u01dc"+
		"\u0003H$\u0000\u01da\u01dc\u0003F#\u0000\u01db\u01d9\u0001\u0000\u0000"+
		"\u0000\u01db\u01da\u0001\u0000\u0000\u0000\u01dc\u01dd\u0001\u0000\u0000"+
		"\u0000\u01dd\u01de\u00050\u0000\u0000\u01de\u01df\u00036\u001b\u0000\u01df"+
		"E\u0001\u0000\u0000\u0000\u01e0\u01e6\u0003\u001c\u000e\u0000\u01e1\u01e7"+
		"\u0005V\u0000\u0000\u01e2\u01e3\u0005E\u0000\u0000\u01e3\u01e4\u0003r"+
		"9\u0000\u01e4\u01e5\u0005F\u0000\u0000\u01e5\u01e7\u0001\u0000\u0000\u0000"+
		"\u01e6\u01e1\u0001\u0000\u0000\u0000\u01e6\u01e2\u0001\u0000\u0000\u0000"+
		"\u01e7G\u0001\u0000\u0000\u0000\u01e8\u01e9\u0005M\u0000\u0000\u01e9I"+
		"\u0001\u0000\u0000\u0000\u01ea\u01eb\u0005\u001b\u0000\u0000\u01eb\u01f1"+
		"\u00036\u001b\u0000\u01ec\u01f2\u0005V\u0000\u0000\u01ed\u01ee\u0005E"+
		"\u0000\u0000\u01ee\u01ef\u0003r9\u0000\u01ef\u01f0\u0005F\u0000\u0000"+
		"\u01f0\u01f2\u0001\u0000\u0000\u0000\u01f1\u01ec\u0001\u0000\u0000\u0000"+
		"\u01f1\u01ed\u0001\u0000\u0000\u0000\u01f1\u01f2\u0001\u0000\u0000\u0000"+
		"\u01f2\u01f3\u0001\u0000\u0000\u0000\u01f3\u01f5\u00034\u001a\u0000\u01f4"+
		"\u01f6\u0003L&\u0000\u01f5\u01f4\u0001\u0000\u0000\u0000\u01f5\u01f6\u0001"+
		"\u0000\u0000\u0000\u01f6K\u0001\u0000\u0000\u0000\u01f7\u01fb\u0005\u001c"+
		"\u0000\u0000\u01f8\u01fc\u0003J%\u0000\u01f9\u01fc\u00034\u001a\u0000"+
		"\u01fa\u01fc\u0003B!\u0000\u01fb\u01f8\u0001\u0000\u0000\u0000\u01fb\u01f9"+
		"\u0001\u0000\u0000\u0000\u01fb\u01fa\u0001\u0000\u0000\u0000\u01fcM\u0001"+
		"\u0000\u0000\u0000\u01fd\u01fe\u0005\u001e\u0000\u0000\u01fe\u01ff\u0003"+
		"6\u001b\u0000\u01ff\u0200\u00034\u001a\u0000\u0200O\u0001\u0000\u0000"+
		"\u0000\u0201\u0202\u0005\u001f\u0000\u0000\u0202\u0203\u0005V\u0000\u0000"+
		"\u0203\u0204\u0005\u0003\u0000\u0000\u0204\u0205\u00036\u001b\u0000\u0205"+
		"\u0206\u00034\u001a\u0000\u0206Q\u0001\u0000\u0000\u0000\u0207\u0208\u0005"+
		"\u0015\u0000\u0000\u0208\u0209\u00036\u001b\u0000\u0209S\u0001\u0000\u0000"+
		"\u0000\u020a\u020d\u0003V+\u0000\u020b\u020c\u00057\u0000\u0000\u020c"+
		"\u020e\u0003T*\u0000\u020d\u020b\u0001\u0000\u0000\u0000\u020d\u020e\u0001"+
		"\u0000\u0000\u0000\u020eU\u0001\u0000\u0000\u0000\u020f\u0212\u0003X,"+
		"\u0000\u0210\u0211\u00056\u0000\u0000\u0211\u0213\u0003V+\u0000\u0212"+
		"\u0210\u0001\u0000\u0000\u0000\u0212\u0213\u0001\u0000\u0000\u0000\u0213"+
		"W\u0001\u0000\u0000\u0000\u0214\u0217\u0003Z-\u0000\u0215\u0216\u0007"+
		"\u0002\u0000\u0000\u0216\u0218\u0003X,\u0000\u0217\u0215\u0001\u0000\u0000"+
		"\u0000\u0217\u0218\u0001\u0000\u0000\u0000\u0218Y\u0001\u0000\u0000\u0000"+
		"\u0219\u021c\u0003\\.\u0000\u021a\u021b\u0007\u0003\u0000\u0000\u021b"+
		"\u021d\u0003Z-\u0000\u021c\u021a\u0001\u0000\u0000\u0000\u021c\u021d\u0001"+
		"\u0000\u0000\u0000\u021d[\u0001\u0000\u0000\u0000\u021e\u0221\u0003^/"+
		"\u0000\u021f\u0220\u0007\u0004\u0000\u0000\u0220\u0222\u0003\\.\u0000"+
		"\u0221\u021f\u0001\u0000\u0000\u0000\u0221\u0222\u0001\u0000\u0000\u0000"+
		"\u0222]\u0001\u0000\u0000\u0000\u0223\u0226\u0003`0\u0000\u0224\u0225"+
		"\u0007\u0005\u0000\u0000\u0225\u0227\u0003^/\u0000\u0226\u0224\u0001\u0000"+
		"\u0000\u0000\u0226\u0227\u0001\u0000\u0000\u0000\u0227_\u0001\u0000\u0000"+
		"\u0000\u0228\u022a\u0007\u0006\u0000\u0000\u0229\u0228\u0001\u0000\u0000"+
		"\u0000\u0229\u022a\u0001\u0000\u0000\u0000\u022a\u022b\u0001\u0000\u0000"+
		"\u0000\u022b\u022c\u0003b1\u0000\u022ca\u0001\u0000\u0000\u0000\u022d"+
		"\u0231\u0003f3\u0000\u022e\u0230\u0003d2\u0000\u022f\u022e\u0001\u0000"+
		"\u0000\u0000\u0230\u0233\u0001\u0000\u0000\u0000\u0231\u022f\u0001\u0000"+
		"\u0000\u0000\u0231\u0232\u0001\u0000\u0000\u0000\u0232\u0237\u0001\u0000"+
		"\u0000\u0000\u0233\u0231\u0001\u0000\u0000\u0000\u0234\u0235\u0005D\u0000"+
		"\u0000\u0235\u0238\u00036\u001b\u0000\u0236\u0238\u0003p8\u0000\u0237"+
		"\u0234\u0001\u0000\u0000\u0000\u0237\u0236\u0001\u0000\u0000\u0000\u0237"+
		"\u0238\u0001\u0000\u0000\u0000\u0238c\u0001\u0000\u0000\u0000\u0239\u023a"+
		"\u0005O\u0000\u0000\u023a\u024b\u0005V\u0000\u0000\u023b\u023c\u0005O"+
		"\u0000\u0000\u023c\u024b\u0005\u0019\u0000\u0000\u023d\u023f\u0003$\u0012"+
		"\u0000\u023e\u023d\u0001\u0000\u0000\u0000\u023e\u023f\u0001\u0000\u0000"+
		"\u0000\u023f\u0240\u0001\u0000\u0000\u0000\u0240\u0241\u0005E\u0000\u0000"+
		"\u0241\u0242\u0003j5\u0000\u0242\u0243\u0005F\u0000\u0000\u0243\u024b"+
		"\u0001\u0000\u0000\u0000\u0244\u0245\u0005I\u0000\u0000\u0245\u0246\u0003"+
		"6\u001b\u0000\u0246\u0247\u0005J\u0000\u0000\u0247\u024b\u0001\u0000\u0000"+
		"\u0000\u0248\u0249\u0005\u0004\u0000\u0000\u0249\u024b\u0003\u001a\r\u0000"+
		"\u024a\u0239\u0001\u0000\u0000\u0000\u024a\u023b\u0001\u0000\u0000\u0000"+
		"\u024a\u023e\u0001\u0000\u0000\u0000\u024a\u0244\u0001\u0000\u0000\u0000"+
		"\u024a\u0248\u0001\u0000\u0000\u0000\u024be\u0001\u0000\u0000\u0000\u024c"+
		"\u0261\u0003h4\u0000\u024d\u024e\u0005E\u0000\u0000\u024e\u024f\u0003"+
		"6\u001b\u0000\u024f\u0250\u0005F\u0000\u0000\u0250\u0261\u0001\u0000\u0000"+
		"\u0000\u0251\u0261\u0005S\u0000\u0000\u0252\u025a\u0005V\u0000\u0000\u0253"+
		"\u0255\u0003$\u0012\u0000\u0254\u0253\u0001\u0000\u0000\u0000\u0254\u0255"+
		"\u0001\u0000\u0000\u0000\u0255\u0256\u0001\u0000\u0000\u0000\u0256\u0257"+
		"\u0005G\u0000\u0000\u0257\u0258\u0003l6\u0000\u0258\u0259\u0005H\u0000"+
		"\u0000\u0259\u025b\u0001\u0000\u0000\u0000\u025a\u0254\u0001\u0000\u0000"+
		"\u0000\u025a\u025b\u0001\u0000\u0000\u0000\u025b\u0261\u0001\u0000\u0000"+
		"\u0000\u025c\u0261\u0005R\u0000\u0000\u025d\u0261\u0005\"\u0000\u0000"+
		"\u025e\u0261\u0005\r\u0000\u0000\u025f\u0261\u0005\u000e\u0000\u0000\u0260"+
		"\u024c\u0001\u0000\u0000\u0000\u0260\u024d\u0001\u0000\u0000\u0000\u0260"+
		"\u0251\u0001\u0000\u0000\u0000\u0260\u0252\u0001\u0000\u0000\u0000\u0260"+
		"\u025c\u0001\u0000\u0000\u0000\u0260\u025d\u0001\u0000\u0000\u0000\u0260"+
		"\u025e\u0001\u0000\u0000\u0000\u0260\u025f\u0001\u0000\u0000\u0000\u0261"+
		"g\u0001\u0000\u0000\u0000\u0262\u0263\u0005A\u0000\u0000\u0263\u0264\u0003"+
		"\u001a\r\u0000\u0264\u0265\u0005@\u0000\u0000\u0265\u0267\u0001\u0000"+
		"\u0000\u0000\u0266\u0262\u0001\u0000\u0000\u0000\u0266\u0267\u0001\u0000"+
		"\u0000\u0000\u0267\u0268\u0001\u0000\u0000\u0000\u0268\u0269\u0005I\u0000"+
		"\u0000\u0269\u026a\u0003j5\u0000\u026a\u026b\u0005J\u0000\u0000\u026b"+
		"i\u0001\u0000\u0000\u0000\u026c\u0271\u00036\u001b\u0000\u026d\u026e\u0005"+
		"L\u0000\u0000\u026e\u0270\u00036\u001b\u0000\u026f\u026d\u0001\u0000\u0000"+
		"\u0000\u0270\u0273\u0001\u0000\u0000\u0000\u0271\u026f\u0001\u0000\u0000"+
		"\u0000\u0271\u0272\u0001\u0000\u0000\u0000\u0272\u0275\u0001\u0000\u0000"+
		"\u0000\u0273\u0271\u0001\u0000\u0000\u0000\u0274\u026c\u0001\u0000\u0000"+
		"\u0000\u0274\u0275\u0001\u0000\u0000\u0000\u0275k\u0001\u0000\u0000\u0000"+
		"\u0276\u027b\u0003n7\u0000\u0277\u0278\u0005L\u0000\u0000\u0278\u027a"+
		"\u0003n7\u0000\u0279\u0277\u0001\u0000\u0000\u0000\u027a\u027d\u0001\u0000"+
		"\u0000\u0000\u027b\u0279\u0001\u0000\u0000\u0000\u027b\u027c\u0001\u0000"+
		"\u0000\u0000\u027c\u027f\u0001\u0000\u0000\u0000\u027d\u027b\u0001\u0000"+
		"\u0000\u0000\u027e\u0276\u0001\u0000\u0000\u0000\u027e\u027f\u0001\u0000"+
		"\u0000\u0000\u027fm\u0001\u0000\u0000\u0000\u0280\u0281\u0005V\u0000\u0000"+
		"\u0281\u0282\u0005C\u0000\u0000\u0282\u0283\u00036\u001b\u0000\u0283o"+
		"\u0001\u0000\u0000\u0000\u0284\u0285\u0005\u0002\u0000\u0000\u0285\u0286"+
		"\u0003\u001a\r\u0000\u0286q\u0001\u0000\u0000\u0000\u0287\u028c\u0003"+
		"t:\u0000\u0288\u0289\u0005L\u0000\u0000\u0289\u028b\u0003t:\u0000\u028a"+
		"\u0288\u0001\u0000\u0000\u0000\u028b\u028e\u0001\u0000\u0000\u0000\u028c"+
		"\u028a\u0001\u0000\u0000\u0000\u028c\u028d\u0001\u0000\u0000\u0000\u028d"+
		"\u0290\u0001\u0000\u0000\u0000\u028e\u028c\u0001\u0000\u0000\u0000\u028f"+
		"\u0287\u0001\u0000\u0000\u0000\u028f\u0290\u0001\u0000\u0000\u0000\u0290"+
		"s\u0001\u0000\u0000\u0000\u0291\u0294\u0005V\u0000\u0000\u0292\u0293\u0005"+
		"C\u0000\u0000\u0293\u0295\u0003\u001a\r\u0000\u0294\u0292\u0001\u0000"+
		"\u0000\u0000\u0294\u0295\u0001\u0000\u0000\u0000\u0295u\u0001\u0000\u0000"+
		"\u0000Ly\u007f\u0086\u0089\u0090\u0097\u009c\u00a3\u00a8\u00ad\u00b3\u00b9"+
		"\u00bf\u00ca\u00d6\u00dc\u00e6\u00eb\u00f1\u00f7\u0104\u0107\u011f\u0123"+
		"\u012f\u0132\u0139\u013c\u0144\u0147\u0151\u0154\u015d\u0164\u016c\u016f"+
		"\u017d\u0180\u018b\u0190\u0194\u019b\u01a1\u01ab\u01b1\u01bc\u01bf\u01cb"+
		"\u01d4\u01db\u01e6\u01f1\u01f5\u01fb\u020d\u0212\u0217\u021c\u0221\u0226"+
		"\u0229\u0231\u0237\u023e\u024a\u0254\u025a\u0260\u0266\u0271\u0274\u027b"+
		"\u027e\u028c\u028f\u0294";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}