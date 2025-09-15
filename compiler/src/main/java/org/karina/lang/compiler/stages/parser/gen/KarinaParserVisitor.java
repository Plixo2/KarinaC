// Generated from C:/Users/Morit/Documents/Projects/Java/ttyl/compiler/grammar/language/KarinaParser.g4 by ANTLR 4.13.2
package org.karina.lang.compiler.stages.parser.gen;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link KarinaParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface KarinaParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link KarinaParser#unit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnit(KarinaParser.UnitContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#import_}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImport_(KarinaParser.Import_Context ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#commaWordChain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCommaWordChain(KarinaParser.CommaWordChainContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitItem(KarinaParser.ItemContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#function}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction(KarinaParser.FunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#const}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConst(KarinaParser.ConstContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#struct}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStruct(KarinaParser.StructContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#implementation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImplementation(KarinaParser.ImplementationContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#boundWhere}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoundWhere(KarinaParser.BoundWhereContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#genericWithBounds}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGenericWithBounds(KarinaParser.GenericWithBoundsContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#field}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitField(KarinaParser.FieldContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#enum}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnum(KarinaParser.EnumContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#enumMember}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnumMember(KarinaParser.EnumMemberContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#interface}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInterface(KarinaParser.InterfaceContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#interfaceExtension}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInterfaceExtension(KarinaParser.InterfaceExtensionContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#typeInterface}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeInterface(KarinaParser.TypeInterfaceContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#selfParameterList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelfParameterList(KarinaParser.SelfParameterListContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#parameterList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameterList(KarinaParser.ParameterListContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#parameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameter(KarinaParser.ParameterContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(KarinaParser.TypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#typePostFix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypePostFix(KarinaParser.TypePostFixContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#typeInner}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeInner(KarinaParser.TypeInnerContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#structType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStructType(KarinaParser.StructTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#arrayType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayType(KarinaParser.ArrayTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#functionType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionType(KarinaParser.FunctionTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#typeList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeList(KarinaParser.TypeListContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#genericHint}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGenericHint(KarinaParser.GenericHintContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#genericHintDefinition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGenericHintDefinition(KarinaParser.GenericHintDefinitionContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#genericWithBound}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGenericWithBound(KarinaParser.GenericWithBoundContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#boundList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoundList(KarinaParser.BoundListContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#dotWordChain}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDotWordChain(KarinaParser.DotWordChainContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#annotation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnnotation(KarinaParser.AnnotationContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#jsonObj}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJsonObj(KarinaParser.JsonObjContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#jsonPair}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJsonPair(KarinaParser.JsonPairContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#jsonArray}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJsonArray(KarinaParser.JsonArrayContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#jsonExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJsonExpression(KarinaParser.JsonExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#jsonType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJsonType(KarinaParser.JsonTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#jsonMethod}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJsonMethod(KarinaParser.JsonMethodContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#jsonValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJsonValue(KarinaParser.JsonValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(KarinaParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#exprWithBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprWithBlock(KarinaParser.ExprWithBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(KarinaParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#varDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDef(KarinaParser.VarDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#usingVarDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUsingVarDef(KarinaParser.UsingVarDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#closure}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClosure(KarinaParser.ClosureContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#interfaceImpl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInterfaceImpl(KarinaParser.InterfaceImplContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#structTypeList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStructTypeList(KarinaParser.StructTypeListContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#match}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMatch(KarinaParser.MatchContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#matchCase}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMatchCase(KarinaParser.MatchCaseContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#matchInstance}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMatchInstance(KarinaParser.MatchInstanceContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#matchDefault}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMatchDefault(KarinaParser.MatchDefaultContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#if}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIf(KarinaParser.IfContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#elseExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElseExpr(KarinaParser.ElseExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#isShort}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIsShort(KarinaParser.IsShortContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#while}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhile(KarinaParser.WhileContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#for}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFor(KarinaParser.ForContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#throw}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitThrow(KarinaParser.ThrowContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#conditionalOrExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditionalOrExpression(KarinaParser.ConditionalOrExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#conditionalAndExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditionalAndExpression(KarinaParser.ConditionalAndExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#equalityExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEqualityExpression(KarinaParser.EqualityExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#relationalExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelationalExpression(KarinaParser.RelationalExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#additiveExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAdditiveExpression(KarinaParser.AdditiveExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#multiplicativeExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiplicativeExpression(KarinaParser.MultiplicativeExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#unaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryExpression(KarinaParser.UnaryExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#factor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFactor(KarinaParser.FactorContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#postFix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPostFix(KarinaParser.PostFixContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#object}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObject(KarinaParser.ObjectContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#array}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArray(KarinaParser.ArrayContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#dotPostFix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDotPostFix(KarinaParser.DotPostFixContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#pathPostFix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPathPostFix(KarinaParser.PathPostFixContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#superCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSuperCall(KarinaParser.SuperCallContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#expressionList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionList(KarinaParser.ExpressionListContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#initList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInitList(KarinaParser.InitListContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#memberInit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMemberInit(KarinaParser.MemberInitContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#isInstanceOf}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIsInstanceOf(KarinaParser.IsInstanceOfContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#optTypeList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOptTypeList(KarinaParser.OptTypeListContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#optTypeName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOptTypeName(KarinaParser.OptTypeNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#id}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitId(KarinaParser.IdContext ctx);
	/**
	 * Visit a parse tree produced by {@link KarinaParser#escaped}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEscaped(KarinaParser.EscapedContext ctx);
}