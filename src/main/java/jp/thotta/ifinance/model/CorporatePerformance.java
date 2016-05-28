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
 * 通期の企業業績クラス.
 * {@link DBModel}を継承し、業績テーブルとのアクセスも持つ
 * <p>
 * 素性追加時の修正箇所は下記のdiffを参考にする
 * https://github.com/toru1055/ifinance/commit/a58d9a992fe0e9ee94648d3d1824e9f40491f4a8
 * 配当金合計 = 1株配当×株数
 * http://jp.kabumap.com/servlets/kabumap/Action?SRC=basic/factor/base&codetext=8594
 * ここ参考にする: 例(成長性)
 * １．ROE
 * ２．売上高成長率
 * ３．今期経常利益変化率
 *
 * @author toru1055
 */
public class CorporatePerformance extends AbstractStockModel implements DBModel {
    //public int stockId; //pk
    public int settlingYear; // pk
    public int settlingMonth; // pk
    public MyDate announcementDate;
    public Long salesAmount;
    public Long operatingProfit;
    public Long ordinaryProfit;
    public Long netProfit;
    public Long totalAssets;
    public Long debtWithInterest;
    public Long capitalFund;
    public Long ownedCapital;
    public Double dividend;

    public CorporatePerformance(
            int stockId,
            int settlingYear,
            int settlingMonth) {
        this.stockId = stockId;
        this.settlingYear = settlingYear;
        this.settlingMonth = settlingMonth;
    }

    public boolean hasEnough() {
        return stockId != 0 &&
                salesAmount != null &&
                operatingProfit != null &&
                ordinaryProfit != null &&
                netProfit != null &&
                totalAssets != null &&
                capitalFund != null &&
                ownedCapital != null;
    }

    /**
     * 自己資本比率を計算して返す.
     */
    public double ownedCapitalRatio() {
        if (totalAssets == null || ownedCapital == null) {
            return 0.0;
        }
        if (totalAssets <= 0) {
            if (ownedCapital == 0) {
                return 0.0;
            } else {
                return 1.0;
            }
        }
        return (double) ownedCapital / totalAssets;
    }

    /**
     * 営業利益率を返す.
     */
    public double operatingProfitRate() {
        if (salesAmount == null ||
                operatingProfit == null ||
                salesAmount == 0) {
            return 0.0;
        } else {
            return (double) operatingProfit / salesAmount;
        }
    }

    /**
     * 自己資本以外の資本を返す.
     */
    public long otherCapital() {
        long oc = totalAssets - ownedCapital;
        return oc > 0 ? oc : 0;
    }

    public String getKeyString() {
        return String.format("%4d,%4d/%02d",
                stockId, settlingYear, settlingMonth);
    }

    @Override
    public String toString() {
        String s = String.format(
                "code[%4d], " +
                        "settlingYM[%4d/%02d], " +
                        "announcementDate[%s], " +
                        "salesAmount[%d], " +
                        "operatingProfit[%d], " +
                        "ordinaryProfit[%d], " +
                        "netProfit[%d], " +
                        "totalAssets[%d], " +
                        "debtWithInterest[%d], " +
                        "capitalFund[%d], " +
                        "ownedCapital[%d], " +
                        "dividend[%.4g]",
                stockId,
                settlingYear,
                settlingMonth,
                announcementDate,
                salesAmount,
                operatingProfit,
                ordinaryProfit,
                netProfit,
                totalAssets,
                debtWithInterest,
                capitalFund,
                ownedCapital,
                dividend);
        return s;
    }

    @Override
    protected String getFindSql() {
        return String.format(
                "SELECT * FROM corporate_performance " +
                        "WHERE stock_id = %d " +
                        "AND settling_year = %d " +
                        "AND settling_month = %d " +
                        "LIMIT 1",
                this.stockId, this.settlingYear, this.settlingMonth);
    }

    @Override
    protected void setResultSet(ResultSet rs)
            throws SQLException, ParseException {
        String aDate = rs.getString("announcement_date");
        if (!rs.wasNull()) {
            this.announcementDate = new MyDate(aDate);
        } else {
            this.announcementDate = null;
        }
        if (rs.wasNull()) {
            this.announcementDate = null;
        }
        this.salesAmount = rs.getLong("sales_amount");
        if (rs.wasNull()) {
            this.salesAmount = null;
        }
        this.operatingProfit = rs.getLong("operating_profit");
        if (rs.wasNull()) {
            this.operatingProfit = null;
        }
        this.ordinaryProfit = rs.getLong("ordinary_profit");
        if (rs.wasNull()) {
            this.ordinaryProfit = null;
        }
        this.netProfit = rs.getLong("net_profit");
        if (rs.wasNull()) {
            this.netProfit = null;
        }
        this.totalAssets = rs.getLong("total_assets");
        if (rs.wasNull()) {
            this.totalAssets = null;
        }
        this.debtWithInterest = rs.getLong("debt_with_interest");
        if (rs.wasNull()) {
            this.debtWithInterest = null;
        }
        this.capitalFund = rs.getLong("capital_fund");
        if (rs.wasNull()) {
            this.capitalFund = null;
        }
        this.ownedCapital = rs.getLong("owned_capital");
        if (rs.wasNull()) {
            this.ownedCapital = null;
        }
        this.dividend = rs.getDouble("dividend");
        if (rs.wasNull()) {
            this.dividend = null;
        }
    }

