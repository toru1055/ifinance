package jp.thotta.ifinance.model;

import jp.thotta.ifinance.common.MyDate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsReminderWeb {
    public Long id;
    public Long newsId;
    public Long userId;
    public Integer stockId;
    public MyDate createDate;
    public MyDate remindDate;
    public String message;

    public void setResultSet(ResultSet rs)
            throws SQLException, ParseException {
        this.id = rs.getLong("id");
        if (rs.wasNull()) {
            this.id = null;
        }
        this.newsId = rs.getLong("news_id");
        if (rs.wasNull()) {
            this.newsId = null;
        }
        this.userId = rs.getLong("user_id");
        if (rs.wasNull()) {
            this.userId = null;
        }
        this.stockId = rs.getInt("stock_id");
        if (rs.wasNull()) {
            this.stockId = null;
        }
        this.message = rs.getString("message");
        if (rs.wasNull()) {
            this.message = null;
        }
        Timestamp createTimestamp = rs.getTimestamp("create_date");
        if (!rs.wasNull()) {
            this.createDate = new MyDate(createTimestamp);
        }
        Timestamp remindTimestamp = rs.getTimestamp("remind_date");
        if (!rs.wasNull()) {
            this.remindDate = new MyDate(remindTimestamp);
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
                "CREATE TABLE news_reminder (" +
                        "id integer primary key AUTOINCREMENT," +
                        "news_id integer," +
                        "user_id integer," +
                        "stock_id integer," +
                        "create_date timestamp," +
                        "remind_date timestamp," +
                        "message text)";
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
        String sql = "DROP TABLE IF EXISTS news_reminder";
        System.out.println(sql);
        c.createStatement().executeUpdate(sql);
    }

    public static List<NewsReminderWeb>
    selectReminderByDate(MyDate sDate, MyDate eDate, Connection c)
            throws SQLException, ParseException {
        List<NewsReminderWeb> reminderList =
                new ArrayList<NewsReminderWeb>();
        String sql = String.format(
                "select * from news_reminder " +
                        "where remind_date >= %d " +
                        "and remind_date < %d",
                sDate.getTimeInMillis(),
                eDate.getTimeInMillis()
        );
        ResultSet rs = c.createStatement().executeQuery(sql);
        while (rs.next()) {
            NewsReminderWeb newsReminder = new NewsReminderWeb();
            newsReminder.setResultSet(rs);
            reminderList.add(newsReminder);
        }
        return reminderList;
    }

    public static Map<String, List<NewsReminderWeb>>
    categorizeByStockId(List<NewsReminderWeb> l) {
        Map<String, List<NewsReminderWeb>> m =
                new HashMap<String, List<NewsReminderWeb>>();
        for (NewsReminderWeb reminder : l) {
            String k = String.format("%4d", reminder.stockId);
            List<NewsReminderWeb> listOfStock = m.get(k);
            if (listOfStock == null) {
                listOfStock = new ArrayList<NewsReminderWeb>();
                m.put(k, listOfStock);
            }
            listOfStock.add(reminder);
        }
        return m;
    }

    public String toString() {
        return String.format(
                "id[%d], " +
                        "newsId[%d], " +
                        "userId[%d], " +
                        "stockId[%d], " +
                        "createDate[%s], " +
                        "remindDate[%s], " +
                        "message[%s]",
                id, newsId, userId, stockId,
                createDate, remindDate, message);
    }
}
