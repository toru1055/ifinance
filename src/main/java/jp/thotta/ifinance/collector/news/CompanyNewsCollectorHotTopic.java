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
 * 話題の銘柄速報を収集する.
 * @author toru1055
 */
public class CompanyNewsCollectorHotTopic
  extends BaseCompanyNewsCollector
  implements CompanyNewsCollector {

  private static final String HOT_TOPIC_URL = "http://kabu-sokuhou.com/";

  @Override
  public void parseInfomation(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    Document doc = Scraper.getHtml(HOT_TOPIC_URL);
    Elements elements = doc.select("table > tbody > tr.code_list_home");
    for(Element elem : elements) {
      String stockCode = elem.select("td.code > div > a > span").text();
      int stockId = Integer.parseInt(stockCode);
      MyDate aDate = MyDate.getToday();
      String hh = MyDate.getCurrentHour();
      int rank = Integer.parseInt(elem.select("td.rank > span > i").text());
      String companyName = elem.select("td.name > a").text();
      String title = "「" + companyName + "」が" + hh + "時の話題ランキングで 【" + rank + "位】 になりました(恐るべき注目銘柄速報)";
      String url = String.format(
          "%s#%s/%s/%02d",
          HOT_TOPIC_URL, aDate.toString(), hh, rank);
      CompanyNews news = new CompanyNews(stockId, url, aDate);
      news.title = title;
      news.createdDate = MyDate.getToday();
      news.type = CompanyNews.NEWS_TYPE_HOT_TOPIC;
      if(news.hasEnough()
          && news.announcementDate.compareTo(MyDate.getPast(90)) > 0) {
        newsList.add(news);
      }
    }
  }

}
