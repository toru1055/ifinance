package jp.thotta.ifinance.model;

import junit.framework.TestCase;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;

import jp.thotta.ifinance.common.MyDate;

public class CompanyNewsTest extends TestCase {
  Connection c;
  List<CompanyNews> newsList;
  CompanyNews cn, cnc;
  Statement st;

  protected void setUp() {
    cn = new CompanyNews(1001, "http://www.citizen.co.jp/files/20150707ji.pdf");
    cn.title = "自己株式の取得結果及び取得終了に関するお知らせ";
    cnc = new CompanyNews(1001, "http://www.citizen.co.jp/files/20150707ji.pdf");
    cnc.type = CompanyNews.NEWS_TYPE_PUBLICITY;
    cnc.announcementDate = new MyDate(2000, 1, 1);

    try {
      Database.setDbUrl("jdbc:sqlite:test.db");
      c = Database.getConnection();
      st = c.createStatement();
      CompanyNews.dropTable(c);
      CompanyNews.createTable(c);
    } catch(SQLException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public void testGetKeyString() {
    CompanyNews news = new CompanyNews(1111, "http://www.yahoo.co.jp");
    assertEquals(news.getKeyString(), "1111, http://www.yahoo.co.jp");
  }

  public void testExists() {
    try {
      assertFalse(cn.exists(st));
      cn.insert(st);
      assertTrue(cn.exists(st));
    } catch(SQLException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public void testInsertUpdate() {
    try {
      cn.insert(st);
      cnc.readDb(st);
      assertEquals(cn.title, cnc.title);
      cnc.update(st);
      cn.readDb(st);
      assertEquals(cn, cnc);
    } catch(Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public void testUpdateList() {
    MyDate d1 = new MyDate(2000, 1, 1);
    MyDate d2 = new MyDate(2011, 3, 5);
    MyDate d3 = new MyDate(1996, 5, 30);
    MyDate d4 = new MyDate(2013, 11, 3);
    List<CompanyNews> newsList = new ArrayList<CompanyNews>();
    CompanyNews cn1 = new CompanyNews(1111, "http://www.yahoo.co.jp/1");
    CompanyNews cn2 = new CompanyNews(1112, "http://www.yahoo.co.jp/2");
    CompanyNews cn3 = new CompanyNews(1113, "http://www.yahoo.co.jp/3");
    CompanyNews cn4 = new CompanyNews(1114, "http://www.yahoo.co.jp/4");
    cn1.title = "title1"; cn1.type = 1; cn1.announcementDate = d1;
    cn2.title = "title2"; cn2.type = 2; cn2.announcementDate = d1;
    cn3.title = "title3"; cn3.type = 3; cn3.announcementDate = d2;
    cn4.title = "title4"; cn4.type = 4; cn4.announcementDate = d3;
    newsList.add(cn1);
    newsList.add(cn2);
    newsList.add(cn3);
    newsList.add(cn4);
    try {
      CompanyNews.updateList(c, newsList);
      List<CompanyNews> newsDb = CompanyNews.selectByDate(c, d1);
      assertEquals(newsDb.size(), 2);
      for(CompanyNews news : newsDb) {
        assertTrue(news.equals(cn1) || news.equals(cn2));
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
