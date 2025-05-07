// Generated from C:/Users/Morit/Documents/Projects/Java/ttyl/resources/grammar/signature/Signature.g4 by ANTLR 4.13.2
package org.karina.lang.compiler.jvm_loading.signature.gen;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SignatureParser}.
 */
public interface SignatureListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link SignatureParser#fieldTypeSignature}.
	 * @param ctx the parse tree
	 */
	void enterFieldTypeSignature(SignatureParser.FieldTypeSignatureContext ctx);
	/**
	 * Exit a parse tree produced by {@link SignatureParser#fieldTypeSignature}.
	 * @param ctx the parse tree
	 */
	void exitFieldTypeSignature(SignatureParser.FieldTypeSignatureContext ctx);
	/**
	 * Enter a parse tree produced by {@link SignatureParser#classSignature}.
	 * @param ctx the parse tree
	 */
	void enterClassSignature(SignatureParser.ClassSignatureContext ctx);
	/**
	 * Exit a parse tree produced by {@link SignatureParser#classSignature}.
	 * @param ctx the parse tree
	 */
	void exitClassSignature(SignatureParser.ClassSignatureContext ctx);
	/**
	 * Enter a parse tree produced by {@link SignatureParser#methodTypeSignature}.
	 * @param ctx the parse tree
	 */
	void enterMethodTypeSignature(SignatureParser.MethodTypeSignatureContext ctx);
	/**
	 * Exit a parse tree produced by {@link SignatureParser#methodTypeSignature}.
	 * @param ctx the parse tree
	 */
	void exitMethodTypeSignature(SignatureParser.MethodTypeSignatureContext ctx);
	/**
	 * Enter a parse tree produced by {@link SignatureParser#formalTypeParameters}.
	 * @param ctx the parse tree
	 */
	void enterFormalTypeParameters(SignatureParser.FormalTypeParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link SignatureParser#formalTypeParameters}.
	 * @param ctx the parse tree
	 */
	void exitFormalTypeParameters(SignatureParser.FormalTypeParametersContext ctx);
	/**
	 * Enter a parse tree produced by {@link SignatureParser#formalTypeParameter}.
	 * @param ctx the parse tree
	 */
	void enterFormalTypeParameter(SignatureParser.FormalTypeParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link SignatureParser#formalTypeParameter}.
	 * @param ctx the parse tree
	 */
	void exitFormalTypeParameter(SignatureParser.FormalTypeParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link SignatureParser#classBound}.
	 * @param ctx the parse tree
	 */
	void enterClassBound(SignatureParser.ClassBoundContext ctx);
	/**
	 * Exit a parse tree produced by {@link SignatureParser#classBound}.
	 * @param ctx the parse tree
	 */
	void exitClassBound(SignatureParser.ClassBoundContext ctx);
	/**
	 * Enter a parse tree produced by {@link SignatureParser#interfaceBound}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceBound(SignatureParser.InterfaceBoundContext ctx);
	/**
	 * Exit a parse tree produced by {@link SignatureParser#interfaceBound}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceBound(SignatureParser.InterfaceBoundContext ctx);
	/**
	 * Enter a parse tree produced by {@link SignatureParser#superclassSignature}.
	 * @param ctx the parse tree
	 */
	void enterSuperclassSignature(SignatureParser.SuperclassSignatureContext ctx);
	/**
	 * Exit a parse tree produced by {@link SignatureParser#superclassSignature}.
	 * @param ctx the parse tree
	 */
	void exitSuperclassSignature(SignatureParser.SuperclassSignatureContext ctx);
	/**
	 * Enter a parse tree produced by {@link SignatureParser#superinterfaceSignature}.
	 * @param ctx the parse tree
	 */
	void enterSuperinterfaceSignature(SignatureParser.SuperinterfaceSignatureContext ctx);
	/**
	 * Exit a parse tree produced by {@link SignatureParser#superinterfaceSignature}.
	 * @param ctx the parse tree
	 */
	void exitSuperinterfaceSignature(SignatureParser.SuperinterfaceSignatureContext ctx);
	/**
	 * Enter a parse tree produced by {@link SignatureParser#fieldTypeSignatureInner}.
	 * @param ctx the parse tree
	 */
	void enterFieldTypeSignatureInner(SignatureParser.FieldTypeSignatureInnerContext ctx);
	/**
	 * Exit a parse tree produced by {@link SignatureParser#fieldTypeSignatureInner}.
	 * @param ctx the parse tree
	 */
	void exitFieldTypeSignatureInner(SignatureParser.FieldTypeSignatureInnerContext ctx);
	/**
	 * Enter a parse tree produced by {@link SignatureParser#classTypeSignature}.
	 * @param ctx the parse tree
	 */
	void enterClassTypeSignature(SignatureParser.ClassTypeSignatureContext ctx);
	/**
	 * Exit a parse tree produced by {@link SignatureParser#classTypeSignature}.
	 * @param ctx the parse tree
	 */
	void exitClassTypeSignature(SignatureParser.ClassTypeSignatureContext ctx);
	/**
	 * Enter a parse tree produced by {@link SignatureParser#packageSpecifier}.
	 * @param ctx the parse tree
	 */
	void enterPackageSpecifier(SignatureParser.PackageSpecifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link SignatureParser#packageSpecifier}.
	 * @param ctx the parse tree
	 */
	void exitPackageSpecifier(SignatureParser.PackageSpecifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link SignatureParser#simpleClassTypeSignature}.
	 * @param ctx the parse tree
	 */
	void enterSimpleClassTypeSignature(SignatureParser.SimpleClassTypeSignatureContext ctx);
	/**
	 * Exit a parse tree produced by {@link SignatureParser#simpleClassTypeSignature}.
	 * @param ctx the parse tree
	 */
	void exitSimpleClassTypeSignature(SignatureParser.SimpleClassTypeSignatureContext ctx);
	/**
	 * Enter a parse tree produced by {@link SignatureParser#classTypeSignatureSuffix}.
	 * @param ctx the parse tree
	 */
	void enterClassTypeSignatureSuffix(SignatureParser.ClassTypeSignatureSuffixContext ctx);
	/**
	 * Exit a parse tree produced by {@link SignatureParser#classTypeSignatureSuffix}.
	 * @param ctx the parse tree
	 */
	void exitClassTypeSignatureSuffix(SignatureParser.ClassTypeSignatureSuffixContext ctx);
	/**
	 * Enter a parse tree produced by {@link SignatureParser#typeVariableSignature}.
	 * @param ctx the parse tree
	 */
	void enterTypeVariableSignature(SignatureParser.TypeVariableSignatureContext ctx);
	/**
	 * Exit a parse tree produced by {@link SignatureParser#typeVariableSignature}.
	 * @param ctx the parse tree
	 */
	void exitTypeVariableSignature(SignatureParser.TypeVariableSignatureContext ctx);
	/**
	 * Enter a parse tree produced by {@link SignatureParser#typeArguments}.
	 * @param ctx the parse tree
	 */
	void enterTypeArguments(SignatureParser.TypeArgumentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SignatureParser#typeArguments}.
	 * @param ctx the parse tree
	 */
	void exitTypeArguments(SignatureParser.TypeArgumentsContext ctx);
	/**
	 * Enter a parse tree produced by {@link SignatureParser#typeArgument}.
	 * @param ctx the parse tree
	 */
	void enterTypeArgument(SignatureParser.TypeArgumentContext ctx);
	/**
	 * Exit a parse tree produced by {@link SignatureParser#typeArgument}.
	 * @param ctx the parse tree
	 */
	void exitTypeArgument(SignatureParser.TypeArgumentContext ctx);
	/**
	 * Enter a parse tree produced by {@link SignatureParser#wildcardIndicator}.
	 * @param ctx the parse tree
	 */
	void enterWildcardIndicator(SignatureParser.WildcardIndicatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link SignatureParser#wildcardIndicator}.
	 * @param ctx the parse tree
	 */
	void exitWildcardIndicator(SignatureParser.WildcardIndicatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link SignatureParser#arrayTypeSignature}.
	 * @param ctx the parse tree
	 */
	void enterArrayTypeSignature(SignatureParser.ArrayTypeSignatureContext ctx);
	/**
	 * Exit a parse tree produced by {@link SignatureParser#arrayTypeSignature}.
	 * @param ctx the parse tree
	 */
	void exitArrayTypeSignature(SignatureParser.ArrayTypeSignatureContext ctx);
	/**
	 * Enter a parse tree produced by {@link SignatureParser#typeSignature}.
	 * @param ctx the parse tree
	 */
	void enterTypeSignature(SignatureParser.TypeSignatureContext ctx);
	/**
	 * Exit a parse tree produced by {@link SignatureParser#typeSignature}.
	 * @param ctx the parse tree
	 */
	void exitTypeSignature(SignatureParser.TypeSignatureContext ctx);
	/**
	 * Enter a parse tree produced by {@link SignatureParser#returnType}.
	 * @param ctx the parse tree
	 */
	void enterReturnType(SignatureParser.ReturnTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SignatureParser#returnType}.
	 * @param ctx the parse tree
	 */
	void exitReturnType(SignatureParser.ReturnTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link SignatureParser#throwsSignature}.
	 * @param ctx the parse tree
	 */
	void enterThrowsSignature(SignatureParser.ThrowsSignatureContext ctx);
	/**
	 * Exit a parse tree produced by {@link SignatureParser#throwsSignature}.
	 * @param ctx the parse tree
	 */
	void exitThrowsSignature(SignatureParser.ThrowsSignatureContext ctx);
	/**
	 * Enter a parse tree produced by {@link SignatureParser#baseType}.
	 * @param ctx the parse tree
	 */
	void enterBaseType(SignatureParser.BaseTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SignatureParser#baseType}.
	 * @param ctx the parse tree
	 */
	void exitBaseType(SignatureParser.BaseTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link SignatureParser#identifier}.
	 * @param ctx the parse tree
	 */
	void enterIdentifier(SignatureParser.IdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link SignatureParser#identifier}.
	 * @param ctx the parse tree
	 */
	void exitIdentifier(SignatureParser.IdentifierContext ctx);
}