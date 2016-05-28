package jp.thotta.ifinance.extractor;

import jp.thotta.ifinance.utilizer.PredictedStockPrice;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * 実時価総額と予測時価総額の組をデータ抽出するクラス.
 *
 * @author toru1055
 */
public class ActualPredictedExtractor extends AbstractDataExtractor {
    private static final String DATA_NAME = "ActualPredicted";
    Connection conn;

    public ActualPredictedExtractor(Connection c) {
        super(DATA_NAME);
        this.conn = c;
        header.add("stock_id");
        header.add("actual_market_cap");
        header.add("predicted_market_cap");
    }

    @Override
    public void extract() {
        try {
            List<PredictedStockPrice> pspList = PredictedStockPrice.selectLatests(conn);
            for (PredictedStockPrice psp : pspList) {
                List<String> l = new ArrayList<String>();
                l.add(String.valueOf(psp.stockId));
                l.add(String.valueOf(psp.joinedStockInfo.dailyStockPrice.marketCap));
                l.add(String.valueOf(psp.predictedMarketCap));
                data.add(l);
            }
            write();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
