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
public class CompanyProfile extends AbstractStockModel implements DBModel {
  public String companyName;
  public MyDate foundationDate;

  public CompanyProfile(int id) {
    this.stockId = id;
  }

  public boolean hasEnough() {
    return stockId != 0 && 
      companyName != null &&
      foundationDate != null;
  }

  public String getKeyString() {
    return String.format("%4d", stockId);
  }

  @Override
  public String toString() {
    return String.format(
        "stockId[%4d], " +
        "companyName[%s], " +
        "foundationDate[%s]",
        stockId, companyName, foundationDate);
  }

  @Override
  protected String getFindSql() {
    return String.format(
        "SELECT * FROM company_profile " +
        "WHERE stock_id = %d " + 
        "LIMIT 1", 
        this.stockId);
  }

  @Override
  protected void setResultSet(ResultSet rs)
    throws SQLException, ParseException {
    this.companyName = rs.getString("company_name");
    String fds = rs.getString("foundation_date");
    if(!rs.wasNull()) {
      this.foundationDate = new MyDate(fds);
    }
  }

  public void insert(Statement st) throws SQLException {
    String sql = String.format(
        "INSERT INTO company_profile(" + 
        "stock_id, " +
        "company_name, foundation_date" +
        ") values(%4d, '%s', date('%s'))",
        stockId, companyName, foundationDate);
    st.executeUpdate(sql);
  }

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
      st.executeUpdate(sql);
    }
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
    v.setResultSet(rs);
    return v;
  }
}
