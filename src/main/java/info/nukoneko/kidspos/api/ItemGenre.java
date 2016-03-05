package info.nukoneko.kidspos.api;

import info.nukoneko.kidspos4j.model.*;
import rx.Observable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.ArrayList;

/**
 * Created by TEJNEK on 2016/01/31.
 */
@Path("item_genre")
public class ItemGenre {
//    @GET
//    @Produces("text/html")
//    public String getItemGenreList(){
//        String res = "<table border=\"1\">";
//        res += "<tr>" +
//                "<td>ID</td>" +
//                "<td>NAME</td>" +
//                "<td>STORE</td>" +
//                "</tr>";
//        for (ModelItemGenre item : ItemGenreFactory.getInstance().findAll()){
//            res += "<tr>";
//            res += String.format("<td>%s</td>", item.getId());
//            res += String.format("<td>%s</td>", item.getName());
//            res += String.format("<td>%s</td>", item.getStore());
//            res += "</tr>";
//        }
//        res += "</table>";
//        return res;
//    }

    @GET
    @Path("list")
    @Produces("application/json")
    public String getItemGenreListArray(@QueryParam("limit") Integer limit){
        ArrayList<ModelItemGenre> baseList = ItemGenreFactory.getInstance().findAll();
        if (limit == null){
            return JSONConvertor.toJSON(baseList);
        }
        Observable<ModelItemGenre> list =
                Observable.from(baseList.toArray(new ModelItemGenre[baseList.size()]));

        return JSONConvertor.toJSON(list.limit(limit).toList().toBlocking().single());
    }

}
