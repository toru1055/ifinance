package jp.thotta.ifinance.batch;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;

import jp.thotta.ifinance.model.Database;
import jp.thotta.ifinance.model.CompanyNews;
import jp.thotta.ifinance.common.MyDate;
import jp.thotta.ifinance.adhoc.TrainPriceIncreaseNewsRegression;
import jp.thotta.oml.client.OmlClient;
import jp.thotta.oml.client.io.Label;

public class PredictPriceIncreaseNikkei extends BaseRankingReport {
  static final int modelId = 4;
  static final String parserType = "ma";
  static final String labelMode = "score";
  static final String host = "localhost";

  OmlClient pr_client;

  public PredictPriceIncreaseNikkei(Connection c, String tmpl)
    throws SQLException, ParseException {
    super(c, tmpl, "WEBニュース値上り予想");
    try {
      pr_client = OmlClient.createPredictBatchConnection(host);
    } catch(Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  @Override
  protected void setCompanyNewsMap()
    throws SQLException, ParseException {
    this.cnMap = CompanyNews.selectMapByDate(this.conn, MyDate.getToday(), 0);
  }

  @Override
  protected Map<String, Double> estimatePriceIncreaseRatio() {
    Map<String, Double> scoreMap = new HashMap<String, Double>();
    try {
      if(pr_client.configure(modelId, parserType, labelMode)) {
        for(String k : cnMap.keySet()) {
          double score = 0.0;
          boolean nikkeiFlag = false;
          List<CompanyNews> cnList = cnMap.get(k);
          for(CompanyNews news : cnList) {
            if(news.type == CompanyNews.NEWS_TYPE_NIKKEI) {
              nikkeiFlag = true;
              Label label = pr_client.predictLabel(news.title);
              score += label.getScore() / cnList.size();
            }
          }
          if(nikkeiFlag) {
            scoreMap.put(k, score);
          }
        }
        return scoreMap;
      }
    } catch(Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
    return null;
  }

  public static void main(String[] args) {
    try {
      Connection c = Database.getConnection();
      String tmpl = "text";
      if(args.length >= 1) {
        tmpl = args[0];
      }
      PredictPriceIncreaseNikkei predictor =
        new PredictPriceIncreaseNikkei(c, tmpl);
      predictor.report();
    } catch(Exception e) {
      e.printStackTrace();
    } finally {
      try {
        Database.closeConnection();
      } catch(Exception e) {
        e.printStackTrace();
        System.exit(1);
      }
    }
  }
}

