package org.karina.lang.compiler.jvm.loading;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.util.jar.JarFile;

public class BytecodeLoading {

    public static OpenSet loadJarFile(JarFile jarFile, OpenSet openSet) throws IOException {

        var entries = jarFile.entries();

        while (entries.hasMoreElements()) {
            var entry = entries.nextElement();
            if (entry.getName().endsWith(".class")) {
                try (var inputStream = jarFile.getInputStream(entry)) {
                    var reader = new ClassReader(inputStream);
                    var classNode = new ClassNode();
                    reader.accept(
                            classNode, 0
//                            ClassReader.SKIP_CODE | ClassReader.SKIP_FRAMES
//                            ClassReader.EXPAND_FRAMES
                    );
                    if (!classNode.name.equals("module-info")) {
                        openSet.add(classNode, entry.getRealName());
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }


        return openSet;
    }


}
