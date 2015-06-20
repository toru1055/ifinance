package jp.thotta.ifinance.model;

import jp.thotta.ifinance.common.MyDate;

/**
 * 日次の株価クラス.
 * {@link DBModel}を継承し、日次株価テーブルとのアクセスも持つ
 *
 * @author toru1055
 */
public class DailyStockPrice extends DBModel {
  int stockId; //pk
  MyDate date; //pk
  public long marketCap;
  public long stockNumber;

  public DailyStockPrice(int stockId, MyDate date) {
    this.stockId = stockId;
    this.date = date;
  }

  /**
   * Map用のキー取得.
   *
   * @return キーになる文字列
   */
  public String getKeyString() {
    return String.format("%4d,%s", stockId, date);
  }

  public String toString() {
    return String.format(
        "code[%4d], " +
        "date[%s], " +
        "marketCap[%d], " +
        "stockNumber[%d]",
        stockId, date, marketCap, stockNumber);
  }
}
