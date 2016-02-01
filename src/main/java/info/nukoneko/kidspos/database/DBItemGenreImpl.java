package info.nukoneko.kidspos.database;

import info.nukoneko.kidspos.model.ItemGenreModel;
import info.nukoneko.kidspos.util.config.TableName;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by TEJNEK on 2016/01/31.
 */
final class DBItemGenreImpl extends BaseKPDatabase<ItemGenreModel> {
    private static final TableName MyTable = TableName.ITEM_GENRE;

    @Override
    String getDBPath() {
        return MyTable.getDbPath();
    }

    @Override
    public ArrayList<ItemGenreModel> findAll() {
        ArrayList<ItemGenreModel> list = new ArrayList<>();
        try {
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM " + MyTable.getName());
            while( rs.next() ) {
                ItemGenreModel model = new ItemGenreModel(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("store"));
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
    String QueryInsert(ItemGenreModel item) {
        return String
                .format("INSERT INTO %s(name, store) VALUES('%s','%s')",
                        MyTable.getName(), item.name, item.store);
    }

    @Override
    String QueryCreate() {
        return "CREATE TABLE IF NOT EXISTS " + MyTable.getName() +
                "(id INTEGER  PRIMARY KEY AUTOINCREMENT, name TEXT, store TEXT)";
    }
}
