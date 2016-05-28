package jp.thotta.ifinance.extractor;

import jp.thotta.ifinance.batch.PredictorBatch;
import jp.thotta.ifinance.utilizer.CollectorSampleGenerator;
import junit.framework.TestCase;

import java.sql.Connection;
import java.sql.SQLException;

public class ActualPredictedExtractorTest
        extends TestCase {
    CollectorSampleGenerator csg;
    Connection c;

    protected void setUp() {
        try {
            csg = new CollectorSampleGenerator(300);
            c = csg.getConnection();
            PredictorBatch predictor = new PredictorBatch(c);
            predictor.predict();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testExtract() {
        ActualPredictedExtractor ex = new ActualPredictedExtractor(c);
        ex.extract();
        assertTrue(ex.data.size() > 0);
    }

    protected void tearDown() {
        try {
            csg.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
