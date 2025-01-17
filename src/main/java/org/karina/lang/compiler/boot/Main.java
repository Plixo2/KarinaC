package org.karina.lang.compiler.boot;


import org.apache.commons.cli.ParseException;
import org.karina.lang.compiler.api.*;
import org.karina.lang.compiler.api.config.Configuration;
import org.karina.lang.compiler.api.config.ConfigurationLoader;
import org.karina.lang.compiler.api.config.ConfigurationParseException;
import org.karina.lang.compiler.backend.interpreter.InterpreterBackend;
import org.karina.lang.compiler.backend.jvm.BytecodeBackend;
import org.karina.lang.compiler.backend.jvm.JarCompilation;
import org.karina.lang.interpreter.Interpreter;
import org.karina.lang.interpreter.SimpleLibrary;

import java.io.IOException;
import java.util.List;


public class Main {

    public static void main(String[] args) throws ConfigurationParseException, IOException {
        var configuration = ConfigurationLoader.fromCommandLineArgs(args);

        var bootstrap = new KarinaBootstrap();
        bootstrap.enablePrintWelcome();
        bootstrap.enablePrintTree();
        bootstrap.enablePrintResult();
        bootstrap.setPrintStream(System.out);
        bootstrap.setErrPrintStream(System.err);

        var code = bootstrap.run(configuration);
        System.exit(code);
    }


}
