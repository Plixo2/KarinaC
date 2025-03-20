parser grammar KarinaParser;

options { tokenVocab=KarinaLexer; }

unit: import_* item* EOF;

import_: 'import' dotWordChain ('*' | id | '{' commaWordChain '}')?;
commaWordChain: id (',' id)*;

item: annotation* (function | struct | enum | interface);


function: 'fn' id? genericHintDefinition? '(' selfParameterList ')' ('->' type)? ('=' expression | block)?;

//boundWhere is not yet implemented
struct: 'struct' id genericHintDefinition? ('{' field* function* implementation* boundWhere* '}')?;
implementation: 'impl' structType ('{' function* '}')?;
boundWhere : 'where' (('{' genericWithBounds '}') | genericWithBounds) ('{' function* '}');
genericWithBounds: (genericWithBound (',' genericWithBound)*)?;

genericWithBound: ID bounds?;
bounds: ':' (bound ('&' bound)*)?;
bound: ('impl' structType | 'extend' structType);

field: id ':' type;

//implementation and boundWhere are not yet implemented
enum: 'enum' id genericHintDefinition? '{' enumMember* function* implementation* boundWhere* '}';
enumMember: id ('(' parameterList ')')?;

interface : 'interface' id genericHintDefinition? '{' function* interfaceExtension* '}';
interfaceExtension: 'impl' structType;

selfParameterList: ((parameter | 'self') (',' parameter)*)?;


parameterList: (parameter (',' parameter)*)?;
parameter: id ':' type;

type: typeInner typePostFix?;

typePostFix: '?';

typeInner: 'void'
    | 'int'
    | 'double'
    | 'short'
    | 'byte'
    | 'char'
    | 'long'
    | 'float'
    | 'bool'
    | 'string'
    | '?'
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
genericHintDefinition: '<' (id (',' id)* )? '>';

dotWordChain: id ('.' id)*;

annotation: '@' id ('=' jsonValue)?;


jsonObj : '{' (jsonPair ((',')? jsonPair)*)? '}';
jsonPair : (STRING_LITERAL | id) ':' jsonValue;
jsonArray : '[' (jsonValue ((',')? jsonValue)*)? ']';

jsonExpression: 'expr' block;
jsonType: 'type' '{' type '}';
jsonMethod: 'fn' '{' function '}';
jsonValue: STRING_LITERAL | NUMBER | jsonObj | jsonArray | 'true' | 'false' | 'null' | jsonExpression | jsonType | jsonMethod;


block: '{' (expression ';'?)* '}';

exprWithBlock : block | expression;

expression: varDef | closure | 'return' exprWithBlock? | match | if | while | for | conditionalOrExpression | 'break' | 'continue' | throw;

varDef: 'let' id (':' type)? '=' (exprWithBlock);

closure : 'fn' '(' optTypeList ')' ('->' type)? interfaceImpl? exprWithBlock;
interfaceImpl: 'impl' structTypeList;
structTypeList: structType (',' structType)*;

match: 'match' exprWithBlock '{' matchCase* '}';
matchCase: (matchDefault | matchInstance) '->' exprWithBlock;
matchInstance: structType (id | '(' optTypeList ')');
matchDefault: '_';

//(id | '(' optTypeList ')')? is used for pattern matching
//this part is only valid when exprWithBlock is a isInstanceOf
if: 'if' exprWithBlock (id | '(' optTypeList ')')? block elseExpr?;
elseExpr: 'else' isShort? (if | block | match);
isShort: 'is' type (id | '(' optTypeList ')')?;

while: 'while' exprWithBlock block;


for: 'for' optTypeName 'in' exprWithBlock block;
//TODO tuple destructuring

throw: 'raise' exprWithBlock;

conditionalOrExpression: conditionalAndExpression ('||' conditionalOrExpression)?;
conditionalAndExpression: equalityExpression ('&&' conditionalAndExpression)?;
equalityExpression: relationalExpression (('==' | '!=' | '===' | '!==') equalityExpression)?;
relationalExpression: additiveExpression (('<' | '>' | '<=' | '>=') relationalExpression)?;
additiveExpression: multiplicativeExpression (('+' | '-' | '&') additiveExpression)?;
multiplicativeExpression: unaryExpression (('*' | '/' | '%') multiplicativeExpression)?;
unaryExpression: ('-' | '!')? factor;
factor: object postFix* (('=' exprWithBlock) | isInstanceOf)?;
postFix: '.' (id | 'class') | '.' 'class' | genericHint? '(' expressionList ')' | '[' exprWithBlock ']' | 'as' type;
object: array | '(' exprWithBlock ')' | NUMBER | id (('.' id)* genericHint? '{' initList '}')? | STRING_LITERAL | CHAR_LITERAL | 'self' | superCall | 'true' | 'false';
array: ('<' type '>')? '[' expressionList ']';

superCall: 'super' '<' structType  '>' ('.' id)?;
//interpolation: '\''  '\'';

expressionList: (exprWithBlock (',' exprWithBlock)*)?;

initList: (memberInit (',' memberInit)*)?;
memberInit: id ':' exprWithBlock;

isInstanceOf: 'is' type;

optTypeList: (optTypeName (',' optTypeName)*)?;
optTypeName: id (':' type)?;

id: ID | 'expr' | 'type' | '\\' escaped | '_';
escaped: FN | IS | IN | AS | EXTEND
| MATCH | OVERRIDE | VIRTUAL | YIELD
| STRUCT | RAISE | TRAIT | IMPL | LET
| MATCHES | SELF | STRING | JSON | BOOL | WHERE;