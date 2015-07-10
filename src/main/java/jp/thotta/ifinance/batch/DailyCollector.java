package jp.thotta.ifinance.batch;

import jp.thotta.ifinance.model.CorporatePerformance;
import jp.thotta.ifinance.model.DailyStockPrice;
import jp.thotta.ifinance.model.PerformanceForecast;
import jp.thotta.ifinance.model.Database;
import jp.thotta.ifinance.collector.FinancialAmountCollector;
import jp.thotta.ifinance.collector.StockPriceCollector;
import jp.thotta.ifinance.collector.ForecastPerformanceCollector;
import jp.thotta.ifinance.collector.yj_finance.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.io.IOException;

/**
 * 日次で各種データ・ソースからデータを収集する.
 */
public class DailyCollector {
  Connection conn;
  StockPriceCollector stockPriceCollector 
    = new StockPriceCollectorImpl();
  FinancialAmountCollector salesAmountCollector 
    = new SalesAmountCollectorImpl();
  FinancialAmountCollector operatingProfitCollector 
    = new OperatingProfitCollectorImpl();
  FinancialAmountCollector ordinaryProfitCollector 
    = new OrdinaryProfitCollectorImpl();
  FinancialAmountCollector netProfitCollector 
    = new NetProfitCollectorImpl();
  FinancialAmountCollector totalAssetsCollector
    = new TotalAssetsCollectorImpl();
  FinancialAmountCollector debtWithInterestCollector
    = new DebtWithInterestCollectorImpl();
  FinancialAmountCollector capitalFundCollector
    = new CapitalFundCollectorImpl();
  FinancialAmountCollector ownedCapitalCollector
    = new OwnedCapitalCollectorImpl();
  ForecastPerformanceCollector dividendCollector
    = new ForecastDividendCollectorImpl();

  public DailyCollector(Connection c) {
    this.conn = c;
  }

  public void collect() throws SQLException, ParseException, IOException {
    // collect DailyStockPrice
    stockPriceCollector.appendDb(conn);
    // collect CorporatePerformance
    salesAmountCollector.appendDb(conn);
    operatingProfitCollector.appendDb(conn);
    ordinaryProfitCollector.appendDb(conn);
    netProfitCollector.appendDb(conn);
    totalAssetsCollector.appendDb(conn);
    debtWithInterestCollector.appendDb(conn);
    capitalFundCollector.appendDb(conn);
    ownedCapitalCollector.appendDb(conn);
    // collect PerformanceForecast
    dividendCollector.appendDb(conn);
  }

  /**
   * 日次で実行するデータ収集バッチ.
   */
  public static void main(String[] args) {
    try {
      Connection conn = Database.getConnection();
      DailyCollector collector = new DailyCollector(conn);
      collector.collect();
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
