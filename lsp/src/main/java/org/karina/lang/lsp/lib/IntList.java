package org.karina.lang.lsp.lib;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Arrays;

public final class IntList {
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    private int[] data;
    @Getter
    @Accessors(fluent = true)
    private int size;

    public IntList() {
        this.data = new int[0];
        this.size = 0;
    }

    public IntList(int initialCapacity) {
        this.data = new int[initialCapacity];
        this.size = 0;
    }

    public void add(int value) {
        if (this.data.length == this.size) {
            ensureCapacity(this.size + 1);
        }

        this.data[this.size] = value;
        this.size++;
    }

    public int get(int index) {
        return this.data[index];
    }

    private void ensureCapacity(int capacity) {
        if (capacity < 0 || capacity > MAX_ARRAY_SIZE) {
            throw new OutOfMemoryError();
        }

        int newLength;
        if (this.data.length == 0) {
            newLength = 8;
        }
        else {
            newLength = this.data.length;
        }

        while (newLength < capacity) {
            newLength = newLength * 2;
            if (newLength < 0 || newLength > MAX_ARRAY_SIZE) {
                newLength = MAX_ARRAY_SIZE;
            }
        }

        this.data = Arrays.copyOf(this.data, newLength);
    }

}
