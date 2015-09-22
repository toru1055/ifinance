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
 * 企業名：【3774】インターネットイニシアティブ
 * @author toru1055
 */
public class CompanyNewsCollector3774
  extends BaseCompanyNewsCollector
  implements CompanyNewsCollector {
  private static final int stockId = 3774;
  private static final String IR_URL = "http://www.iij.ad.jp/rss-temp/ir/index.xml";
  private static final String PR_URL = "http://www.iij.ad.jp/rss-temp/press/index.xml";
  private static final String DEV_URL = "http://www.iij.ad.jp/rss-temp/devinfo/index.xml";
  private static final String INFO_URL = "http://www.iij.ad.jp/rss-temp/info/index.xml";

  @Override
  public void parsePRList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    parseXml(newsList, stockId, IR_URL,
        CompanyNews.NEWS_TYPE_INVESTOR_RELATIONS);
    parseXml(newsList, stockId, PR_URL,
        CompanyNews.NEWS_TYPE_PRESS_RELEASE);
    parseXml(newsList, stockId, DEV_URL,
        CompanyNews.NEWS_TYPE_DEVELOPMENT);
    parseXml(newsList, stockId, INFO_URL,
        CompanyNews.NEWS_TYPE_INFORMATION);
  }

}
