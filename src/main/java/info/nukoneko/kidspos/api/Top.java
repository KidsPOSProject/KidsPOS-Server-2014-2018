package info.nukoneko.kidspos.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;

@Path("/")
public class Top {
    @GET
    public String root(){
        throw new WebApplicationException(404);
    }
}
