package org.karina.lang.compiler.model_api.impl.table;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model_api.impl.jvm.JClassModel;
import org.karina.lang.compiler.model_api.impl.karina.KClassModel;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.model_api.ClassModel;
import org.karina.lang.compiler.utils.ObjectPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrefixTreeLookup implements ClassLookup {
    //null allowed, never tested
    TrieNode root = new TrieNode();
    boolean locked = false;
    private int count = 0;

    List<KClassModel> userClasses = new ArrayList<>();
    List<JClassModel> binaryClasses = new ArrayList<>();

    public static class TrieNode {
        @Nullable Map<String, TrieNode> children = null;
        @Nullable ClassModel value = null;
    }
    /**
     * Returns the previous object at the path
     */
    @Override
    public @Nullable ClassModel insert(ObjectPath path, ClassModel object) {
        if (this.locked) {
            Log.internal(new IllegalStateException("trie locked"));
            throw new RuntimeException("trie locked");
        }
        var currentNode = this.root;

        var names = path.elements();
        for (var element : names) {

            var children = currentNode.children;
            if (currentNode.children == null) {
                children = currentNode.children = new HashMap<>(100);
            }
            currentNode = children.get(element);
            if (currentNode == null) {
                currentNode = new TrieNode();
                children.put(element, currentNode);
            }
        }
        if (currentNode.value != null) {
            return currentNode.value;
        }
        this.count += 1;
        currentNode.value = object;
        if (object instanceof KClassModel model) {
            this.userClasses.add(model);
        } else  if (object instanceof JClassModel model) {
            this.binaryClasses.add(model);
        }

        return null;
    }

    @Override
    public boolean contains(ObjectPath path) {
        var currentNode = this.root;
        var names = path.elements();
        for (var element : names) {
            if (currentNode.children == null) {
                return false;
            }
            currentNode = currentNode.children.get(element);
            if (currentNode == null) {
                return false;
            }
        }
        return currentNode.value != null;
    }

    @Override
    public @Nullable ClassModel get(ObjectPath path) {
        var currentNode = this.root;
        var names = path.elements();
        for (var element : names) {
            if (currentNode.children == null) {
                return null;
            }
            currentNode = currentNode.children.get(element);
            if (currentNode == null) {
                return null;
            }
        }
        return currentNode.value;
    }

    /**
     * Returns the previous object at the path
     */
    @Override
    public PrefixTreeLookup lock() {
        this.userClasses = ImmutableList.copyOf(this.userClasses);
        this.binaryClasses = ImmutableList.copyOf(this.binaryClasses);
        this.locked = true;
        return this;
    }

    @Override
    public boolean locked() {
        return this.locked;
    }

    @Override
    public int count() {
        return this.count;
    }

    @Override
    public List<KClassModel> userClasses() {
        return this.userClasses;
    }

    @Override
    public List<JClassModel> binaryClasses() {
        return this.binaryClasses;
    }
}
