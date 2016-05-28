package jp.thotta.ifinance.adhoc;

import jp.thotta.ifinance.model.CompanyProfile;
import jp.thotta.ifinance.model.DailyStockPrice;
import jp.thotta.ifinance.utilizer.JoinedStockInfo;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class StockPriceIncreaseEstimator
        extends BaseStockScoreEstimator {
    static final int INCREASE_PERIOD = 90;
    static final int MODEL_ID = 7;

    Map<String, JoinedStockInfo> joinedMap;

    public StockPriceIncreaseEstimator(Connection c) throws Exception {
        super(c, MODEL_ID);
        joinedMap = JoinedStockInfo.selectAllMap(c);
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
        responseVariables = DailyStockPrice.selectIncreaseRatio(INCREASE_PERIOD, c);
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
