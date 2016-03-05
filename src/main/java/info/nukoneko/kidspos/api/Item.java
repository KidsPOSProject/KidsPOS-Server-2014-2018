package info.nukoneko.kidspos.api;

import info.nukoneko.kidspos4j.model.ItemFactory;
import info.nukoneko.kidspos4j.model.JSONConvertor;
import info.nukoneko.kidspos4j.model.ModelItem;
import rx.Observable;
import rx.Observer;

import javax.ws.rs.*;
import java.util.ArrayList;

/**
 * Created by TEJNEK on 2016/01/31.
 */
@Path("item")
public class Item {
//    @GET
//    @Produces("text/html")
//    public String getItemList(){
//        String res = "<table border=\"1\">";
//        res += "<tr>" +
//                "<td>ID</td>" +
//                "<td>BARCODE</td>" +
//                "<td>NAME</td>" +
//                "<td>PRICE</td>" +
//                "<td>SHOP</td>" +
//                "<td>GENRE</td>" +
//                "</tr>";
//        for (ModelItem itemModel : ItemFactory.getInstance().findAll()){
//            res += "<tr>";
//            res += String.format("<td>%d</td>", itemModel.getId());
//            res += String.format("<td>%s</td>", itemModel.getBarcode());
//            res += String.format("<td>%s</td>", itemModel.getName());
//            res += String.format("<td>%d</td>", itemModel.getPrice());
//            res += String.format("<td>%d</td>", itemModel.getStoreId());
//            res += String.format("<td>%s</td>", itemModel.getGenreId());
//            res += "</tr>";
//        }
//        res += "</table>";
//        return res;
//    }

    @GET
    @Path("list")
    @Produces("application/json")
    public String getItemListArray(@QueryParam("limit") Integer limit){
        ArrayList<ModelItem> baseList = ItemFactory.getInstance().findAll();
        if (limit == null){
            return JSONConvertor.toJSON(baseList);
        }
        Observable<ModelItem> list = Observable.from(baseList.toArray(new ModelItem[baseList.size()]));

        return JSONConvertor.toJSON(list.limit(limit).toList().toBlocking().single());
    }

    @GET
    @Path("/")
    public String getItem(@QueryParam("barcode") String barcode){
        ModelItem item = ItemFactory.getInstance().findFromBarcode(barcode);
        if (item == null) {
            return "";
        } else {
            return JSONConvertor.toJSON(item);
        }
    }

    /*
    String name,
                                   Integer storeId,
                                   Integer genreId,
                                   Integer price
     */

    @POST
    @Path("create")
    public String createItem(@FormParam("itemName") String name,
                             @FormParam("storeId") int storeId,
                             @FormParam("genreId") int genreId,
                             @FormParam("price") int price){
        ModelItem item = ItemFactory.getInstance()
                .createNewItem(name, storeId, genreId, price);
        if (item == null){
            return "";
        } else {
            return JSONConvertor.toJSON(item);
        }
    }

    @POST
    @Path("/")
    public String updatePrice(@FormParam("barcode") String barcode, @FormParam("new_price") Integer newPrice){
        ModelItem item = ItemFactory.getInstance().findFromBarcode(barcode);
        if (item == null) {
            return "";
        } else {
            item.setPrice(newPrice);
        }
        if (ItemFactory.getInstance().update(item)){
            return JSONConvertor.toJSON(item);
        } else {
            return "";
        }
    }
}
