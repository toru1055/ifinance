package jp.thotta.ifinance.model;

import junit.framework.TestCase;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseWebTest extends TestCase {
    public void testConnection() {
        Connection c;
        try {
            DatabaseWeb.setDbUrl("jdbc:sqlite:test.db");
            c = DatabaseWeb.getConnection();
        } catch (SQLException e) {
            fail(e.getMessage());
        } finally {
            try {
                DatabaseWeb.closeConnection();
            } catch (SQLException e) {
                fail(e.getMessage());
            }
        }
    }
}
