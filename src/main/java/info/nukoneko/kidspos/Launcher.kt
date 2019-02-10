package info.nukoneko.kidspos

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.webapp.WebAppContext
import java.awt.Desktop
import java.net.URI

object Launcher {
    @Throws(Exception::class)
    @JvmStatic
    fun main(args : Array<String>) {
        val server = Server(9500)
        val context = WebAppContext()
        context.contextPath = "/"

        val protectionDomain = Launcher::class.java.protectionDomain
        val location = protectionDomain.codeSource.location
        context.war = location.toExternalForm()

        server.handler = context
        server.start()

        try {
            server.start()

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(URI("http://localhost:9500"))
            }

            System.err.println("Press ENTER to exit.")
            System.`in`.read()
            server.stop()
            server.join()
        } catch (e: Exception) {
            e.printStackTrace()
            System.exit(1)
        }
    }
}
