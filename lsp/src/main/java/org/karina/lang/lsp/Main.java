package org.karina.lang.lsp;

import org.eclipse.lsp4j.launch.LSPLauncher;
import org.karina.lang.lsp.base.EventLanguageServer;



///
/// Main class for the Language Server.
///
public class Main {


    public static void main(String[] args) {
        var server = new EventLanguageServer(new KarinaLSP());
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
