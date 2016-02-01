package info.nukoneko.kidspos.database;

import info.nukoneko.kidspos.model.ItemGenreModel;

/**
 * Created by TEJNEK on 2016/01/31.
 */
final public class ItemGenreFactory {
    private static DBItemGenreImpl INSTANCE = new DBItemGenreImpl();
    public static BaseKPDatabase<ItemGenreModel> getInstance(){
        return INSTANCE;
    }
}
