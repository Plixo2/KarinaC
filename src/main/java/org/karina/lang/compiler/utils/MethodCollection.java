package org.karina.lang.compiler.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.karina.lang.compiler.model_api.pointer.MethodPointer;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

@Getter
@Accessors(fluent = true)
public class MethodCollection implements Iterable<MethodPointer>{
    String name;
    List<MethodPointer> methods;

    public MethodCollection(String name, List<MethodPointer> methods) {
        this.name = name;
        this.methods = List.copyOf(methods);
    }

    public MethodCollection filter(Predicate<MethodPointer> predicate) {
        return new MethodCollection(this.name, this.methods.stream().filter(predicate).toList());
    }

    @Override
    public String toString() {
        return "MethodCollection{" + "name='" + this.name + '\'' + ", methods=" + this.methods + '}';
    }

    @Override
    public @NotNull Iterator<MethodPointer> iterator() {
        return this.methods.iterator();
    }

    public boolean isEmpty() {
        return this.methods.isEmpty();
    }
}
