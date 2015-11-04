package jp.thotta.ifinance.utilizer;

import java.util.Map;
import java.util.HashMap;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

public class ProfitCategoryPredictor
  extends BaseStockPricePredictor
  implements StockPricePredictor {
  public Map<String, Double[]> weightMap;

  public void train(Map<String, JoinedStockInfo> jsiMap) {
    Map<String, Map<String, JoinedStockInfo>> jsiMapMap =
      new HashMap<String, Map<String, JoinedStockInfo>>();
    for(String k : jsiMap.keySet()) {
      JoinedStockInfo jsi = jsiMap.get(k);
      String category = getCategory(jsi);
      Map<String, JoinedStockInfo> jsiMapCategory = jsiMapMap.get(category);
      if(jsiMapCategory == null) {
        jsiMapCategory = new HashMap<String, JoinedStockInfo>();
      }
      jsiMapCategory.put(k, jsi);
      jsiMapMap.put(category, jsiMapCategory);
    }
    jsiMapMap.put("all", jsiMap);
    weightMap = new HashMap<String, Double[]>();
    for(String category : jsiMapMap.keySet()) {
      Map<String, JoinedStockInfo> jsiMapCategory = jsiMapMap.get(category);
      System.out.println("「" + category + "」の学習モデル: 学習データ数 = " + jsiMapCategory.size());
      if(jsiMapCategory.size() < JoinedStockInfo.FEATURE_DIMENSION * 10) {
        System.out.println("学習データ数が少ないので学習スキップ\n");
        continue;
      }
      Double[] w = trainCategory(jsiMapCategory);
      showWeights(w);
      weightMap.put(category, w);
    }
  }

  private String getCategory(JoinedStockInfo jsi) {
    String category = "minus";
    if(jsi.corporatePerformance.operatingProfit > 5246) {
      category = "q4";
    } else if(jsi.corporatePerformance.operatingProfit > 1339) {
      category = "q3";
    } else if(jsi.corporatePerformance.operatingProfit > 345) {
      category = "q2";
    } else if(jsi.corporatePerformance.operatingProfit > 0) {
      category = "q1";
    } else {
      category = "minus";
    }
    return category;
  }

  public Double[] trainCategory(Map<String, JoinedStockInfo> jsiMap) {
    OLSMultipleLinearRegression mlr = 
      new OLSMultipleLinearRegression();
    mlr.setNoIntercept(true);
    double[][] X = new double[jsiMap.size()][];
    double[] y = new double[jsiMap.size()];
    int i = 0;
    for(String k : jsiMap.keySet()) {
      JoinedStockInfo jsi = jsiMap.get(k);
      X[i] = jsi.getRegressors();
      y[i] = jsi.getRegressand();
      i++;
    }
    mlr.newSampleData(y, X);
    double[] w = mlr.estimateRegressionParameters();
    return castDouble(w);
  }

  Double[] castDouble(double[] a) {
    Double[] obj = new Double[a.length];
    for(int i = 0; i < a.length; i++) {
      obj[i] = a[i];
    }
    return obj;
  }

  public long predict(JoinedStockInfo jsi) {
    Double[] w = weightMap.get(getCategory(jsi));
    if(w  == null || getCategory(jsi).equals("minus")) {
      w = weightMap.get("all");
    }
    double y_hat = 0.0;
    double[] x = jsi.getRegressors();
    for(int i = 0; i < x.length; i++) {
      y_hat += w[i] * x[i];
    }
    return (long)y_hat;
  }

  private void showWeights(Double[] w) {
    for(int i = 0; i < w.length; i++) {
      System.out.println(String.format("%d: %.2f", i, w[i]));
    }
    System.out.println();
  }

  public boolean isTrained() {
    return (weightMap != null && weightMap.size() > 0);
  }

  // TODO: 実装する
  public void save(String filename) {
  }
}
