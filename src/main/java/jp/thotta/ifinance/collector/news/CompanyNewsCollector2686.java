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
 * 企業名：【2686】ジーフット
 * @author toru1055
 */
public class CompanyNewsCollector2686
  extends BaseCompanyNewsCollector
  implements CompanyNewsCollector {
  private static final int stockId = 2686;
  private static final String IR_URL = "http://www.g-foot.co.jp/corporate/ir/monthly/";
  private static final String PR_URL = "http://www.g-foot.co.jp/";
  private static final String SHOP_URL = "http://www.g-foot.co.jp/";
  private static final String PUBLICITY_URL = "";

  @Override
  public void parseIRList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    Document doc = Scraper.getHtml(IR_URL);
    Elements elements = doc.select("table.ir_table01 > tbody > tr");
    for(Element elem : elements) {
      String aTxt = elem.select("th > p").first().text();
      MyDate aDate = MyDate.parseYmd(aTxt,
          new SimpleDateFormat("yyyy.MM.dd"));
      Element anchor = elem.select("td.text03 > a").first();
      String title = elem.select("td > div.text02").text();
      String url = IR_URL + "#" + aDate.toString();
      if(anchor != null) {
        url = anchor.attr("abs:href");
        //title = anchor.text();
      }
      CompanyNews news = new CompanyNews(stockId, url, aDate);
      news.title = title;
      news.createdDate = MyDate.getToday();
      news.type = CompanyNews.NEWS_TYPE_INVESTOR_RELATIONS;
      if(news.hasEnough()
          && news.announcementDate.compareTo(MyDate.getPast(60)) > 0) {
        newsList.add(news);
      }
    }
  }

  @Override
  public void parsePRList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    Document doc = Scraper.getHtml(PR_URL);
    Elements elements = doc.select("table.index_04").first().select("tbody > tr > td");
    for(Element elem : elements) {
      Element dt = elem.select("p.f9.mT10").first();
      if(dt == null) { continue; }
      String aTxt = elem.select("p.f9.mT10").first().text();
      MyDate aDate = MyDate.parseYmd(aTxt,
          new SimpleDateFormat("[ yyyy年MM月dd日 ]"));
      Element anchor = elem.select("p.arrow3 > a").first();
      String title = elem.select("p.arrow3").text();
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
          && news.announcementDate.compareTo(MyDate.getPast(90)) > 0) {
        newsList.add(news);
      }
    }
  }

  @Override
  public void parseShopList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    Document doc = Scraper.getHtml(SHOP_URL);
    Elements elements = doc.select("table.index_04").last().select("tbody > tr > td");
    for(Element elem : elements) {
      Element dt = elem.select("p.f9.mT10").first();
      if(dt == null) { continue; }
      String aTxt = elem.select("p.f9.mT10").first().text();
      MyDate aDate = MyDate.parseYmd(aTxt,
          new SimpleDateFormat("[ yyyy年MM月dd日 ]"));
      Element anchor = elem.select("p.arrow3 > a").first();
      String title = elem.select("p.arrow3").text();
      String url = SHOP_URL + "#" + aDate.toString();
      if(anchor != null) {
        url = anchor.attr("abs:href");
        title = anchor.text();
      }
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

}
