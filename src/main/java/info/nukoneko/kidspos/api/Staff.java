package info.nukoneko.kidspos.api;


import info.nukoneko.kidspos4j.model.ModelStaff;
import info.nukoneko.kidspos4j.model.StaffFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Created by TEJNEK on 2016/01/31.
 */
@Path("staff")
public class Staff {
    @GET
    @Produces("text/html")
    public String getStaffList(){
        String res = "<table border=\"1\">";
        res += "<tr>" +
                "<td>BARCODE</td>" +
                "<td>NAME</td>" +
                "</tr>";
        for (ModelStaff item : StaffFactory.getInstance().findAll()){
            res += "<tr>";
            res += String.format("<td>%s</td>", item.getBarcode());
            res += String.format("<td>%s</td>", item.getName());
            res += "</tr>";
        }
        res += "</table>";
        return res;
    }
}
