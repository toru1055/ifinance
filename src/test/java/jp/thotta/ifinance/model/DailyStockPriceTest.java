package jp.thotta.ifinance.model;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import jp.thotta.ifinance.common.MyDate;

public class DailyStockPriceTest extends TestCase {
  Connection c;
  Statement st;
  DailyStockPrice dsp, dsp2;

  protected void setUp() {
    MyDate myDate = new MyDate(2015, 3, 3);
    dsp = new DailyStockPrice(9999, myDate);
    dsp.marketCap = 1000;
    dsp2 = new DailyStockPrice(9999, myDate);
    dsp2.stockNumber = 100;
    try {
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
      assertEquals(dsp.stockNumber, dsp2.stockNumber);
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
