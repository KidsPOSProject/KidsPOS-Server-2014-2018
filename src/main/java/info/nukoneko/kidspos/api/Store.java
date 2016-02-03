package info.nukoneko.kidspos.api;

import info.nukoneko.kidspos4j.model.ModelStore;
import info.nukoneko.kidspos4j.model.StoreFactory;

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
        for (ModelStore item : StoreFactory.getInstance().findAll()){
            res += "<tr>";
            res += String.format("<td>%s</td>", item.getId());
            res += String.format("<td>%s</td>", item.getName());
            res += "</tr>";
        }
        res += "</table>";
        return res;
    }
}
