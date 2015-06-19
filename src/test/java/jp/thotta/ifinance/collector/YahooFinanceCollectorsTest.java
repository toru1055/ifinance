package jp.thotta.ifinance.collector.yj_finance;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import jp.thotta.ifinance.model.CorporatePerformance;

/**
 * Unit test for YahooFinanceCollectors.
 */
public class YahooFinanceCollectorsTest 
  extends TestCase {
  Map<String, CorporatePerformance> m;

  public void setUp() {
    m = new HashMap<String, CorporatePerformance>();
  }

  /**
   * SalesAmountCollectorImplのテスト.
   */
  public void testSalesAmountCollectorImpl() {
    SalesAmountCollectorImpl sc 
      = new SalesAmountCollectorImpl();
    sc.setStartPage(71);
    try {
      sc.append(m);
    } catch(IOException e) {
      e.printStackTrace();
    }
    for(String k : m.keySet()) {
      CorporatePerformance cp = m.get(k);
      System.out.println(cp);
    }
  }

  /**
   * Test for OperatingProfitCollectorImpl.
   */
  public void testOperatingProfitCollectorImpl() {
    OperatingProfitCollectorImpl oc
      = new OperatingProfitCollectorImpl();
    oc.setStartPage(71);
    try {
      oc.append(m);
    } catch(IOException e) {
      e.printStackTrace();
    }
    for(String k : m.keySet()) {
      CorporatePerformance cp = m.get(k);
      System.out.println(cp);
    }
  }

  /**
   * Test for OrdinaryProfitCollectorImpl.
   */
  public void testOrdinaryProfitCollectorImpl() {
    OrdinaryProfitCollectorImpl oc2
      = new OrdinaryProfitCollectorImpl();
    oc2.setStartPage(71);
    try {
      oc2.append(m);
    } catch(IOException e) {
      e.printStackTrace();
    }
    for(String k : m.keySet()) {
      CorporatePerformance cp = m.get(k);
      System.out.println(cp);
    }
  }

  /**
   * Test for NetProfitCollectorImpl.
   */
  public void testNetProfitCollectorImpl() {
    NetProfitCollectorImpl np
      = new NetProfitCollectorImpl();
    np.setStartPage(71);
    try {
      np.append(m);
    } catch(IOException e) {
      e.printStackTrace();
    }
    for(String k : m.keySet()) {
      CorporatePerformance cp = m.get(k);
      System.out.println(cp);
    }
  }

}
