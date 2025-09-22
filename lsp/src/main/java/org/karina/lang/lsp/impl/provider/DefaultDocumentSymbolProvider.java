package org.karina.lang.lsp.impl.provider;

import karina.lang.Option;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.eclipse.lsp4j.*;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.stages.parser.gen.KarinaLexer;
import org.karina.lang.compiler.stages.parser.gen.KarinaParser;
import org.karina.lang.lsp.lib.events.EventService;
import org.karina.lang.lsp.lib.provider.DocumentSymbolProvider;
import org.karina.lang.lsp.test_compiler.CompiledModelIndex;
import org.karina.lang.lsp.test_compiler.ProviderArgs;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class DefaultDocumentSymbolProvider implements DocumentSymbolProvider {
    private final EventService eventService;

    @Override
    public List<DocumentSymbol> getSymbols(ProviderArgs index, URI uri) {
        if (!(index.getCache(uri) instanceof Option.Some(var cacheState))) {
            return List.of();
        }

        var symbols = new ArrayList<DocumentSymbol>();
        var unit = cacheState.parsedUnit();
        if (unit != null) {
            var items = unit.item();
            if (items != null) {
                for (var item : items) {
                    DocumentSymbol symbol = null;
                    if (item.interface_() != null) {
                        symbol = interfaceSymbol(item.interface_());
                    } else if (item.struct() != null) {
                        symbol = structSymbol(item.struct());
                    } else if (item.enum_() != null) {
                        symbol = enumSymbol(item.enum_());
                    } else if (item.const_() != null) {
                        symbol = constSymbol(item.const_());
                    } else if (item.function() != null) {
                        symbol = functionSymbol(item.function());
                    }
                    if (symbol != null) {
                        symbols.add(symbol);
                    }
                }
            }
        }

        return symbols;
    }

    private static void setCorrectRange(
            DocumentSymbol symbol,
            ParserRuleContext ctx,
            ParserRuleContext selectionCtx
    ) {
        symbol.setRange(getRange(ctx));
        symbol.setSelectionRange(getRange(selectionCtx));

    }

    private static @Nullable DocumentSymbol interfaceSymbol(@Nullable KarinaParser.InterfaceContext ctx) {
        if (ctx == null) {
            return null;
        }
        var id = ctx.id();
        var name = escapeID(id);
        if (name == null) {
            return null;
        }

        var symbol = new DocumentSymbol();
        symbol.setName(name);
        var genericDefString = getGenericHintDefString(ctx.genericHintDefinition());
        symbol.setDetail(genericDefString);
        symbol.setKind(SymbolKind.Interface);

        setCorrectRange(symbol, ctx, id);

        var children = new ArrayList<DocumentSymbol>();

        var consts = ctx.const_();
        if (consts != null) {
            children.addAll(consts.stream().map(DefaultDocumentSymbolProvider::constSymbol).filter(Objects::nonNull).toList());
        }
        var functions = ctx.function();
        if (functions != null) {
            children.addAll(functions.stream().map(DefaultDocumentSymbolProvider::functionSymbol).filter(Objects::nonNull).toList());
        }

        symbol.setChildren(children);

        return symbol;
    }

    private static @Nullable DocumentSymbol structSymbol(@Nullable KarinaParser.StructContext ctx) {
        if (ctx == null) {
            return null;
        }
        var id = ctx.id();
        var name = escapeID(id);
        if (name == null) {
            return null;
        }

        var symbol = new DocumentSymbol();
        symbol.setName(name);
        var genericDefString = getGenericHintDefString(ctx.genericHintDefinition());
        symbol.setDetail(genericDefString);
        symbol.setKind(SymbolKind.Struct);

        setCorrectRange(symbol, ctx, id);

        var children = new ArrayList<DocumentSymbol>();

        var consts = ctx.const_();
        if (consts != null) {
            children.addAll(consts.stream().map(DefaultDocumentSymbolProvider::constSymbol).filter(Objects::nonNull).toList());
        }
        var fields = ctx.field();
        if (fields != null) {
            children.addAll(fields.stream().map(DefaultDocumentSymbolProvider::fieldSymbol).filter(Objects::nonNull).toList());
        }
        var functions = ctx.function();
        if (functions != null) {
            children.addAll(functions.stream().map(DefaultDocumentSymbolProvider::functionSymbol).filter(Objects::nonNull).toList());
        }
        var impls = ctx.implementation();
        if (impls != null) {
            for (var impl : impls) {
                var functionsInImpl = impl.function();
                if (functionsInImpl != null) {
                    children.addAll(functionsInImpl.stream().map(DefaultDocumentSymbolProvider::functionSymbol).filter(Objects::nonNull).toList());
                }
            }
        }
        var boundWhere = ctx.boundWhere();
        if (boundWhere != null) {
            for (var bound : boundWhere) {
                var functionsInBound = bound.function();
                if (functionsInBound != null) {
                    children.addAll(functionsInBound.stream().map(DefaultDocumentSymbolProvider::functionSymbol).filter(Objects::nonNull).toList());
                }
            }
        }

        symbol.setChildren(children);

        return symbol;
    }

    private static @Nullable DocumentSymbol enumSymbol(@Nullable KarinaParser.EnumContext ctx) {
        if (ctx == null) {
            return null;
        }
        var id = ctx.id();
        var name = escapeID(id);
        if (name == null) {
            return null;
        }

        var symbol = new DocumentSymbol();
        symbol.setName(name);
        var genericDefString = getGenericHintDefString(ctx.genericHintDefinition());
        symbol.setDetail(genericDefString);
        symbol.setKind(SymbolKind.Enum);

        setCorrectRange(symbol, ctx, id);

        var children = new ArrayList<DocumentSymbol>();

        var consts = ctx.const_();
        if (consts != null) {
            children.addAll(consts.stream().map(DefaultDocumentSymbolProvider::constSymbol).filter(Objects::nonNull).toList());
        }
        var members = ctx.enumMember();
        if (members != null) {
            children.addAll(members.stream().map(DefaultDocumentSymbolProvider::enumMemberSymbol).filter(Objects::nonNull).toList());
        }
        var functions = ctx.function();
        if (functions != null) {
            children.addAll(functions.stream().map(DefaultDocumentSymbolProvider::functionSymbol).filter(Objects::nonNull).toList());
        }
        var impls = ctx.implementation();
        if (impls != null) {
            for (var impl : impls) {
                var functionsInImpl = impl.function();
                if (functionsInImpl != null) {
                    children.addAll(functionsInImpl.stream().map(DefaultDocumentSymbolProvider::functionSymbol).filter(Objects::nonNull).toList());
                }
            }
        }
        var boundWhere = ctx.boundWhere();
        if (boundWhere != null) {
            for (var bound : boundWhere) {
                var functionsInBound = bound.function();
                if (functionsInBound != null) {
                    children.addAll(functionsInBound.stream().map(DefaultDocumentSymbolProvider::functionSymbol).filter(Objects::nonNull).toList());
                }
            }
        }


        symbol.setChildren(children);

        return symbol;
    }

    private static @Nullable DocumentSymbol constSymbol(@Nullable KarinaParser.ConstContext ctx) {
        if (ctx == null) {
            return null;
        }
        var id = ctx.id();
        var name = escapeID(id);
        if (name == null) {
            return null;
        }

        var symbol = new DocumentSymbol();
        symbol.setName(name);
        if (ctx.type() != null) {
            var typeString = getReadableType(ctx.type());
            symbol.setDetail(typeString);
        }
        symbol.setKind(SymbolKind.Constant);

        setCorrectRange(symbol, ctx, id);

        return symbol;
    }

    private static @Nullable DocumentSymbol fieldSymbol(@Nullable KarinaParser.FieldContext ctx) {
        if (ctx == null) {
            return null;
        }
        var id = ctx.id();
        var name = escapeID(id);
        if (name == null) {
            return null;
        }

        var symbol = new DocumentSymbol();
        symbol.setName(name);
        if (ctx.type() != null) {
            var typeString = getReadableType(ctx.type());
            symbol.setDetail(typeString);
        }
        symbol.setKind(SymbolKind.Field);

        setCorrectRange(symbol, ctx, id);
        return symbol;
    }

    private static @Nullable DocumentSymbol enumMemberSymbol(@Nullable KarinaParser.EnumMemberContext ctx) {
        if (ctx == null) {
            return null;
        }
        var id = ctx.id();
        var name = escapeID(id);
        if (name == null) {
            return null;
        }

        var symbol = new DocumentSymbol();
        symbol.setName(name);
        symbol.setKind(SymbolKind.EnumMember);

        setCorrectRange(symbol, ctx, id);

        var children = new ArrayList<DocumentSymbol>();

        var parameterList = ctx.parameterList();
        if (parameterList != null) {
            var parameters = parameterList.parameter();
            if (parameters != null) {
                children.addAll(parameters.stream().map(DefaultDocumentSymbolProvider::paramToFieldSymbol).filter(Objects::nonNull).toList());
            }
        }

        symbol.setChildren(children);

        return symbol;
    }

    private static @Nullable DocumentSymbol functionSymbol(@Nullable KarinaParser.FunctionContext ctx) {
        if (ctx == null) {
            return null;
        }
        var id = ctx.id();
        if (id == null) {
            var symbol = new DocumentSymbol();
            symbol.setName("<init>");
            if (ctx.FN() == null) {
                return null;
            }
            var signature = getSignatureString(ctx);
            symbol.setDetail(signature);
            symbol.setKind(SymbolKind.Function);

            setCorrectRange(symbol, ctx, ctx);
            return symbol;
        }
        var name = escapeID(id);
        if (name == null) {
            return null;
        }

        var symbol = new DocumentSymbol();
        symbol.setName(name);
        var signature = getSignatureString(ctx);
        symbol.setDetail(signature);
        symbol.setKind(SymbolKind.Function);

        setCorrectRange(symbol, ctx, id);

        return symbol;
    }

    private static Range getRange(ParserRuleContext ctx) {

        var start = new Position(ctx.start.getLine() - 1, ctx.start.getCharPositionInLine());
        var end = new Position(ctx.stop.getLine() - 1, ctx.stop.getCharPositionInLine());
        return new Range(start, end);
    }

    private static @Nullable DocumentSymbol paramToFieldSymbol(@Nullable KarinaParser.ParameterContext ctx) {
        if (ctx == null) {
            return null;
        }
        var id = ctx.id();
        var name = escapeID(id);
        if (name == null) {
            return null;
        }

        var symbol = new DocumentSymbol();
        symbol.setName(name);
        if (ctx.type() != null) {
            var typeString = getReadableType(ctx.type());
            symbol.setDetail(typeString);
        }
        symbol.setKind(SymbolKind.Field);

        symbol.setRange(getRange(ctx));
        symbol.setSelectionRange(getRange(id));

        return symbol;
    }

    private static String getReadableType(@Nullable KarinaParser.TypeContext ctx) {
        if (ctx == null) {
            return "";
        }
        var inner = getReadableType(ctx.typeInner());

        if (ctx.typePostFix() != null) {
            var otherInnerCtx = ctx.typePostFix().typeInner();
            if (otherInnerCtx != null) {
                var otherInner = getReadableType(otherInnerCtx);
                return "Result<" + inner + ", " + otherInner + ">";
            } else {
                return "Option<" + inner + ">";
            }
        }

        return inner;

    }

    private static String getReadableType(@Nullable KarinaParser.TypeInnerContext ctx) {
        if (ctx == null) {
            return "";
        }
        if (ctx.VOID() != null) {
            return "void";
        } else if (ctx.INT() != null) {
            return "int";
        } else if (ctx.DOUBLE() != null) {
            return "double";
        } else if (ctx.SHORT() != null) {
            return "short";
        } else if (ctx.BYTE() != null) {
            return "byte";
        } else if (ctx.CHAR() != null) {
            return "char";
        } else if (ctx.LONG() != null) {
            return "long";
        } else if (ctx.FLOAT() != null) {
            return "float";
        } else if (ctx.BOOL() != null) {
            return "bool";
        } else if (ctx.STRING() != null) {
            return "String";
        } else if (ctx.structType() != null) {
            return getStructTypeString(ctx.structType());
        } else if (ctx.arrayType() != null) {
            var innerType = ctx.arrayType().type();
            return "[" + getReadableType(innerType) + "]";
        } else if (ctx.functionType() != null) {
            var functionType = ctx.functionType();
            var interfaceImpl = getInterfaceImplString(functionType.interfaceImpl());

            var params = List.<String>of();
            if (functionType.typeList() != null) {
                var types = functionType.typeList().type();
                if (types != null) {
                    params = types.stream().map(DefaultDocumentSymbolProvider::getReadableType).toList();
                }
            }

            var returnType = "";
            if (functionType.type() != null) {
                returnType = " -> " + getReadableType(functionType.type());
            }

            return "fn(" + String.join(", ", params) + ")" + returnType + interfaceImpl;

        } else if (ctx.type() != null) {
            return getReadableType(ctx.type());
        } else if (ctx.ANY() != null) {
            return "any";
        }
        else {
            return "";
        }
    }

    private static String getInterfaceImplString(@Nullable KarinaParser.InterfaceImplContext ctx) {
        if (ctx == null) {
            return "";
        }
        var types = ctx.structTypeList();
        if (types == null || types.structType() == null || types.structType().isEmpty()) {
            return "";
        }
        var names = types.structType().stream().map(DefaultDocumentSymbolProvider::getStructTypeString).toList();
        return " impl " + String.join(", ", names);
    }

    private static String getStructTypeString(KarinaParser.StructTypeContext ctx) {
        if (ctx == null) {
            return "";
        }
        var path = ctx.dotWordChain();
        if (path == null) {
            return "";
        }
        var ids = path.id();
        if (ids == null || ids.isEmpty()) {
            return "";
        }
        var name = escapeID(ids.getLast());
        if (name == null) {
            return "";
        }
        return name + getGenericHintString(ctx.genericHint());
    }


    private static String getSignatureString(KarinaParser.FunctionContext ctx) {
        var genericDefString = getGenericHintDefString(ctx.genericHintDefinition());

        String returnString = "void";
        if (ctx.type() != null) {
            returnString = getReadableType(ctx.type());
        }

        var params = new ArrayList<String>();
        if (ctx.selfParameterList() != null) {
            var parameterListContext = ctx.selfParameterList();
            if (parameterListContext.SELF() != null) {
                params.add("self");
            }
            var parameters = parameterListContext.parameter();
            if (parameters != null) {
                params.addAll(parameters.stream()
                                        .map(KarinaParser.ParameterContext::type)
                                        .map(DefaultDocumentSymbolProvider::getReadableType)
                                        .toList()
                );
            }
        }

        return genericDefString + "(" + String.join(", ", params) + ") -> " + returnString;
    }

    private static String getGenericHintDefString(@Nullable KarinaParser.GenericHintDefinitionContext ctx) {
        if (ctx == null) {
            return "";
        }
        var bounds = ctx.genericWithBound();
        if (bounds == null || bounds.isEmpty()) {
            return "";
        }
        var names = bounds.stream().map(ref -> escapeID(ref.id())).filter(Objects::nonNull).toList();
        return "<" + String.join(", ", names) + ">";
    }

    private static String getGenericHintString(@Nullable KarinaParser.GenericHintContext ctx) {
        if (ctx == null) {
            return "";
        }
        var ids = ctx.type();
        if (ids == null || ids.isEmpty()) {
            return "";
        }
        var names = ids.stream().map(DefaultDocumentSymbolProvider::getReadableType).toList();
        return "<" + String.join(", ", names) + ">";
    }

    private static @Nullable String escapeID(@Nullable KarinaParser.IdContext context) {
        if (context == null) {
            return null;
        }
        var str = context.getText();
        if (str == null) {
            return null;
        }
        if (str.startsWith("\\")) {
            str = str.substring(1);
        }
        if (str.isEmpty()) {
            return null;
        }
        return str;
    }


}

