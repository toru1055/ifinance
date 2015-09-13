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
 * 企業名：【___STOCK_ID___】___COMPANY_NAME___
 * @author toru1055
 */
public class CompanyNewsCollector___STOCK_ID___
  extends BaseCompanyNewsCollector
  implements CompanyNewsCollector {
  private static final int stockId = ___STOCK_ID___;
  private static final String IR_URL = "";
  private static final String PR_URL = "";
  private static final String SHOP_URL = "";
  private static final String PUBLICITY_URL = "";

  @Override
  public void parseIRList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    parseXml(newsList, stockId, IR_URL,
        CompanyNews.NEWS_TYPE_INVESTOR_RELATIONS);
  }

  @Override
  public void parsePRList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    parseXml(newsList, stockId, PR_URL,
        CompanyNews.NEWS_TYPE_PRESS_RELEASE);
  }

  @Override
  public void parseShopList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    parseXml(newsList, stockId, SHOP_URL,
        CompanyNews.NEWS_TYPE_SHOP_OPEN);
  }

  @Override
  public void parsePublicityList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    parseXml(newsList, stockId, PUBLICITY_URL,
        CompanyNews.NEWS_TYPE_PUBLICITY);
  }

}