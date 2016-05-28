package jp.thotta.ifinance.common;

import junit.framework.TestCase;

import java.text.SimpleDateFormat;

public class MyDateTest extends TestCase {
    public void testGetCurrentHour() {
        String hh = MyDate.getCurrentHour();
        assertEquals(hh.length(), 2);
    }

    public void testSimpleDateFormat() {
        SimpleDateFormat f = new SimpleDateFormat("MM月dd日");
        MyDate md = MyDate.parseYmd("1月2日", f);
        System.out.println(md);
    }
}
