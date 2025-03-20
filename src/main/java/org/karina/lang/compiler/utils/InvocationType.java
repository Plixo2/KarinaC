package org.karina.lang.compiler.utils;

import org.karina.lang.compiler.objects.KType;


// Represents the type of invocation that is being on an init method
public sealed interface InvocationType {

    record NewInit(KType.ClassType classType) implements InvocationType {}
    record SpecialInvoke(String name, KType superType) implements InvocationType {}

}
