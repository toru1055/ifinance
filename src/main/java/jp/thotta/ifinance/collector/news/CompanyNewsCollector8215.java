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
 * 企業名：【8215】銀座山形屋
 * @author toru1055
 */
public class CompanyNewsCollector8215
  extends BaseCompanyNewsCollector
  implements CompanyNewsCollector {
  private static final int stockId = 8215;
  private static final String IR_URL = "";
  private static final String PR_URL = "http://www.ginyama.co.jp/hc/";
  private static final String SHOP_URL = "http://www.ginyama.co.jp/shops/";
  private static final String PUBLICITY_URL = "";

  @Override
  public void parsePRList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    Document doc = Scraper.getHtml(PR_URL);
    Elements elements = doc.select("ul.list_news > li");
    for(Element elem : elements) {
      String aTxt = elem.select("span").first().text();
      MyDate aDate = MyDate.parseYmd(aTxt,
          new SimpleDateFormat("yyyy/MM/dd"));
      Element anchor = elem.select("a").first();
      String title = elem.select("a").text();
      String url = PR_URL + "#" + aDate.toString();
      if(anchor != null) {
        url = anchor.attr("abs:href");
        title = anchor.text();
      }
      CompanyNews news = new CompanyNews(stockId, url, aDate);
      news.title = title;
      news.createdDate = MyDate.getToday();
      news.type = CompanyNews.NEWS_TYPE_PRESS_RELEASE;
      if(news.hasEnough()
          && news.announcementDate.compareTo(MyDate.getPast(30)) > 0) {
        newsList.add(news);
      }
    }
  }

  @Override
  public void parseShopList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    Document doc = Scraper.getHtml(SHOP_URL);
    Elements elements = doc.select("dl.detail");
    int shopNumber = elements.size();
    MyDate aDate = new MyDate(2100, 1, 1);
    String title = "銀座山形屋の店舗数が【" + shopNumber + "】になりました";
    String url = SHOP_URL + "#shopNum/" + shopNumber;
    CompanyNews news = new CompanyNews(stockId, url, aDate);
    news.title = title;
    news.createdDate = MyDate.getToday();
    news.type = CompanyNews.NEWS_TYPE_SHOP_OPEN;
    if(news.hasEnough()
        && news.announcementDate.compareTo(MyDate.getPast(30)) > 0) {
      newsList.add(news);
    }
  }

}
