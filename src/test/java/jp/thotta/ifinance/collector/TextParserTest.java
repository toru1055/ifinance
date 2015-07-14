package jp.thotta.ifinance.collector.yj_finance;

import jp.thotta.ifinance.common.MyDate;

import junit.framework.TestCase;

public class TextParserTest extends TestCase {
  public void testParseYearMonthJp() {
    MyDate md1 = TextParser.parseYearMonthJp("2014年3月期");
    assertEquals(md1, new MyDate(2014, 3, 1));
    assertEquals(TextParser.parseYearMonthJp("2015/3"), null);
    assertEquals(TextParser.parseYearMonthJp("---"), null);
  }

  public void testParseMillionMoney() {
    Long money1, money2, money3;
    money1 = TextParser.parseMillionMoney("1,654,432百万円");
    money2 = TextParser.parseMillionMoney("-500百万円");
    money3 = TextParser.parseMillionMoney("---");
    assertEquals(money1, Long.valueOf(1654432));
    assertEquals(money2, Long.valueOf(-500));
    assertEquals(money3, null);
  }
}
