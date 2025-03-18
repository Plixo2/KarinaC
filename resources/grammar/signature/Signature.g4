grammar Signature;

fieldTypeSignature:
    fieldTypeSignatureInner EOF;

classSignature:
    formalTypeParameters? superclassSignature superinterfaceSignature* EOF;

methodTypeSignature:
    formalTypeParameters? '(' typeSignature* ')' returnType throwsSignature* EOF;

formalTypeParameters:
    '<' formalTypeParameter+ '>';

formalTypeParameter:
    identifier classBound interfaceBound*;

classBound:
    ':' fieldTypeSignatureInner?;

interfaceBound:
    ':' fieldTypeSignatureInner;

superclassSignature:
    classTypeSignature;

superinterfaceSignature:
    classTypeSignature;


fieldTypeSignatureInner:
    classTypeSignature
    | arrayTypeSignature
    | typeVariableSignature;

classTypeSignature:
    L packageSpecifier? simpleClassTypeSignature classTypeSignatureSuffix* ';' ;

packageSpecifier:
    identifier '/' packageSpecifier*;

simpleClassTypeSignature:
    identifier typeArguments?;

classTypeSignatureSuffix:
    '.' simpleClassTypeSignature;

typeVariableSignature:
    T identifier ';';

typeArguments:
    '<' typeArgument+ '>';

typeArgument:
    wildcardIndicator? fieldTypeSignatureInner
    | '*';

wildcardIndicator:
    '+'
    | '-';

arrayTypeSignature:
    '[' typeSignature;

typeSignature:
    fieldTypeSignatureInner
    | baseType;



returnType:
    typeSignature
    | V;

throwsSignature:
    '^' classTypeSignature
    | '^' typeVariableSignature;


baseType:
    B | C | D | F | I | J | S | Z;

V: 'V';
T: 'T';
L: 'L';
B: 'B';
C: 'C';
D: 'D';
F: 'F';
I: 'I';
J: 'J';
S: 'S';
Z: 'Z';

identifier: (V | T | L | B | C | D | F | I | J | S | Z | NormalStart) (V | T | L | B | C | D | F | I | J | S | Z | NormalRest | NormalStart)*;

NormalStart: [a-zA-KM-SW-ZU_$];
NormalRest: [0-9];