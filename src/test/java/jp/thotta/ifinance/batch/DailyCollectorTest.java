package jp.thotta.ifinance.batch;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import jp.thotta.ifinance.model.DailyStockPrice;
import jp.thotta.ifinance.model.CorporatePerformance;
import jp.thotta.ifinance.model.Database;

public class DailyCollectorTest extends TestCase {
  Connection c;
  Statement st;

  protected void setUp() {
    try {
      c = Database.getConnection();
      st = c.createStatement();
      DailyStockPrice.dropTable(c);
      DailyStockPrice.createTable(c);
      CorporatePerformance.dropTable(c);
      CorporatePerformance.createTable(c);
    } catch(SQLException e) {
      e.printStackTrace();
    }
  }

  /*
  public void testCollector() {
    DailyCollector.collectDailyStockPrice();
    DailyCollector.collectCorporatePerformance();
    try {
      Map<String, DailyStockPrice> m = DailyStockPrice.selectAll(c);
      Map<String, CorporatePerformance> mcp = CorporatePerformance.selectAll(c);
      assertTrue(m.size() > 100);
      assertTrue(mcp.size() > 100);
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
  */

  protected void tearDown() {
    try {
      Database.closeConnection();
    } catch(SQLException e) {
      e.printStackTrace();
    }
  }
}
