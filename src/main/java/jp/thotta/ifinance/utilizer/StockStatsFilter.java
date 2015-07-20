package jp.thotta.ifinance.utilizer;

import java.util.Map;

import jp.thotta.ifinance.common.StatSummary;

/**
 * 株価指標の統計情報を使ってフィルタリングするクラス.
 * @author toru1055
 */
public class StockStatsFilter {
  public StatSummary salesAmountSummary;
  public StatSummary operatingProfitSummary;
  public StatSummary iPsrSummary;
  public StatSummary iPerSummary;
  public StatSummary iPbrSummary;
  public double salesAmountPercentile;
  public double operatingProfitPercentile;
  public double iPsrPercentile;
  public double iPerPercentile;
  public double iPbrPercentile;

  public StockStatsFilter(
      Map<String, JoinedStockInfo> jsiMap, 
      double salesAmountPercentile, 
      double operatingProfitPercentile, 
      double iPsrPercentile, 
      double iPerPercentile, 
      double iPbrPercentile) {
    double[] iPsrs = new double[jsiMap.size()];
    double[] iPers = new double[jsiMap.size()];
    double[] iPbrs = new double[jsiMap.size()];
    double[] sales = new double[jsiMap.size()];
    double[] profits = new double[jsiMap.size()];
    int i = 0;
    for(String k : jsiMap.keySet()) {
      JoinedStockInfo jsi = jsiMap.get(k);
      Long salesAmount = jsi.corporatePerformance.salesAmount;
      Long operatingProfit = jsi.corporatePerformance.operatingProfit;
      iPsrs[i] = jsi.psrInverse;
      iPers[i] = jsi.perInverse;
      iPbrs[i] = jsi.pbrInverse;
      sales[i] = salesAmount != null ? salesAmount : 0;
      profits[i] = operatingProfit != null ? operatingProfit : 0;
      i++;
    }
    iPsrSummary = new StatSummary(iPsrs);
    iPerSummary = new StatSummary(iPers);
    iPbrSummary = new StatSummary(iPbrs);
    salesAmountSummary = new StatSummary(sales);
    operatingProfitSummary = new StatSummary(profits);
    this.salesAmountPercentile = salesAmountPercentile;
    this.operatingProfitPercentile = operatingProfitPercentile;
    this.iPsrPercentile = iPsrPercentile;
    this.iPerPercentile = iPerPercentile;
    this.iPbrPercentile = iPbrPercentile;
  }

  public StockStatsFilter(Map<String, JoinedStockInfo> jsiMap) {
    this(jsiMap, 60, 60, 60, 75, 60);
  }

  @Override
  public String toString() {
    return String.format(
        "iPsrSummary: %s\n" +
        "iPerSummary: %s\n" +
        "iPbrSummary: %s\n" +
        "salesAmountSummary: %s\n" +
        "operatingProfitSummary: %s",
        iPsrSummary,
        iPerSummary,
        iPbrSummary,
        salesAmountSummary,
        operatingProfitSummary);
  }

  /**
   * 注目株フィルタ.
   * @param jsi フィルタリングする対象のJoinedStockInfo
   * @return 注目株かどうか
   */
  public boolean isNotable(JoinedStockInfo jsi) {
    return (
        jsi.psrInverse > iPsrSummary.percentile(iPsrPercentile) &&
        jsi.perInverse > iPerSummary.percentile(iPerPercentile) &&
        jsi.pbrInverse > iPbrSummary.percentile(iPbrPercentile) &&
        jsi.corporatePerformance.salesAmount > salesAmountSummary.percentile(salesAmountPercentile) &&
        jsi.corporatePerformance.operatingProfit > operatingProfitSummary.percentile(operatingProfitPercentile));
  }
}
