package org.karina.lang.compiler.utils;


// Represents the type of invocation that is being on an init method
public sealed interface InvocationType {

    record NewInit(KType.ClassType classType) implements InvocationType {}
    record SpecialInvoke(String name, KType superType) implements InvocationType {
        public SpecialInvoke {
//            if (name.equals("<init>")) {
//                Log.internal(new IllegalStateException("SpecialInvoke cannot be <init>"));
//                throw new Log.KarinaException();
//            }
        }
    }

}