    public void insert(Statement st) throws SQLException {
        String sql = String.format(
                "INSERT INTO corporate_performance(" +
                        "stock_id, settling_year, settling_month, " +
                        "announcement_date, " +
                        "sales_amount, operating_profit, ordinary_profit, net_profit, " +
                        "total_assets, debt_with_interest, capital_fund, " +
                        "owned_capital, dividend" +
                        ") values(%4d, %4d, %2d, date('%s'), %d, %d, %d, %d, %d, %d, %d, %d, %f)",
                stockId, settlingYear, settlingMonth,
                announcementDate,
                salesAmount, operatingProfit, ordinaryProfit, netProfit,
                totalAssets, debtWithInterest, capitalFund, ownedCapital,
                dividend);
        st.executeUpdate(sql);
    }

    public void update(Statement st) throws SQLException {
        int updateColumn = 0;
        String sql = "UPDATE corporate_performance SET ";
        if (announcementDate != null) {
            updateColumn++;
            sql += String.format("announcement_date = date('%s'), ", announcementDate);
        }
        if (salesAmount != null) {
            updateColumn++;
            sql += String.format("sales_amount = %d, ", salesAmount);
        }
        if (operatingProfit != null) {
            updateColumn++;
            sql += String.format("operating_profit = %d, ", operatingProfit);
        }
        if (ordinaryProfit != null) {
            updateColumn++;
            sql += String.format("ordinary_profit = %d, ", ordinaryProfit);
        }
        if (netProfit != null) {
            updateColumn++;
            sql += String.format("net_profit = %d, ", netProfit);
        }
        if (totalAssets != null) {
            updateColumn++;
            sql += String.format("total_assets = %d, ", totalAssets);
        }
        if (debtWithInterest != null) {
            updateColumn++;
            sql += String.format("debt_with_interest = %d, ", debtWithInterest);
        }
        if (capitalFund != null) {
            updateColumn++;
            sql += String.format("capital_fund = %d, ", capitalFund);
        }
        if (ownedCapital != null) {
            updateColumn++;
            sql += String.format("owned_capital = %d, ", ownedCapital);
        }
        if (dividend != null) {
            updateColumn++;
            sql += String.format("dividend = %f, ", dividend);
        }
        sql += "id = id ";
        sql += String.format(
                "WHERE stock_id = %d " +
                        "AND settling_year = %d " +
                        "AND settling_month = %d",
                stockId, settlingYear, settlingMonth);
        if (updateColumn > 0) {
            st.executeUpdate(sql);
        }
    }

    /**
     * 企業業績テーブル作成.
     *
     * @param c dbのコネクション
     */
    public static void createTable(Connection c)
            throws SQLException {
        String sql =
                "CREATE TABLE corporate_performance(" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "stock_id INT NOT NULL, " +
                        "settling_year INT NOT NULL, " +
                        "settling_month INT NOT NULL, " +
                        "sales_amount BIGINT, " +
                        "operating_profit BIGINT, " +
                        "ordinary_profit BIGINT, " +
                        "net_profit BIGINT, " +
                        "total_assets BIGINT, " +
                        "debt_with_interest BIGINT, " +
                        "capital_fund BIGINT, " +
                        "owned_capital BIGINT, " +
                        "dividend DOUBLE, " +
                        "announcement_date DATE, " +
                        "UNIQUE(stock_id, settling_year, settling_month)" +
                        ")";
        System.out.println(sql);
        c.createStatement().executeUpdate(sql);
    }

    public static void addAnnouncementDate(Connection c)
            throws SQLException {
        String sql =
                "ALTER TABLE corporate_performance " +
                        "ADD COLUMN announcement_date DATE DEFAULT NULL";
        System.out.println(sql);
        c.createStatement().executeUpdate(sql);
    }

    /**
     * 企業業績テーブルを削除.
     *
     * @param c dbのコネクション
     */
    public static void dropTable(Connection c)
            throws SQLException {
        String sql = "DROP TABLE IF EXISTS corporate_performance";
        System.out.println(sql);
        c.createStatement().executeUpdate(sql);
    }

