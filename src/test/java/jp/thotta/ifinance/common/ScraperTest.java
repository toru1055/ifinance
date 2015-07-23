package jp.thotta.ifinance.common;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;

import junit.framework.TestCase;

import jp.thotta.ifinance.collector.yj_finance.TextParser;

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

  public void testProfile() {
    String url = "http://profile.yahoo.co.jp/fundamental/1954";
    Document d = Scraper.get(url);
    Elements records = d.select("table").select("tr.yjMt");
    System.out.println(records.get(4).select("td").get(1).text());
    System.out.println(d.select("div.selectFinTitle").select("h1 > strong.yjL").text());
  }
}
