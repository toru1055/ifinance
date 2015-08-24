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
 * 企業名：【2735】ワッツ
 * @author toru1055
 */
public class CompanyNewsCollector2735
  extends BaseCompanyNewsCollector
  implements CompanyNewsCollector {
  private static final int stockId = 2735;
  private static final String IR_URL = "http://www.watts-jp.com/company/news/";
  private static final String PR_URL = "http://www.watts-jp.com/";
  private static final String SHOP_URL = "http://www.watts-jp.com/";

  @Override
  public void parseIRList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    Document doc = Scraper.getHtml(IR_URL);
    Elements elements = doc.select("div.newsarchives > dl > dd > ul > li");
    for(Element elem : elements) {
      MyDate aDate = MyDate.parseYmd(elem.ownText(), new SimpleDateFormat("yyyy.MM.dd"));
      Element anchor = elem.select("a").first();
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

  @Override
  public void parseShopList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    Document doc = Scraper.getHtml(SHOP_URL);
    Element dl = doc.select("div#scroll01 > div.scrollInner > dl.news").first();
    Elements dtList = dl.select("dt");
    Elements anchorList = dl.select("dd > a");
    for(int i = 0; i < anchorList.size(); i++) {
      Element anchor = anchorList.get(i);
      Element dt = dtList.get(i);
      String url = anchor.attr("abs:href");
      MyDate aDate = MyDate.parseYmd(dt.text(),
          new SimpleDateFormat("yyyy.MM.dd"));
      CompanyNews news = new CompanyNews(stockId, url, aDate);
      news.title = anchor.text();
      news.createdDate = MyDate.getToday();
      news.type = CompanyNews.NEWS_TYPE_SHOP_OPEN;
      if(news.hasEnough() && aDate.compareTo(MyDate.getPast(90)) > 0) {
        newsList.add(news);
      }
    }
  }

  @Override
  public void parsePRList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    Document doc = Scraper.getHtml(PR_URL);
    Element dl = doc.select("div#scroll02 > div.scrollInner > dl.news").first();
    Elements dtList = dl.select("dt");
    Elements anchorList = dl.select("dd > a");
    for(int i = 0; i < anchorList.size(); i++) {
      Element anchor = anchorList.get(i);
      Element dt = dtList.get(i);
      String url = anchor.attr("abs:href");
      MyDate aDate = MyDate.parseYmd(dt.text(),
          new SimpleDateFormat("yyyy.MM.dd"));
      CompanyNews news = new CompanyNews(stockId, url, aDate);
      news.title = anchor.text();
      news.createdDate = MyDate.getToday();
      news.type = CompanyNews.NEWS_TYPE_PRESS_RELEASE;
      if(news.hasEnough() && aDate.compareTo(MyDate.getPast(90)) > 0) {
        newsList.add(news);
      }
    }
  }

}
