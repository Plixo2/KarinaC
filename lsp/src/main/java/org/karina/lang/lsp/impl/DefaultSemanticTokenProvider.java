package org.karina.lang.lsp.impl;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.stages.parser.gen.KarinaLexer;
import org.karina.lang.compiler.stages.parser.gen.KarinaParser;
import org.karina.lang.compiler.stages.parser.gen.KarinaParserBaseVisitor;
import org.karina.lang.lsp.lib.SemanticToken;
import org.karina.lang.lsp.lib.SemanticTokenProvider;

import java.util.ArrayList;
import java.util.List;

public class DefaultSemanticTokenProvider implements SemanticTokenProvider  {


    @Override
    public List<Integer> getTokens(String content) {

        var inputStream = CharStreams.fromString(content);
        var karinaLexer = new KarinaLexer(inputStream);
        var parser = new KarinaParser(new CommonTokenStream(karinaLexer));

        karinaLexer.removeErrorListeners();
        parser.removeErrorListeners();

        var visitor = new SemanticVisitor();
        visitor.visit(parser.unit());
        return getDeltaTokens(visitor.tokens);
    }

    /// Calculates deltas for semantic tokens.
    /// @param tokens the tokens in multiple of 4, where each group of 4 represents: line, character, length, type
    /// @return a list of integers representing the deltas, where each group of 5 represents: deltaLine, deltaCharacter, length, type, modifier
    private static List<Integer> getDeltaTokens(IntList tokens) {
        assert tokens.size() % 5 == 0;
        var result = new ArrayList<Integer>(tokens.size());
        var groups = groupBy(tokens, 5);
        groups.sort((p1, p2) -> {
            var lineA = p1[0];
            var lineB = p2[0];
            if (lineA != lineB) {
                return Integer.compare(lineA, lineB);
            }
            var columnA = p1[1];
            var columnB = p2[1];
            return Integer.compare(columnA, columnB);
        });


        var lastLine = 0;
        var lastCharacter = 0;

        for (var group : groups) {
            var line = group[0];
            var character = group[1];
            var length = group[2];
            var type = group[3];
            var mod = group[4];

            var deltaLine = line - lastLine;
            var deltaCharacter = character - lastCharacter;
            if (deltaLine != 0) {
                deltaCharacter = character;
            }

            result.add(deltaLine);
            result.add(deltaCharacter);
            result.add(length);
            result.add(type);
            result.add(mod);

            lastLine = line;
            lastCharacter = character;
        }

        return result;
    }

    private static List<int[]> groupBy(IntList inputList, int groupSize) {
        List<int[]> result = new ArrayList<>();
        for (int i = 0; i < inputList.size(); i += groupSize) {
            var group = new int[groupSize];
            for (int j = 0; j < groupSize; j++) {
                group[j] = inputList.get(i + j);
            }
            result.add(group);
        }
        return result;
    }


    private static class SemanticVisitor extends KarinaParserBaseVisitor<Object> implements  /*for constants*/ SemanticToken {
        private final IntList tokens = new IntList();

        private void addToken(@Nullable Token token, int type, int mod) {
            if (token == null) {
                return;
            }
            var line = token.getLine() - 1;
            var character = token.getCharPositionInLine();
            var length = token.getText().length();
            this.tokens.add(line, character, length, type, mod);
        }

        private void addToken(@Nullable KarinaParser.IdContext id, int type, int mod) {
            if (id == null) {
                return;
            }
            addToken(id.start, type, mod);
            var escaped = id.escaped();
            if (escaped != null) {
                addToken(escaped.start, type, mod);
            }
        }
        private void addToken(@Nullable KarinaParser.IdContext id, int type) {
            addToken(id, type, 0);
        }



        @Override
        public Object visitStruct(KarinaParser.StructContext ctx) {
            addToken(ctx.id(), CLASS, MOD_DEFINITION | MOD_STATIC);
            return super.visitStruct(ctx);
        }

        @Override
        public Object visitImport_(KarinaParser.Import_Context ctx) {
            var idContext = ctx.id();
            if (idContext != null) {
                addToken(idContext, PROPERTY);
            }

            var dotWordChainContext = ctx.dotWordChain();
            if (dotWordChainContext != null) {
                var ids = dotWordChainContext.id();
                if (ids != null) {
                    for (var id : ids) {
                        addToken(id, NAMESPACE);
                    }
                }
            }

            var commaWordChain = ctx.commaWordChain();
            if (commaWordChain != null) {
                var ids = commaWordChain.id();
                if (ids != null) {
                    for (var id : ids) {
                        addToken(id, PROPERTY);
                    }
                }
            }

            return super.visitImport_(ctx);
        }

        @Override
        public Object visitFunction(KarinaParser.FunctionContext ctx) {
            addToken(ctx.id(), FUNCTION, MOD_DEFINITION);
            return super.visitFunction(ctx);
        }

