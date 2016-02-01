package info.nukoneko.kidspos.model;

/**
 * Created by TEJNEK on 2016/01/31.
 */
final public class ItemGenreModel implements BaseKPModel {
    public int id;
    public String name;
    public String store;

    public ItemGenreModel(int id, String name, String store){
        this.id = id;
        this.name = name;
        this.store = store;
    }

    public ItemGenreModel(String name, String store){
        this.name = name;
        this.store = store;
    }

    @Override
    public String debugOutput() {
        return "";
    }
}
