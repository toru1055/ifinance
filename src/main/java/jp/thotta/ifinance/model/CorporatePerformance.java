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
   * 同じキーのレコードがDB内に存在するかをチェック.
   * @param st SQL実行オブジェクト
   */
  public boolean exists(Statement st) throws SQLException {
    String sql = String.format(
        "SELECT * FROM corporate_performance " +
        "WHERE stock_id = %d " + 
        "AND settling_year = %d " + 
        "AND settling_month = %d " +
        "LIMIT 1", 
        this.stockId, this.settlingYear, this.settlingMonth);
    ResultSet rs = st.executeQuery(sql);
    return rs.next();
  }

  /**
   * 同じキーのレコードをDBから取得.
   * @param st SQL実行オブジェクト
   */
  public void readDb(Statement st) throws SQLException {
    String sql = String.format(
        "SELECT * FROM corporate_performance " +
        "WHERE stock_id = %d " + 
        "AND settling_year = %d " +
        "AND settling_month = %d " + 
        "LIMIT 1", 
        this.stockId, this.settlingYear, this.settlingMonth);
    System.out.println(sql);
    ResultSet rs = st.executeQuery(sql);
    if(rs.next()) {
      long lSalesAmount = rs.getLong("sales_amount");
      long lOperatingProfit = rs.getLong("operating_profit");
      long lOrdinaryProfit = rs.getLong("ordinary_profit");
      long lNetProfit = rs.getLong("net_profit");
      long lTotalAssets = rs.getLong("total_assets");
      long lDebtWithInterest = rs.getLong("debt_with_interest");
      long lCapitalFund = rs.getLong("capital_fund");
      if(lSalesAmount != 0) { this.salesAmount = lSalesAmount; }
      if(lOperatingProfit != 0) { this.operatingProfit = lOperatingProfit; }
      if(lOrdinaryProfit != 0) { this.ordinaryProfit = lOrdinaryProfit; }
      if(lNetProfit != 0) { this.netProfit = lNetProfit; }
      if(lTotalAssets != 0) { this.totalAssets = lTotalAssets; }
      if(lDebtWithInterest != 0) { this.debtWithInterest = lDebtWithInterest; }
      if(lCapitalFund != 0) { this.capitalFund = lCapitalFund; }
    }
  }

  /**
   * このインスタンスをdbにインサート.
   * @param st SQL実行オブジェクト
   */
  public void insert(Statement st) throws SQLException {
    String sql = String.format(
        "INSERT INTO corporate_performance(" + 
        "stock_id, settling_year, settling_month," +
        "sales_amount, operating_profit, ordinary_profit, net_profit, " + 
        "total_assets, debt_with_interest, capital_fund" + 
        ") values(%4d, %4d, %2d, %d, %d, %d, %d, %d, %d, %d)",
        stockId, settlingYear, settlingMonth,
        salesAmount, operatingProfit, ordinaryProfit, netProfit,
        totalAssets, debtWithInterest, capitalFund);
    System.out.println(sql);
    st.executeUpdate(sql);
  }

  /**
   * 同じキーのレコードをデータ更新.
   * @param st SQL実行オブジェクト
   */
  public void update(Statement st) throws SQLException {
    int updateColumn = 0;
    String sql = "UPDATE corporate_performance SET ";
    if(salesAmount != 0) {
      updateColumn++;
      sql += String.format("sales_amount = %d, ", salesAmount);
    }
    if(operatingProfit != 0) {
      updateColumn++;
      sql += String.format("operating_profit = %d, ", operatingProfit);
    }
    if(ordinaryProfit != 0) {
      updateColumn++;
      sql += String.format("ordinary_profit = %d, ", ordinaryProfit);
    }
    if(netProfit != 0) {
      updateColumn++;
      sql += String.format("net_profit = %d, ", netProfit);
    }
    if(totalAssets != 0) {
      updateColumn++;
      sql += String.format("total_assets = %d, ", totalAssets);
    }
    if(debtWithInterest != 0) {
      updateColumn++;
      sql += String.format("debt_with_interest = %d, ", debtWithInterest);
    }
    if(capitalFund != 0) {
      updateColumn++;
      sql += String.format("capital_fund = %d, ", capitalFund);
    }
    sql += "id = id ";
    sql += String.format(
        "WHERE stock_id = %d " +
        "AND settling_year = %d " +
        "AND settling_month = %d",
        stockId, settlingYear, settlingMonth);
    if(updateColumn > 0) {
      System.out.println(sql);
      st.executeUpdate(sql);
    }
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
    System.out.println(sql);
    c.createStatement().executeUpdate(sql);
  }

  /**
   * 企業業績テーブルを削除.
   * @param c dbのコネクション
   */
  public static void dropTable(Connection c) 
    throws SQLException {
    String sql = "DROP TABLE IF EXISTS corporate_performance";
    System.out.println(sql);
    c.createStatement().executeUpdate(sql);
  }

  /**
   * Mapのデータを全てDBにInsertする.
   * @param m モデルのmap
   * @param c dbのコネクション
   */
  public static void insertMap(Map<String, CorporatePerformance> m, Connection c) 
    throws SQLException {
    Statement st = c.createStatement();
    for(String k : m.keySet()) {
      CorporatePerformance v = m.get(k);
      v.insert(st);
    }
  }

  /**
   * MapのデータでDBをUpdateする.
   * @param m モデルのmap
   * @param c dbのコネクション
   */
  public static void updateMap(Map<String, CorporatePerformance> m, Connection c) 
    throws SQLException {
    Statement st = c.createStatement();
    for(String k : m.keySet()) {
      CorporatePerformance v = m.get(k);
      if(v.exists(st)) {
        v.update(st);
      } else {
        v.insert(st);
      }
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
