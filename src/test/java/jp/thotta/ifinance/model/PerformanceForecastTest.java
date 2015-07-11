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

public class PerformanceForecastTest extends TestCase {
  Connection c;
  Statement st;
  Map<String, PerformanceForecast> m;
  PerformanceForecast pf, pfc;
  PerformanceForecast pm1, pm2;

  protected void setUp() {
    pf = new PerformanceForecast(9999, 2016, 3);
    pfc = new PerformanceForecast(9999, 2016, 3);
    pf.dividend = 5.5;
    pfc.dividendYield = 0.01;
    m = new HashMap<String, PerformanceForecast>();
    pm1 = new PerformanceForecast(1001, 2016, 3);
    pm2 = new PerformanceForecast(1002, 2016, 5);
    pm1.dividend = 50.0; pm1.dividendYield = 0.031;
    pm2.dividend = 30.0; pm2.dividendYield = 0.021;
    m.put(pm1.getKeyString(), pm1);
    m.put(pm2.getKeyString(), pm2);
    try {
      Database.setDbUrl("jdbc:sqlite:test.db");
      c = Database.getConnection();
      st = c.createStatement();
      PerformanceForecast.dropTable(c);
      PerformanceForecast.createTable(c);
    } catch(SQLException e) {
      e.printStackTrace();
    }
  }

  public void testGetKeyString() {
    PerformanceForecast pf = new PerformanceForecast(9999, 2016, 3);
    assertEquals(pf.getKeyString(), "9999,2016/03");
  }

  public void testExists() {
    try {
      assertFalse(pf.exists(st));
      pf.insert(st);
      assertTrue(pf.exists(st));
    } catch(SQLException e) {
      e.printStackTrace();
    }
  }

  public void testInsertUpdate() {
    try {
      pf.insert(st);
      pfc.readDb(st);
      assertEquals(pf.dividend, pfc.dividend);
      pfc.update(st);
      pf.readDb(st);
      assertEquals(pf, pfc);
    } catch(SQLException e) {
      e.printStackTrace();
    }
  }

  public void testUpdateMap() {
    try {
      PerformanceForecast.updateMap(m, c);
      Map<String, PerformanceForecast> fromDbMap = PerformanceForecast.selectAll(c);
      for(String k : m.keySet()) {
        PerformanceForecast pf_map = m.get(k);
        PerformanceForecast pf_db = fromDbMap.get(k);
        assertEquals(pf_map, pf_db);
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void testSelectLatests() {
    PerformanceForecast pf1_1 = new PerformanceForecast(1001, 2015, 3);
    PerformanceForecast pf1_2 = new PerformanceForecast(1001, 2016, 3);
    PerformanceForecast pf2_1 = new PerformanceForecast(1002, 2015, 3);
    pf1_1.dividend = 50; pf1_1.dividendYield = 0.05;
    pf1_2.dividend = 200; pf1_2.dividendYield = 0.03;
    pf2_1.dividend = 100; pf2_1.dividendYield = 0.04;
    Map<String, PerformanceForecast> m = new HashMap<String, PerformanceForecast>();
    m.put(pf1_1.getKeyString(), pf1_1);
    m.put(pf1_2.getKeyString(), pf1_2);
    m.put(pf2_1.getKeyString(), pf2_1);
    try {
      PerformanceForecast.updateMap(m, c);
      Map<String, PerformanceForecast> latests = PerformanceForecast.selectLatests(c);
      System.out.println(latests);
      PerformanceForecast pf1 = latests.get("1001");
      PerformanceForecast pf2 = latests.get("1002");
      pf1_2.settlingYear = 0; pf1_2.settlingMonth = 0;
      pf2_1.settlingYear = 0; pf2_1.settlingMonth = 0;
      assertEquals(pf1, pf1_2);
      assertEquals(pf2, pf2_1);
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
