package jp.thotta.ifinance.collector.yj_finance;

import junit.framework.TestCase;

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

import java.sql.Connection;
import java.sql.SQLException;

import jp.thotta.ifinance.model.CompanyProfile;
import jp.thotta.ifinance.model.Database;
import jp.thotta.ifinance.collector.CompanyProfileCollector;

public class CompanyProfileCollectorTest extends TestCase {
  Map<String, CompanyProfile> profiles;
  Connection c;

  protected void setUp() {
    profiles = new HashMap<String, CompanyProfile>();
    try {
      Database.setDbUrl("jdbc:sqlite:test.db");
      c = Database.getConnection();
      CompanyProfile.dropTable(c);
      CompanyProfile.createTable(c);
    } catch(SQLException e) {
      e.printStackTrace();
    }
  }

  public void testFoundationDateCollector() {
    FoundationDateCollectorImpl fdc =
      new FoundationDateCollectorImpl();
    fdc.setStartPage(78);
    try {
      fdc.append(profiles);
      assertCompanyProfileMap(profiles);
    } catch(IOException e) {
      e.printStackTrace();
    }
  }

  public void assertCompanyProfileMap(Map<String, CompanyProfile> m) {
    assertTrue(m.size() > 0);
    for(String k : m.keySet()) {
      CompanyProfile cp = m.get(k);
      assertTrue(cp.stockId > 0 && cp.stockId < 100000);
      assertTrue(!cp.companyName.equals(""));
      assertTrue(cp.foundationDate == null || cp.foundationDate.year > 1000);
    }
  }

  public void testDirectDb() {
    FoundationDateCollectorImpl fdc =
      new FoundationDateCollectorImpl();
    fdc.setStartPage(78);
    try {
      fdc.appendDb(c);
      Map<String, CompanyProfile> cm = CompanyProfile.selectAll(c);
      assertCompanyProfileMap(cm);
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
