package jp.thotta.ifinance.utilizer;

import jp.thotta.ifinance.common.MyDate;
import jp.thotta.ifinance.utilizer.JoinedStockInfo;
import jp.thotta.ifinance.utilizer.StockPricePredictor;
import jp.thotta.ifinance.utilizer.StockStatsFilter;

/**
 * 株価予測の結果クラス.
 * @author toru1055
 */
public class PredictedStockPrice {
  public JoinedStockInfo joinedStockInfo;
  public int stockId; //pk
  public MyDate predictedDate; //pk
  public long predictedMarketCap;
  public boolean isStableStock;

  public PredictedStockPrice(JoinedStockInfo joinedStockInfo,
      StockPricePredictor spp, StockStatsFilter filter) {
    this.joinedStockInfo = joinedStockInfo;
    this.stockId = joinedStockInfo.dailyStockPrice.stockId;
    this.predictedDate = MyDate.getToday();
    this.predictedMarketCap = spp.predict(joinedStockInfo);
    this.isStableStock = filter.isNotable(joinedStockInfo);
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
    return joinedStockInfo.companyProfile.companyName;
  }

  /**
   * 割安スコアを出力.
   */
  public double undervaluedScore() {
    return (double)predictedMarketCap / joinedStockInfo.dailyStockPrice.marketCap;
  }

  /**
   * 予測株価を出力.
   */
  public double predStockPrice() {
    return (double)(predictedMarketCap * 1000000) / joinedStockInfo.dailyStockPrice.stockNumber;
  }

  /**
   * 現在株価を出力.
   */
  public double actualStockPrice() {
    return (double)(joinedStockInfo.dailyStockPrice.marketCap * 1000000) / joinedStockInfo.dailyStockPrice.stockNumber;
  }

  /**
   * PERを出力.
   */
  public double per() {
    if(joinedStockInfo.perInverse > 0.0) {
      return 1.0 / joinedStockInfo.perInverse;
    } else {
      return -1.0;
    }
  }

  /**
   * 配当利回り（会社予想）を出力.
   */
  public double dividendYieldPercent() {
    if(joinedStockInfo.performanceForecast == null) {
      return 0.0;
    } else {
      return joinedStockInfo.performanceForecast.dividendYield * 100;
    }
  }

  /**
   * 自己資本比率を出力.
   */
  public double ownedCapitalRatioPercent() {
    return joinedStockInfo.corporatePerformance.ownedCapitalRatio() * 100;
  }

}
