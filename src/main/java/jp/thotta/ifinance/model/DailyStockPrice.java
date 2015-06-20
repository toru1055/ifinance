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
  public long marketCapitalization;
  public long stockNumber;

  public DailyStockPrice(int stockId, MyDate date) {
    this.stockId = stockId;
    this.date = date;
  }
}
