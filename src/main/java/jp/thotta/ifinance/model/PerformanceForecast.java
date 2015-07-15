package jp.thotta.ifinance.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.HashMap;

/**
 * 会社予想業績クラス.
 * @author toru1055
 */
public class PerformanceForecast implements DBModel {
  public int stockId; //pk
  public int settlingYear; // pk
  public int settlingMonth; // pk
  public double dividend;
  public double dividendYield;

  public PerformanceForecast(
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
  public boolean hasEnough() {
    return stockId != 0;
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
    return String.format(
        "code[%4d], " +
        "YM[%4d/%02d], " +
        "dividend[%.2f], " +
        "dividendYield[%.4f]",
        stockId,
        settlingYear,
        settlingMonth,
        dividend,
        dividendYield);
  }

  /**
   * 同じキーのレコードがDB内に存在するかをチェック.
   * @param st SQL実行オブジェクト
   */
  public boolean exists(Statement st) throws SQLException {
    String sql = String.format(
        "SELECT * FROM performance_forecast " +
        "WHERE stock_id = %d " + 
        "AND settling_year = %d " + 
        "AND settling_month = %d " +
        "LIMIT 1", 
        this.stockId, this.settlingYear, this.settlingMonth);
    ResultSet rs = st.executeQuery(sql);
    return rs.next();
  }

  /**
   * 同じキーのレコードをDBから取得(上書き).
   * @param st SQL実行オブジェクト
   */
  public void readDb(Statement st) throws SQLException {
    String sql = String.format(
        "SELECT * FROM performance_forecast " +
        "WHERE stock_id = %d " + 
        "AND settling_year = %d " +
        "AND settling_month = %d " + 
        "LIMIT 1", 
        this.stockId, this.settlingYear, this.settlingMonth);
    //System.out.println(sql);
    ResultSet rs = st.executeQuery(sql);
    if(rs.next()) {
      PerformanceForecast pf = parseResult(rs);
      this.dividend = pf.dividend;
      this.dividendYield = pf.dividendYield;
    }
  }

  /**
   * このインスタンスをdbにインサート.
   * @param st SQL実行オブジェクト
   */
  public void insert(Statement st) throws SQLException {
    String sql = String.format(
        "INSERT INTO performance_forecast(" + 
        "stock_id, settling_year, settling_month," +
        "dividend, dividend_yield" +
        ") values(%4d, %4d, %2d, %.2f, %.4f)",
        stockId, settlingYear, settlingMonth,
        dividend, dividendYield);
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
    if(dividend > 0.0) {
      updateColumn++;
      sql += String.format("dividend = %.2f, ", dividend);
    }
    if(dividendYield > 0.0) {
      updateColumn++;
      sql += String.format("dividend_yield = %.4f, ", dividendYield);
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
    throws SQLException {
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
    throws SQLException {
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
      pf.settlingYear = 0;
      pf.settlingMonth = 0;
      latests.put(pf.getJoinKey(), pf);
    }
    return latests;
  } 

  /**
   * SQLで取得したResultSetをパースする.
   * @param rs SQLで返ってきたResultSet
   */
  private static Map<String, PerformanceForecast> 
    parseResultSet(ResultSet rs) throws SQLException {
    Map<String, PerformanceForecast> m =
      new HashMap<String, PerformanceForecast>();
    while(rs.next()) {
      PerformanceForecast v = parseResult(rs);
      m.put(v.getKeyString(), v);
    }
    return m;
  }

  private static PerformanceForecast parseResult(ResultSet rs) 
    throws SQLException {
      int stockId = rs.getInt("stock_id");
      int settlingYear = rs.getInt("settling_year");
      int settlingMonth = rs.getInt("settling_month");
      PerformanceForecast v = 
        new PerformanceForecast(stockId, settlingYear, settlingMonth);
      v.dividend = rs.getDouble("dividend");
      v.dividendYield = rs.getDouble("dividend_yield");
      return v;
  }
}
