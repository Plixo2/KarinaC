package org.karina.lang.lsp;

import com.google.errorprone.annotations.NoAllocation;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;

import java.util.concurrent.atomic.AtomicInteger;

public class Test {

    public static void main(String[] args) {

//        t2(0x400108, "N T N N T N N T N");
//        t2(0x40021C, "T N T T N T T N T");
//        t2(0x400230, "T T N T T N T T N");
//        t2(0x400304, "- - T - - T - - T");
//
//        System.out.println();
//        t2(0x400230, "T T N T T N T T N");
//        t2(0x400304, "- - T - - T - - T");
//        t2(0x400108, "N T N N T N N T N");
//        t2(0x40021C, "T N T T N T T N T");

//        var counter = new AtomicInteger(1);
//        var cache = new Cache();
//        cache.print(0);
//        read(Address.of(0x4008), cache, counter);               cache.print(counter.getAndIncrement());
//        read(Address.of(0x4020), cache, counter);               cache.print(counter.getAndIncrement());
//        write(Address.of(0x400c), cache, counter, 0xff);   cache.print(counter.getAndIncrement());
//        read(Address.of(0x410c), cache, counter);               cache.print(counter.getAndIncrement());
//        write(Address.of(0x4004), cache, counter, 0xff);   cache.print(counter.getAndIncrement());
//        read(Address.of(0x412c), cache, counter);               cache.print(counter.getAndIncrement());
//        read(Address.of(0x4020), cache, counter);               cache.print(counter.getAndIncrement());
//        write(Address.of(0x402c), cache, counter, 0xff);   cache.print(counter.getAndIncrement());




        runSimulation();
        runSimulation();
        runSimulation();
    }

    @NoAllocation
    private static void runSimulation() {
        final var RUNS = 1_000_000;

        var success_count = 0;
        var rolls_per_success = 0;
        for (var i = 0; i < RUNS; i++) {
            var result = simulateRun();
            if (result > 0) {
                success_count += 1;
                rolls_per_success += result;
            }
        }

        System.out.println(10);


        var average_success = (double) success_count / RUNS;
        var average = (double) rolls_per_success / success_count;

        System.out.println("Success count: " + success_count);
        System.out.println("Average success rate: " + average_success);
        System.out.println("Average rolls per success: " + average);

        System.out.println();
    }

    @NoAllocation
    private static int simulateRun() {
        final var CORRECT_STARTS = 3;
        final var CARD_COUNT = 38;
        final var CATEGORY_COUNT = 24;
        final var CARDS_PER_CATEGORY = 8;
        final var FAIL_RATE = 0.0931; // 9.31% fail rate

        var open_set = CARD_COUNT - CORRECT_STARTS;
        var correct_category_count = CORRECT_STARTS;

        var counter = 0;
        while (open_set > 0) {
            counter += 1;
            var failed = sample(FAIL_RATE);
            if (failed) {
                // remove from open set on roll failure
                open_set -= 1;
                continue;
            }

            var in_correct_category = sample(1.0 / CATEGORY_COUNT);

            if (in_correct_category) {
                var p_already_in_category = (double) correct_category_count / CARDS_PER_CATEGORY;
                var already_in_category = sample(p_already_in_category);
                if (already_in_category) {
                    // when in category already just re-roll
                    continue;
                } else {
                    // otherwise remove from open set and add to correct set
                    open_set -= 1;
                    correct_category_count += 1;

                    if (correct_category_count == CARDS_PER_CATEGORY) {
                        return counter;
                    }
                }
            } else {
                // otherwise re-roll when not in correct category
            }

        }
        return 0;
    }

    @NoAllocation
    private static boolean sample(double p) {
        var r = Math.random();
        return r <= p;
    }


