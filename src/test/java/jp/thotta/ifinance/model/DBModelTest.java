package jp.thotta.ifinance.model;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.sql.Connection;
import java.sql.SQLException;

public class DBModelTest extends TestCase {
  public void testDailyStockPrice() {
    try {
      DailyStockPrice.dropTable(Database.getConnection());
      DailyStockPrice.createTable(Database.getConnection());
    } catch(SQLException e) {
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
