package jp.thotta.ifinance.batch;

import java.sql.Connection;
import java.sql.SQLException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import jp.thotta.ifinance.utilizer.CollectorSampleGenerator;

public class ReportBatchTest extends TestCase {
  public void testUndervaluedStockRankingReport() {
    try {
      Connection c = CollectorSampleGenerator.getConnection(300);
      UndervaluedStockRankingReport r = new UndervaluedStockRankingReport(c);
      assertTrue(r.report());
    } catch(Exception e) {
      e.printStackTrace();
    } finally {
      try {
        CollectorSampleGenerator.closeConnection();
      } catch(SQLException e) {
        e.printStackTrace();
      }
    }
  }
}
