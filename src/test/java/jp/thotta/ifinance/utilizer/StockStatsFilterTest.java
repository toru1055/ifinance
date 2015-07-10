package jp.thotta.ifinance.utilizer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;

public class StockStatsFilterTest extends TestCase {
  Map<String, JoinedStockInfo> jsiMap;
  CollectorSampleGenerator csg;

  protected void setUp() {
    try {
      csg = new CollectorSampleGenerator(200);
      Connection conn = csg.getConnection();
      jsiMap = JoinedStockInfo.selectMap(conn);
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void testIsNotable() {
    StockStatsFilter filter = new StockStatsFilter(jsiMap);
    System.out.println("[StockStatsFilter]\n" + filter);
    int numNotable = 0;
    for(String k : jsiMap.keySet()) {
      JoinedStockInfo jsi = jsiMap.get(k);
      if(filter.isNotable(jsi)) {
        numNotable++;
      }
    }
    System.out.println(
        String.format("notable / all = %d / %d = %.3f", numNotable, jsiMap.size(), (double)numNotable/jsiMap.size()));
    assertTrue(numNotable > 0);
  }

  protected void tearDown() {
    try {
      csg.closeConnection();
    } catch(SQLException e) {
      e.printStackTrace();
    }
  }
}
