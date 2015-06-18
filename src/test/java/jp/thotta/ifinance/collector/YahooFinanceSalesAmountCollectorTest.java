package jp.thotta.ifinance.collector;

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
 * Unit test for YahooFinanceSalesAmountCollector.
 */
public class YahooFinanceSalesAmountCollectorTest 
  extends TestCase {

  /**
   * 正常系のテスト.
   */
  public void testNormal() {
    YahooFinanceSalesAmountCollector c 
      = new YahooFinanceSalesAmountCollector();
    c.setStartPage(71);
    Map<String, CorporatePerformance> m 
      = new HashMap<String, CorporatePerformance>();
    try {
      c.appendSalesAmounts(m);
    } catch(IOException e) {
      e.printStackTrace();
    }
    for(String k : m.keySet()) {
      CorporatePerformance cp = m.get(k);
      System.out.println(cp);
    }
  }
}
