package jp.thotta.ifinance.batch;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.sql.Connection;
import java.sql.SQLException;

import jp.thotta.ifinance.model.Database;

public class DailyCollectorTest extends TestCase {

  public void testCollect() {
    /*
    try {
      Database.setDbUrl("jdbc:sqlite:collector_test.db");
      Connection conn = Database.getConnection();
      Initializer init = new Initializer(conn);
      init.dropTables();
      init.createTables();
      DailyCollector collector = new DailyCollector(conn);
      collector.collect();
    } catch(Exception e) {
      e.printStackTrace();
    } finally {
      try {
        Database.closeConnection();
      } catch(SQLException e) {
        e.printStackTrace();
      }
    }
    */
  }
}
