package jp.thotta.ifinance.model;

import jp.thotta.ifinance.common.MyDate;

/**
 * 株価予測の結果クラス.
 * @author toru1055
 */
public class PredictedStockPrice implements DBModel {
  public int stockId; //pk
  public MyDate predictedDate; //pk
  public long predictedMarketCap;
  public double undervaluedScore;
  public boolean isUndervalued;

  public PredictedStockPrice(int stockId, MyDate d) {
    this.stockId = stockId;
    this.predictedDate = d;
  }

  public String getPredictionInfo() {
    return String.format(
        "stockId[%4d], " +
        "predictedMarketCap[%d], " +
        "actualMarketCap[%d], " +
        "undervaluedScore[%.3f]",
        stockId, 
        predictedMarketCap, 
        (long)((double)predictedMarketCap / undervaluedScore),
        undervaluedScore);
  }

  /**
   * Map用のキー取得.
   *
   * @return キーになる文字列
   */
  public String getKeyString() {
    return String.format("%4d,%s", stockId, predictedDate);
  }

  /**
   * Join用のキー取得.
   */
  public String getJoinKey() {
    return String.format("%4d", stockId);
  }

}
