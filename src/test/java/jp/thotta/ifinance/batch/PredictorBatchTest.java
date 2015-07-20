package jp.thotta.ifinance.batch;

import java.sql.Connection;
import java.sql.SQLException;

import junit.framework.TestCase;
import java.util.Map;
import jp.thotta.ifinance.utilizer.CollectorSampleGenerator;
import jp.thotta.ifinance.model.PredictedStockHistory;

public class PredictorBatchTest extends TestCase {
  CollectorSampleGenerator csg;

  protected void setUp() {
    try {
      csg = new CollectorSampleGenerator(30);
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void testPredict() {
    try {
      Connection c = csg.getConnection();
      PredictorBatch predictor = new PredictorBatch(c);
      predictor.predict();
      Map<String, PredictedStockHistory> m = 
        PredictedStockHistory.selectAll(c);
      assertTrue(m.size() > 0);
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
