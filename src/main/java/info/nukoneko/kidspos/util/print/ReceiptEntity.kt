package info.nukoneko.kidspos.util.print

import info.nukoneko.cuc.kidspos4j.model.ModelItem

data class ReceiptEntity(val items: List<ModelItem>,
                         val storeName: String, val staffName: String,
                         val total: Int, val deposit: Int, val change: Int, val transactionId: String)