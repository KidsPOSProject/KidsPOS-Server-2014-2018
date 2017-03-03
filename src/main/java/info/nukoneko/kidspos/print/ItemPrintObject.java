package info.nukoneko.kidspos.print;

import javafx.util.Pair;

import java.util.ArrayList;

public class ItemPrintObject {
    public final ArrayList<Pair<String, Integer>> items = new ArrayList<>();
    private final String storeName;
    private final int storeId;
    private final String staffName;
    private final int receiveMoney;
    public ItemPrintObject(String storeName, int storeId, String staffName, int receiveMoney){
        this.storeName = storeName;
        this.storeId = storeId;
        this.staffName = staffName;
        this.receiveMoney = receiveMoney;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getStaffName() {
        return staffName;
    }

    public int getStoreId() {
        return storeId;
    }

    public ArrayList<Pair<String, Integer>> getItems() {
        return items;
    }

    public int getReceiveMoney() {
        return receiveMoney;
    }
}
