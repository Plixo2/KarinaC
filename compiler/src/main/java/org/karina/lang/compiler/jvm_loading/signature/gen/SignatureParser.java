// Generated from C:/Users/Morit/Documents/Projects/Java/ttyl/compiler/grammar/signature/Signature.g4 by ANTLR 4.13.2
package org.karina.lang.compiler.jvm_loading.signature.gen;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class SignatureParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, V=14, T=15, L=16, B=17, C=18, D=19, 
		F=20, I=21, J=22, S=23, Z=24, NormalStart=25, NormalRest=26;
	public static final int
		RULE_fieldTypeSignature = 0, RULE_classSignature = 1, RULE_methodTypeSignature = 2, 
		RULE_formalTypeParameters = 3, RULE_formalTypeParameter = 4, RULE_classBound = 5, 
		RULE_interfaceBound = 6, RULE_superclassSignature = 7, RULE_superinterfaceSignature = 8, 
		RULE_fieldTypeSignatureInner = 9, RULE_classTypeSignature = 10, RULE_packageSpecifier = 11, 
		RULE_simpleClassTypeSignature = 12, RULE_classTypeSignatureSuffix = 13, 
		RULE_typeVariableSignature = 14, RULE_typeArguments = 15, RULE_typeArgument = 16, 
		RULE_wildcardIndicator = 17, RULE_arrayTypeSignature = 18, RULE_typeSignature = 19, 
		RULE_returnType = 20, RULE_throwsSignature = 21, RULE_baseType = 22, RULE_identifier = 23;
	private static String[] makeRuleNames() {
		return new String[] {
			"fieldTypeSignature", "classSignature", "methodTypeSignature", "formalTypeParameters", 
			"formalTypeParameter", "classBound", "interfaceBound", "superclassSignature", 
			"superinterfaceSignature", "fieldTypeSignatureInner", "classTypeSignature", 
			"packageSpecifier", "simpleClassTypeSignature", "classTypeSignatureSuffix", 
			"typeVariableSignature", "typeArguments", "typeArgument", "wildcardIndicator", 
			"arrayTypeSignature", "typeSignature", "returnType", "throwsSignature", 
			"baseType", "identifier"
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

	@Override
	public String getGrammarFileName() { return "Signature.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public SignatureParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FieldTypeSignatureContext extends ParserRuleContext {
		public FieldTypeSignatureInnerContext fieldTypeSignatureInner() {
			return getRuleContext(FieldTypeSignatureInnerContext.class,0);
		}
		public TerminalNode EOF() { return getToken(SignatureParser.EOF, 0); }
		public FieldTypeSignatureContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fieldTypeSignature; }
	}

	public final FieldTypeSignatureContext fieldTypeSignature() throws RecognitionException {
		FieldTypeSignatureContext _localctx = new FieldTypeSignatureContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_fieldTypeSignature);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(48);
			fieldTypeSignatureInner();
			setState(49);
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
	public static class ClassSignatureContext extends ParserRuleContext {
		public SuperclassSignatureContext superclassSignature() {
			return getRuleContext(SuperclassSignatureContext.class,0);
		}
		public TerminalNode EOF() { return getToken(SignatureParser.EOF, 0); }
		public FormalTypeParametersContext formalTypeParameters() {
			return getRuleContext(FormalTypeParametersContext.class,0);
		}
		public List<SuperinterfaceSignatureContext> superinterfaceSignature() {
			return getRuleContexts(SuperinterfaceSignatureContext.class);
		}
		public SuperinterfaceSignatureContext superinterfaceSignature(int i) {
			return getRuleContext(SuperinterfaceSignatureContext.class,i);
		}
		public ClassSignatureContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classSignature; }
	}

	public final ClassSignatureContext classSignature() throws RecognitionException {
		ClassSignatureContext _localctx = new ClassSignatureContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_classSignature);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(52);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__2) {
				{
				setState(51);
				formalTypeParameters();
				}
			}

			setState(54);
			superclassSignature();
			setState(58);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==L) {
				{
				{
				setState(55);
				superinterfaceSignature();
				}
				}
				setState(60);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(61);
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
	public static class MethodTypeSignatureContext extends ParserRuleContext {
		public ReturnTypeContext returnType() {
			return getRuleContext(ReturnTypeContext.class,0);
		}
		public TerminalNode EOF() { return getToken(SignatureParser.EOF, 0); }
		public FormalTypeParametersContext formalTypeParameters() {
			return getRuleContext(FormalTypeParametersContext.class,0);
		}
		public List<TypeSignatureContext> typeSignature() {
			return getRuleContexts(TypeSignatureContext.class);
		}
		public TypeSignatureContext typeSignature(int i) {
			return getRuleContext(TypeSignatureContext.class,i);
		}
		public List<ThrowsSignatureContext> throwsSignature() {
			return getRuleContexts(ThrowsSignatureContext.class);
		}
		public ThrowsSignatureContext throwsSignature(int i) {
			return getRuleContext(ThrowsSignatureContext.class,i);
		}
		public MethodTypeSignatureContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_methodTypeSignature; }
	}

	public final MethodTypeSignatureContext methodTypeSignature() throws RecognitionException {
		MethodTypeSignatureContext _localctx = new MethodTypeSignatureContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_methodTypeSignature);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(64);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__2) {
				{
				setState(63);
				formalTypeParameters();
				}
			}

			setState(66);
			match(T__0);
			setState(70);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 33525760L) != 0)) {
				{
				{
				setState(67);
				typeSignature();
				}
				}
				setState(72);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(73);
			match(T__1);
			setState(74);
			returnType();
			setState(78);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__12) {
				{
				{
				setState(75);
				throwsSignature();
				}
				}
				setState(80);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(81);
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
	public static class FormalTypeParametersContext extends ParserRuleContext {
		public List<FormalTypeParameterContext> formalTypeParameter() {
			return getRuleContexts(FormalTypeParameterContext.class);
		}
		public FormalTypeParameterContext formalTypeParameter(int i) {
			return getRuleContext(FormalTypeParameterContext.class,i);
		}
		public FormalTypeParametersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formalTypeParameters; }
	}

	public final FormalTypeParametersContext formalTypeParameters() throws RecognitionException {
		FormalTypeParametersContext _localctx = new FormalTypeParametersContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_formalTypeParameters);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(83);
			match(T__2);
			setState(85); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(84);
				formalTypeParameter();
				}
				}
				setState(87); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & 67092480L) != 0) );
			setState(89);
			match(T__3);
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
	public static class FormalTypeParameterContext extends ParserRuleContext {
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public ClassBoundContext classBound() {
			return getRuleContext(ClassBoundContext.class,0);
		}
		public List<InterfaceBoundContext> interfaceBound() {
			return getRuleContexts(InterfaceBoundContext.class);
		}
		public InterfaceBoundContext interfaceBound(int i) {
			return getRuleContext(InterfaceBoundContext.class,i);
		}
		public FormalTypeParameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formalTypeParameter; }
	}

	public final FormalTypeParameterContext formalTypeParameter() throws RecognitionException {
		FormalTypeParameterContext _localctx = new FormalTypeParameterContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_formalTypeParameter);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(91);
			identifier();
			setState(92);
			classBound();
			setState(96);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(93);
				interfaceBound();
				}
				}
				setState(98);
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
	public static class ClassBoundContext extends ParserRuleContext {
		public FieldTypeSignatureInnerContext fieldTypeSignatureInner() {
			return getRuleContext(FieldTypeSignatureInnerContext.class,0);
		}
		public ClassBoundContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classBound; }
	}

	public final ClassBoundContext classBound() throws RecognitionException {
		ClassBoundContext _localctx = new ClassBoundContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_classBound);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(99);
			match(T__4);
			setState(101);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				{
				setState(100);
				fieldTypeSignatureInner();
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
	public static class InterfaceBoundContext extends ParserRuleContext {
		public FieldTypeSignatureInnerContext fieldTypeSignatureInner() {
			return getRuleContext(FieldTypeSignatureInnerContext.class,0);
		}
		public InterfaceBoundContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_interfaceBound; }
	}

	public final InterfaceBoundContext interfaceBound() throws RecognitionException {
		InterfaceBoundContext _localctx = new InterfaceBoundContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_interfaceBound);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(103);
			match(T__4);
			setState(104);
			fieldTypeSignatureInner();
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
	public static class SuperclassSignatureContext extends ParserRuleContext {
		public ClassTypeSignatureContext classTypeSignature() {
			return getRuleContext(ClassTypeSignatureContext.class,0);
		}
		public SuperclassSignatureContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_superclassSignature; }
	}

	public final SuperclassSignatureContext superclassSignature() throws RecognitionException {
		SuperclassSignatureContext _localctx = new SuperclassSignatureContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_superclassSignature);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(106);
			classTypeSignature();
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
	public static class SuperinterfaceSignatureContext extends ParserRuleContext {
		public ClassTypeSignatureContext classTypeSignature() {
			return getRuleContext(ClassTypeSignatureContext.class,0);
		}
		public SuperinterfaceSignatureContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_superinterfaceSignature; }
	}

	public final SuperinterfaceSignatureContext superinterfaceSignature() throws RecognitionException {
		SuperinterfaceSignatureContext _localctx = new SuperinterfaceSignatureContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_superinterfaceSignature);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(108);
			classTypeSignature();
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
	public static class FieldTypeSignatureInnerContext extends ParserRuleContext {
		public ClassTypeSignatureContext classTypeSignature() {
			return getRuleContext(ClassTypeSignatureContext.class,0);
		}
		public ArrayTypeSignatureContext arrayTypeSignature() {
			return getRuleContext(ArrayTypeSignatureContext.class,0);
		}
		public TypeVariableSignatureContext typeVariableSignature() {
			return getRuleContext(TypeVariableSignatureContext.class,0);
		}
		public FieldTypeSignatureInnerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fieldTypeSignatureInner; }
	}

	public final FieldTypeSignatureInnerContext fieldTypeSignatureInner() throws RecognitionException {
		FieldTypeSignatureInnerContext _localctx = new FieldTypeSignatureInnerContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_fieldTypeSignatureInner);
		try {
			setState(113);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case L:
				enterOuterAlt(_localctx, 1);
				{
				setState(110);
				classTypeSignature();
				}
				break;
			case T__11:
				enterOuterAlt(_localctx, 2);
				{
				setState(111);
				arrayTypeSignature();
				}
				break;
			case T:
				enterOuterAlt(_localctx, 3);
				{
				setState(112);
				typeVariableSignature();
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
	public static class ClassTypeSignatureContext extends ParserRuleContext {
		public TerminalNode L() { return getToken(SignatureParser.L, 0); }
		public SimpleClassTypeSignatureContext simpleClassTypeSignature() {
			return getRuleContext(SimpleClassTypeSignatureContext.class,0);
		}
		public PackageSpecifierContext packageSpecifier() {
			return getRuleContext(PackageSpecifierContext.class,0);
		}
		public List<ClassTypeSignatureSuffixContext> classTypeSignatureSuffix() {
			return getRuleContexts(ClassTypeSignatureSuffixContext.class);
		}
		public ClassTypeSignatureSuffixContext classTypeSignatureSuffix(int i) {
			return getRuleContext(ClassTypeSignatureSuffixContext.class,i);
		}
		public ClassTypeSignatureContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classTypeSignature; }
	}

	public final ClassTypeSignatureContext classTypeSignature() throws RecognitionException {
		ClassTypeSignatureContext _localctx = new ClassTypeSignatureContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_classTypeSignature);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(115);
			match(L);
			setState(117);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				{
				setState(116);
				packageSpecifier();
				}
				break;
			}
			setState(119);
			simpleClassTypeSignature();
			setState(123);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__7) {
				{
				{
				setState(120);
				classTypeSignatureSuffix();
				}
				}
				setState(125);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(126);
			match(T__5);
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
	public static class PackageSpecifierContext extends ParserRuleContext {
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public List<PackageSpecifierContext> packageSpecifier() {
			return getRuleContexts(PackageSpecifierContext.class);
		}
		public PackageSpecifierContext packageSpecifier(int i) {
			return getRuleContext(PackageSpecifierContext.class,i);
		}
		public PackageSpecifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_packageSpecifier; }
	}

	public final PackageSpecifierContext packageSpecifier() throws RecognitionException {
		PackageSpecifierContext _localctx = new PackageSpecifierContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_packageSpecifier);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(128);
			identifier();
			setState(129);
			match(T__6);
			setState(133);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(130);
					packageSpecifier();
					}
					} 
				}
				setState(135);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
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
	public static class SimpleClassTypeSignatureContext extends ParserRuleContext {
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public TypeArgumentsContext typeArguments() {
			return getRuleContext(TypeArgumentsContext.class,0);
		}
		public SimpleClassTypeSignatureContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_simpleClassTypeSignature; }
	}

	public final SimpleClassTypeSignatureContext simpleClassTypeSignature() throws RecognitionException {
		SimpleClassTypeSignatureContext _localctx = new SimpleClassTypeSignatureContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_simpleClassTypeSignature);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(136);
			identifier();
			setState(138);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__2) {
				{
				setState(137);
				typeArguments();
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
	public static class ClassTypeSignatureSuffixContext extends ParserRuleContext {
		public SimpleClassTypeSignatureContext simpleClassTypeSignature() {
			return getRuleContext(SimpleClassTypeSignatureContext.class,0);
		}
		public ClassTypeSignatureSuffixContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classTypeSignatureSuffix; }
	}

	public final ClassTypeSignatureSuffixContext classTypeSignatureSuffix() throws RecognitionException {
		ClassTypeSignatureSuffixContext _localctx = new ClassTypeSignatureSuffixContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_classTypeSignatureSuffix);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(140);
			match(T__7);
			setState(141);
			simpleClassTypeSignature();
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
	public static class TypeVariableSignatureContext extends ParserRuleContext {
		public TerminalNode T() { return getToken(SignatureParser.T, 0); }
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public TypeVariableSignatureContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeVariableSignature; }
	}

	public final TypeVariableSignatureContext typeVariableSignature() throws RecognitionException {
		TypeVariableSignatureContext _localctx = new TypeVariableSignatureContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_typeVariableSignature);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(143);
			match(T);
			setState(144);
			identifier();
			setState(145);
			match(T__5);
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
	public static class TypeArgumentsContext extends ParserRuleContext {
		public List<TypeArgumentContext> typeArgument() {
			return getRuleContexts(TypeArgumentContext.class);
		}
		public TypeArgumentContext typeArgument(int i) {
			return getRuleContext(TypeArgumentContext.class,i);
		}
		public TypeArgumentsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeArguments; }
	}

	public final TypeArgumentsContext typeArguments() throws RecognitionException {
		TypeArgumentsContext _localctx = new TypeArgumentsContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_typeArguments);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(147);
			match(T__2);
			setState(149); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(148);
				typeArgument();
				}
				}
				setState(151); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & 105984L) != 0) );
			setState(153);
			match(T__3);
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
	public static class TypeArgumentContext extends ParserRuleContext {
		public FieldTypeSignatureInnerContext fieldTypeSignatureInner() {
			return getRuleContext(FieldTypeSignatureInnerContext.class,0);
		}
		public WildcardIndicatorContext wildcardIndicator() {
			return getRuleContext(WildcardIndicatorContext.class,0);
		}
		public TypeArgumentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeArgument; }
	}

	public final TypeArgumentContext typeArgument() throws RecognitionException {
		TypeArgumentContext _localctx = new TypeArgumentContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_typeArgument);
		int _la;
		try {
			setState(160);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__9:
			case T__10:
			case T__11:
			case T:
			case L:
				enterOuterAlt(_localctx, 1);
				{
				setState(156);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__9 || _la==T__10) {
					{
					setState(155);
					wildcardIndicator();
					}
				}

				setState(158);
				fieldTypeSignatureInner();
				}
				break;
			case T__8:
				enterOuterAlt(_localctx, 2);
				{
				setState(159);
				match(T__8);
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
	public static class WildcardIndicatorContext extends ParserRuleContext {
		public WildcardIndicatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_wildcardIndicator; }
	}

	public final WildcardIndicatorContext wildcardIndicator() throws RecognitionException {
		WildcardIndicatorContext _localctx = new WildcardIndicatorContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_wildcardIndicator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(162);
			_la = _input.LA(1);
			if ( !(_la==T__9 || _la==T__10) ) {
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

	@SuppressWarnings("CheckReturnValue")
	public static class ArrayTypeSignatureContext extends ParserRuleContext {
		public TypeSignatureContext typeSignature() {
			return getRuleContext(TypeSignatureContext.class,0);
		}
		public ArrayTypeSignatureContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arrayTypeSignature; }
	}

	public final ArrayTypeSignatureContext arrayTypeSignature() throws RecognitionException {
		ArrayTypeSignatureContext _localctx = new ArrayTypeSignatureContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_arrayTypeSignature);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(164);
			match(T__11);
			setState(165);
			typeSignature();
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
	public static class TypeSignatureContext extends ParserRuleContext {
		public FieldTypeSignatureInnerContext fieldTypeSignatureInner() {
			return getRuleContext(FieldTypeSignatureInnerContext.class,0);
		}
		public BaseTypeContext baseType() {
			return getRuleContext(BaseTypeContext.class,0);
		}
		public TypeSignatureContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeSignature; }
	}

	public final TypeSignatureContext typeSignature() throws RecognitionException {
		TypeSignatureContext _localctx = new TypeSignatureContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_typeSignature);
		try {
			setState(169);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__11:
			case T:
			case L:
				enterOuterAlt(_localctx, 1);
				{
				setState(167);
				fieldTypeSignatureInner();
				}
				break;
			case B:
			case C:
			case D:
			case F:
			case I:
			case J:
			case S:
			case Z:
				enterOuterAlt(_localctx, 2);
				{
				setState(168);
				baseType();
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
	public static class ReturnTypeContext extends ParserRuleContext {
		public TypeSignatureContext typeSignature() {
			return getRuleContext(TypeSignatureContext.class,0);
		}
		public TerminalNode V() { return getToken(SignatureParser.V, 0); }
		public ReturnTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_returnType; }
	}

	public final ReturnTypeContext returnType() throws RecognitionException {
		ReturnTypeContext _localctx = new ReturnTypeContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_returnType);
		try {
			setState(173);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__11:
			case T:
			case L:
			case B:
			case C:
			case D:
			case F:
			case I:
			case J:
			case S:
			case Z:
				enterOuterAlt(_localctx, 1);
				{
				setState(171);
				typeSignature();
				}
				break;
			case V:
				enterOuterAlt(_localctx, 2);
				{
				setState(172);
				match(V);
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
	public static class ThrowsSignatureContext extends ParserRuleContext {
		public ClassTypeSignatureContext classTypeSignature() {
			return getRuleContext(ClassTypeSignatureContext.class,0);
		}
		public TypeVariableSignatureContext typeVariableSignature() {
			return getRuleContext(TypeVariableSignatureContext.class,0);
		}
		public ThrowsSignatureContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_throwsSignature; }
	}

	public final ThrowsSignatureContext throwsSignature() throws RecognitionException {
		ThrowsSignatureContext _localctx = new ThrowsSignatureContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_throwsSignature);
		try {
			setState(179);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(175);
				match(T__12);
				setState(176);
				classTypeSignature();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(177);
				match(T__12);
				setState(178);
				typeVariableSignature();
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
	public static class BaseTypeContext extends ParserRuleContext {
		public TerminalNode B() { return getToken(SignatureParser.B, 0); }
		public TerminalNode C() { return getToken(SignatureParser.C, 0); }
		public TerminalNode D() { return getToken(SignatureParser.D, 0); }
		public TerminalNode F() { return getToken(SignatureParser.F, 0); }
		public TerminalNode I() { return getToken(SignatureParser.I, 0); }
		public TerminalNode J() { return getToken(SignatureParser.J, 0); }
		public TerminalNode S() { return getToken(SignatureParser.S, 0); }
		public TerminalNode Z() { return getToken(SignatureParser.Z, 0); }
		public BaseTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_baseType; }
	}

	public final BaseTypeContext baseType() throws RecognitionException {
		BaseTypeContext _localctx = new BaseTypeContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_baseType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(181);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 33423360L) != 0)) ) {
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

	@SuppressWarnings("CheckReturnValue")
	public static class IdentifierContext extends ParserRuleContext {
		public List<TerminalNode> V() { return getTokens(SignatureParser.V); }
		public TerminalNode V(int i) {
			return getToken(SignatureParser.V, i);
		}
		public List<TerminalNode> T() { return getTokens(SignatureParser.T); }
		public TerminalNode T(int i) {
			return getToken(SignatureParser.T, i);
		}
		public List<TerminalNode> L() { return getTokens(SignatureParser.L); }
		public TerminalNode L(int i) {
			return getToken(SignatureParser.L, i);
		}
		public List<TerminalNode> B() { return getTokens(SignatureParser.B); }
		public TerminalNode B(int i) {
			return getToken(SignatureParser.B, i);
		}
		public List<TerminalNode> C() { return getTokens(SignatureParser.C); }
		public TerminalNode C(int i) {
			return getToken(SignatureParser.C, i);
		}
		public List<TerminalNode> D() { return getTokens(SignatureParser.D); }
		public TerminalNode D(int i) {
			return getToken(SignatureParser.D, i);
		}
		public List<TerminalNode> F() { return getTokens(SignatureParser.F); }
		public TerminalNode F(int i) {
			return getToken(SignatureParser.F, i);
		}
		public List<TerminalNode> I() { return getTokens(SignatureParser.I); }
		public TerminalNode I(int i) {
			return getToken(SignatureParser.I, i);
		}
		public List<TerminalNode> J() { return getTokens(SignatureParser.J); }
		public TerminalNode J(int i) {
			return getToken(SignatureParser.J, i);
		}
		public List<TerminalNode> S() { return getTokens(SignatureParser.S); }
		public TerminalNode S(int i) {
			return getToken(SignatureParser.S, i);
		}
		public List<TerminalNode> Z() { return getTokens(SignatureParser.Z); }
		public TerminalNode Z(int i) {
			return getToken(SignatureParser.Z, i);
		}
		public List<TerminalNode> NormalStart() { return getTokens(SignatureParser.NormalStart); }
		public TerminalNode NormalStart(int i) {
			return getToken(SignatureParser.NormalStart, i);
		}
		public List<TerminalNode> NormalRest() { return getTokens(SignatureParser.NormalRest); }
		public TerminalNode NormalRest(int i) {
			return getToken(SignatureParser.NormalRest, i);
		}
		public IdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_identifier; }
	}

	public final IdentifierContext identifier() throws RecognitionException {
		IdentifierContext _localctx = new IdentifierContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_identifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(183);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 67092480L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(187);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 134201344L) != 0)) {
				{
				{
				setState(184);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 134201344L) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
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

	public static final String _serializedATN =
		"\u0004\u0001\u001a\u00bf\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
		"\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004"+
		"\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007"+
		"\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b"+
		"\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007"+
		"\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007"+
		"\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007"+
		"\u0015\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0001\u0003\u00015\b\u0001\u0001\u0001\u0001"+
		"\u0001\u0005\u00019\b\u0001\n\u0001\f\u0001<\t\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0002\u0003\u0002A\b\u0002\u0001\u0002\u0001\u0002\u0005"+
		"\u0002E\b\u0002\n\u0002\f\u0002H\t\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0005\u0002M\b\u0002\n\u0002\f\u0002P\t\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0003\u0001\u0003\u0004\u0003V\b\u0003\u000b\u0003\f\u0003"+
		"W\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0005\u0004"+
		"_\b\u0004\n\u0004\f\u0004b\t\u0004\u0001\u0005\u0001\u0005\u0003\u0005"+
		"f\b\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007"+
		"\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0003\tr\b\t\u0001\n\u0001\n"+
		"\u0003\nv\b\n\u0001\n\u0001\n\u0005\nz\b\n\n\n\f\n}\t\n\u0001\n\u0001"+
		"\n\u0001\u000b\u0001\u000b\u0001\u000b\u0005\u000b\u0084\b\u000b\n\u000b"+
		"\f\u000b\u0087\t\u000b\u0001\f\u0001\f\u0003\f\u008b\b\f\u0001\r\u0001"+
		"\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000f"+
		"\u0001\u000f\u0004\u000f\u0096\b\u000f\u000b\u000f\f\u000f\u0097\u0001"+
		"\u000f\u0001\u000f\u0001\u0010\u0003\u0010\u009d\b\u0010\u0001\u0010\u0001"+
		"\u0010\u0003\u0010\u00a1\b\u0010\u0001\u0011\u0001\u0011\u0001\u0012\u0001"+
		"\u0012\u0001\u0012\u0001\u0013\u0001\u0013\u0003\u0013\u00aa\b\u0013\u0001"+
		"\u0014\u0001\u0014\u0003\u0014\u00ae\b\u0014\u0001\u0015\u0001\u0015\u0001"+
		"\u0015\u0001\u0015\u0003\u0015\u00b4\b\u0015\u0001\u0016\u0001\u0016\u0001"+
		"\u0017\u0001\u0017\u0005\u0017\u00ba\b\u0017\n\u0017\f\u0017\u00bd\t\u0017"+
		"\u0001\u0017\u0000\u0000\u0018\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010"+
		"\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.\u0000\u0004\u0001"+
		"\u0000\n\u000b\u0001\u0000\u0011\u0018\u0001\u0000\u000e\u0019\u0001\u0000"+
		"\u000e\u001a\u00bb\u00000\u0001\u0000\u0000\u0000\u00024\u0001\u0000\u0000"+
		"\u0000\u0004@\u0001\u0000\u0000\u0000\u0006S\u0001\u0000\u0000\u0000\b"+
		"[\u0001\u0000\u0000\u0000\nc\u0001\u0000\u0000\u0000\fg\u0001\u0000\u0000"+
		"\u0000\u000ej\u0001\u0000\u0000\u0000\u0010l\u0001\u0000\u0000\u0000\u0012"+
		"q\u0001\u0000\u0000\u0000\u0014s\u0001\u0000\u0000\u0000\u0016\u0080\u0001"+
		"\u0000\u0000\u0000\u0018\u0088\u0001\u0000\u0000\u0000\u001a\u008c\u0001"+
		"\u0000\u0000\u0000\u001c\u008f\u0001\u0000\u0000\u0000\u001e\u0093\u0001"+
		"\u0000\u0000\u0000 \u00a0\u0001\u0000\u0000\u0000\"\u00a2\u0001\u0000"+
		"\u0000\u0000$\u00a4\u0001\u0000\u0000\u0000&\u00a9\u0001\u0000\u0000\u0000"+
		"(\u00ad\u0001\u0000\u0000\u0000*\u00b3\u0001\u0000\u0000\u0000,\u00b5"+
		"\u0001\u0000\u0000\u0000.\u00b7\u0001\u0000\u0000\u000001\u0003\u0012"+
		"\t\u000012\u0005\u0000\u0000\u00012\u0001\u0001\u0000\u0000\u000035\u0003"+
		"\u0006\u0003\u000043\u0001\u0000\u0000\u000045\u0001\u0000\u0000\u0000"+
		"56\u0001\u0000\u0000\u00006:\u0003\u000e\u0007\u000079\u0003\u0010\b\u0000"+
		"87\u0001\u0000\u0000\u00009<\u0001\u0000\u0000\u0000:8\u0001\u0000\u0000"+
		"\u0000:;\u0001\u0000\u0000\u0000;=\u0001\u0000\u0000\u0000<:\u0001\u0000"+
		"\u0000\u0000=>\u0005\u0000\u0000\u0001>\u0003\u0001\u0000\u0000\u0000"+
		"?A\u0003\u0006\u0003\u0000@?\u0001\u0000\u0000\u0000@A\u0001\u0000\u0000"+
		"\u0000AB\u0001\u0000\u0000\u0000BF\u0005\u0001\u0000\u0000CE\u0003&\u0013"+
		"\u0000DC\u0001\u0000\u0000\u0000EH\u0001\u0000\u0000\u0000FD\u0001\u0000"+
		"\u0000\u0000FG\u0001\u0000\u0000\u0000GI\u0001\u0000\u0000\u0000HF\u0001"+
		"\u0000\u0000\u0000IJ\u0005\u0002\u0000\u0000JN\u0003(\u0014\u0000KM\u0003"+
		"*\u0015\u0000LK\u0001\u0000\u0000\u0000MP\u0001\u0000\u0000\u0000NL\u0001"+
		"\u0000\u0000\u0000NO\u0001\u0000\u0000\u0000OQ\u0001\u0000\u0000\u0000"+
		"PN\u0001\u0000\u0000\u0000QR\u0005\u0000\u0000\u0001R\u0005\u0001\u0000"+
		"\u0000\u0000SU\u0005\u0003\u0000\u0000TV\u0003\b\u0004\u0000UT\u0001\u0000"+
		"\u0000\u0000VW\u0001\u0000\u0000\u0000WU\u0001\u0000\u0000\u0000WX\u0001"+
		"\u0000\u0000\u0000XY\u0001\u0000\u0000\u0000YZ\u0005\u0004\u0000\u0000"+
		"Z\u0007\u0001\u0000\u0000\u0000[\\\u0003.\u0017\u0000\\`\u0003\n\u0005"+
		"\u0000]_\u0003\f\u0006\u0000^]\u0001\u0000\u0000\u0000_b\u0001\u0000\u0000"+
		"\u0000`^\u0001\u0000\u0000\u0000`a\u0001\u0000\u0000\u0000a\t\u0001\u0000"+
		"\u0000\u0000b`\u0001\u0000\u0000\u0000ce\u0005\u0005\u0000\u0000df\u0003"+
		"\u0012\t\u0000ed\u0001\u0000\u0000\u0000ef\u0001\u0000\u0000\u0000f\u000b"+
		"\u0001\u0000\u0000\u0000gh\u0005\u0005\u0000\u0000hi\u0003\u0012\t\u0000"+
		"i\r\u0001\u0000\u0000\u0000jk\u0003\u0014\n\u0000k\u000f\u0001\u0000\u0000"+
		"\u0000lm\u0003\u0014\n\u0000m\u0011\u0001\u0000\u0000\u0000nr\u0003\u0014"+
		"\n\u0000or\u0003$\u0012\u0000pr\u0003\u001c\u000e\u0000qn\u0001\u0000"+
		"\u0000\u0000qo\u0001\u0000\u0000\u0000qp\u0001\u0000\u0000\u0000r\u0013"+
		"\u0001\u0000\u0000\u0000su\u0005\u0010\u0000\u0000tv\u0003\u0016\u000b"+
		"\u0000ut\u0001\u0000\u0000\u0000uv\u0001\u0000\u0000\u0000vw\u0001\u0000"+
		"\u0000\u0000w{\u0003\u0018\f\u0000xz\u0003\u001a\r\u0000yx\u0001\u0000"+
		"\u0000\u0000z}\u0001\u0000\u0000\u0000{y\u0001\u0000\u0000\u0000{|\u0001"+
		"\u0000\u0000\u0000|~\u0001\u0000\u0000\u0000}{\u0001\u0000\u0000\u0000"+
		"~\u007f\u0005\u0006\u0000\u0000\u007f\u0015\u0001\u0000\u0000\u0000\u0080"+
		"\u0081\u0003.\u0017\u0000\u0081\u0085\u0005\u0007\u0000\u0000\u0082\u0084"+
		"\u0003\u0016\u000b\u0000\u0083\u0082\u0001\u0000\u0000\u0000\u0084\u0087"+
		"\u0001\u0000\u0000\u0000\u0085\u0083\u0001\u0000\u0000\u0000\u0085\u0086"+
		"\u0001\u0000\u0000\u0000\u0086\u0017\u0001\u0000\u0000\u0000\u0087\u0085"+
		"\u0001\u0000\u0000\u0000\u0088\u008a\u0003.\u0017\u0000\u0089\u008b\u0003"+
		"\u001e\u000f\u0000\u008a\u0089\u0001\u0000\u0000\u0000\u008a\u008b\u0001"+
		"\u0000\u0000\u0000\u008b\u0019\u0001\u0000\u0000\u0000\u008c\u008d\u0005"+
		"\b\u0000\u0000\u008d\u008e\u0003\u0018\f\u0000\u008e\u001b\u0001\u0000"+
		"\u0000\u0000\u008f\u0090\u0005\u000f\u0000\u0000\u0090\u0091\u0003.\u0017"+
		"\u0000\u0091\u0092\u0005\u0006\u0000\u0000\u0092\u001d\u0001\u0000\u0000"+
		"\u0000\u0093\u0095\u0005\u0003\u0000\u0000\u0094\u0096\u0003 \u0010\u0000"+
		"\u0095\u0094\u0001\u0000\u0000\u0000\u0096\u0097\u0001\u0000\u0000\u0000"+
		"\u0097\u0095\u0001\u0000\u0000\u0000\u0097\u0098\u0001\u0000\u0000\u0000"+
		"\u0098\u0099\u0001\u0000\u0000\u0000\u0099\u009a\u0005\u0004\u0000\u0000"+
		"\u009a\u001f\u0001\u0000\u0000\u0000\u009b\u009d\u0003\"\u0011\u0000\u009c"+
		"\u009b\u0001\u0000\u0000\u0000\u009c\u009d\u0001\u0000\u0000\u0000\u009d"+
		"\u009e\u0001\u0000\u0000\u0000\u009e\u00a1\u0003\u0012\t\u0000\u009f\u00a1"+
		"\u0005\t\u0000\u0000\u00a0\u009c\u0001\u0000\u0000\u0000\u00a0\u009f\u0001"+
		"\u0000\u0000\u0000\u00a1!\u0001\u0000\u0000\u0000\u00a2\u00a3\u0007\u0000"+
		"\u0000\u0000\u00a3#\u0001\u0000\u0000\u0000\u00a4\u00a5\u0005\f\u0000"+
		"\u0000\u00a5\u00a6\u0003&\u0013\u0000\u00a6%\u0001\u0000\u0000\u0000\u00a7"+
		"\u00aa\u0003\u0012\t\u0000\u00a8\u00aa\u0003,\u0016\u0000\u00a9\u00a7"+
		"\u0001\u0000\u0000\u0000\u00a9\u00a8\u0001\u0000\u0000\u0000\u00aa\'\u0001"+
		"\u0000\u0000\u0000\u00ab\u00ae\u0003&\u0013\u0000\u00ac\u00ae\u0005\u000e"+
		"\u0000\u0000\u00ad\u00ab\u0001\u0000\u0000\u0000\u00ad\u00ac\u0001\u0000"+
		"\u0000\u0000\u00ae)\u0001\u0000\u0000\u0000\u00af\u00b0\u0005\r\u0000"+
		"\u0000\u00b0\u00b4\u0003\u0014\n\u0000\u00b1\u00b2\u0005\r\u0000\u0000"+
		"\u00b2\u00b4\u0003\u001c\u000e\u0000\u00b3\u00af\u0001\u0000\u0000\u0000"+
		"\u00b3\u00b1\u0001\u0000\u0000\u0000\u00b4+\u0001\u0000\u0000\u0000\u00b5"+
		"\u00b6\u0007\u0001\u0000\u0000\u00b6-\u0001\u0000\u0000\u0000\u00b7\u00bb"+
		"\u0007\u0002\u0000\u0000\u00b8\u00ba\u0007\u0003\u0000\u0000\u00b9\u00b8"+
		"\u0001\u0000\u0000\u0000\u00ba\u00bd\u0001\u0000\u0000\u0000\u00bb\u00b9"+
		"\u0001\u0000\u0000\u0000\u00bb\u00bc\u0001\u0000\u0000\u0000\u00bc/\u0001"+
		"\u0000\u0000\u0000\u00bd\u00bb\u0001\u0000\u0000\u0000\u00144:@FNW`eq"+
		"u{\u0085\u008a\u0097\u009c\u00a0\u00a9\u00ad\u00b3\u00bb";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}