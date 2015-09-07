package jp.thotta.ifinance.model;

import junit.framework.TestCase;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import jp.thotta.ifinance.common.MyDate;

public class CompanyNewsTest extends TestCase {
  Connection c;
  List<CompanyNews> newsList;
  CompanyNews cn, cnc;
  Statement st;

  protected void setUp() {
    cn = new CompanyNews(1001, "http://www.citizen.co.jp/files/20150707ji.pdf", new MyDate(2015, 7, 7));
    cn.title = "自己株式の取得結果及び取得終了に関するお知らせ";
    cnc = new CompanyNews(1001, "http://www.citizen.co.jp/files/20150707ji.pdf", new MyDate(2015, 7, 7));
    cnc.type = CompanyNews.NEWS_TYPE_PUBLICITY;
    cnc.createdDate = new MyDate(2000, 3, 1);

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
    CompanyNews news = new CompanyNews(1111, "http://www.yahoo.co.jp", new MyDate(2015, 7, 5));
    assertEquals(news.getKeyString(), "1111, http://www.yahoo.co.jp, 2015-07-05");
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

  public void testSelectPast() {
    MyDate d0 = MyDate.getToday();
    MyDate d1 = MyDate.getPast(1);
    MyDate d2 = MyDate.getPast(2);
    MyDate d3 = MyDate.getPast(3);
    MyDate d4 = MyDate.getPast(4);
    MyDate d5 = MyDate.getPast(5);
    List<CompanyNews> newsList = new ArrayList<CompanyNews>();
    CompanyNews cn1 = new CompanyNews(1111, "http://www.yahoo.co.jp/1", d1);
    CompanyNews cn2 = new CompanyNews(1111, "http://www.yahoo.co.jp/2", d2);
    CompanyNews cn3 = new CompanyNews(1112, "http://www.yahoo.co.jp/3", d3);
    CompanyNews cn4 = new CompanyNews(1112, "http://www.yahoo.co.jp/4", d4);
    CompanyNews cn5 = new CompanyNews(1112, "http://www.yahoo.co.jp/5", d5);
    cn1.title = "title1"; cn1.type = 1; cn1.createdDate = d1;
    cn2.title = "title2"; cn2.type = 2; cn2.createdDate = d2;
    cn3.title = "title3"; cn3.type = 3; cn3.createdDate = d3;
    cn4.title = "title4"; cn4.type = 1; cn4.createdDate = d4;
    cn5.title = "title5"; cn5.type = 2; cn5.createdDate = d5;
    newsList.add(cn1);
    newsList.add(cn2);
    newsList.add(cn3);
    newsList.add(cn4);
    newsList.add(cn5);
    try {
      CompanyNews.updateList(c, newsList);
      assertEquals(CompanyNews.selectByPast(c, 3).size(), 2);
      List<CompanyNews> newsDb = CompanyNews.selectByPast(c, 4);
      assertEquals(newsDb.size(), 3);
      for(CompanyNews news : newsDb) {
        assertFalse(news.equals(cn5));
        assertFalse(news.equals(cn4));
      }
      Map<String, List<CompanyNews>> m =
        CompanyNews.selectMapByPast(c, 4);
      assertEquals(m.size(), 2);
      assertEquals(m.get("1111").size(), 2);
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
    CompanyNews cn1 = new CompanyNews(1111, "http://www.yahoo.co.jp/1", d1);
    CompanyNews cn2 = new CompanyNews(1112, "http://www.yahoo.co.jp/2", d1);
    CompanyNews cn3 = new CompanyNews(1111, "http://www.yahoo.co.jp/3", d2);
    CompanyNews cn4 = new CompanyNews(1114, "http://www.yahoo.co.jp/4", d3);
    cn1.title = "title1"; cn1.type = 1; cn1.createdDate = d2;
    cn2.title = "title2"; cn2.type = 2; cn2.createdDate = d2;
    cn3.title = "title3"; cn3.type = 3; cn3.createdDate = d2;
    cn4.title = "title4"; cn4.type = 4; cn4.createdDate = d4;
    newsList.add(cn1);
    newsList.add(cn2);
    newsList.add(cn3);
    newsList.add(cn4);
    try {
      CompanyNews.updateList(c, newsList);
      assertEquals(CompanyNews.selectByDate(c, d1).size(), 0);
      List<CompanyNews> newsDb = CompanyNews.selectByDate(c, d2);
      assertEquals(newsDb.size(), 3);
      for(CompanyNews news : newsDb) {
        assertFalse(news.equals(cn4));
      }
      Map<String, List<CompanyNews>> m = CompanyNews.selectMapByDate(c, d2);
      assertEquals(m.size(), 2);
      assertEquals(m.get("1111").size(), 2);
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
