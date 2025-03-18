package org.karina.lang.compiler.jvm.model;

public enum PhaseDebug {
    JVM, //loaded by the jvm, so already typed
    LOADED, //first loaded
    IMPORTED, //everything imported
    TYPED //everything typed
}
