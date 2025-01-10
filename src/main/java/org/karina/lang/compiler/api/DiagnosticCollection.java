package org.karina.lang.compiler.api;

import org.jetbrains.annotations.NotNull;
import org.karina.lang.compiler.errors.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class DiagnosticCollection implements Iterable<Log.LogWithTrace> {
    private final List<Log.LogWithTrace> traces = new ArrayList<>();

    public static void printDiagnostic(DiagnosticCollection collection, boolean printVerbose) {
            System.out.println("\u001B[31mCompilation failed\u001B[0m");
            System.out.flush();
            System.err.println();
            for (var log : collection) {
                System.err.println(log.mkString(printVerbose));
            }
            System.err.flush();
    }

    public void addAll(Collection<Log.LogWithTrace> logs) {
        this.traces.addAll(logs);
    }

    public void add(Log.LogWithTrace log) {
        this.traces.add(log);
    }

    public List<Log.LogWithTrace> getTraces() {
        return new ArrayList<>(this.traces);
    }

    @Override
    public @NotNull Iterator<Log.LogWithTrace> iterator() {
        return this.traces.iterator();
    }
}
