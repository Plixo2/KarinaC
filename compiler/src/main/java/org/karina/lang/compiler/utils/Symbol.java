package org.karina.lang.compiler.utils;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * An annotation to mark information in expressions that are filled in after the attribution phase.
 * This is only used inside the attribution and code generation phases.
 * All other code before the attribution phase cannot use symbols and should insert nulls instead.
 */
@Retention(SOURCE)
@Target({FIELD, LOCAL_VARIABLE})
public @interface Symbol {}
