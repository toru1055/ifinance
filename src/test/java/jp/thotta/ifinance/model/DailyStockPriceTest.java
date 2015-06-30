package jp.thotta.ifinance.model;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.HashMap;

import jp.thotta.ifinance.common.MyDate;

public class DailyStockPriceTest extends TestCase {
  Connection c;
  Statement st;
  DailyStockPrice dsp, dsp2, dm1, dm2;
  Map<String, DailyStockPrice> m;

  protected void setUp() {
    MyDate myDate = new MyDate(2015, 3, 3);
    dsp = new DailyStockPrice(9999, myDate);
    dsp.marketCap = 1000;
    dsp2 = new DailyStockPrice(9999, myDate);
    dsp2.stockNumber = 100;
    m = new HashMap<String, DailyStockPrice>();
    dm1 = new DailyStockPrice(1001, myDate);
    dm2 = new DailyStockPrice(1002, myDate);
    dm1.marketCap=1000; dm1.stockNumber=100;
    dm2.marketCap=2000; dm2.stockNumber=200;
    m.put(dm1.getKeyString(), dm1);
    m.put(dm2.getKeyString(), dm2);
    try {
      Database.setDbUrl("jdbc:sqlite:test.db");
      c = Database.getConnection();
      st = c.createStatement();
      DailyStockPrice.dropTable(c);
      DailyStockPrice.createTable(c);
    } catch(SQLException e) {
      e.printStackTrace();
    }
  }

  public void testGetKeyString() {
    assertEquals(dsp.getKeyString(), "9999,2015-03-03");
  }

  public void testExists() {
    try {
      assertFalse(dsp.exists(st));
      dsp.insert(st);
      assertTrue(dsp.exists(st));
    } catch(SQLException e) {
      e.printStackTrace();
    }
  }

  public void testInsertUpdate() {
    try {
      dsp.insert(st);
      dsp2.readDb(st);
      assertEquals(dsp2.marketCap, dsp.marketCap);
      dsp2.update(st);
      dsp.readDb(st);
      assertEquals(dsp, dsp2);
    } catch(SQLException e) {
      e.printStackTrace();
    }
  }

  public void testUpdateMap() {
    try {
      DailyStockPrice.updateMap(m, c);
      Map<String, DailyStockPrice> fromDbMap = DailyStockPrice.selectAll(c);
      for(String k : m.keySet()) {
        DailyStockPrice m_dsp = m.get(k);
        DailyStockPrice db_dsp = fromDbMap.get(k);
        assertEquals(m_dsp, db_dsp);
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void testSelectLatests() {
    MyDate md1 = new MyDate(2015, 3, 3);
    MyDate md2 = new MyDate(2014, 12, 3);
    DailyStockPrice dsp1_1 = new DailyStockPrice(1001, md1);
    DailyStockPrice dsp1_2 = new DailyStockPrice(1001, md2);
    DailyStockPrice dsp2_2 = new DailyStockPrice(1002, md2);
    dsp1_1.marketCap = 100; dsp1_1.stockNumber = 10;
    dsp1_2.marketCap = 1000; dsp1_2.stockNumber = -10;
    dsp2_2.marketCap = 400; dsp2_2.stockNumber = 30;
    Map<String, DailyStockPrice> dsp_map = new HashMap<String, DailyStockPrice>();
    dsp_map.put(dsp1_1.getKeyString(), dsp1_1);
    dsp_map.put(dsp1_2.getKeyString(), dsp1_2);
    dsp_map.put(dsp2_2.getKeyString(), dsp2_2);
    try {
      DailyStockPrice.updateMap(dsp_map, c);
      Map<String, DailyStockPrice> latests = DailyStockPrice.selectLatests(c);
      System.out.println(latests);
      for(String k : latests.keySet()) {
        System.out.println("key = " + k);
      }
      DailyStockPrice dsp_1 = latests.get("1001");
      DailyStockPrice dsp_2 = latests.get("1002");
      dsp1_1.date = null;
      dsp2_2.date = null;
      assertEquals(dsp_1, dsp1_1);
      assertEquals(dsp_2, dsp2_2);
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
