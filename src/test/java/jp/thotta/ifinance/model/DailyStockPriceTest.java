package jp.thotta.ifinance.model;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

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
    dm1.marketCap=1000; dm1.stockNumber=100; dm1.tradingVolume=(long)200; dm1.previousTradingVolume=(long)300;
    dm2.marketCap=2000; dm2.stockNumber=200; dm2.tradingVolume=(long)200; dm2.previousTradingVolume=(long)100;
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
    } catch(Exception e) {
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

  public void testSelectStockIds() {
    List<Integer> ids = null;
    try {
      DailyStockPrice.updateMap(m, c);
      ids = DailyStockPrice.selectStockIds(c);
      System.out.println(ids);
    } catch(Exception e) {
      e.printStackTrace();
    }
    assertTrue(ids != null);
    assertTrue(ids.size() > 0);
  }

  public void testSelectLatests() {
    MyDate md1 = new MyDate(2015, 3, 3);
    MyDate md2 = new MyDate(2014, 12, 3);
    DailyStockPrice dsp1_1 = new DailyStockPrice(1001, md1);
    DailyStockPrice dsp1_2 = new DailyStockPrice(1001, md2);
    DailyStockPrice dsp2_2 = new DailyStockPrice(1002, md2);
    dsp1_1.marketCap = 100; dsp1_1.stockNumber = 10; dsp1_1.tradingVolume = (long)100; dsp1_1.previousTradingVolume = (long)1000;
    dsp1_2.marketCap = 1000; dsp1_2.stockNumber = -10; dsp1_2.tradingVolume = (long)200; dsp1_2.previousTradingVolume = (long)300;
    dsp2_2.marketCap = 400; dsp2_2.stockNumber = 30; dsp2_2.tradingVolume = (long)400; dsp2_2.previousTradingVolume = (long)500;
    Map<String, DailyStockPrice> dsp_map = new HashMap<String, DailyStockPrice>();
    dsp_map.put(dsp1_1.getKeyString(), dsp1_1);
    dsp_map.put(dsp1_2.getKeyString(), dsp1_2);
    dsp_map.put(dsp2_2.getKeyString(), dsp2_2);
    try {
      DailyStockPrice.updateMap(dsp_map, c);
      Map<String, DailyStockPrice> latests = DailyStockPrice.selectLatests(c);
      DailyStockPrice dsp99 = DailyStockPrice.selectLatestByStockId(1001, c);
      assertEquals(dsp99, dsp1_1);
      System.out.println(latests);
      for(String k : latests.keySet()) {
        System.out.println("key = " + k);
      }
      DailyStockPrice dsp_1 = latests.get("1001");
      DailyStockPrice dsp_2 = latests.get("1002");
      assertEquals(dsp_1, dsp1_1);
      assertEquals(dsp_2, null);
      assertEquals(dsp_1.tradingVolumeGrowthRatio(), 0.1);
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void testSelectPasts() {
    MyDate d0 = MyDate.getToday();
    MyDate d1 = MyDate.getPast(1);
    MyDate d2 = MyDate.getPast(2);
    MyDate d3 = MyDate.getPast(3);
    MyDate d4 = MyDate.getPast(4);
    MyDate d5 = MyDate.getPast(5);
    DailyStockPrice dsp1 = new DailyStockPrice(1004, d1);
    DailyStockPrice cdsp1 = new DailyStockPrice(1004, d2);
    DailyStockPrice dsp2 = new DailyStockPrice(1001, d2);
    DailyStockPrice dsp3 = new DailyStockPrice(1001, d3);
    DailyStockPrice dsp4 = new DailyStockPrice(1002, d4);
    DailyStockPrice dsp5 = new DailyStockPrice(1003, d5);
    dsp1.marketCap = 100; dsp1.stockNumber = 10; dsp1.tradingVolume = (long)900000; dsp1.previousTradingVolume = (long)1;
    cdsp1.marketCap = 300; cdsp1.stockNumber = 10; cdsp1.tradingVolume = (long)900000; cdsp1.previousTradingVolume = (long)1;
    dsp2.marketCap = 1000; dsp2.stockNumber = -10; dsp2.tradingVolume = (long)1; dsp2.previousTradingVolume = (long)9000000;
    dsp3.marketCap = 400; dsp3.stockNumber = 30; dsp3.tradingVolume = (long)10000; dsp3.previousTradingVolume = (long)10000;
    dsp4.marketCap = 500; dsp4.stockNumber = 30; dsp4.tradingVolume = (long)10; dsp4.previousTradingVolume = (long)100;
    dsp5.marketCap = 600; dsp5.stockNumber = 30; dsp5.tradingVolume = (long)100; dsp5.previousTradingVolume = (long)1000;
    Map<String, DailyStockPrice> dsp_map = new HashMap<String, DailyStockPrice>();
    dsp_map.put(dsp1.getKeyString(), dsp1);
    dsp_map.put(cdsp1.getKeyString(), cdsp1);
    dsp_map.put(dsp2.getKeyString(), dsp2);
    dsp_map.put(dsp3.getKeyString(), dsp3);
    dsp_map.put(dsp4.getKeyString(), dsp4);
    dsp_map.put(dsp5.getKeyString(), dsp5);
    try {
      DailyStockPrice.updateMap(dsp_map, c);
      Map<String, DailyStockPrice> pasts = DailyStockPrice.selectPasts(c, 3);
      System.out.println(pasts);
      for(String k : pasts.keySet()) {
        System.out.println("key = " + k);
      }
      DailyStockPrice dsp_1 = pasts.get("1001");
      DailyStockPrice dsp_2 = pasts.get("1004");
      assertEquals(dsp_1, dsp3);
      assertEquals(dsp_2, null);
      Map<Integer, Double> dropRank = DailyStockPrice.selectDropStockRanking(3, c);
      System.out.println(dropRank);
      assertEquals(dropRank.get(1001), null);
      assertEquals(dropRank.get(1004), (1.0/3) - 1.0, 0.01);
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
