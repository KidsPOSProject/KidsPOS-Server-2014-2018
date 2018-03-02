package info.nukoneko.kidspos.print;

public class PrintManager {
    public enum PRINTER_TYPE {
        PRINTER_100("192.168.0.100", 1),
        PRINTER_101("192.168.0.101", 2);

        private final String name;
        private final int printableStoreId;

        PRINTER_TYPE(String name, int printableStoreId) {
            this.name = name;
            this.printableStoreId = printableStoreId;
        }

        public String getName() {
            return name;
        }

        /**
         * 出力するプリンタを取得します
         * @param storeId お店のID
         * @return プリンターの種類
         */
        public static PRINTER_TYPE valueOf(int storeId) {
            for (PRINTER_TYPE type : PRINTER_TYPE.values()) {
                if (type.printableStoreId == storeId) return type;
            }
            return null;
        }
    }
}
