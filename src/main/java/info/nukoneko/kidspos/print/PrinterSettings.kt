package info.nukoneko.kidspos.print

enum class PrinterSettings(val host: String, private val printableStoreId: Int) {
    PRINTER_100("192.168.0.100", 1),
    PRINTER_101("192.168.0.101", 2);

    companion object {
        /**
         * 出力するプリンタを取得します
         * @param storeId お店のID
         * @return プリンターの種類
         */
        fun valueOf(storeId: Int): PrinterSettings? {
            for (type in PrinterSettings.values()) {
                if (type.printableStoreId == storeId) return type
            }
            return null
        }
    }
}
