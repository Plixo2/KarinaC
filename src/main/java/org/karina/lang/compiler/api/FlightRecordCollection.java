package org.karina.lang.compiler.api;

import org.jetbrains.annotations.NotNull;
import org.karina.lang.compiler.logging.FlightRecorder;

import java.io.PrintStream;
import java.util.*;
import java.util.regex.Pattern;

public class FlightRecordCollection implements Iterable<FlightRecorder.SectionRecord> {
    private List<FlightRecorder.SectionRecord> records = new ArrayList<>();

    public FlightRecordCollection() {
    }

    @Override
    public @NotNull Iterator<FlightRecorder.SectionRecord> iterator() {
        return this.records.iterator();
    }

    public void add(FlightRecorder.SectionRecord record) {
        this.records.add(record);
        record.subSections().forEach(this::add);
    }

    public static void print(FlightRecordCollection collection, boolean verbose, PrintStream stream) {
        for (var log : collection) {
            if (log.hide) {
                continue;
            }
            stream.println(log.mkString(verbose));
        }
    }

    public static void printColored(FlightRecordCollection collection, boolean verbose, PrintStream stream) {
        var name = "\u001B[37m";
        var time = "\u001B[33m";
        var end = "\u001B[0m";

        var colorFormatter = new FlightRecorder.ColorFormatter();
        colorFormatter.addCustomString("true", "\u001B[32mtrue\u001B[37m");
        colorFormatter.addCustomString("false", "\u001B[31mfalse\u001B[37m");
        colorFormatter.addCustomString(".", "\u001B[34m.\u001B[37m");
        colorFormatter.addCustomString(",", "\u001B[34m,\u001B[37m");
        colorFormatter.addCustomString("(", "\u001B[34m(\u001B[37m");
        colorFormatter.addCustomString("{", "\u001B[34m{\u001B[37m");
        colorFormatter.addCustomString(")", "\u001B[34m)\u001B[37m");
        colorFormatter.addCustomString("}", "\u001B[34m}\u001B[37m");
        colorFormatter.addCustomString(">", "\u001B[34m>\u001B[37m");
        colorFormatter.addCustomString("<", "\u001B[34m<\u001B[37m");
        colorFormatter.addCustomString("-", "\u001B[34m-\u001B[37m");
        colorFormatter.addCustomString("+", "\u001B[34m+\u001B[37m");
        colorFormatter.addCustomString(":", "\u001B[34m:\u001B[37m");
        colorFormatter.addCustomString("error", "\u001B[31merror\u001B[37m");
        colorFormatter.addCustomString("ok", "\u001B[32mok\u001B[37m");
        colorFormatter.addCustomString("success", "\u001B[32msuccess\u001B[37m");
        colorFormatter.addCustomString("warn", "\u001B[31mwarn\u001B[37m");
        colorFormatter.addCustomString("warning", "\u001B[31mwarning\u001B[37m");

        for (var log : collection) {
            if (log.hide) {
                continue;
            }
            stream.println(log.mkString(verbose, name, time, end, colorFormatter));
        }
    }

}
