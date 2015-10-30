package jp.thotta.ifinance.collector.news;

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

import jp.thotta.ifinance.collector.CompanyNewsCollector;
import jp.thotta.ifinance.collector.BaseCompanyNewsCollector;
import jp.thotta.ifinance.common.FailToScrapeException;
import jp.thotta.ifinance.common.ParseNewsPageException;
import jp.thotta.ifinance.model.CompanyNews;
import jp.thotta.ifinance.common.MyDate;
import jp.thotta.ifinance.common.Scraper;

/**
 * 株探：「朝刊」ニュース銘柄をScrape
 * @author toru1055
 */
public class CompanyNewsCollectorKabutan41
  extends BaseCompanyNewsCollector
  implements CompanyNewsCollector {
  private static final String BASE_URL = "http://kabutan.jp/warning/?mode=4_1";

  @Override
  public void parseIRList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    Document doc = Scraper.getHtml(BASE_URL);
    String aText = doc.select("#main > div.warning_contents_title-4 > h3").text();
    MyDate aDate = MyDate.parseYmd(aText, new SimpleDateFormat("MM月dd日"));
    MyDate today = MyDate.getToday();
    if(aDate.month == today.month) {
      aDate.year = today.year;
    } else if(aDate.month == 12 && today.month == 1) {
      aDate.year = today.year - 1;
    }
    Map<String, Boolean> pageUrlMap = new HashMap<String, Boolean>();
    Elements pageAnchors = doc.select("div.pagination > ul > li > a");
    for(Element pageAnchor : pageAnchors) {
      String pageUrl = pageAnchor.attr("abs:href");
      pageUrlMap.put(pageUrl, true);
    }
    pageUrlMap.put(BASE_URL, true);
    Map<Integer, Integer> stockCounter = new HashMap<Integer, Integer>();
    for(String pageUrl : pageUrlMap.keySet()) {
      Document d = Scraper.getHtml(pageUrl);
      Elements trList = d.select("#main > div.warning_contents > table > tbody > tr");
      for(Element tr : trList) {
        String stockIdStr = tr.select("td:nth-child(1) > a").first().text();
        String newsTitle = tr.select("td:nth-child(5) > a").first().text();
        int stockId = Integer.parseInt(stockIdStr);
        Integer counter = stockCounter.get(stockId);
        if(counter == null) {
          counter = 1;
        } else {
          counter += 1;
        }
        stockCounter.put(stockId, counter);
        String url = "http://kabutan.jp/stock/news?nmode=3&code=" + stockIdStr;
        if(counter > 1) { url += "#" + counter; }
        CompanyNews news = new CompanyNews(stockId, url, aDate);
        news.title = newsTitle;
        news.createdDate = MyDate.getToday();
        news.type = CompanyNews.NEWS_TYPE_INVESTOR_RELATIONS; //TODO: Type変える？
        if(news.hasEnough()) {
          newsList.add(news);
        }
      }
    }
  }

}
