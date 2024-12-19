package org.karina.lang.lsp.features;

import lombok.Getter;
import org.karina.lang.compiler.parser.gen.KarinaParser;
import org.karina.lang.compiler.parser.gen.KarinaParserBaseVisitor;

import java.util.ArrayList;
import java.util.List;

import static org.karina.lang.lsp.features.TokenProvider.SemanticTokenType.*;

public class SemanticTokenVisitor extends KarinaParserBaseVisitor<Object> {

    @Getter
    private List<Integer> tokens = new ArrayList<>();

    @Override
    public Object visitAnnotation(KarinaParser.AnnotationContext ctx) {
        var token = ctx.ID().getSymbol();
        var line = token.getLine() - 1;
        var character = token.getCharPositionInLine();
        var length = token.getText().length();
        insertType(line, character, length, MACRO);
        return super.visitAnnotation(ctx);
    }

    @Override
    public Object visitMemberInit(KarinaParser.MemberInitContext ctx) {
        var token = ctx.ID().getSymbol();
        var line = token.getLine() - 1;
        var character = token.getCharPositionInLine();
        var length = token.getText().length();
        insertType(line, character, length, PROPERTY);

        return super.visitMemberInit(ctx);
    }

    @Override
    public Object visitObject(KarinaParser.ObjectContext ctx) {
        var initList = ctx.initList();
        if (initList != null) {
            var id = ctx.ID();
            if (id != null) {
                var token = id.getSymbol();
                var line = token.getLine() - 1;
                var character = token.getCharPositionInLine();
                var length = token.getText().length();
                insertType(line, character, length, CLASS);
            }
        }

        return super.visitObject(ctx);
    }

    @Override
    public Object visitMatchInstance(KarinaParser.MatchInstanceContext ctx) {
        var id = ctx.ID();
        if (id != null) {
            var token = id.getSymbol();
            var line = token.getLine() - 1;
            var character = token.getCharPositionInLine();
            var length = token.getText().length();
            insertType(line, character, length, PARAMETER);
        }

        return super.visitMatchInstance(ctx);
    }

    @Override
    public Object visitIf(KarinaParser.IfContext ctx) {
        var id = ctx.ID();
        if (id != null) {
            var token = id.getSymbol();
            var line = token.getLine() - 1;
            var character = token.getCharPositionInLine();
            var length = token.getText().length();
            insertType(line, character, length, PARAMETER);
        }

        return super.visitIf(ctx);
    }

    @Override
    public Object visitFor(KarinaParser.ForContext ctx) {
        var token = ctx.ID().getSymbol();
        var line = token.getLine() - 1;
        var character = token.getCharPositionInLine();
        var length = token.getText().length();
        insertType(line, character, length, PROPERTY);

        return super.visitFor(ctx);
    }

    @Override
    public Object visitPostFix(KarinaParser.PostFixContext ctx) {
        var id = ctx.ID();
        if (id != null) {
            var token = id.getSymbol();
            var line = token.getLine() - 1;
            var character = token.getCharPositionInLine();
            var length = token.getText().length();
            insertType(line, character, length, PROPERTY);
        }

        return super.visitPostFix(ctx);
    }

    @Override
    public Object visitImport_(KarinaParser.Import_Context ctx) {
        for (var names : ctx.dotWordChain().ID()) {
            var token = names.getSymbol();
            var line = token.getLine() - 1;
            var character = token.getCharPositionInLine();
            var length = token.getText().length();
            insertType(line, character, length, NAMESPACE);
        }

        return super.visitImport_(ctx);
    }

    @Override
    public Object visitField(KarinaParser.FieldContext ctx) {
        var token = ctx.ID().getSymbol();
        var line = token.getLine() - 1;
        var character = token.getCharPositionInLine();
        var length = token.getText().length();
        insertType(line, character, length, PROPERTY);

        return super.visitField(ctx);
    }

    @Override
    public Object visitGenericHintDefinition(KarinaParser.GenericHintDefinitionContext ctx) {
        for (var names : ctx.ID()) {
            var token = names.getSymbol();
            var line = token.getLine() - 1;
            var character = token.getCharPositionInLine();
            var length = token.getText().length();
            insertType(line, character, length, TYPE);
        }

        return super.visitGenericHintDefinition(ctx);
    }

    @Override
    public Object visitEnumMember(KarinaParser.EnumMemberContext ctx) {
        var token = ctx.ID().getSymbol();
        var line = token.getLine() - 1;
        var character = token.getCharPositionInLine();
        var length = token.getText().length();
        insertType(line, character, length, ENUM_MEMBER);

        return super.visitEnumMember(ctx);
    }

    @Override
    public Object visitParameter(KarinaParser.ParameterContext ctx) {
        var token = ctx.ID().getSymbol();
        var line = token.getLine() - 1;
        var character = token.getCharPositionInLine();
        var length = token.getText().length();
        insertType(line, character, length, PARAMETER);

        return super.visitParameter(ctx);
    }

    @Override
    public Object visitOptTypeName(KarinaParser.OptTypeNameContext ctx) {
        var token = ctx.ID().getSymbol();
        var line = token.getLine() - 1;
        var character = token.getCharPositionInLine();
        var length = token.getText().length();
        insertType(line, character, length, PARAMETER);

        return super.visitOptTypeName(ctx);
    }

    @Override
    public Object visitVarDef(KarinaParser.VarDefContext ctx) {
        var token = ctx.ID().getSymbol();
        var line = token.getLine() - 1;
        var character = token.getCharPositionInLine();
        var length = token.getText().length();
        insertType(line, character, length, PARAMETER);
        return super.visitVarDef(ctx);
    }

    @Override
    public Object visitStructType(KarinaParser.StructTypeContext ctx) {
        for (var names : ctx.dotWordChain().ID()) {
            var token = names.getSymbol();
            var line = token.getLine() - 1;
            var character = token.getCharPositionInLine();
            var length = token.getText().length();
            insertType(line, character, length, TYPE);
        }
        return super.visitStructType(ctx);
    }

    @Override
    public Object visitInterface(KarinaParser.InterfaceContext ctx) {
        var token = ctx.ID().getSymbol();
        var line = token.getLine() - 1;
        var character = token.getCharPositionInLine();
        var length = token.getText().length();
        insertType(line, character, length, INTERFACE);
        return super.visitInterface(ctx);
    }

    @Override
    public Object visitEnum(KarinaParser.EnumContext ctx) {
        var token = ctx.ID().getSymbol();
        var line = token.getLine() - 1;
        var character = token.getCharPositionInLine();
        var length = token.getText().length();
        insertType(line, character, length, ENUM);
        return super.visitEnum(ctx);
    }

    @Override
    public Object visitStruct(KarinaParser.StructContext ctx) {
        var token = ctx.ID().getSymbol();
        var line = token.getLine() - 1;
        var character = token.getCharPositionInLine();
        var length = token.getText().length();
        insertType(line, character, length, CLASS);
        return super.visitStruct(ctx);
    }

    @Override
    public Object visitFunction(KarinaParser.FunctionContext ctx) {
        var token = ctx.ID().getSymbol();
        var line = token.getLine() - 1;
        var character = token.getCharPositionInLine();
        var length = token.getText().length();
        insertType(line, character, length, FUNCTION);
        return super.visitFunction(ctx);
    }

    private void insertType(int line, int character, int length, TokenProvider.SemanticTokenType type) {
        insertToken(line, character, length, type.ordinal());
    }

    private void insertToken(int line, int character, int length, int type) {
        this.tokens.add(line);
        this.tokens.add(character);
        this.tokens.add(length);
        this.tokens.add(type);
    }

}

