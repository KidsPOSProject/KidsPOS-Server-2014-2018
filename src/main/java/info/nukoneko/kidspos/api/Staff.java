package info.nukoneko.kidspos.api;


import info.nukoneko.cuc.kidspos4j.model.JSONConvertor;
import info.nukoneko.cuc.kidspos4j.model.ModelStaff;
import info.nukoneko.cuc.kidspos4j.model.StaffFactory;
import rx.Observable;

import javax.ws.rs.*;
import java.util.ArrayList;

@Path("staff")
public class Staff {
    @GET
    @Path("list")
    public String getStaff(@QueryParam("limit") Integer limit){
        ArrayList<ModelStaff> baseList = StaffFactory.getInstance().findAll();
        if (limit == null){
            return JSONConvertor.toJSON(baseList);
        }
        Observable<ModelStaff> list =
                Observable.from(baseList.toArray(new ModelStaff[baseList.size()]));

        return JSONConvertor
                .toJSON(list.limit(limit).toList().toBlocking().single());
    }

    @POST
    @Path("create")
    public String createStaff(
            @FormParam("name") String name) {
        ModelStaff staff =
                StaffFactory.getInstance().createNewStaff(name);
        if (staff == null) {
            return "";
        } else {
            return JSONConvertor.toJSON(staff);
        }
    }

    @GET
    @Path("/")
    public String getStaff(@QueryParam("barcode") String barcode){
        ModelStaff staff = StaffFactory.getInstance().findFromBarcode(barcode);
        if (staff == null){
            return "";
        } else {
            return JSONConvertor.toJSON(staff);
        }
    }
}
