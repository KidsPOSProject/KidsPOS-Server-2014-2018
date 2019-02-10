package info.nukoneko.kidspos.api

import info.nukoneko.cuc.kidspos4j.model.JSONConvertor
import info.nukoneko.cuc.kidspos4j.model.StoreFactory
import rx.Observable
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.QueryParam

@Path("store")
class Store {
    @GET
    @Path("list")
    fun getStore(
            @QueryParam("limit") limit: Int?): String {
        val baseList = StoreFactory.getInstance().findAll()
        if (limit == null) {
            return JSONConvertor.toJSON(baseList)
        }
        val list = Observable.from(baseList.toTypedArray())

        return JSONConvertor
                .toJSON(list.limit(limit).toList().toBlocking().single())
    }
}