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
  private static final String APP_URL = "http://colopl.co.jp/ir/library/appdls.html";

  @Override
  public void parsePRList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    Document doc = Scraper.getHtml(PR_URL);
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
      if(news.hasEnough()) {
        newsList.add(news);
      }
    }
  }

  @Override
  public void parseAppList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    Document doc = Scraper.getHtml(APP_URL);
    Elements appDivs = doc.select("#tab1 > ul.applist > li");
    for(Element app : appDivs) {
      CompanyNews news = parseAppTab(app);
      if(!news.hasEnough()) {
        throw new ParseNewsPageException(news.toString());
      }
      news.title += "万ダウンロード達成";
      newsList.add(news);
    }
    appDivs = doc.select("#tab2 > ul.applist > li");
    for(Element app : appDivs) {
      CompanyNews news = parseAppTab(app);
      news.title += "万利用者達成";
      if(news.hasEnough()) {
        newsList.add(news);
      }
    }
  }

  private CompanyNews parseAppTab(Element app) {
      String url = app.select("dl.app_icon > dd > a").first().attr("abs:href");
      String appTitle = app.select("dl.app_detail > dt").text();
      Element nowDl = app.select("dl.app_detail > dd > div > p.dlnum").first();
      String dlTxt = nowDl.text();
      String dateTxt = app.select("dl.app_detail > dd > div > p:nth-child(2)").first().text();
      MyDate aDate = MyDate.parseYmd(dateTxt, new SimpleDateFormat("(達成日：yyyy.MM.dd)"));
      CompanyNews news = new CompanyNews(stockId, url, aDate);
      news.title = appTitle + ": " + dlTxt;
      news.createdDate = MyDate.getToday();
      news.type = CompanyNews.NEWS_TYPE_APP_DOWNLOAD;
      return news;
  }
}
