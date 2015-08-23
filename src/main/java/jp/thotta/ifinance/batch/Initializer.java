package jp.thotta.ifinance.batch;

import java.sql.Connection;
import java.sql.SQLException;

import jp.thotta.ifinance.model.CorporatePerformance;
import jp.thotta.ifinance.model.DailyStockPrice;
import jp.thotta.ifinance.model.PerformanceForecast;
import jp.thotta.ifinance.model.CompanyProfile;
import jp.thotta.ifinance.model.PredictedStockHistory;
import jp.thotta.ifinance.model.CompanyNews;
import jp.thotta.ifinance.model.Database;

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
    CompanyNews.createTable(conn);
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
      if(args.length == 0) {
        init.createTables();
      } else if(args[0].equals("migrate")) {
        init.migrateTables();
      } else {
        System.out.println("Wrong argument: " + args[0]);
      }
    } catch(Exception e) {
      e.printStackTrace();
    } finally {
      try {
        Database.closeConnection();
      } catch(SQLException e) {
        e.printStackTrace();
      }
    }
  }
}
