package jp.thotta.ifinance.utilizer;

import junit.framework.TestCase;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

import jp.thotta.ifinance.common.MyDate;
import jp.thotta.ifinance.utilizer.CollectorSampleGenerator;
import jp.thotta.ifinance.model.PredictedStockHistory;
import jp.thotta.ifinance.batch.PredictorBatch;

public class PredictedStockPriceTest extends TestCase {
  CollectorSampleGenerator csg;
  Connection c;

  protected void setUp() {
    try {
      csg = new CollectorSampleGenerator(300);
      c = csg.getConnection();
      PredictorBatch predictor = new PredictorBatch(c);
      predictor.predict();
    } catch(Exception e) {
      e.printStackTrace();
    }
  } 

  public void testSelectLatests() {
    try {
      List<PredictedStockPrice> l = PredictedStockPrice.selectLatests(c);
      System.out.println("pspList.size=" + l.size());
      for(PredictedStockPrice psp : l) {
        System.out.println(psp);
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
