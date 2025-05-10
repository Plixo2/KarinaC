lexer grammar KarinaLexer;


EXPR: 'expr';
TYPE: 'type';
FN: 'fn';
IS: 'is';
IN: 'in';
AS: 'as';
NULL: 'null';
IMPORT: 'import';
EXTENDS: 'extends';
EXTEND: 'extend';
MATCH: 'match';
OVERRIDE: 'override';
NATIVE: 'native';
TRUE: 'true';
FALSE: 'false';
VIRTUAL: 'virtual';
BREAK: 'break';
RETURN: 'return';
YIELD: 'yield';
STRUCT: 'struct';
STATIC: 'static';
THROW: 'throw';
TRAIT: 'trait';
IMPL: 'impl';
ENUM: 'enum';
CLASS: 'class';
LET: 'let';
IF: 'if';
CONST: 'const';
ELSE: 'else';
WHILE: 'while';
FOR: 'for';
SUPER: 'super';
WHERE: 'where';
INTERFACE: 'interface';
SELF: 'self';
INT: 'int';
MUT: 'mut';
LONG: 'long';
BYTE: 'byte';
CHAR: 'char';
DOUBLE: 'double';
SHORT: 'short';
STRING: 'string';
FLOAT: 'float';
BOOL: 'bool';
VOID: 'void';
JSON: 'json';
CONTINUE: 'continue';

ARROW_RIGHT: '->';
ARROW_RIGHT_BOLD: '=>';
GREATER_EQULAS :'>=';
SMALLER_EQUALS :'<=';
EQUALS :'==';
STRICT_EQUALS :'===';
STRICT_NOT_EQUALS :'!==';
NOT_EQUALS :'!=';
AND_AND :'&&';
OR_OR :'||';


CHAR_PLIS:'+';
CHAR_MINUS:'-';
CHAR_STAR:'*';
CHAR_R_SLASH:'/';
CHAR_PERCENT:'%';
CHAR_OR:'|';
CHAR_XOR:'^';
CHAR_TILDE:'~';
CHAR_GREATER:'>';
CHAR_SMALLER:'<';
CHAR_EXCLAMATION:'!';
CHAR_COLON:':';
CHAR_COLON_COLON:'::';
CHAR_EQUAL:'=';
CHAR_L_PAREN:'(';
CHAR_R_PAREN:')';
CHAR_L_BRACE:'{';
CHAR_R_BRACE:'}';
CHAR_L_BRACKET:'[';
CHAR_R_BRACKET:']';
CHAR_AT:'@';
CHAR_COMMA:',';
CHAR_UNDER:'_';
CHAR_AND:'&';
CHAR_DOT:'.';
CHAR_QUESTION:'?';
CHAR_SEMICOLON:';';
CHAR_ESCAPE:'\\';


STRING_LITERAL: '"' STRING_CHARACTERS? '"';
CHAR_LITERAL: '\'' (CHAR_CHARACTERS | ESCAPED_DOLLAR | CHAR_VARIABLE)* '\'';

fragment CHAR_VARIABLE: '$' (ID | 'expr' | 'type' | '\\' ESCAPED);
fragment ESCAPED: FN | IS | IN | AS | EXTEND
                   | MATCH | OVERRIDE | VIRTUAL | YIELD
                   | STRUCT | TRAIT | IMPL | LET
                   | SELF | STRING | JSON | BOOL | WHERE | CONST | MUT;
fragment ESCAPED_DOLLAR: '\\$';
fragment CHAR_CHARACTERS: CHAR_CHARACTER+;
fragment CHAR_CHARACTER: ~[$'\\\r\n] | ESCAPE_SEQUENCE;

fragment STRING_CHARACTERS: STRING_CHARACTER+;
fragment STRING_CHARACTER: ~["\\\r\n] | ESCAPE_SEQUENCE;
fragment ESCAPE_SEQUENCE: '\\' [btnfr"'\\] | 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT;

NUMBER: HEX_NUMBER | BINARY_NUMBER | INTEGER_NUMBER | FLOAT_NUMBER;
fragment HEX_NUMBER: '0x' HEX_DIGIT+;
fragment BINARY_NUMBER: '0b' BINARY_DIGIT+;
fragment BINARY_DIGIT: '0' | '1' | '_';
fragment HEX_DIGIT: [0-9a-fA-F_];
fragment DECIMAL_NUMBER: DIGIT;
fragment DIGIT: [0-9_];

INTEGER_NUMBER: ('-')? DIGITS;

FLOAT_NUMBER: ('-')? DIGITS '.' DIGITS EXPONENT?
    | '.' DIGITS EXPONENT?
    | DIGITS EXPONENT
    ;

fragment DIGITS: DIGIT+;
fragment EXPONENT : [eE] [+-]? DIGITS;

ID: [a-zA-Z_][a-zA-Z_0-9$]* ;
WS: [ \t\r\n\u000C]+ -> skip;
COMMENT: '/*' .*? '*/' -> skip;
LINE_COMMENT: '//' ~[\r\n]* -> skip;
