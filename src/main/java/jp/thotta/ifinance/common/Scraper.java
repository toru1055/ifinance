package jp.thotta.ifinance.common;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;

import java.net.UnknownHostException;

public class Scraper {
  private static final int SLEEP_TIME = 1000;
  private static final int RETRY_NUM = 2;

  public static Document get(String url) {
    System.setProperty("javax.net.ssl.trustStore",
        "/home/thotta/git/ifinance/sslkey/sn.jks");
    int retryNum = 0;
    while(retryNum < RETRY_NUM) {
      try {
        sleep();
        String retryMsg = "";
        if(retryNum > 0) {
          retryMsg = "Retrying[" + retryNum + "], ";
        }
        System.out.println(retryMsg + "[Scraper.get] " + url);
        Document d = Jsoup.connect(url).get();
        return d;
      } catch(UnknownHostException e) {
        System.out.println(
            "java.net.UnknownHostException: " + e.getMessage());
      } catch(Exception e) {
        e.printStackTrace();
      }
      retryNum++;
    }
    return null;
  }

  private static void sleep() {
    try {
      Thread.sleep(SLEEP_TIME);
    } catch(InterruptedException e) { }
  }
}
