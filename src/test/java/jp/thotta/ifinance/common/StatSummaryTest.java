package jp.thotta.ifinance.common;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class StatSummaryTest extends TestCase {
    double[] d = new double[100];
    List<Double> dList = new ArrayList<Double>();

    protected void setUp() {
        for (int i = 0; i < d.length; i++) {
            d[i] = 100 - i;
            dList.add((double) (100 - i));
        }
    }

    public void testNormal() {
        StatSummary ss = new StatSummary(d);
        System.out.println(ss);
        assertEquals(ss.min(), 1, 0.01);
        assertEquals(ss.max(), 100, 0.01);
        assertEquals(ss.mean(), 50.5, 0.01);
        assertEquals(ss.percentile(25), 25, 0.01);
        ss = new StatSummary(dList);
        System.out.println(ss);
        assertEquals(ss.min(), 1, 0.01);
        assertEquals(ss.max(), 100, 0.01);
        assertEquals(ss.mean(), 50.5, 0.01);
        assertEquals(ss.percentile(25), 25, 0.01);
    }
}
