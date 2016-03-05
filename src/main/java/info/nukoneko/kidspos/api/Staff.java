package info.nukoneko.kidspos.api;


import info.nukoneko.kidspos4j.exception.CannotCreateItemException;
import info.nukoneko.kidspos4j.model.*;
import rx.Observable;

import javax.ws.rs.*;
import java.util.ArrayList;

/**
 * Created by TEJNEK on 2016/01/31.
 */
@Path("staff")
public class Staff {
//    @GET
//    @Produces("text/html")
//    public String getStaffList(){
//        String res = "<table border=\"1\">";
//        res += "<tr>" +
//                "<td>BARCODE</td>" +
//                "<td>NAME</td>" +
//                "</tr>";
//        for (ModelStaff item : StaffFactory.getInstance().findAll()){
//            res += "<tr>";
//            res += String.format("<td>%s</td>", item.getBarcode());
//            res += String.format("<td>%s</td>", item.getName());
//            res += "</tr>";
//        }
//        res += "</table>";
//        return res;
//    }

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
    @Path("/")
    public String createStaff(@FormParam("new_staff") String staffJson) {
        ModelStaff staff = JSONConvertor.parse(staffJson, ModelStaff.class);
        if (staff == null) {
            return "";
        } else {
            try {
                ModelStaff _staff = ((DataStaffImpl)StaffFactory.getInstance()).createNewStaff(staff.getName());
                // Print

                return JSONConvertor.toJSON(_staff);
            } catch (CannotCreateItemException e) {
                e.printStackTrace();
            }
            return "";
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
