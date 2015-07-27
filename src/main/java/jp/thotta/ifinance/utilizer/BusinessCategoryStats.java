package jp.thotta.ifinance.utilizer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.text.ParseException;

import jp.thotta.ifinance.common.StatSummary;
import jp.thotta.ifinance.model.CorporatePerformance;
import jp.thotta.ifinance.model.DailyStockPrice;
import jp.thotta.ifinance.model.PerformanceForecast;
import jp.thotta.ifinance.model.CompanyProfile;

/**
 * 業種ごとの統計情報を管理.
 */
public class BusinessCategoryStats {
  public String categoryName;
  public Map<String, CorporatePerformance> performances;
  public Map<String, DailyStockPrice> stockPrices;
  public StatSummary operatingPerInverse;
  public StatSummary ordinaryPerInverse;
  public StatSummary netPerInverse;

  public BusinessCategoryStats(String categoryName) {
    this.categoryName = categoryName;
    performances = new HashMap<String, CorporatePerformance>();
    stockPrices = new HashMap<String, DailyStockPrice>();
  }

  public boolean hasEnough() {
    return categoryName != null &&
      performances != null &&
      stockPrices != null &&
      operatingPerInverse != null &&
      ordinaryPerInverse != null &&
      netPerInverse != null;
  }

  /**
   * この業種の銘柄情報を追加.
   * @param cp 決算情報
   * @param dsp 株価情報
   */
  public void addItem(CorporatePerformance cp,
      DailyStockPrice dsp) {
    performances.put(cp.getJoinKey(), cp);
    stockPrices.put(dsp.getJoinKey(), dsp);
  }

  /**
   * 各種統計サマリを作成.
   */
  public void makeSummaries() {
    List<Double> iOpePer = new ArrayList<Double>();
    List<Double> iOrdPer = new ArrayList<Double>();
    List<Double> iNetPer = new ArrayList<Double>();
    for(String k : performances.keySet()) {
      CorporatePerformance cp = performances.get(k);
      DailyStockPrice dsp = stockPrices.get(k);
      if(cp != null && dsp != null &&
          cp.hasEnough() && dsp.hasEnough()) {
        iOpePer.add((double)cp.operatingProfit / dsp.marketCap);
        iOrdPer.add((double)cp.ordinaryProfit / dsp.marketCap);
        iNetPer.add((double)cp.netProfit / dsp.marketCap);
      }
    }
    operatingPerInverse = new StatSummary(iOpePer);
    ordinaryPerInverse = new StatSummary(iOrdPer);
    netPerInverse = new StatSummary(iNetPer);
  }

  @Override
  public String toString() {
    return String.format(
        "categoryName: %s\n" +
        "operatingPerInverse: %s\n" +
        "ordinaryPerInverse: %s\n" +
        "netPerInverse: %s\n",
        categoryName,
        operatingPerInverse,
        ordinaryPerInverse,
        netPerInverse);
  }

  /**
   * DBから各業種の統計情報をMapで取得.
   * @param c dbコネクション
   * @return 業種ごとの株価指標情報のMap
   */
  public static Map<String, BusinessCategoryStats> selectMap(Connection c)
    throws SQLException, ParseException {
    Map<String, BusinessCategoryStats> m = 
      new HashMap<String, BusinessCategoryStats>();
    Map<String, CorporatePerformance> cpMap =
      CorporatePerformance.selectLatests(c);
    Map<String, DailyStockPrice> dspMap = DailyStockPrice.selectLatests(c);
    Map<String, CompanyProfile> profMap = CompanyProfile.selectAll(c);
    for(String k : dspMap.keySet()) {
      DailyStockPrice dsp = dspMap.get(k);
      CorporatePerformance cp = cpMap.get(k);
      CompanyProfile prof = profMap.get(k);
      if(dsp != null && cp != null && prof != null &&
          dsp.hasEnough() && cp.hasEnough() && prof.hasEnough()) {
        BusinessCategoryStats bc = m.get(prof.businessCategory);
        if(bc == null) {
          bc = new BusinessCategoryStats(prof.businessCategory);
          m.put(prof.businessCategory, bc);
        }
        bc.addItem(cp, dsp);
      }
    }
    for(String k : m.keySet()) {
      m.get(k).makeSummaries();
    }
    return m;
  }
}
