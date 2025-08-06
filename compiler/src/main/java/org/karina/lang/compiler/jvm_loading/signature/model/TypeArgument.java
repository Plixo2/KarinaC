package org.karina.lang.compiler.jvm_loading.signature.model;

public sealed interface TypeArgument {

    record Extends(TypeSignature signature) implements TypeArgument {}
    record Super(TypeSignature signature) implements TypeArgument {}
    record Base(TypeSignature signature) implements TypeArgument {}
    record Any() implements TypeArgument {}

}
