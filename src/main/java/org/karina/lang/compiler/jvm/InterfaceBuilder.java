package org.karina.lang.compiler.jvm;

import lombok.SneakyThrows;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.SignatureRemapper;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class InterfaceBuilder {
    private static final String text =
            "resources/jars/commons-cli-1.9.0.jar";

    @SneakyThrows
    public static void main(String[] args) {
        try (var jarFile = new JarFile(text)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                var entry = entries.nextElement();
                if (entry.getName().endsWith(".class")) {
                    try (var inputStream = jarFile.getInputStream(entry)) {
                        var reader = new ClassReader(inputStream);
                        var classNode = new ClassNode();
                        reader.accept(classNode, 0);


                        System.out.println("Class Name: " + classNode.name);
                        System.out.println("Super Class: " + classNode.superName);
                    }
                }
            }
        }
    }


}
