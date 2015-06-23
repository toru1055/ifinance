package jp.thotta.ifinance.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
  private static Connection connection = null;
  private static final String DRIVER = "org.sqlite.JDBC";
  private static String dbUrl = "jdbc:sqlite:sample.db";

  /**
   * jdbcのURLを設定.
   * テスト時などにURLを変更する用
   * @param url JDBCのURL
   */
  public static void setDbUrl(String url) {
    dbUrl = url;
  }

  /**
   * データベースのコネクションをSingletonで取得.
   */
  public static Connection getConnection() throws SQLException {
    if(connection == null) {
      try {
        Class.forName(DRIVER);
      } catch(ClassNotFoundException e) {
        e.printStackTrace();
      }
      connection = DriverManager.getConnection(dbUrl);
    }
    return connection;
  }

  /**
   * データベースのコネクションを閉じる
   */
  public static void closeConnection() throws SQLException {
    if (connection != null) {
      connection.close();
      connection = null;
    }
  }
}
