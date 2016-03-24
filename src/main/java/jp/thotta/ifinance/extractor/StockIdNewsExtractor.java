package jp.thotta.ifinance.extractor;

import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.SQLException;

import jp.thotta.ifinance.batch.TrainCompanyNewsClassifier;

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
      TrainCompanyNewsClassifier train = new TrainCompanyNewsClassifier(1);
      for(String stockId : train.keySet()) {
        String newsDoc = train.get(stockId);
        List<String> row = new ArrayList<String>();
        row.add(stockId);
        row.add(newsDoc);
        this.data.add(row);
      }
      this.write();
    } catch(Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
