package jp.thotta.ifinance.collector.news;

import java.util.List;
import java.util.ArrayList;
import junit.framework.TestCase;
import java.sql.Connection;
import java.sql.SQLException;

import jp.thotta.ifinance.common.MyDate;
import jp.thotta.ifinance.model.CompanyNews;
import jp.thotta.ifinance.model.Database;
import jp.thotta.ifinance.collector.CompanyNewsCollector;

public class CompanyNewsCollectorTest extends TestCase {
  Connection c;

  protected void setUp() {
    try {
      Database.setDbUrl("jdbc:sqlite:test.db");
      c = Database.getConnection();
      CompanyNews.dropTable(c);
      CompanyNews.createTable(c);
    } catch(SQLException e) {
      e.printStackTrace();
    }
  }

  public void testAppendDb() {
    CompanyNewsCollector coll = new CompanyNewsCollector4689();
    try {
      coll.appendDb(c);
      List<CompanyNews> newsList = CompanyNews.selectByDate(c, MyDate.getToday());
      assertTrue(newsList.size() > 0);
      for(CompanyNews news : newsList) {
        assertTrue(news.hasEnough());
      }
    } catch(Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public void test3668() {
    CompanyNewsCollector coll = new CompanyNewsCollector3668();
    try {
      coll.appendDb(c);
      List<CompanyNews> newsList = CompanyNews.selectByDate(c, MyDate.getToday());
      assertTrue(newsList.size() > 0);
      for(CompanyNews news : newsList) {
        System.out.println(news);
        assertTrue(news.hasEnough());
      }
    } catch(Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  protected void tearDown() {
    try {
      Database.closeConnection();
    } catch(SQLException e) {
      e.printStackTrace();
    }
  }
}
