package org.karina.lang.compiler.model_api.impl.table;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model_api.impl.jvm.JClassModel;
import org.karina.lang.compiler.model_api.impl.karina.KClassModel;
import org.karina.lang.compiler.model_api.ClassModel;
import org.karina.lang.compiler.utils.ObjectPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinearLookup implements ClassLookup {
    private boolean locked = false;
    Map<ObjectPath, ClassModel> map = new HashMap<>(64, 0.5f);
    List<KClassModel> userClasses = new ArrayList<>();
    List<JClassModel> binaryClasses = new ArrayList<>();
    @Override
    public @Nullable ClassModel insert(ObjectPath path, ClassModel object) {
        if (this.locked) {
            throw new IllegalStateException("trie locked");
        }
        var prev = this.map.put(path, object);
        if (prev != null) {
            return prev;
        }
        if (object instanceof KClassModel model) {
            this.userClasses.add(model);
        } else if (object instanceof JClassModel model) {
            this.binaryClasses.add(model);
        }
        return null;
    }

    @Override
    public boolean contains(ObjectPath path) {
        return this.map.containsKey(path);
    }

    @Override
    public @Nullable ClassModel get(ObjectPath path) {
        return this.map.get(path);
    }

    @Override
    public ClassLookup lock() {
        this.locked = true;
        return this;
    }

    @Override
    public boolean locked() {
        return this.locked;
    }

    @Override
    public int count() {
        return this.map.size();
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
