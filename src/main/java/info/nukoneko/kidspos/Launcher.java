package info.nukoneko.kidspos;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import java.awt.*;
import java.net.URI;
import java.net.URL;
import java.security.ProtectionDomain;

public class Launcher {
    public static void main(String[] args) throws Exception {
        Server server = new Server(9500);
        WebAppContext context = new WebAppContext();
        context.setContextPath("/");

        ProtectionDomain protectionDomain = Launcher.class.getProtectionDomain();
        URL location = protectionDomain.getCodeSource().getLocation();
        context.setWar(location.toExternalForm());

        server.setHandler(context);
        server.start();

        try {
            server.start();

            if(Desktop.isDesktopSupported())
            {
                Desktop.getDesktop().browse(new URI("http://localhost:9500"));
            }

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
