package jp.thotta.ifinance.collector.yj_finance;

import jp.thotta.ifinance.model.CorporatePerformance;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import junit.framework.TestCase;

public class BasePerformanceCollectorTest extends TestCase {
  List<Integer> stockIdList = new ArrayList<Integer>();
  BasePerformanceCollectorImpl coll;

  protected void setUp() {
    stockIdList.add(8411);
    stockIdList.add(3787);
    coll = new BasePerformanceCollectorImpl(stockIdList);
  }

  public void testParseIndependentPerformance() {
    List<CorporatePerformance> cpList = coll.parseIndependentPerformance(8411);
    for(CorporatePerformance cp : cpList) {
      System.out.println(cp);
      assertTrue(cp != null);
    }
  }

  public void testParseConsolidatePerformance() {
    List<CorporatePerformance> cpList = coll.parseConsolidatePerformance(8411);
    for(CorporatePerformance cp : cpList) {
      System.out.println(cp);
      assertTrue(cp != null);
    }
  }

  public void testAppend() {
    Map<String, CorporatePerformance> m = new HashMap<String, CorporatePerformance>();
    try {
      coll.append(m);
    } catch(Exception e) {
      e.printStackTrace();
    }
    assertTrue(m.size() > 0);
    for(String k : m.keySet()) {
      System.out.println(k + ": " + m.get(k));
    }
  }

}
