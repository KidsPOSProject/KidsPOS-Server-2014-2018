package info.nukoneko.kidspos.model;

/**
 * Created by TEJNEK on 2016/01/31.
 */
final public class StaffModel implements BaseKPModel {
    public String barcode;
    public String name;

    public StaffModel(String barcode, String name){
        this.barcode = barcode;
        this.name = name;
    }

    @Override
    public String debugOutput() {
        return "";
    }
}
