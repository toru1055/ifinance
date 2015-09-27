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
  public String companyFeature;
  public String businessDescription;
  public String businessCategory;
  public String smallBusinessCategory;
  public MyDate foundationDate;
  public MyDate listingDate;
  public Integer shareUnitNumber;
  public Integer independentEmployee;
  public Integer consolidateEmployee;
  public Double averageAge;
  public Double averageAnnualIncome;

  public CompanyProfile(int id) {
    this.stockId = id;
  }

  @Override
  public String toString() {
    return String.format(
        "stockId[%4d], " +
        "companyName[%s], " +
        "companyFeature[%s], " +
        "businessDescription[%s], " +
        "businessCategory[%s], " +
        "smallBusinessCategory[%s], " +
        "foundationDate[%s], " +
        "listingDate[%s], " +
        "shareUnitNumber[%d], " +
        "independentEmployee[%d], " +
        "consolidateEmployee[%d], " +
        "averageAge[%.4f], " +
        "averageAnnualIncome[%.4f]",
        stockId, companyName, companyFeature,
        businessDescription, businessCategory,
        smallBusinessCategory, foundationDate,
        listingDate, shareUnitNumber, independentEmployee,
        consolidateEmployee, averageAge, averageAnnualIncome);
  }

  public String getDescription() {
    return String.format(
        "%s（%4d）[%s > %s]\n" +
        "平均年齢[%.4f歳], 平均年収[%.4f万円], 設立年月日[%s]\n" +
        "企業特色：%s\n" +
        "株価推移：http://stocks.finance.yahoo.co.jp/stocks/chart/?code=%4d&ct=w \n" +
        "掲示板：http://textream.yahoo.co.jp/search?query=%4d",
        companyName,
        stockId,
        businessCategory,
        smallBusinessCategory,
        averageAge,
        averageAnnualIncomeMan(),
        foundationDate,
        companyFeature,
        stockId,
        stockId);
  }

  public Double averageAnnualIncomeMan() {
    if(averageAnnualIncome == null) {
      return null;
    } else {
      return averageAnnualIncome / 10000;
    }
  }

  public boolean hasEnough() {
    return stockId != 0
      && companyName != null
      && businessCategory != null
      //&& smallBusinessCategory != null
      ;
  }

  public String getKeyString() {
    return String.format("%4d", stockId);
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
    if(rs.wasNull()) { this.companyName = null; }
    this.companyFeature = rs.getString("company_feature");
    if(rs.wasNull()) { this.companyFeature = null; }
    this.businessDescription = rs.getString("business_description");
    if(rs.wasNull()) { this.businessDescription = null; }
    this.businessCategory = rs.getString("business_category");
    if(rs.wasNull()) { this.businessCategory = null; }
    this.smallBusinessCategory = rs.getString("small_business_category");
    if(rs.wasNull()) { this.smallBusinessCategory = null; }
    String fds = rs.getString("foundation_date");
    if(!rs.wasNull()) {
      this.foundationDate = new MyDate(fds);
    }
    String lds = rs.getString("listing_date");
    if(!rs.wasNull()) {
      this.listingDate = new MyDate(lds);
    }
    this.shareUnitNumber = rs.getInt("share_unit_number");
    if(rs.wasNull()) { this.shareUnitNumber = null; }
    this.independentEmployee = rs.getInt("independent_employee");
    if(rs.wasNull()) { this.independentEmployee = null; }
    this.consolidateEmployee = rs.getInt("consolidate_employee");
    if(rs.wasNull()) { this.consolidateEmployee = null; }
    this.averageAge = rs.getDouble("average_age");
    if(rs.wasNull()) { this.averageAge = null; }
    this.averageAnnualIncome = rs.getDouble("average_annual_income");
    if(rs.wasNull()) { this.averageAnnualIncome = null; }
  }

  public void insert(Statement st) throws SQLException {
    String lCompanyName = companyName == null ? "null" : "'"+companyName+"'";
    String lCompanyFeature = companyFeature == null ? "null" : "'"+companyFeature+"'";
    String lBusinessDescription = businessDescription == null ? "null" : "'"+businessDescription+"'";
    String lBusinessCategory = businessCategory == null ? "null" : "'"+businessCategory+"'";
    String lSmallBusinessCategory = smallBusinessCategory == null ? "null" : "'"+smallBusinessCategory+"'";

    String sql = String.format(
        "INSERT INTO company_profile(" + 
        "stock_id, " +
        "company_name, company_feature, " +
        "business_description, business_category, small_business_category, " + 
        "foundation_date, listing_date, share_unit_number, " +
        "independent_employee, consolidate_employee, " +
        "average_age, average_annual_income" +
        ") values(%4d, %s, %s, %s, %s, %s, " +
        "date('%s'), date('%s'), " +
        "%d, %d, %d, %f, %f)",
        stockId, lCompanyName, lCompanyFeature,
        lBusinessDescription, lBusinessCategory, lSmallBusinessCategory,
        foundationDate, listingDate, shareUnitNumber,
        independentEmployee, consolidateEmployee,
        averageAge, averageAnnualIncome);
    st.executeUpdate(sql);
  }

  public void update(Statement st) throws SQLException {
    int updateColumn = 0;
    String sql = "UPDATE company_profile SET ";
    if(companyName != null) {
      updateColumn++;
      sql += String.format("company_name = '%s', ", companyName);
    }
    if(companyFeature != null) {
      updateColumn++;
      sql += String.format("company_feature = '%s', ", companyFeature);
    }
    if(businessDescription != null) {
      updateColumn++;
      sql += String.format("business_description = '%s', ", businessDescription);
    }
    if(businessCategory != null) {
      updateColumn++;
      sql += String.format("business_category = '%s', ", businessCategory);
    }
    if(smallBusinessCategory != null) {
      updateColumn++;
      sql += String.format("small_business_category = '%s', ", smallBusinessCategory);
    }
    if(foundationDate != null) {
      updateColumn++;
      sql += String.format("foundation_date = date('%s'), ", foundationDate);
    }
    if(listingDate != null) {
      updateColumn++;
      sql += String.format("listing_date = date('%s'), ", listingDate);
    }
    if(shareUnitNumber != null) {
      updateColumn++;
      sql += String.format("share_unit_number = %d, ", shareUnitNumber);
    }
    if(independentEmployee != null) {
      updateColumn++;
      sql += String.format("independent_employee = %d, ", independentEmployee);
    }
    if(consolidateEmployee != null) {
      updateColumn++;
      sql += String.format("consolidate_employee = %d, ", consolidateEmployee);
    }
    if(averageAge != null) {
      updateColumn++;
      sql += String.format("average_age = %f, ", averageAge);
    }
    if(averageAnnualIncome != null) {
      updateColumn++;
      sql += String.format("average_annual_income = %f, ", averageAnnualIncome);
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
        "company_feature TEXT, " +
        "business_description TEXT, " +
        "business_category TEXT, " +
        "small_business_category TEXT, " +
        "foundation_date DATE, " +
        "listing_date DATE, " +
        "share_unit_number INT, " +
        "independent_employee INT, " +
        "consolidate_employee INT, " +
        "average_age DOUBLE, " +
        "average_annual_income DOUBLE, " +
        "UNIQUE(stock_id)" +
      ")";
    System.out.println(sql);
    c.createStatement().executeUpdate(sql);
  }

  public static void addSmallBusinessCategory(Connection c)
    throws SQLException {
    String sql =
      "ALTER TABLE company_profile ADD COLUMN " +
      "small_business_category TEXT DEFAULT NULL";
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
