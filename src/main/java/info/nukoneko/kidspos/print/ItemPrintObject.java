package info.nukoneko.kidspos.print;

import javafx.util.Pair;

import java.util.ArrayList;

public class ItemPrintObject {
    public final ArrayList<Pair<String, Integer>> items = new ArrayList<>();
    final String storeName;
    final String staffName;
    final int receiveMoney;
    public ItemPrintObject(String storeName, String staffName, int receiveMoney){
        this.storeName = storeName;
        this.staffName = staffName;
        this.receiveMoney = receiveMoney;
    }
}
