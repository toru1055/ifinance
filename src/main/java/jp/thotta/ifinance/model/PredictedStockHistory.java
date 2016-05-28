package jp.thotta.ifinance.model;

import jp.thotta.ifinance.common.MyDate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * 株価予測結果クラス.
 *
 * @author toru1055
 */
public class PredictedStockHistory
        extends AbstractStockModel implements DBModel {
    //public int stockId; // pk
    public MyDate predictedDate; //pk
    public Long predictedMarketCap;
    public Boolean isStableStock;

    public PredictedStockHistory(int stockId, MyDate predictedDate) {
        this.stockId = stockId;
        this.predictedDate = predictedDate.copy();
    }

    public boolean hasEnough() {
        return stockId != 0 &&
                predictedDate != null &&
                predictedMarketCap != null &&
                isStableStock != null;
    }

    public String getKeyString() {
        return String.format("%4d,%s", stockId, predictedDate);
    }

    @Override
    public String toString() {
        return String.format(
                "stockId[%4d], " +
                        "predictedDate[%s], " +
                        "predictedMarketCap[%d], " +
                        "isStableStock[%b]",
                stockId, predictedDate,
                predictedMarketCap, isStableStock);
    }

    @Override
    protected String getFindSql() {
        return String.format(
                "SELECT * FROM predicted_stock_history " +
                        "WHERE stock_id = %d AND predicted_date = '%s' " +
                        "LIMIT 1",
                this.stockId, this.predictedDate);
    }

    @Override
    protected void setResultSet(ResultSet rs)
            throws SQLException, ParseException {
        this.predictedMarketCap = rs.getLong("predicted_market_cap");
        if (rs.wasNull()) {
            this.predictedMarketCap = null;
        }
        this.isStableStock = rs.getBoolean("is_stable_stock");
        if (rs.wasNull()) {
            this.isStableStock = null;
        }
    }

    public void insert(Statement st) throws SQLException {
        String sql = String.format(
                "INSERT INTO predicted_stock_history(" +
                        "stock_id, predicted_date, " +
                        "predicted_market_cap, is_stable_stock" +
                        ") values(%4d, date('%s'), %d, %d)",
                stockId, predictedDate, predictedMarketCap, isStableStock ? 1 : 0);
        st.executeUpdate(sql);
    }

    public void update(Statement st) throws SQLException {
        int updateColumn = 0;
        String sql = "UPDATE predicted_stock_history SET ";
        if (predictedMarketCap != null) {
            updateColumn++;
            sql += String.format("predicted_market_cap = %d, ", predictedMarketCap);
        }
        if (isStableStock != null) {
            updateColumn++;
            sql += String.format("is_stable_stock = %d, ", isStableStock ? 1 : 0);
        }
        sql += "id = id ";
        sql += String.format("WHERE stock_id = %d AND predicted_date = '%s'",
                stockId, predictedDate);
        if (updateColumn > 0) {
            st.executeUpdate(sql);
        }
    }

    /**
     * モデルのテーブル作成.
     *
     * @param c dbのコネクション
     */
    public static void createTable(Connection c)
            throws SQLException {
        String sql =
                "CREATE TABLE predicted_stock_history(" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "stock_id INT NOT NULL, " +
                        "predicted_date DATE DEFAULT CURRENT_DATE, " +
                        "predicted_market_cap BIGINT, " +
                        "is_stable_stock BOOLEAN, " +
                        "UNIQUE(stock_id, predicted_date)" +
                        ")";
        System.out.println(sql);
        c.createStatement().executeUpdate(sql);
    }

    /**
     * モデルのテーブルを削除.
     *
     * @param c dbのコネクション
     */
    public static void dropTable(Connection c)
            throws SQLException {
        String sql = "DROP TABLE IF EXISTS predicted_stock_history";
        System.out.println(sql);
        c.createStatement().executeUpdate(sql);
    }

    /**
     * MapのデータでDBをUpdateする.
     *
     * @param m モデルのmap
     * @param c dbのコネクション
     */
    public static void updateMap(
            Map<String, PredictedStockHistory> m, Connection c)
            throws SQLException {
        Statement st = c.createStatement();
        for (String k : m.keySet()) {
            PredictedStockHistory v = m.get(k);
            if (v.exists(st)) {
                v.update(st);
            } else {
                v.insert(st);
            }
        }
    }

    /**
     * テーブル内の全てのレコードをMapにして返す.
     *
     * @param c dbのコネクション
     */
    public static Map<String, PredictedStockHistory> selectAll(Connection c)
            throws SQLException, ParseException {
        String sql = "SELECT * FROM predicted_stock_history";
        ResultSet rs = c.createStatement().executeQuery(sql);
        return parseResultSet(rs);
    }

    /**
     * 入力された期間だけ前の予測株価の履歴を取得.
     *
     * @param c    dbのコネクション
     * @param days 何日前の予測株価を取得するか
     * @return 予測株価履歴のマップ
     */
    public static Map<String, PredictedStockHistory> selectPast(
            Connection c, int days) throws SQLException, ParseException {
        MyDate d = MyDate.getPast(days);
        String sql = String.format(
                "SELECT * FROM predicted_stock_history " +
                        "WHERE predicted_date = '%s'", d);
        ResultSet rs = c.createStatement().executeQuery(sql);
        Map<String, PredictedStockHistory> m = parseResultSet(rs);
        return m;
    }

    /**
     * 銘柄IDに対応する最新の予測株価を取得.
     *
     * @param stockId 銘柄ID
     * @param c       dbコネクション
     */
    public static PredictedStockHistory
    selectLatestByStockId(int stockId, Connection c)
            throws SQLException, ParseException {
        String sql = String.format(
                "SELECT * FROM predicted_stock_history " +
                        "WHERE stock_id = %4d " +
                        "AND predicted_date = (" +
                        "select max(predicted_date) from predicted_stock_history" +
                        ")",
                stockId);
        ResultSet rs = c.createStatement().executeQuery(sql);
        if (rs.next()) {
            MyDate date = new MyDate(rs.getString("predicted_date"));
            PredictedStockHistory v = new PredictedStockHistory(stockId, date);
            v.setResultSet(rs);
            return v;
        } else {
            return null;
        }
    }

    /**
     * SQLで取得したResultSetをパースする.
     *
     * @param rs SQLで返ってきたResultSet
     */
    private static Map<String, PredictedStockHistory> parseResultSet(ResultSet rs)
            throws SQLException, ParseException {
        Map<String, PredictedStockHistory> m = new HashMap<String, PredictedStockHistory>();
        while (rs.next()) {
            int stockId = rs.getInt("stock_id");
            MyDate date = new MyDate(rs.getString("predicted_date"));
            PredictedStockHistory v = new PredictedStockHistory(stockId, date);
            v.setResultSet(rs);
            m.put(v.getKeyString(), v);
        }
        return m;
    }

}
