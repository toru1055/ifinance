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
  CollectorSampleGenerator csg;
  protected void setUp() {
    try {
      csg = new CollectorSampleGenerator();
      conn = csg.getConnection();
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
      double rmse = spp.trainValidate(jsiMap);
      assertEquals(spp.w.length, JoinedStockInfo.FEATURE_DIMENSION + 1);
      System.out.print("w: [");
      for(int i = 0; i < spp.w.length; i++) {
        System.out.print(spp.w[i] + ", ");
      }
      System.out.println("]");
      System.out.println("RMSE = " + rmse);
      int j = 0;
      double t_rmse = 0.0;
      double t_rmser = 0.0;
      for(String k : jsiMap.keySet()) {
        JoinedStockInfo jsi = jsiMap.get(k);
        double error = jsi.getRegressand() - spp.predict(jsi);
        t_rmse += (error * error) / jsiMap.size();
        t_rmser += (Math.abs(error) / jsi.getRegressand()) / jsiMap.size();
        if(j++ < 5) {
          System.out.println(
            String.format("k = %s, y = %d, y_hat = %d",
              k,
              (long)jsi.getRegressand(), 
              spp.predict(jsi)));
        }
      }
      t_rmse = Math.sqrt(t_rmse);
      assertEquals(t_rmser, rmse, 0.5);
      assertTrue(rmse < 350000);
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void testTrainPredictNoIntercept() {
    try {
      LinearStockPricePredictorNoIntercept spp =
        new LinearStockPricePredictorNoIntercept();
      Map<String, JoinedStockInfo> jsiMap =
        JoinedStockInfo.selectMap(conn);
      double rmse = spp.trainValidate(jsiMap);
      assertEquals(spp.w.length, JoinedStockInfo.FEATURE_DIMENSION);
      System.out.print("w: [");
      for(int i = 0; i < spp.w.length; i++) {
        System.out.print(spp.w[i] + ", ");
      }
      System.out.println("]");
      System.out.println("RMSE = " + rmse);
      int j = 0;
      double t_rmse = 0.0;
      double t_rmser = 0.0;
      for(String k : jsiMap.keySet()) {
        JoinedStockInfo jsi = jsiMap.get(k);
        double error = jsi.getRegressand() - spp.predict(jsi);
        t_rmse += (error * error) / jsiMap.size();
        t_rmser += (Math.abs(error) / jsi.getRegressand()) / jsiMap.size();
        if(j++ < 5) {
          System.out.println(
            String.format("k = %s, y = %d, y_hat = %d",
              k,
              (long)jsi.getRegressand(), 
              spp.predict(jsi)));
        }
      }
      t_rmse = Math.sqrt(t_rmse);
      assertEquals(t_rmser, rmse, 0.5);
      assertTrue(rmse < 350000);
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void tearDown() {
    try {
      csg.closeConnection();
    } catch(SQLException e) {
      e.printStackTrace();
    }
  }
}
