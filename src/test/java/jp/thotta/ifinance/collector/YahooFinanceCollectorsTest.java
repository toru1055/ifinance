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
    SalesAmountCollectorImpl sc 
      = new SalesAmountCollectorImpl();
    sc.setStartPage(71);
    try {
      sc.append(performances);
    } catch(IOException e) {
      e.printStackTrace();
    }
    for(String k : performances.keySet()) {
      CorporatePerformance cp = performances.get(k);
      System.out.println(cp);
    }
    assertTrue(performances.size() > 0);
  }

  /**
   * Test for OperatingProfitCollectorImpl.
   */
  public void testOperatingProfitCollectorImpl() {
    OperatingProfitCollectorImpl oc
      = new OperatingProfitCollectorImpl();
    oc.setStartPage(71);
    try {
      oc.append(performances);
    } catch(IOException e) {
      e.printStackTrace();
    }
    for(String k : performances.keySet()) {
      CorporatePerformance cp = performances.get(k);
      System.out.println(cp);
    }
    assertTrue(performances.size() > 0);
  }

  /**
   * Test for OrdinaryProfitCollectorImpl.
   */
  public void testOrdinaryProfitCollectorImpl() {
    OrdinaryProfitCollectorImpl oc2
      = new OrdinaryProfitCollectorImpl();
    oc2.setStartPage(71);
    try {
      oc2.append(performances);
    } catch(IOException e) {
      e.printStackTrace();
    }
    for(String k : performances.keySet()) {
      CorporatePerformance cp = performances.get(k);
      System.out.println(cp);
    }
    assertTrue(performances.size() > 0);
  }

  /**
   * Test for NetProfitCollectorImpl.
   */
  public void testNetProfitCollectorImpl() {
    NetProfitCollectorImpl np
      = new NetProfitCollectorImpl();
    np.setStartPage(71);
    try {
      np.append(performances);
    } catch(IOException e) {
      e.printStackTrace();
    }
    for(String k : performances.keySet()) {
      CorporatePerformance cp = performances.get(k);
      System.out.println(cp);
    }
    assertTrue(performances.size() > 0);
  }

  /**
   * Test for TotalAssetsCollectorImpl.
   */
  public void testTotalAssetsCollectorImpl() {
    TotalAssetsCollectorImpl ta
      = new TotalAssetsCollectorImpl();
    ta.setStartPage(66);
    try {
      ta.append(performances);
    } catch(IOException e) {
      e.printStackTrace();
    }
    for(String k : performances.keySet()) {
      CorporatePerformance cp = performances.get(k);
      System.out.println(cp);
    }
    assertTrue(performances.size() > 0);
  }

  /**
   * Test for DebtWithInterestCollectorImpl.
   */
  public void testDebtWithInterestCollectorImpl() {
    DebtWithInterestCollectorImpl dwi
      = new DebtWithInterestCollectorImpl();
    dwi.setStartPage(59);
    try {
      dwi.append(performances);
    } catch(IOException e) {
      e.printStackTrace();
    }
    for(String k : performances.keySet()) {
      CorporatePerformance cp = performances.get(k);
      System.out.println(cp);
    }
    assertTrue(performances.size() > 0);
  }

  /**
   * Test for CapitalFundCollectorImpl.
   */
  public void testCapitalFundCollectorImpl() {
    CapitalFundCollectorImpl cf
      = new CapitalFundCollectorImpl();
    cf.setStartPage(72);
    try {
      cf.append(performances);
    } catch(IOException e) {
      e.printStackTrace();
    }
    for(String k : performances.keySet()) {
      CorporatePerformance cp = performances.get(k);
      System.out.println(cp);
    }
    assertTrue(performances.size() > 0);
  }

  /**
   * Test for StockPriceCollectorImpl.
   */
  public void testStockPriceCollectorImpl() {
    StockPriceCollectorImpl spc = new StockPriceCollectorImpl();
    spc.setStartPage(73);
    try {
      spc.append(stockTable);
    } catch(IOException e) {
      e.printStackTrace();
    }
    for(String k : stockTable.keySet()) {
      DailyStockPrice dsp = stockTable.get(k);
      System.out.println(dsp);
    }
    assertTrue(stockTable.size() > 0);
  }

  /**
   * Test for StockPriceCollectorDirectDb
   */
  public void testDailyStockPriceDirectDb() {
    StockPriceCollectorImpl spc = new StockPriceCollectorImpl();
    spc.setStartPage(73);
    try {
      spc.appendDb(c);
    } catch(Exception e) {
      e.printStackTrace();
    }
    try {
      Map<String, DailyStockPrice> m = DailyStockPrice.selectAll(c);
      for(String k : m.keySet()) {
        System.out.println(m.get(k));
      }
      assertTrue(m.size() > 0);
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Test for FinancialAmountCollector DirectDb
   */
  public void testCorporatePerformanceDirectDb() {
    SalesAmountCollectorImpl sac = new SalesAmountCollectorImpl();
    sac.setStartPage(71);
    OperatingProfitCollectorImpl oppc = new OperatingProfitCollectorImpl();
    oppc.setStartPage(71);
    try {
      sac.appendDb(c);
      oppc.appendDb(c);
      Map<String, CorporatePerformance> m = CorporatePerformance.selectAll(c);
      for(String k : m.keySet()) {
        System.out.println(m.get(k));
      }
      assertTrue(m.size() > 0);
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

}
