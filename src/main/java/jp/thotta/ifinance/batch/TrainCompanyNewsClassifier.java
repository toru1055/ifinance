package jp.thotta.ifinance.batch;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.text.SimpleDateFormat;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import jp.thotta.ifinance.common.FailToScrapeException;
import jp.thotta.ifinance.common.ParseNewsPageException;
import jp.thotta.ifinance.common.MyDate;
import jp.thotta.ifinance.common.Scraper;

import jp.thotta.oml.client.OmlClient;
import jp.thotta.oml.client.io.Label;

/**
 * 株探：「市場ニュース」の材料タブを１つずつクロール.
 */
public class TrainCompanyNewsClassifier {
  static final String BASE_LIST_URL =
    "http://kabutan.jp/news/marketnews/?&category=2&date=";
  static final int modelId = 5;
  static final String parserType = "ma";
  static final String labelMode = "multi";
  static final String host = "localhost";

  protected Map<String, String> stockIdNews;
  protected String topUrl;
  protected int past;
  protected OmlClient tr_client;
  protected OmlClient pr_client;

  public TrainCompanyNewsClassifier(int past) {
    this(past, BASE_LIST_URL);
  }

  public TrainCompanyNewsClassifier(int past, String baseUrl) {
    stockIdNews = new HashMap<String, String>();
    this.past = past;
    this.topUrl = baseUrl +
      MyDate.getPast(past).toFormat("%4d%02d%02d");
    try {
      setTrainData();
    } catch(Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public TrainCompanyNewsClassifier() {
    this(1);
  }

  public Set<String> keySet() {
    return stockIdNews.keySet();
  }

  public String get(String stockId) {
    return stockIdNews.get(stockId);
  }

  public int getPageSize()
    throws FailToScrapeException, ParseNewsPageException {
    int maxPageSize = 1;
    Document doc = Scraper.getHtml(topUrl);
    Elements pageAnchors = doc.select("div.pagination > ul > li > a");
    for(Element anchor : pageAnchors) {
      String pageUrl = anchor.attr("href");
      String pageNumber = pageUrl.replaceAll("^.*&page=", "");
      int pageSize = Integer.parseInt(pageNumber);
      if(pageSize > maxPageSize) {
        maxPageSize = pageSize;
      }
    }
    return maxPageSize;
  }

  public Map<String, String> getNewsUrlMap()
    throws FailToScrapeException, ParseNewsPageException {
    Map<String, String> newsUrlMap = new HashMap<String, String>();
    int pageSize = this.getPageSize();
    for(int p = 1; p <= pageSize; p++) {
      String pageUrl = topUrl + "&page=" + String.valueOf(p);
      Document doc = Scraper.getHtml(pageUrl);
      Elements newsAnchorList = doc.select("table.s_news_list > tbody > tr > td:nth-child(3) > a");
      for(Element newsAnchor : newsAnchorList) {
        String newsUrl = newsAnchor.attr("abs:href");
        String newsTitle = newsAnchor.text();
        newsUrlMap.put(newsUrl, newsTitle);
      }
    }
    return newsUrlMap;
  }

  public void setTrainData()
    throws FailToScrapeException, ParseNewsPageException {
    Map<String, String> newsUrlMap = this.getNewsUrlMap();
    for(String url : newsUrlMap.keySet()) {
      Document doc = Scraper.getHtml(url);
      Element p = doc.select("#shijyounews > p").first();
      Elements stockAnchors = p.select("a[href^=/stock/?code=]");
      String stockId = "";
      String newsDocument = "";
      if(stockAnchors != null && stockAnchors.size() == 1) {
        stockId = stockAnchors.text();
        //newsDocument = newsUrlMap.get(url);
        newsDocument = newsUrlMap.get(url) + " " + p.ownText();
        newsDocument = newsDocument.replaceAll("\\s", " ");
        newsDocument = newsDocument.substring(0, 
              (int)Math.min(500, newsDocument.length()));
        stockIdNews.put(stockId, newsDocument);
      }
    }
  }

  public void execTrain() {
    try {
      tr_client = OmlClient.createTrainBatchConnection(host);
      if(tr_client.configure(modelId, parserType, labelMode)) {
        for(String stockId : stockIdNews.keySet()) {
          String newsDocument = stockIdNews.get(stockId);
          tr_client.train(stockId, newsDocument);
        }
      }
    } catch(Exception e) {
      e.printStackTrace();
      System.exit(1);
    } finally {
      try {
        tr_client.close();
      } catch(Exception e) {
        e.printStackTrace();
        System.exit(1);
      }
    }
  }

  public static void main(String[] args) {
    int past = 1;
    if(args.length >= 1) {
      int p = Integer.parseInt(args[0]);
      if(p >= 0) { past = p; }
    }
    new TrainCompanyNewsClassifier(past).execTrain();
    new TrainYahooNewsClassifier(past).execTrain();
  }
}
