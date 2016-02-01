package info.nukoneko.kidspos.api;
import info.nukoneko.kidspos.database.ItemFactory;
import info.nukoneko.kidspos.database.SaleFactory;
import info.nukoneko.kidspos.model.ItemModel;
import info.nukoneko.kidspos.model.SaleModel;
import info.nukoneko.kidspos.util.converter.JSON;

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
        for (SaleModel item : SaleFactory.getInstance().findAll()){
            res += "<tr>";
            res += String.format("<td>%d</td>", item.id);
            res += String.format("<td>%s</td>", item.barcode);
            res += String.format("<td>%s</td>", item.createdAt);
            res += String.format("<td>%d</td>", item.points);
            res += String.format("<td>%d</td>", item.price);
            res += String.format("<td>%s</td>", item.items);
            res += String.format("<td>%d</td>", item.store);
            res += String.format("<td>%d</td>", item.staff);
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
}