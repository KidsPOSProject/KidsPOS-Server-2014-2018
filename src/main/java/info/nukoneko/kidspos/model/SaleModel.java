package info.nukoneko.kidspos.model;

/**
 * Created by TEJNEK on 2016/01/31.
 */
/*
                "(id INTEGER  PRIMARY KEY AUTOINCREMENT, " +
                "barcode TEXT UNIQUE, " +
                "created_at TEXT, " +
                "points INTEGER, " +
                "price INTEGER, " +
                "items TEXT, " +
                "store INTEGER, " +
                "staff INTEGER)";
 */
final public class SaleModel implements BaseKPModel {
    public int id;
    public String barcode;
    public String createdAt;
    public int points;
    public int price;
    public String items;
    public int store;
    public int staff;

    public SaleModel(int id,
                     String barcode,
                     String createdAt,
                     int points,
                     int price,
                     String items,
                     int store,
                     int staff){
        this.id = id;
        this.barcode = barcode;
        this.createdAt = createdAt;
        this.points = points;
        this.price = price;
        this.items = items;
        this.store = store;
        this.staff = staff;
    }

    public SaleModel(
            String barcode,
            String createdAt,
            int points,
            int price,
            String items,
            int store,
            int staff){
        this.barcode = barcode;
        this.createdAt = createdAt;
        this.points = points;
        this.price = price;
        this.items = items;
        this.store = store;
        this.staff = staff;
    }

    @Override
    public String debugOutput() {
        return "";
    }
}
