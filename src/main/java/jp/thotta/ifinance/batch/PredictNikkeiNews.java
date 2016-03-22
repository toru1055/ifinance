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
public class PredictNikkeiNews extends BasePredictNewsCategory {

  static final String BASE_URL = "http://www.nikkei.com/news/category/company/?bn=";

  public PredictNikkeiNews(Connection conn) {
    super(conn);
  }

  @Override
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
        batchNewsMap.put(newsUrl, bn);
      }
      if(finishFlag) { break; }
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
