package info.nukoneko.kidspos.api;

import info.nukoneko.kidspos.database.SaleFactory;
import info.nukoneko.kidspos.database.StaffFactory;
import info.nukoneko.kidspos.model.SaleModel;
import info.nukoneko.kidspos.model.StaffModel;

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
        for (StaffModel item : StaffFactory.getInstance().findAll()){
            res += "<tr>";
            res += String.format("<td>%s</td>", item.barcode);
            res += String.format("<td>%s</td>", item.name);
            res += "</tr>";
        }
        res += "</table>";
        return res;
    }
}
