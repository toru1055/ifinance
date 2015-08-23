package jp.thotta.ifinance.collector.news;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
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
 * 企業名：【3181】買取王国
 * @author toru1055
 */
public class CompanyNewsCollector3181
  extends BaseCompanyNewsCollector
  implements CompanyNewsCollector {
  private static final int stockId = 3181;
  private static final String IR_URL = "http://www.okoku.jp/company/rss.xml";
  private static final String PR_URL = "http://www.okoku.jp/inc/news/";

  @Override
  public void parseIRList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    Document doc = Scraper.getXml(IR_URL);
    Elements elements = doc.select("item");
    for(Element elem : elements) {
      MyDate aDate = MyDate.parseYmd(elem.select("pubdate").text(), new SimpleDateFormat("EEE, dd MMM yyyy", Locale.US));
      String url = elem.select("link").text();
      CompanyNews news = new CompanyNews(stockId, url, aDate);
      news.title = elem.select("title").text();
      news.createdDate = MyDate.getToday();
      news.type = CompanyNews.NEWS_TYPE_INVESTOR_RELATIONS;
      if(news.hasEnough() && aDate.compareTo(MyDate.getPast(90)) > 0) {
        newsList.add(news);
      }
    }
  }

  @Override
  public void parsePRList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    Document doc = Scraper.getHtml(PR_URL);
    Elements elements = doc.select("div.news_list > ul > li div.news_cont");
    for(Element elem : elements) {
      MyDate aDate = MyDate.parseYmd(elem.select("div.news_desc > span.date").text(), new SimpleDateFormat("yyyy.MM.dd"));
      Element anchor = elem.select("div.news_detail.clearfix > a").first();
      String url = anchor.attr("abs:href");
      CompanyNews news = new CompanyNews(stockId, url, aDate);
      news.title = elem.select("span.title").text();
      news.createdDate = MyDate.getToday();
      news.type = CompanyNews.NEWS_TYPE_PRESS_RELEASE;
      if(news.hasEnough() && aDate.compareTo(MyDate.getPast(90)) > 0) {
        newsList.add(news);
      }
    }
  }
}
