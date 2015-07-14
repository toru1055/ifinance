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
 * TODO: [素性追加]
 * 素性追加時の修正箇所は下記のdiffを参考にする
 *  https://github.com/toru1055/ifinance/commit/a58d9a992fe0e9ee94648d3d1824e9f40491f4a8
 * 配当金合計 = 1株配当×株数
 * http://jp.kabumap.com/servlets/kabumap/Action?SRC=basic/factor/base&codetext=8594
 * ここ参考にする: 例(成長性)
 * １．ROE 
 * ２．売上高成長率 
 * ３．今期経常利益変化率 
 * TODO: 会社予想の配当金額みたいな値は、別テーブル・別クラスを作って管理するのが良い。そもそもキーが違う。
 * @author toru1055
 */
public class CorporatePerformance implements DBModel {
  public int stockId; //pk
  public int settlingYear; // pk
  public int settlingMonth; // pk
  public Long salesAmount;
  public Long operatingProfit;
  public Long ordinaryProfit;
  public Long netProfit;
  public Long totalAssets;
  public Long debtWithInterest;
  public Long capitalFund;
  public Long ownedCapital;
  public Double dividend;

  public CorporatePerformance(
      int stockId, 
      int settlingYear, 
      int settlingMonth) {
    this.stockId = stockId;
    this.settlingYear = settlingYear;
    this.settlingMonth = settlingMonth;
  }

  /**
   * 全ての要素が取得できたか.
   */
  public boolean isAllInclude() {
    return stockId != 0 && 
      salesAmount != 0 &&
//      operatingProfit != 0 &&
      ordinaryProfit != 0 &&
      netProfit != 0 &&
      totalAssets != 0 &&
      capitalFund != 0 &&
      ownedCapital != 0;
  }

