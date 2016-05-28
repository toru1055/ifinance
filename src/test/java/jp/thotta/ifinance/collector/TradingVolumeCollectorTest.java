package jp.thotta.ifinance.collector.yj_finance;

import jp.thotta.ifinance.model.DailyStockPrice;
import jp.thotta.ifinance.model.Database;
import junit.framework.TestCase;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class TradingVolumeCollectorTest
        extends TestCase {
    Map<String, DailyStockPrice> dspMap;
    Connection c;

    protected void setUp() {
        dspMap = new HashMap<String, DailyStockPrice>();
        try {
            Database.setDbUrl("jdbc:sqlite:test.db");
            c = Database.getConnection();
            DailyStockPrice.dropTable(c);
            DailyStockPrice.createTable(c);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test for StockPriceCollectorDirectDb
     */
    public void testAppendDb() {
        TradingVolumeCollectorImpl coll = new TradingVolumeCollectorImpl();
        coll.setStartPage(73);
        try {
            coll.appendDb(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Map<String, DailyStockPrice> m = DailyStockPrice.selectAll(c);
            assertTrue(m.size() > 0);
            for (String k : m.keySet()) {
                DailyStockPrice dsp = m.get(k);
                assertTrue(dsp.stockId > 0 && dsp.stockId < 10000);
                assertTrue(dsp.date.year > 0 && dsp.date.year < 3000);
                assertTrue(dsp.tradingVolume > 0);
                assertTrue(dsp.previousTradingVolume == null || dsp.previousTradingVolume > 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void tearDown() {
        try {
            Database.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
