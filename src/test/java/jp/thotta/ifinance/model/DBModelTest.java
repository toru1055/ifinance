package jp.thotta.ifinance.model;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.text.ParseException;

import jp.thotta.ifinance.collector.yj_finance.*;

public class DBModelTest extends TestCase {
  Map<String, DailyStockPrice> stockTable;
  Map<String, CorporatePerformance> performances;
  Connection c;

  protected void setUp() {
    stockTable = new HashMap<String, DailyStockPrice>();
    performances = new HashMap<String, CorporatePerformance>();
    try {
      c = Database.getConnection();
      DailyStockPrice.dropTable(c);
      DailyStockPrice.createTable(c);
      CorporatePerformance.dropTable(c);
      CorporatePerformance.createTable(c);
    } catch(SQLException e) {
      e.printStackTrace();
    }
  }

  public void testDailyStockPriceDirectDb() {
    StockPriceCollectorImpl spc = new StockPriceCollectorImpl();
    spc.setStartPage(73);
    try {
      spc.appendDb(c);
    } catch(Exception e) {
      e.printStackTrace();
    }
    try {
      Map<String, DailyStockPrice> m = DailyStockPrice.selectAll(c);
      for(String k : m.keySet()) {
        System.out.println(m.get(k));
      }
      assertTrue(m.size() > 0);
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void testCorporatePerformanceDirectDb() {
    SalesAmountCollectorImpl sac = new SalesAmountCollectorImpl();
    sac.setStartPage(71);
    OperatingProfitCollectorImpl oppc = new OperatingProfitCollectorImpl();
    oppc.setStartPage(71);
    OrdinaryProfitCollectorImpl orpc = new OrdinaryProfitCollectorImpl();
    orpc.setStartPage(71);
    try {
      sac.appendDb(c);
      oppc.appendDb(c);
      orpc.appendDb(c);
      Map<String, CorporatePerformance> m = CorporatePerformance.selectAll(c);
      for(String k : m.keySet()) {
        System.out.println(m.get(k));
      }
      assertTrue(m.size() > 0);
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  protected void tearDown() {
    try {
      Database.closeConnection();
    } catch(SQLException e) {
      e.printStackTrace();
    }
  }
}
