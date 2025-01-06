// Generated from C:/Users/Morit/Documents/Projects/Java/ttyl/resources/KarinaParser.g4 by ANTLR 4.13.2
package org.karina.lang.compiler.parser.gen;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link KarinaParser}.
 */
public interface KarinaParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link KarinaParser#unit}.
	 * @param ctx the parse tree
	 */
	void enterUnit(KarinaParser.UnitContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#unit}.
	 * @param ctx the parse tree
	 */
	void exitUnit(KarinaParser.UnitContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#import_}.
	 * @param ctx the parse tree
	 */
	void enterImport_(KarinaParser.Import_Context ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#import_}.
	 * @param ctx the parse tree
	 */
	void exitImport_(KarinaParser.Import_Context ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#item}.
	 * @param ctx the parse tree
	 */
	void enterItem(KarinaParser.ItemContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#item}.
	 * @param ctx the parse tree
	 */
	void exitItem(KarinaParser.ItemContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#function}.
	 * @param ctx the parse tree
	 */
	void enterFunction(KarinaParser.FunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#function}.
	 * @param ctx the parse tree
	 */
	void exitFunction(KarinaParser.FunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#struct}.
	 * @param ctx the parse tree
	 */
	void enterStruct(KarinaParser.StructContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#struct}.
	 * @param ctx the parse tree
	 */
	void exitStruct(KarinaParser.StructContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#implementation}.
	 * @param ctx the parse tree
	 */
	void enterImplementation(KarinaParser.ImplementationContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#implementation}.
	 * @param ctx the parse tree
	 */
	void exitImplementation(KarinaParser.ImplementationContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#field}.
	 * @param ctx the parse tree
	 */
	void enterField(KarinaParser.FieldContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#field}.
	 * @param ctx the parse tree
	 */
	void exitField(KarinaParser.FieldContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#enum}.
	 * @param ctx the parse tree
	 */
	void enterEnum(KarinaParser.EnumContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#enum}.
	 * @param ctx the parse tree
	 */
	void exitEnum(KarinaParser.EnumContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#enumMember}.
	 * @param ctx the parse tree
	 */
	void enterEnumMember(KarinaParser.EnumMemberContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#enumMember}.
	 * @param ctx the parse tree
	 */
	void exitEnumMember(KarinaParser.EnumMemberContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#interface}.
	 * @param ctx the parse tree
	 */
	void enterInterface(KarinaParser.InterfaceContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#interface}.
	 * @param ctx the parse tree
	 */
	void exitInterface(KarinaParser.InterfaceContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#interfaceExtension}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceExtension(KarinaParser.InterfaceExtensionContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#interfaceExtension}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceExtension(KarinaParser.InterfaceExtensionContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void enterParameterList(KarinaParser.ParameterListContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void exitParameterList(KarinaParser.ParameterListContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#parameter}.
	 * @param ctx the parse tree
	 */
	void enterParameter(KarinaParser.ParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#parameter}.
	 * @param ctx the parse tree
	 */
	void exitParameter(KarinaParser.ParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(KarinaParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(KarinaParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#structType}.
	 * @param ctx the parse tree
	 */
	void enterStructType(KarinaParser.StructTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#structType}.
	 * @param ctx the parse tree
	 */
	void exitStructType(KarinaParser.StructTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#arrayType}.
	 * @param ctx the parse tree
	 */
	void enterArrayType(KarinaParser.ArrayTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#arrayType}.
	 * @param ctx the parse tree
	 */
	void exitArrayType(KarinaParser.ArrayTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#functionType}.
	 * @param ctx the parse tree
	 */
	void enterFunctionType(KarinaParser.FunctionTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#functionType}.
	 * @param ctx the parse tree
	 */
	void exitFunctionType(KarinaParser.FunctionTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#typeList}.
	 * @param ctx the parse tree
	 */
	void enterTypeList(KarinaParser.TypeListContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#typeList}.
	 * @param ctx the parse tree
	 */
	void exitTypeList(KarinaParser.TypeListContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#genericHint}.
	 * @param ctx the parse tree
	 */
	void enterGenericHint(KarinaParser.GenericHintContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#genericHint}.
	 * @param ctx the parse tree
	 */
	void exitGenericHint(KarinaParser.GenericHintContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#genericHintDefinition}.
	 * @param ctx the parse tree
	 */
	void enterGenericHintDefinition(KarinaParser.GenericHintDefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#genericHintDefinition}.
	 * @param ctx the parse tree
	 */
	void exitGenericHintDefinition(KarinaParser.GenericHintDefinitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#dotWordChain}.
	 * @param ctx the parse tree
	 */
	void enterDotWordChain(KarinaParser.DotWordChainContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#dotWordChain}.
	 * @param ctx the parse tree
	 */
	void exitDotWordChain(KarinaParser.DotWordChainContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#annotation}.
	 * @param ctx the parse tree
	 */
	void enterAnnotation(KarinaParser.AnnotationContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#annotation}.
	 * @param ctx the parse tree
	 */
	void exitAnnotation(KarinaParser.AnnotationContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#jsonObj}.
	 * @param ctx the parse tree
	 */
	void enterJsonObj(KarinaParser.JsonObjContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#jsonObj}.
	 * @param ctx the parse tree
	 */
	void exitJsonObj(KarinaParser.JsonObjContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#jsonPair}.
	 * @param ctx the parse tree
	 */
	void enterJsonPair(KarinaParser.JsonPairContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#jsonPair}.
	 * @param ctx the parse tree
	 */
	void exitJsonPair(KarinaParser.JsonPairContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#jsonArray}.
	 * @param ctx the parse tree
	 */
	void enterJsonArray(KarinaParser.JsonArrayContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#jsonArray}.
	 * @param ctx the parse tree
	 */
	void exitJsonArray(KarinaParser.JsonArrayContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#jsonValue}.
	 * @param ctx the parse tree
	 */
	void enterJsonValue(KarinaParser.JsonValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#jsonValue}.
	 * @param ctx the parse tree
	 */
	void exitJsonValue(KarinaParser.JsonValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(KarinaParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(KarinaParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#exprWithBlock}.
	 * @param ctx the parse tree
	 */
	void enterExprWithBlock(KarinaParser.ExprWithBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#exprWithBlock}.
	 * @param ctx the parse tree
	 */
	void exitExprWithBlock(KarinaParser.ExprWithBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(KarinaParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(KarinaParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#varDef}.
	 * @param ctx the parse tree
	 */
	void enterVarDef(KarinaParser.VarDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#varDef}.
	 * @param ctx the parse tree
	 */
	void exitVarDef(KarinaParser.VarDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#closure}.
	 * @param ctx the parse tree
	 */
	void enterClosure(KarinaParser.ClosureContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#closure}.
	 * @param ctx the parse tree
	 */
	void exitClosure(KarinaParser.ClosureContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#interfaceImpl}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceImpl(KarinaParser.InterfaceImplContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#interfaceImpl}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceImpl(KarinaParser.InterfaceImplContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#structTypeList}.
	 * @param ctx the parse tree
	 */
	void enterStructTypeList(KarinaParser.StructTypeListContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#structTypeList}.
	 * @param ctx the parse tree
	 */
	void exitStructTypeList(KarinaParser.StructTypeListContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#match}.
	 * @param ctx the parse tree
	 */
	void enterMatch(KarinaParser.MatchContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#match}.
	 * @param ctx the parse tree
	 */
	void exitMatch(KarinaParser.MatchContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#matchCase}.
	 * @param ctx the parse tree
	 */
	void enterMatchCase(KarinaParser.MatchCaseContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#matchCase}.
	 * @param ctx the parse tree
	 */
	void exitMatchCase(KarinaParser.MatchCaseContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#matchInstance}.
	 * @param ctx the parse tree
	 */
	void enterMatchInstance(KarinaParser.MatchInstanceContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#matchInstance}.
	 * @param ctx the parse tree
	 */
	void exitMatchInstance(KarinaParser.MatchInstanceContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#matchDefault}.
	 * @param ctx the parse tree
	 */
	void enterMatchDefault(KarinaParser.MatchDefaultContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#matchDefault}.
	 * @param ctx the parse tree
	 */
	void exitMatchDefault(KarinaParser.MatchDefaultContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#if}.
	 * @param ctx the parse tree
	 */
	void enterIf(KarinaParser.IfContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#if}.
	 * @param ctx the parse tree
	 */
	void exitIf(KarinaParser.IfContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#elseExpr}.
	 * @param ctx the parse tree
	 */
	void enterElseExpr(KarinaParser.ElseExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#elseExpr}.
	 * @param ctx the parse tree
	 */
	void exitElseExpr(KarinaParser.ElseExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#while}.
	 * @param ctx the parse tree
	 */
	void enterWhile(KarinaParser.WhileContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#while}.
	 * @param ctx the parse tree
	 */
	void exitWhile(KarinaParser.WhileContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#for}.
	 * @param ctx the parse tree
	 */
	void enterFor(KarinaParser.ForContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#for}.
	 * @param ctx the parse tree
	 */
	void exitFor(KarinaParser.ForContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#throw}.
	 * @param ctx the parse tree
	 */
	void enterThrow(KarinaParser.ThrowContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#throw}.
	 * @param ctx the parse tree
	 */
	void exitThrow(KarinaParser.ThrowContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#conditionalOrExpression}.
	 * @param ctx the parse tree
	 */
	void enterConditionalOrExpression(KarinaParser.ConditionalOrExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#conditionalOrExpression}.
	 * @param ctx the parse tree
	 */
	void exitConditionalOrExpression(KarinaParser.ConditionalOrExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#conditionalAndExpression}.
	 * @param ctx the parse tree
	 */
	void enterConditionalAndExpression(KarinaParser.ConditionalAndExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#conditionalAndExpression}.
	 * @param ctx the parse tree
	 */
	void exitConditionalAndExpression(KarinaParser.ConditionalAndExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#equalityExpression}.
	 * @param ctx the parse tree
	 */
	void enterEqualityExpression(KarinaParser.EqualityExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#equalityExpression}.
	 * @param ctx the parse tree
	 */
	void exitEqualityExpression(KarinaParser.EqualityExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#relationalExpression}.
	 * @param ctx the parse tree
	 */
	void enterRelationalExpression(KarinaParser.RelationalExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#relationalExpression}.
	 * @param ctx the parse tree
	 */
	void exitRelationalExpression(KarinaParser.RelationalExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#additiveExpression}.
	 * @param ctx the parse tree
	 */
	void enterAdditiveExpression(KarinaParser.AdditiveExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#additiveExpression}.
	 * @param ctx the parse tree
	 */
	void exitAdditiveExpression(KarinaParser.AdditiveExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#multiplicativeExpression}.
	 * @param ctx the parse tree
	 */
	void enterMultiplicativeExpression(KarinaParser.MultiplicativeExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#multiplicativeExpression}.
	 * @param ctx the parse tree
	 */
	void exitMultiplicativeExpression(KarinaParser.MultiplicativeExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#unaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterUnaryExpression(KarinaParser.UnaryExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#unaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitUnaryExpression(KarinaParser.UnaryExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#factor}.
	 * @param ctx the parse tree
	 */
	void enterFactor(KarinaParser.FactorContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#factor}.
	 * @param ctx the parse tree
	 */
	void exitFactor(KarinaParser.FactorContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#postFix}.
	 * @param ctx the parse tree
	 */
	void enterPostFix(KarinaParser.PostFixContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#postFix}.
	 * @param ctx the parse tree
	 */
	void exitPostFix(KarinaParser.PostFixContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#object}.
	 * @param ctx the parse tree
	 */
	void enterObject(KarinaParser.ObjectContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#object}.
	 * @param ctx the parse tree
	 */
	void exitObject(KarinaParser.ObjectContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#array}.
	 * @param ctx the parse tree
	 */
	void enterArray(KarinaParser.ArrayContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#array}.
	 * @param ctx the parse tree
	 */
	void exitArray(KarinaParser.ArrayContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#expressionList}.
	 * @param ctx the parse tree
	 */
	void enterExpressionList(KarinaParser.ExpressionListContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#expressionList}.
	 * @param ctx the parse tree
	 */
	void exitExpressionList(KarinaParser.ExpressionListContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#initList}.
	 * @param ctx the parse tree
	 */
	void enterInitList(KarinaParser.InitListContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#initList}.
	 * @param ctx the parse tree
	 */
	void exitInitList(KarinaParser.InitListContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#memberInit}.
	 * @param ctx the parse tree
	 */
	void enterMemberInit(KarinaParser.MemberInitContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#memberInit}.
	 * @param ctx the parse tree
	 */
	void exitMemberInit(KarinaParser.MemberInitContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#isInstanceOf}.
	 * @param ctx the parse tree
	 */
	void enterIsInstanceOf(KarinaParser.IsInstanceOfContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#isInstanceOf}.
	 * @param ctx the parse tree
	 */
	void exitIsInstanceOf(KarinaParser.IsInstanceOfContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#optTypeList}.
	 * @param ctx the parse tree
	 */
	void enterOptTypeList(KarinaParser.OptTypeListContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#optTypeList}.
	 * @param ctx the parse tree
	 */
	void exitOptTypeList(KarinaParser.OptTypeListContext ctx);
	/**
	 * Enter a parse tree produced by {@link KarinaParser#optTypeName}.
	 * @param ctx the parse tree
	 */
	void enterOptTypeName(KarinaParser.OptTypeNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link KarinaParser#optTypeName}.
	 * @param ctx the parse tree
	 */
	void exitOptTypeName(KarinaParser.OptTypeNameContext ctx);
}