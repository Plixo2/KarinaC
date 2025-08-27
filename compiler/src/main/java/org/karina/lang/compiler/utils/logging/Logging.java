package org.karina.lang.compiler.utils.logging;


public interface Logging {

    class Parsing implements Logging {}
    class Merging implements Logging {}
    class Importing implements Logging {}
    class Attribution implements Logging {}
    class Lowering implements Logging {}
    class Generation implements Logging {}
    class Writing implements Logging {}

    class ReadJar implements Logging {}
    class BinaryFile implements Logging {}
    class Forks implements Logging {}

    class ReadBinary implements Logging {}
}
