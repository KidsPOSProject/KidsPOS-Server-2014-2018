package info.nukoneko.kidspos.api;


import info.nukoneko.kidspos4j.exception.CannotCreateItemException;
import info.nukoneko.kidspos4j.model.DataStaffImpl;
import info.nukoneko.kidspos4j.model.JSONConvertor;
import info.nukoneko.kidspos4j.model.ModelStaff;
import info.nukoneko.kidspos4j.model.StaffFactory;

import javax.ws.rs.*;

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
