package jp.thotta.ifinance.model;

/**
 * 通期の企業業績クラス.
 * {@link DBModel}を継承し、業績テーブルとのアクセスも持つ
 *
 * @author toru1055
 */
public class CorporatePerformance extends DBModel {
  int stockId; //pk
  int settlingYear; // pk
  int settlingMonth; // pk
  public long salesAmount;
  public long operatingProfit;
  public long ordinaryProfit;
  public long netProfit;
  public long totalAssets;
  public long debtWithInterest;
  public long capitalFund;

  public CorporatePerformance(
      int stockId, 
      int settlingYear, 
      int settlingMonth) {
    this.stockId = stockId;
    this.settlingYear = settlingYear;
    this.settlingMonth = settlingMonth;
  }

  public String toString() {
    String s = String.format(
        "code[%4d], " +
        "YM[%4d/%02d], " +
        "salesAmount[%d]",
        stockId,
        settlingYear,
        settlingMonth,
        salesAmount);
    return s;
  }
}