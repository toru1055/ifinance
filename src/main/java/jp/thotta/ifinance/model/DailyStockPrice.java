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

  public String toString() {
    return String.format(
        "code[%4d], " +
        "date[%s], " +
        "marketCap[%d], " +
        "stockNumber[%d]",
        stockId, date, marketCap, stockNumber);
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
    c.createStatement().executeUpdate(sql);
  }

  /**
   * モデルのテーブルを削除.
   * @param c dbのコネクション
   */
  public static void dropTable(Connection c) 
    throws SQLException {
    String sql = "DROP TABLE IF EXISTS daily_stock_price";
    c.createStatement().executeUpdate(sql);
  }

  /**
   * Mapのデータを全てDBにInsertする.
   * @param m モデルのmap
   * @param c dbのコネクション
   */
  public static void insertMap(Map<String, DailyStockPrice> m, Connection c) 
    throws SQLException {
    String sqlFormat = 
      "INSERT INTO daily_stock_price(" +
      "stock_id, o_date, market_cap, stock_number)" +
      "values(%4d, date('%s'), %d, %d)";
    Statement st = c.createStatement();
    for(String k : m.keySet()) {
      DailyStockPrice v = m.get(k);
      st.executeUpdate(String.format(sqlFormat,
            v.stockId, v.date, v.marketCap, v.stockNumber));
    }
  }

  /**
   * テーブル内の全てのレコードをMapにして返す.
   * @param c dbのコネクション
   */
  public static Map<String, DailyStockPrice> selectAll(Connection c) 
    throws SQLException, ParseException {
    Map<String, DailyStockPrice> m = new HashMap<String, DailyStockPrice>();
    String sql = "SELECT * FROM daily_stock_price";
    ResultSet rs = c.createStatement().executeQuery(sql);
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
