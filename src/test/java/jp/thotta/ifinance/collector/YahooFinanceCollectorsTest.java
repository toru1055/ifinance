package jp.thotta.ifinance.collector.yj_finance;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

import java.sql.Connection;
import java.sql.SQLException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import jp.thotta.ifinance.model.CorporatePerformance;
import jp.thotta.ifinance.model.DailyStockPrice;
import jp.thotta.ifinance.model.Database;
import jp.thotta.ifinance.collector.FinancialAmountCollector;

/**
 * Unit test for YahooFinanceCollectors.
 */
public class YahooFinanceCollectorsTest 
  extends TestCase {
  Map<String, CorporatePerformance> performances;
  Map<String, DailyStockPrice> stockTable;
  Connection c;

  protected void setUp() {
    stockTable = new HashMap<String, DailyStockPrice>();
    performances = new HashMap<String, CorporatePerformance>();
    try {
      Database.setDbUrl("jdbc:sqlite:test.db");
      c = Database.getConnection();
      DailyStockPrice.dropTable(c);
      DailyStockPrice.createTable(c);
      CorporatePerformance.dropTable(c);
      CorporatePerformance.createTable(c);
    } catch(SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * SalesAmountCollectorImplのテスト.
   */
  public void testSalesAmountCollectorImpl() {
    SalesAmountCollectorImpl coll = new SalesAmountCollectorImpl();
    coll.setStartPage(72);
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
    coll.setStartPage(72);
    CorporatePerformance cp = getFirst(coll);
  }

  /**
   * Test for NetProfitCollectorImpl.
   */
  public void testNetProfitCollectorImpl() {
    NetProfitCollectorImpl coll = new NetProfitCollectorImpl();
    coll.setStartPage(72);
    CorporatePerformance cp = getFirst(coll);
  }

  /**
   * Test for TotalAssetsCollectorImpl.
   */
  public void testTotalAssetsCollectorImpl() {
    TotalAssetsCollectorImpl coll = new TotalAssetsCollectorImpl();
    coll.setStartPage(66);
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
    coll.setStartPage(72);
    CorporatePerformance cp = getFirst(coll);
    assertTrue(cp.capitalFund > 0);
  }

  /**
   * Test for OwnedCapitalCollectorImpl.
   */
  public void testOwnedCapitalCollectorImpl() {
    OwnedCapitalCollectorImpl coll = new OwnedCapitalCollectorImpl();
    coll.setStartPage(72);
    CorporatePerformance cp = getFirst(coll);
    assertTrue(cp.ownedCapital > 0);
  }

  /**
   * Test for OwnedCapitalCollectorImpl.
   */
  public void testDividendCollectorImpl() {
    DividendCollectorImpl coll = new DividendCollectorImpl();
    coll.setStartPage(58);
    CorporatePerformance cp = getFirst(coll);
    assertTrue(cp.dividend > 0.0);
  }

  private CorporatePerformance getFirst(FinancialAmountCollector collector) {
    try {
      collector.append(performances);
    } catch(IOException e) {
      e.printStackTrace();
    }
    return assertCorporatePerformances(performances);
  }

  /**
   * Test for StockPriceCollectorImpl.
   */
  public void testStockPriceCollectorImpl() {
    StockPriceCollectorImpl spc = new StockPriceCollectorImpl();
    spc.setStartPage(74);
    try {
      spc.append(stockTable);
    } catch(IOException e) {
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
    assertTrue(dsp.date.day >= 1 && dsp.date.day <=31 );
    assertTrue(dsp.marketCap > 0);
    assertTrue(dsp.stockNumber > 0);
  }

  /**
   * Test for StockPriceCollectorDirectDb
   */
  public void testDailyStockPriceDirectDb() {
    StockPriceCollectorImpl spc = new StockPriceCollectorImpl();
    spc.setStartPage(74);
    try {
      spc.appendDb(c);
    } catch(Exception e) {
      e.printStackTrace();
    }
    try {
      Map<String, DailyStockPrice> m = DailyStockPrice.selectAll(c);
      assertStockTable(m);
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Test for FinancialAmountCollector DirectDb
   */
  public void testCorporatePerformanceDirectDb() {
    SalesAmountCollectorImpl sac = new SalesAmountCollectorImpl();
    OperatingProfitCollectorImpl oppc = new OperatingProfitCollectorImpl();
    DividendCollectorImpl dc = new DividendCollectorImpl();
    sac.setStartPage(72);
    oppc.setStartPage(71);
    dc.setStartPage(58);
    try {
      sac.appendDb(c);
      oppc.appendDb(c);
      dc.appendDb(c);
      Map<String, CorporatePerformance> m = CorporatePerformance.selectAll(c);
      assertCorporatePerformances(m);
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

}
