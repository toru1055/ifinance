package jp.thotta.ifinance.model;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.HashMap;

public class CorporatePerformanceTest extends TestCase {
  Connection c;
  Statement st;
  CorporatePerformance cp, cp2, cm1, cm2;
  Map<String, CorporatePerformance> m;

  protected void setUp() {
    cp = new CorporatePerformance(9999, 2015, 3);
    cp.salesAmount = 1000;
    cp2 = new CorporatePerformance(9999, 2015, 3);
    cp2.netProfit = -100;
    cm1 = new CorporatePerformance(1001, 2015, 3);
    cm2 = new CorporatePerformance(1002, 2014, 12);
    cm1.salesAmount = 1000; cm1.netProfit = 100;
    cm2.salesAmount = 2000; cm2.netProfit = -10;
    m = new HashMap<String, CorporatePerformance>();
    m.put(cm1.getKeyString(), cm1);
    m.put(cm2.getKeyString(), cm2);
    try {
      Database.setDbUrl("jdbc:sqlite:test.db");
      c = Database.getConnection();
      st = c.createStatement();
      CorporatePerformance.dropTable(c);
      CorporatePerformance.createTable(c);
    } catch(SQLException e) {
      e.printStackTrace();
    }
  }

  public void testGetKeyString() {
    assertEquals(cp.getKeyString(), "9999,2015/03");
  }

  public void testExists() {
    try {
      assertFalse(cp.exists(st));
      cp.insert(st);
      assertTrue(cp.exists(st));
    } catch(SQLException e) {
      e.printStackTrace();
    }
  }

  public void testInsertUpdate() {
    try {
      cp.insert(st);
      cp2.readDb(st);
      assertEquals(cp2.salesAmount, cp.salesAmount);
      cp2.update(st);
      cp.readDb(st);
      assertEquals(cp.netProfit, cp2.netProfit);
    } catch(SQLException e) {
      e.printStackTrace();
    }
  }

  public void testUpdateMap() {
    try {
      CorporatePerformance.updateMap(m, c);
      Map<String, CorporatePerformance> fromDbMap = CorporatePerformance.selectAll(c);
      for(String k : m.keySet()) {
        CorporatePerformance m_cp = m.get(k);
        CorporatePerformance db_cp = fromDbMap.get(k);
        assertEquals(m_cp, db_cp);
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void testSelectLatests() {
    CorporatePerformance cp11 = new CorporatePerformance(1001, 2014, 12);
    CorporatePerformance cp12 = new CorporatePerformance(1001, 2013, 12);
    CorporatePerformance cp21 = new CorporatePerformance(1002, 2013, 12);
    cp11.salesAmount = 100; cp11.netProfit = 10;
    cp12.salesAmount = 200; cp11.netProfit = -10;
    cp21.salesAmount = 1000; cp11.netProfit = 100;
    Map<String, CorporatePerformance> cp_map = new HashMap<String, CorporatePerformance>();
    cp_map.put(cp11.getKeyString(), cp11);
    cp_map.put(cp12.getKeyString(), cp12);
    cp_map.put(cp21.getKeyString(), cp21);
    try {
      CorporatePerformance.updateMap(cp_map, c);
      Map<String, CorporatePerformance> latests = CorporatePerformance.selectLatests(c);
      CorporatePerformance tcp1 = latests.get("1001");
      CorporatePerformance tcp2 = latests.get("1002");
      cp11.settlingYear = 0; cp11.settlingMonth = 0;
      cp21.settlingYear = 0; cp21.settlingMonth = 0;
      assertEquals(tcp1, cp11);
      assertEquals(tcp2, cp21);
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
