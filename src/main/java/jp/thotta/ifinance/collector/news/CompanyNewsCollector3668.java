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
import jp.thotta.ifinance.common.FailToScrapeException;
import jp.thotta.ifinance.common.ParseNewsPageException;

/**
 * 個別企業のニュースコレクター.
 * 企業名：【3668】コロプラ
 * @author toru1055
 */
public class CompanyNewsCollector3668
  extends AbstractCompanyNewsCollector
  implements CompanyNewsCollector {
  private static final int stockId = 3668;
  private static final String PR_URL = "http://colopl.co.jp/news/";
  private static final String IR_URL = "http://colopl.co.jp/ir/";
  private static final String APP_URL = "http://colopl.co.jp/ir/appdls/";

  @Override
  public void parsePRList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    Document doc = Scraper.get(PR_URL);
    if(doc == null) {
      throw new FailToScrapeException("url: " + PR_URL);
    }
  }

  @Override
  public void parseIRList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
    Document doc = Scraper.get(IR_URL);
    if(doc == null) {
      throw new FailToScrapeException("url: " + PR_URL);
    }
  }

  @Override
  public void parseAppList(List<CompanyNews> newsList)
    throws FailToScrapeException, ParseNewsPageException {
  }
}
