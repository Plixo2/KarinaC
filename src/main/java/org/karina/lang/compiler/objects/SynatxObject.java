package org.karina.lang.compiler.objects;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.Span;
import org.karina.lang.compiler.SpanOf;

import java.util.List;

public class SynatxObject {

    public enum BinaryOperator {
        ADD,
        SUBTRACT,
        MULTIPLY,
        DIVIDE,
        MODULUS,
        BIN_AND,
        EQUAL,
        NOT_EQUAL,
        LESS_THAN,
        LESS_THAN_OR_EQUAL,
        GREATER_THAN,
        GREATER_THAN_OR_EQUAL,
        AND,
        OR,
    }

    public enum UnaryOperator {
        NEGATE,
        NOT,
    }

    public sealed interface BranchPattern {
        KType type();

        record Cast(KType type, SpanOf<String> castedName) implements BranchPattern { }
        record Destruct(KType type, List<NameAndOptType> variables) implements BranchPattern { }
    }

    public sealed interface TypeImport {
        Span region();

        record All(Span region) implements TypeImport {}

        record Single(SpanOf<String> name) implements TypeImport {
            @Override
            public Span region() {
                return this.name.region();
            }
        }

        record JavaClass(Span region) implements TypeImport {}

        record JavaAlias(SpanOf<String> alias) implements TypeImport {
            @Override
            public Span region() {
                return this.alias.region();
            }
        }
    }
    public record NamedExpression(Span region, SpanOf<String> name, KExpr expr) { }
    public record NameAndOptType(Span region, SpanOf<String> name, @Nullable KType type) {}

    public sealed interface MatchPattern {
        KExpr expr();
        Span region();

        record Default(Span region, KExpr expr) implements MatchPattern { }
        record Cast(Span region, KType type, SpanOf<String> name, KExpr expr) implements MatchPattern { }
        record Destruct(Span region, KType type, List<NameAndOptType> variables, KExpr expr) implements MatchPattern { }
    }
}
