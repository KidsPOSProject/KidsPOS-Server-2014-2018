package info.nukoneko.kidspos.database;

import info.nukoneko.kidspos.model.ItemModel;
import info.nukoneko.kidspos.util.config.TableName;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by TEJNEK on 2016/01/31.
 */
final class DBItemImpl extends BaseKPDatabase<ItemModel> {
    private static final TableName MyTable = TableName.ITEM;

    @Override
    String getDBPath() {
        return MyTable.getDbPath();
    }

    @Override
    public ArrayList<ItemModel> findAll() {
        ArrayList<ItemModel> list = new ArrayList<>();
        try {
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM " + MyTable.getName());
            while( rs.next() ) {
                ItemModel model = new ItemModel(
                        rs.getInt("id"),
                        rs.getString("barcode"),
                        rs.getString("name"),
                        rs.getInt("price"),
                        rs.getInt("shop"),
                        rs.getString("genre"));
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
    String QueryInsert(ItemModel item) {
        return String
                .format("INSERT INTO %s(barcode, name, price, shop, genre) " +
                                "VALUES('%s','%s', %d, %d, '%s')",
                        MyTable.getName(), item.barcode, item.name, item.price, item.shop, item.genre);
    }

    @Override
    String QueryCreate() {
        return "CREATE TABLE  IF NOT EXISTS " + MyTable.getName() +
                "(id INTEGER  PRIMARY KEY AUTOINCREMENT, barcode INTEGER UNIQUE, name TEXT, price INTEGER, shop INT, genre TEXT)";
    }
}
