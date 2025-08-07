package org.karina.lang.compiler.logging.errors;

import org.karina.lang.compiler.logging.ErrorInformation;
import org.karina.lang.compiler.utils.Region;

public sealed interface LowerError extends Error {
    @Deprecated
    record NotValidAnymore(Region region, String message) implements LowerError {
        @Override
        public void addInformation(ErrorInformation builder) {
            builder.setTitle("Cannot be Expressed");
            builder.append(this.message());
            builder.setPrimarySource(this.region());
        }
    }
}
