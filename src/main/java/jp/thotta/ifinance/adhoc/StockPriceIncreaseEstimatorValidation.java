package jp.thotta.ifinance.adhoc;

import jp.thotta.ifinance.model.CompanyProfile;
import jp.thotta.ifinance.model.DailyStockPrice;
import jp.thotta.ifinance.utilizer.JoinedStockInfo;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class StockPriceIncreaseEstimatorValidation
        extends BaseStockScoreEstimator {
    static final int VALIDATION_PERIOD = 30;
    static final int INCREASE_PERIOD = 90;
    static final int MODEL_ID = 6;

    Map<String, JoinedStockInfo> joinedMap;
    Map<String, Double> validationScores;

    public StockPriceIncreaseEstimatorValidation(Connection c)
            throws Exception {
        super(c, MODEL_ID);
        setValidationScores();
        joinedMap = JoinedStockInfo.selectAllMap(c);
    }

    public Double getValidation(String k) {
        return validationScores.get(k);
    }

    void setValidationScores() throws Exception {
        validationScores = DailyStockPrice.selectIncreaseRatio(VALIDATION_PERIOD, c);
        BaseStockScoreEstimator.centering(validationScores);
    }

    @Override
    protected boolean isPrediction(String k) {
        if (joinedMap.containsKey(k) && joinedMap.get(k).isGrowing()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void setResponseVariables() throws Exception {
        responseVariables = DailyStockPrice.selectIncreaseRatio(VALIDATION_PERIOD, INCREASE_PERIOD, c);
        BaseStockScoreEstimator.centering(responseVariables);
    }

    @Override
    protected void setPredictorVariables() throws Exception {
        predictorVariables = new HashMap<String, String>();
        Map<String, CompanyProfile> profileMap =
                CompanyProfile.selectAll(c);
        for (String k : profileMap.keySet()) {
            CompanyProfile profile = profileMap.get(k);
            if (profile.hasEnoughFeature()) {
                predictorVariables.put(k, profile.companyFeature);
            }
        }
    }
}
