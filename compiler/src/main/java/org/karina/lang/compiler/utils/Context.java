package org.karina.lang.compiler.utils;

import com.google.common.collect.ImmutableList;
import com.google.errorprone.annotations.CheckReturnValue;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.KarinaCompiler;
import org.karina.lang.compiler.logging.FlightRecorder;
import org.karina.lang.compiler.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;


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
public class Context implements IntoContext {

    /// A <b>non-thread-safe</b> List of logs
    /// Threading should be done with the  {@link #fork} method and {@link Collector} class
    private final List<Log.LogWithTrace> errors = new ArrayList<>();
    private final List<Log.LogWithTrace> warning = new ArrayList<>();

//    Flight recorder for logging. Not safe for concurrent use.
//    private static final FlightRecorder flightRecorder = new FlightRecorder();

    private Context() {}

    /// <b>not thread-safe</b>
    public void mergeUp(Context context) {
        this.errors.addAll(context.errors);
        this.warning.addAll(context.warning);
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
        return new Collector<>(this);
    }


    public void addError(Log.LogWithTrace log) {
        this.errors.add(log);
    }

    public void addWarn(Log.LogWithTrace log) {
        this.warning.add(log);
    }

    public boolean hasErrors() {
        return !this.errors.isEmpty();
    }

    public List<Log.LogWithTrace> getErrors() {
        return ImmutableList.copyOf(this.errors);
    }

    public List<Log.LogWithTrace> getWarnings() {
        return ImmutableList.copyOf(this.warning);
    }

    @Override
    public Context intoContext() {
        return this;
    }

    public static Context empty() {
        return new Context();
    }

    public static class Collector<T> implements AutoCloseable {
        private final Context context;
        private boolean dispatched = false;

        public Collector(Context context) {
            this.context = context;
        }

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
            if (!KarinaCompiler.useThreading) {
                return dispatch();
            }

            if (this.dispatched) {
                Log.internal(this.context, new IllegalStateException("Dispatch already called on this collector"));
            }
            this.dispatched = true;

            var error = new AtomicBoolean(false);


            List<ParallelEntry<T>> results = new ArrayList<>();
            List<Future<ParallelEntry<T>>> futures = new ArrayList<>();
            var availableProcessors = Runtime.getRuntime().availableProcessors();
            try (var executor = Executors.newFixedThreadPool(availableProcessors)) {

                for (var function : this.functions) {
                    futures.add(executor.submit(() -> {
                        var context = new Context();
                        try {
                            var result = function.apply(context);
                            return new ParallelEntry<>(result, context);
                        } catch (Log.KarinaException e) {
                            if (!KarinaCompiler.allowMultipleErrors) {
                                synchronized (this.context) {
                                    this.context.mergeUp(context);
                                }
                                throw e;
                            }
                            error.set(true);
                            return new ParallelEntry<>(null, context);
                        }
                    }));
                }


                for (var future : futures) {
                    results.add(future.get());
                }

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Log.internal(this.context, e);
                throw new Log.KarinaException();
            }

            for (var result : results) {
                this.context.mergeUp(result.context);
            }

            if (error.get()) {
                throw new Log.KarinaException();
            }

            //should not contain nulls
            return results.stream().map(ParallelEntry::t).toList();
        }

        ///
        /// <b>non-parallel</b> dispatch of the collected functions.
        /// If any function throws a {@link Log.KarinaException}, it will be caught and rethrown after merging contexts.
        /// @return a list of results in the order they were collected, when no function throws an exception.
        ///
        @CheckReturnValue
        public List<T> dispatch() {

            if (this.dispatched) {
                Log.internal(this.context, new IllegalStateException("Dispatch already called on this collector"));
            }
            this.dispatched = true;

            var error = false;

            var results = new ArrayList<T>();

            for (var sub : this.functions) {
                var context = new Context();
                try {
                    var result = sub.apply(context);
                    results.add(result);
                } catch(Log.KarinaException e) {
                    if (!KarinaCompiler.allowMultipleErrors) {
                        this.context.mergeUp(context);
                        throw e;
                    }
                    error = true;
                }
                this.context.mergeUp(context);
            }

            if (error) {
                throw new Log.KarinaException();
            }

            return results;
        }

        @Override
        public void close()  {
            if (!this.dispatched) {
                Log.internal(this.context, new IllegalStateException("Dispatch not called on this collector"));
            }
        }
    }

    private record ParallelEntry<T>(@Nullable T t, Context context) {

    }


}
