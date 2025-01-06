package org.karina.lang.sym;

public class VM {
    Instruction[] instructions;
    int activationRecordPointer = 0;
    int stackPointer = 0;

    long[] recordStack;
    int[] callStack;

    int activeRecordSize = 0;
    int pc = 0;


    public VM() {
        this.recordStack = new long[262144]; // 2^18
        this.callStack = new int[65536]; // 2^16
    }

}
