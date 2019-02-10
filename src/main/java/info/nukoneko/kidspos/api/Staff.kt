package info.nukoneko.kidspos.api

import info.nukoneko.cuc.kidspos4j.model.JSONConvertor
import info.nukoneko.cuc.kidspos4j.model.StaffFactory
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.QueryParam

@Path("staff")
class Staff {
    @GET
    @Path("/")
    fun getStaff(@QueryParam("barcode") barcode: String): String {
        val staff = StaffFactory.getInstance().findFromBarcode(barcode)
        return if (staff == null) {
            ""
        } else {
            JSONConvertor.toJSON(staff)
        }
    }
}
