package jp.thotta.ifinance.common;

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

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
   * コンストラクタ.
   * @param s ハイフン(-)区切りの日付
   */
  public MyDate(String s) throws ParseException {
    SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
    Date d = f.parse(s);
    Calendar c = Calendar.getInstance();
    c.setTime(d);
    this.year = c.get(Calendar.YEAR);
    this.month = c.get(Calendar.MONTH) + 1;
    this.day = c.get(Calendar.DAY_OF_MONTH);
  }

  public MyDate(MyDate d) {
    this(d.year, d.month, d.day);
  }

  public MyDate copy() {
    return new MyDate(year, month, day);
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
    return String.format("%4d-%02d-%02d",
        year, month, day);
  }

  @Override
  public boolean equals(Object o) {
    MyDate md = (MyDate)o;
    if(md.year == this.year &&
        md.month == this.month &&
        md.day == this.day) 
    {
      return true;
    } else {
      return false;
    }
  }
}
