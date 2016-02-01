package info.nukoneko.kidspos.database;

import info.nukoneko.kidspos.model.SaleModel;
import info.nukoneko.kidspos.util.config.TableName;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by TEJNEK on 2016/01/31.
 */
final class DBSaleImpl extends BaseKPDatabase<SaleModel> {
    private static final TableName MyTable = TableName.SALE;

    @Override
    String getDBPath() {
        return MyTable.getDbPath();
    }

    @Override
    public ArrayList<SaleModel> findAll() {
        ArrayList<SaleModel> list = new ArrayList<>();
        try {
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM " + MyTable.getName());
            while( rs.next() ) {
                SaleModel model = new SaleModel(
                        rs.getInt("id"),
                        rs.getString("barcode"),
                        rs.getString("created_at"),
                        rs.getInt("points"),
                        rs.getInt("price"),
                        rs.getString("items"),
                        rs.getInt("store"),
                        rs.getInt("staff"));
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
    String QueryInsert(SaleModel item) {
        return String
                .format("INSERT INTO %s(barcode, created_at, points, price, items, store, staff) " +
                                "VALUES('%s','%s', %d, %d, '%s' %d, %d)",
                        MyTable.getName(),
                        item.barcode,
                        item.createdAt,
                        item.points,
                        item.price,
                        item.items,
                        item.store,
                        item.staff);
    }

    @Override
    String QueryCreate() {
        return "CREATE TABLE  IF NOT EXISTS " + MyTable.getName() +
                "(id INTEGER  PRIMARY KEY AUTOINCREMENT, " +
                "barcode TEXT UNIQUE, " +
                "created_at TEXT, " +
                "points INTEGER, " +
                "price INTEGER, " +
                "items TEXT, " +
                "store INTEGER, " +
                "staff INTEGER)";
    }
}
