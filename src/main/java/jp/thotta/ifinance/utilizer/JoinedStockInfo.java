package jp.thotta.ifinance.utilizer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;
import java.text.ParseException;

import jp.thotta.ifinance.model.CorporatePerformance;
import jp.thotta.ifinance.model.DailyStockPrice;

/**
 * キーに対する株価関連情報をJoinしたクラス.
 * キーは今のところ銘柄IDだけど、
 * 銘柄ID×決算年とかになる可能性あり.
 */
public class JoinedStockInfo {
  public static final int FEATURE_DIMENSION = 9;
  public DailyStockPrice dailyStockPrice;
  public CorporatePerformance corporatePerformance;
  public double psrInverse;
  public double perInverse;
  public double pbrInverse;

  public JoinedStockInfo(DailyStockPrice dsp,
      CorporatePerformance cp) {
    this.dailyStockPrice = dsp;
    this.corporatePerformance = cp;
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
        "key=%s, DailyStockPrice={%s}, CorporatePerformance={%s}", 
        getKeyString(), dailyStockPrice, corporatePerformance);
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
    return x;
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
   * 今はCorporatePerformance, DailyStockPrice
   * @param c dbコネクション
   */
  public static Map<String, JoinedStockInfo> selectMap(Connection c) 
    throws SQLException, ParseException {
    Map<String, JoinedStockInfo> m = new HashMap<String, JoinedStockInfo>();
    Map<String, CorporatePerformance> cpMap = CorporatePerformance.selectLatests(c);
    Map<String, DailyStockPrice> dspMap = DailyStockPrice.selectLatests(c);
    for(String key : dspMap.keySet()) {
      DailyStockPrice dsp = dspMap.get(key);
      CorporatePerformance cp = cpMap.get(key);
      if(cp != null && dsp != null) {
        JoinedStockInfo jsi = new JoinedStockInfo(dsp, cp);
        m.put(jsi.getKeyString(), jsi);
      } else {
      }
    }
    return m;
  }
}
