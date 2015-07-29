package jp.thotta.ifinance.batch;

import jp.thotta.ifinance.model.CorporatePerformance;
import jp.thotta.ifinance.model.DailyStockPrice;
import jp.thotta.ifinance.model.PerformanceForecast;
import jp.thotta.ifinance.model.CompanyProfile;
import jp.thotta.ifinance.model.Database;
import jp.thotta.ifinance.collector.FinancialAmountCollector;
import jp.thotta.ifinance.collector.StockPriceCollector;
import jp.thotta.ifinance.collector.ForecastPerformanceCollector;
import jp.thotta.ifinance.collector.CompanyProfileCollector;
import jp.thotta.ifinance.collector.yj_finance.*;
import jp.thotta.ifinance.collector.kmonos.SmallBusinessCategoryCollectorImpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.io.IOException;
import java.util.List;

/**
 * 日次で各種データ・ソースからデータを収集する.
 */
public class DailyCollector {
  Connection conn;
  StockPriceCollector stockPriceCollector 
    = new StockPriceCollectorImpl();
  ForecastPerformanceCollector dividendCollector
    = new ForecastDividendCollectorImpl();
  CompanyProfileCollector smallCategoryCollector
    = new SmallBusinessCategoryCollectorImpl();

  public DailyCollector(Connection c) {
    this.conn = c;
  }

  public void collect() throws SQLException, ParseException, IOException {
    // collect DailyStockPrice
    stockPriceCollector.appendDb(conn);
    List<Integer> stockIds = DailyStockPrice.selectStockIds(conn);
    // collect PerformanceForecast
    dividendCollector.appendDb(conn);
    // collect CorporatePerformance
    FinancialAmountCollector baseCollector
      = new BasePerformanceCollectorImpl(stockIds);
    baseCollector.appendDb(conn);
    // collect CompanyProfile
    CompanyProfileCollector baseProfileCollector
      = new BaseProfileCollectorImpl(stockIds);
    baseProfileCollector.appendDb(conn);
    smallCategoryCollector.appendDb(conn);
  }

  /**
   * companyProfileだけ収集実行
   */
  public void collectCompanyProfile() 
    throws SQLException, ParseException, IOException {
    List<Integer> stockIds = DailyStockPrice.selectStockIds(conn);
    // collect CompanyProfile
    CompanyProfileCollector baseProfileCollector
      = new BaseProfileCollectorImpl(stockIds);
    baseProfileCollector.appendDb(conn);
    smallCategoryCollector.appendDb(conn);
  }

  /**
   * 日次で実行するデータ収集バッチ.
   */
  public static void main(String[] args) {
    try {
      Connection conn = Database.getConnection();
      DailyCollector collector = new DailyCollector(conn);
      if(args.length == 0) {
        collector.collect();
      } else {
        String command = args[0];
        if(command.equals("CompanyProfile")) {
          collector.collectCompanyProfile();
        } else {
          System.out.println("Syntax error: command = " + command);
          System.exit(1);
        }
      }
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
