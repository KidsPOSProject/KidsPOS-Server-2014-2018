package info.nukoneko.kidspos.api;

import info.nukoneko.kidspos.database.ItemFactory;
import info.nukoneko.kidspos.model.ItemModel;
import info.nukoneko.kidspos.util.converter.JSON;

import javax.ws.rs.*;
import java.util.ArrayList;

/**
 * Created by TEJNEK on 2016/01/31.
 */
@Path("item")
public class Item {
    @GET
    @Produces("text/html")
    public String getItemList(){
        String res = "<table border=\"1\">";
        res += "<tr>" +
                "<td>ID</td>" +
                "<td>BARCODE</td>" +
                "<td>NAME</td>" +
                "<td>PRICE</td>" +
                "<td>SHOP</td>" +
                "<td>GENRE</td>" +
                "</tr>";
        for (ItemModel itemModel : ItemFactory.getInstance().findAll()){
            res += "<tr>";
            res += String.format("<td>%d</td>", itemModel.id);
            res += String.format("<td>%s</td>", itemModel.barcode);
            res += String.format("<td>%s</td>", itemModel.name);
            res += String.format("<td>%d</td>", itemModel.price);
            res += String.format("<td>%d</td>", itemModel.shop);
            res += String.format("<td>%s</td>", itemModel.genre);
            res += "</tr>";
        }
        res += "</table>";
        return res;
    }

    @GET
    @Path("list")
    @Produces("application/json")
    public String getItemListArray(){
        ArrayList<ItemModel> list = ItemFactory.getInstance().findAll();
        return JSON.covertJSON(list);
    }

//    @POST
//    @Path("add")
//    public void insertItem(@FormParam("barcode") String barcode, @FormParam("name") String name, @FormParam("price") int price){
//        ItemModel model = new ItemModel(barcode, name, price, 999, "Test");
//        ItemFactory.getInstance().insert(model);
//    }

}
