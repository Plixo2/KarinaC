package org.karina.lang.lsp;

import org.eclipse.lsp4j.launch.LSPLauncher;
import org.karina.lang.lsp.internal.KarinaServer;


public class Main {
    public static void main(String[] args) {
        var server = new KarinaServer();
        var inputStream = System.in;
        var outputStream = System.out;
        var launcher = LSPLauncher.createServerLauncher(server, inputStream, outputStream);
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
