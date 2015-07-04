package jp.thotta.ifinance.utilizer;

import java.util.Map;

import jp.thotta.ifinance.common.StatSummary;

/**
 * 株価指標の統計情報を使ってフィルタリングするクラス.
 * @author toru1055
 */
public class StockStatsFilter {
  public StatSummary iPsrSummary;
  public StatSummary iPerSummary;
  public StatSummary iPbrSummary;
  public StatSummary salesAmountSummary;

  public StockStatsFilter(Map<String, JoinedStockInfo> jsiMap) {
    double[] iPsrs = new double[jsiMap.size()];
    double[] iPers = new double[jsiMap.size()];
    double[] iPbrs = new double[jsiMap.size()];
    double[] sales = new double[jsiMap.size()];
    int i = 0;
    for(String k : jsiMap.keySet()) {
      JoinedStockInfo jsi = jsiMap.get(k);
      iPsrs[i] = jsi.psrInverse;
      iPers[i] = jsi.perInverse;
      iPbrs[i] = jsi.pbrInverse;
      sales[i] = jsi.corporatePerformance.salesAmount;
      i++;
    }
    iPsrSummary = new StatSummary(iPsrs);
    iPerSummary = new StatSummary(iPers);
    iPbrSummary = new StatSummary(iPbrs);
    salesAmountSummary = new StatSummary(sales);
  }

  @Override
  public String toString() {
    return String.format(
        "iPsrSummary: %s\n" +
        "iPerSummary: %s\n" +
        "iPbrSummary: %s\n" +
        "salesAmountSummary: %s",
        iPsrSummary,
        iPerSummary,
        iPbrSummary,
        salesAmountSummary);
  }

  /**
   * 注目株フィルタ.
   * @param jsi フィルタリングする対象のJoinedStockInfo
   * @return 注目株かどうか
   */
  public boolean isNotable(JoinedStockInfo jsi) {
    return (
        jsi.psrInverse > iPsrSummary.percentile(75) &&
        jsi.perInverse > iPerSummary.percentile(75) &&
        jsi.pbrInverse > iPbrSummary.percentile(75) &&
        jsi.corporatePerformance.salesAmount > salesAmountSummary.percentile(25)
        );
  }
}
