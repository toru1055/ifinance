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
 * 企業名：【9853】銀座ルノアール
 * @author toru1055
 */
public class CompanyNewsCollector9853
  extends BaseCompanyNewsCollector
  implements CompanyNewsCollector {
  private static final int stockId = 9853;
  private static final String IR_URL = "http://www.ginza-renoir.co.jp/ir/";
  private static final String PR_URL = "http://www.ginza-renoir.co.jp/news/";

  @Override
  public void parseIRList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    Document doc = Scraper.getJs(IR_URL);
    Elements elements = doc.select("ul#news > li");
    for(Element elem : elements) {
      MyDate aDate = MyDate.getToday();
      Element anchor = elem.select("a").first();
      String url = anchor.attr("abs:href");
      CompanyNews news = new CompanyNews(stockId, url, aDate);
      news.title = anchor.text();
      news.announcementDate = MyDate.parseYmd(news.title, new SimpleDateFormat("yyyy年MM月dd日"));
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
    Elements elements = doc.select("ul.newslist.clear > li.news.clearfix");
    for(Element elem : elements) {
      MyDate aDate = MyDate.parseYmd(elem.select("span").text());
      Element anchor = elem.select("a").first();
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
}
