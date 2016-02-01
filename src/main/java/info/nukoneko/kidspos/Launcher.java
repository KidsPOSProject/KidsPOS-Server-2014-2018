package info.nukoneko.kidspos;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import java.net.URL;
import java.security.ProtectionDomain;

/**
 * Created by TEJNEK on 2016/02/01.
 */
public class Launcher {
    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        WebAppContext context = new WebAppContext();
        context.setContextPath("/");

        ProtectionDomain protectionDomain = Launcher.class.getProtectionDomain();
        URL location = protectionDomain.getCodeSource().getLocation();
        context.setWar(location.toExternalForm());

        server.setHandler(context);
        server.start();

        try {
            server.start();
            System.err.println("Press ENTER to exit.");
            System.in.read();
            server.stop();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