        @Override
        public Object visitConst(KarinaParser.ConstContext ctx) {
            if (ctx.MUT() != null) {
                addToken(ctx.id(), VARIABLE, MOD_DECLARATION | MOD_STATIC);
            } else {
                addToken(ctx.id(), VARIABLE, MOD_DECLARATION | MOD_STATIC | MOD_READONLY);
            }
            return super.visitConst(ctx);
        }

        @Override
        public Object visitField(KarinaParser.FieldContext ctx) {
            if (ctx.MUT() != null) {
                addToken(ctx.id(), VARIABLE, MOD_DECLARATION);
            } else {
                addToken(ctx.id(), VARIABLE, MOD_DECLARATION | MOD_READONLY);
            }

            return super.visitField(ctx);
        }

        @Override
        public Object visitEnum(KarinaParser.EnumContext ctx) {
            addToken(ctx.id(), ENUM, MOD_DEFINITION | MOD_STATIC);

            return super.visitEnum(ctx);
        }

        @Override
        public Object visitEnumMember(KarinaParser.EnumMemberContext ctx) {
            addToken(ctx.id(), ENUM_MEMBER, MOD_DEFINITION | MOD_STATIC);
            return super.visitEnumMember(ctx);
        }

        @Override
        public Object visitInterface(KarinaParser.InterfaceContext ctx) {
            addToken(ctx.id(), INTERFACE);
            return super.visitInterface(ctx);
        }

        @Override
        public Object visitParameter(KarinaParser.ParameterContext ctx) {
            addToken(ctx.id(), PARAMETER);
            return super.visitParameter(ctx);
        }

        @Override
        public Object visitStructType(KarinaParser.StructTypeContext ctx) {
            var dotWordChain = ctx.dotWordChain();
            if (dotWordChain != null) {
                var ids = dotWordChain.id();
                if (ids != null) {
                    for (var id : ids) {
                        addToken(id, CLASS);
                    }
                }
            }
            return super.visitStructType(ctx);
        }

        @Override
        public Object visitGenericHintDefinition(KarinaParser.GenericHintDefinitionContext ctx) {
            var ids = ctx.id();
            if (ids != null) {
                for (var id : ids) {
                    addToken(id, TYPE_PARAMETER);
                }
            }

            return super.visitGenericHintDefinition(ctx);
        }

        @Override
        public Object visitAnnotation(KarinaParser.AnnotationContext ctx) {
            addToken(ctx.id(), MACRO);
            return super.visitAnnotation(ctx);
        }

        @Override
        public Object visitJsonPair(KarinaParser.JsonPairContext ctx) {
            addToken(ctx.id(), STRING);
            return super.visitJsonPair(ctx);
        }

        @Override
        public Object visitVarDef(KarinaParser.VarDefContext ctx) {
            addToken(ctx.id(), VARIABLE, MOD_DECLARATION);
            return super.visitVarDef(ctx);
        }

        @Override
        public Object visitMatchInstance(KarinaParser.MatchInstanceContext ctx) {
            addToken(ctx.id(), VARIABLE, MOD_DECLARATION);
            return super.visitMatchInstance(ctx);
        }


        @Override
        public Object visitIf(KarinaParser.IfContext ctx) {
            addToken(ctx.id(), VARIABLE, MOD_DECLARATION);
            return super.visitIf(ctx);
        }

        @Override
        public Object visitIsShort(KarinaParser.IsShortContext ctx) {
            addToken(ctx.id(), VARIABLE, MOD_DECLARATION);
            return super.visitIsShort(ctx);
        }

        @Override
        public Object visitOptTypeName(KarinaParser.OptTypeNameContext ctx) {
            addToken(ctx.id(), VARIABLE, MOD_DECLARATION);
            return super.visitOptTypeName(ctx);
        }

        @Override
        public Object visitObject(KarinaParser.ObjectContext ctx) {

            var ids = ctx.id();
            if (ids != null) {
                if (ctx.CHAR_L_BRACE() != null) {
                    for (var id : ids) {
                        addToken(id, TYPE);
                    }
                } else {
                    var size = ids.size();
                    if (size >= 1) {
                        var endOffset = 1;

                        if (ids.size() >= 2) {
                            var secondToLast = ids.get(size - 2);
                            if (secondToLast != null && startsWithUppercase(secondToLast.getText())) {
                                addToken(secondToLast, CLASS);
                                endOffset = 2;
                            }
                        }

                        for (int i = 0; i < size - endOffset; i++) {
                            addToken(ids.get(i), NAMESPACE);
                        }
                    }
                }
            }

            return super.visitObject(ctx);
        }
        private boolean startsWithUppercase(String s) {
            return s != null && !s.isEmpty() && Character.isUpperCase(s.charAt(0));
        }

        @Override
        public Object visitMemberInit(KarinaParser.MemberInitContext ctx) {
            addToken(ctx.id(), PROPERTY);
            return super.visitMemberInit(ctx);
        }
    }

}
