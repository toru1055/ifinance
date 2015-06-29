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
  }

  protected void tearDown() {
    try {
      Database.closeConnection();
    } catch(SQLException e) {
      e.printStackTrace();
    }
  }
}
