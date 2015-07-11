package jp.thotta.ifinance.batch;

import java.sql.Connection;
import java.sql.SQLException;

import jp.thotta.ifinance.model.CorporatePerformance;
import jp.thotta.ifinance.model.DailyStockPrice;
import jp.thotta.ifinance.model.PerformanceForecast;
import jp.thotta.ifinance.model.CompanyProfile;
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
  }

  public void dropTables() throws SQLException {
    CorporatePerformance.dropTable(conn);
    DailyStockPrice.dropTable(conn);
    PerformanceForecast.dropTable(conn);
    CompanyProfile.dropTable(conn);
  }

  public static void main(String[] args) {
    try {
      Connection c = Database.getConnection();
      Initializer init = new Initializer(c);
      init.createTables();
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
