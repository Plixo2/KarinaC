// Generated from C:/Users/Morit/Documents/Projects/Java/ttyl/resources/grammar/signature/Signature.g4 by ANTLR 4.13.2
package org.karina.lang.compiler.jvm_loading.signature.gen;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SignatureParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface SignatureVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link SignatureParser#fieldTypeSignature}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldTypeSignature(SignatureParser.FieldTypeSignatureContext ctx);
	/**
	 * Visit a parse tree produced by {@link SignatureParser#classSignature}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassSignature(SignatureParser.ClassSignatureContext ctx);
	/**
	 * Visit a parse tree produced by {@link SignatureParser#methodTypeSignature}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethodTypeSignature(SignatureParser.MethodTypeSignatureContext ctx);
	/**
	 * Visit a parse tree produced by {@link SignatureParser#formalTypeParameters}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormalTypeParameters(SignatureParser.FormalTypeParametersContext ctx);
	/**
	 * Visit a parse tree produced by {@link SignatureParser#formalTypeParameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormalTypeParameter(SignatureParser.FormalTypeParameterContext ctx);
	/**
	 * Visit a parse tree produced by {@link SignatureParser#classBound}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassBound(SignatureParser.ClassBoundContext ctx);
	/**
	 * Visit a parse tree produced by {@link SignatureParser#interfaceBound}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInterfaceBound(SignatureParser.InterfaceBoundContext ctx);
	/**
	 * Visit a parse tree produced by {@link SignatureParser#superclassSignature}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSuperclassSignature(SignatureParser.SuperclassSignatureContext ctx);
	/**
	 * Visit a parse tree produced by {@link SignatureParser#superinterfaceSignature}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSuperinterfaceSignature(SignatureParser.SuperinterfaceSignatureContext ctx);
	/**
	 * Visit a parse tree produced by {@link SignatureParser#fieldTypeSignatureInner}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldTypeSignatureInner(SignatureParser.FieldTypeSignatureInnerContext ctx);
	/**
	 * Visit a parse tree produced by {@link SignatureParser#classTypeSignature}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassTypeSignature(SignatureParser.ClassTypeSignatureContext ctx);
	/**
	 * Visit a parse tree produced by {@link SignatureParser#packageSpecifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPackageSpecifier(SignatureParser.PackageSpecifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link SignatureParser#simpleClassTypeSignature}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimpleClassTypeSignature(SignatureParser.SimpleClassTypeSignatureContext ctx);
	/**
	 * Visit a parse tree produced by {@link SignatureParser#classTypeSignatureSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassTypeSignatureSuffix(SignatureParser.ClassTypeSignatureSuffixContext ctx);
	/**
	 * Visit a parse tree produced by {@link SignatureParser#typeVariableSignature}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeVariableSignature(SignatureParser.TypeVariableSignatureContext ctx);
	/**
	 * Visit a parse tree produced by {@link SignatureParser#typeArguments}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeArguments(SignatureParser.TypeArgumentsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SignatureParser#typeArgument}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeArgument(SignatureParser.TypeArgumentContext ctx);
	/**
	 * Visit a parse tree produced by {@link SignatureParser#wildcardIndicator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWildcardIndicator(SignatureParser.WildcardIndicatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link SignatureParser#arrayTypeSignature}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayTypeSignature(SignatureParser.ArrayTypeSignatureContext ctx);
	/**
	 * Visit a parse tree produced by {@link SignatureParser#typeSignature}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeSignature(SignatureParser.TypeSignatureContext ctx);
	/**
	 * Visit a parse tree produced by {@link SignatureParser#returnType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnType(SignatureParser.ReturnTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SignatureParser#throwsSignature}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitThrowsSignature(SignatureParser.ThrowsSignatureContext ctx);
	/**
	 * Visit a parse tree produced by {@link SignatureParser#baseType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBaseType(SignatureParser.BaseTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SignatureParser#identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentifier(SignatureParser.IdentifierContext ctx);
}