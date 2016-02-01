package info.nukoneko.kidspos.database;

import info.nukoneko.kidspos.model.SaleModel;
import info.nukoneko.kidspos.model.StaffModel;

/**
 * Created by TEJNEK on 2016/01/31.
 */
final public class StaffFactory {
    private static DBStaffImpl INSTANCE = new DBStaffImpl();
    public static BaseKPDatabase<StaffModel> getInstance(){
        return INSTANCE;
    }
}
