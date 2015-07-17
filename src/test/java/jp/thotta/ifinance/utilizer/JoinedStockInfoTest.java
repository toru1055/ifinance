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
import jp.thotta.ifinance.model.PerformanceForecast;
import jp.thotta.ifinance.model.CompanyProfile;
import jp.thotta.ifinance.model.Database;
import jp.thotta.ifinance.common.MyDate;

public class JoinedStockInfoTest extends TestCase {
  Connection conn;
  CollectorSampleGenerator csg;
  protected void setUp() {
    try {
      csg = new CollectorSampleGenerator();
      conn = csg.getConnection();
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void testSelectMap() {
    try {
      Map<String, JoinedStockInfo> jsiMap =
        JoinedStockInfo.selectMap(conn);
      Map<String, CorporatePerformance> cpMap = csg.cpMap;
      Map<String, DailyStockPrice> dspMap = csg.dspMap;
      Map<String, PerformanceForecast> pfMap = csg.pfMap;
      Map<String, CompanyProfile> profMap = csg.profMap;
      System.out.println("jsiMap.size = " + jsiMap.size());

      for(String k : profMap.keySet()) {
        CompanyProfile prof = profMap.get(k);
        JoinedStockInfo jsi = jsiMap.get(k);
        assertEquals(jsi.companyProfile, prof);
      }

      for(String k : cpMap.keySet()) {
        CorporatePerformance cp = cpMap.get(k);
        String joinKey = String.format("%04d", cp.stockId);
        JoinedStockInfo jsi = jsiMap.get(joinKey);
        if(cp.settlingYear == 2014) {
          assertEquals(jsi.corporatePerformance, cp);
        } else {
          assertFalse(jsi.corporatePerformance.equals(cp));
        }
      }

      for(String k : pfMap.keySet()) {
        PerformanceForecast pf = pfMap.get(k);
        String joinKey = String.format("%04d", pf.stockId);
        JoinedStockInfo jsi = jsiMap.get(joinKey);
        if(pf.settlingYear == 2016) {
          assertEquals(jsi.performanceForecast, pf);
        } else {
          assertFalse(jsi.performanceForecast.equals(pf));
        }
      }

      for(String k : dspMap.keySet()) {
        DailyStockPrice dsp = dspMap.get(k);
        String joinKey = String.format("%04d", dsp.stockId);
        JoinedStockInfo jsi = jsiMap.get(joinKey);
        if(dsp.date.day == 10) {
          assertEquals(jsi.dailyStockPrice, dsp);
        } else {
          assertFalse(jsi.dailyStockPrice.equals(dsp));
        }
      }

      Map<String, JoinedStockInfo> jsiFil = JoinedStockInfo.filterMap(jsiMap);
      assertTrue(jsiFil.size() > 0);
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  protected void tearDown() {
    try {
      csg.closeConnection();
    } catch(SQLException e) {
      e.printStackTrace();
    }
  }
}
