package jp.thotta.ifinance.utilizer;

import jp.thotta.ifinance.batch.PredictorBatch;
import junit.framework.TestCase;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class PredictedStockPriceTest extends TestCase {
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

    public void testSelectLatests() {
        try {
            List<PredictedStockPrice> l = PredictedStockPrice.selectLatests(c);
            System.out.println("pspList.size=" + l.size());
            for (PredictedStockPrice psp : l) {
                System.out.println(psp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void testSelectLatestMap() {
        try {
            List<PredictedStockPrice> l = PredictedStockPrice.selectLatests(c);
            Map<String, PredictedStockPrice> m = PredictedStockPrice.selectLatestMap(c);
            System.out.println("pspList.size=" + m.size());
            assertTrue(m.size() > 0);
            for (String k : m.keySet()) {
                PredictedStockPrice psp = m.get(k);
                System.out.println(psp.getDescription());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
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
