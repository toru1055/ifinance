package jp.thotta.ifinance.collector;

import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.text.SimpleDateFormat;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import jp.thotta.ifinance.collector.news.*;
import jp.thotta.ifinance.model.CompanyNews;
import jp.thotta.ifinance.common.MyDate;
import jp.thotta.ifinance.common.Scraper;
import jp.thotta.ifinance.common.FailToScrapeException;
import jp.thotta.ifinance.common.ParseNewsPageException;

public abstract class BaseCompanyNewsCollector
  implements CompanyNewsCollector {

  public void appendDb(Connection conn)
    throws SQLException, FailToScrapeException, ParseNewsPageException {
    Statement st = conn.createStatement();
    List<CompanyNews> newsList = new ArrayList<CompanyNews>();
    append(newsList);
    for(CompanyNews news : newsList) {
      if(!news.exists(st)) {
        news.insert(st);
      }
    }
  }

  public void append(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);
    int newsOriginalSize = newsList.size();
    parsePRList(newsList);
    parseIRList(newsList);
    parseAppList(newsList);
    parseShopList(newsList);
    parsePublicityList(newsList);
    if(newsList.size() == newsOriginalSize) {
      throw new ParseNewsPageException("No news: " + getClass().getSimpleName());
    }
  }

  public void parsePRList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
  }

  public void parseIRList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
  }

  public void parseAppList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
  }

  public void parseShopList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
  }

  public void parsePublicityList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
  }

  public void parseXjStorageId(List<CompanyNews> newsList,
                               int stockId,
                               String companyId,
                               int newsType)
    throws FailToScrapeException, ParseNewsPageException {
    String parseUrl = "http://www.xj-storage.jp/public-list/GetList.aspx?len=5&output=rss&company=" + companyId;
    parseXjStorageUrl(newsList, stockId, parseUrl, newsType);
  }

  public void parseXjStorageUrl(List<CompanyNews> newsList,
                             int stockId,
                             String parseUrl,
                             int newsType)
    throws FailToScrapeException, ParseNewsPageException {
    Document doc = Scraper.getXml(parseUrl);
    Elements elements = doc.select("item");
    for(Element elem : elements) {
      String aTxt = elem.select("dc|date").first().text();
      MyDate aDate = MyDate.parseYmd(aTxt,
          new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH));
      Element anchor = elem.select("link").first();
      String url = anchor.text();
      CompanyNews news = new CompanyNews(stockId, url, aDate);
      news.title = elem.select("title").text();
      news.createdDate = MyDate.getToday();
      news.type = newsType;
      if(news.hasEnough() &&
          news.announcementDate.compareTo(MyDate.getPast(90)) > 0) {
        newsList.add(news);
      }
    }
  }

  public void parseXml(List<CompanyNews> newsList,
                       int stockId,
                       String parseUrl,
                       int newsType)
    throws FailToScrapeException, ParseNewsPageException {
    Document doc = Scraper.getXml(parseUrl);
    Elements elements = doc.select("item");
    for(Element elem : elements) {
      String aTxt = elem.select("pubDate").first().text();
      MyDate aDate = MyDate.parseYmd(aTxt,
          new SimpleDateFormat("EEE, dd MMM yyyy", Locale.ENGLISH));
      Element anchor = elem.select("link").first();
      String url = anchor.text();
      CompanyNews news = new CompanyNews(stockId, url, aDate);
      news.title = elem.select("title").text();
      news.createdDate = MyDate.getToday();
      news.type = newsType;
      if(news.hasEnough() &&
          news.announcementDate.compareTo(MyDate.getPast(90)) > 0) {
        newsList.add(news);
      }
    }
  }


  public static List<CompanyNewsCollector> getAllCollectors() {
    List<CompanyNewsCollector> collectors = new ArrayList<CompanyNewsCollector>();
    collectors.add(new CompanyNewsCollector4689());
    collectors.add(new CompanyNewsCollector3668());
    collectors.add(new CompanyNewsCollector2705());
    collectors.add(new CompanyNewsCollector3093());
    collectors.add(new CompanyNewsCollector3395());
    collectors.add(new CompanyNewsCollector3091());
    collectors.add(new CompanyNewsCollector9853());
    collectors.add(new CompanyNewsCollector2780());
    collectors.add(new CompanyNewsCollector3181());
    collectors.add(new CompanyNewsCollector2735());
    collectors.add(new CompanyNewsCollector7647());
    collectors.add(new CompanyNewsCollector3094());
    collectors.add(new CompanyNewsCollector2698());
    collectors.add(new CompanyNewsCollector2674());
    collectors.add(new CompanyNewsCollector9927());
    collectors.add(new CompanyNewsCollector3021());
    collectors.add(new CompanyNewsCollector3177());
    collectors.add(new CompanyNewsCollector7610());
    collectors.add(new CompanyNewsCollector3313());
    collectors.add(new CompanyNewsCollector6076());
    collectors.add(new CompanyNewsCollector8273());
    collectors.add(new CompanyNewsCollector7611());
    collectors.add(new CompanyNewsCollector9990());
    collectors.add(new CompanyNewsCollector3169());
    collectors.add(new CompanyNewsCollector3133());
    collectors.add(new CompanyNewsCollector3329());
    collectors.add(new CompanyNewsCollector9899());
    collectors.add(new CompanyNewsCollector3175());
    collectors.add(new CompanyNewsCollector9842());
    collectors.add(new CompanyNewsCollector3077());
    collectors.add(new CompanyNewsCollector3082());
    collectors.add(new CompanyNewsCollector3224());
    collectors.add(new CompanyNewsCollector3221());
    collectors.add(new CompanyNewsCollector7412());
    collectors.add(new CompanyNewsCollector3366());
    collectors.add(new CompanyNewsCollector9994());
    collectors.add(new CompanyNewsCollector2686());
    collectors.add(new CompanyNewsCollector9640());
    collectors.add(new CompanyNewsCollector3075());
    collectors.add(new CompanyNewsCollector8230());
    collectors.add(new CompanyNewsCollector2753());
    collectors.add(new CompanyNewsCollector7618());
    collectors.add(new CompanyNewsCollector8167());
    collectors.add(new CompanyNewsCollector8252());
    collectors.add(new CompanyNewsCollector8218());
    collectors.add(new CompanyNewsCollector2659());
    collectors.add(new CompanyNewsCollector7606());
    collectors.add(new CompanyNewsCollector8008());
    collectors.add(new CompanyNewsCollector3050());
    collectors.add(new CompanyNewsCollector3372());
    collectors.add(new CompanyNewsCollector8215());
    collectors.add(new CompanyNewsCollector2675());
    collectors.add(new CompanyNewsCollector2373());
    collectors.add(new CompanyNewsCollector6082());
    collectors.add(new CompanyNewsCollector3223());
    collectors.add(new CompanyNewsCollector9835());
    collectors.add(new CompanyNewsCollector3032());
    collectors.add(new CompanyNewsCollector3346());
    collectors.add(new CompanyNewsCollector3317());
    collectors.add(new CompanyNewsCollector7577());
    collectors.add(new CompanyNewsCollector2789());
    collectors.add(new CompanyNewsCollector3318());
    collectors.add(new CompanyNewsCollector7571());
    collectors.add(new CompanyNewsCollector3147());
    collectors.add(new CompanyNewsCollector2796());
    collectors.add(new CompanyNewsCollector3134());
    collectors.add(new CompanyNewsCollector2662());
    collectors.add(new CompanyNewsCollector3067());
    collectors.add(new CompanyNewsCollector2786());
    collectors.add(new CompanyNewsCollector3370());
    collectors.add(new CompanyNewsCollector3136());
    collectors.add(new CompanyNewsCollector3083());
    collectors.add(new CompanyNewsCollector2138());
    collectors.add(new CompanyNewsCollector4565());
    collectors.add(new CompanyNewsCollector3845());
    collectors.add(new CompanyNewsCollector3664());
    collectors.add(new CompanyNewsCollector7415());
    collectors.add(new CompanyNewsCollector7623());
    collectors.add(new CompanyNewsCollector7462());
    collectors.add(new CompanyNewsCollector3358());
    return collectors;
  }

  public static List<CompanyNewsCollector> getTestCollectors() {
    List<CompanyNewsCollector> collectors = new ArrayList<CompanyNewsCollector>();
    collectors.add(new CompanyNewsCollector3358());
    return collectors;
  }

}
