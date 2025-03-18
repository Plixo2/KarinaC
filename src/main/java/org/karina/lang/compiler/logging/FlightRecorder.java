package org.karina.lang.compiler.logging;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple FlightRecorder to measure the time taken by different sections of code
 * Use <code>Log.begin(..)</code> to start a section and <code>Log.end()</code> to end it.
 * A section also stores the stack trace of where it was started, so it can be used to debug
 */
public class FlightRecorder {
    SectionRecord topLevel = null;
    private SectionRecord top = null;


    public SectionRecord begin(String name) {
        var stackTrace = Thread.currentThread().getStackTrace();
        if (this.topLevel == null) {
            this.topLevel = new SectionRecord(name, stackTrace, null, System.nanoTime(), 0);
            this.top = this.topLevel;
            return this.top;
        }
        int depth = 0;
        if (this.top != null) {
            depth = this.top.depth + 1;
        }

        return this.top = new SectionRecord(name, stackTrace, this.top, System.nanoTime(), depth);
    }



    /*
     * The name isn't necessary, but it can be used to check if the section is being closed correctly
     */
    public void end(String name) {
        if (this.top == null) {
            return;
        }
        var current = this.top;
        if (current.name.equals(name)) {
            current.endTimeNano = System.nanoTime();
        } else {
          //  Log.warn("Ending section " + name + " but current section is " + current.name);
        }

        this.top = current.parent;

    }

    public void endAll() {
        while (this.top != null) {
            this.end(this.top.name);
        }
    }

    public void clear() {
        this.top = null;
        this.topLevel = null;
    }

    public static class SectionRecord {
        private final StackTraceElement[] stackTrace;
        @Getter
        @Accessors(fluent = true)
        private final String name;
        private final @Nullable SectionRecord parent;
        @Getter
        @Accessors(fluent = true)
        private final List<SectionRecord> subSections;
        private final long startTimeNano;
        private long endTimeNano;
        private final int depth;
        public boolean includeTime = true;
        public boolean hide = false;

        public SectionRecord(String name, StackTraceElement[] stackTrace, @Nullable SectionRecord parent, long startTimeNano, int depth) {
            this.parent = parent;
            if (parent != null) {
                parent.subSections.add(this);
            }
            this.stackTrace = stackTrace;
            this.name = name;
            this.startTimeNano = startTimeNano;
            this.subSections = new ArrayList<>();
            this.depth = depth;
        }

        public String mkString(boolean verbose) {
           return mkString(verbose, "", "", "", null);
        }

        public String mkString(boolean verbose, String nameFormat, String timeFormat, String escape, @Nullable AbstractFormatter colorFormatter) {
            var indent = "|  ".repeat(this.depth);
            var endTime = this.endTimeNano;
            var duration = (endTime - this.startTimeNano) / 1_000_000.0f;

            var suffix = escape;
            if (this.stackTrace.length >= 4 && verbose) {
                var stackTraceElement = this.stackTrace[3];
                suffix = escape + " at " + getTrace(stackTraceElement);
            }
            var nameStr = this.name;
            if (colorFormatter != null) {
                nameStr = colorFormatter.format(nameStr);
            }

            if (endTime == 0 || !this.includeTime) {
                return String.format("%s%s%s%s", nameFormat, indent, nameStr, suffix);
            } else {
                return String.format("%s%s%s: %s%.2fms%s", nameFormat, indent, nameStr, timeFormat, duration , suffix);
            }
        }
    }

    private static String getTrace(StackTraceElement stackTrace) {
        var sb = new StringBuilder();
        var path = stackTrace.getClassName().split("\\.");
        var errorClass = path[path.length - 1];
        sb.append(errorClass).append('.').append(stackTrace.getMethodName()).append('(');
        sb.append(stackTrace.getFileName());
        if (stackTrace.getLineNumber() >= 0) {
            sb.append(':').append(stackTrace.getLineNumber());
        }
        sb.append(')');

        return sb.toString();
    }

    public abstract static class AbstractFormatter {
        public abstract String format(String element);
    }

    public static class ColorFormatter extends AbstractFormatter {
        private final Map<String, String> customStrings = new HashMap<>();

        public void addCustomString(String original, String formatted) {
            this.customStrings.put(original, formatted);
        }


        @Override
        public String format(String element) {
            var value = element;
            for (var stringStringEntry : this.customStrings.entrySet()) {
                value = value.replace(stringStringEntry.getKey(), stringStringEntry.getValue());
            }
            return value;
        }
    }


}
