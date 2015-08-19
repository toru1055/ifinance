package jp.thotta.ifinance.collector;

import java.util.List;
import java.util.ArrayList;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

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
    parsePRList(newsList);
    parseIRList(newsList);
    parseAppList(newsList);
    parseShopList(newsList);
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

  public static List<CompanyNewsCollector> getAllCollectors() {
    List<CompanyNewsCollector> collectors = new ArrayList<CompanyNewsCollector>();
    collectors.add(new CompanyNewsCollector4689());
    collectors.add(new CompanyNewsCollector3668());
    collectors.add(new CompanyNewsCollector2705());
    return collectors;
  }
}
