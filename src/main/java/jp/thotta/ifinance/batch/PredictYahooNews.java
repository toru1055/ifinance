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
 * Yahooニュースの銘柄予測.
 */
public class PredictYahooNews extends BasePredictNewsCategory {

  static final String BASE_URL = "http://news.yahoo.co.jp/hl?c=biz&d=";

  public PredictYahooNews(Connection conn) {
    super(conn);
  }

  @Override
  public void setTodayNewsUrls()
    throws FailToScrapeException, ParseNewsPageException {
    String url = BASE_URL + MyDate.getToday().toFormat("%4d%02d%02d");
    Document doc = Scraper.getHtml(url);
    Elements list = doc.select("ul.listBd > li > p.ttl > a");
    for(Element li : list) {
      BatchNews bn = new BatchNews();
      bn.url = li.attr("abs:href");
      bn.title = li.text();
      batchNewsMap.put(bn.url, bn);
    }
  }

  public static void main(String[] args) {
    try {
      Connection c = Database.getConnection();
      PredictYahooNews pnn = new PredictYahooNews(c);
      pnn.execPredict();
      pnn.insertDatabase();
    } catch(Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

}
