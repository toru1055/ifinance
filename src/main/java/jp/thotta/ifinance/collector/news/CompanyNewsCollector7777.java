package jp.thotta.ifinance.collector.news;

import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.Locale;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import jp.thotta.ifinance.collector.CompanyNewsCollector;
import jp.thotta.ifinance.collector.BaseCompanyNewsCollector;
import jp.thotta.ifinance.model.CompanyNews;
import jp.thotta.ifinance.common.MyDate;
import jp.thotta.ifinance.common.Scraper;
import jp.thotta.ifinance.common.FailToScrapeException;
import jp.thotta.ifinance.common.ParseNewsPageException;

/**
 * 個別企業のニュースコレクター.
 * 企業名：【7777】スリー・ディー・マトリックス
 * @author toru1055
 */
public class CompanyNewsCollector7777
  extends BaseCompanyNewsCollector
  implements CompanyNewsCollector {
  private static final int stockId = 7777;
  private static final String IR_URL = "http://www.3d-matrix.co.jp/news/new2.html";
  private static final String PR_URL = "http://www.3d-matrix.co.jp/news/new1.html";
  private static final String SHOP_URL = "";
  private static final String PUBLICITY_URL = "";

  @Override
  public void parseIRList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    Document doc = Scraper.getHtml(IR_URL);
    Elements elements = doc.select("#page-1 > div.asset-content > div > dl > dd:nth-child(2) > table > tbody > tr");
    for(Element elem : elements) {
      String aTxt = elem.select("th").first().text();
      MyDate aDate = MyDate.parseYmd(aTxt,
          new SimpleDateFormat("yyyy年MM月dd日"));
      Element anchor = elem.select("td > a").first();
      String title = elem.select("td").text();
      String url = IR_URL + "#" + aDate.toString();
      if(anchor != null) {
        url = anchor.attr("abs:href");
      }
      CompanyNews news = new CompanyNews(stockId, url, aDate);
      news.title = title;
      news.createdDate = MyDate.getToday();
      news.type = CompanyNews.NEWS_TYPE_INVESTOR_RELATIONS;
      if(news.hasEnough()
          && news.announcementDate.compareTo(MyDate.getPast(90)) > 0) {
        newsList.add(news);
      }
    }
  }

  @Override
  public void parsePRList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    Document doc = Scraper.getHtml(PR_URL);
    Elements elements = doc.select("#page-1 > div.asset-content > div > div > dl > table > tbody > tr");
    for(Element elem : elements) {
      String aTxt = elem.select("th").first().text();
      MyDate aDate = MyDate.parseYmd(aTxt,
          new SimpleDateFormat("yyyy年MM月dd日"));
      Element anchor = elem.select("td > a").first();
      String title = elem.select("td").text();
      String url = PR_URL + "#" + aDate.toString();
      if(anchor != null) {
        url = anchor.attr("abs:href");
      }
      CompanyNews news = new CompanyNews(stockId, url, aDate);
      news.title = title;
      news.createdDate = MyDate.getToday();
      news.type = CompanyNews.NEWS_TYPE_PRESS_RELEASE;
      if(news.hasEnough()
          && news.announcementDate.compareTo(MyDate.getPast(90)) > 0) {
        newsList.add(news);
      }
    }
  }

}
