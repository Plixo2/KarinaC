package org.karina.lang.lsp;

import org.eclipse.lsp4j.launch.LSPLauncher;
import org.karina.lang.lsp.lib.VirtualFileSystem;

import java.net.URI;

///
/// Main class for the Language Server.
///
public class Main {

    public static void main(String[] args) {
        URI a = VirtualFileSystem.toUri("file:///Users/me/../me/project/Main.java");
        URI b = VirtualFileSystem.toUri("file:///Users/me/project/./Main.java");

        System.out.println(a.equals(b)); // true
        System.out.println(a.hashCode() == b.hashCode()); // true


        var server = new KarinaLanguageServer();
        var launcher = LSPLauncher.createServerLauncher(server, System.in, System.out);
        server.connect(launcher.getRemoteProxy());
        try {
            launcher.startListening().get();
            System.exit(0);
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }


}
