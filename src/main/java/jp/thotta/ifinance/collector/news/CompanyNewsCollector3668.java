package jp.thotta.ifinance.collector.news;

import java.util.List;
import java.util.ArrayList;
import java.text.SimpleDateFormat;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

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
 * 企業名：【3668】コロプラ
 * @author toru1055
 */
public class CompanyNewsCollector3668
  extends BaseCompanyNewsCollector
  implements CompanyNewsCollector {
  private static final int stockId = 3668;
  private static final String PR_URL = "http://colopl.co.jp/news/";
  private static final String IR_URL = "http://colopl.co.jp/ir/";
  private static final String APP_URL = "http://colopl.co.jp/ir/appdls/";

  @Override
  public void parsePRList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    Document doc = Scraper.get(PR_URL);
    if(doc == null) {
      throw new FailToScrapeException("url: " + PR_URL);
    }
    Elements elements = doc.select("div.unitNewsItem > ul > li");
    for(Element elem : elements) {
      Element anchor = elem.select("a").first();
      String url = anchor.attr("abs:href");
      String aDateText = anchor.select("span.date").first().text();
      MyDate announcementDate = MyDate.parseYmd(aDateText, new SimpleDateFormat("yyyy.MM.dd"));
      CompanyNews news = new CompanyNews(stockId, url, announcementDate);
      news.title = anchor.select("span.txt").first().text();
      news.type = CompanyNews.NEWS_TYPE_PRESS_RELEASE;
      news.createdDate = MyDate.getToday();
      if(!news.hasEnough()) {
        throw new ParseNewsPageException(news.toString());
      }
      newsList.add(news);
    }
  }

  @Override
  public void parseAppList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    Document doc = Scraper.get(APP_URL);
    if(doc == null) {
      throw new FailToScrapeException("url: " + APP_URL);
    }
    Element tab1 = doc.select("div#tab1").first();
    Elements appDivs = tab1.select("div.overflow > div.tableSet");
    for(Element app : appDivs) {
      CompanyNews news = parseAppTab(app);
      if(!news.hasEnough()) {
        throw new ParseNewsPageException(news.toString());
      }
      news.title += "万ダウンロード達成";
      newsList.add(news);
    }
    Element tab2 = doc.select("div#tab2").first();
    appDivs = tab2.select("div.overflow > div.tableSet");
    for(Element app : appDivs) {
      CompanyNews news = parseAppTab(app);
      if(!news.hasEnough()) {
        throw new ParseNewsPageException(news.toString());
      }
      news.title += "万利用者達成";
      newsList.add(news);
    }
  }

  private CompanyNews parseAppTab(Element app) {
      String url = app.select("p.link > a").first().attr("abs:href");
      String appTitle = app.select("h3 > span").text();
      String appLanguage = app.select("p.language").text();
      Element nowDl = app.select("div.dlShiftArea > div.nowDl").first();
      String dlTxt = nowDl.ownText();
      String dateTxt = nowDl.select("span.dateTxt").first().text();
      MyDate aDate = MyDate.parseYmd(dateTxt, new SimpleDateFormat("（達成日：yyyy.MM.dd）"));
      CompanyNews news = new CompanyNews(stockId, url, aDate);
      news.title = appLanguage + appTitle + ": " + dlTxt;
      news.createdDate = MyDate.getToday();
      news.type = CompanyNews.NEWS_TYPE_APP_DOWNLOAD;
      return news;
  }
}
