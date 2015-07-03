package jp.thotta.ifinance.utilizer;

import java.util.Map;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

public class LinearStockPricePredictor
  implements StockPricePredictor {
  public double[] w;

  public double train(Map<String, JoinedStockInfo> jsiMap) {
    OLSMultipleLinearRegression mlr = 
      new OLSMultipleLinearRegression();
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
    w = mlr.estimateRegressionParameters();
    double rmse = 0.0;
    double[] error = mlr.estimateResiduals();
    for(i = 0; i < error.length; i++) {
      rmse += (error[i] * error[i]) / error.length;
    }
    rmse = Math.sqrt(rmse);
    return rmse;
  }

  public long predict(JoinedStockInfo jsi) {
    double y_hat = w[0];
    double[] x = jsi.getRegressors();
    for(int i = 0; i < x.length; i++) {
      y_hat += w[i+1] * x[i];
    }
    return (long)y_hat;
  }

  // TODO: 実装する
  public void save(String filename) {
  }

}
