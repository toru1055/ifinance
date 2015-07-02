package jp.thotta.ifinance.utilizer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.HashMap;

import jp.thotta.ifinance.model.Database;

public class LinearStockPricePredictorTest extends TestCase {
  Connection conn;
  protected void setUp() {
    try {
      conn = CollectorSampleGenerator.getConnection();
      //conn = Database.getConnection();
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void testTrainPredict() {
    try {
      LinearStockPricePredictor spp =
        new LinearStockPricePredictor();
      Map<String, JoinedStockInfo> jsiMap =
        JoinedStockInfo.selectMap(conn);
      spp.train(jsiMap);
      System.out.print("w: [");
      for(int i = 0; i < spp.w.length; i++) {
        System.out.print(spp.w[i] + ", ");
      }
      System.out.println("]");
      int j = 0;
      for(String k : jsiMap.keySet()) {
        JoinedStockInfo jsi = jsiMap.get(k);
        System.out.println(
            String.format("k = %s, y = %d, y_hat = %d",
              k,
              (long)jsi.getRegressand(), 
              spp.predict(jsi)));
        if(j++ > 5) { break; }
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void tearDown() {
    try {
      conn.close();
    } catch(SQLException e) {
      e.printStackTrace();
    }
  }
}
