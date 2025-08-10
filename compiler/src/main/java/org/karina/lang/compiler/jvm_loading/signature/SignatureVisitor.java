package org.karina.lang.compiler.jvm_loading.signature;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.jvm_loading.signature.gen.SignatureLexer;
import org.karina.lang.compiler.jvm_loading.signature.gen.SignatureParser;
import org.karina.lang.compiler.jvm_loading.signature.model.*;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Region;

import java.util.List;

public class SignatureVisitor {
    SignatureParser parser;
    Region region;
    public SignatureVisitor(Region region, String signature) {
        this.parser = getParser(signature);
        this.region = region;
    }

    private static SignatureParser getParser(String signature) {
        var inputStream = CharStreams.fromString(signature);
        var lexer = new SignatureLexer(inputStream);
        var tokenStream = new CommonTokenStream(lexer);
        return new SignatureParser(tokenStream);
    }


    public ClassSignature parseClassSignature() {
        var parser = this.parser.classSignature();
        List<TypeParameter> typeParameters = List.of();
        var formalTypeParameters = parser.formalTypeParameters();
        if (formalTypeParameters != null) {
            typeParameters = parseTypeParameters(formalTypeParameters);
        }

        var superClass = parseClassTypeSignature(parser.superclassSignature().classTypeSignature());
        var interfaces = parser.superinterfaceSignature().stream().map(ref -> parseClassTypeSignature(ref.classTypeSignature())).toList();

        return new ClassSignature(typeParameters, superClass, interfaces);
    }

    public MethodSignature parseMethodSignature() {
        var parser = this.parser.methodTypeSignature();
        List<TypeParameter> typeParameters = List.of();
        var formalTypeParameters = parser.formalTypeParameters();
        if (formalTypeParameters != null) {
            typeParameters = parseTypeParameters(formalTypeParameters);
        }
        var parameters = parser.typeSignature().stream().map(this::parseTypeSignature).toList();

        var returnType = parseReturnType(parser.returnType());

        return new MethodSignature(typeParameters, parameters, returnType);
    }

    public FieldSignature parseFieldSignature() {
        var parser = this.parser.fieldTypeSignature();
        var typeSignature = parseFieldTypeSignature(parser.fieldTypeSignatureInner());
        return new FieldSignature(typeSignature);
    }

    private @Nullable TypeSignature parseReturnType(SignatureParser.ReturnTypeContext ctx) {
        if (ctx.typeSignature() != null) {
            return parseTypeSignature(ctx.typeSignature());
        } else {
            return null;
        }
    }


    private List<TypeParameter> parseTypeParameters(SignatureParser.FormalTypeParametersContext ctx) {
        return ctx.formalTypeParameter().stream().map(this::parseFormalTypeParameter).toList();
    }

    private TypeParameter parseFormalTypeParameter(SignatureParser.FormalTypeParameterContext ctx) {
        var name = ctx.identifier().getText();
        var clsBound = ctx.classBound().fieldTypeSignatureInner();
        TypeSignature superType = null;
        if (clsBound != null) {
            superType = parseFieldTypeSignature(clsBound);
        }

        var interfaceTypes = ctx.interfaceBound().stream().map(ref -> parseFieldTypeSignature(ref.fieldTypeSignatureInner())).toList();

        return new TypeParameter(name, superType, interfaceTypes);
    }

    private TypeSignature.ClassTypeSignature parseClassTypeSignature(SignatureParser.ClassTypeSignatureContext ctx) {
        var path = new ObjectPath();
        if (ctx.packageSpecifier() != null) {
            path = getPath(ctx.packageSpecifier());
        }
        var baseClass = parseSubClassSignature(ctx.simpleClassTypeSignature());
        var split = baseClass.name().split("/");
        for (var s : split) {
            path = path.append(s);
        }
        var suffix = ctx.classTypeSignatureSuffix().stream().map(ref -> parseSubClassSignature(ref.simpleClassTypeSignature())).toList();


        var pointer = ClassPointer.of(this.region, path);
        return new TypeSignature.ClassTypeSignature(pointer, baseClass.typeArguments(), suffix);
    }