    //<editor-fold desc="CacheSym">
    @Contract(mutates = "param2")
    private static void read(Address address, Cache cache, AtomicInteger step) {
        var block_way_0 = cache.way_0.of(address.index);
        var block_way_1 = cache.way_1.of(address.index);

        var data_0 = block_way_0.of(address.word_offset);
        var data_1 = block_way_1.of(address.word_offset);

        boolean hit;
        int data;
        if (block_way_0.tag == address.tag) {
            block_way_0.step_count = step.get();
            hit = block_way_0.valid;
            data = data_0;
        } else if (block_way_1.tag == address.tag) {
            block_way_1.step_count = step.get();
            hit = block_way_1.valid;
            data = data_1;
        } else {
            hit = false;

            Block block_to_write;
            if (block_way_0.step_count <= block_way_1.step_count) {
                block_to_write = block_way_0;
            } else {
                block_to_write = block_way_1;
            }

            if (block_to_write.dirty) {
                System.out.println("Writing back to memory: " + Integer.toHexString(block_to_write.data_0) + " | " +
                        Integer.toHexString(block_to_write.data_1));
                // TODO
            }
            if (block_to_write.valid) {
                System.out.println("Read collision at address: " + Integer.toHexString(address.all));
            } else {
                System.out.println("Read miss at address: " + Integer.toHexString(address.all));
            }

            block_to_write.data_0 = address.data()[0];
            block_to_write.data_1 = address.data()[1];
            block_to_write.tag = address.tag;
            block_to_write.valid = true;
            block_to_write.dirty = false; // Assuming we are not writing back to memory
            block_to_write.step_count = step.get(); // Reset step count for LRU

        }

    }
    @Contract(mutates = "param2")
    private static void write(Address address, Cache cache, AtomicInteger step, int data) {
        var block_way_0 = cache.way_0.of(address.index);
        var block_way_1 = cache.way_1.of(address.index);


        if (block_way_0.tag == address.tag) {
            if (block_way_0.valid) {
                block_way_0.dirty = true;
                block_way_0.set(address.word_offset, data);
                block_way_0.step_count = step.get();
                return;
            } else {
                System.out.println("Write miss at (b0) address: " + Integer.toHexString(address.all));
                // else miss
            }

        } else if (block_way_1.tag == address.tag) {
            if (block_way_1.valid) {
                block_way_1.dirty = true;
                block_way_1.set(address.word_offset, data);
                block_way_1.step_count = step.get();
                return;
            } else {
                System.out.println("Write miss at (b1) address: " + Integer.toHexString(address.all));
                // else miss
            }

        } else {
            System.out.println("Write miss at address: " + Integer.toHexString(address.all));
            // else miss
        }

        //miss
        Block block_to_write;
        if (block_way_0.step_count <= block_way_1.step_count) {
            block_to_write = block_way_0;
        } else {
            block_to_write = block_way_1;
        }

        block_to_write.data_0 = address.data()[0];
        block_to_write.data_1 = address.data()[1];
        block_to_write.set(address.word_offset, data);
        block_to_write.tag = address.tag;
        block_to_write.valid = true;
        block_to_write.dirty = true; // Assuming we are not writing back to memory
        block_to_write.step_count = step.get(); // Reset step count for LRU

    }


    record Address(int all, int tag, int index, int word_offset) {


        /// 2 bit: byte-offset (ignored)
        /// 1 bit: word-offset
        /// 1 bit: index
        /// 12 bit: tag
        /// @param address 16 bit address
        public static Address of(int address) {
            var all = address;
            address = address >> 2; // Shift right by 2 to ignore the byte offset
            int word_offset = address & 0b1; // Next 1 bit for word offset
            address = address >> 1; // Shift right by 1 to ignore the word offset
            int index = address & 0b1; // Next 1 bits for index
            address = address >> 1; // Shift right by 1 to ignore the index
            int tag = address & 0b111111111111; // Last 12 bits for tag

            return new Address(all, tag, index, word_offset);
        }

        public int[] data() {
            int mem_data_start =
                    (this.all >> 3) << 3; // Clear the last 3 bits to get the start of the block
            int mem_data_0 = mem_data_start;
            int mem_data_1 = mem_data_start + 4;

            return new int[]{mem_data_0, mem_data_1};
        }

        public void print() {
            System.out.println("Address: " + Integer.toHexString(all));
            System.out.println("Tag: " + Integer.toHexString(tag));
            System.out.println("Index: " + index);
            System.out.println("Word Offset: " + word_offset);
        }
    }

    static final class Cache {
        private final Set way_0 = new Set();
        private final Set way_1 = new Set();

