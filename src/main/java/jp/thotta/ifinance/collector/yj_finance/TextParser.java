package jp.thotta.ifinance.collector.yj_finance;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.GregorianCalendar;
import java.util.Calendar;

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
    String regex = "^[0-9]{4}$";
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
   * 年月文字列のパーサー.
   * スラッシュ区切りの年月のパーサー
   *
   * @param s 年月の文字列
   * @return 入力年月の1日分のCalendar
   */
  public static Calendar parseYearMonth(String s) {
    String regex = "^([0-9]{4})/([0-9]{2})$";
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(s);
    if(m.find()) {
      int year = Integer.parseInt(m.group(1));
      int month = Integer.parseInt(m.group(2));
      Calendar c = new GregorianCalendar(year, month-1, 1);
      return c;
    } else {
      throw new IllegalArgumentException(
          "Expected Regex[" + regex + "], " + 
          "Input[" + s + "]");
    }
  }
}
