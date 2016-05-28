package jp.thotta.ifinance.collector.yj_finance;

import jp.thotta.ifinance.collector.FinancialAmountCollector;
import jp.thotta.ifinance.collector.ForecastPerformanceCollector;
import jp.thotta.ifinance.model.CorporatePerformance;
import jp.thotta.ifinance.model.DailyStockPrice;
import jp.thotta.ifinance.model.Database;
import jp.thotta.ifinance.model.PerformanceForecast;
import junit.framework.TestCase;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Unit test for YahooFinanceCollectors.
 */
public class YahooFinanceCollectorsTest
        extends TestCase {
    Map<String, CorporatePerformance> performances;
    Map<String, DailyStockPrice> stockTable;
    Map<String, PerformanceForecast> forecasts;
    Connection c;

    protected void setUp() {
        stockTable = new HashMap<String, DailyStockPrice>();
        performances = new HashMap<String, CorporatePerformance>();
        forecasts = new HashMap<String, PerformanceForecast>();
        try {
            Database.setDbUrl("jdbc:sqlite:test.db");
            c = Database.getConnection();
            DailyStockPrice.dropTable(c);
            DailyStockPrice.createTable(c);
            CorporatePerformance.dropTable(c);
            CorporatePerformance.createTable(c);
            PerformanceForecast.dropTable(c);
            PerformanceForecast.createTable(c);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * SalesAmountCollectorImplのテスト.
     */
    public void testSalesAmountCollectorImpl() {
        SalesAmountCollectorImpl coll = new SalesAmountCollectorImpl();
        coll.setStartPage(71);
        CorporatePerformance cp = getFirst(coll);
        assertTrue(cp.salesAmount > 0);
    }

    /**
     * Test for OperatingProfitCollectorImpl.
     */
    public void testOperatingProfitCollectorImpl() {
        OperatingProfitCollectorImpl coll = new OperatingProfitCollectorImpl();
        coll.setStartPage(71);
        CorporatePerformance cp = getFirst(coll);
    }

    /**
     * Test for OrdinaryProfitCollectorImpl.
     */
    public void testOrdinaryProfitCollectorImpl() {
        OrdinaryProfitCollectorImpl coll = new OrdinaryProfitCollectorImpl();
        coll.setStartPage(71);
        CorporatePerformance cp = getFirst(coll);
    }

    /**
     * Test for NetProfitCollectorImpl.
     */
    public void testNetProfitCollectorImpl() {
        NetProfitCollectorImpl coll = new NetProfitCollectorImpl();
        coll.setStartPage(71);
        CorporatePerformance cp = getFirst(coll);
    }

    /**
     * Test for TotalAssetsCollectorImpl.
     */
    public void testTotalAssetsCollectorImpl() {
        TotalAssetsCollectorImpl coll = new TotalAssetsCollectorImpl();
        coll.setStartPage(71);
        CorporatePerformance cp = getFirst(coll);
        assertTrue(cp.totalAssets > 0);
    }

    /**
     * Test for DebtWithInterestCollectorImpl.
     */
    public void testDebtWithInterestCollectorImpl() {
        DebtWithInterestCollectorImpl coll = new DebtWithInterestCollectorImpl();
        coll.setStartPage(59);
        CorporatePerformance cp = getFirst(coll);
        assertTrue(cp.debtWithInterest >= 0);
    }

    /**
     * Test for CapitalFundCollectorImpl.
     */
    public void testCapitalFundCollectorImpl() {
        CapitalFundCollectorImpl coll = new CapitalFundCollectorImpl();
        coll.setStartPage(71);
        CorporatePerformance cp = getFirst(coll);
        assertTrue(cp.capitalFund > 0);
    }

    /**
     * Test for OwnedCapitalCollectorImpl.
     */
    public void testOwnedCapitalCollectorImpl() {
        OwnedCapitalCollectorImpl coll = new OwnedCapitalCollectorImpl();
        coll.setStartPage(71);
        CorporatePerformance cp = getFirst(coll);
        assertTrue(cp.ownedCapital > 0);
    }

    private CorporatePerformance getFirst(FinancialAmountCollector collector) {
        try {
            collector.append(performances);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return assertCorporatePerformances(performances);
    }

    private PerformanceForecast getFirstForecast(
            ForecastPerformanceCollector collector) {
        try {
            collector.append(forecasts);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return assertPerformanceForecast(forecasts);
    }

    /**
     * Test for ForecastDividendCollectorImpl.
     */
    public void testForecastDividendCollectorImpl() {
        ForecastDividendCollectorImpl coll = new ForecastDividendCollectorImpl();
        coll.setStartPage(57);
        PerformanceForecast pf = getFirstForecast(coll);
        assertTrue(pf.dividend > 0.0 && pf.dividendYield > 0.0);
    }

    /**
     * Test for StockPriceCollectorImpl.
     */
    public void testStockPriceCollectorImpl() {
        StockPriceCollectorImpl spc = new StockPriceCollectorImpl();
        spc.setStartPage(73);
        try {
            spc.append(stockTable);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertStockTable(stockTable);
    }

    private CorporatePerformance assertCorporatePerformances(
            Map<String, CorporatePerformance> p_map) {
        assertTrue(p_map.size() > 0);
        CorporatePerformance cp = p_map.get(p_map.keySet().iterator().next());
        assertTrue(cp.stockId > 0 && cp.stockId < 10000);
        assertTrue(cp.settlingYear > 0 && cp.settlingYear < 3000);
        assertTrue(cp.settlingMonth >= 1 && cp.settlingMonth <= 12);
        return cp;
    }

    private void assertStockTable(Map<String, DailyStockPrice> s_map) {
        assertTrue(s_map.size() > 0);
        DailyStockPrice dsp = s_map.get(s_map.keySet().iterator().next());
        assertTrue(dsp.stockId > 0 && dsp.stockId < 10000);
        assertTrue(dsp.date.year > 0 && dsp.date.year < 3000);
        assertTrue(dsp.date.month >= 1 && dsp.date.month <= 12);
        assertTrue(dsp.date.day >= 1 && dsp.date.day <= 31);
        assertTrue(dsp.marketCap > 0);
        assertTrue(dsp.stockNumber > 0);
    }

    private PerformanceForecast assertPerformanceForecast(
            Map<String, PerformanceForecast> m) {
        assertTrue(m.size() > 0);
        for (String k : m.keySet()) {
            PerformanceForecast pf = m.get(k);
            assertTrue(pf.stockId > 0 && pf.stockId < 10000);
            assertTrue(pf.settlingYear > 0 && pf.settlingYear < 3000);
            assertTrue(pf.settlingMonth >= 1 && pf.settlingMonth <= 12);
            return pf;
        }
        return null;
    }

    /**
     * Test for StockPriceCollectorDirectDb
     */
    public void testDailyStockPriceDirectDb() {
        StockPriceCollectorImpl spc = new StockPriceCollectorImpl();
        spc.setStartPage(73);
        try {
            spc.appendDb(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Map<String, DailyStockPrice> m = DailyStockPrice.selectAll(c);
            assertStockTable(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test for PerformanceForecastCollector DirectDb
     */
    public void testPerformanceForecastDirectDb() {
        ForecastDividendCollectorImpl fdc = new ForecastDividendCollectorImpl();
        fdc.setStartPage(58);
        try {
            fdc.appendDb(c);
            Map<String, PerformanceForecast> m = PerformanceForecast.selectAll(c);
            assertPerformanceForecast(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test for FinancialAmountCollector DirectDb
     */
    public void testCorporatePerformanceDirectDb() {
        SalesAmountCollectorImpl sac = new SalesAmountCollectorImpl();
        OperatingProfitCollectorImpl oppc = new OperatingProfitCollectorImpl();
        ForecastDividendCollectorImpl fdc = new ForecastDividendCollectorImpl();
        sac.setStartPage(72);
        oppc.setStartPage(71);
        fdc.setStartPage(57);
        try {
            sac.appendDb(c);
            oppc.appendDb(c);
            fdc.appendDb(c);
            fdc.setStartPage(57);
            fdc.appendDb(c);
            Map<String, CorporatePerformance> m = CorporatePerformance.selectAll(c);
            assertCorporatePerformances(m);
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
