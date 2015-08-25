package jp.thotta.ifinance.collector.news;

import java.util.List;
import java.util.ArrayList;
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
 * 企業名：【2674】ハードオフコーポレーション
 * @author toru1055
 */
public class CompanyNewsCollector2674
  extends BaseCompanyNewsCollector
  implements CompanyNewsCollector {
  private static final int stockId = 2674;
  private static final String IR_URL = "http://www.hardoff.co.jp/ir/";
  private static final String PR_URL = "http://www.hardoff.co.jp/news/";
  private static final String SHOP_URL = "http://www.hardoff.co.jp/shop/";

  /*
  @Override
  public void parseIRList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    Document doc = Scraper.getHtml(IR_URL);
    Elements elements = doc.select("table#xj-mainlist-News > tbody > tr");
    for(Element elem : elements) {
      String aTxt = elem.select("td > div").first().text();
      MyDate aDate = MyDate.parseYmd(aTxt);
      Element anchor = elem.select("td > a").first();
      String url = anchor.attr("abs:href");
      CompanyNews news = new CompanyNews(stockId, url, aDate);
      news.title = anchor.text();
      news.createdDate = MyDate.getToday();
      news.type = CompanyNews.NEWS_TYPE_INVESTOR_RELATIONS;
      if(news.hasEnough() && aDate.compareTo(MyDate.getPast(90)) > 0) {
        newsList.add(news);
      }
    }
  }
  */

  @Override
  public void parsePRList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    Document doc = Scraper.getHtml(PR_URL, 10000);
    Elements elements = doc.select("div.newsContentArea > ul > li");
    for(Element elem : elements) {
      String aTxt = elem.select("p.date").first().text();
      MyDate aDate = MyDate.parseYmd(aTxt, new SimpleDateFormat("yyyy年MM月dd日"));
      Element anchor = elem.select("p.news > a").first();
      String url = anchor.attr("abs:href");
      CompanyNews news = new CompanyNews(stockId, url, aDate);
      news.title = anchor.text();
      news.createdDate = MyDate.getToday();
      news.type = CompanyNews.NEWS_TYPE_PRESS_RELEASE;
      if(news.hasEnough() && aDate.compareTo(MyDate.getPast(90)) > 0) {
        newsList.add(news);
      }
    }
  }

  @Override
  public void parseShopList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    Document doc = Scraper.getHtml(SHOP_URL, 10000);
    Elements elements = doc.select("div.inner > ul.newShopWrap > li.oneShop.clearfix");
    for(Element elem : elements) {
      MyDate aDate = MyDate.getToday();
      Element anchor = elem.select("div.info > p.ttl > a").first();
      String url = anchor.attr("abs:href");
      CompanyNews news = new CompanyNews(stockId, url, aDate);
      news.title = anchor.text();
      String desc = elem.select("div.info").text();
      String aTxt = desc.replaceAll(".*グランドオープン：", "");
      news.announcementDate = MyDate.parseYmd(aTxt, new SimpleDateFormat("yyyy年MM月dd日"));
      news.createdDate = MyDate.getToday();
      news.type = CompanyNews.NEWS_TYPE_SHOP_OPEN;
      if(news.hasEnough() && news.announcementDate.compareTo(MyDate.getPast(90)) > 0) {
        newsList.add(news);
      }
    }
  }

}
