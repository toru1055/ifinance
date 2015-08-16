package jp.thotta.ifinance.collector;

import jp.thotta.ifinance.model.CompanyNews;
import jp.thotta.ifinance.common.FailToScrapeException;
import jp.thotta.ifinance.common.ParseNewsPageException;
import java.util.List;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 企業ニュースを取得するInterface.
 * @author toru1055
 */
public interface CompanyNewsCollector {
  /**
   * データ収集し、企業ニュースListに情報を追加.
   * @param newsList 企業ニュースリスト
   */
  public void append(List<CompanyNews> newsList) throws FailToScrapeException, ParseNewsPageException;

  /**
   * データを収集し、DBの企業ニューステーブルに追加.
   */
  public void appendDb(Connection conn) throws SQLException, FailToScrapeException, ParseNewsPageException;
}
