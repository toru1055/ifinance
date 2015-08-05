package jp.thotta.ifinance.batch;

import java.sql.Connection;
import java.sql.SQLException;

import jp.thotta.ifinance.extractor.*;
import jp.thotta.ifinance.model.Database;

/**
 * データ抽出バッチ.
 * @author toru1055
 */
public class Extractor {
  Connection conn;

  public Extractor(Connection c) {
    this.conn = c;
  }

  public void exec(String command) {
    if(command.equals("ActualPredicted")) {
      try {
        ActualPredictedExtractor ex = new ActualPredictedExtractor(conn);
        ex.extract();
      } catch(Exception e) {
        e.printStackTrace();
        System.exit(1);
      }
    }
  }

  public static void main(String[] args) {
    try {
      Connection c = Database.getConnection();
      Extractor extractor = new Extractor(c);
      if(args.length == 0) {
        System.out.println("Error at: Extractor");
        System.exit(1);
      } else {
        extractor.exec(args[0]);
      }
    } catch(Exception e) {
      e.printStackTrace();
    } finally {
      try {
        Database.closeConnection();
      } catch(SQLException e) {
        e.printStackTrace();
      }
    }
  }
}
