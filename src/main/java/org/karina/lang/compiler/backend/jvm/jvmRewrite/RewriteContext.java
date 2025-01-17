package org.karina.lang.compiler.backend.jvm.jvmRewrite;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Variable;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class RewriteContext {
    private final ObjectPath baseInterfacePath;
    @Getter
    private final ObjectPath unitPath;

    @Getter
    private int itemCounter = 0;

    @Getter
    private final List<KTree.KItem> addedItems = new ArrayList<>();


    public void add(KTree.KItem item) {
        this.addedItems.add(item);
        this.itemCounter += 1;
    }

    public List<Variable> toReplaceWithSelf = new ArrayList<>();

}
