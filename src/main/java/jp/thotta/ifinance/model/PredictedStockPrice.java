package jp.thotta.ifinance.model;

import jp.thotta.ifinance.common.MyDate;
import jp.thotta.ifinance.utilizer.JoinedStockInfo;
import jp.thotta.ifinance.utilizer.StockPricePredictor;
import jp.thotta.ifinance.utilizer.StockStatsFilter;

/**
 * 株価予測の結果クラス.
 * @author toru1055
 */
public class PredictedStockPrice implements DBModel {
  private JoinedStockInfo jsi;
  public int stockId; //pk
  public MyDate predictedDate; //pk
  public long predictedMarketCap;
  public boolean isStableStock;

  public PredictedStockPrice(JoinedStockInfo jsi,
      StockPricePredictor spp, StockStatsFilter filter) {
    this.jsi = jsi;
    this.stockId = jsi.dailyStockPrice.stockId;
    this.predictedDate = MyDate.getToday();
    this.predictedMarketCap = spp.predict(jsi);
    this.isStableStock = filter.isNotable(jsi);
  }

  @Override
  public String toString() {
    return String.format(
        "%s（%4d）\n" + 
        "予想株価[%.1f円], 現在株価[%.1f円], スコア[%.1f倍]\n" +
        "PER[%.2f倍], 配当利回り[%.2f％], 自己資本比率[%.2f％]\n" +
        "企業情報：http://stocks.finance.yahoo.co.jp/stocks/profile/?code=%4d \n" +
        "決算推移：http://minkabu.jp/stock/%4d/consolidated \n",
        companyName(), stockId, 
        predStockPrice(), actualStockPrice(), undervaluedScore(),
        per(), dividendYieldPercent(), ownedCapitalRatioPercent(),
        stockId, stockId);
  }

  /**
   * 企業名を取得.
   */
  public String companyName() {
    return jsi.companyProfile.companyName;
  }

  /**
   * 割安スコアを出力.
   */
  public double undervaluedScore() {
    return (double)predictedMarketCap / jsi.dailyStockPrice.marketCap;
  }

  /**
   * 予測株価を出力.
   */
  public double predStockPrice() {
    return (double)(predictedMarketCap * 1000000) / jsi.dailyStockPrice.stockNumber;
  }

  /**
   * 現在株価を出力.
   */
  public double actualStockPrice() {
    return (double)(jsi.dailyStockPrice.marketCap * 1000000) / jsi.dailyStockPrice.stockNumber;
  }

  /**
   * PERを出力.
   */
  public double per() {
    if(jsi.perInverse > 0.0) {
      return 1.0 / jsi.perInverse;
    } else {
      return -1.0;
    }
  }

  /**
   * 配当利回り（会社予想）を出力.
   */
  public double dividendYieldPercent() {
    return jsi.performanceForecast.dividendYield * 100;
  }

  /**
   * 自己資本比率を出力.
   */
  public double ownedCapitalRatioPercent() {
    return jsi.corporatePerformance.ownedCapitalRatio() * 100;
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
