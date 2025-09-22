package org.karina.lang.lsp.impl;

import karina.lang.Option;
import lombok.Getter;
import lombok.experimental.Accessors;

public final class IntList {

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
        this.size += 1;
    }

    public void add(int a, int b, int c, int d, int e) {
        if (this.data.length == this.size) {
            ensureCapacity(this.size + 5);
        }

        this.data[this.size + 0] = a;
        this.data[this.size + 1] = b;
        this.data[this.size + 2] = c;
        this.data[this.size + 3] = d;
        this.data[this.size + 4] = e;
        this.size += 5;
    }

    public int get(int index) {
        return this.data[index];
    }

    private void ensureCapacity(int capacity) {
        if (capacity > this.data.length) {
            int newCapacity = Math.max(capacity, this.data.length * 2);
            int[] newData = new int[newCapacity];
            System.arraycopy(this.data, 0, newData, 0, this.size);
            this.data = newData;
        }
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public Option<Integer> last() {
        if (this.size == 0) {
            return Option.none();
        }
        return Option.some(this.data[this.size - 1]);
    }

}
