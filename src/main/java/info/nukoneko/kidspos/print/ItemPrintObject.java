package info.nukoneko.kidspos.print;

import javafx.util.Pair;

import java.util.List;

public class ItemPrintObject {
    private final List<Pair<String, Integer>> items;
    private final String storeName;
    private final int storeId;
    private final String staffName;
    private final int receiveMoney;
    public ItemPrintObject(List<Pair<String, Integer>> items, String storeName, int storeId, String staffName, int receiveMoney){
        this.items = items;
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

    public List<Pair<String, Integer>> getItems() {
        return items;
    }

    public int getReceiveMoney() {
        return receiveMoney;
    }
}
