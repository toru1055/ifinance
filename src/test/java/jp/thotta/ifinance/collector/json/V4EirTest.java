package jp.thotta.ifinance.collector.json;

import junit.framework.TestCase;
import com.google.gson.Gson;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.BufferedReader;
import java.net.URL;

import jp.thotta.ifinance.common.Scraper;

public class V4EirTest extends TestCase {
  String url = "http://v4.eir-parts.net/V4Public/EIR/3782/ja/announcement/announcement_1.js";

  public void testGetRaw() {
    try {
      String raw = Scraper.getRaw(url);
      assertTrue(raw.length() > 0);
    } catch(Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

}
