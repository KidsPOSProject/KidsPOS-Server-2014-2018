package info.nukoneko.kidspos.model;

/**
 * Created by TEJNEK on 2016/01/31.
 */
final public class ItemModel implements BaseKPModel {
    public int id;
    public String barcode;
    public String name;
    public int price;
    public int shop;
    public String genre;

    public ItemModel(int id, String barcode, String name, int price, int shop, String genre){
        this.id = id;
        this.barcode = barcode;
        this.name = name;
        this.price = price;
        this.shop = shop;
        this.genre = genre;
    }

    public ItemModel(String barcode, String name, int price, int shop, String genre){
        this.barcode = barcode;
        this.name = name;
        this.price = price;
        this.shop = shop;
        this.genre = genre;
    }

    @Override
    public String debugOutput() {
        return "";
    }
}
