package jp.thotta.ifinance.utilizer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.io.File;

import jp.thotta.ifinance.model.CorporatePerformance;
import jp.thotta.ifinance.model.DailyStockPrice;
import jp.thotta.ifinance.model.PerformanceForecast;
import jp.thotta.ifinance.model.CompanyProfile;
import jp.thotta.ifinance.model.PredictedStockHistory;
import jp.thotta.ifinance.model.Database;
import jp.thotta.ifinance.common.MyDate;

public class CollectorSampleGenerator {
  Connection conn;
  int corpNum;
  List<Integer> stockIdList = new ArrayList<Integer>();
  public Map<String, CorporatePerformance> cpMap 
    = new HashMap<String, CorporatePerformance>();
  public Map<String, DailyStockPrice> dspMap 
    = new HashMap<String, DailyStockPrice>();
  public Map<String, PerformanceForecast> pfMap
    = new HashMap<String, PerformanceForecast>();
  public Map<String, CompanyProfile> profMap
    = new HashMap<String, CompanyProfile>();

  public CollectorSampleGenerator(int corpNum) throws SQLException {
    this.corpNum = corpNum;
    Database.setDbUrl("jdbc:sqlite:test.db");
    conn = Database.getConnection();
    init(conn);
    generateCorporatePerformance(corpNum);
    generateDailyStockPrice();
    generatePerformanceForecast();
    generateCompanyProfile();
  }

  public CollectorSampleGenerator() throws SQLException {
    this(100);
  }

  public Connection getConnection() {
    return conn;
  }

  public void closeConnection() throws SQLException {
    System.out.println("stockIdList.size=" + stockIdList.size());
    Database.closeConnection();
  }

  public void init(Connection c) throws SQLException {
    CorporatePerformance.dropTable(conn);
    DailyStockPrice.dropTable(conn);
    PerformanceForecast.dropTable(conn);
    CompanyProfile.dropTable(conn);
    PredictedStockHistory.dropTable(c);
    CorporatePerformance.createTable(c);
    DailyStockPrice.createTable(c);
    PerformanceForecast.createTable(c);
    CompanyProfile.createTable(conn);
    PredictedStockHistory.createTable(c);
  }

  /**
   * ランダムに100個の企業を生成して、3年分の決算を生成.
   */
  private void generateCorporatePerformance(int corpNum) throws SQLException {
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
        cp.salesAmount = Long.valueOf(random.nextInt(amount));
        cp.operatingProfit = Long.valueOf(random.nextInt(amount) - amount/2);
        cp.ordinaryProfit = Long.valueOf(random.nextInt(amount) - amount/2);
        cp.netProfit = Long.valueOf(random.nextInt(amount) - amount/2);
        cp.totalAssets = Long.valueOf(random.nextInt(amount));
        cp.debtWithInterest = Long.valueOf(random.nextInt(amount));
        cp.capitalFund = Long.valueOf(random.nextInt(amount));
        cp.ownedCapital = Long.valueOf(random.nextInt(amount));
        cp.dividend = random.nextDouble() * 500;
        cpMap.put(cp.getKeyString(), cp);
      }
    }
    CorporatePerformance.updateMap(cpMap, conn);
  }

  public void generatePerformanceForecast() throws SQLException {
    Random random = new Random();
    for(Integer stockId : stockIdList) {
      for(int j = 0; j < 3; j++) {
        int year = 2016 - j;
        int month = random.nextInt(12) + 1;
        PerformanceForecast pf = new PerformanceForecast(stockId, year, month);
        pf.dividend = (double)random.nextInt(20000) / 100;
        pf.dividendYield = (double)random.nextInt(10000) / 10000;
        pf.netEps = (long)random.nextInt(1000);
        pfMap.put(pf.getKeyString(), pf);
      }
    }
    PerformanceForecast.updateMap(pfMap, conn);
  }

  public void generateCompanyProfile() throws SQLException {
    Random random = new Random();
    String corpName = "株式会社あああ：";
    char c = 'あ';
    for(Integer stockId : stockIdList) {
      char cc = (char)((int)c + random.nextInt(30));
      CompanyProfile prof = new CompanyProfile(stockId);
      prof.companyName = corpName + String.valueOf(cc);
      int year = 2000 + random.nextInt(15);
      int month = 1 + random.nextInt(12);
      int day = 1 + random.nextInt(28);
      prof.foundationDate = new MyDate(year, month, day);
      prof.companyFeature = "aaa";
      prof.businessDescription = "aaa";
      prof.businessCategory = day % 2 == 1 ? "運送屋" : "建設業";
      prof.smallBusinessCategory = day % 2 == 1 ? "トラック輸送" : "マンション開発";
      profMap.put(prof.getKeyString(), prof);
    }
    CompanyProfile.updateMap(profMap, conn);
  }

  /** 
   * generateCorporatePerformanceで生成した企業のリストに対して、10日分の株価データを生成
   */
  private void generateDailyStockPrice() throws SQLException {
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
