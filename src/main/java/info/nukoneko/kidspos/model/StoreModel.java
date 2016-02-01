package info.nukoneko.kidspos.model;

/**
 * Created by TEJNEK on 2016/01/31.
 */
final public class StoreModel implements BaseKPModel {
    public int id;
    public String name;

    public StoreModel(int id, String name){
        this.id = id;
        this.name = name;
    }

    public StoreModel(String name){
        this.name = name;
    }

    @Override
    public String debugOutput() {
        return "";
    }
}
