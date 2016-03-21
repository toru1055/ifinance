package jp.thotta.ifinance.batch;

import junit.framework.TestCase;
import java.util.*;
import java.sql.Connection;
import java.sql.SQLException;

import jp.thotta.ifinance.model.Database;
import jp.thotta.ifinance.model.CompanyNews;

public class PredictNikkeiNewsTest extends TestCase {
  Connection c;

  protected void setUp() {
    try {
      Database.setDbUrl("jdbc:sqlite:test.db");
      c = Database.getConnection();
      CompanyNews.dropTable(c);
      CompanyNews.createTable(c);
    } catch(SQLException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public void testSetPredictData() {
    try {
      //PredictNikkeiNews p = new PredictNikkeiNews();
      //for(String url : p.keySet()) {
      //  System.out.println("title = " + p.get(url).title);
      //  System.out.println("url = " + p.get(url).url);
      //}
    } catch(Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public void testExecPredict() {
    try {
      //new PredictNikkeiNews().execPredict();
    } catch(Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public void testInsertDatabase() {
    try {
      //PredictNikkeiNews pnn = new PredictNikkeiNews(c);
      //pnn.execPredict();
      //pnn.insertDatabase();
      //assertTrue(pnn.keySet().size() > 0);
    } catch(Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

}
