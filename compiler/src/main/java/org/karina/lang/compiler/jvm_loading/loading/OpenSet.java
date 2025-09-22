package org.karina.lang.compiler.jvm_loading.loading;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.jvm_loading.JavaResource;
import org.karina.lang.compiler.utils.DefaultTextSource;
import org.karina.lang.compiler.utils.TextSource;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Set of loaded classes that need to be processed
 */
@RequiredArgsConstructor
public class OpenSet {
    private final @Nullable String sourceJar;

    @Getter
    private final Map<String, LoadedClass> openSet = new ConcurrentHashMap<>();


    public boolean isEmpty() {
        return this.openSet.isEmpty();
    }

    public List<LoadedClass> removeTopClasses() {
        var topClasses = new ArrayList<LoadedClass>();
        var keysToRemove = new ArrayList<String>();
        for (var entry : this.openSet.entrySet()) {
            var name = entry.getKey();
            var cls = entry.getValue();

            if (cls.node.nestHostClass == null && cls.node.outerClass == null) {
                topClasses.add(cls);
                keysToRemove.add(name);
            }
        }
        for (var key : keysToRemove) {
            this.openSet.remove(key);
        }

        return topClasses;
    }

    public @Nullable LoadedClass removeByName(String name) {
        return this.openSet.remove(name);
    }

    public void add(ClassNode node, String fileName) {
        if (this.openSet.containsKey(node.name)) {
            throw new NullPointerException("Class already loaded " + node.name);
        }
        this.openSet.put(node.name, new LoadedClass(this.sourceJar, fileName, node));
    }

    public record LoadedClass(@Nullable String sourceJar, String fileName, ClassNode node){
        public TextSource getSource() {
            if (this.sourceJar == null) {
                var sourceID = "<buildin>" + this.fileName;
                return new DefaultTextSource(new JavaResource(sourceID), "");
            } else {
                var srcId = "jar:file:///" + this.sourceJar + "!/" + this.fileName;
                return new DefaultTextSource(new JavaResource(srcId), "");
            }
        }
    }
}
