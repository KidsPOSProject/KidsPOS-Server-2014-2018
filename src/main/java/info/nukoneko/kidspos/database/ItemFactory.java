package info.nukoneko.kidspos.database;

import info.nukoneko.kidspos.model.ItemModel;

/**
 * Created by TEJNEK on 2016/01/31.
 */
final public class ItemFactory {
    private static DBItemImpl INSTANCE = new DBItemImpl();
    public static BaseKPDatabase<ItemModel> getInstance(){
        return INSTANCE;
    }
}
