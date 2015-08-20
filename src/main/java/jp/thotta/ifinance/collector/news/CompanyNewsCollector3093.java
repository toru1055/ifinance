package jp.thotta.ifinance.collector.news;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.text.SimpleDateFormat;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.DataNode;

import jp.thotta.ifinance.collector.CompanyNewsCollector;
import jp.thotta.ifinance.collector.BaseCompanyNewsCollector;
import jp.thotta.ifinance.model.CompanyNews;
import jp.thotta.ifinance.common.MyDate;
import jp.thotta.ifinance.common.Scraper;
import jp.thotta.ifinance.common.FailToScrapeException;
import jp.thotta.ifinance.common.ParseNewsPageException;

/**
 * 個別企業のニュースコレクター.
 * 企業名：【3093】トレジャー・ファクトリー
 * @author toru1055
 */
public class CompanyNewsCollector3093
  extends BaseCompanyNewsCollector
  implements CompanyNewsCollector {
  private static final int stockId = 3093;
  private static final String COMPANY_URL = "http://www.treasurefactory.co.jp/";
  private static final String PUBLICITY_URL = "http://www.treasurefactory.co.jp/compainfo/publicity.html";
  private static final String IR_PR_URL = "http://www.treasurefactory.co.jp/news/index.html";

  @Override
  public void parseIRList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    int newsOriginalSize = newsList.size();
    Document doc = Scraper.getJs(IR_PR_URL);
    Elements irElems = doc.select("div#irp-press-list > div.irp-item.ir");
    Elements prElems = doc.select("div#irp-press-list > div.irp-item.press");
    for(Element elem : irElems) {
      CompanyNews news = parseIrPrElement(elem);
      news.type = CompanyNews.NEWS_TYPE_INVESTOR_RELATIONS;
      if(!news.hasEnough()) {
        throw new ParseNewsPageException(news.toString());
      }
      newsList.add(news);
    }
    if(newsList.size() == newsOriginalSize) {
      throw new ParseNewsPageException("No news: " + PUBLICITY_URL);
    }
    newsOriginalSize = newsList.size();
    for(Element elem : prElems) {
      CompanyNews news = parseIrPrElement(elem);
      news.type = CompanyNews.NEWS_TYPE_PRESS_RELEASE;
      if(!news.hasEnough()) {
        throw new ParseNewsPageException(news.toString());
      }
      newsList.add(news);
    }
    if(newsList.size() == newsOriginalSize) {
      throw new ParseNewsPageException("No news: " + PUBLICITY_URL);
    }
  }

  private CompanyNews parseIrPrElement(Element elem) {
    MyDate aDate = MyDate.parseYmd(
        elem.select("dt").text(),
        new SimpleDateFormat("yyyy年MM月dd日"));
    Element anchor = elem.select("dd > span.irp-title > a").first();
    String url = anchor.attr("abs:href");
    CompanyNews news = new CompanyNews(stockId, url, aDate);
    news.title = anchor.text();
    news.createdDate = MyDate.getToday();
    return news;
  }

  @Override
  public void parsePublicityList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    Map<String, Integer> dateCounter = new HashMap<String, Integer>();
    int newsOriginalSize = newsList.size();
    Document doc = Scraper.getJs(PUBLICITY_URL);
    Elements newsElems = doc.select("div.irp-press-listS > div.news");
    for(Element newsElem : newsElems) {
      MyDate aDate = MyDate.parseYmd(
          newsElem.select("dt").text(),
          new SimpleDateFormat("yyyy年MM月dd日"));
      Integer counter = dateCounter.get(aDate.toString());
      if(counter == null) { counter = 0; }
      counter++;
      dateCounter.put(aDate.toString(), counter);
      String url = PUBLICITY_URL + "#" + aDate + "/" + counter;
      CompanyNews news = new CompanyNews(stockId, url, aDate);
      news.title = newsElem.select("dd > span.irp-title").text();
      news.createdDate = MyDate.getToday();
      news.type = CompanyNews.NEWS_TYPE_PUBLICITY;
      if(!news.hasEnough()) {
        throw new ParseNewsPageException(news.toString());
      }
      newsList.add(news);
    }
    if(newsList.size() == newsOriginalSize) {
      throw new ParseNewsPageException("No news: " + PUBLICITY_URL);
    }
  }
}