    private ObjectPath getPath(SignatureParser.PackageSpecifierContext ctx) {

        var name = ctx.identifier().getText();
        var path = new ObjectPath(name);
        for (var packageCtx : ctx.packageSpecifier()) {
            path = path.join(getPath(packageCtx));
        }

        return path;
    }

    private TypeSignature.ClassTypeSignature.SubClassSignature parseSubClassSignature(SignatureParser.SimpleClassTypeSignatureContext ctx) {

        var name  = ctx.identifier().getText();
        var typeArguments = List.<TypeArgument>of();
        if (ctx.typeArguments() != null) {
            typeArguments = parseTypeArguments(ctx.typeArguments());
        }

        return new TypeSignature.ClassTypeSignature.SubClassSignature(name, typeArguments);
    }

    private List<TypeArgument> parseTypeArguments(SignatureParser.TypeArgumentsContext ctx) {
        return ctx.typeArgument().stream().map(this::parseTypeArgument).toList();
    }

    private TypeArgument parseTypeArgument(SignatureParser.TypeArgumentContext ctx) {
        if (ctx.fieldTypeSignatureInner() != null) {
            var signature = parseFieldTypeSignature(ctx.fieldTypeSignatureInner());
            if (ctx.wildcardIndicator() != null) {
                var type = ctx.wildcardIndicator().getText();
                if (type.equals("+")) {
                    return new TypeArgument.Extends(signature);
                } else if (type.equals("-")) {
                    return new TypeArgument.Super(signature);
                } else {
                    throw new IllegalStateException("Unexpected wildcard indicator: " + type);
                }
            }
            return new TypeArgument.Base(signature);
        }
        return new TypeArgument.Any();
    }

    private TypeSignature parseFieldTypeSignature(SignatureParser.FieldTypeSignatureInnerContext ctx) {
        if (ctx.arrayTypeSignature() != null) {
            return parseArrayTypeSignature(ctx.arrayTypeSignature());
        } else if (ctx.classTypeSignature() != null) {
            return parseClassTypeSignature(ctx.classTypeSignature());
        } else if (ctx.typeVariableSignature() != null) {
            return parseTypeVarSignature(ctx);
        }
        throw new NullPointerException("invalid field fieldType signature");
    }

    private TypeSignature parseTypeSignature(SignatureParser.TypeSignatureContext ctx) {
        if (ctx.fieldTypeSignatureInner() != null) {
            return parseFieldTypeSignature(ctx.fieldTypeSignatureInner());
        }
        var text = ctx.baseType().getText();
        var primitive = switch (text) {
            case "B" -> KType.KPrimitive.BYTE;
            case "C" -> KType.KPrimitive.CHAR;
            case "D" -> KType.KPrimitive.DOUBLE;
            case "F" -> KType.KPrimitive.FLOAT;
            case "I" -> KType.KPrimitive.INT;
            case "J" -> KType.KPrimitive.LONG;
            case "S" -> KType.KPrimitive.SHORT;
            case "Z" -> KType.KPrimitive.BOOL;
            default -> throw new IllegalStateException("Unexpected value: " + text);
        };
        return new TypeSignature.BaseSignature(primitive);
    }

    private TypeSignature.ArraySignature parseArrayTypeSignature(SignatureParser.ArrayTypeSignatureContext ctx) {
        var inner = parseTypeSignature(ctx.typeSignature());
        return new TypeSignature.ArraySignature(inner);
    }

    private TypeSignature.TypeVarSignature parseTypeVarSignature(SignatureParser.FieldTypeSignatureInnerContext ctx) {
        return new TypeSignature.TypeVarSignature(ctx.typeVariableSignature().identifier().getText());
    }
}
