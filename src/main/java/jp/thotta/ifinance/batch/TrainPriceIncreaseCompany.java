package jp.thotta.ifinance.batch;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;

import jp.thotta.ifinance.model.Database;
import jp.thotta.ifinance.model.DailyStockPrice;
import jp.thotta.ifinance.model.CompanyNews;
import jp.thotta.ifinance.utilizer.Accuracy;
import jp.thotta.ifinance.utilizer.Utility;
import jp.thotta.ifinance.adhoc.StockPriceIncreaseEstimator;
import jp.thotta.ifinance.adhoc.StockPriceIncreaseEstimatorValidation;

/**
 * 直近の値上り率を銘柄の特徴で学習させる.
 */
public class TrainPriceIncreaseCompany extends BaseRankingReport {
  public static final double p_threshold = 0.05;
  public static final double a_threshold = 0.03;
  Connection c;
  StockPriceIncreaseEstimator estimator;
  StockPriceIncreaseEstimatorValidation validator;

  public TrainPriceIncreaseCompany(Connection c, String tmpl) throws Exception {
    super(c, tmpl, "週刊値上り予想銘柄ランキング");
    this.c = c;
    estimator = new StockPriceIncreaseEstimator(c);
    validator = new StockPriceIncreaseEstimatorValidation(c);
  }

  public void execTraining() {
    System.out.println("=== Training ===");
    estimator.execTraining();
    validator.execTraining();
  }

  public void execValidation() {
    System.out.println("=== Validation Result ===");
    Map<String, Double> m = new HashMap<String, Double>();
    validator.execPrediction();
    for(String k : validator.keySet()) {
      Double p = validator.getPredicted(k);
      Double a = validator.getActual(k);
      if(p > p_threshold && a > a_threshold) {
        m.put(k, p - a);
      }
    }
    int tp = 0, fp = 0;
    double score = 0.0;
    for(String k : Utility.sortedKeys(m)) {
      Double p = validator.getPredicted(k);
      Double a = validator.getActual(k);
      Double v = validator.getValidation(k);
      if(validator.getValidation(k) > 0.0) {
        tp++;
      } else {
        fp++;
      }
      score += validator.getValidation(k);
      System.out.println(k + "\t" + p + "\t" + a + "\t" + v + "\t" + m.get(k));
      if(tp + fp >= 30) {
        break;
      }
    }
    score = score / (tp + fp);
    double precision = (double)tp / (tp + fp);
    System.out.println("Precision: " + precision);
    System.out.println("Score: " + score);
  }

  public void execPrediction() {
    System.out.println("=== Prediction Result ===");
    Map<String, Double> m = new HashMap<String, Double>();
    estimator.execPrediction();
    for(String k : estimator.keySet()) {
      Double p = estimator.getPredicted(k);
      Double a = estimator.getActual(k);
      if(p > p_threshold && a > a_threshold) {
        m.put(k, p - a);
      }
    }
  }

  @Override
  protected void setCompanyNewsMap()
    throws SQLException, ParseException {
    this.cnMap = CompanyNews.selectMapByPast(this.conn, 30);
  }

  @Override
  protected Map<String, Double> estimatePriceIncreaseRatio() {
    Map<String, Double> m = new HashMap<String, Double>();
    estimator.execPrediction();
    for(String k : estimator.keySet()) {
      Double p = estimator.getPredicted(k);
      Double a = estimator.getActual(k);
      if(p > p_threshold && a > a_threshold) {
        m.put(k, p - a);
      }
    }
    return m;
  }

  public static void main(String[] args) {
    String mode = "predict";
    String tmpl = "text";
    if(args.length >= 1) {
      mode = args[0];
    }
    if(args.length >= 2) {
      tmpl = args[1];
    }
    try {
      TrainPriceIncreaseCompany train =
        new TrainPriceIncreaseCompany(Database.getConnection(), tmpl);
      if("train".equals(mode)) {
        train.execTraining();
      } else if("predict".equals(mode)) {
        train.report();
      } else if("validate".equals(mode)) {
        train.execValidation();
      }
    } catch(Exception e) {
      e.printStackTrace();
      System.exit(1);
    } finally {
      try {
        Database.closeConnection();
      } catch(Exception e) {
        e.printStackTrace();
        System.exit(1);
      }
    }
  }
}
