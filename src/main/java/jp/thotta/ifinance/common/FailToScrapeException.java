package jp.thotta.ifinance.common;

public class FailToScrapeException extends Exception {
  public FailToScrapeException(String str) {
    super(str);
  }
}
