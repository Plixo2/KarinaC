package org.karina.lang.compiler.utils.logging;


public sealed interface Logging {

    final class Parsing implements Logging {
        public static final class ParseUnit implements Logging {}
    }
    final class Merging implements Logging {}
    final class Importing implements Logging {
        public static final class Classes implements Logging {}
        public static final class ClassesSecondPass implements Logging {}
    }
    final class Attribution implements Logging {
        public static final class Classes implements Logging {}
    }
    final class Lowering implements Logging {
        public static final class Classes implements Logging {}
    }
    final class Generation implements Logging {
        public static final class Classes implements Logging {}
        public static final class InnerClassesNodes implements Logging {}
    }
    final class Writing implements Logging {}

    final class ReadJar implements Logging {}
    final class BinaryFile implements Logging {}
    final class Forks implements Logging {}

    final class ReadBinary implements Logging {}

    final class MethodAttribution implements Logging {}
    final class TypeChecking implements Logging {}
    final class Expression implements Logging {}
}
