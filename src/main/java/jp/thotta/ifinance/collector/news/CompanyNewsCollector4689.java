package jp.thotta.ifinance.collector.news;

import java.util.List;
import java.util.ArrayList;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import jp.thotta.ifinance.collector.CompanyNewsCollector;
import jp.thotta.ifinance.model.CompanyNews;
import jp.thotta.ifinance.common.MyDate;
import jp.thotta.ifinance.common.Scraper;

/**
 * 個別企業のニュースコレクター.
 * 企業名：【4689】ヤフー株式会社
 * @author toru1055
 */
public class CompanyNewsCollector4689
  extends AbstractCompanyNewsCollector
  implements CompanyNewsCollector {
  private static final int stockId = 4689;
  private static final String PR_URL = "http://pr.yahoo.co.jp/";
  private static final String IR_URL = "http://ir.yahoo.co.jp/";

  public void append(List<CompanyNews> newsList) {
    parsePRList(newsList);
    parseIRList(newsList);
  }

  public void parsePRList(List<CompanyNews> newsList) {
    Document doc = Scraper.get(PR_URL);
    if(doc == null) { return; }
    Elements elements = doc.select("section.contents");
    for(Element elem : elements) {
      Element anchor = elem.select("h2 > a").first();
      String url = anchor.attr("abs:href");
      String aDateText = elem.select("p.jannle > span").first().text();
      CompanyNews news = new CompanyNews(stockId, url);
      news.title = anchor.text();
      news.type = CompanyNews.NEWS_TYPE_PRESS_RELEASE;
      news.createdDate = MyDate.getToday();
      news.announcementDate = MyDate.parseYmdJapan(aDateText);
      newsList.add(news);
    }
  }

  public void parseIRList(List<CompanyNews> newsList) {
    Document doc = Scraper.get(IR_URL);
    if(doc == null) { return; }
    Elements ulList = doc.select("main > article > section > ul");
    Element ul = ulList.get(0); // IR最新情報
    for(Element elem : ul.select("li")) {
      Element anchor = elem.select("a").first();
      String url = anchor.attr("abs:href");
      CompanyNews news = new CompanyNews(stockId, url);
      news.title = anchor.text();
      news.type = CompanyNews.NEWS_TYPE_INVESTOR_RELATIONS;
      news.createdDate = MyDate.getToday();
      String aDateText = elem.ownText().replaceAll("[（）]", "");
      news.announcementDate = MyDate.parseYmd(aDateText);
      newsList.add(news);
    }
  }
}
