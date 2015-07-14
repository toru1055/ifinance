package jp.thotta.ifinance.collector.yj_finance;

import jp.thotta.ifinance.model.CorporatePerformance;
import jp.thotta.ifinance.model.Database;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import junit.framework.TestCase;

import java.sql.Connection;
import java.sql.SQLException;

public class BasePerformanceCollectorTest extends TestCase {
  List<Integer> stockIdList = new ArrayList<Integer>();
  BasePerformanceCollectorImpl coll;
  Connection c;

  protected void setUp() {
    stockIdList.add(8411);
    stockIdList.add(3787);
    coll = new BasePerformanceCollectorImpl(stockIdList);
    try {
      Database.setDbUrl("jdbc:sqlite:test.db");
      c = Database.getConnection();
      CorporatePerformance.dropTable(c);
      CorporatePerformance.createTable(c);
    } catch(SQLException e) {
      e.printStackTrace();
    }
  }

  public void testParseIndependentPerformance() {
    List<CorporatePerformance> cpList = coll.parseIndependentPerformance(8411);
    for(CorporatePerformance cp : cpList) {
      System.out.println(cp);
      assertTrue(cp != null);
    }
    cpList = coll.parseIndependentPerformance(8421);
    assertEquals(cpList.size(), 0);
  }

  public void testParseConsolidatePerformance() {
    List<CorporatePerformance> cpList = coll.parseConsolidatePerformance(8411);
    for(CorporatePerformance cp : cpList) {
      System.out.println(cp);
      assertTrue(cp != null);
    }
  }

  public void testAppend() {
    Map<String, CorporatePerformance> m = new HashMap<String, CorporatePerformance>();
    try {
      coll.append(m);
    } catch(Exception e) {
      e.printStackTrace();
    }
    assertTrue(m.size() > 0);
    for(String k : m.keySet()) {
      System.out.println(k + ": " + m.get(k));
    }
  }

  public void testAppendDb() {
    try {
      coll.appendDb(c);
      Map<String, CorporatePerformance> m = CorporatePerformance.selectAll(c);
      Map<String, CorporatePerformance> m2 = new HashMap<String, CorporatePerformance>();
      coll.append(m2);
      for(String k : m.keySet()) {
        assertEquals(m2.get(k), m.get(k));
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void tearDown() {
    try {
      Database.closeConnection();
    } catch(SQLException e) {
      e.printStackTrace();
    }
  }
}
