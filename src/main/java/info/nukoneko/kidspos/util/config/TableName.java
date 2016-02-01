package info.nukoneko.kidspos.util.config;

/**
 * Created by TEJNEK on 2016/01/31.
 */
// TODO: Need Refactoring
public enum TableName {
    ITEM("item", "item.db"),
    ITEM_GENRE("item_genre", "item.db"),
    SALE("sale", "item.db"),
    STORE("store", "item.db"),
    STAFF("staff", "staff.db");

    private final String name;
    private final String dbPath;
    TableName(final String name, final String dbPath){
        this.name = name;
        this.dbPath = dbPath;
    }

    public String getName(){
        return this.name;
    }

    public String getDbPath(){
        return this.dbPath;
    }
}
