package info.nukoneko.kidspos.api

import info.nukoneko.cuc.kidspos4j.model.ItemFactory
import info.nukoneko.cuc.kidspos4j.model.JSONConvertor
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam

@Path("item")
class Item {
    @GET
    @Path("{barcode}")
    fun readItem(@PathParam("barcode") barcode: String): String {
        val item = ItemFactory.getInstance().findFromBarcode(barcode)
        return if (item == null) {
            "{}"
        } else {
            JSONConvertor.toJSON(item)
        }
    }
}
