package jp.thotta.ifinance.common;

import junit.framework.TestCase;

public class MyDateTest extends TestCase {
  public void testGetCurrentHour() {
    String hh = MyDate.getCurrentHour();
    assertEquals(hh.length(), 2);
  }
}
