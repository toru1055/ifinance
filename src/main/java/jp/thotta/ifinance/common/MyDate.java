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
public class MyDate implements Comparable {
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
   * @param c Calendarクラス
   */
  public MyDate(Calendar c) {
    this.year = c.get(Calendar.YEAR);
    this.month = c.get(Calendar.MONTH) + 1;
    this.day = c.get(Calendar.DAY_OF_MONTH);
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

  /**
   * 現在の時間(hour)を返す.
   * @return 現在時刻(hour)
   */
  public static String getCurrentHour() {
    Calendar c = Calendar.getInstance();
    SimpleDateFormat f  = new SimpleDateFormat("HH");
    String hh = f.format(c.getTime());
    return hh;
  }

  /**
   * 現在の時間(hour)を返す.
   * @return 現在時刻(hour)
   */
  public static int getCurrentHourInt() {
    Calendar c = Calendar.getInstance();
    SimpleDateFormat f  = new SimpleDateFormat("HH");
    String hh = f.format(c.getTime());
    return Integer.parseInt(hh);
  }

  /**
   * 入力された日付分過去のインスタンスを生成.
   * @return 過去の日付
   */
  public static MyDate getPast(int days) {
    Calendar c = Calendar.getInstance();
    c.add(Calendar.DAY_OF_MONTH, -days);
    return new MyDate(c);
  }

  /**
   * スラッシュ区切りの年月日文字列パース.
   */
  public static MyDate parseYmd(String s) {
    try {
      SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd");
      Date d = f.parse(s);
      Calendar c = Calendar.getInstance();
      c.setTime(d);
      int year = c.get(Calendar.YEAR);
      int month = c.get(Calendar.MONTH) + 1;
      int day = c.get(Calendar.DAY_OF_MONTH);
      return new MyDate(year, month, day);
    } catch(ParseException e) {
      return null;
    }
  }

  /**
   * フォーマットを指定して年月日文字列をパース.
   */
  public static MyDate parseYmd(String s, SimpleDateFormat f) {
    try {
      Date d = f.parse(s);
      Calendar c = Calendar.getInstance();
      c.setTime(d);
      int year = c.get(Calendar.YEAR);
      int month = c.get(Calendar.MONTH) + 1;
      int day = c.get(Calendar.DAY_OF_MONTH);
      return new MyDate(year, month, day);
    } catch(ParseException e) {
      return null;
    }
  }

  /**
   * 日本語表記の年月日文字列パース.
   */
  public static MyDate parseYmdJapan(String s) {
    try {
      SimpleDateFormat f = new SimpleDateFormat("yyyy年MM月dd日");
      Date d = f.parse(s);
      Calendar c = Calendar.getInstance();
      c.setTime(d);
      int year = c.get(Calendar.YEAR);
      int month = c.get(Calendar.MONTH) + 1;
      int day = c.get(Calendar.DAY_OF_MONTH);
      return new MyDate(year, month, day);
    } catch(ParseException e) {
      return null;
    }
  }

  public String toString() {
    return String.format("%4d-%02d-%02d",
        year, month, day);
  }

  public String toFormat(String format) {
    return String.format(format,
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

  public int compareTo(Object obj) {
    MyDate md = (MyDate)obj;
    if(this.year == md.year) {
      if(this.month == md.month) {
        if(this.day == md.day) {
          return 0;
        } else {
          return this.day - md.day;
        }
      } else {
        return this.month - md.month;
      }
    } else {
      return this.year - md.year;
    }
  }
}
