package jp.thotta.ifinance.extractor;

import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.SQLException;

import jp.thotta.ifinance.batch.TrainPriceIncreaseNews;

/**
 * 値上り率とニュースの学習データを抽出.
 */
public class PriceIncreaseNewsExtractor extends AbstractDataExtractor {
  private static final String DATA_NAME = "PriceIncreaseNews";

  public PriceIncreaseNewsExtractor() {
    super(DATA_NAME);
    header.add("stock_id");
    header.add("price_increase");
    header.add("company_news");
  }

  @Override
  public void extract() {
    try {
      TrainPriceIncreaseNews train = new TrainPriceIncreaseNews();
      for(String k : train.keySet()) {
        Double score = train.getScore(k);
        List<String> newsList = train.getNewsList(k);
        for(String newsTitle : newsList) {
          List<String> row = new ArrayList<String>();
          row.add(k);
          row.add(String.valueOf(score));
          row.add(newsTitle);
          this.data.add(row);
        }
      }
      this.write();
    } catch(Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
