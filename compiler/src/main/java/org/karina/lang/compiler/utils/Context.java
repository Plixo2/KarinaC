package org.karina.lang.compiler.utils;

import com.google.common.collect.ImmutableList;
import com.google.errorprone.annotations.CheckReturnValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.utils.logging.DiagnosticCollection;
import org.karina.lang.compiler.utils.logging.FlightRecordCollection;
import org.karina.lang.compiler.utils.logging.Log;
import org.karina.lang.compiler.utils.logging.Logging;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;


///
/// Local Context
/// Context is always referred to as 'c' in the codebase
///
/// The fork method should be used in a try-with-resources block.
/// The {@link Collector#collect} method on the {@link Collector} is thread-safe.
///
/// ```
/// try(var collector = context.fork()) {
///     List.of(1, 2, 3).forEach(i -> {
///         collector.collect((ctx) -> i);
///     });
///     var result = collector.dispatch(); // or collector.dispatchParallel();
/// }
/// ```
///
///
public final class Context implements IntoContext {

    /// A <b>non-thread-safe</b> List of logs
    /// Threading should be done with the  {@link #fork} method and {@link Collector} class
    private final List<Log.LogWithTrace> errors = new ArrayList<>();
    private final List<Log.LogWithTrace> warnings = new ArrayList<>();

    private final Stack<LoggingContext> sections = new Stack<>();

    @Getter
    @Accessors(fluent = true)
    private final ContextHandling infos;


    private Context(String name, ContextHandling infos, int traceOffset) {
        this.infos = infos;
        this.sections.push(new LoggingContext(name, traceOffset));
    }


    /// <b>not thread-safe</b>
    private synchronized void mergeUp(Context context) {
        this.errors.addAll(context.errors);
        this.warnings.addAll(context.warnings);


        if (!this.infos.traces()) {
            return;
        }
        context.ensureSectionStackNotEmpty();
        var lastSection = context.sections.peek();
        // close() removes it from the stack
        lastSection.close();
        if (!context.sections.isEmpty()) {
            Log.internal(this, new IllegalStateException(
                    "Sections stack is not empty, this should not happen. Some sections were not closed properly."
            ));
        }

        ensureSectionStackNotEmpty();
        var top = this.sections.peek();

        if (this.log(Logging.Forks.class)) {
            top.entries.add(new LoggingContext.Entry.SubSection(lastSection));
        } else {
            top.entries.addAll(lastSection.entries);
        }
    }

    /// Fork a new context
    /// ```
    /// try(var collector = context.fork()) {
    ///     List.of(1, 2, 3).forEach(i -> {
    ///         collector.collect((ctx) -> i);
    ///     });
    ///     var result = collector.dispatch(); // or collector.dispatchParallel();
    /// }
    /// ```
    public <T> Collector<T> fork() {
        return this.new Collector<>();
    }

    public Context uncheckedSubContext() {
        return new Context("unchecked-fork", this.infos, 1);
    }

    public boolean log(Class<? extends Logging> type) {
        if (!this.infos.traces()) {
            return false;
        }
        if (type == Logging.Forks.class) {
            return false;
        }

        return true;
    }

    public void tag(String text, Object... args) {
        if (!this.infos.traces()) {
            return;
        }

        String formattedText;
        if (args == null || args.length == 0) {
            formattedText = text;
        } else {
            var formattedArgs = Arrays.stream(args).map(Context::objectToString).collect(Collectors.joining());
            formattedText = text + ": " + formattedArgs;
        }

        this.ensureSectionStackNotEmpty();
        var top = this.sections.peek();
        var trace = Thread.currentThread().getStackTrace();
        top.entries.add(new LoggingContext.Entry.Tag(formattedText, trace, false));
    }


    public @Nullable OpenSection section(Class<? extends Logging> type, String name) {
        if (!this.log(type)) {
            return null;
        }
        var subLoggingContext = this.new LoggingContext(name, 0);
        this.ensureSectionStackNotEmpty();
        var top = this.sections.peek();
        top.entries.add(new LoggingContext.Entry.SubSection(subLoggingContext));
        this.sections.push(subLoggingContext);
        return new OpenSection(subLoggingContext);
    }

    public void ensureSectionStackNotEmpty() {
        if (this.sections.isEmpty()) {
            Log.internal(this, new IllegalStateException(
                    "Sections stack is empty, this should not happen. Some sections were not closed properly."
            ));
            throw new Log.KarinaException();
        }
    }

