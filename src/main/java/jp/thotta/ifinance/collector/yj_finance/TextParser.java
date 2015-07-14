package jp.thotta.ifinance.collector.yj_finance;

import jp.thotta.ifinance.common.MyDate;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 文字列パーサー.
 * @author toru1055
 */
public class TextParser {
  /**
   * 銘柄コードのパーサー.
   *
   * @param s 銘柄コードの文字列
   * @return 銘柄コードの数値
   */
  public static int parseStockId(String s) {
    String regex = "^[0-9]{4,5}$";
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(s);
    if(m.find()) {
      return Integer.parseInt(s);
    } else {
      throw new IllegalArgumentException(
          "Expected Regex[" + regex + "], " + 
          "Input[" + s + "]");
    }
  }

  /**
   * 各種決算金額のパーサー.
   *
   * @param s 決算金額の文字列
   * @return 決算金額(long)
   */
  public static long parseFinancialAmount(String s) {
    String regex = "^\\([連単]\\)[0-9,\\-]+$";
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(s);
    if(m.find()) {
      return Long.parseLong(s.replaceAll("[^0-9\\-]", ""));
    } else {
      throw new IllegalArgumentException(
          "Expected Regex[" + regex + "], " + 
          "Input[" + s + "]");
    }
  }

  /**
   * 各種決算金額のパーサー(百万円).
   *
   * @param s 決算金額の文字列
   * @return 決算金額(long)
   */
  public static Long parseMillionMoney(String s) {
    String regex = "^[0-9,\\-]+百万円$";
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(s);
    if(m.find()) {
      return Long.parseLong(s.replaceAll("[^0-9\\-]", ""));
    } else {
      return null;
    }
  }

  /**
   * 年月文字列のパーサー.
   * スラッシュ区切りの年月のパーサー
   *
   * @param s 年月の文字列
   * @return 入力年月の1日分のMyDate
   */
  public static MyDate parseYearMonth(String s) {
    String regex = "^([0-9]{4})/([0-9]{2})$";
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(s);
    if(m.find()) {
      int year = Integer.parseInt(m.group(1));
      int month = Integer.parseInt(m.group(2));
      MyDate d = new MyDate(year, month, 1);
      return d;
    } else {
      throw new IllegalArgumentException(
          "Expected Regex[" + regex + "], " + 
          "Input[" + s + "]");
    }
  }

  /**
   * 年月文字列のパーサー(日本語表記Ver).
   * "○○○○年△△月期"表記の文字列をパース
   * @param s 年月の文字列
   * @return 入力年月の1日分のMyDate
   */
  public static MyDate parseYearMonthJp(String s) {
    s += "1日";
    try {
      SimpleDateFormat f = new SimpleDateFormat("yyyy年MM月期dd日");
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

  public static MyDate parseYMD(String s) throws ParseException {
    if(s.equals("-")) {
      return null;
    } else {
      SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd");
      Date d = f.parse(s);
      Calendar c = Calendar.getInstance();
      c.setTime(d);
      int year = c.get(Calendar.YEAR);
      int month = c.get(Calendar.MONTH) + 1;
      int day = c.get(Calendar.DAY_OF_MONTH);
      return new MyDate(year, month, day);
    }
  }

  /**
   * カンマ付き数値のパーサー.
   *
   * @param s カンマ付き数値の文字列
   * @return longで返す
   */
  public static long parseNumberWithComma(String s) {
    String regex = "^[0-9,\\-]+$";
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(s);
    if(m.find()) {
      return Long.parseLong(s.replaceAll("[^0-9\\-]", ""));
    } else {
      throw new IllegalArgumentException(
          "Expected Regex[" + regex + "], " + 
          "Input[" + s + "]");
    }
  }

  /**
   * 少数第二位まである文字列を100倍してlong型で返す.
   *
   * @param s 少数を含む数値の文字列
   * @return 100倍してlongで返す
   */
  public static long parseDecimal100(String s) {
    String regex = "^[0-9,\\.]+$";
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(s);
    if(m.find()) {
      double d = Double.parseDouble(
          s.replaceAll("[^0-9\\-\\.]", ""));
      return (long)(d * 100);
    } else {
      throw new IllegalArgumentException(
          "Expected Regex[" + regex + "], " + 
          "Input[" + s + "]");
    }
  }

  /**
   * 少数を含む文字列をパース.
   * @param s 少数を含む数値の文字列
   * @return 少数
   */
  public static double parseWithDecimal(String s) {
    String regex = "^[0-9,\\-\\.]+円?$";
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(s);
    if(m.find()) {
      return Double.parseDouble(
          s.replaceAll("[^0-9\\-\\.]", ""));
    } else {
      throw new IllegalArgumentException(
          "Expected Regex[" + regex + "], " +
          "Input[" + s + "]");
    }
  }

  /**
   * パーセントを数値としてパース.
   * @param s 少数を含む数値の文字列
   * @return 少数
   */
  public static double parsePercent(String s) {
    String regex = "^[0-9,\\-\\.%]+$";
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(s);
    if(m.find()) {
      double d = Double.parseDouble(
          s.replaceAll("[^0-9\\-\\.]", ""));
      return d / 100;
    } else {
      throw new IllegalArgumentException(
          "Expected Regex[" + regex + "], " +
          "Input[" + s + "]");
    }
  }
}
