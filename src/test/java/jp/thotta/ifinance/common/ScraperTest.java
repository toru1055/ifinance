package jp.thotta.ifinance.common;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ScraperTest extends TestCase {
  public void testGet() {
    String url = "http://profile.yahoo.co.jp/independent/3787";
    Document d = Scraper.get(url);
    Elements records = d.select("table.yjMt").select("tr");
    assertEquals(records.get(0).select("td").get(1).text(), "前期");

    url = "http://unknown.host.jp/";
    Document d2 = Scraper.get(url);
    assertEquals(d2, null);
  }
}