    public void addError(Log.LogWithTrace log) {
        this.errors.add(log);
    }

    public void addWarn(Log.LogWithTrace log) {
        this.warnings.add(log);
    }

    public boolean hasErrors() {
        return !this.errors.isEmpty();
    }

    @Override
    public Context intoContext() {
        return this;
    }

    private static String objectToString(Object obj) {
        if (obj == null) {
            return "null";
        }
        if (obj instanceof Region region) {
            region = region.reorder();
            var path = region.source().resource().identifier();
            var column = region.start().column() + 1;
            var line = region.start().line() + 1;
            return "region: " + path + ":" + line + ":" + column;
        }
        return obj.toString();
    }


    public class Collector<T> implements AutoCloseable {
        private final AtomicBoolean dispatched = new AtomicBoolean(false);

        private final CopyOnWriteArrayList<Function<Context, T>> functions = new CopyOnWriteArrayList<>();

        public void collect(Function<Context, T> runnable) {
            this.functions.add(runnable);
        }

        ///
        /// <b>parallel</b> dispatch of the collected functions.
        /// If any function throws a {@link Log.KarinaException}, it will be caught and rethrown after merging contexts.
        /// @return a list of results in the order they were collected, when no function throws an exception.
        @CheckReturnValue
        public List<T> dispatchParallel() {
            if (!Context.this.infos.threading()) {
                return dispatchWrapper();
            }

            if (this.dispatched.getAndSet(true)) {
                Log.internal(Context.this, new IllegalStateException("dispatch() already called on this collector"));
                throw new Log.KarinaException();
            }
            var error = new AtomicReference<Log.KarinaException>(null);

            record ParallelEntry<T>(@Nullable T t, Context context) {}
            List<ParallelEntry<T>> results = new ArrayList<>();
            List<Future<ParallelEntry<T>>> futures = new ArrayList<>();
            var availableProcessors = Runtime.getRuntime().availableProcessors();
            try (var executor = Executors.newFixedThreadPool(availableProcessors)) {
                for (var function : this.functions) {
                    futures.add(executor.submit(() -> {
                        var context = new Context("fork-parallel", Context.this.infos, 2);
                        try {
                            var result = function.apply(context);
                            return new ParallelEntry<>(result, context);
                        } catch (Log.KarinaException e) {
                            if (!Context.this.infos.allowMultipleErrors()) {
                                Context.this.mergeUp(context);
                                throw e;
                            }
                            error.set(e);
                            return new ParallelEntry<>(null, context);
                        }
                    }));
                }

                for (var future : futures) {
                    results.add(future.get());
                }

            } catch (ExecutionException e) {
                if (e.getCause() instanceof Log.KarinaException karinaException) {
                    throw karinaException; // rethrow the KarinaException
                } else {
                    Log.internal(Context.this, e);
                    throw new Log.KarinaException();
                }
            } catch (InterruptedException e) {
                Log.internal(Context.this, e);
                throw new Log.KarinaException();
            }

            for (var result : results) {
                Context.this.mergeUp(result.context);
            }

            var exception = error.get();
            if (exception != null) {
                throw exception;
            }

            //should not contain nulls
            return results.stream().map(ParallelEntry::t).toList();
        }

        ///
        /// <b>non-parallel</b> dispatch of the collected functions.
        /// If any function throws a {@link Log.KarinaException}, it will be caught and rethrown after merging contexts.
        /// @return a list of results in the order they were collected, when no function throws an exception.
        @CheckReturnValue
        public List<T> dispatch() {
            return dispatchWrapper();
        }

        // wrapper for the same thread offset
        private List<T> dispatchWrapper() {

            if (this.dispatched.getAndSet(true)) {
                Log.internal(Context.this, new IllegalStateException("dispatch() already called on this collector"));
                throw new Log.KarinaException();
            }

            Log.KarinaException exception = null;

            var results = new ArrayList<T>();

            for (var sub : this.functions) {
                var context = new Context("fork", Context.this.infos, 2);
                try {
                    var result = sub.apply(context);
                    results.add(result);
                } catch(Log.KarinaException e) {
                    if (!Context.this.infos.allowMultipleErrors()) {
                        Context.this.mergeUp(context);
                        throw e;
                    }
                    exception = e;
                }
                Context.this.mergeUp(context);
            }

            if (exception != null) {
                throw exception;
            }

            return results;
        }


