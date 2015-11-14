package jp.thotta.ifinance.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
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
  public static final int NEWS_TYPE_APP_DOWNLOAD = 4;
  public static final int NEWS_TYPE_SHOP_OPEN = 5;
  public static final int NEWS_TYPE_DEVELOPMENT = 6;
  public static final int NEWS_TYPE_INFORMATION = 7;
  public static final int NEWS_TYPE_HOT_TOPIC = 8;
  public static final int NEWS_TYPE_OTHER = 99;

  //public int stockId; //pk
  public String url; //pk
  public String title;
  public Integer type;
  public MyDate announcementDate; //pk
  public MyDate createdDate;

  public CompanyNews(int id, String url, MyDate aDate) {
    this.stockId = id;
    this.url = url;
    this.announcementDate = aDate;
  }

  @Override
  public String toString() {
    return String.format(
        "CompanyNews: stockId[%4d], " +
        "url[%s], " +
        "title[%s], " +
        "type[%s], " +
        "announcementDate[%s], " +
        "createdDate[%s]",
        stockId, url, title, getNewsType(),
        announcementDate, createdDate);
  }

  public String getDescription() {
    return String.format(
        "[%s] %s (%s)\n" +
        "%s",
        getNewsType(),
        title, announcementDate, url);
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
      case NEWS_TYPE_APP_DOWNLOAD:
        return "App Download";
      case NEWS_TYPE_SHOP_OPEN:
        return "Shop Open";
      case NEWS_TYPE_DEVELOPMENT:
        return "Development";
      case NEWS_TYPE_INFORMATION:
        return "Information";
      case NEWS_TYPE_HOT_TOPIC:
        return "話題速報";
      default:
        return "Other";
    }
  }

  public boolean hasEnough() {
    return stockId != 0 &&
      url != null &&
      title != null &&
      type != null &&
      announcementDate != null &&
      createdDate != null;
  }

  public String getKeyString() {
    return String.format("%4d, %s, %s",
        stockId, url, announcementDate);
  }

  @Override
  protected String getFindSql() {
    return String.format(
        "SELECT * FROM company_news " +
        "WHERE stock_id = %d " +
        "AND url = '%s' " +
        "AND announcement_date = '%s' " +
        "LIMIT 1",
        stockId, url, announcementDate);
  }

  @Override
  protected void setResultSet(ResultSet rs) 
    throws SQLException, ParseException {
    this.title = rs.getString("title");
    if(rs.wasNull()) { this.title = null; }
    this.type = rs.getInt("type");
    if(rs.wasNull()) { this.type = null; }
    String cds = rs.getString("created_date");
    if(!rs.wasNull()) {
      this.createdDate = new MyDate(cds);
    }
  }

  public void insert(Statement st) throws SQLException {
    String lUrl = url == null ? "null" : "'"+url+"'";
    String lTitle = title == null ? "null" : "'"+title.replaceAll("'", "''")+"'";
    String sql = String.format(
        "INSERT INTO company_news(" +
        "stock_id, url, " +
        "title, type, announcement_date, created_date) " +
        "values(%4d, %s, " +
        "%s, %d, date('%s'), date('%s'))",
        stockId, lUrl,
        lTitle, type, announcementDate, createdDate);
    st.executeUpdate(sql);
  }

  public void update(Statement st) throws SQLException {
    int updateColumn = 0;
    String sql = "UPDATE company_news SET ";
    if(title != null) {
      updateColumn++;
      title.replaceAll("'", "''");
      sql += String.format("title = '%s', ", title);
    }
    if(type != null) {
      updateColumn++;
      sql += String.format("type = %d, ", type);
    }
    if(createdDate != null) {
      updateColumn++;
      sql += String.format("created_date = date('%s'), ", createdDate);
    }
    sql += "id = id ";
    sql += String.format(
        "WHERE stock_id = %d " +
        "AND url = '%s' " +
        "AND announcement_date = '%s'",
        stockId, url, announcementDate);
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
        "announcement_date DATE NOT NULL, " +
        "created_date DATE, " +
        "UNIQUE(stock_id, url, announcement_date)" +
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
   * ニュース登録日を指定してニュースを取得.
   * @param c dbコネクション
   * @param md ニュース発表日
   */
  public static List<CompanyNews> selectByDate(Connection c, MyDate md)
    throws SQLException, ParseException {
    String sql = String.format(
        "SELECT * FROM company_news " +
        "WHERE created_date = date('%s') " +
        "AND type != %d", 
        md, NEWS_TYPE_HOT_TOPIC);
    ResultSet rs = c.createStatement().executeQuery(sql);
    return parseResultSet(rs);
  }

  /**
   * ニュース登録日を指定してニュースを取得.
   * @param c dbコネクション
   * @param md ニュース発表日
   */
  public static List<CompanyNews> selectByDate(Connection c, MyDate md, int announcementPast)
    throws SQLException, ParseException {
    String sql = String.format(
        "SELECT * FROM company_news " +
        "WHERE created_date = date('%s') " +
        "AND announcement_date >= date('%s') " +
        "AND type != %d",
        md, MyDate.getPast(announcementPast), NEWS_TYPE_HOT_TOPIC);
    ResultSet rs = c.createStatement().executeQuery(sql);
    return parseResultSet(rs);
  }

  /**
   * 指定日数過去以降のニュースを取得.
   * @param c dbコネクション
   * @param past 何日前
   */
  public static List<CompanyNews> 
    selectByPast(Connection c, int past)
    throws SQLException, ParseException {
    String sql = String.format(
        "SELECT * FROM company_news " +
        "WHERE created_date > date('%s')" +
        "AND announcement_date > date('%s') " +
        "AND type != %d",
        MyDate.getPast(past),
        MyDate.getPast(past + 2),
        NEWS_TYPE_HOT_TOPIC);
    ResultSet rs = c.createStatement().executeQuery(sql);
    return parseResultSet(rs);
  }

  /**
   * 最新の話題銘柄ランキング.
   * @param c dbコネクション
   */
  public static List<CompanyNews>
    selectLatestHotTopics(Connection c)
    throws SQLException, ParseException {
    String sql = String.format(
        "SELECT * FROM company_news " +
        "WHERE type = %d " +
        "ORDER BY url DESC LIMIT 25",
        NEWS_TYPE_HOT_TOPIC);
    ResultSet rs = c.createStatement().executeQuery(sql);
    return parseResultSet(rs);
  }

  public static Map<String, CompanyNews>
    selectMapLatestHotTopics(Connection c)
    throws SQLException, ParseException {
    Map<String, CompanyNews> m =
      new HashMap<String, CompanyNews>();
    List<CompanyNews> cnList = selectLatestHotTopics(c);
    for(CompanyNews news : cnList) {
      String k = news.getJoinKey();
      m.put(k, news);
    }
    return m;
  }

  /**
   * 各銘柄の直近ニュースを取得.
   * @param c dbコネクション
   */
  public static List<CompanyNews> selectLatests(Connection c)
    throws SQLException, ParseException {
    String sql = String.format(
        "SELECT cn.* FROM company_news AS cn " +
        "JOIN (" +
          "select stock_id, max(announcement_date) as max_a_date " +
          "from company_news where type != %d group by stock_id" +
        ") AS a_table ON cn.stock_id = a_table.stock_id AND " +
        "cn.announcement_date = a_table.max_a_date WHERE cn.type != %d",
        NEWS_TYPE_HOT_TOPIC, NEWS_TYPE_HOT_TOPIC);
    ResultSet rs = c.createStatement().executeQuery(sql);
    return parseResultSet(rs);
  }

  public static Map<String, List<CompanyNews>>
    selectLatestMap(Connection c)
    throws SQLException, ParseException {
    Map<String, List<CompanyNews>> m =
      new HashMap<String, List<CompanyNews>>();
    List<CompanyNews> cnList = selectLatests(c);
    for(CompanyNews news : cnList) {
      String k = news.getJoinKey();
      List<CompanyNews> cnl;
      if(m.containsKey(k)) {
        cnl = m.get(k);
      } else {
        cnl = new ArrayList<CompanyNews>();
      }
      cnl.add(news);
      m.put(k, cnl);
    }
    return m;
  }

  /**
   * ニュース登録日を指定してニュースを取得.
   * 銘柄ごとのリストを作成.
   * @param c dbコネクション
   * @param md ニュース発表日
   */
  public static Map<String, List<CompanyNews>>
    selectMapByDate(Connection c, MyDate md, int announcementPast)
    throws SQLException, ParseException {
    Map<String, List<CompanyNews>> m =
      new HashMap<String, List<CompanyNews>>();
    List<CompanyNews> cnList = selectByDate(c, md, announcementPast);
    for(CompanyNews news : cnList) {
      String k = news.getJoinKey();
      List<CompanyNews> cnl;
      if(m.containsKey(k)) {
        cnl = m.get(k);
      } else {
        cnl = new ArrayList<CompanyNews>();
      }
      cnl.add(news);
      m.put(k, cnl);
    }
    return m;
  }

  /**
   * 銘柄IDに対応する直近ニュースを取得.
   * @param stockId 銘柄ID
   * @param num 直近いくつのニュースを取得するか
   * @param c dbコネクション
   */
  public static List<CompanyNews> selectRecentsByStockId(
      int stockId, int num, Connection c)
    throws SQLException, ParseException {
    String sql = String.format(
        "SELECT * FROM company_news " +
        "WHERE stock_id = %d " +
        "AND type != %d " +
        "ORDER BY created_date DESC " +
        "limit %d",
        stockId, NEWS_TYPE_HOT_TOPIC, num);
    ResultSet rs = c.createStatement().executeQuery(sql);
    return parseResultSet(rs);
  }

  /**
   * 指定日数過去以降のニュースを取得.
   * 銘柄ごとのリストをMapにして作成.
   * @param c dbコネクション
   * @param past 何日前
   */
  public static Map<String, List<CompanyNews>>
    selectMapByPast(Connection c, int past)
    throws SQLException, ParseException {
    Map<String, List<CompanyNews>> m =
      new HashMap<String, List<CompanyNews>>();
    List<CompanyNews> cnList = selectByPast(c, past);
    for(CompanyNews news : cnList) {
      String k = news.getJoinKey();
      List<CompanyNews> cnl;
      if(m.containsKey(k)) {
        cnl = m.get(k);
      } else {
        cnl = new ArrayList<CompanyNews>();
      }
      cnl.add(news);
      m.put(k, cnl);
    }
    return m;
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
      String ads = rs.getString("announcement_date");
      MyDate announcementDate = new MyDate(ads);
      CompanyNews news = new CompanyNews(stockId, url, announcementDate);
      news.setResultSet(rs);
      newsList.add(news);
    }
    return newsList;
  }
}
