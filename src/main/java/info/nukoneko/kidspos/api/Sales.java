package info.nukoneko.kidspos.api;

import info.nukoneko.kidspos.print.ItemPrintObject;
import info.nukoneko.kidspos.print.ItemPrintable;
import info.nukoneko.kidspos.print.PrintManager;
import info.nukoneko.kidspos4j.model.*;
import info.nukoneko.kidspos4j.util.config.BarcodeCreator;
import javafx.util.Pair;
import rx.Observable;

import javax.ws.rs.*;
import java.util.ArrayList;


@Path("sale")
public class Sales {
    @GET
    @Path("list")
    @Produces("application/json")
    public String getItemListArray(@QueryParam("limit") Integer limit){
        ArrayList<ModelSale> baseList = SaleFactory.getInstance().findAll();
        if (limit == null){
            return JSONConvertor.toJSON(baseList);
        }
        Observable<ModelSale> list = Observable.from(baseList.toArray(new ModelSale[baseList.size()]));

        return JSONConvertor.toJSON(list.limit(limit).toList().toBlocking().single());
    }

    @GET
    @Path("{barcode}")
    public String getSale(@PathParam("barcode") String barcode){
        ModelSale sale = SaleFactory.getInstance().findFromBarcode(barcode);
        if (sale == null){
            return "";
        } else {
            return JSONConvertor.toJSON(sale);
        }
    }


    @POST
    @Path("create")
    public String createSale(
            @FormParam("received") int receivedRiver,
            @FormParam("points") int points,
                             @FormParam("price") int price,
                             @FormParam("items") String items,
                             @FormParam("storeId") int storeId,
                             @FormParam("staffBarcode") String staffBarcode) {

        System.out.println(receivedRiver);
        System.out.println(points);
        System.out.println(price);
        System.out.println(items);
        System.out.println(storeId);
        System.out.println(staffBarcode);

        int staffId = 0;
        if (!staffBarcode.isEmpty()) {
            staffId = Integer.parseInt(staffBarcode
                    .substring(staffBarcode.length() -
                            BarcodeCreator.MAX_ITEM_LENGTH));
        }
        ModelSale sale = SaleFactory.getInstance()
                .createNewSale(
                        points,
                        price,
                        items,
                        storeId,
                        staffId);

        System.out.println(sale);
        if (sale == null) {
            return "";
        } else {
            try {
                System.out.println("====== Store Process =======");
                ArrayList<ModelStore> stores = StoreFactory.getInstance().find("id = '" + String.valueOf(storeId) + "'");
                String storeName = "";
                if (stores.size() > 0) {
                    storeName = stores.get(0).getName();
                }

                System.out.println("====== Staff Process =======");
                ModelStaff staffs
                        = StaffFactory.getInstance().findFromBarcode(staffBarcode);
                String staffName = "";
                if (staffs != null ) {
                    staffName = staffs.getName();
                }

                System.out.println("====== Print Process =======");
                ItemPrintObject itemPrintObject = new ItemPrintObject(storeName, storeId, staffName, receivedRiver);
                String[] itemIds = items.split(",");

                DataItemImpl itemFunc = ItemFactory.getInstance();
                for (String itemId : itemIds){
                    try {
                        int _itemId = Integer.parseInt(itemId);
                        ModelItem item = itemFunc.findFirst("id = '" + _itemId + "'");
                        itemPrintObject.items.add(new Pair<>(item.getName(), item.getPrice()));
                    } catch (Exception ignored) {
                    }
                }

                PrintManager.printReceipt(new ItemPrintable(itemPrintObject));
                return JSONConvertor.toJSON(sale);

            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
    }

    @POST
    @Path("{barcode}/update")
    public String updateSale(
            @PathParam("barcode") String barcode,
            @FormParam("points") int points,
            @FormParam("price") int price,
            @FormParam("items") String items,
            @FormParam("storeId") int storeId,
            @FormParam("staffId") int staffId) {
        ModelSale sale = SaleFactory.getInstance().findFromBarcode(barcode);
        if (sale == null) {
            return "";
        }

        sale.setPoints(points);
        sale.setPrice(price);
        sale.setItems(items);
        sale.setStoreId(storeId);
        sale.setStaffId(staffId);

        if (SaleFactory.getInstance().update(sale)) {
            return JSONConvertor.toJSON(sale);
        } else {
            return "";
        }
    }

//    /***
//     * 全ての売上
//     * @param storeId 対象の商店ID
//     * @return 売上リスト
//     */
//    @GET
//    @Path("{storeId}")
//    public String getSales(@PathParam("storeId") int storeId){
//        return "GET " + String.valueOf(storeId);
//    }
//
//    /***
//     * 一つの取引の詳細
//     * @param storeId 対象の商店ID
//     * @param saleId 取引ID
//     * @return 取引詳細
//     */
//    @GET
//    @Path("{storeId}/{saleId}")
//    public String getSalesDetail(@PathParam("storeId") int storeId, @PathParam("saleId") int saleId){
//        return "GET " + String.valueOf(storeId) + " " + String.valueOf(saleId);
//    }
//
//    /***
//     * 新しい取引データを作成
//     * @param storeId 対象の商店ID
//     * @return 取引詳細
//     */
//    @POST
//    @Path("{storeId}")
//    public String createSales(@PathParam("storeId") int storeId, @FormParam("tan") String data){
//        return "nya";
//    }
//
//    /***
//     * 取引データを更新
//     * @param storeId 対象の商店ID
//     * @param saleId 取引ID
//     * @return 取引詳細
//     */
//    @PUT
//    @Path("{storeId}/{saleId}")
//    public String updateSales(@PathParam("storeId") int storeId, @PathParam("saleId") int saleId){
//        return "PUT " + String.valueOf(storeId) + " " + String.valueOf(saleId);
//    }
//
//    /***
//     * 取引データを削除
//     * @param storeId 対象の商店ID
//     * @param saleId 取引ID
//     * @return 成功かどうか
//     */
//    @DELETE
//    @Path("{storeId}/{saleId}")
//    public String deleteSales(@PathParam("storeId") int storeId, @PathParam("saleId") int saleId){
//        return "DELETE " + String.valueOf(storeId) + " " + String.valueOf(saleId);
//    }
//
//    @GET
//    @Path("list")
//    @Produces("application/json")
//    public String getSaleListArray(@QueryParam("limit") Integer limit){
//        ArrayList<ModelSale> baseList = SaleFactory.getInstance().findAll();
//        if (limit == null){
//            return JSONConvertor.toJSON(baseList);
//        }
//        Observable<ModelSale> list = Observable.from(baseList.toArray(new ModelSale[baseList.size()]));
//
//        return JSONConvertor.toJSON(list.limit(limit).toList().toBlocking().single());
//    }
}