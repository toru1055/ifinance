package jp.thotta.ifinance.collector.news;

import java.util.List;
import java.util.ArrayList;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import jp.thotta.ifinance.collector.CompanyNewsCollector;
import jp.thotta.ifinance.model.CompanyNews;
import jp.thotta.ifinance.common.MyDate;
import jp.thotta.ifinance.common.Scraper;

public abstract class AbstractCompanyNewsCollector implements CompanyNewsCollector {

  public void appendDb(Connection conn) throws SQLException {
    Statement st = conn.createStatement();
    List<CompanyNews> newsList = new ArrayList<CompanyNews>();
    append(newsList);
    for(CompanyNews news : newsList) {
      if(!news.exists(st)) {
        news.insert(st);
      }
    }
  }

  abstract public void append(List<CompanyNews> newsList);
}
