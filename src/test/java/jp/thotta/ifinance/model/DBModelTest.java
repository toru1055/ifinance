package jp.thotta.ifinance.model;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.text.ParseException;

import jp.thotta.ifinance.collector.yj_finance.*;

public class DBModelTest extends TestCase {
  Map<String, DailyStockPrice> stockTable;
  Map<String, CorporatePerformance> performances;
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

  public void testDailyStockPrice() {
    StockPriceCollectorImpl spc = new StockPriceCollectorImpl();
    spc.setStartPage(73);
    try {
      spc.append(stockTable);
    } catch(IOException e) {
      e.printStackTrace();
    }
    assertTrue(stockTable.size() > 0);
    try {
      DailyStockPrice.insertMap(stockTable, c);
      Map<String, DailyStockPrice> m = DailyStockPrice.selectAll(c);
      for(String k : m.keySet()) {
        System.out.println(m.get(k));
      }
      assertTrue(m.size() > 0);
    } catch(SQLException e) {
      e.printStackTrace();
    } catch(ParseException e) {
      e.printStackTrace();
    }
  }

  public void testCorporatePerformance() {
    SalesAmountCollectorImpl sac = new SalesAmountCollectorImpl();
    sac.setStartPage(71);
    OperatingProfitCollectorImpl oppc = new OperatingProfitCollectorImpl();
    oppc.setStartPage(71);
    OrdinaryProfitCollectorImpl orpc = new OrdinaryProfitCollectorImpl();
    orpc.setStartPage(71);
    NetProfitCollectorImpl npc = new NetProfitCollectorImpl();
    npc.setStartPage(71);
    TotalAssetsCollectorImpl tac = new TotalAssetsCollectorImpl();
    tac.setStartPage(66);
    DebtWithInterestCollectorImpl dic = new DebtWithInterestCollectorImpl();
    dic.setStartPage(59);
    CapitalFundCollectorImpl cfc = new CapitalFundCollectorImpl();
    cfc.setStartPage(72);
    try {
      sac.append(performances);
      oppc.append(performances);
      orpc.append(performances);
      npc.append(performances);
      tac.append(performances);
      dic.append(performances);
      cfc.append(performances);
    } catch(IOException e) {
      e.printStackTrace();
    }
    assertTrue(performances.size() > 0);
    try {
      CorporatePerformance.updateMap(performances, c);
      Map<String, CorporatePerformance> m = CorporatePerformance.selectAll(c);
      for(String k : m.keySet()) {
        System.out.println(m.get(k));
      }
      assertTrue(m.size() > 0);
    } catch(SQLException e) {
      e.printStackTrace();
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
