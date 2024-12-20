package org.karina.lang.compiler.errors;

/**
 * Collect multiple logs and throw an error when the collector is closed, if any errors occurred.
 * Use a try-with-resources block to ensure that the errors are thrown.
 */
public class ErrorCollector implements AutoCloseable {

    private boolean error = false;

    public void collect(Runnable runnable) {
        try {
            runnable.run();
        } catch (Log.KarinaException e) {
            this.error = true;
        }
    }

    @Override
    public void close() {
        if (this.error) {
            throw new Log.KarinaException();
        }
    }
}
