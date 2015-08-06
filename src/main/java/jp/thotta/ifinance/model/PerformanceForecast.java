package jp.thotta.ifinance.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.HashMap;
import java.text.ParseException;

/**
 * 会社予想業績クラス.
 * @author toru1055
 */
public class PerformanceForecast extends AbstractStockModel implements DBModel {
  //public int stockId; //pk
  public int settlingYear; // pk
  public int settlingMonth; // pk
  public Double dividend;
  public Double dividendYield;
  public Long netEps;

  public PerformanceForecast(
      int stockId, 
      int settlingYear, 
      int settlingMonth) {
    this.stockId = stockId;
    this.settlingYear = settlingYear;
    this.settlingMonth = settlingMonth;
  }

  public boolean hasEnough() {
    return stockId != 0;
  }

  public String getKeyString() {
    return String.format("%4d,%4d/%02d", 
        stockId, settlingYear, settlingMonth);
  }

  @Override
  public String toString() {
    return String.format(
        "code[%4d], " +
        "YM[%4d/%02d], " +
        "dividend[%.2f], " +
        "dividendYield[%.4f], " +
        "netEps[%d]",
        stockId,
        settlingYear,
        settlingMonth,
        dividend,
        dividendYield,
        netEps);
  }

  @Override
  protected String getFindSql() {
    return String.format(
        "SELECT * FROM performance_forecast " +
        "WHERE stock_id = %d " + 
        "AND settling_year = %d " + 
        "AND settling_month = %d " +
        "LIMIT 1", 
        this.stockId, this.settlingYear, this.settlingMonth);
  }

  @Override
  protected void setResultSet(ResultSet rs)
    throws SQLException, ParseException {
    this.dividend = rs.getDouble("dividend");
    if(rs.wasNull()) { this.dividend = null; }
    this.dividendYield = rs.getDouble("dividend_yield");
    if(rs.wasNull()) { this.dividendYield = null; }
    this.netEps = rs.getLong("net_eps");
    if(rs.wasNull()) { this.netEps = null; }
  }

  public void insert(Statement st) throws SQLException {
    String sql = String.format(
        "INSERT INTO performance_forecast(" + 
        "stock_id, settling_year, settling_month," +
        "dividend, dividend_yield, net_eps" +
        ") values(%4d, %4d, %2d, %f, %f, %d)",
        stockId, settlingYear, settlingMonth,
        dividend, dividendYield, netEps);
    st.executeUpdate(sql);
  }

  public void update(Statement st) throws SQLException {
    int updateColumn = 0;
    String sql = "UPDATE performance_forecast SET ";
    if(dividend != null) {
      updateColumn++;
      sql += String.format("dividend = %f, ", dividend);
    }
    if(dividendYield != null) {
      updateColumn++;
      sql += String.format("dividend_yield = %f, ", dividendYield);
    }
    if(netEps != null) {
      updateColumn++;
      sql += String.format("net_eps = %d, ", netEps);
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

  /**
   * 会社予想業績テーブル作成.
   * @param c dbのコネクション
   */
  public static void createTable(Connection c) 
    throws SQLException {
    String sql = 
      "CREATE TABLE performance_forecast(" +
        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "stock_id INT NOT NULL, " +
        "settling_year INT NOT NULL, " +
        "settling_month INT NOT NULL, " +
        "dividend DOUBLE, " +
        "dividend_yield DOUBLE, " +
        "net_eps BIGINT, " +
        "UNIQUE(stock_id, settling_year, settling_month)" +
      ")";
    System.out.println(sql);
    c.createStatement().executeUpdate(sql);
  }

  /**
   * 会社予想業績テーブルを削除.
   * @param c dbのコネクション
   */
  public static void dropTable(Connection c) 
    throws SQLException {
    String sql = "DROP TABLE IF EXISTS performance_forecast";
    System.out.println(sql);
    c.createStatement().executeUpdate(sql);
  }

  /**
   * MapのデータでDBをUpdateする.
   * @param m モデルのmap
   * @param c dbのコネクション
   */
  public static void updateMap(
      Map<String, PerformanceForecast> m, Connection c) 
    throws SQLException {
    Statement st = c.createStatement();
    for(String k : m.keySet()) {
      PerformanceForecast v = m.get(k);
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
  public static Map<String, PerformanceForecast> selectAll(Connection c)
    throws SQLException, ParseException {
    String sql = "SELECT * FROM performance_forecast";
    ResultSet rs = c.createStatement().executeQuery(sql);
    return parseResultSet(rs);
  }

  /**
   * 各銘柄ごとに、最新のデータを取得して返す.
   * @param c dbのコネクション
   */
  public static Map<String, PerformanceForecast> 
    selectLatests(Connection c)
    throws SQLException, ParseException {
    String sql = 
      "SELECT pf.* " + 
      "FROM performance_forecast AS pf JOIN ( " +
      "SELECT stock_id, MAX(settling_year) AS settling_year " +
      "FROM performance_forecast GROUP BY stock_id " +
      ") AS years " +
      "ON pf.stock_id = years.stock_id AND pf.settling_year = years.settling_year";
    ResultSet rs = c.createStatement().executeQuery(sql);
    Map<String, PerformanceForecast> m = parseResultSet(rs);
    Map<String, PerformanceForecast> latests = new HashMap<String, PerformanceForecast>();
    for(String k : m.keySet()) {
      PerformanceForecast pf = m.get(k);
      latests.put(pf.getJoinKey(), pf);
    }
    return latests;
  } 

  /**
   * SQLで取得したResultSetをパースする.
   * @param rs SQLで返ってきたResultSet
   */
  private static Map<String, PerformanceForecast> 
    parseResultSet(ResultSet rs) throws SQLException, ParseException {
    Map<String, PerformanceForecast> m =
      new HashMap<String, PerformanceForecast>();
    while(rs.next()) {
      PerformanceForecast v = parseResult(rs);
      m.put(v.getKeyString(), v);
    }
    return m;
  }

  private static PerformanceForecast parseResult(ResultSet rs) 
    throws SQLException, ParseException {
      int stockId = rs.getInt("stock_id");
      int settlingYear = rs.getInt("settling_year");
      int settlingMonth = rs.getInt("settling_month");
      PerformanceForecast v = 
        new PerformanceForecast(stockId, settlingYear, settlingMonth);
      v.setResultSet(rs);
      return v;
  }
}
