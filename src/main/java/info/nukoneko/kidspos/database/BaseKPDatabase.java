package info.nukoneko.kidspos.database;

import info.nukoneko.kidspos.model.BaseKPModel;
import info.nukoneko.kidspos.model.SaleModel;
import info.nukoneko.kidspos.util.KPLogger;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by TEJNEK on 2016/01/31.
 */
public abstract class BaseKPDatabase<T extends BaseKPModel> {
    abstract String getDBPath();

    abstract String QueryCreate();
    abstract String QueryInsert(T item);
    abstract public ArrayList<T> findAll();

    BaseKPDatabase(){
        try {
            System.out.println("CREATE-TABLE" + Execute(QueryCreate()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    final public void insert(T item) {
        try {
            System.out.println(Execute(QueryInsert(item)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    final Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection("jdbc:sqlite:"+ getDBPath());
    }

    final boolean Execute(String query) throws SQLException {
        KPLogger.log(query);
        Connection connection = getConnection();
        Statement stmt = connection.createStatement();
        boolean ret = stmt.execute(query);
        stmt.close();
        connection.close();
        return ret;
    }
}
