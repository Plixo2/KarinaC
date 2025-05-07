package org.karina.lang.compiler.model_api.impl.table;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.Log;

import java.util.ArrayList;
import java.util.List;

public class Trie<T> {
    //null allowed, never tested
    TrieNode<T> root = new TrieNode<>(null);
    List<T>[] objects;
    boolean locked = false;
    int maxBuckets;

    @SuppressWarnings("unchecked")
    public Trie(int maxBuckets) {
        this.maxBuckets = maxBuckets;
        this.objects = new List[maxBuckets];
        for (var i = 0; i < maxBuckets; i++) {
            this.objects[i] = new ArrayList<>();
        }
    }

    public static class TrieNode<T> {
        String name;
        @Nullable TrieNode<T>[] children = null;
        @Nullable T value = null;

        public TrieNode(String name) {
            this.name = name;
        }
    }
    @SuppressWarnings("unchecked")
    public void insert(String[] path, T object, int bucket) {
        if (this.locked) {
            Log.internal(new IllegalStateException("trie locked"));
            throw new RuntimeException("trie locked");
        }
        var currentNode = this.root;

        for (var element : path) {

            var children = currentNode.children;
            if (currentNode.children == null) {
                children = currentNode.children = new TrieNode[32];
            }
            var childCount = children.length;

            var hashCode = element.hashCode();
            var indexToPlace = hashCode % childCount;


            var firstChild = children[indexToPlace];
            if (firstChild != null) {
                if (firstChild.name.equals(element)) {
                    currentNode = firstChild;
                } else {
                    while (true) {
                        //probe
                        var i = 0;
                        for (i = 0; i < childCount; i++) {
                            var child = children[(indexToPlace + i) % childCount];
                            if (child == null) {
                                var newNode = new TrieNode<T>(element);
                                children[i] = newNode;
                                currentNode = newNode;
                            } else if (child.name.equals(element)) {
                                currentNode = child;
                            }
                        }
                        //rehash
                        if (i == childCount) {
                            var newChildCount = childCount * 2;
                            var newArray = new TrieNode[childCount];
                            for (var copyI = 0; copyI < newChildCount; copyI++) {
                                var currentCopyChild = children[copyI];
                                if (currentCopyChild == null) {
                                    continue;
                                }
                                var newIndex = currentCopyChild.name.hashCode() % newChildCount;

                                //newArray is always bigger, should not fail
                                for (var i1 = 0; i1 < newChildCount; i1++) {
                                    var newIndexToPlace = (newIndex + i1) % newChildCount;
                                    if (newArray[newIndexToPlace] == null) {
                                        newArray[newIndexToPlace] = currentCopyChild;
                                        break;
                                    }
                                }
                            }

                            childCount = newChildCount;
                            children = currentNode.children = newArray;
                        } else {
                            break;
                        }
                    }
                }
            } else {
                var newNode = new TrieNode<T>(element);
                children[indexToPlace] = newNode;
                currentNode = newNode;
            }
        }
        if (currentNode.value != null) {
            //TODO error handling
            throw new RuntimeException("Duplicate key");
        }
        currentNode.value = object;
        this.objects[bucket].add(object);
    }


    public List<T> getBucket(int bucket) {
        if (bucket >= this.maxBuckets || bucket < 0) {
            throw new IllegalArgumentException("bucket out of bounds");
        }
        return this.objects[bucket];
    }
}