        @Override
        public void close()  {
            if (!this.dispatched.get()) {
                Log.internal(Context.this, new IllegalStateException("dispatch() not called on this collector"));
                throw new Log.KarinaException();
            }
        }
    }

    public sealed interface ContextHandling {
        boolean traces();
        boolean threading();
        boolean allowMultipleErrors();
        boolean allowMissingFields();


        @Contract("_, _, _ -> new")
        static ContextHandling of(boolean traces, boolean threading, boolean allowMultipleErrors) {
            return new ContextHandlingImpl(traces, threading, allowMultipleErrors, false);
        }


        default ContextHandling enableMissingMembersSupport() {
            return new ContextHandlingImpl(this.traces(), this.threading(), this.allowMultipleErrors(), true);
        }
    }
    private record ContextHandlingImpl(
            boolean traces,
            boolean threading,
            boolean allowMultipleErrors,
            boolean allowMissingFields
    ) implements ContextHandling { }


    public class LoggingContext {
        @Getter
        @Accessors(fluent = true)
        private final String name;
        private final List<Entry> entries = new ArrayList<>();

        private final long startTime = System.nanoTime();
        private long stopTime = 0;
        @Getter
        @Accessors(fluent = true)
        private final StackTraceElement[] stackTrace;
        @Getter
        @Accessors(fluent = true)
        private final int traceOffset;

        public LoggingContext(String name, int traceOffset) {
            this.name = name;
            this.stackTrace = Thread.currentThread().getStackTrace();
            this.traceOffset = traceOffset;
        }

        public ImmutableList<Entry> entries() {
            return ImmutableList.copyOf(this.entries);
        }

        private void close() {
            this.stopTime = System.nanoTime();
            Context.this.ensureSectionStackNotEmpty();
            var current = Context.this.sections.pop();
            if (current != this) {
                Log.internal(Context.this, new IllegalStateException(
                        "Invalid section close, expected " + this + " but got " + current
                ));
                throw new Log.KarinaException();
            }
        }

        public String getTimeString() {
            var duration = (this.stopTime - this.startTime) / 1_000_000.0f;
            if (duration < 0) {
                return "0.00ms";
            }
            return String.format("%.2fms", duration);
        }


        @Override
        public String toString() {
            // also print the hash, since we compare sections by identity in the close method
            var hash = Integer.toHexString(System.identityHashCode(this));
            return "LoggingContext{" + "name='" + this.name + '\'' + ", hash='" + hash + '\'' + '}';
        }

        public sealed interface Entry {
            record Tag(String text, StackTraceElement[] stackTrace, boolean error) implements Entry {}
            record SubSection(LoggingContext subSection) implements Entry {}
        }

    }


    @Contract(mutates = "param2, param3, param4")
    public static <T> @Nullable T run(
            ContextHandling contextHandling,
            @Nullable DiagnosticCollection errors,
            @Nullable DiagnosticCollection warnings,
            @Nullable FlightRecordCollection recordings,
            Function<Context, T> function
    ) {
        // Context is always referred to as 'c' in the codebase
        var c = new Context("root", contextHandling, 2);

        try {
            var object = function.apply(c);

            if (c.hasErrors()) {
                Log.internal(c,new IllegalStateException("Errors in log, this should not happen"));
                throw new Log.KarinaException(); // trigger error handling
            }

            return object;
        } catch (Exception error) {
            if (!(error instanceof Log.KarinaException)) {
                Log.internal(c, error);
            }
            if (!c.hasErrors()) {
                Log.internal(c, new IllegalStateException("An exception was thrown, but no errors were logged"));
            }

            return null;
        } finally {
            c.ensureSectionStackNotEmpty();
            var lastSection = c.sections.peek();
            // close() removes it from the stack
            lastSection.close();
            if (!c.sections.isEmpty()) {
                Log.internal(c, new IllegalStateException(
                        "Sections stack is not empty, this should not happen. Some sections were not closed properly."
                ));
            }

            if (warnings != null) {
                warnings.addAll(c.warnings);
            }
            if (errors != null) {
                errors.addAll(c.errors);
            }
            if (recordings != null) {
                recordings.set(lastSection);
            }

        }

    }

    /// needed so the compiler can detect that to error is thrown
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public final class OpenSection implements AutoCloseable {
        private final LoggingContext context;

        @Override
        public void close() {
            this.context.close();
        }
    }

}
