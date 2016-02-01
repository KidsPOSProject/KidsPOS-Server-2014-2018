package info.nukoneko.kidspos.database;

import info.nukoneko.kidspos.model.StaffModel;
import info.nukoneko.kidspos.model.StoreModel;

/**
 * Created by TEJNEK on 2016/01/31.
 */
final public class StoreFactory {
    private static DBStoreImpl INSTANCE = new DBStoreImpl();
    public static BaseKPDatabase<StoreModel> getInstance(){
        return INSTANCE;
    }
}


