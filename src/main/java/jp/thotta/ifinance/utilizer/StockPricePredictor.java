package jp.thotta.ifinance.utilizer;

import java.util.Map;

public interface StockPricePredictor {
  /**
   * 業績情報と株価の関係を学習.
   * @param jsiMap 銘柄情報を結合したクラスのmap
   * @return 学習データに対するRMSE
   */
  public double train(Map<String, JoinedStockInfo> jsiMap);

  /**
   * 株価を予測.
   * @param jsi 銘柄情報を結合したクラス
   * @return 時価総額
   */
  public long predict(JoinedStockInfo jsi);

  /**
   * 学習した予測モデルをファイル保存
   * @param filename 保存先ファイル名
   */
  public void save(String filename);
}
