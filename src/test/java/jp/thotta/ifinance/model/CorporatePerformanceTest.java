package jp.thotta.ifinance.model;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CorporatePerformanceTest extends TestCase {
  Connection c;
  Statement st;
  CorporatePerformance cp, cp2;

  protected void setUp() {
    cp = new CorporatePerformance(9999, 2015, 3);
    cp.salesAmount = 1000;
    cp2 = new CorporatePerformance(9999, 2015, 3);
    cp2.netProfit = -100;
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

  protected void tearDown() {
    try {
      Database.closeConnection();
    } catch(SQLException e) {
      e.printStackTrace();
    }
  }
}
