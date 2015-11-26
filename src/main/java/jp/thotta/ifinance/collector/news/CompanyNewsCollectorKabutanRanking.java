package jp.thotta.ifinance.collector.news;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import jp.thotta.ifinance.collector.CompanyNewsCollector;
import jp.thotta.ifinance.collector.BaseCompanyNewsCollector;
import jp.thotta.ifinance.common.FailToScrapeException;
import jp.thotta.ifinance.common.ParseNewsPageException;
import jp.thotta.ifinance.model.CompanyNews;
import jp.thotta.ifinance.common.MyDate;
import jp.thotta.ifinance.common.Scraper;

/**
 * 株探：週間ランキング【値上がり・値下がり率】で銘柄ニュースをScrape
 * @author toru1055
 */
public class CompanyNewsCollectorKabutanRanking
  extends BaseCompanyNewsCollector
  implements CompanyNewsCollector {
  private static final String NEWS_LIST_BASE_URL =
    "http://kabutan.jp/news/marketnews/?category=2&date=";

  @Override
  public void parseIRList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    if(MyDate.getCurrentHourInt() < 10) {
      return;
    }
    MyDate aDate = MyDate.getToday();
    String newsListUrl = NEWS_LIST_BASE_URL +
      aDate.toFormat("%4d%02d%02d");
    Document doc = Scraper.getHtml(newsListUrl);
    Elements newsAnchorList = doc.select("a[href*=\"/news/marketnews/?b=n\"]");
    for(Element newsAnchor : newsAnchorList) {
      String rankingUrl = newsAnchor.attr("abs:href");
      String rankingTitle = newsAnchor.text();
      if(rankingTitle.contains("週間ランキング【値上がり率】")) {
        appendNews(newsList, aDate, rankingUrl, "【値上り】");
      } else if(rankingTitle.contains("週間ランキング【値下がり率】")) {
        appendNews(newsList, aDate, rankingUrl, "【値下り】");
      } else if(rankingTitle.contains("本日の【ストップ高／ストップ安】 引け")) {
        appendNews(newsList, aDate, rankingUrl, "【S高/S安】");
      } else if(rankingTitle.contains("本日のランキング【寄付からの値上がり率】")) {
        appendNews(newsList, aDate, rankingUrl, "【値上り】");
      } else {
      }
    }
  }

  void appendNews(List<CompanyNews> newsList,
                  MyDate aDate,
                  String rankingUrl,
                  String prefix)
    throws FailToScrapeException {
    Document doc = Scraper.getHtml(rankingUrl);
    Elements anchorList = doc.select("a[href*=\"/stock/news?code=\"]");
    for(Element anchor : anchorList) {
      String newsUrl = anchor.attr("abs:href");
      String newsTitle = prefix + anchor.text();
      Pattern p = Pattern.compile("code=(\\d\\d\\d\\d)");
      Matcher m = p.matcher(newsUrl);
      if(m.find()) {
        int stockId = Integer.parseInt(m.group(1));
        CompanyNews news = new CompanyNews(stockId, newsUrl, aDate);
        news.title = newsTitle;
        news.createdDate = MyDate.getToday();
        news.type = CompanyNews.NEWS_TYPE_INVESTOR_RELATIONS;
        if(news.hasEnough()) {
          newsList.add(news);
        }
      }
    }
  }

}
