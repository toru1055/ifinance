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
 * 企業名：【3169】ミサワ
 * @author toru1055
 */
public class CompanyNewsCollector3169
  extends BaseCompanyNewsCollector
  implements CompanyNewsCollector {
  private static final int stockId = 3169;
  private static final String IR_URL = "http://v4.eir-parts.net/V4Public/EIR/3169/ja/announcement/announcement_7.xml";
  private static final String SHOP_URL = "http://unico-lifestyle.com/ShopInfo/";

  @Override
  public void parseIRList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    parseXml(newsList, stockId, IR_URL,
        CompanyNews.NEWS_TYPE_INVESTOR_RELATIONS);
  }

  @Override
  public void parseShopList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    Document doc = Scraper.getHtml(SHOP_URL);
    Elements shopList = doc.select("div.shopConWrap > ul.shopList > li");
    MyDate aDate = new MyDate(2100, 1, 1);
    for(Element shop : shopList) {
      Element anchor = shop.select("a").first();
      String url = anchor.attr("abs:href");
      String shopName = anchor.select("img").first().attr("alt");
      CompanyNews news = new CompanyNews(stockId, url, aDate);
      news.title = "「" + shopName + "」がオープンしています";
      news.createdDate = MyDate.getToday();
      news.type = CompanyNews.NEWS_TYPE_SHOP_OPEN;
      if(news.hasEnough() &&
          news.announcementDate.compareTo(MyDate.getPast(90)) > 0) {
        newsList.add(news);
      }
    }
    String url = SHOP_URL + "#shop-num/" + shopList.size();
    CompanyNews news = new CompanyNews(stockId, url, aDate);
    news.title = "現在の総店舗数は: " + shopList.size();
    news.createdDate = MyDate.getToday();
    news.type = CompanyNews.NEWS_TYPE_SHOP_OPEN;
    newsList.add(news);
  }

}
