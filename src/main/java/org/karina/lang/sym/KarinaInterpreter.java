package org.karina.lang.sym;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import lombok.AllArgsConstructor;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.objects.KTree;

import java.nio.charset.StandardCharsets;
import java.util.List;

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
