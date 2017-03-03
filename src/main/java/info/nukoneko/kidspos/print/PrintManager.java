package info.nukoneko.kidspos.print;

import info.nukoneko.kidspos.util.KPLogger;

import javax.print.DocPrintJob;
import javax.print.PrintService;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterJob;

public class PrintManager {
    public enum PRINTER_TYPE {
        PRINTER_100("Printer100", 1),
        PRINTER_101("Printer101", 2);

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

    private final static PrintableInsets defaultInsets = new PrintableInsets(10, 10, 280, 400);

    public synchronized static boolean printReceipt(KPPrintable printable) {
        final DocPrintJob printJob = getPrinterJob(printable.gerUserPrinterType());
        if (printJob == null) return false;

        try {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintService(printJob.getPrintService());

            final Paper paper = new Paper() {{
                setImageableArea(defaultInsets.x, defaultInsets.y, defaultInsets.width, printable.getPrintableHeight());
            }};

            final PageFormat pageFormat = new PageFormat() {{
                setPaper(paper);
            }};

            job.setPrintable(printable, pageFormat);
            job.print();
        } catch (Exception e) {
            KPLogger.log(e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    private static DocPrintJob getPrinterJob(PRINTER_TYPE usePrinter) {
        if (usePrinter == null) return null;
        for (PrintService service : PrinterJob.lookupPrintServices()) {
            if (service.getName().equalsIgnoreCase(usePrinter.getName())) return service.createPrintJob();
        }
        return null;
    }
}
