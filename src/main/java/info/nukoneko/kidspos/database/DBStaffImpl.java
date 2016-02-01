package info.nukoneko.kidspos.database;

import info.nukoneko.kidspos.model.SaleModel;
import info.nukoneko.kidspos.model.StaffModel;
import info.nukoneko.kidspos.util.config.TableName;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by TEJNEK on 2016/01/31.
 */
final class DBStaffImpl extends BaseKPDatabase<StaffModel> {
    private static final TableName MyTable = TableName.STAFF;

    @Override
    String getDBPath() {
        return MyTable.getDbPath();
    }

    @Override
    public ArrayList<StaffModel> findAll() {
        ArrayList<StaffModel> list = new ArrayList<>();
        try {
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM " + MyTable.getName());
            while( rs.next() ) {
                StaffModel model = new StaffModel(
                        rs.getString("barcode"),
                        rs.getString("name"));
                list.add(model);
            }
            rs.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    @Override
    String QueryInsert(StaffModel item) {
        return String
                .format("INSERT INTO %s(barcode,name) VALUES('%s','%s')",
                        MyTable.getName(),
                        item.barcode,
                        item.name);
    }

    @Override
    String QueryCreate() {
        return "CREATE TABLE  IF NOT EXISTS " + MyTable.getName() +
                "(barcode INTEGER PRIMARY KEY, name TEXT)";
    }
}
