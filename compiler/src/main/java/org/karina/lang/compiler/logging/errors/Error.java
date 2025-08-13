package org.karina.lang.compiler.logging.errors;

import org.karina.lang.compiler.logging.ErrorInformation;
import org.karina.lang.compiler.utils.Region;
import org.karina.lang.compiler.utils.Resource;

public sealed interface Error
        permits Error.BytecodeLoading, Error.InternalException,
        Error.SyntaxError, Error.Default, Error.Warn, FileLoadError,
        AttribError, ImportError, LowerError, GenerateError {

    void addInformation(ErrorInformation builder);

    record Default(Region region, String message) implements Error {
        @Override
        public void addInformation(ErrorInformation builder) {
            builder.setTitle(this.message);
            builder.setPrimarySource(this.region);
        }
    }
    record Warn(String message) implements Error {
        @Override
        public void addInformation(ErrorInformation builder) {
            builder.setTitle(this.message);
        }
    }

    record InternalException(Throwable exception) implements Error {
        @Override
        public void addInformation(ErrorInformation builder) {
            builder.setTitle("Internal Exception");
            builder.append("oh no, please report this issue");
            builder.append("");
            if (this.exception.getMessage() == null || this.exception.getMessage().isEmpty()) {
                builder.append("<no message given>");
            } else {
                builder.append(this.exception.getMessage());
            }
            for (var stackTraceElement : this.exception.getStackTrace()) {
                builder.append(stackTraceElement.toString());
            }
        }
    }
    record SyntaxError(Region region, String msg) implements Error {

        @Override
        public void addInformation(ErrorInformation builder) {
            builder.setTitle("Syntax Error");
            builder.append(this.msg);
            builder.setPrimarySource(this.region);
        }
    }
    record BytecodeLoading(Resource resource, String location, String msg) implements Error {
        @Override
        public void addInformation(ErrorInformation builder) {
            builder.setTitle("Bytecode Handling");
            builder.append("File: ").append(this.resource.identifier());
            builder.append("symbol: " + this.location);
            builder.append(this.msg);
        }
    }

}