  /**
   * 自己資本比率を計算して返す.
   */
  public double ownedCapitalRatio() {
    if(totalAssets <= 0) {
      if(ownedCapital == 0) {
        return 0.0;
      } else {
        return 1.0;
      }
    }
    return (double)ownedCapital / totalAssets;
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

  /**
   * Join用のキー取得.
   */
  public String getJoinKey() {
    return String.format("%4d", stockId);
  }

  @Override
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
        "capitalFund[%d], " +
        "ownedCapital[%d], " +
        "dividend[%.4g]",
        stockId,
        settlingYear,
        settlingMonth,
        salesAmount,
        operatingProfit,
        ordinaryProfit,
        netProfit,
        totalAssets,
        debtWithInterest,
        capitalFund,
        ownedCapital,
        dividend);
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
    //System.out.println(sql);
    ResultSet rs = st.executeQuery(sql);
    if(rs.next()) {
      long lSalesAmount = rs.getLong("sales_amount");
      long lOperatingProfit = rs.getLong("operating_profit");
      long lOrdinaryProfit = rs.getLong("ordinary_profit");
      long lNetProfit = rs.getLong("net_profit");
      long lTotalAssets = rs.getLong("total_assets");
      long lDebtWithInterest = rs.getLong("debt_with_interest");
      long lCapitalFund = rs.getLong("capital_fund");
      long lOwnedCapital = rs.getLong("owned_capital");
      double lDividend = rs.getDouble("dividend");
      if(lSalesAmount != 0) { this.salesAmount = lSalesAmount; }
      if(lOperatingProfit != 0) { this.operatingProfit = lOperatingProfit; }
      if(lOrdinaryProfit != 0) { this.ordinaryProfit = lOrdinaryProfit; }
      if(lNetProfit != 0) { this.netProfit = lNetProfit; }
      if(lTotalAssets != 0) { this.totalAssets = lTotalAssets; }
      if(lDebtWithInterest != 0) { this.debtWithInterest = lDebtWithInterest; }
      if(lCapitalFund != 0) { this.capitalFund = lCapitalFund; }
      if(lOwnedCapital != 0) { this.ownedCapital = lOwnedCapital; }
      if(lDividend != 0) { this.dividend = lDividend; }
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
        "total_assets, debt_with_interest, capital_fund, " + 
        "owned_capital, dividend" +
        ") values(%4d, %4d, %2d, %d, %d, %d, %d, %d, %d, %d, %d, %f)",
        stockId, settlingYear, settlingMonth,
        salesAmount, operatingProfit, ordinaryProfit, netProfit,
        totalAssets, debtWithInterest, capitalFund, ownedCapital, 
        dividend);
    //System.out.println(sql);
    st.executeUpdate(sql);
  }

  /**
   * 同じキーのレコードをデータ更新.
   * @param st SQL実行オブジェクト
   */
  public void update(Statement st) throws SQLException {
    int updateColumn = 0;
    String sql = "UPDATE corporate_performance SET ";
    if(salesAmount != null) {
      updateColumn++;
      sql += String.format("sales_amount = %d, ", salesAmount);
    }
    if(operatingProfit != null) {
      updateColumn++;
      sql += String.format("operating_profit = %d, ", operatingProfit);
    }
    if(ordinaryProfit != null) {
      updateColumn++;
      sql += String.format("ordinary_profit = %d, ", ordinaryProfit);
    }
    if(netProfit != null) {
      updateColumn++;
      sql += String.format("net_profit = %d, ", netProfit);
    }
    if(totalAssets != null) {
      updateColumn++;
      sql += String.format("total_assets = %d, ", totalAssets);
    }
    if(debtWithInterest != null) {
      updateColumn++;
      sql += String.format("debt_with_interest = %d, ", debtWithInterest);
    }
    if(capitalFund != null) {
      updateColumn++;
      sql += String.format("capital_fund = %d, ", capitalFund);
    }
    if(ownedCapital != null) {
      updateColumn++;
      sql += String.format("owned_capital = %d, ", ownedCapital);
    }
    if(dividend != null) {
      updateColumn++;
      sql += String.format("dividend = %f, ", dividend);
    }
    sql += "id = id ";
    sql += String.format(
        "WHERE stock_id = %d " +
        "AND settling_year = %d " +
        "AND settling_month = %d",
        stockId, settlingYear, settlingMonth);
    if(updateColumn > 0) {
      //System.out.println(sql);
      st.executeUpdate(sql);
    }
  }

  @Override
  public boolean equals(Object o) {
    return o.toString().equals(this.toString());
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
        "owned_capital BIGINT, " +
        "dividend DOUBLE, " +
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
    String sql = "SELECT * FROM corporate_performance";
    ResultSet rs = c.createStatement().executeQuery(sql);
    return parseResultSet(rs);
  }

  /**
   * 各銘柄ごとに、最新のデータを取得して返す.
   * @param c dbのコネクション
   */
  public static Map<String, CorporatePerformance> selectLatests(Connection c)
    throws SQLException {
    String sql = 
      "SELECT cp.* " + 
      "FROM corporate_performance AS cp JOIN ( " +
      "SELECT stock_id, MAX(settling_year) AS settling_year " +
      "FROM corporate_performance GROUP BY stock_id " +
      ") AS years " +
      "ON cp.stock_id = years.stock_id AND cp.settling_year = years.settling_year";
    ResultSet rs = c.createStatement().executeQuery(sql);
    Map<String, CorporatePerformance> m = parseResultSet(rs);
    Map<String, CorporatePerformance> latests = new HashMap<String, CorporatePerformance>();
    for(String k : m.keySet()) {
      CorporatePerformance cp = m.get(k);
      cp.settlingYear = 0;
      cp.settlingMonth = 0;
      latests.put(cp.getJoinKey(), cp);
    }
    return latests;
  } 

  /**
   * SQLで取得したResultSetをパースする.
   * @param rs SQLで返ってきたResultSet
   */
  private static Map<String, CorporatePerformance> 
    parseResultSet(ResultSet rs) throws SQLException {
    Map<String, CorporatePerformance> m =
      new HashMap<String, CorporatePerformance>();
    while(rs.next()) {
      CorporatePerformance v = parseResult(rs);
      m.put(v.getKeyString(), v);
    }
    return m;
  }

  private static CorporatePerformance parseResult(ResultSet rs) 
    throws SQLException {
      int stockId = rs.getInt("stock_id");
      int settlingYear = rs.getInt("settling_year");
      int settlingMonth = rs.getInt("settling_month");
      CorporatePerformance v = 
        new CorporatePerformance(stockId, settlingYear, settlingMonth);
      v.salesAmount = rs.getLong("sales_amount");
      if(rs.wasNull()) { v.salesAmount = null; }
      v.operatingProfit = rs.getLong("operating_profit");
      if(rs.wasNull()) { v.operatingProfit = null; }
      v.ordinaryProfit = rs.getLong("ordinary_profit");
      if(rs.wasNull()) { v.ordinaryProfit = null; }
      v.netProfit = rs.getLong("net_profit");
      if(rs.wasNull()) { v.netProfit = null; }
      v.totalAssets = rs.getLong("total_assets");
      if(rs.wasNull()) { v.totalAssets = null; }
      v.debtWithInterest = rs.getLong("debt_with_interest");
      if(rs.wasNull()) { v.debtWithInterest = null; }
      v.capitalFund = rs.getLong("capital_fund");
      if(rs.wasNull()) { v.capitalFund = null; }
      v.ownedCapital = rs.getLong("owned_capital");
      if(rs.wasNull()) { v.ownedCapital = null; }
      v.dividend = rs.getDouble("dividend");
      if(rs.wasNull()) { v.dividend = null; }
      return v;
  }
}
