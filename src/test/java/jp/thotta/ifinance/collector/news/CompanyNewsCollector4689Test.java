package jp.thotta.ifinance.collector.news;

import java.util.List;
import java.util.ArrayList;
import junit.framework.TestCase;
import java.sql.Connection;
import java.sql.SQLException;

import jp.thotta.ifinance.common.MyDate;
import jp.thotta.ifinance.model.CompanyNews;
import jp.thotta.ifinance.model.Database;

public class CompanyNewsCollector4689Test extends TestCase {
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

  public void testParsePRList() {
    CompanyNewsCollector4689 coll = new CompanyNewsCollector4689();
    List<CompanyNews> prList = new ArrayList<CompanyNews>();
    coll.parsePRList(prList);
    assertTrue(prList.size() > 0);
    for(CompanyNews pr : prList) {
      assertTrue(pr.hasEnough());
    }
  }

  public void testParseIRList() {
    CompanyNewsCollector4689 coll = new CompanyNewsCollector4689();
    List<CompanyNews> prList = new ArrayList<CompanyNews>();
    coll.parseIRList(prList);
    assertTrue(prList.size() > 0);
    for(CompanyNews pr : prList) {
      assertTrue(pr.hasEnough());
    }
  }

  public void testAppend() {
    CompanyNewsCollector4689 coll = new CompanyNewsCollector4689();
    List<CompanyNews> prList = new ArrayList<CompanyNews>();
    coll.append(prList);
  }

  public void testAppendDb() {
    CompanyNewsCollector4689 coll = new CompanyNewsCollector4689();
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

  protected void tearDown() {
    try {
      Database.closeConnection();
    } catch(SQLException e) {
      e.printStackTrace();
    }
  }
}
