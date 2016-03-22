package jp.thotta.ifinance.batch;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.text.SimpleDateFormat;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import jp.thotta.ifinance.common.FailToScrapeException;
import jp.thotta.ifinance.common.ParseNewsPageException;
import jp.thotta.ifinance.common.MyDate;
import jp.thotta.ifinance.common.Scraper;
import jp.thotta.ifinance.model.CompanyNews;
import jp.thotta.ifinance.model.Database;

import jp.thotta.oml.client.OmlClient;
import jp.thotta.oml.client.io.*;

/**
 * 日経新聞（企業）の予測.
 */
public abstract class BasePredictNewsCategory {

  public class BatchNews {
    public String url;
    public String title;
    public String description;
    public String stockId;
  }

  static final int modelId = 5;
  static final String parserType = "ma";
  static final String labelMode = "multi";
  static final String host = "localhost";

  Map<String, BatchNews> batchNewsMap;
  OmlClient pr_client;
  Connection conn;

  public BasePredictNewsCategory(Connection conn) {
    this.batchNewsMap = new HashMap<String, BatchNews>();
    this.conn = conn;
    try {
      this.setPredictData();
    } catch(Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public Set<String> keySet() {
    return batchNewsMap.keySet();
  }

  public BatchNews get(String url) {
    return batchNewsMap.get(url);
  }

  abstract public void setTodayNewsUrls()
    throws FailToScrapeException, ParseNewsPageException;

  public void setPredictData()
    throws FailToScrapeException, ParseNewsPageException {
    setTodayNewsUrls();
    //for(String newsUrl : batchNewsMap.keySet()) {
    //  Document doc = Scraper.getHtml(newsUrl);
    //  String newsDesc = doc.select("div.cmn-article_text > p").text();
    //  batchNewsMap.get(newsUrl).description = newsDesc;
    //}
  }

  public void execPredict() {
    try {
      pr_client = OmlClient.createPredictBatchConnection(host);
      if(pr_client.configure(modelId, parserType, labelMode)) {
        for(String newsUrl : batchNewsMap.keySet()) {
          String newsTitle = batchNewsMap.get(newsUrl).title;
          //String newsDesc = batchNewsMap.get(newsUrl).description;
          //String newsDocument = newsTitle + " " + newsDesc;
          //newsDocument = newsDocument.replaceAll("\\s", " ");
          //newsDocument = newsDocument.substring(0, 
          //      (int)Math.min(500, newsDocument.length()));
          //String label = pr_client.predict(newsDocument);
          String label = pr_client.predict(newsTitle);
          MultiClassLabel ml = new MultiClassLabel();
          ml.parse(label);
          batchNewsMap.get(newsUrl).stockId = ml.getLabel();
          if(ml.getLabel() != null) {
            System.out.println(ml.getLabel() + "\t" + newsTitle);
          }
        }
      }
    } catch(Exception e) {
      e.printStackTrace();
      System.exit(1);
    } finally {
      try {
        pr_client.close();
      } catch(Exception e) {
        e.printStackTrace();
        System.exit(1);
      }
    }
  }

  public void insertDatabase() throws SQLException {
    Statement st = conn.createStatement();
    for(String newsUrl : batchNewsMap.keySet()) {
      BatchNews bn = batchNewsMap.get(newsUrl);
      if(bn.stockId != null) {
        int stockId = Integer.parseInt(bn.stockId);
        CompanyNews news = new CompanyNews(stockId, bn.url, MyDate.getToday());
        news.title = bn.title;
        news.type = CompanyNews.NEWS_TYPE_NIKKEI;
        news.createdDate = MyDate.getToday();
        if(!news.exists(st)) {
          news.insert(st);
        }
      }
    }
  }

}
