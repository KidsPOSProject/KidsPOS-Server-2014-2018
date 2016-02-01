package info.nukoneko.kidspos.api;

import info.nukoneko.kidspos.database.ItemGenreFactory;
import info.nukoneko.kidspos.database.StaffFactory;
import info.nukoneko.kidspos.model.ItemGenreModel;
import info.nukoneko.kidspos.model.StaffModel;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Created by TEJNEK on 2016/01/31.
 */
@Path("item_genre")
public class ItemGenre {
    @GET
    @Produces("text/html")
    public String getItemGenreList(){
        String res = "<table border=\"1\">";
        res += "<tr>" +
                "<td>ID</td>" +
                "<td>NAME</td>" +
                "<td>STORE</td>" +
                "</tr>";
        for (ItemGenreModel item : ItemGenreFactory.getInstance().findAll()){
            res += "<tr>";
            res += String.format("<td>%s</td>", item.id);
            res += String.format("<td>%s</td>", item.name);
            res += String.format("<td>%s</td>", item.store);
            res += "</tr>";
        }
        res += "</table>";
        return res;
    }
}
