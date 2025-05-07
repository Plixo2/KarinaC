package org.karina.lang.compiler.utils.annotations;

import org.karina.lang.compiler.utils.Region;

public sealed interface AnnotationValue
        permits AnnotationAST, AnnotationArray, AnnotationBool, AnnotationNull, AnnotationNumber,
        AnnotationObject, AnnotationString {

    Region region();
}
