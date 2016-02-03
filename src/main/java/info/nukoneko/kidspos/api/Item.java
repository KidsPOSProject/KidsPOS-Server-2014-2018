package info.nukoneko.kidspos.api;

import info.nukoneko.kidspos4j.model.ItemFactory;
import info.nukoneko.kidspos4j.model.JSONConverter;
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
        for (ModelItem itemModel : ItemFactory.getInstance().findAll()){
            res += "<tr>";
            res += String.format("<td>%d</td>", itemModel.getId());
            res += String.format("<td>%s</td>", itemModel.getBarcode());
            res += String.format("<td>%s</td>", itemModel.getName());
            res += String.format("<td>%d</td>", itemModel.getPrice());
            res += String.format("<td>%d</td>", itemModel.getShop());
            res += String.format("<td>%s</td>", itemModel.getGenre());
            res += "</tr>";
        }
        res += "</table>";
        return res;
    }

    @GET
    @Path("list")
    @Produces("application/json")
    public String getItemListArray(@QueryParam("limit") Integer limit){
        ArrayList<ModelItem> baseList = ItemFactory.getInstance().findAll();
        if (limit == null){
            return JSONConverter.toJSON(baseList);
        }
        Observable<ModelItem> list = Observable.from(baseList.toArray(new ModelItem[baseList.size()]));

        return JSONConverter.toJSON(list.limit(limit).toList().toBlocking().single());
    }

//    @POST
//    @Path("add")
//    public void insertItem(@FormParam("barcode") String barcode, @FormParam("name") String name, @FormParam("price") int price){
//        ItemModel model = new ItemModel(barcode, name, price, 999, "Test");
//        ItemFactory.getInstance().insert(model);
//    }

}
