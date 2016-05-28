package jp.thotta.ifinance.model;

import jp.thotta.ifinance.common.MyDate;
import junit.framework.TestCase;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class NewsReminderWebTest extends TestCase {
    Connection c;

    protected void setUp() {
        try {
            DatabaseWeb.setDbUrl("jdbc:sqlite:test.db");
            c = DatabaseWeb.getConnection();
            NewsReminderWeb.dropTable(c);
            NewsReminderWeb.createTable(c);
        } catch (SQLException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testSelectReminderByFuture() {
        // create: 2015-12-06 12:13:11.000
        // remind: 2015-12-25 00:00:00.000
        insert(1L, 1L, 1111, 1449371591000L, 1450969200000L, "２５");
        insert(2L, 2L, 1112, 1449371491000L, 1451055600000L, "２６");
        insert(3L, 3L, 1113, 1449371691000L, 1451142000000L, "２７");
        insert(2L, 4L, 1112, 1449371791000L, 1451228400000L, "２８");
        insert(3L, 2L, 1113, 1449371891000L, 1451314800000L, "２９");
        insert(4L, 1L, 1114, 1449371191000L, 1451401200000L, "３０");
        try {
            List<NewsReminderWeb> reminderList =
                    NewsReminderWeb.selectReminderByDate(
                            new MyDate(2015, 12, 26),
                            new MyDate(2015, 12, 28), c);
            for (NewsReminderWeb reminder : reminderList) {
                System.out.println(reminder.message);
            }
            assertEquals(reminderList.size(), 2);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        assertTrue(true);
    }

    private void insert(Long newsId,
                        Long userId,
                        Integer stockId,
                        Long createDate,
                        Long remindDate,
                        String message) {
        try {
            String sql = String.format(
                    "insert into news_reminder(" +
                            "news_id, user_id, stock_id, " +
                            "create_date, remind_date, message" +
                            ") values(" +
                            "%d, %d, %d, " +
                            "%d, %d, '%s'" +
                            ")",
                    newsId, userId, stockId,
                    createDate, remindDate, message);
            c.createStatement().executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    protected void tearDown() {
        try {
            DatabaseWeb.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
