package jp.thotta.ifinance.extractor;

import jp.thotta.ifinance.batch.TrainCompanyNewsClassifier;
import jp.thotta.ifinance.batch.TrainYahooNewsClassifier;

import java.util.ArrayList;
import java.util.List;

/**
 * 値上り率とニュースの学習データを抽出.
 */
public class StockIdNewsExtractor extends AbstractDataExtractor {
    private static final String DATA_NAME = "StockIdNewsDoc";

    public StockIdNewsExtractor() {
        super(DATA_NAME);
        header.add("stock_id");
        header.add("news_document");
    }

    @Override
    public void extract() {
        try {
            TrainCompanyNewsClassifier train =
                    new TrainCompanyNewsClassifier(1);
            for (String stockId : train.keySet()) {
                String newsDoc = train.get(stockId);
                List<String> row = new ArrayList<String>();
                row.add(stockId);
                row.add(newsDoc);
                this.data.add(row);
            }
            TrainYahooNewsClassifier yahoo =
                    new TrainYahooNewsClassifier(1);
            for (String stockId : yahoo.keySet()) {
                String newsDoc = yahoo.get(stockId);
                List<String> row = new ArrayList<String>();
                row.add(stockId);
                row.add(newsDoc);
                this.data.add(row);
            }
            this.write();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
