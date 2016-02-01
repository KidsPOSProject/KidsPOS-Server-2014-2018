package info.nukoneko.kidspos.database;

import info.nukoneko.kidspos.model.ItemModel;
import info.nukoneko.kidspos.model.SaleModel;

/**
 * Created by TEJNEK on 2016/01/31.
 */
final public class SaleFactory {
    private static DBSaleImpl INSTANCE = new DBSaleImpl();
    public static BaseKPDatabase<SaleModel> getInstance(){
        return INSTANCE;
    }
}
