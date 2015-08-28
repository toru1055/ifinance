package jp.thotta.ifinance.collector.news;

import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.text.SimpleDateFormat;

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
 * 企業名：【8273】イズミ
 * @author toru1055
 */
public class CompanyNewsCollector8273
  extends BaseCompanyNewsCollector
  implements CompanyNewsCollector {
  private static final int stockId = 8273;
  private static final String IR_URL = "http://www.izumi.co.jp/corp/ir/news.html";
  private static final String PR_URL = "http://www.izumi.co.jp/corp/outline/news_release/index.html";
  private static final String SHOP_URL = "";


  @Override
  public void parseIRList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    Document doc = Scraper.getHtml(IR_URL);
    Elements elements = doc.select("ul.news-release > li");
    for(Element elem : elements) {
      String aTxt = elem.select("span.date").first().text();
      MyDate aDate = MyDate.parseYmd(aTxt,
          new SimpleDateFormat("yyyy/MM/dd"));
      Element anchor = elem.select("a").first();
      String url;
      if(anchor == null) {
        url = IR_URL + "#" + aDate.toString();
      } else {
        url = anchor.attr("abs:href");
      }
      CompanyNews news = new CompanyNews(stockId, url, aDate);
      if(anchor == null) {
        news.title = elem.text();
      } else {
        news.title = anchor.text();
      }
      news.createdDate = MyDate.getToday();
      news.type = CompanyNews.NEWS_TYPE_INVESTOR_RELATIONS;
      if(news.hasEnough() &&
          news.announcementDate.compareTo(MyDate.getPast(90)) > 0) {
        newsList.add(news);
      }
    }
  }

  @Override
  public void parsePRList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    Document doc = Scraper.getHtml(PR_URL);
    Elements elements = doc.select("ul.news-release > li");
    for(Element elem : elements) {
      String aTxt = elem.select("span.date").first().text();
      MyDate aDate = MyDate.parseYmd(aTxt,
          new SimpleDateFormat("yyyy/MM/dd"));
      Element anchor = elem.select("a").first();
      String url;
      if(anchor == null) {
        url = PR_URL + "#" + aDate.toString();
      } else {
        url = anchor.attr("abs:href");
      }
      CompanyNews news = new CompanyNews(stockId, url, aDate);
      if(anchor == null) {
        news.title = elem.text();
      } else {
        news.title = anchor.text();
      }
      news.createdDate = MyDate.getToday();
      news.type = CompanyNews.NEWS_TYPE_PRESS_RELEASE;
      if(news.hasEnough() &&
          news.announcementDate.compareTo(MyDate.getPast(90)) > 0) {
        newsList.add(news);
          }
    }
    }

}
