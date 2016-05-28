package jp.thotta.ifinance.utilizer;

import java.util.Map;

public abstract class BaseStockPricePredictor
        implements StockPricePredictor {

    public abstract void train(Map<String, JoinedStockInfo> jsiMap);

    public abstract long predict(JoinedStockInfo jsi);

    public abstract void save(String filename);

    public abstract boolean isTrained();

    public Double validate(Map<String, JoinedStockInfo> jsiMap) {
        if (!isTrained()) {
            return null;
        }
        double rmse = 0.0;
        double rmser = 0.0;
        for (String k : jsiMap.keySet()) {
            JoinedStockInfo jsi = jsiMap.get(k);
            double predVal = (double) predict(jsi);
            double actualVal = (double) jsi.dailyStockPrice.marketCap;
            double error = predVal - actualVal;
            double squareError = error * error;
            double errorRatio = Math.abs(error) / actualVal;
            rmse += squareError / jsiMap.size();
            rmser += errorRatio / jsiMap.size();
        }
        rmse = Math.sqrt(rmse);
        System.out.println("平均誤差: " + rmse + ", 平均誤差割合: " + rmser);
        return rmser;
    }

    public Double trainValidate(Map<String, JoinedStockInfo> jsiMap) {
        train(jsiMap);
        return validate(jsiMap);
    }

}
