package info.nukoneko.kidspos.api;
import info.nukoneko.kidspos4j.model.*;
import rx.Observable;

import javax.ws.rs.*;
import java.util.ArrayList;

/**
 * Created atsumi on 2016/01/29.
 */

/** Memo
 * @QueryParam Get等の ?name=hoge
 * @FormParam POST等の ?name=hoge
 * @CookieParam Cookie等の ?name=Hoge
 *
 * @Consumes(MediaType.$type) 受け取るデータ
 * @Produces(MediaType.$type) 返すデータ
 */
@Path("sale")
public class Sales {
    @GET
    @Produces("text/html")
    public String getSalesList(){
        String res = "<table border=\"1\">";
        res += "<tr>" +
                "<td>ID</td>" +
                "<td>BARCODE</td>" +
                "<td>CREATED_AT</td>" +
                "<td>POINTS</td>" +
                "<td>PRICE</td>" +
                "<td>ITEMS</td>" +
                "<td>STORE</td>" +
                "<td>STAFF</td>" +
                "</tr>";
        for (ModelSale item : SaleFactory.getInstance().findAll()){
            res += "<tr>";
            res += String.format("<td>%d</td>", item.getId());
            res += String.format("<td>%s</td>", item.getBarcode());
            res += String.format("<td>%s</td>", item.getCreatedAt());
            res += String.format("<td>%d</td>", item.getPoints());
            res += String.format("<td>%d</td>", item.getPrice());
            res += String.format("<td>%s</td>", item.getItems());
            res += String.format("<td>%d</td>", item.getStore());
            res += String.format("<td>%d</td>", item.getStaff());
            res += "</tr>";
        }
        res += "</table>";
        return res;
    }

    /***
     * 全ての売上
     * @param storeId 対象の商店ID
     * @return 売上リスト
     */
    @GET
    @Path("{storeId}")
    public String getSales(@PathParam("storeId") int storeId){
        return "GET " + String.valueOf(storeId);
    }

    /***
     * 一つの取引の詳細
     * @param storeId 対象の商店ID
     * @param saleId 取引ID
     * @return 取引詳細
     */
    @GET
    @Path("{storeId}/{saleId}")
    public String getSalesDetail(@PathParam("storeId") int storeId, @PathParam("saleId") int saleId){
        return "GET " + String.valueOf(storeId) + " " + String.valueOf(saleId);
    }

    /***
     * 新しい取引データを作成
     * @param storeId 対象の商店ID
     * @return 取引詳細
     */
    @POST
    @Path("{storeId}")
    public String createSales(@PathParam("storeId") int storeId, @FormParam("tan") String data){
        return "nya";
    }

    /***
     * 取引データを更新
     * @param storeId 対象の商店ID
     * @param saleId 取引ID
     * @return 取引詳細
     */
    @PUT
    @Path("{storeId}/{saleId}")
    public String updateSales(@PathParam("storeId") int storeId, @PathParam("saleId") int saleId){
        return "PUT " + String.valueOf(storeId) + " " + String.valueOf(saleId);
    }

    /***
     * 取引データを削除
     * @param storeId 対象の商店ID
     * @param saleId 取引ID
     * @return 成功かどうか
     */
    @DELETE
    @Path("{storeId}/{saleId}")
    public String deleteSales(@PathParam("storeId") int storeId, @PathParam("saleId") int saleId){
        return "DELETE " + String.valueOf(storeId) + " " + String.valueOf(saleId);
    }
    @GET
    @Path("list")
    @Produces("application/json")
    public String getSaleListArray(@QueryParam("limit") Integer limit){
        ArrayList<ModelSale> baseList = SaleFactory.getInstance().findAll();
        if (limit == null){
            return JSONConvertor.toJSON(baseList);
        }
        Observable<ModelSale> list = Observable.from(baseList.toArray(new ModelSale[baseList.size()]));

        return JSONConvertor.toJSON(list.limit(limit).toList().toBlocking().single());
    }
}