    /**
     * MapのデータでDBをUpdateする.
     *
     * @param m モデルのmap
     * @param c dbのコネクション
     */
    public static void updateMap(Map<String, CorporatePerformance> m, Connection c)
            throws SQLException {
        Statement st = c.createStatement();
        for (String k : m.keySet()) {
            CorporatePerformance v = m.get(k);
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
    public static Map<String, CorporatePerformance> selectAll(Connection c)
            throws SQLException, ParseException {
        String sql = "SELECT * FROM corporate_performance";
        ResultSet rs = c.createStatement().executeQuery(sql);
        return parseResultSet(rs);
    }

    /**
     * 各銘柄ごとに、最新のデータを取得して返す.
     *
     * @param c dbのコネクション
     */
    public static Map<String, CorporatePerformance> selectLatests(Connection c)
            throws SQLException, ParseException {
        String sql =
                "SELECT cp.* " +
                        "FROM corporate_performance AS cp JOIN ( " +
                        "SELECT stock_id, MAX(settling_year) AS settling_year " +
                        "FROM corporate_performance GROUP BY stock_id " +
                        ") AS years " +
                        "ON cp.stock_id = years.stock_id AND cp.settling_year = years.settling_year";
        ResultSet rs = c.createStatement().executeQuery(sql);
        Map<String, CorporatePerformance> m = parseResultSet(rs);
        Map<String, CorporatePerformance> latests = new HashMap<String, CorporatePerformance>();
        for (String k : m.keySet()) {
            CorporatePerformance cp = m.get(k);
            latests.put(cp.getJoinKey(), cp);
        }
        return latests;
    }

    /**
     * 銘柄毎に指定した年数前の決算データを取得する.
     *
     * @param c       dbコネクション
     * @param yearAgo 何年前の決算データがほしいか
     * @return 銘柄毎の決算情報のMap
     */
    public static Map<String, CorporatePerformance> selectPasts(
            Connection c, int yearAgo) throws SQLException, ParseException {
        String sql = String.format(
                "SELECT cp.* " +
                        "FROM corporate_performance AS cp JOIN ( " +
                        "SELECT stock_id, MAX(settling_year) AS settling_year " +
                        "FROM corporate_performance GROUP BY stock_id " +
                        ") AS years " +
                        "ON cp.stock_id = years.stock_id " +
                        "AND cp.settling_year = (years.settling_year - %d)",
                yearAgo);
        ResultSet rs = c.createStatement().executeQuery(sql);
        Map<String, CorporatePerformance> m = parseResultSet(rs);
        Map<String, CorporatePerformance> pasts =
                new HashMap<String, CorporatePerformance>();
        for (String k : m.keySet()) {
            CorporatePerformance cp = m.get(k);
            pasts.put(cp.getJoinKey(), cp);
        }
        return pasts;
    }

    /**
     * 入力銘柄に対応する決算データ取得.
     *
     * @param stockId 銘柄ID
     * @param yearAgo 何年前の決算データがほしいか
     * @param c       dbコネクション
     */
    public static CorporatePerformance selectPastByStockId(
            int stockId, int yearAgo, Connection c)
            throws SQLException, ParseException {
        String sql = String.format(
                "SELECT * " +
                        "FROM corporate_performance " +
                        "WHERE stock_id = %d " +
                        "and settling_year = (" +
                        "select max(settling_year) " +
                        "from corporate_performance " +
                        "where stock_id = %d" +
                        ") - %d",
                stockId, stockId, yearAgo);
        ResultSet rs = c.createStatement().executeQuery(sql);
        if (rs.next()) {
            return parseResult(rs);
        } else {
            return null;
        }
    }

    /**
     * SQLで取得したResultSetをパースする.
     *
     * @param rs SQLで返ってきたResultSet
     */
    private static Map<String, CorporatePerformance>
    parseResultSet(ResultSet rs) throws SQLException, ParseException {
        Map<String, CorporatePerformance> m =
                new HashMap<String, CorporatePerformance>();
        while (rs.next()) {
            CorporatePerformance v = parseResult(rs);
            m.put(v.getKeyString(), v);
        }
        return m;
    }

    private static CorporatePerformance parseResult(ResultSet rs)
            throws SQLException, ParseException {
        int stockId = rs.getInt("stock_id");
        int settlingYear = rs.getInt("settling_year");
        int settlingMonth = rs.getInt("settling_month");
        CorporatePerformance v =
                new CorporatePerformance(stockId, settlingYear, settlingMonth);
        v.setResultSet(rs);
        return v;
    }
}
