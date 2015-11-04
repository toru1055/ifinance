package jp.thotta.ifinance.utilizer;

import java.util.Map;
import java.util.HashMap;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

public class BusinessCategoryPredictor
  extends BaseStockPricePredictor
  implements StockPricePredictor {
  public Map<String, Double[]> weightMap;

  public void train(Map<String, JoinedStockInfo> jsiMap) {
    Map<String, Map<String, JoinedStockInfo>> jsiMapMap =
      new HashMap<String, Map<String, JoinedStockInfo>>();
    for(String k : jsiMap.keySet()) {
      JoinedStockInfo jsi = jsiMap.get(k);
      String category = jsi.companyProfile.businessCategory;
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
      System.out.println("「" + category + "」業種の学習モデル: 学習データ数 = " + jsiMapCategory.size());
      if(jsiMapCategory.size() < JoinedStockInfo.FEATURE_DIMENSION * 10) {
        System.out.println("学習データ数が少ないので学習スキップ\n");
        continue;
      }
      Double[] w = trainCategory(jsiMapCategory);
      showWeights(w);
      weightMap.put(category, w);
    }
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
    Double[] w = weightMap.get(jsi.companyProfile.businessCategory);
    if(w  == null) {
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
