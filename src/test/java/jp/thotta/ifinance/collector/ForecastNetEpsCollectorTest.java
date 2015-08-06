package jp.thotta.ifinance.collector.yj_finance;

import junit.framework.TestCase;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import jp.thotta.ifinance.model.Database;
import jp.thotta.ifinance.model.PerformanceForecast;
import jp.thotta.ifinance.collector.ForecastPerformanceCollector;

public class ForecastNetEpsCollectorTest
  extends TestCase {
  Map<String, PerformanceForecast> forecasts;
  Connection c;

  protected void setUp() {
    forecasts = new HashMap<String, PerformanceForecast>();
    try {
      Database.setDbUrl("jdbc:sqlite:test.db");
      c = Database.getConnection();
      PerformanceForecast.dropTable(c);
      PerformanceForecast.createTable(c);
    } catch(SQLException e) {
      e.printStackTrace();
    }
  }

  public void testNetEpsCollector() {
    ForecastNetEpsCollectorImpl coll =
      new ForecastNetEpsCollectorImpl();
    coll.setStartPage(69);
    try {
      coll.appendDb(c);
      forecasts = PerformanceForecast.selectLatests(c);
      assertTrue(forecasts.size() > 0);
      for(String k : forecasts.keySet()) {
        PerformanceForecast pf = forecasts.get(k);
        //System.out.println(pf);
        assertTrue(pf.hasEnough());
        assertTrue(pf.netEps != null);
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void tearDown() {
    try {
      Database.closeConnection();
    } catch(SQLException e) {
      e.printStackTrace();
    }
  }
}
