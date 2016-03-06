package info.nukoneko.kidspos.print;

import javafx.util.Pair;

import java.util.ArrayList;

/**
 * Created by b140096 on 2016/03/06.
 */
public class ItemPrintObject {
    public ArrayList<Pair<String, Integer>> items = new ArrayList<>();
    final String storeName;
    final String staffName;
    final int receiveMoney;
    public ItemPrintObject(String storeName, String staffName, int receiveMoney){
        this.storeName = storeName;
        this.staffName = staffName;
        this.receiveMoney = receiveMoney;
    }
}
