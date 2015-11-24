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
  public Long tradingVolume;
  public Long previousTradingVolume;

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

  public Double tradingVolumeGrowthRatio() {
    if(tradingVolume != null && previousTradingVolume != null && previousTradingVolume != 0) {
      return (double)tradingVolume / previousTradingVolume;
    } else {
      return null;
    }
  }

  public double actualStockPrice() {
    return (double)(marketCap * 1000000) / stockNumber;
  }

  public String getDescription() {
    return String.format(
        "前日終値[%.1f円], 時価総額[%,3d百万円], 発行済株式数[%,3d株]\n" +
        "前日出来高[%,3d株], 出来高増加率[%.1f倍]",
        actualStockPrice(), marketCap, stockNumber,
        tradingVolume, tradingVolumeGrowthRatio());
  }

  @Override
  public String toString() {
    return String.format(
        "code[%4d], " +
        "date[%s], " +
        "marketCap[%d], " +
        "stockNumber[%d], " +
        "tradingVolume[%d], " +
        "previousTradingVolume[%d]",
        stockId, date, marketCap, stockNumber, tradingVolume, previousTradingVolume);
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
    this.tradingVolume = rs.getLong("trading_volume");
    if(rs.wasNull()) { this.tradingVolume = null; }
    this.previousTradingVolume = rs.getLong("previous_trading_volume");
    if(rs.wasNull()) { this.previousTradingVolume = null; }
  }

  public void insert(Statement st) throws SQLException {
    String sql = String.format(
        "INSERT INTO daily_stock_price(" +
        "stock_id, o_date, market_cap, stock_number, trading_volume, previous_trading_volume)" +
        "values(%4d, date('%s'), %d, %d, %d, %d)",
        this.stockId, this.date, this.marketCap,
        this.stockNumber, this.tradingVolume,
        this.previousTradingVolume);
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
    if(tradingVolume != null) {
      updateColumn++;
      sql += String.format("trading_volume = %d, ", this.tradingVolume);
    }
    if(previousTradingVolume != null) {
      updateColumn++;
      sql += String.format("previous_trading_volume = %d, ", this.previousTradingVolume);
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
        "trading_volume BIGINT, " +
        "previous_trading_volume BIGINT, " +
        "UNIQUE(stock_id, o_date)" +
      ")";
    System.out.println(sql);
    c.createStatement().executeUpdate(sql);
  }

  public static void addTradingVolume(Connection c)
    throws SQLException {
    String sql1 =
      "ALTER TABLE daily_stock_price " +
      "ADD COLUMN trading_volume BIGINT DEFAULT NULL";
    String sql2 =
      "ALTER TABLE daily_stock_price " +
      "ADD COLUMN previous_trading_volume BIGINT DEFAULT NULL";
    System.out.println(sql1);
    System.out.println(sql2);
    c.createStatement().executeUpdate(sql1);
    c.createStatement().executeUpdate(sql2);
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
   * 指定期間内での株価上昇率ランキングを取得.
   * @param days 期間
   * @param c dbコネクション
   */
  public static Map<Integer, Double> selectRiseStockRanking(int days, Connection c)
    throws SQLException, ParseException {
    Map<Integer, Double> rankMap = new HashMap<Integer, Double>();
    String sql = String.format(
        "SELECT " + 
          "dsp.stock_id AS dsp_id, " +
          "1.0 * latest.market_cap / MIN(dsp.market_cap) AS ratio " +
        "FROM daily_stock_price AS dsp " +
        "JOIN ( " +
          "select stock_id, market_cap, o_date " +
          "from daily_stock_price " +
          "where o_date = (select max(o_date) from daily_stock_price)" +
        ") AS latest ON dsp.stock_id = latest.stock_id " +
        "WHERE dsp.o_date >= date('%s') " +
        "AND latest.market_cap > 0 " +
        "GROUP BY dsp.stock_id " +
        "HAVING ratio > 1.0 ",
        MyDate.getPast(days));
    ResultSet rs = c.createStatement().executeQuery(sql);
    while(rs.next()) {
      int stockId = rs.getInt("dsp_id");
      double ratio = rs.getDouble("ratio");
      rankMap.put(stockId, ratio - 1.0);
    }
    return rankMap;
  }

  /**
   * 指定期間内での株価下落率ランキングを取得.
   * @param days 期間
   * @param c dbコネクション
   */
  public static Map<Integer, Double> selectDropStockRanking(int days, Connection c)
    throws SQLException, ParseException {
    Map<Integer, Double> dropRank = new HashMap<Integer, Double>();
    String sql = String.format(
        "SELECT " + 
          "dsp.stock_id AS dsp_id, " +
          "1.0 * MAX(dsp.market_cap) / latest.market_cap AS ratio " +
        "FROM daily_stock_price AS dsp " +
        "JOIN ( " +
          "select stock_id, market_cap, o_date " +
          "from daily_stock_price " +
          "where o_date = (select max(o_date) from daily_stock_price)" +
        ") AS latest ON dsp.stock_id = latest.stock_id " +
        "WHERE dsp.o_date >= date('%s') " +
        "AND latest.market_cap > 0 " +
        "GROUP BY dsp.stock_id " +
        "HAVING ratio > 1.0 ",
        MyDate.getPast(days));
    ResultSet rs = c.createStatement().executeQuery(sql);
    while(rs.next()) {
      int stockId = rs.getInt("dsp_id");
      double ratio = rs.getDouble("ratio");
      dropRank.put(stockId, (1.0 / ratio) - 1.0);
    }
    return dropRank;
  }

  /**
   * 指定期間内での行ってこい度ランキングを取得.
   * @param days 期間
   * @param c dbコネクション
   */
  public static Map<Integer, Double> selectRiseDropRanking(int days, Connection c)
    throws SQLException, ParseException {
    Map<Integer, Double> rank = new HashMap<Integer, Double>();
    String sql = String.format(
        "select * from ( " +
        "select " +
        "tmax.stock_id as dsp_id, " +
        "(1.0 * tmax.max_mcap / past.market_cap) as max_past, " +
        "(1.0 * tmax.max_mcap / latest.market_cap) as max_latest, " +
        "(1.0 * tmax.max_mcap / past.market_cap) * (1.0 * tmax.max_mcap / latest.market_cap) as score " +
        "from " +
        "(select stock_id, max(market_cap) as max_mcap from daily_stock_price " +
        "where o_date >= date('%s') group by stock_id) as tmax, " +
        "(select stock_id, market_cap from daily_stock_price " +
        "where o_date = (select min(o_date) from daily_stock_price where o_date >= date('%s'))) as past, " +
        "(select stock_id, market_cap from daily_stock_price " +
        "where o_date = (select max(o_date) from daily_stock_price)) as latest " +
        "where tmax.stock_id = past.stock_id and " +
        "tmax.stock_id = latest.stock_id " +
        "order by score desc " +
        ") where max_past < max_latest * 4 " +
        "and max_latest < max_past * 4 " +
        "and max_past > 1.1 " +
        "and max_latest > 1.1 " +
        "",
      MyDate.getPast(days), MyDate.getPast(days));
    ResultSet rs = c.createStatement().executeQuery(sql);
    while(rs.next()) {
      int stockId = rs.getInt("dsp_id");
      double score = rs.getDouble("score");
      rank.put(stockId, score);
    }
    return rank;
  }

  /**
   * 指定した日数で下げて、下げ止まった銘柄のランキングを取得.
   * @param dropDays 下げる日数
   * @param floorDays 下げ止まりを判定する日数
   * @param c dbコネクション
   */
  public static Map<Integer, Double>
    selectReachedFloorRanking(int dropDays, int floorDays, Connection c)
    throws SQLException, ParseException {
    Map<Integer, Double> rank = new HashMap<Integer, Double>();
    String sql = String.format(
        "select * from ( " +
        "select " +
        " latest.stock_id as dsp_id, " +
        " past.market_cap as past_cap, " +
        " weekago.market_cap as weekago_cap, " +
        " latest.market_cap as latest_cap, " +
        " maxval.mmcap as max_cap, " +
        " 1.0 * maxval.mmcap / weekago.market_cap as drop_score " +
        "from " +
        " (select stock_id, max(market_cap) as mmcap from daily_stock_price " +
        " where " +
        " o_date >= (select max(o_date) from daily_stock_price " +
        "   where o_date < date((select max(o_date) from daily_stock_price), '-%d days')) and " +
        " o_date <= (select max(o_date) from daily_stock_price " +
        "   where o_date < date((select max(o_date) from daily_stock_price), '-%d days')) " +
        " group by stock_id) as maxval, " +
        " (select stock_id, market_cap from daily_stock_price " +
        " where o_date = (select max(o_date) from daily_stock_price " +
        "   where o_date < date((select max(o_date) from daily_stock_price), '-%d days'))) as past, " +
        " (select stock_id, market_cap from daily_stock_price " +
        " where o_date = (select max(o_date) from daily_stock_price " +
        "   where o_date < date((select max(o_date) from daily_stock_price), '-%d days'))) as weekago, " +
        " (select stock_id, market_cap from daily_stock_price " +
        " where o_date = (select max(o_date) from daily_stock_price)) as latest " +
        "where " +
        " maxval.stock_id = past.stock_id and " +
        " past.stock_id = weekago.stock_id and " +
        " weekago.stock_id = latest.stock_id " +
        ") " +
        "where weekago_cap < latest_cap " +
        "and weekago_cap * 1.03 > latest_cap * 1.0 " +
        "order by drop_score desc ",
      dropDays + floorDays, floorDays, dropDays + floorDays, floorDays);
    ResultSet rs = c.createStatement().executeQuery(sql);
    while(rs.next()) {
      int stockId = rs.getInt("dsp_id");
      double score = rs.getDouble("drop_score");
      rank.put(stockId, score);
    }
    return rank;
    }

  /**
   * 銘柄IDに対応するインスタンスを取得.
   * @param stockId 銘柄ID
   * @param c dbコネクション
   */
  public static DailyStockPrice selectLatestByStockId(
      int stockId, Connection c)
    throws SQLException, ParseException {
    String sql = String.format(
        "SELECT * FROM daily_stock_price " +
        "WHERE stock_id = %d AND " +
        "o_date = (select max(o_date) from daily_stock_price)",
        stockId);
    ResultSet rs = c.createStatement().executeQuery(sql);
    MyDate date = new MyDate(rs.getString("o_date"));
    if(rs.wasNull()) {
      return null;
    }
    DailyStockPrice v = new DailyStockPrice(stockId, date);
    v.setResultSet(rs);
    return v;
  }

  /**
   * 各銘柄ごとに、最新のデータを取得して返す.
   * @param c dbのコネクション
   */
  public static Map<String, DailyStockPrice> selectLatests(Connection c)
    throws SQLException, ParseException {
    String sql =
      "SELECT * FROM daily_stock_price " +
      "WHERE o_date = (select max(o_date) from daily_stock_price)";
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
   * 銘柄ごとに、指定日数過去の時点での最新データを取得.
   * @param c dbのコネクション
   * @param past 指定日数
   */
  public static Map<String, DailyStockPrice>
    selectPasts(Connection c, int past)
    throws SQLException, ParseException {
    String sql = String.format(
      "SELECT * FROM daily_stock_price " +
      "WHERE o_date = (" +
        "SELECT MAX(o_date) FROM daily_stock_price " +
        "WHERE o_date <= date('%s')" +
      ")", MyDate.getPast(past)
      );
    ResultSet rs = c.createStatement().executeQuery(sql);
    Map<String, DailyStockPrice> m = parseResultSet(rs);
    Map<String, DailyStockPrice> pasts = new HashMap<String, DailyStockPrice>();
    for(String k : m.keySet()) {
      DailyStockPrice dsp = m.get(k);
      pasts.put(dsp.getJoinKey(), dsp);
    }
    return pasts;
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
