package jp.thotta.ifinance.model;

import jp.thotta.ifinance.common.MyDate;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.HashMap;
import java.text.ParseException;

/**
 * 日次の株価クラス.
 * {@link DBModel}の実装.
 * 日次株価テーブルとのアクセスも持つ
 *
 * @author toru1055
 */
public class DailyStockPrice implements DBModel {
  public int stockId; //pk
  public MyDate date; //pk
  public long marketCap;
  public long stockNumber;

  public DailyStockPrice(int stockId, MyDate date) {
    this.stockId = stockId;
    this.date = date;
  }

  /**
   * Map用のキー取得.
   *
   * @return キーになる文字列
   */
  public String getKeyString() {
    return String.format("%4d,%s", stockId, date);
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
        "date[%s], " +
        "marketCap[%d], " +
        "stockNumber[%d]",
        stockId, date, marketCap, stockNumber);
  }

  /**
   * 同じキーのレコードがDB内に存在するかをチェック.
   * @param st SQL実行オブジェクト
   */
  public boolean exists(Statement st) throws SQLException {
    String sql = String.format(
        "SELECT * FROM daily_stock_price " +
        "WHERE stock_id = %d " + 
        "AND o_date = '%s' LIMIT 1", 
        this.stockId, this.date);
    ResultSet rs = st.executeQuery(sql);
    return rs.next();
  }

  /**
   * 同じキーのレコードをDBから取得.
   * @param st SQL実行オブジェクト
   */
  public void readDb(Statement st) throws SQLException {
    String sql = String.format(
        "SELECT * FROM daily_stock_price " +
        "WHERE stock_id = %d " + 
        "AND o_date = '%s' LIMIT 1", 
        this.stockId, this.date);
    ResultSet rs = st.executeQuery(sql);
    if(rs.next()) {
      long lMarketCap = rs.getLong("market_cap");
      long lStockNumber = rs.getLong("stock_number");
      if(lMarketCap != 0) {
        this.marketCap = lMarketCap;
      }
      if(lStockNumber != 0) {
        this.stockNumber = lStockNumber;
      }
    }
  }

  /**
   * このインスタンスをdbにインサート.
   * @param st SQL実行オブジェクト
   */
  public void insert(Statement st) throws SQLException {
    String sql = String.format(
        "INSERT INTO daily_stock_price(" +
        "stock_id, o_date, market_cap, stock_number)" +
        "values(%4d, date('%s'), %d, %d)",
        this.stockId, this.date, this.marketCap, this.stockNumber);
    System.out.println(sql);
    st.executeUpdate(sql);
  }

  /**
   * 同じキーのレコードをデータ更新.
   * @param st SQL実行オブジェクト
   */
  public void update(Statement st) throws SQLException {
    int updateColumn = 0;
    String sql = "UPDATE daily_stock_price SET ";
    if(marketCap != 0) {
      updateColumn++;
      sql += String.format("market_cap = %d, ", this.marketCap);
    }
    if(stockNumber != 0) {
      updateColumn++;
      sql += String.format("stock_number = %d, ", this.stockNumber);
    }
    sql += "id = id ";
    sql += String.format( 
        "WHERE stock_id = %d " + "AND o_date = '%s'", 
        stockId, date);
    if(updateColumn > 0) {
      System.out.println(sql);
      st.executeUpdate(sql);
    }
  }

  @Override
  public boolean equals(Object o) {
    return o.toString().equals(this.toString());
  }

  /**
   * モデルのテーブル作成.
   * @param c dbのコネクション
   */
  public static void createTable(Connection c) 
    throws SQLException {
    String sql = 
      "CREATE TABLE daily_stock_price(" +
        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "stock_id INT NOT NULL, " +
        "o_date DATE DEFAULT CURRENT_DATE, " +
        "market_cap BIGINT, " +
        "stock_number BIGINT, " +
        "UNIQUE(stock_id, o_date)" +
      ")";
    System.out.println(sql);
    c.createStatement().executeUpdate(sql);
  }

  /**
   * モデルのテーブルを削除.
   * @param c dbのコネクション
   */
  public static void dropTable(Connection c) 
    throws SQLException {
    String sql = "DROP TABLE IF EXISTS daily_stock_price";
    System.out.println(sql);
    c.createStatement().executeUpdate(sql);
  }

  /**
   * MapのデータでDBをUpdateする.
   * @param m モデルのmap
   * @param c dbのコネクション
   */
  public static void updateMap(Map<String, DailyStockPrice> m, Connection c) 
    throws SQLException {
    Statement st = c.createStatement();
    for(String k : m.keySet()) {
      DailyStockPrice v = m.get(k);
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
  public static Map<String, DailyStockPrice> selectAll(Connection c) 
    throws SQLException, ParseException {
    String sql = "SELECT * FROM daily_stock_price";
    ResultSet rs = c.createStatement().executeQuery(sql);
    return parseResultSet(rs);
  }

  /**
   * 各銘柄ごとに、最新のデータを取得して返す.
   * @param c dbのコネクション
   */
  public static Map<String, DailyStockPrice> selectLatests(Connection c)
    throws SQLException, ParseException {
    String sql = 
      "SELECT dsp.stock_id, dsp.o_date, dsp.market_cap, dsp.stock_number " + 
      "FROM daily_stock_price AS dsp JOIN (" +
        "select stock_id, max(o_date) as max_date " +
        "from daily_stock_price group by stock_id " +
      ") as dates " +
      "on dsp.stock_id = dates.stock_id and dsp.o_date = dates.max_date ";
    ResultSet rs = c.createStatement().executeQuery(sql);
    Map<String, DailyStockPrice> m = parseResultSet(rs);
    Map<String, DailyStockPrice> latests = new HashMap<String, DailyStockPrice>();
    for(String k : m.keySet()) {
      DailyStockPrice dsp = m.get(k);
      dsp.date = null;
      latests.put(dsp.getJoinKey(), dsp);
    }
    return latests;
  }

  private static Map<String, DailyStockPrice> parseResultSet(ResultSet rs) 
    throws SQLException, ParseException {
    Map<String, DailyStockPrice> m = new HashMap<String, DailyStockPrice>();
    while(rs.next()) {
      int stockId = rs.getInt("stock_id");
      MyDate date = new MyDate(rs.getString("o_date"));
      DailyStockPrice v = new DailyStockPrice(stockId, date);
      v.marketCap = rs.getLong("market_cap");
      v.stockNumber = rs.getLong("stock_number");
      m.put(v.getKeyString(), v);
    }
    return m;
  }
}
