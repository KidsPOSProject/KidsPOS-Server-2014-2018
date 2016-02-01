package info.nukoneko.kidspos.api;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.io.IOException;

/**
 * Created by TEJNEK on 2016/01/31.
 */
@Path("/")
public class Top extends HttpServlet {
    @GET
    public String welcome(){
        return "キッズビジネスタウン POSシステムサーバーです";
    }
}
