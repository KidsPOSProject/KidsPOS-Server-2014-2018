package info.nukoneko.kidspos.database;

import info.nukoneko.kidspos.model.StaffModel;
import info.nukoneko.kidspos.model.StoreModel;
import info.nukoneko.kidspos.util.config.TableName;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by TEJNEK on 2016/01/31.
 */
final class DBStoreImpl extends BaseKPDatabase<StoreModel> {
    private static final TableName MyTable = TableName.STORE;

    @Override
    String getDBPath() {
        return MyTable.getDbPath();
    }

    @Override
    public ArrayList<StoreModel> findAll() {
        ArrayList<StoreModel> list = new ArrayList<>();
        try {
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM " + MyTable.getName());
            while( rs.next() ) {
                StoreModel model = new StoreModel(
                        rs.getInt("id"),
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
    String QueryInsert(StoreModel item) {
        return String
                .format("INSERT INTO %s(name) VALUES('%s')",
                        MyTable.getName(),
                        item.name);
    }

    @Override
    String QueryCreate() {
        return "CREATE TABLE  IF NOT EXISTS " + MyTable.getName() +
                "(id INTEGER  PRIMARY KEY AUTOINCREMENT, name TEXT)";
    }
}
