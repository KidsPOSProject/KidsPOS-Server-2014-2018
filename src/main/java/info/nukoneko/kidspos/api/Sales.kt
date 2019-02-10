package info.nukoneko.kidspos.api

import info.nukoneko.cuc.kidspos4j.model.*
import info.nukoneko.cuc.kidspos4j.util.config.BarcodeRule
import info.nukoneko.kidspos.print.PrinterSettings
import info.nukoneko.kidspos.util.print.PrintOrder
import info.nukoneko.kidspos.util.print.ReceiptEntity
import rx.Observable
import javax.ws.rs.FormParam
import javax.ws.rs.POST
import javax.ws.rs.Path

@Path("sale")
class Sales {
    @POST
    @Path("create")
    fun createSale(
            @FormParam("received") receivedRiver: Int,
            @FormParam("points") points: Int,
            @FormParam("price") price: Int,
            @FormParam("items") items: String,
            @FormParam("storeId") storeId: Int,
            @FormParam("staffBarcode") staffBarcode: String): String {
        var staffId = 0
        if (!staffBarcode.isEmpty()) {
            staffId = Integer.parseInt(staffBarcode
                    .substring(staffBarcode.length - BarcodeRule.MAX_TYPE_VALUE2_LENGTH))
        }
        val sale = SaleFactory.getInstance()
                .createNewSale(
                        points,
                        price,
                        items,
                        storeId,
                        staffId)
        return if (sale == null) {
            ""
        } else {
            try {
                // getStoreName
                val stores = StoreFactory.getInstance().find(String.format("id = '%d'", storeId))
                val storeName = if (stores.size > 0) stores[0].name else ""

                // getStaffName
                val staff = StaffFactory.getInstance().findFromBarcode(staffBarcode)
                val staffName = if (staff == null) "" else staff.name

                // getItemLists
                val itemFunc = ItemFactory.getInstance()
                val itemLists = Observable.from(items.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()).map { itemId -> itemFunc.findFirst("id = '$itemId'") }.toList().toBlocking().single()

                // Print
                val printOrder = PrintOrder(PrinterSettings.valueOf(storeId)!!.host, 9100)

                val printObject = ReceiptEntity(
                        itemLists, storeName, staffName,
                        sale.price!!, receivedRiver, sale.price!! - receivedRiver, sale.barcode
                )
                printOrder.print(printObject)

                JSONConvertor.toJSON(sale)

            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }

        }
    }
}