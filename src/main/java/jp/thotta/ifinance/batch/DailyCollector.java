package jp.thotta.ifinance.batch;

import jp.thotta.ifinance.model.CorporatePerformance;
import jp.thotta.ifinance.model.DailyStockPrice;
import jp.thotta.ifinance.model.Database;
import jp.thotta.ifinance.collector.FinancialAmountCollector;
import jp.thotta.ifinance.collector.StockPriceCollector;
import jp.thotta.ifinance.collector.yj_finance.*;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 日次で各種データ・ソースからデータを収集するメソッド群.
 */
public class DailyCollector {
  static StockPriceCollector stockPriceCollector 
    = new StockPriceCollectorImpl();
  static FinancialAmountCollector salesAmountCollector 
    = new SalesAmountCollectorImpl();
  static FinancialAmountCollector operatingProfitCollector 
    = new OperatingProfitCollectorImpl();
  static FinancialAmountCollector ordinaryProfitCollector 
    = new OrdinaryProfitCollectorImpl();
  static FinancialAmountCollector netProfitCollector 
    = new NetProfitCollectorImpl();
  static FinancialAmountCollector totalAssetsCollector
    = new TotalAssetsCollectorImpl();
  static FinancialAmountCollector debtWithInterestCollector
    = new DebtWithInterestCollectorImpl();
  static FinancialAmountCollector capitalFundCollector
    = new CapitalFundCollectorImpl();

  /** 日次で株価を取得する.
   */
  public static void collectDailyStockPrice() {
    try {
      Connection conn = Database.getConnection();
      stockPriceCollector.appendDb(conn);
    } catch(Exception e) {
      e.printStackTrace();
    } finally {
      try {
        Database.closeConnection();
      } catch(SQLException e) {
        e.printStackTrace();
      }
    }
  }

  public static void collectCorporatePerformance() {
    try {
      Connection conn = Database.getConnection();
      salesAmountCollector.appendDb(conn);
      operatingProfitCollector.appendDb(conn);
      ordinaryProfitCollector.appendDb(conn);
      netProfitCollector.appendDb(conn);
      totalAssetsCollector.appendDb(conn);
      debtWithInterestCollector.appendDb(conn);
      capitalFundCollector.appendDb(conn);
    } catch(Exception e) {
      e.printStackTrace();
    } finally {
      try {
        Database.closeConnection();
      } catch(SQLException e) {
        e.printStackTrace();
      }
    }
  }

}
