package jp.thotta.ifinance.batch;

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
import jp.thotta.ifinance.adhoc.TrainPriceIncreaseNewsRegression;
import jp.thotta.oml.client.OmlClient;
import jp.thotta.oml.client.io.Label;

/**
 * 株探：「朝刊」ニュース銘柄で値上り率を学習.
 */
public class TrainPriceIncreaseNews {
  static final String BASE_URL = "http://kabutan.jp/warning/?mode=4_1";
  Map<String, Double> scoreMap;
  Map<String, List<String>> newsMap;
  OmlClient tr_client;

  public TrainPriceIncreaseNews() {
    try {
      tr_client = OmlClient.createTrainBatchConnection(TrainPriceIncreaseNewsRegression.host);
    } catch(Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
    scoreMap = new HashMap<String, Double>();
    newsMap = new HashMap<String, List<String>>();
  }

  void setTrainData()
    throws FailToScrapeException, ParseNewsPageException {
    Document doc = Scraper.getHtml(BASE_URL);
    String aText = doc.select("div.meigara_count > ul > li:nth-child(1)").text();
    MyDate aDate = MyDate.parseYmd(aText, new SimpleDateFormat("yyyy年MM月dd日"));
    if(!MyDate.getToday().equals(aDate)) {
      System.out.println("Date is not match: pageDate=" + aDate + ", today=" + MyDate.getToday());
      return;
    }
    Map<String, Boolean> pageUrlMap = new HashMap<String, Boolean>();
    Elements pageAnchors = doc.select("div.pagination > ul > li > a");
    for(Element pageAnchor : pageAnchors) {
      String pageUrl = pageAnchor.attr("abs:href");
      pageUrlMap.put(pageUrl, true);
    }
    pageUrlMap.put(BASE_URL, true);
    for(String pageUrl : pageUrlMap.keySet()) {
      Document d = Scraper.getHtml(pageUrl);
      Elements trList = d.select("#main > div.warning_contents > table > tbody > tr");
      for(Element tr : trList) {
        String stockId= tr.select("td:nth-child(1) > a").first().text();
        String newsTitle = tr.select("td:nth-child(5) > a").first().text();
        Element scoreElem = tr.select("td:nth-child(9) > span").first();
        String scoreText = tr.select("td:nth-child(9)").first().text().replaceAll("%$", "");
        Double score = 0.0;
        if(!"－".equals(scoreText)) {
          score = Double.parseDouble(scoreText);
        }
        scoreMap.put(stockId, score);
        List<String> newsList = null;
        if(!newsMap.containsKey(stockId)) {
          newsList = new ArrayList<String>();
        } else {
          newsList = newsMap.get(stockId);
        }
        newsList.add(newsTitle);
        newsMap.put(stockId, newsList);
      }
    }
  }

  public void execTrain() {
    int modelId = TrainPriceIncreaseNewsRegression.modelId;
    String parserType = TrainPriceIncreaseNewsRegression.parserType;
    String labelMode = TrainPriceIncreaseNewsRegression.labelMode;
    try {
      setTrainData();
      if(tr_client.configure(modelId, parserType, labelMode)) {
        for(String k : newsMap.keySet()) {
          List<String> newsList = newsMap.get(k);
          Double score = scoreMap.get(k);
          for(String newsTitle : newsList) {
            System.out.println("Score = " + score + ", title = " + newsTitle);
            tr_client.train(String.valueOf(score), newsTitle);
          }
        }
      }
    } catch(Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public static void main(String[] args) {
    TrainPriceIncreaseNews train = new TrainPriceIncreaseNews();
    train.execTrain();
  }
}
