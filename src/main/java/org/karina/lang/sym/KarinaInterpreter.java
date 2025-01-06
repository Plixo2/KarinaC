package org.karina.lang.sym;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.primitives.Longs;
import lombok.AllArgsConstructor;
import org.karina.lang.compiler.ObjectPath;
import org.karina.lang.compiler.objects.KTree;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
public class KarinaInterpreter {
    private KTree.KPackage environment;
    
    public void run(ObjectPath path) {
        var vm = new VM();

    }
    public static long hashList(List<String> list) {
        var hashFunction = Hashing.murmur3_128();
        StringBuilder concatenated = new StringBuilder();
        for (String s : list) {
            concatenated.append(s).append("\u0000");
        }

        HashCode hashCode = hashFunction.hashString(concatenated.toString(), StandardCharsets.UTF_8);
        return hashCode.asLong();
    }
}
