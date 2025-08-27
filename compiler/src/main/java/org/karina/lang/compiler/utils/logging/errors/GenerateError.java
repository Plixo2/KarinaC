package org.karina.lang.compiler.utils.logging.errors;

import org.karina.lang.compiler.utils.logging.ErrorInformationBuilder;
import org.karina.lang.compiler.utils.Region;

public sealed interface GenerateError extends Error {

    record GenerateClass(Region region, String className, String code) implements GenerateError {
        @Override
        public void addInformation(ErrorInformationBuilder builder) {
            builder.setTitle("Error while generating class " + className());
            builder.setPrimarySource(this.region());
            this.code.lines().forEach(builder::append);
        }
    }

    record GenerateMethod(Region region, String method, String code) implements GenerateError {
        @Override
        public void addInformation(ErrorInformationBuilder builder) {
            builder.setTitle("Error while generating method " + method());
            builder.setPrimarySource(this.region());
            this.code.lines().forEach(builder::append);
        }
    }
}
