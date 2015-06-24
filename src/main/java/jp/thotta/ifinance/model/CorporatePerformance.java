package jp.thotta.ifinance.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.HashMap;

/**
 * 通期の企業業績クラス.
 * {@link DBModel}を継承し、業績テーブルとのアクセスも持つ
 *
 * @author toru1055
 */
public class CorporatePerformance implements DBModel {
  public int stockId; //pk
  public int settlingYear; // pk
  public int settlingMonth; // pk
  public long salesAmount;
  public long operatingProfit;
  public long ordinaryProfit;
  public long netProfit;
  public long totalAssets;
  public long debtWithInterest;
  public long capitalFund;

  public CorporatePerformance(
      int stockId, 
      int settlingYear, 
      int settlingMonth) {
    this.stockId = stockId;
    this.settlingYear = settlingYear;
    this.settlingMonth = settlingMonth;
  }

  /**
   * Map用のキー取得.
   *
   * @return キーになる文字列
   */
  public String getKeyString() {
    return String.format("%4d,%4d/%02d", 
        stockId, settlingYear, settlingMonth);
  }

  public String toString() {
    String s = String.format(
        "code[%4d], " +
        "YM[%4d/%02d], " +
        "salesAmount[%d], " +
        "operatingProfit[%d], " +
        "ordinaryProfit[%d], " +
        "netProfit[%d], " +
        "totalAssets[%d], " +
        "debtWithInterest[%d], " +
        "capitalFund[%d]",
        stockId,
        settlingYear,
        settlingMonth,
        salesAmount,
        operatingProfit,
        ordinaryProfit,
        netProfit,
        totalAssets,
        debtWithInterest,
        capitalFund);
    return s;
  }

  /**
   * 企業業績テーブル作成.
   * @param c dbのコネクション
   */
  public static void createTable(Connection c) 
    throws SQLException {
    String sql = 
      "CREATE TABLE corporate_performance(" +
        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "stock_id INT NOT NULL, " +
        "settling_year INT NOT NULL, " +
        "settling_month INT NOT NULL, " +
        "sales_amount BIGINT, " +
        "operating_profit BIGINT, " +
        "ordinary_profit BIGINT, " +
        "net_profit BIGINT, " +
        "total_assets BIGINT, " +
        "debt_with_interest BIGINT, " +
        "capital_fund BIGINT, " +
        "UNIQUE(stock_id, settling_year, settling_month)" +
      ")";
    c.createStatement().executeUpdate(sql);
  }

  /**
   * 企業業績テーブルを削除.
   * @param c dbのコネクション
   */
  public static void dropTable(Connection c) 
    throws SQLException {
    String sql = "DROP TABLE IF EXISTS corporate_performance";
    c.createStatement().executeUpdate(sql);
  }

  /**
   * Mapのデータを全てDBにInsertする.
   * @param m モデルのmap
   * @param c dbのコネクション
   */
  public static void insertMap(Map<String, CorporatePerformance> m, Connection c) 
    throws SQLException {
    String sqlFormat = 
      "INSERT INTO corporate_performance(" +
        "stock_id, settling_year, settling_month," +
        "sales_amount, operating_profit, ordinary_profit, net_profit, " +
        "total_assets, debt_with_interest, capital_fund" +
      ") values(%4d, %4d, %2d, %d, %d, %d, %d, %d, %d, %d)";
    Statement st = c.createStatement();
    for(String k : m.keySet()) {
      CorporatePerformance v = m.get(k);
      st.executeUpdate(String.format(sqlFormat,
            v.stockId, v.settlingYear, v.settlingMonth,
            v.salesAmount, v.operatingProfit, v.ordinaryProfit, v.netProfit,
            v.totalAssets, v.debtWithInterest, v.capitalFund));
    }
  }

  /**
   * テーブル内の全てのレコードをMapにして返す.
   * @param c dbのコネクション
   */
  public static Map<String, CorporatePerformance> selectAll(Connection c)
    throws SQLException {
    Map<String, CorporatePerformance> m =
      new HashMap<String, CorporatePerformance>();
    String sql = "SELECT * FROM corporate_performance";
    ResultSet rs = c.createStatement().executeQuery(sql);
    while(rs.next()) {
      int stockId = rs.getInt("stock_id");
      int settlingYear = rs.getInt("settling_year");
      int settlingMonth = rs.getInt("settling_month");
      CorporatePerformance v = 
        new CorporatePerformance(stockId, settlingYear, settlingMonth);
      v.salesAmount = rs.getLong("sales_amount");
      v.operatingProfit = rs.getLong("operating_profit");
      v.ordinaryProfit = rs.getLong("ordinary_profit");
      v.netProfit = rs.getLong("net_profit");
      v.totalAssets = rs.getLong("total_assets");
      v.debtWithInterest = rs.getLong("debt_with_interest");
      v.capitalFund = rs.getLong("capital_fund");
      m.put(v.getKeyString(), v);
    }
    return m;
  }
}
