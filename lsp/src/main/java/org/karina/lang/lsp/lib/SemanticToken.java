package org.karina.lang.lsp.lib;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public interface SemanticToken {


    int NAMESPACE             = SemanticTokenType.NAMESPACE_IMPL.ordinal();
    int TYPE                  = SemanticTokenType.TYPE_IMPL.ordinal();
    int CLASS                 = SemanticTokenType.CLASS_IMPL.ordinal();
    int ENUM                  = SemanticTokenType.ENUM_IMPL.ordinal();
    int INTERFACE             = SemanticTokenType.INTERFACE_IMPL.ordinal();
    int STRUCT                = SemanticTokenType.STRUCT_IMPL.ordinal();
    int TYPE_PARAMETER        = SemanticTokenType.TYPE_PARAMETER_IMPL.ordinal();
    int PARAMETER             = SemanticTokenType.PARAMETER_IMPL.ordinal();
    int VARIABLE              = SemanticTokenType.VARIABLE_IMPL.ordinal();
    int PROPERTY              = SemanticTokenType.PROPERTY_IMPL.ordinal();
    int ENUM_MEMBER           = SemanticTokenType.ENUM_MEMBER_IMPL.ordinal();
    int EVENT                 = SemanticTokenType.EVENT_IMPL.ordinal();
    int FUNCTION              = SemanticTokenType.FUNCTION_IMPL.ordinal();
    int METHOD                = SemanticTokenType.METHOD_IMPL.ordinal();
    int MACRO                 = SemanticTokenType.MACRO_IMPL.ordinal();
    int KEYWORD               = SemanticTokenType.KEYWORD_IMPL.ordinal();
    int MODIFIER              = SemanticTokenType.MODIFIER_IMPL.ordinal();
    int COMMENT               = SemanticTokenType.COMMENT_IMPL.ordinal();
    int STRING                = SemanticTokenType.STRING_IMPL.ordinal();
    int NUMBER                = SemanticTokenType.NUMBER_IMPL.ordinal();
    int REGEXP                = SemanticTokenType.REGEXP_IMPL.ordinal();
    int OPERATOR              = SemanticTokenType.OPERATOR_IMPL.ordinal();
    int DECORATOR             = SemanticTokenType.DECORATOR_IMPL.ordinal();

    int MOD_DECLARATION       = 1 << SemanticTokenModifier.DECLARATION_IMPL.ordinal();
    int MOD_DEFINITION        = 1 << SemanticTokenModifier.DEFINITION_IMPL.ordinal();
    int MOD_READONLY          = 1 << SemanticTokenModifier.READONLY_IMPL.ordinal();
    int MOD_STATIC            = 1 << SemanticTokenModifier.STATIC_IMPL.ordinal();
    int MOD_DEPRECATED        = 1 << SemanticTokenModifier.DEPRECATED_IMPL.ordinal();
    int MOD_ABSTRACT          = 1 << SemanticTokenModifier.ABSTRACT_IMPL.ordinal();
    int MOD_ASYNC             = 1 << SemanticTokenModifier.ASYNC_IMPL.ordinal();
    int MOD_MODIFICATION      = 1 << SemanticTokenModifier.MODIFICATION_IMPL.ordinal();
    int MOD_DOCUMENTATION     = 1 << SemanticTokenModifier.DOCUMENTATION_IMPL.ordinal();
    int MOD_DEFAULT_LIBRARY   = 1 << SemanticTokenModifier.DEFAULT_LIBRARY_IMPL.ordinal();

    @RequiredArgsConstructor
    enum SemanticTokenType {

        NAMESPACE_IMPL("namespace"),
        TYPE_IMPL("type"),
        CLASS_IMPL("class"),
        ENUM_IMPL("enum"),
        INTERFACE_IMPL("interface"),
        STRUCT_IMPL("struct"),
        TYPE_PARAMETER_IMPL("typeParameter"),
        PARAMETER_IMPL("parameter"),
        VARIABLE_IMPL("variable"),
        PROPERTY_IMPL("property"),
        ENUM_MEMBER_IMPL("enumMember"),
        EVENT_IMPL("event"),
        FUNCTION_IMPL("function"),
        METHOD_IMPL("method"),
        MACRO_IMPL("macro"),
        KEYWORD_IMPL("keyword"),
        MODIFIER_IMPL("modifier"),
        COMMENT_IMPL("comment"),
        STRING_IMPL("string"),
        NUMBER_IMPL("number"),
        REGEXP_IMPL("regexp"),
        OPERATOR_IMPL("operator"),
        DECORATOR_IMPL("decorator"),

        ;
        final String value;

        public static List<String> names() {
            var names = new ArrayList<String>();
            for (var type : values()) {
                names.add(type.value);
            }
            return names;
        }
    }

    @RequiredArgsConstructor
    enum SemanticTokenModifier {
        DECLARATION_IMPL("declaration"),
        DEFINITION_IMPL("definition"),
        READONLY_IMPL("readonly"),
        STATIC_IMPL("static"),
        DEPRECATED_IMPL("deprecated"),
        ABSTRACT_IMPL("abstract"),
        ASYNC_IMPL("async"),
        MODIFICATION_IMPL("modification"),
        DOCUMENTATION_IMPL("documentation"),
        DEFAULT_LIBRARY_IMPL("defaultLibrary"),
        ;

        final String value;

        public static List<String> names() {
            var names = new ArrayList<String>();
            for (var modifier : values()) {
                names.add(modifier.value);
            }
            return names;
        }
    }
}
