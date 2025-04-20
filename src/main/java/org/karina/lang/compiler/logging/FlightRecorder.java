package org.karina.lang.compiler.logging;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Simple FlightRecorder to measure the time taken by different sections of code
 * Use <code>Log.begin(..)</code> to start a section and <code>Log.end()</code> to end it.
 * A section also stores the stack trace of where it was started, so it can be used to debug
 */
public class FlightRecorder {
    SectionRecord topLevel = null;
    private SectionRecord currentSection = null;


    public SectionRecord begin(String name) {
        var stackTrace = Thread.currentThread().getStackTrace();
        if (this.topLevel == null) {
            this.topLevel = new SectionRecord(name, stackTrace, null, System.nanoTime(), 0);
            this.currentSection = this.topLevel;
            return this.currentSection;
        }
        int depth = 0;
        if (this.currentSection != null) {
            depth = this.currentSection.depth + 1;
        }

        return this.currentSection = new SectionRecord(name, stackTrace, this.currentSection, System.nanoTime(), depth);
    }



    /*
     * The name isn't necessary, but it can be used to check if the section is being closed correctly
     */
    public void end(String name) {
        if (this.currentSection == null) {
            return;
        }
        var current = this.currentSection;
        if (current.name.equals(name)) {
            current.endTimeNano = System.nanoTime();
        } else {
//            Log.warn("Ending section " + name + " but current section is " + current.name);
        }

        this.currentSection = current.parent;

    }

    public Sample beginSample(String name) {
        return new Sample(name, System.nanoTime(), this.currentSection);
    }
    public Sample beginSuperSample(String name) {
        return new Sample(name, System.nanoTime(), this.topLevel);
    }

    public void endAll() {
        while (this.currentSection != null) {
            this.end(this.currentSection.name);
        }
    }

    public void clear() {
        this.currentSection = null;
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
        //Samples with name, deltaTime in NS
        private final Map<String, Long> samples;

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
            this.samples = new HashMap<>();
        }

        public boolean skipSample() {
            return this.samples.isEmpty();
        }

        public String mkSampleStr() {
            return mkSampleStr("", "");
        }

        public String mkSampleStr(String nameFormat, String timeFormat) {
            var indent = "|  ".repeat(this.depth + 1);

            var sampleStr = new StringBuilder();

            var sampleIter = this.samples.entrySet().iterator();

            while (sampleIter.hasNext()) {
                var entry = sampleIter.next();
                var deltaMS = entry.getValue() / 1_000_000.0f;
                sampleStr.append(nameFormat).append(entry.getKey()).append(": ").append(timeFormat).append(deltaMS).append("ms");
                if (sampleIter.hasNext()) {
                    sampleStr.append(", ");
                }
            }

            return nameFormat + indent + "[" + sampleStr + "]";

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


        private void addSample(String name, long deltaNS) {
            this.samples.merge(name, deltaNS, Long::sum);
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


    public static class Sample {
        public String name;
        private final long startTimeNano;
        private long endTimeNano;
        private final @Nullable SectionRecord parent;

        public Sample(String name, long startTimeNano, @Nullable SectionRecord parent) {
            this.name = name;
            this.startTimeNano = startTimeNano;
            this.parent = parent;
        }

        public void endSample() {
            this.endTimeNano = System.nanoTime();

            var deltaNS = (this.endTimeNano - this.startTimeNano);
            if (this.parent != null) {
                this.parent.addSample(this.name, deltaNS);
            }
        }
    }

}
