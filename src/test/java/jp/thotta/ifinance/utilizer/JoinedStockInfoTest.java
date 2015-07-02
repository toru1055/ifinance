package jp.thotta.ifinance.utilizer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.HashMap;

import jp.thotta.ifinance.model.CorporatePerformance;
import jp.thotta.ifinance.model.DailyStockPrice;
import jp.thotta.ifinance.model.Database;
import jp.thotta.ifinance.common.MyDate;

public class JoinedStockInfoTest extends TestCase {
  Connection conn;
  protected void setUp() {
    try {
      conn = CollectorSampleGenerator.getConnection();
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void testSelectMap() {
    try {
      Map<String, JoinedStockInfo> jsiMap =
        JoinedStockInfo.selectMap(conn);
      Map<String, CorporatePerformance> cpMap =
        CollectorSampleGenerator.cpMap;
      Map<String, DailyStockPrice> dspMap =
        CollectorSampleGenerator.dspMap;
      for(String k : cpMap.keySet()) {
        CorporatePerformance cp = cpMap.get(k);
        String joinKey = String.format("%04d", cp.stockId);
        JoinedStockInfo jsi = jsiMap.get(joinKey);
        if(cp.settlingYear == 2014) {
          cp.settlingYear = 0;
          cp.settlingMonth = 0;
          assertEquals(jsi.corporatePerformance, cp);
        } else {
          cp.settlingYear = 0;
          cp.settlingMonth = 0;
          assertFalse(jsi.corporatePerformance.equals(cp));
        }
      }
      for(String k : dspMap.keySet()) {
        DailyStockPrice dsp = dspMap.get(k);
        String joinKey = String.format("%04d", dsp.stockId);
        JoinedStockInfo jsi = jsiMap.get(joinKey);
        if(dsp.date.day == 10) {
          dsp.date = null;
          assertEquals(jsi.dailyStockPrice, dsp);
        } else {
          dsp.date = null;
          assertFalse(jsi.dailyStockPrice.equals(dsp));
        }
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  protected void tearDown() {
    try {
      conn.close();
    } catch(SQLException e) {
      e.printStackTrace();
    }
  }
}
