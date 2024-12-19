lexer grammar KarinaLexer;


FN: 'fn';
IS: 'is';
IN: 'in';
AS: 'as';
OF: 'of';
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
TRAIT: 'trait';
IMPL: 'impl';
ENUM: 'enum';
CLASS: 'class';
LET: 'let';
IF: 'if';
ELSE: 'else';
NEW: 'new';
MATCHES: 'matches';
WHILE: 'while';
FOR: 'for';
SUPER: 'super';
INTERFACE: 'interface';
SELF: 'self';
INT: 'int';
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

JAVA_IMPORT:'java::';

ARROW_RIGHT: '->';
ARROW_RIGHT_BOLD: '=>';
GREATER_EQULAS :'>=';
SMALLER_EQUALS :'<=';
EQUALS :'==';
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

STRING_LITERAL: '"' STRING_CHARACTERS? '"';

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

INTEGER_NUMBER: DIGITS;

FLOAT_NUMBER: DIGITS '.' DIGITS EXPONENT?
    | '.' DIGITS EXPONENT?
    | DIGITS EXPONENT
    ;

fragment DIGITS: DIGIT+;
fragment EXPONENT : [eE] [+-]? DIGITS;

ID: [a-zA-Z_][a-zA-Z_0-9]* ;
WS: [ \t\r\n\u000C]+ -> skip;
COMMENT: '/*' .*? '*/' -> skip;
LINE_COMMENT: '//' ~[\r\n]* -> skip;
