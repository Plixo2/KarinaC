package org.karina.lang.compiler.objects;

public sealed interface NumberFormat {

    record DFormat() implements NumberFormat {}
    record FFormat() implements NumberFormat {}
    record LFormat() implements NumberFormat {}
    record DecimalFormat() implements NumberFormat {}
    record IntegerFormat() implements NumberFormat {}

}
