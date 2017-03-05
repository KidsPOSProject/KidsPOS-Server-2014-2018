package info.nukoneko.kidspos.api;

import info.nukoneko.cuc.kidspos4j.model.*;
import info.nukoneko.cuc.kidspos4j.util.config.BarcodeRule;
import info.nukoneko.kidspos.print.ItemPrintObject;
import info.nukoneko.kidspos.print.ItemPrintable;
import info.nukoneko.kidspos.print.PrintManager;
import javafx.util.Pair;
import rx.Observable;

import javax.ws.rs.*;
import java.util.ArrayList;
import java.util.List;


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
                    .substring(staffBarcode.length() - BarcodeRule.MAX_TYPE_VALUE2_LENGTH));
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
                // getStoreName
                final ArrayList<ModelStore> stores = StoreFactory.getInstance().find(String.format("id = '%d'", storeId));
                final String storeName = stores.size() > 0 ? stores.get(0).getName() : "";

                // getStaffName
                final ModelStaff staff = StaffFactory.getInstance().findFromBarcode(staffBarcode);
                final String staffName = staff == null ? "" : staff.getName();

                // getItemLists
                final DataItemImpl itemFunc = ItemFactory.getInstance();
                final List<Pair<String, Integer>> itemLists = Observable.from(items.split(",")).map(itemId -> {
                    final ModelItem item = itemFunc.findFirst("id = '" + itemId + "'");
                    return new Pair<>(item.getName(), item.getPrice());
                }).toList().toBlocking().single();

                // Print
                PrintManager.printReceipt(new ItemPrintable(new ItemPrintObject(itemLists, storeName, storeId, staffName, receivedRiver)));
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