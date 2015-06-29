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
 * 日次で各種データ・ソースからデータを収集する.
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

  /** 
   * 日次で株価を取得する.
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

  /**
   * 日次で企業の決算情報を取得する.
   */
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

  /**
   * 日次で実行するデータ収集バッチ.
   * TODO: 実装する
   * TODO: sampl.dbを変える. Test用と本番用で切り替えられるように.
   */
  public static void main(String[] args) {
  }

}
