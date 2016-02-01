package info.nukoneko.kidspos.api;

import info.nukoneko.kidspos.database.StaffFactory;
import info.nukoneko.kidspos.database.StoreFactory;
import info.nukoneko.kidspos.model.StaffModel;
import info.nukoneko.kidspos.model.StoreModel;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Created by TEJNEK on 2016/01/31.
 */
@Path("store")
public class Store {
    @GET
    @Produces("text/html")
    public String getStoreList(){
        String res = "<table border=\"1\">";
        res += "<tr>" +
                "<td>ID</td>" +
                "<td>NAME</td>" +
                "</tr>";
        for (StoreModel item : StoreFactory.getInstance().findAll()){
            res += "<tr>";
            res += String.format("<td>%s</td>", item.id);
            res += String.format("<td>%s</td>", item.name);
            res += "</tr>";
        }
        res += "</table>";
        return res;
    }
}