        public void print(int step) {

//            System.out.println("Step " + step);
//            // 10 11
//            System.out.print(" |Step | Tag            | Valid | Dirty | Data 0   | Data 1  | \t\t\t |");
//            System.out.println("Step | Tag            | Valid | Dirty | Data 0   | Data 1  |");
//
//            System.out.print(" | ");
//            System.out.print(way_0.block_0);
//            System.out.print("| \t\t\t | ");
//            System.out.print(way_1.block_0);
//            System.out.print(" | ");
//
//            System.out.println();
//
//            System.out.print(" | ");
//            System.out.print(way_0.block_1);
//            System.out.print("| \t\t\t | ");
//            System.out.print(way_1.block_1);
//            System.out.print(" | ");
//
//            System.out.println();

            System.out.println("Step " + step);
            // 10 11
            System.out.println("|Step | Tag            | Valid | Dirty | Data 0   | Data 1  | ");

            System.out.println("  " + way_0.block_0);
            System.out.println("  " + way_1.block_0);

            System.out.println("-----------------------------------------------------------------");

            System.out.println("  " + way_0.block_1);
            System.out.println("  " + way_1.block_1);

            System.out.println();

        }
    }


    private static final class Set {
        private final Block block_0 = new Block();
        private final Block block_1 = new Block();

        public Block of(int index) {
            if (index == 0) {
                return block_0;
            } else if (index == 1) {
                return block_1;
            } else {
                throw new IllegalArgumentException("Index must be 0 or 1, but was: " + index);
            }
        }

    }

    @NoArgsConstructor
    private static final class Block {
        private int step_count;
        private int tag;
        private boolean valid;
        private boolean dirty;
        private int data_0;
        private int data_1;


        public int of(int wordOffset) {
            if (wordOffset == 0) {
                return data_0;
            } else if (wordOffset == 1) {
                return data_1;
            } else {
                throw new IllegalArgumentException("Word offset must be 0 or 1, but was: " + wordOffset);
            }
        }
        public void set(int wordOffset, int value) {
            if (wordOffset == 0) {
                data_0 = value;
            } else if (wordOffset == 1) {
                data_1 = value;
            } else {
                throw new IllegalArgumentException("Word offset must be 0 or 1, but was: " + wordOffset);
            }
        }

        @Override
        public String toString() {
            var ref = String.format("%-3s", step_count);
            var tag_str = String.format("%-14s", Integer.toHexString(tag));
            var valid_str = valid ? "1    " :"0    ";
            var dirty_str = dirty ? "1    " :"0    ";
            var data_0_str = String.format("%-8s", Integer.toHexString(data_0));
            var data_1_str = String.format("%-8s", Integer.toHexString(data_1));
            return ref + " | " + tag_str + " | " + valid_str + " | " + dirty_str + " | " + data_0_str + " | " + data_1_str;

        }


    }
    //</editor-fold>

    //<editor-fold desc="BHT">
    private static void t2(int adress, String s) {
        var index = (adress >> 2) & 0b11;
        System.out.println("BHT " + index);

        var table = 0b01;

        for (var i = 0; i < s.length(); i++) {
            var c = s.charAt(i);

            if (c == '-' || c == ' ') {
                if (c == '-') {
                    System.out.print("---- ");
                }
                continue;
            }
            if (c != 'T' && c != 'N') {
                throw new NullPointerException("Invalid character: " + c);
            }
            var state = c == 'T';
            var prediction = table > 1;
            if (state) {
                System.out.print(prediction);
                table = Math.min(table + 1, 3);
            } else {
                System.out.print(!prediction);
                table = Math.max(table - 1, 0);
            }
            System.out.print(" ");

        }
        System.out.println();

    }

    private static void t1(int adress, String s) {
        var index = (adress >> 2) & 0b11;
        System.out.println("BHT " + index);
        var table = 0;

        for (var i = 0; i < s.length(); i++) {
            var c = s.charAt(i);

            if (c == '-' || c == ' ') {
                if (c == '-') {
                    System.out.print("---- ");
                }
                continue;
            }
            if (c != 'T' && c != 'N') {
                throw new NullPointerException("Invalid character: " + c);
            }
            var state = c == 'T';
            if (state) {
                System.out.print(table == 1);
                table = 1;
            } else {
                System.out.print(table == 0);
                table = 0;
            }
            System.out.print(" ");

        }
        System.out.println();

    }
    //</editor-fold>
}
