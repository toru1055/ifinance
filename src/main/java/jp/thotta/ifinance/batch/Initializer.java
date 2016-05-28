package jp.thotta.ifinance.batch;

import jp.thotta.ifinance.model.*;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 実行環境の初期化.
 * 各種テーブル作成など
 */
public class Initializer {
    Connection conn;

    public Initializer(Connection c) {
        this.conn = c;
    }

    public void createTables() throws SQLException {
        CorporatePerformance.createTable(conn);
        DailyStockPrice.createTable(conn);
        PerformanceForecast.createTable(conn);
        CompanyProfile.createTable(conn);
        PredictedStockHistory.createTable(conn);
        CompanyNews.createTable(conn);
    }

    public void migrateTables() throws SQLException {
        DailyStockPrice.addTradingVolume(conn);
    }

    public void dropTables() throws SQLException {
        CorporatePerformance.dropTable(conn);
        DailyStockPrice.dropTable(conn);
        PerformanceForecast.dropTable(conn);
        CompanyProfile.dropTable(conn);
        PredictedStockHistory.dropTable(conn);
        CompanyNews.dropTable(conn);
    }

    public static void main(String[] args) {
        try {
            Connection c = Database.getConnection();
            Initializer init = new Initializer(c);
            if (args.length == 0) {
                init.createTables();
            } else if (args[0].equals("migrate")) {
                init.migrateTables();
            } else {
                System.out.println("Wrong argument: " + args[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                Database.closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
