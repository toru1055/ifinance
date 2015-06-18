package jp.thotta.ifinance.collector;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;

import java.net.UnknownHostException;

/**
 * Y!FinanceのランキングページをIterativeに取得.
 *
 * @author toru1055
 */
public class YahooFinancePageIterator {
  private static final String YAHOO_FINANCE_URL 
    = "http://info.finance.yahoo.co.jp/ranking/";
  private static final int SLEEP_TIME = 1000;
  private static final int RETRY_NUM = 2;

  private int kd;
  private int p;
  private String targetUrl;
  private Document document;

  /**
   * コンストラクタ.
   *
   * @param kd ランキングの種別ID
   */
  public YahooFinancePageIterator(int kd) {
    this.kd = kd;
    this.targetUrl = YAHOO_FINANCE_URL;
    this.p = 1;
  }

  /**
   * ターゲットURLをセット.
   *
   * @param url ターゲットURL
   */
  public void setTargetUrl(String url) {
    this.targetUrl = url;
  }
  
  /**
   * 現在のページをセット.
   *
   * @param page ページID
   */
  public void setCurrentPage(int page) {
    this.p = page;
  }

  /**
   * 次のページがあるかをチェック.
   *
   * @return <code>true</code> on success
   */
  public boolean hasNext() {
    document = getJsoupWithRetry(targetUrl);
    if(document != null) {
      Element e = document.select("table.rankingTable")
        .select("tr.rankingTabledata.yjM").first();
      if(e != null) {
        return true;
      }
    }
    document = null;
    return false;
  }

  private Document getJsoupWithRetry(String url) {
    int restRetryNum = RETRY_NUM;
    while(restRetryNum-- > 0) {
      try {
        sleep();
        Document d = Jsoup.connect(url)
          .data("kd", String.valueOf(kd))
          .data("tm", "d")
          .data("vl", "a")
          .data("mk", "1")
          .data("p", String.valueOf(p))
          .get();
        return d;
      } catch(UnknownHostException e) {
        System.out.println("java.net.UnknownHostException: " + e.getMessage());
      } catch(Exception e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  /**
   * 次のページを取得.
   *
   * @return 次のページの{@link Document}
   */
  public Document next() {
    p++;
    return document;
  }

  private void sleep() {
    try {
      Thread.sleep(SLEEP_TIME);
    } catch(InterruptedException e) { }
  }
}
