package info.nukoneko.kidspos.api;

import info.nukoneko.kidspos.util.converter.JSON;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by TEJNEK on 2016/01/31.
 */

// /info
@Path("info")
public class Information {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getVersion(){
        return "0.0.1";
    }

    // /info/yama
    @GET
    @Path("yama")
    public String getTin(){
        return "tin";
    }
}
