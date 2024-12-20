package org.karina.lang.compiler;

import org.jetbrains.annotations.NotNull;
import org.karina.lang.compiler.errors.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class DiagnosticCollection implements Iterable<Log.LogWithTrace> {
    private final List<Log.LogWithTrace> traces = new ArrayList<>();

    public void addAll(Collection<Log.LogWithTrace> logs) {
        this.traces.addAll(logs);
    }

    public void add(Log.LogWithTrace log) {
        this.traces.add(log);
    }

    public List<Log.LogWithTrace> getTraces() {
        return this.traces;
    }

    @Override
    public @NotNull Iterator<Log.LogWithTrace> iterator() {
        return this.traces.iterator();
    }
}
