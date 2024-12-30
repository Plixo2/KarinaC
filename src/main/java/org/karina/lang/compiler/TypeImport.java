package org.karina.lang.compiler;

public sealed interface TypeImport {
    Span region();

    record All(Span region) implements TypeImport {
    }

    record Single(SpanOf<String> name) implements TypeImport {
        @Override
        public Span region() {
            return this.name.region();
        }
    }

    record JavaClass(Span region) implements TypeImport {
    }

    record JavaAlias(SpanOf<String> alias) implements TypeImport {
        @Override
        public Span region() {
            return this.alias.region();
        }
    }
}
