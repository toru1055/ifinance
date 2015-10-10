package jp.thotta.ifinance.collector.news;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import junit.framework.TestCase;
import java.sql.Connection;
import java.sql.SQLException;

import jp.thotta.ifinance.common.MyDate;
import jp.thotta.ifinance.model.CompanyNews;
import jp.thotta.ifinance.model.Database;
import jp.thotta.ifinance.collector.CompanyNewsCollector;
import jp.thotta.ifinance.collector.BaseCompanyNewsCollector;

public class CompanyNewsCollectorTest extends TestCase {
  Connection c;

  protected void setUp() {
    try {
      Database.setDbUrl("jdbc:sqlite:test.db");
      c = Database.getConnection();
    } catch(SQLException e) {
      e.printStackTrace();
    }
  }

  public void testCollectors() {
    List<CompanyNewsCollector> collectors =
      BaseCompanyNewsCollector.getTestCollectors();
    try {
      for(CompanyNewsCollector coll : collectors) {
        CompanyNews.dropTable(c);
        CompanyNews.createTable(c);
        coll.appendDb(c);
        List<CompanyNews> newsList =
          CompanyNews.selectByDate(c, MyDate.getToday());
        assertTrue(newsList.size() > 0);
        for(CompanyNews news : newsList) {
          System.out.println(news);
          assertTrue(news.hasEnough());
        }
      }
    } catch(Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public void testGetAllCollectorsMap() {
    Map<String, CompanyNewsCollector> colls =
      BaseCompanyNewsCollector.getStockCollectorMap();
    assertTrue(colls.get("4689") != null);
    assertTrue(colls.size() > 100);
  }

  protected void tearDown() {
    try {
      Database.closeConnection();
    } catch(SQLException e) {
      e.printStackTrace();
    }
  }
}
