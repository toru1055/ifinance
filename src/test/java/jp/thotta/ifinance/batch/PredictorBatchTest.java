package jp.thotta.ifinance.batch;

import jp.thotta.ifinance.model.PredictedStockHistory;
import jp.thotta.ifinance.utilizer.CollectorSampleGenerator;
import junit.framework.TestCase;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class PredictorBatchTest extends TestCase {
    CollectorSampleGenerator csg;

    protected void setUp() {
        try {
            csg = new CollectorSampleGenerator(300);
        } catch (Exception e) {
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
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    protected void tearDown() {
        try {
            csg.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
