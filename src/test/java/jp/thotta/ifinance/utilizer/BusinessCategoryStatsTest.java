package jp.thotta.ifinance.utilizer;

import junit.framework.TestCase;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import jp.thotta.ifinance.model.CorporatePerformance;
import jp.thotta.ifinance.model.DailyStockPrice;
import jp.thotta.ifinance.model.CompanyProfile;
import jp.thotta.ifinance.model.Database;
import jp.thotta.ifinance.common.MyDate;

public class BusinessCategoryStatsTest extends TestCase {
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
      Map<String, BusinessCategoryStats> m = BusinessCategoryStats.selectMap(conn);
      for(String k : m.keySet()) {
        System.out.println(m.get(k));
      }
    } catch(Exception e) {
      e.printStackTrace();
      System.exit(1);
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
