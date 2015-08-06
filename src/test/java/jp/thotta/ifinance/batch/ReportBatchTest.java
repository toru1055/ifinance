package jp.thotta.ifinance.batch;

import java.sql.Connection;
import java.sql.SQLException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import jp.thotta.ifinance.utilizer.CollectorSampleGenerator;
import jp.thotta.ifinance.model.PredictedStockHistory;

public class ReportBatchTest extends TestCase {
  CollectorSampleGenerator csg;

  protected void setUp() {
    try {
      csg = new CollectorSampleGenerator(300);
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void testUndervaluedStockRankingReport() {
    try {
      Connection c = csg.getConnection();
      PredictorBatch predictor = new PredictorBatch(c);
      predictor.predict();
      UndervaluedStockRankingReport r = new UndervaluedStockRankingReport(c);
      //assertTrue(r.report());
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
