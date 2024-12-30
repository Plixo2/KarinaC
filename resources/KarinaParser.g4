parser grammar KarinaParser;

options { tokenVocab=KarinaLexer; }

unit: import_* item* EOF;

import_: 'import' ('*' | ID)? 'java::'? dotWordChain;

item: annotation* (function | struct | enum | interface);


function: 'fn' ID genericHintDefinition? '(' parameterList ')' ('->' type)? ('=' expression | block)?;

struct: 'struct' ID genericHintDefinition? '{' field* function* implementation* '}';
implementation: 'impl' structType '{' function* '}';
field: ID ':' type;

enum: 'enum' ID genericHintDefinition? '{' enumMember* '}';
enumMember: ID ('(' parameterList ')')?;

interface : 'interface' ID genericHintDefinition? '{' function* interfaceExtension* '}';
interfaceExtension: 'impl' structType;

parameterList: (parameter (',' parameter)*)?;
parameter: ID ':' type;


type: 'void'
    | 'int'
    | 'double'
    | 'short'
    | 'byte'
    | 'char'
    | 'long'
    | 'float'
    | 'bool'
    | 'string'
    | structType
    | arrayType
    | functionType
    | '(' type ')'
    ;

structType: dotWordChain genericHint?;
arrayType: '[' type ']' ;
functionType: 'fn' '(' typeList ')' ('->' type)? interfaceImpl?;
typeList: (type (',' type)*)?;

genericHint: '<' (type (',' type)* )? '>';
genericHintDefinition: '<' (ID (',' ID)* )? '>';



dotWordChain: ID ('.' ID)*;

annotation: '@' ID ('=' jsonValue)?;


jsonObj : '{' (jsonPair (',' jsonPair)*)? '}';
jsonPair : (STRING_LITERAL | ID) ':' jsonValue;
jsonArray : '[' (jsonValue (',' jsonValue)*)? ']';
jsonValue : STRING_LITERAL | NUMBER | jsonObj | jsonArray | 'true' | 'false' | 'null';


block: '{' (expression ';'?)* '}';

exprWithBlock : block | expression;

expression: varDef | closure | 'return' exprWithBlock? | match | if | while | for | conditionalOrExpression | 'break' | 'continue';

varDef: 'let' ID (':' type)? '=' (exprWithBlock);

closure : 'fn' '(' optTypeList ')' ('->' type)? interfaceImpl? exprWithBlock;
interfaceImpl: 'impl' structTypeList;
structTypeList: structType (',' structType)*;

match: 'match' exprWithBlock '{' matchCase* '}';
matchCase: (matchDefault | matchInstance) '->' exprWithBlock;
matchInstance: structType (ID | '(' optTypeList ')');
matchDefault: '_';

if: 'if' exprWithBlock (ID | '(' optTypeList ')')? block elseExpr?;
elseExpr: 'else' (if | block | match);

while: 'while' exprWithBlock block;
for: 'for' ID 'in' exprWithBlock block;

conditionalOrExpression: conditionalAndExpression ('||' conditionalOrExpression)?;
conditionalAndExpression: equalityExpression ('&&' conditionalAndExpression)?;
equalityExpression: relationalExpression (('==' | '!=') equalityExpression)?;
relationalExpression: additiveExpression (('<' | '>' | '<=' | '>=') relationalExpression)?;
additiveExpression: multiplicativeExpression (('+' | '-' | '&') additiveExpression)?;
multiplicativeExpression: unaryExpression (('*' | '/' | '%') multiplicativeExpression)?;
unaryExpression: ('-' | '!')? factor;
factor: object postFix* (('=' exprWithBlock) | isInstanceOf)?;
postFix: '.' ID | '.' 'class' | genericHint? '(' expressionList ')' | '[' exprWithBlock ']' | 'as' type;
object: array | '(' exprWithBlock ')' | NUMBER | ID (genericHint? '{' initList '}')? | STRING_LITERAL | 'self' | 'true' | 'false';
array: ('<' type '>')? '[' expressionList ']';



expressionList: (exprWithBlock (',' exprWithBlock)*)?;

initList: (memberInit (',' memberInit)*)?;
memberInit: ID ':' exprWithBlock;

isInstanceOf: 'is' type;

optTypeList: (optTypeName (',' optTypeName)*)?;
optTypeName: ID (':' type)?;