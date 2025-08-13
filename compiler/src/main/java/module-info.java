module compiler {
    requires com.google.common;
    requires com.google.errorprone.annotations;
    requires com.google.gson;
    requires jdk.jfr;
    requires static lombok;
    requires org.antlr.antlr4.runtime;
    requires org.apache.commons.lang3;
    requires org.apache.commons.text;
    requires org.checkerframework.checker.qual;
    requires org.jetbrains.annotations;
    requires org.objectweb.asm;
    requires org.objectweb.asm.tree;
    requires java.desktop;
    requires java.sql;
    requires org.objectweb.asm.util;


    exports org.karina.lang.compiler;

    // lsp
    exports org.karina.lang.compiler.utils;
    exports org.karina.lang.compiler.utils.annotations;
    exports org.karina.lang.compiler.utils.symbols;
    exports org.karina.lang.compiler.stages.attrib;
    exports org.karina.lang.compiler.stages.attrib.expr;
    exports org.karina.lang.compiler.stages.imports;
    exports org.karina.lang.compiler.stages.imports.table;
    exports org.karina.lang.compiler.stages.parser;
    exports org.karina.lang.compiler.stages.parser.error;
    exports org.karina.lang.compiler.stages.parser.gen;
    exports org.karina.lang.compiler.stages.parser.visitor;
    exports org.karina.lang.compiler.stages.parser.visitor.model;
    exports org.karina.lang.compiler.stages.lower;
    exports org.karina.lang.compiler.stages.lower.special;
    exports org.karina.lang.compiler.jvm_loading.signature;
    exports org.karina.lang.compiler.jvm_loading.signature.model;
    exports org.karina.lang.compiler.jvm_loading.signature.gen;
    exports org.karina.lang.compiler.jvm_loading.loading;
    exports org.karina.lang.compiler.jvm_loading.binary;
    exports org.karina.lang.compiler.jvm_loading.binary.in;
    exports org.karina.lang.compiler.jvm_loading.binary.out;
    exports org.karina.lang.compiler.logging;
    exports org.karina.lang.compiler.logging.errors;
    exports org.karina.lang.compiler.model_api;
    exports org.karina.lang.compiler.model_api.pointer;
    exports org.karina.lang.compiler.model_api.impl.karina;
    exports org.karina.lang.compiler.model_api.impl.table;
    exports org.karina.lang.compiler.model_api.impl.jvm;
    exports org.karina.lang.compiler.model_api.impl;


}