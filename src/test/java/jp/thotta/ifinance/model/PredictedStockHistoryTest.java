package jp.thotta.ifinance.model;

import junit.framework.TestCase;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import jp.thotta.ifinance.common.MyDate;

public class PredictedStockHistoryTest extends TestCase {
  Connection c;
  Map<String, PredictedStockHistory> m = new HashMap<String, PredictedStockHistory>();
  PredictedStockHistory h1_45 = new PredictedStockHistory(1001, MyDate.getPast(45));
  PredictedStockHistory h1_30 = new PredictedStockHistory(1001, MyDate.getPast(30));
  PredictedStockHistory h2_45 = new PredictedStockHistory(1002, MyDate.getPast(45));

  protected void setUp() {
    h1_45.predictedMarketCap = (long)100;
    h1_45.isStableStock = false;
    h1_30.predictedMarketCap = (long)300;
    h1_30.isStableStock = true;
    h2_45.predictedMarketCap = (long)400;
    h2_45.isStableStock = true;
    m.put(h1_45.getKeyString(), h1_45);
    m.put(h1_30.getKeyString(), h1_30);
    m.put(h2_45.getKeyString(), h2_45);
    try {
      Database.setDbUrl("jdbc:sqlite:test.db");
      c = Database.getConnection();
      PredictedStockHistory.dropTable(c);
      PredictedStockHistory.createTable(c);
    } catch(SQLException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public void testInstanceMethods() {
    MyDate d = new MyDate(2014, 3, 5);
    PredictedStockHistory psh = new PredictedStockHistory(9999, d);
    assertFalse(psh.hasEnough());
    psh.predictedMarketCap = (long)100;
    psh.isStableStock = false;
    assertTrue(psh.hasEnough());
    assertEquals(psh.getKeyString(), "9999,2014-03-05");
    System.out.println(psh);
    System.out.println(psh.getFindSql());
  }

  public void testInsertUpdate() {
    MyDate d = new MyDate(2014, 3, 5);
    PredictedStockHistory psh = new PredictedStockHistory(9999, d);
    psh.predictedMarketCap = (long)100;
    psh.isStableStock = false;
    PredictedStockHistory psh2 = new PredictedStockHistory(9999, d);
    try {
      Statement st = c.createStatement();
      psh.insert(st);
      psh2.readDb(st);
      assertEquals(psh, psh2);
      psh2.isStableStock = true;
      psh2.update(st);
      psh.readDb(st);
      assertEquals(psh, psh2);
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void testUpdateMapAndSelect() {
    try {
      PredictedStockHistory.updateMap(m, c);
      Map<String, PredictedStockHistory> dbm = PredictedStockHistory.selectAll(c);
      for(String k : m.keySet()) {
        PredictedStockHistory m_h = m.get(k);
        PredictedStockHistory db_h = dbm.get(k);
        assertEquals(m_h, db_h);
      }
      Map<String, PredictedStockHistory> pasts = PredictedStockHistory.selectPast(c, 45);
      PredictedStockHistory hdb1 = pasts.get(h1_45.getKeyString());
      PredictedStockHistory hdb2 = pasts.get(h2_45.getKeyString());
      assertEquals(h1_45, hdb1);
      assertEquals(h2_45, hdb2);
      PredictedStockHistory h1_latest =
        PredictedStockHistory.selectLatestByStockId(1001, c);
      PredictedStockHistory h2_latest =
        PredictedStockHistory.selectLatestByStockId(1002, c);
      System.out.println(h1_latest);
      System.out.println(h2_latest);
      assertEquals(h1_30, h1_latest);
      assertEquals(null, h2_latest);
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
