package jp.thotta.ifinance.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.HashMap;
import java.text.ParseException;

import jp.thotta.ifinance.common.MyDate;

/**
 * 企業の固有情報クラス.
 * @author toru1055
 */
public class CompanyProfile implements DBModel {
  public int stockId; //pk
  public String companyName;
  public MyDate foundationDate;

  public CompanyProfile(int id) {
    this.stockId = id;
  }

  /**
   * 全ての要素が取得できたか.
   */
  public boolean isAllInclude() {
    return stockId != 0 && 
      companyName != null &&
      foundationDate != null;
  }

  /**
   * Map用のキー取得.
   *
   * @return キーになる文字列
   */
  public String getKeyString() {
    return String.format("%4d", stockId);
  }

  /**
   * Join用のキー取得.
   */
  public String getJoinKey() {
    return getKeyString();
  }

  @Override
  public String toString() {
    return String.format(
        "stockId[%4d], " +
        "companyName[%s], " +
        "foundationDate[%s]",
        stockId, companyName, foundationDate);
  }

  /**
   * 同じキーのレコードがDB内に存在するかをチェック.
   * @param st SQL実行オブジェクト
   */
  public boolean exists(Statement st) throws SQLException {
    String sql = String.format(
        "SELECT * FROM company_profile " +
        "WHERE stock_id = %d " + 
        "LIMIT 1", 
        this.stockId);
    ResultSet rs = st.executeQuery(sql);
    return rs.next();
  }

  /**
   * 同じキーのレコードをDBから取得(上書き).
   * @param st SQL実行オブジェクト
   */
  public void readDb(Statement st) throws SQLException, ParseException {
    String sql = String.format(
        "SELECT * FROM company_profile " +
        "WHERE stock_id = %d " + 
        "LIMIT 1", 
        this.stockId);
    //System.out.println(sql);
    ResultSet rs = st.executeQuery(sql);
    if(rs.next()) {
      CompanyProfile profile = parseResult(rs);
      this.companyName = profile.companyName;
      if(profile.foundationDate != null) {
        this.foundationDate = new MyDate(profile.foundationDate);
      }
    }
  }

  /**
   * このインスタンスをdbにインサート.
   * @param st SQL実行オブジェクト
   */
  public void insert(Statement st) throws SQLException {
    String sql = String.format(
        "INSERT INTO company_profile(" + 
        "stock_id, " +
        "company_name, foundation_date" +
        ") values(%4d, '%s', date('%s'))",
        stockId, companyName, foundationDate);
    //System.out.println(sql);
    st.executeUpdate(sql);
  }

  /**
   * 同じキーのレコードをデータ更新.
   * @param st SQL実行オブジェクト
   */
  public void update(Statement st) throws SQLException {
    int updateColumn = 0;
    String sql = "UPDATE company_profile SET ";
    if(companyName != null) {
      updateColumn++;
      sql += String.format("company_name = '%s', ", companyName);
    }
    if(foundationDate != null) {
      updateColumn++;
      sql += String.format("foundation_date = date('%s'), ", foundationDate);
    }
    sql += "id = id ";
    sql += String.format("WHERE stock_id = %d", stockId);
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
   * 企業情報テーブル作成.
   * @param c dbのコネクション
   */
  public static void createTable(Connection c) 
    throws SQLException {
    String sql = 
      "CREATE TABLE company_profile(" +
        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "stock_id INT NOT NULL, " +
        "company_name TEXT, " +
        "foundation_date DATE, " +
        "UNIQUE(stock_id)" +
      ")";
    System.out.println(sql);
    c.createStatement().executeUpdate(sql);
  }

  /**
   * テーブルを削除.
   * @param c dbのコネクション
   */
  public static void dropTable(Connection c) 
    throws SQLException {
    String sql = "DROP TABLE IF EXISTS company_profile";
    System.out.println(sql);
    c.createStatement().executeUpdate(sql);
  }

  /**
   * MapのデータでDBをUpdateする.
   * @param m モデルのmap
   * @param c dbのコネクション
   */
  public static void updateMap(
      Map<String, CompanyProfile> m, Connection c) 
    throws SQLException {
    Statement st = c.createStatement();
    for(String k : m.keySet()) {
      CompanyProfile v = m.get(k);
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
  public static Map<String, CompanyProfile> selectAll(Connection c)
    throws SQLException, ParseException {
    String sql = "SELECT * FROM company_profile";
    ResultSet rs = c.createStatement().executeQuery(sql);
    return parseResultSet(rs);
  }

  /**
   * SQLで取得したResultSetをパースする.
   * @param rs SQLで返ってきたResultSet
   */
  private static Map<String, CompanyProfile> 
    parseResultSet(ResultSet rs) throws SQLException, ParseException {
    Map<String, CompanyProfile> m =
      new HashMap<String, CompanyProfile>();
    while(rs.next()) {
      CompanyProfile v = parseResult(rs);
      m.put(v.getKeyString(), v);
    }
    return m;
  }

  /**
   * ResultSetを１個だけパース.
   */
  private static CompanyProfile parseResult(ResultSet rs) 
    throws SQLException, ParseException {
    int stockId = rs.getInt("stock_id");
    CompanyProfile v = new CompanyProfile(stockId);
    v.companyName = rs.getString("company_name");
    String fds = rs.getString("foundation_date");
    if(fds != null) {
      v.foundationDate = new MyDate(fds);
    }
    return v;
  }
}
