package jp.thotta.ifinance.common;

import java.util.Calendar;

/**
 * 日付クラス.
 * CalendarのWrapper
 *
 * @author toru1055
 */
public class MyDate {
  public int year;
  public int month;
  public int day;

  /**
   * コンストラクタ.
   * @param y 年
   * @param m 月
   * @param d 日
   */
  public MyDate(int y, int m, int d) {
    this.year = y;
    this.month = m;
    this.day = d;
  }

  /**
   * 本日のインスタンスを生成.
   * @return 本日のMyDateインスタンス
   */
  public static MyDate getToday() {
    Calendar c = Calendar.getInstance();
    return new MyDate(
        c.get(Calendar.YEAR),
        c.get(Calendar.MONTH) + 1,
        c.get(Calendar.DAY_OF_MONTH));
  }

  public String toString() {
    return String.format("%4d/%02d/%02d",
        year, month, day);
  }
}
