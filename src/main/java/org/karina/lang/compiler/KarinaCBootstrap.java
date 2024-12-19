package org.karina.lang.compiler;

import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.errors.LogCollector;

import java.io.PrintStream;

/**
 * Interface for the Karina compiler.
 * Handles errors and other can parse command line arguments
 */
public class KarinaCBootstrap {

    public void compile(CompileConfig config, PrintStream err) {
        try {
            var karina = new KarinaC();
            karina.compile(config);

            if (Log.hasErrors()) {
                err.println("Errors in log, this should not happen");
            }
        } catch (Log.KarinaException ignored) {
            handleErrors(config.printVerbose, err);
        } finally {
            Log.clearLogs();
        }
    }

    private void handleErrors(boolean printVerbose, PrintStream out) {
        if (!Log.hasErrors()) {
            out.println("An exception was thrown, but no errors were logged");
        } else {
            var report = Log.generateReport(printVerbose);
            out.println("  ");
            out.println(LogCollector.consoleString(report));
            out.println("  ");
        }
        out.flush();
    }

}
