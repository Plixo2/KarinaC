package org.karina.lang.lsp.fs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.karina.lang.compiler.ObjectPath;
import org.karina.lang.lsp.EventHandler;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Virtual document tree
 */
@AllArgsConstructor
public class SyncFileTree {
    @Getter
    @Accessors(fluent = true)
    private ObjectPath path;
    @Getter
    @Accessors(fluent = true)
    private String name;

    @Getter
    private List<SyncFileTree> children;
    @Getter
    private List<KarinaFile> leafs;

    public boolean delete(ObjectPath path) {
        if (path.tail().isEmpty() || !path.first().equals(this.name)) {
            EventHandler.INSTANCE.errorMessage("invalid path");
        }
        return deleteImpl(path.tail());
    }

    private boolean deleteImpl(ObjectPath path) {
        var deleteHere = path.tail().isEmpty();
        var current = path.first();
        if (deleteHere) {
            for (var leaf : this.leafs) {
                if (current.equals(leaf.name())) {
                    this.leafs.remove(leaf);
                    return true;
                }
            }
        } else {
            for (var child : this.children) {
                if (current.equals(child.name)) {
                    return child.deleteImpl(path.tail());
                }
            }
        }
        return false;
    }

    public KarinaFile createOrGet(Path absolutURIPath, ObjectPath path) {
        if (path.tail().isEmpty() || !path.first().equals(this.name)) {
            EventHandler.INSTANCE.errorMessage("invalid path");
        }
        return createOrGetImpl(path.tail(), new ObjectPath(this.name), path, absolutURIPath);
    }


    private KarinaFile createOrGetImpl(ObjectPath toTheRight, ObjectPath toTheLeft, ObjectPath absolutPath, Path absolutURIPath) {
        var insertHere = toTheRight.tail().isEmpty();
        var current = toTheRight.first();
        if (insertHere) {
            for (var leaf : this.leafs) {
                if (current.equals(leaf.name())) {
                    return leaf;
                }
            }
            var newFile = new KarinaFile(current, absolutPath, absolutURIPath);
            this.leafs.add(newFile);
            return newFile;
        } else {
            var nextBuild = toTheLeft.append(current);
            for (var child : this.children) {
                if (current.equals(child.name)) {
                    return child.createOrGetImpl(toTheRight.tail(), nextBuild, absolutPath, absolutURIPath);
                }
            }
            var newChild = new SyncFileTree(nextBuild, current, new ArrayList<>(), new ArrayList<>());
            this.children.add(newChild);
            return newChild.createOrGetImpl(toTheRight.tail(), nextBuild, absolutPath, absolutURIPath);
        }
    }

    public List<KarinaFile> getAllLeafsRecursive() {
        List<KarinaFile> files = new ArrayList<>();
        this.addAllLeafs(files);
        return files;
    }

    private void addAllLeafs(List<KarinaFile> files) {
        files.addAll(this.leafs);
        for (var child : this.children) {
            child.addAllLeafs(files);
        }
    }


    public String toPrettyString() {
        return prettyString(0);
    }
    private String prettyString(int level) {
        var sb = new StringBuilder();
        sb.append("    ".repeat(level)).append(this.name).append(" path: ").append(this.path).append("\n");
        for (var child : this.children) {
            sb.append(child.prettyString(level + 1));
        }
        for (var leaf : this.leafs) {
            var state = "Raw";
            if (leaf.isTyped()) {
                state = "Typed";
            }
            sb.append("    ".repeat(level + 1)).append(leaf.name()).append(" path: ").append(leaf.path()).append(" state: ").append(state).append("\n");
        }
        return sb.toString();
    }
}
