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
  public DailyStockPrice dailyStockPrice;
  public CorporatePerformance corporatePerformance;
  public double psrInverse;
  public double perInverse;
  public double pbrInverse;

  public JoinedStockInfo(DailyStockPrice dsp,
      CorporatePerformance cp) {
    this.dailyStockPrice = dsp;
    this.corporatePerformance = cp;
    this.psrInverse = cp.salesAmount / dsp.marketCap;
    this.perInverse = cp.netProfit / dsp.marketCap;
    this.pbrInverse = cp.totalAssets / dsp.marketCap;
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
      JoinedStockInfo jsi = new JoinedStockInfo(dsp, cp);
      m.put(jsi.getKeyString(), jsi);
    }
    return m;
  }
}
