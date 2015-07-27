package jp.thotta.ifinance.utilizer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.text.ParseException;

import jp.thotta.ifinance.common.MyDate;
import jp.thotta.ifinance.utilizer.JoinedStockInfo;
import jp.thotta.ifinance.utilizer.StockPricePredictor;
import jp.thotta.ifinance.utilizer.StockStatsFilter;
import jp.thotta.ifinance.model.PredictedStockHistory;

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

  public PredictedStockPrice(int stockId,
      MyDate predictedDate,
      long predictedMarketCap,
      boolean isStableStock,
      JoinedStockInfo joinedStockInfo) {
    this.stockId = stockId;
    this.predictedDate = predictedDate.copy();
    this.predictedMarketCap = predictedMarketCap;
    this.isStableStock = isStableStock;
    this.joinedStockInfo = joinedStockInfo;
  }

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
        "%s（%4d）[%s]\n" + 
        "予想株価[%.1f円], 現在株価[%.1f円], スコア[%.1f倍]\n" +
        "PER[%.2f倍], 業種NetPER[%.2f倍], 配当利回り[%.2f％], 自己資本比率[%.2f％]\n" +
        //"企業情報：http://stocks.finance.yahoo.co.jp/stocks/profile/?code=%4d \n" +
        "企業特色：%s\n" +
        "決算推移：http://minkabu.jp/stock/%4d/consolidated \n" +
        "決算発表日[%s]\n",
        companyName(), stockId, businessCategory(),
        predStockPrice(), actualStockPrice(), undervaluedScore(),
        per(), businessCategoryNetPer(),
        dividendYieldPercent(), ownedCapitalRatioPercent(),
        companyFeature(), stockId, announceFinancialResultDate());
  }

  public double businessCategoryNetPer() {
    return 1.0 / joinedStockInfo.businessCategoryStats.netPerInverse.median();
  }

  /**
   * 企業名を取得.
   */
  public String companyName() {
    return joinedStockInfo.companyProfile.companyName;
  }

  /**
   * 企業の特色.
   */
  public String companyFeature() {
    return joinedStockInfo.companyProfile.companyFeature;
  }

  /**
   * 業種.
   */
  public String businessCategory() {
    return joinedStockInfo.companyProfile.businessCategory;
  }

  /**
   * 決算発表日.
   */
  public MyDate announceFinancialResultDate() {
    return joinedStockInfo.corporatePerformance.announcementDate;
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

  /**
   * 最新の予測株価情報を取得.
   * @return 予測株価情報のリスト
   */
  public static List<PredictedStockPrice> selectLatests(Connection c) 
    throws SQLException, ParseException {
    List<PredictedStockPrice> pspList = new ArrayList<PredictedStockPrice>();
    Map<String, JoinedStockInfo> jsiMap = JoinedStockInfo.selectMap(c);
    Map<String, PredictedStockHistory> pshMap = PredictedStockHistory.selectPast(c, 0);
    for(String k : pshMap.keySet()) {
      PredictedStockHistory psh = pshMap.get(k);
      JoinedStockInfo jsi = jsiMap.get(psh.getJoinKey());
      if(jsi != null && jsi.hasEnough() &&
          psh.hasEnough() && psh.isStableStock) {
        PredictedStockPrice psp = new PredictedStockPrice(
            psh.stockId, psh.predictedDate,
            psh.predictedMarketCap, psh.isStableStock, jsi);
        pspList.add(psp);
      }
    }
    return pspList;
  }

}
