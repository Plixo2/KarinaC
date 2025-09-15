package org.karina.lang.compiler.utils.logging;



import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.utils.Context;

import java.io.PrintStream;

public class FlightRecordCollection {
    @Getter
    @Accessors(fluent = true)
    public @Nullable Context.LoggingContext rootSection;

    public void set(@NotNull Context.LoggingContext rootSection) {
        this.rootSection = rootSection;
    }

    public static void print(FlightRecordCollection collection, boolean addTrace, PrintStream stream) {
        if (collection.rootSection == null) {
            return;
        }
        printSection(collection.rootSection, 0, addTrace, false, stream);
    }

    private static void printSection(Context.LoggingContext section, int depth, boolean addTrace, boolean color, PrintStream stream) {
        var prefixStr = "|  ".repeat(depth);
        stream.print(prefixStr + section.name() + " ");

        var suffix = "";
        var baseOffset = 3;
        var offset = baseOffset + section.traceOffset();
        if (section.stackTrace().length > offset && addTrace) {
            var stackTraceElement = section.stackTrace()[offset];
            suffix = " at " + getTrace(stackTraceElement);
        }

        if (color) {
            Colored.begin(ConsoleColor.YELLOW)
                   .append(section.getTimeString())
                   .print(stream);
        } else {
            stream.print(section.getTimeString());
        }
        stream.println(suffix);


        for (var entry : section.entries()) {
            switch (entry) {
                case Context.LoggingContext.Entry.SubSection(var subSection) -> {
                    printSection(subSection, depth + 1, addTrace, color, stream);
                }
                case Context.LoggingContext.Entry.Tag(var text, var trace, var error) -> {
                    var childPrefix = ">  ".repeat(depth + 1);
                    var childSuffix = "";
                    var tagStackOffset = 3;
                    if (error) {
                        tagStackOffset = 0; // point straight to the error
                    }
                    if (trace.length > tagStackOffset && addTrace) {
                        var stackTraceElement = trace[tagStackOffset];
                        childSuffix = " at " + getTrace(stackTraceElement);
                    }
                    if (color) {
                        if (error) {
                            Colored.begin(ConsoleColor.RED)
                                   .append(childPrefix + text)
                                   .print(stream);
                        } else {
                            Colored.begin(ConsoleColor.CYAN)
                                   .append(childPrefix + text)
                                   .print(stream);
                        }

                    } else {
                        stream.print(childPrefix + text);
                    }
                    stream.println(childSuffix);
                }
            }
        }
    }

    public static void printColored(FlightRecordCollection collection, boolean addTrace, PrintStream stream) {
        if (collection.rootSection == null) {
            return;
        }
        printSection(collection.rootSection, 0, addTrace, true, stream);
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

}
