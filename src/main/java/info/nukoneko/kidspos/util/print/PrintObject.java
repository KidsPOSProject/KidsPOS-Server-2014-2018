package info.nukoneko.kidspos.util.print;

import com.sun.istack.internal.Nullable;
import javafx.util.Pair;

import java.util.List;

public final class PrintObject {
    private final List<Pair<String, Integer>> items;
    @Nullable
    private final String storeName;
    @Nullable
    private final String staffName;
    private final int total;
    private final int deposit;
    private final int change;
    private final String transactionId;

    public PrintObject(List<Pair<String, Integer>> items,
                       @Nullable String storeName, @Nullable String staffName,
                       int total, int deposit, int change, String transactionId) {
        this.items = items;
        this.storeName = storeName;
        this.staffName = staffName;
        this.total = total;
        this.deposit = deposit;
        this.change = change;
        this.transactionId = transactionId;
    }

    public List<Pair<String, Integer>> getItems() {
        return items;
    }

    @Nullable
    public String getStoreName() {
        return storeName;
    }

    @Nullable
    public String getStaffName() {
        return staffName;
    }

    public int getTotal() {
        return total;
    }

    public int getDeposit() {
        return deposit;
    }

    public int getChange() {
        return change;
    }

    public String getTransactionId() {
        return transactionId;
    }
}