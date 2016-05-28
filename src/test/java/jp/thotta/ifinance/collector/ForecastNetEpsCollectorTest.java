package jp.thotta.ifinance.collector.yj_finance;

import jp.thotta.ifinance.model.Database;
import jp.thotta.ifinance.model.PerformanceForecast;
import junit.framework.TestCase;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

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
        } catch (SQLException e) {
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
            for (String k : forecasts.keySet()) {
                PerformanceForecast pf = forecasts.get(k);
                //System.out.println(pf);
                assertTrue(pf.hasEnough());
                assertTrue(pf.netEps != null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void tearDown() {
        try {
            Database.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
