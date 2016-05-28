package jp.thotta.ifinance.batch;

import jp.thotta.ifinance.common.MyDate;
import jp.thotta.ifinance.model.Database;
import jp.thotta.ifinance.model.PredictedStockHistory;
import jp.thotta.ifinance.utilizer.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * 予測結果をDBに登録するバッチ.
 *
 * @author toru1055
 */
public class PredictorBatch {
    Connection conn;

    public PredictorBatch(Connection c) {
        this.conn = c;
    }

    /**
     * 予測実行.
     */
    public void predict() throws SQLException, ParseException {
        Map<String, JoinedStockInfo> jsiMap = JoinedStockInfo.selectMap(conn);
        Map<String, JoinedStockInfo> jsiFil = JoinedStockInfo.filterMap(jsiMap);
        //StockPricePredictor spp = new LinearStockPricePredictor();
        //StockPricePredictor spp = new LinearStockPricePredictorNoIntercept();
        StockPricePredictor spp = new ProfitCategoryPredictor();
        //StockPricePredictor spp = new BusinessCategoryPredictor();
        double rmse = spp.trainValidate(jsiFil);
        System.out.println("Train data size = " + jsiFil.size() + ", RMSE = " + rmse);
        StockStatsFilter filter = new StockStatsFilter(jsiMap, 0, 0, 0, 0, 0);
        System.out.println(filter);
        Map<String, PredictedStockHistory> histories =
                new HashMap<String, PredictedStockHistory>();
        for (String k : jsiFil.keySet()) {
            JoinedStockInfo jsi = jsiMap.get(k);
            PredictedStockPrice psp = new PredictedStockPrice(jsi, spp, filter);
            PredictedStockHistory psh = new PredictedStockHistory(
                    jsi.dailyStockPrice.stockId,
                    MyDate.getToday());
            psh.predictedMarketCap = psp.predictedMarketCap;
            psh.isStableStock = psp.isStableStock;
            histories.put(psh.getKeyString(), psh);
        }
        PredictedStockHistory.updateMap(histories, conn);
        Map<String, BusinessCategoryStats> bcMap = BusinessCategoryStats.selectMap(conn);
        //System.out.println("=== 業種情報 ===");
        //for(String k : bcMap.keySet()) {
        //  BusinessCategoryStats bc = bcMap.get(k);
        //  System.out.println(bc);
        //}
    }

    public static void main(String[] args) {
        try {
            Connection c = Database.getConnection();
            PredictorBatch preditor = new PredictorBatch(c);
            preditor.predict();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                Database.closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
