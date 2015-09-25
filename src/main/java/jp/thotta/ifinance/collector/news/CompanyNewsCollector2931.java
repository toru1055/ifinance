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
 * 企業名：【2931】ユーグレナ
 * @author toru1055
 */
public class CompanyNewsCollector2931
  extends BaseCompanyNewsCollector
  implements CompanyNewsCollector {
  private static final int stockId = 2931;
  private static final String IR_URL = "http://v4.eir-parts.net/V4Public/EIR/2931/ja/announcement/announcement_5.xml";
  private static final String PR_URL = "http://euglena.jp/euglena_news.xml";
  private static final String SHOP_URL = "http://euglena.jp/euglena_info.xml";
  private static final String PUBLICITY_URL = "http://euglena.jp/euglena_media.xml";

  @Override
  public void parseIRList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    parseXml(newsList, stockId, IR_URL,
        CompanyNews.NEWS_TYPE_INVESTOR_RELATIONS);
  }

  @Override
  public void parsePRList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    parseXjStorageUrl(newsList, stockId, PR_URL,
        CompanyNews.NEWS_TYPE_PRESS_RELEASE);
  }

  @Override
  public void parseShopList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    parseXjStorageUrl(newsList, stockId, SHOP_URL,
        CompanyNews.NEWS_TYPE_INFORMATION);
  }

  @Override
  public void parsePublicityList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    parseXjStorageUrl(newsList, stockId, PUBLICITY_URL,
        CompanyNews.NEWS_TYPE_PUBLICITY);
  }

}
