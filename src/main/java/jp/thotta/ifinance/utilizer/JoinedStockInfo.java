package jp.thotta.ifinance.utilizer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;
import java.text.ParseException;

import jp.thotta.ifinance.model.CorporatePerformance;
import jp.thotta.ifinance.model.DailyStockPrice;
import jp.thotta.ifinance.model.PerformanceForecast;
import jp.thotta.ifinance.model.CompanyProfile;

/**
 * キーに対する株価関連情報をJoinしたクラス.
 * キーは今のところ銘柄IDだけど、
 * 銘柄ID×決算年とかになる可能性あり.
 */
public class JoinedStockInfo {

  public static final int FEATURE_DIMENSION = 5;
  public DailyStockPrice dailyStockPrice;
  public CorporatePerformance corporatePerformance;
  public PerformanceForecast performanceForecast;
  public CompanyProfile companyProfile;
  public double psrInverse = 0.0;
  public double perInverse = 0.0;
  public double pbrInverse = 0.0;

  public JoinedStockInfo(DailyStockPrice dsp,
      CorporatePerformance cp,
      PerformanceForecast pf,
      CompanyProfile prof) {
    this.dailyStockPrice = dsp;
    this.corporatePerformance = cp;
    this.performanceForecast = pf;
    this.companyProfile = prof;
    if(cp.salesAmount != null) {
      this.psrInverse = (double)cp.salesAmount / dsp.marketCap;
    }
    if(cp.netProfit != null) {
      this.perInverse = (double)cp.netProfit / dsp.marketCap;
    }
    if(cp.totalAssets != null) {
      this.pbrInverse = (double)cp.totalAssets / dsp.marketCap;
    }
  }

  /**
   * 全ての要素が取得できたか.
   */
  public boolean hasEnough() {
    return dailyStockPrice != null &&
      corporatePerformance != null &&
      companyProfile != null &&
      dailyStockPrice.hasEnough() &&
      corporatePerformance.hasEnough() &&
      companyProfile.hasEnough();
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
        "key=%s, CompanyProfile={%s}, DailyStockPrice={%s}, CorporatePerformance={%s}, PerformanceForecast={%s}", 
        getKeyString(), companyProfile, dailyStockPrice, corporatePerformance, performanceForecast);
  }

  /**
   * 銘柄の説明変数ベクトルを返す.
   * @return 説明変数ベクトル x
   */
  public double[] getRegressors() {
    double[] x = new double[FEATURE_DIMENSION];
//    x[0] = (double)corporatePerformance.salesAmount;
//    x[1] = (double)corporatePerformance.operatingProfit;
//    x[4] = (double)corporatePerformance.totalAssets;
//    x[4] = getDividend();
//    x[6] = debtWithInterest();
    x[0] = (double)corporatePerformance.ownedCapital;
    x[1] = (double)corporatePerformance.ownedCapitalRatio();
    x[2] = (double)corporatePerformance.ordinaryProfit;
    x[3] = (double)corporatePerformance.netProfit;
    x[4] = getTotalDividend();
    return x;
  }

  public double debtWithInterest() {
    if(corporatePerformance.debtWithInterest != null) {
      return (double)corporatePerformance.debtWithInterest;
    } else {
      return 0.0;
    }
  }

  public double getDividend() {
    if(corporatePerformance.dividend != null) {
      return corporatePerformance.dividend;
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
    Map<String, CompanyProfile> profMap = CompanyProfile.selectAll(c);
    for(String key : dspMap.keySet()) {
      DailyStockPrice dsp = dspMap.get(key);
      CorporatePerformance cp = cpMap.get(key);
      PerformanceForecast pf = pfMap.get(key);
      CompanyProfile prof = profMap.get(key);
      if(cp != null && dsp != null) {
        JoinedStockInfo jsi = new JoinedStockInfo(dsp, cp, pf, prof);
        m.put(jsi.getKeyString(), jsi);
      } else {
      }
    }
    return m;
  }

  /**
   * 全ての情報が取得できた銘柄だけに絞り込む.
   */
  public static Map<String, JoinedStockInfo> filterMap(Map<String, JoinedStockInfo> jsiMap) {
    Map<String, JoinedStockInfo> m = new HashMap<String, JoinedStockInfo>();
    for(String k : jsiMap.keySet()) {
      JoinedStockInfo jsi = jsiMap.get(k);
      if(jsi.hasEnough()) {
        m.put(jsi.getKeyString(), jsi);
      } else {
//        System.out.println(jsi);
      }
    }
    return m;
  }
}
