package info.nukoneko.kidspos.api

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.WebApplicationException

@Path("/")
class Top {
    @GET
    fun root(): String {
        throw WebApplicationException(404)
    }
}
