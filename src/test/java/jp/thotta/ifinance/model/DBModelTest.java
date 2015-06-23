package jp.thotta.ifinance.model;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

import jp.thotta.ifinance.collector.yj_finance.StockPriceCollectorImpl;

public class DBModelTest extends TestCase {
  Map<String, DailyStockPrice> stockTable;
  Connection c;

  protected void setUp() {
    stockTable = new HashMap<String, DailyStockPrice>();
    try {
      c = Database.getConnection();
      DailyStockPrice.dropTable(c);
      DailyStockPrice.createTable(c);

    } catch(SQLException e) {
      e.printStackTrace();
    }
  }

  public void testDailyStockPrice() {
    StockPriceCollectorImpl spc = new StockPriceCollectorImpl();
    spc.setStartPage(73);
    try {
      spc.append(stockTable);
    } catch(IOException e) {
      e.printStackTrace();
    }
    assertTrue(stockTable.size() > 0);
    try {
      DailyStockPrice.insertMap(stockTable, c);
    } catch(SQLException e) {
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
