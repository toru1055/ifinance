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
public class PredictNikkeiNews {

  public class BatchNews {
    public String url;
    public String title;
    public String description;
    public String stockId;
  }

  static final String BASE_URL = "http://www.nikkei.com/news/category/company/?bn=";
  static final int modelId = 5;
  static final String parserType = "ma";
  static final String labelMode = "multi";
  static final String host = "localhost";

  Map<String, BatchNews> nikkeiMap;
  OmlClient pr_client;
  Connection conn;

  public PredictNikkeiNews(Connection conn) {
    this.nikkeiMap = new HashMap<String, BatchNews>();
    this.conn = conn;
    try {
      this.setPredictData();
    } catch(Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public Set<String> keySet() {
    return nikkeiMap.keySet();
  }

  public BatchNews get(String url) {
    return nikkeiMap.get(url);
  }

  public void setTodayNewsUrls()
    throws FailToScrapeException, ParseNewsPageException {
    boolean finishFlag = false;
    for(int i = 2; i <= 202; i += 20) {
      String url = BASE_URL + i;
      Document doc = Scraper.getHtml(url);
      Elements list = doc.select("h4.cmn-article_title");
      for(Element li : list) {
        Element a = li.select("a").first();
        Element span = li.select("span.cmnc-time").first();
        if(span != null && a != null) {
          String spanText = span.text();
          if(spanText.contains("日")) {
            finishFlag = true;
          }
        } else {
          finishFlag = true;
        }
        if(finishFlag) { break; }
        String newsUrl = a.attr("abs:href");
        String newsTitle = a.text();
        BatchNews bn = new BatchNews();
        bn.url = newsUrl;
        bn.title = newsTitle;
        nikkeiMap.put(newsUrl, bn);
      }
      if(finishFlag) { break; }
    }
  }

  public void setPredictData()
    throws FailToScrapeException, ParseNewsPageException {
    setTodayNewsUrls();
    //for(String newsUrl : nikkeiMap.keySet()) {
    //  Document doc = Scraper.getHtml(newsUrl);
    //  String newsDesc = doc.select("div.cmn-article_text > p").text();
    //  nikkeiMap.get(newsUrl).description = newsDesc;
    //}
  }

  public void execPredict() {
    try {
      pr_client = OmlClient.createPredictBatchConnection(host);
      if(pr_client.configure(modelId, parserType, labelMode)) {
        for(String newsUrl : nikkeiMap.keySet()) {
          String newsTitle = nikkeiMap.get(newsUrl).title;
          //String newsDesc = nikkeiMap.get(newsUrl).description;
          //String newsDocument = newsTitle + " " + newsDesc;
          //newsDocument = newsDocument.replaceAll("\\s", " ");
          //newsDocument = newsDocument.substring(0, 
          //      (int)Math.min(500, newsDocument.length()));
          //String label = pr_client.predict(newsDocument);
          String label = pr_client.predict(newsTitle);
          MultiClassLabel ml = new MultiClassLabel();
          ml.parse(label);
          nikkeiMap.get(newsUrl).stockId = ml.getLabel();
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
    for(String newsUrl : nikkeiMap.keySet()) {
      BatchNews bn = nikkeiMap.get(newsUrl);
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

  public static void main(String[] args) {
    try {
      Connection c = Database.getConnection();
      PredictNikkeiNews pnn = new PredictNikkeiNews(c);
      pnn.execPredict();
      pnn.insertDatabase();
    } catch(Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

}
