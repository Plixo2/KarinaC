package org.karina.lang.compiler;


import org.apache.commons.cli.ParseException;


public class Main {
    public static void main(String[] args) throws ParseException {
        var bootstrap = new KarinaCBootstrap();
        CompileConfig config = CompileConfig.parseArgs(args);
        bootstrap.compile(config, System.err);
    }
}
