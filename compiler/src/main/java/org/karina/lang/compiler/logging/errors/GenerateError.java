package org.karina.lang.compiler.logging.errors;

import org.karina.lang.compiler.logging.ErrorInformation;
import org.karina.lang.compiler.utils.Region;

public sealed interface GenerateError extends Error {

    record NotValidAnymore(Region region, String className, String code) implements GenerateError {
        @Override
        public void addInformation(ErrorInformation builder) {
            builder.setTitle("Error while generating class " + className());
            builder.setPrimarySource(this.region());
            this.code.lines().forEach(builder::append);
        }
    }
}
