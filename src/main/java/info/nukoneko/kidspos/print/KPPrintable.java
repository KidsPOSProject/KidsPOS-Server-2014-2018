package info.nukoneko.kidspos.print;

import java.awt.print.Printable;

public interface KPPrintable extends Printable {
    double getPrintableHeight();
    PrintManager.PRINTER_TYPE gerUserPrinterType();
}
