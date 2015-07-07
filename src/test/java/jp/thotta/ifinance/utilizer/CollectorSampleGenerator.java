package jp.thotta.ifinance.utilizer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import jp.thotta.ifinance.model.CorporatePerformance;
import jp.thotta.ifinance.model.DailyStockPrice;
import jp.thotta.ifinance.model.Database;
import jp.thotta.ifinance.common.MyDate;

public class CollectorSampleGenerator {
  static Connection conn;
  static List<Integer> stockIdList = new ArrayList<Integer>();
  public static Map<String, CorporatePerformance> cpMap 
    = new HashMap<String, CorporatePerformance>();
  public static Map<String, DailyStockPrice> dspMap 
    = new HashMap<String, DailyStockPrice>();

  public static Connection getConnection(int corpNum) throws SQLException {
    Database.setDbUrl("jdbc:sqlite:test.db");
    conn = Database.getConnection();
    init();
    generateCorporatePerformance(corpNum);
    generateDailyStockPrice();
    return conn;
  }

  public static Connection getConnection() throws SQLException {
    return getConnection(100);
  }

  public static void closeConnection() throws SQLException {
    Database.closeConnection();
  }

  private static void init() throws SQLException {
    CorporatePerformance.dropTable(conn);
    DailyStockPrice.dropTable(conn);
    CorporatePerformance.createTable(conn);
    DailyStockPrice.createTable(conn);
  }

  /**
   * ランダムに100個の企業を生成して、3年分の決算を生成.
   */
  private static void generateCorporatePerformance(int corpNum) throws SQLException {
    Random random = new Random();
    for(int i = 0; i < corpNum; i++) {
      int stockId = random.nextInt(9000) + 1000;
      if(stockIdList.indexOf(stockId) >= 0) {
        continue;
      }
      stockIdList.add(stockId);
      for(int j = 0; j < 3; j++) {
        int year = 2012 + j;
        int month = random.nextInt(12) + 1;
        CorporatePerformance cp = new CorporatePerformance(stockId, year, month);
        int amount = 1000000;
        cp.salesAmount = random.nextInt(amount);
        cp.operatingProfit = random.nextInt(amount) - amount/2;
        cp.ordinaryProfit = random.nextInt(amount) - amount/2;
        cp.netProfit = random.nextInt(amount) - amount/2;
        cp.totalAssets = random.nextInt(amount);
        cp.debtWithInterest = random.nextInt(amount);
        cp.capitalFund = random.nextInt(amount);
        cp.ownedCapital = random.nextInt(amount);
        cp.dividend = random.nextDouble() * 500;
        cpMap.put(cp.getKeyString(), cp);
      }
    }
    CorporatePerformance.updateMap(cpMap, conn);
  }

  /** 
   * generateCorporatePerformanceで生成した企業のリストに対して、10日分の株価データを生成
   */
  private static void generateDailyStockPrice() throws SQLException {
    Random random = new Random();
    for(Integer stockId : stockIdList) {
      for(int j = 0; j < 10; j++) {
        int year = 2015;
        int month = 6;
        int day = 1 + j;
        MyDate d = new MyDate(year, month, day);
        DailyStockPrice dsp = new DailyStockPrice(stockId, d);
        int amount = 1000000;
        dsp.marketCap = random.nextInt(amount);
        dsp.stockNumber = random.nextInt(amount);
        dspMap.put(dsp.getKeyString(), dsp);
      }
    }
    DailyStockPrice.updateMap(dspMap, conn);
  }
}
