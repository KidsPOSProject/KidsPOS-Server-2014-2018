package info.nukoneko.kidspos.api;

import info.nukoneko.cuc.kidspos4j.model.JSONConvertor;
import info.nukoneko.cuc.kidspos4j.model.ModelStore;
import info.nukoneko.cuc.kidspos4j.model.StoreFactory;
import rx.Observable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.util.ArrayList;

@Path("store")
public class Store {
    @GET
    @Path("list")
    public String getStore(
            @QueryParam("limit") Integer limit){
        ArrayList<ModelStore> baseList = StoreFactory.getInstance().findAll();
        if (limit == null){
            return JSONConvertor.toJSON(baseList);
        }
        Observable<ModelStore> list =
                Observable.from(baseList.toArray(new ModelStore[baseList.size()]));

        return JSONConvertor
                .toJSON(list.limit(limit).toList().toBlocking().single());
    }

}
