package info.nukoneko.kidspos.print;

import javafx.util.Pair;

import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by b140096 on 2016/03/06.
 */
public class ItemPrintable implements KPPrintable {
    final ItemPrintObject printObject;
    public ItemPrintable(ItemPrintObject printObject){
        this.printObject = printObject;
    }
    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        // 印刷の最大枚数
        if (pageIndex >= 1) return Printable.NO_SUCH_PAGE;
//        Font font = new Font("MS UI Gothic", Font.BOLD, 10);
//        Font fontBig = new Font("MS UI Gothic", Font.BOLD, 13);
        Graphics2D g2 = (Graphics2D) graphics;

        g2.drawString("キッズビジネスタウンいちかわ", 20, 20);

        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy'年'MM'月'dd'日' HH時mm分ss秒");
        g2.drawString(sdf1.format(new Date()), 10, 60);

        g2.fillRect(0, 70, 260, 5);

        int baseHeight = 90;

        int sum = 0;
        baseHeight += 20;
        for (int i = 0 ; i < printObject.items.size() ; i++){
            Pair<String, Integer> item = printObject.items.get(i);
            g2.drawString(item.getKey(), 5, baseHeight);
            g2.drawString(String.valueOf(item.getValue()) + "リバー", 150, baseHeight);
            sum += item.getValue();
            if (printObject.items.size() > i - 1) {
                baseHeight += 20;
            }
        }

        g2.fillRect(0, baseHeight, 260, 5);

        baseHeight += 20;

        g2.drawString("ごうけい", 0, baseHeight);
        g2.drawString(String.valueOf(sum) + "リバー", 140, baseHeight);
        baseHeight += 20;

        g2.drawString("おあずかり", 0, baseHeight);
        g2.drawString(String.valueOf(printObject.receiveMoney) + "リバー", 140, baseHeight);
        baseHeight += 20;

        g2.drawString("おつり", 0, baseHeight);
        g2.drawString(String.valueOf((printObject.receiveMoney - sum)) + "リバー", 140, baseHeight);
        baseHeight += 20;
        baseHeight += 20;

        if (!printObject.storeName.isEmpty()) {
            g2.drawString("おみせ", 0, baseHeight);
            g2.drawString(printObject.storeName, 40, baseHeight);
            baseHeight += 20;
        }

        if (!printObject.staffName.isEmpty()){
            g2.drawString("れじのたんとう", 0, baseHeight);
            g2.drawString(printObject.staffName, 100, baseHeight);
            baseHeight += 20;
        }

        baseHeight += 20;
        g2.drawString("印字保護のためこちらの面を", 10, baseHeight);
        baseHeight += 20;
        g2.drawString("内側に折って保管してください", 10, baseHeight);
        baseHeight += 20;

        g2.fillRect(0, baseHeight, 260, 5);

        return Printable.PAGE_EXISTS;
    }

    @Override
    public double getPrintableHeight() {
        return 0;
    }
}
