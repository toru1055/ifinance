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
 * 企業名：【8274】東武ストア
 * @author toru1055
 */
public class CompanyNewsCollector8274
  extends BaseCompanyNewsCollector
  implements CompanyNewsCollector {
  private static final int stockId = 8274;
  private static final String IR_URL = "";
  private static final String PR_URL = "http://www.tobustore.co.jp/";
  private static final String SHOP_URL = "";
  private static final String PUBLICITY_URL = "";

  @Override
  public void parsePRList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    String targetUrl = PR_URL;
    int type = CompanyNews.NEWS_TYPE_PRESS_RELEASE;
    Document doc = Scraper.getHtml(targetUrl);
    Elements dtList = doc.select("#sb-site > div.container > div.main_area > div.section.info_area > dl > dt");
    Elements ddList = doc.select("#sb-site > div.container > div.main_area > div.section.info_area > dl > dd");
    for(int i = 0; i < dtList.size(); i++) {
      Element dt = dtList.get(i);
      Element dd = ddList.get(i);
      String aTxt = dt.ownText();
      MyDate aDate = MyDate.parseYmd(aTxt,
          new SimpleDateFormat("yyyy/MM/dd"));
      Element anchor = dd.select("a").first();
      String title = dd.select("a").text();
      String url = targetUrl + "#" + aDate.toString();
      if(anchor != null) {
        url = anchor.attr("abs:href");
        title = anchor.text();
      }
      CompanyNews news = new CompanyNews(stockId, url, aDate);
      news.title = anchor.text();
      news.createdDate = MyDate.getToday();
      news.type = type;
      if(news.hasEnough() &&
          news.announcementDate.compareTo(MyDate.getPast(90)) > 0) {
        newsList.add(news);
      }
    }
  }

}
