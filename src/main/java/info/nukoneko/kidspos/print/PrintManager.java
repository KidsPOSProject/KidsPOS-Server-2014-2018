package info.nukoneko.kidspos.print;

import info.nukoneko.kidspos.util.KPLogger;

import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterJob;

/**
 * Created by b140096 on 2016/03/06.
 */
public class PrintManager {

    final public static PrintableInsets defaultInsets = new PrintableInsets(10, 10, 280, 400);

    public static synchronized void printRecipt(KPPrintable printable){
        PrinterJob job = PrinterJob.getPrinterJob();
        Paper paper = new Paper();
//        paper.setSize((defaultInsets.x * 2) + defaultInsets.width, defaultInsets.y + printable.getPrintableHeight());
        paper.setImageableArea(defaultInsets.x, defaultInsets.y, defaultInsets.width, printable.getPrintableHeight());

        PageFormat pageFormat = new PageFormat();
        pageFormat.setPaper(paper);

        job.setPrintable(printable, pageFormat);
        try {
            job.print();
        }
        catch (Exception e) {
            KPLogger.log(e.getLocalizedMessage());
        }
    }
}
