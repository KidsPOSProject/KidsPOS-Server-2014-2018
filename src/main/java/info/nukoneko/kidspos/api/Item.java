package info.nukoneko.kidspos.api;

import info.nukoneko.kidspos4j.model.ItemFactory;
import info.nukoneko.kidspos4j.model.JSONConvertor;
import info.nukoneko.kidspos4j.model.ModelItem;
import rx.Observable;

import javax.ws.rs.*;
import java.util.ArrayList;

@Path("item")
public class Item {
    @GET
    @Path("list")
    @Produces("application/json")
    public String getItemListArray(@QueryParam("limit") Integer limit) {
        ArrayList<ModelItem> baseList = ItemFactory.getInstance().findAll();
        if (limit == null) {
            return JSONConvertor.toJSON(baseList);
        }
        Observable<ModelItem> list = Observable.from(baseList.toArray(new ModelItem[baseList.size()]));

        return JSONConvertor.toJSON(list.limit(limit).toList().toBlocking().single());
    }

    @GET
    @Path("{barcode}")
    public String readItem(@PathParam("barcode") String barcode) {
        ModelItem item = ItemFactory.getInstance().findFromBarcode(barcode);
        if (item == null) {
            return "";
        } else {
            return JSONConvertor.toJSON(item);
        }
    }

    @POST
    @Path("create")
    public String createItem(@FormParam("itemName") String name,
                             @FormParam("storeId") int storeId,
                             @FormParam("genreId") int genreId,
                             @FormParam("price") int price) {
        ModelItem item = ItemFactory.getInstance()
                .createNewItem(name, storeId, genreId, price);
        if (item == null) {
            return "";
        } else {
            return JSONConvertor.toJSON(item);
        }
    }

    @POST
    @Path("{barcode}/update")
    public String updateItem(
            @PathParam("barcode") String barcode,
            @FormParam("itemName") String name,
            @FormParam("storeId") int storeId,
            @FormParam("genreId") int genreId,
            @FormParam("price") int price
    ) {

        ModelItem item =
                ItemFactory.getInstance()
                        .findFromBarcode(barcode);
        if (item == null) {
            return "";
        }

        item.setName(name);
        item.setStoreId(storeId);
        item.setGenreId(genreId);
        item.setPrice(price);

        if (ItemFactory.getInstance().update(item)) {
            return JSONConvertor.toJSON(item);
        } else {
            return "";
        }
    }
}
