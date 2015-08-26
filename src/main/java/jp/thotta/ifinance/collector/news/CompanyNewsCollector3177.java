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

import jp.thotta.ifinance.collector.CompanyNewsCollector;
import jp.thotta.ifinance.collector.BaseCompanyNewsCollector;
import jp.thotta.ifinance.model.CompanyNews;
import jp.thotta.ifinance.common.MyDate;
import jp.thotta.ifinance.common.Scraper;
import jp.thotta.ifinance.common.FailToScrapeException;
import jp.thotta.ifinance.common.ParseNewsPageException;

/**
 * 個別企業のニュースコレクター.
 * 企業名：【3177】ありがとうサービス
 * @author toru1055
 */
public class CompanyNewsCollector3177
  extends BaseCompanyNewsCollector
  implements CompanyNewsCollector {
  private static final int stockId = 3177;
  private static final String IR_URL = "http://www.arigatou-s.com/ir/";
  private static final String PR_URL = "http://www.arigatou-s.com/category/info/";
  private static final String PUBLICITY_URL = "http://www.arigatou-s.com/category/media/";
  private static final String SHOP_URL = "http://www.arigatou-s.com/store/";

  @Override
  public void parseIRList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    Document doc = Scraper.getJs(IR_URL);
    Elements elements = doc.select("div#xj-mainlist > dl.news");
    for(Element elem : elements) {
      String aTxt = elem.select("dt").first().text();
      MyDate aDate = MyDate.parseYmd(aTxt,
          new SimpleDateFormat("yyyy年MM月dd日"));
      Element anchor = elem.select("dd.txt > a").first();
      String url = anchor.attr("abs:href");
      CompanyNews news = new CompanyNews(stockId, url, aDate);
      news.title = anchor.text();
      news.createdDate = MyDate.getToday();
      news.type = CompanyNews.NEWS_TYPE_INVESTOR_RELATIONS;
      if(news.hasEnough() &&
          news.announcementDate.compareTo(MyDate.getPast(90)) > 0) {
        newsList.add(news);
      }
    }
  }

  @Override
  public void parsePublicityList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    Document doc = Scraper.getHtml(PUBLICITY_URL);
    Elements elements = doc.select("div.post > article.list > header > p");
    for(Element elem : elements) {
      String aTxt = elem.select("time").first().text();
      MyDate aDate = MyDate.parseYmd(aTxt);
      Element anchor = elem.select("a").first();
      String url = anchor.attr("abs:href");
      CompanyNews news = new CompanyNews(stockId, url, aDate);
      news.title = anchor.text();
      news.createdDate = MyDate.getToday();
      news.type = CompanyNews.NEWS_TYPE_PUBLICITY;
      if(news.hasEnough() &&
          news.announcementDate.compareTo(MyDate.getPast(90)) > 0) {
        newsList.add(news);
      }
    }
  }

  @Override
  public void parseShopList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    Document doc = Scraper.getHtml(SHOP_URL);
    Elements elements = doc.select("div.post > div.store01 > table.wp-table-reloaded > tbody > tr > td.column-1");
    Map<String, Integer> shopCounter = new HashMap<String, Integer>();
    for(Element elem : elements) {
      String shopType = elem.text();
      if(shopCounter.containsKey(shopType)) {
        Integer counter = shopCounter.get(shopType);
        counter += 1;
        shopCounter.put(shopType, counter);
      } else {
        shopCounter.put(shopType, 1);
      }
    }
    shopCounter.put("全店舗", elements.size());
    MyDate aDate = MyDate.parseYmd("2100/1/1"); //dummy
    for(String shopType : shopCounter.keySet()) {
      int num = shopCounter.get(shopType);
      String url = SHOP_URL + "#" + shopType + "/" + num;
      CompanyNews news = new CompanyNews(stockId, url, aDate);
      news.title = "「" + shopType + "」の店舗数が " + num + " になりました";
      news.createdDate = MyDate.getToday();
      news.type = CompanyNews.NEWS_TYPE_SHOP_OPEN;
      if(news.hasEnough() &&
          news.announcementDate.compareTo(MyDate.getPast(90)) > 0) {
        newsList.add(news);
      }
    }
  }

}
