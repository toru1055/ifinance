
package jp.thotta.ifinance.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;
import java.text.ParseException;

import jp.thotta.ifinance.common.MyDate;

/**
 * 企業のIR, PR, Publicityニュース
 * @author toru1055
 */
public class CompanyNews extends AbstractStockModel implements DBModel {
  public static final int NEWS_TYPE_INVESTOR_RELATIONS = 1;
  public static final int NEWS_TYPE_PRESS_RELEASE = 2;
  public static final int NEWS_TYPE_PUBLICITY = 3;
  public static final int NEWS_TYPE_OTHER = 99;

  //public int stockId; //pk
  public String url; //pk
  public String title;
  public Integer type;
  public MyDate announcementDate;

  public CompanyNews(int id, String url) {
    this.stockId = id;
    this.url = url;
  }

  @Override
  public String toString() {
    return String.format(
        "CompanyNews: stockId[%4d], " +
        "url[%s], " +
        "title[%s], " +
        "type[%s], " +
        "announcementDate[%s]",
        stockId, url, title, getNewsType(),
        announcementDate);
  }

  public String getNewsType() {
    if(type == null) {
      return null;
    }
    switch(type) {
      case NEWS_TYPE_INVESTOR_RELATIONS:
        return "IR";
      case NEWS_TYPE_PRESS_RELEASE:
        return "PR";
      case NEWS_TYPE_PUBLICITY:
        return "Publicity";
      default:
        return "Other";
    }
  }

  public boolean hasEnough() {
    return stockId != 0 &&
      url != null &&
      title != null;
  }

  public String getKeyString() {
    return String.format("%4d, %s", stockId, url);
  }

  @Override
  protected String getFindSql() {
    return String.format(
        "SELECT * FROM company_news " +
        "WHERE stock_id = %d " +
        "AND url = '%s' " +
        "LIMIT 1",
        stockId, url);
  }

  @Override
  protected void setResultSet(ResultSet rs) 
    throws SQLException, ParseException {
    this.title = rs.getString("title");
    if(rs.wasNull()) { this.title = null; }
    this.type = rs.getInt("type");
    if(rs.wasNull()) { this.type = null; }
    String ads = rs.getString("announcement_date");
    if(!rs.wasNull()) {
      this.announcementDate = new MyDate(ads);
    }
  }

  public void insert(Statement st) throws SQLException {
    String lUrl = url == null ? "null" : "'"+url+"'";
    String lTitle = title == null ? "null" : "'"+title+"'";
    String sql = String.format(
        "INSERT INTO company_news(" +
        "stock_id, url, " +
        "title, type, announcement_date) " +
        "values(%4d, %s, " +
        "%s, %d, date('%s'))",
        stockId, lUrl,
        lTitle, type, announcementDate);
    st.executeUpdate(sql);
  }

  public void update(Statement st) throws SQLException {
    int updateColumn = 0;
    String sql = "UPDATE company_news SET ";
    if(title != null) {
      updateColumn++;
      sql += String.format("title = '%s', ", title);
    }
    if(type != null) {
      updateColumn++;
      sql += String.format("type = %d, ", type);
    }
    if(announcementDate != null) {
      updateColumn++;
      sql += String.format("announcement_date = date('%s'), ", announcementDate);
    }
    sql += "id = id ";
    sql += String.format("WHERE stock_id = %d AND url = '%s'", stockId, url);
    if(updateColumn > 0) {
      st.executeUpdate(sql);
    }
  }

  /**
   * 企業ニューステーブル作成.
   * @param c dbのコネクション
   */
  public static void createTable(Connection c) 
    throws SQLException {
    String sql =
      "CREATE TABLE company_news(" +
        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "stock_id INT NOT NULL, " +
        "url TEXT NOT NULL, " +
        "title TEXT, " +
        "type INT, " +
        "announcement_date DATE, " +
        "UNIQUE(stock_id, url)" +
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
    String sql = "DROP TABLE IF EXISTS company_news";
    System.out.println(sql);
    c.createStatement().executeUpdate(sql);
  }

  /**
   * ニュース発表日を指定してニュースを取得.
   * @param c dbコネクション
   * @param md ニュース発表日
   */
  public static List<CompanyNews> selectByDate(Connection c, MyDate md)
    throws SQLException, ParseException {
    String sql = String.format(
        "SELECT * FROM company_news " +
        "WHERE announcement_date = date('%s')",
        md);
    ResultSet rs = c.createStatement().executeQuery(sql);
    return parseResultSet(rs);
  }

  /**
   * ListでDBをUpdate.
   * @param newsList モデルのリスト
   * @param c dbコネクション
   */
  public static void updateList(
      Connection c, List<CompanyNews> newsList)
    throws SQLException {
    Statement st = c.createStatement();
    for(CompanyNews news : newsList) {
      if(news.exists(st)) {
        news.update(st);
      } else {
        news.insert(st);
      }
    }
  }

  /**
   * SQLで取得したResultSetをパースする.
   * @param rs SQLで返ってきたResultSet
   */
  public static List<CompanyNews> parseResultSet(ResultSet rs)
    throws SQLException, ParseException {
    List<CompanyNews> newsList = new ArrayList<CompanyNews>();
    while(rs.next()) {
      int stockId = rs.getInt("stock_id");
      String url = rs.getString("url");
      CompanyNews news = new CompanyNews(stockId, url);
      news.setResultSet(rs);
      newsList.add(news);
    }
    return newsList;
  }
}
