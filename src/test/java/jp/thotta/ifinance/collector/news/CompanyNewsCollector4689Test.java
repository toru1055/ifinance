package jp.thotta.ifinance.collector.news;

import java.util.List;
import java.util.ArrayList;
import junit.framework.TestCase;

import jp.thotta.ifinance.model.CompanyNews;

public class CompanyNewsCollector4689Test extends TestCase {

  public void testParsePRList() {
    CompanyNewsCollector4689 coll = new CompanyNewsCollector4689();
    List<CompanyNews> prList = new ArrayList<CompanyNews>();
    coll.parsePRList(prList);
    assertTrue(prList.size() > 0);
    for(CompanyNews pr : prList) {
      assertTrue(pr.hasEnough());
    }
  }

  public void testParseIRList() {
    CompanyNewsCollector4689 coll = new CompanyNewsCollector4689();
    List<CompanyNews> prList = new ArrayList<CompanyNews>();
    coll.parseIRList(prList);
    System.out.println(prList);
  }
}
