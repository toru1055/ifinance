package jp.thotta.ifinance.utilizer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;
import java.text.ParseException;

import jp.thotta.ifinance.model.CorporatePerformance;
import jp.thotta.ifinance.model.DailyStockPrice;
import jp.thotta.ifinance.model.PerformanceForecast;

/**
 * キーに対する株価関連情報をJoinしたクラス.
 * キーは今のところ銘柄IDだけど、
 * 銘柄ID×決算年とかになる可能性あり.
 */
public class JoinedStockInfo {
  public static final int FEATURE_DIMENSION = 11;
  public DailyStockPrice dailyStockPrice;
  public CorporatePerformance corporatePerformance;
  public PerformanceForecast performanceForecast;
  public double psrInverse;
  public double perInverse;
  public double pbrInverse;

  public JoinedStockInfo(DailyStockPrice dsp,
      CorporatePerformance cp,
      PerformanceForecast pf) {
    this.dailyStockPrice = dsp;
    this.corporatePerformance = cp;
    this.performanceForecast = pf;
    this.psrInverse = (double)cp.salesAmount / dsp.marketCap;
    this.perInverse = (double)cp.netProfit / dsp.marketCap;
    this.pbrInverse = (double)cp.totalAssets / dsp.marketCap;
  }

  /**
   * Map用のキー取得.
   */
  public String getKeyString() {
    return dailyStockPrice.getJoinKey();
  }

  @Override
  public String toString() {
    return String.format(
        "key=%s, DailyStockPrice={%s}, CorporatePerformance={%s}, PerformanceForecast={%s}", 
        getKeyString(), dailyStockPrice, corporatePerformance, performanceForecast);
  }

  /**
   * 銘柄の説明変数ベクトルを返す.
   * @return 説明変数ベクトル x
   */
  public double[] getRegressors() {
    double[] x = new double[FEATURE_DIMENSION];
    x[0] = (double)corporatePerformance.salesAmount;
    x[1] = (double)corporatePerformance.operatingProfit;
    x[2] = (double)corporatePerformance.ordinaryProfit;
    x[3] = (double)corporatePerformance.netProfit;
    x[4] = (double)corporatePerformance.totalAssets;
    x[5] = (double)corporatePerformance.debtWithInterest;
    x[6] = (double)corporatePerformance.capitalFund;
    x[7] = (double)corporatePerformance.ownedCapital;
    x[8] = (double)corporatePerformance.ownedCapitalRatio();
    x[9] = getTotalDividend();
    x[10] = getDividend();
    //x[11] = getDividendYield();
    return x;
  }

  public double getDividendYield() {
    if(performanceForecast != null) {
      return performanceForecast.dividendYield;
    } else {
      return 0.0;
    }
  }

  public double getDividend() {
    if(performanceForecast != null) {
      return performanceForecast.dividend;
    } else {
      return 0.0;
    }
  }

  /**
   * 配当金額の合計.
   * @return 合計配当金額(会社予想)
   */
  private double getTotalDividend() {
    return getDividend() * dailyStockPrice.stockNumber / 1000000;
  }

  /**
   * 銘柄の株価(目的変数)を返す.
   * @return 株価(目的変数)
   */
  public double getRegressand() {
    return (double)dailyStockPrice.marketCap;
  }

  /**
   * 紐付け対象のDBテーブルをJoinして、Mapを生成する.
   * 今はCorporatePerformance, DailyStockPrice, PerformanceForecast
   * @param c dbコネクション
   */
  public static Map<String, JoinedStockInfo> selectMap(Connection c) 
    throws SQLException, ParseException {
    Map<String, JoinedStockInfo> m = new HashMap<String, JoinedStockInfo>();
    Map<String, CorporatePerformance> cpMap = CorporatePerformance.selectLatests(c);
    Map<String, DailyStockPrice> dspMap = DailyStockPrice.selectLatests(c);
    Map<String, PerformanceForecast> pfMap = PerformanceForecast.selectLatests(c);
    for(String key : dspMap.keySet()) {
      DailyStockPrice dsp = dspMap.get(key);
      CorporatePerformance cp = cpMap.get(key);
      PerformanceForecast pf = pfMap.get(key);
      if(cp != null && dsp != null) {
        JoinedStockInfo jsi = new JoinedStockInfo(dsp, cp, pf);
        m.put(jsi.getKeyString(), jsi);
      } else {
      }
    }
    return m;
  }
}
