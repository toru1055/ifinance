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
 * 企業名：【2191】テラ
 * @author toru1055
 */
public class CompanyNewsCollector2191
  extends BaseCompanyNewsCollector
  implements CompanyNewsCollector {
  private static final int stockId = 2191;
  private static final String IR_URL = "https://www.tella.jp/ir/rss/news_rss.xml";
  private static final String PR_URL = "https://www.tella.jp/company/release/category/%E3%83%97%E3%83%AC%E3%82%B9%E3%83%AA%E3%83%AA%E3%83%BC%E3%82%B9/";
  private static final String SHOP_URL = "";
  private static final String PUBLICITY_URL = "https://www.tella.jp/company/release/category/%E3%81%8A%E7%9F%A5%E3%82%89%E3%81%9B/";

  @Override
  public void parseIRList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    parseXml(newsList, stockId, IR_URL,
        CompanyNews.NEWS_TYPE_INVESTOR_RELATIONS);
  }

}
