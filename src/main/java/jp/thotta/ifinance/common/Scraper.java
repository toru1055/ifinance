package jp.thotta.ifinance.common;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.BufferedReader;
import java.net.URL;

import java.net.UnknownHostException;

public class Scraper {
  private static final int SLEEP_TIME = 1000;
  private static final int RETRY_NUM = 2;

  public static Document get(String url) {
    //System.setProperty("javax.net.ssl.trustStore", "sslkey/sn.jks");
    //System.out.println(System.getProperty("javax.net.ssl.trustStore"));
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

  public static Document getHtml(String url, int timeout)
    throws FailToScrapeException {
    int retryNum = 0;
    while(retryNum < RETRY_NUM) {
      try {
        sleep();
        String retryMsg = "";
        if(retryNum > 0) {
          retryMsg = "Retrying[" + retryNum + "], ";
        }
        System.out.println(retryMsg + "[Scraper.get] " + url);
        Document d = Jsoup.connect(url).validateTLSCertificates(false).timeout(timeout).get();
        return d;
      } catch(UnknownHostException e) {
        System.out.println(
            "java.net.UnknownHostException: " + e.getMessage());
      } catch(Exception e) {
        e.printStackTrace();
      }
      retryNum++;
    }
    throw new FailToScrapeException("target url: " + url);
  }

  public static Document getHtml(String url) throws FailToScrapeException {
    return getHtml(url, 3000);
  }

  public static Document getXml(String url) throws FailToScrapeException {
    int retryNum = 0;
    while(retryNum < RETRY_NUM) {
      try {
        sleep();
        String retryMsg = "";
        if(retryNum > 0) {
          retryMsg = "Retrying[" + retryNum + "], ";
        }
        System.out.println(retryMsg + "[Scraper.get] " + url);
        Document d = Jsoup.connect(url).validateTLSCertificates(false).parser(Parser.xmlParser()).get();
        return d;
      } catch(UnknownHostException e) {
        System.out.println(
            "java.net.UnknownHostException: " + e.getMessage());
      } catch(Exception e) {
        e.printStackTrace();
      }
      retryNum++;
    }
    throw new FailToScrapeException("target url: " + url);
  }

  public static Document getJs(String url) throws FailToScrapeException {
    int retryNum = 0;
    while(retryNum < RETRY_NUM) {
      try {
        String retryMsg = "";
        if(retryNum > 0) {
          retryMsg = "Retrying[" + retryNum + "], ";
        }
        System.out.println(retryMsg + "[Scraper.getJs] " + url);
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        //webClient.getOptions().setThrowExceptionOnScriptError(false);
        HtmlPage page = webClient.getPage(url);
        String pageAsXml = page.asXml();
        Document doc = Jsoup.parse(pageAsXml);
        return doc;
      } catch(Exception e) {
        e.printStackTrace();
      }
      retryNum++;
    }
    throw new FailToScrapeException("target url: " + url);
  }

  public static String getRaw(String url) throws FailToScrapeException {
    int retryNum = 0;
    while(retryNum < RETRY_NUM) {
      try {
        String retryMsg = "";
        if(retryNum > 0) {
          retryMsg = "Retrying[" + retryNum + "], ";
        }
        System.out.println(retryMsg + "[Scraper.getJson] " + url);
        InputStream input = new URL(url).openStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
        String line;
        String rawText = "";
        int counter = 0;
        while((line = reader.readLine()) != null) {
          rawText += line + "\n";
        }
        return rawText;
      } catch(Exception e) {
        e.printStackTrace();
      }
      retryNum++;
    }
    throw new FailToScrapeException("target url: " + url);
  }

  private static void sleep() {
    try {
      Thread.sleep(SLEEP_TIME);
    } catch(InterruptedException e) { }
  }
}
