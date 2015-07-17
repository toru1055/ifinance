package jp.thotta.ifinance.model;

import jp.thotta.ifinance.common.MyDate;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.text.ParseException;

/**
 * 日次の株価クラス.
 * {@link DBModel}の実装.
 * 日次株価テーブルとのアクセスも持つ
 *
 * @author toru1055
 */
public class DailyStockPrice extends AbstractStockModel implements DBModel {
  //public int stockId; //pk
  public MyDate date; //pk
  public long marketCap;
  public long stockNumber;

  public DailyStockPrice(int stockId, MyDate date) {
    this.stockId = stockId;
    this.date = date;
  }

  public boolean hasEnough() {
    return stockId != 0 && 
      marketCap != 0 && 
      stockNumber != 0;
  }

  public String getKeyString() {
    return String.format("%4d,%s", stockId, date);
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

  @Override
  protected String getFindSql() {
    return String.format(
        "SELECT * FROM daily_stock_price " +
        "WHERE stock_id = %d " + 
        "AND o_date = '%s' LIMIT 1", 
        this.stockId, this.date);
  }

  @Override
  protected void setResultSet(ResultSet rs)
    throws SQLException, ParseException {
    this.marketCap = rs.getLong("market_cap");
    this.stockNumber = rs.getLong("stock_number");
  }

  public void insert(Statement st) throws SQLException {
    String sql = String.format(
        "INSERT INTO daily_stock_price(" +
        "stock_id, o_date, market_cap, stock_number)" +
        "values(%4d, date('%s'), %d, %d)",
        this.stockId, this.date, this.marketCap, this.stockNumber);
    st.executeUpdate(sql);
  }

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
      st.executeUpdate(sql);
    }
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
   * DB内の銘柄コードをリストで取得.
   * @param c DBコネクション
   * @return 銘柄コードのリスト
   */
  public static List<Integer> selectStockIds(Connection c)
    throws SQLException, ParseException {
    List<Integer> stockIdList = new ArrayList<Integer>();
    String sql = "SELECT DISTINCT(stock_id) FROM daily_stock_price";
    ResultSet rs = c.createStatement().executeQuery(sql);
    while(rs.next()) {
      int stockId = rs.getInt("stock_id");
      stockIdList.add(stockId);
    }
    return stockIdList;
  }

  /**
   * 各銘柄ごとに、最新のデータを取得して返す.
   * @param c dbのコネクション
   */
  public static Map<String, DailyStockPrice> selectLatests(Connection c)
    throws SQLException, ParseException {
    String sql = 
      "SELECT dsp.* " +
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
      latests.put(dsp.getJoinKey(), dsp);
    }
    return latests;
  }

  /**
   * SQLで取得したResultSetをパースする.
   * @param rs SQLで返ってきたResultSet
   */
  private static Map<String, DailyStockPrice> parseResultSet(ResultSet rs) 
    throws SQLException, ParseException {
    Map<String, DailyStockPrice> m = new HashMap<String, DailyStockPrice>();
    while(rs.next()) {
      int stockId = rs.getInt("stock_id");
      MyDate date = new MyDate(rs.getString("o_date"));
      DailyStockPrice v = new DailyStockPrice(stockId, date);
      v.setResultSet(rs);
      m.put(v.getKeyString(), v);
    }
    return m;
  }
}
