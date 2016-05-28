package jp.thotta.ifinance.utilizer;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

import java.util.Map;

public class LinearStockPricePredictor
        extends BaseStockPricePredictor
        implements StockPricePredictor {
    public double[] w;

    public void train(Map<String, JoinedStockInfo> jsiMap) {
        OLSMultipleLinearRegression mlr =
                new OLSMultipleLinearRegression();
        double[][] X = new double[jsiMap.size()][];
        double[] y = new double[jsiMap.size()];
        int i = 0;
        for (String k : jsiMap.keySet()) {
            JoinedStockInfo jsi = jsiMap.get(k);
            X[i] = jsi.getRegressors();
            y[i] = jsi.getRegressand();
            i++;
        }
        mlr.newSampleData(y, X);
        w = mlr.estimateRegressionParameters();
        showWeights();
    }

    public boolean isTrained() {
        return (w.length > 0);
    }

    private void showWeights() {
        System.out.println("Trained model w:");
        for (int i = 0; i < w.length; i++) {
            System.out.println(String.format("%d: %.2f", i, w[i]));
        }
    }

    public long predict(JoinedStockInfo jsi) {
        double y_hat = w[0];
        double[] x = jsi.getRegressors();
        for (int i = 0; i < x.length; i++) {
            y_hat += w[i + 1] * x[i];
        }
        return (long) y_hat;
    }

    // TODO: 実装する
    public void save(String filename) {
    }

}
