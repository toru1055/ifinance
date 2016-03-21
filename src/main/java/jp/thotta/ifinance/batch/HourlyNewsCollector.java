package jp.thotta.ifinance.batch;

import jp.thotta.ifinance.common.MyDate;
import jp.thotta.ifinance.common.FailToScrapeException;
import jp.thotta.ifinance.common.ParseNewsPageException;
import jp.thotta.ifinance.model.CompanyNews;
import jp.thotta.ifinance.model.Database;
import jp.thotta.ifinance.collector.CompanyNewsCollector;
import jp.thotta.ifinance.collector.BaseCompanyNewsCollector;
import jp.thotta.ifinance.collector.news.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.io.IOException;
import java.util.List;
import java.lang.NullPointerException;

/**
 * できれば１時間に一回ニュースを収集
 * @author toru1055
 */
public class HourlyNewsCollector {
  Connection conn;

  public HourlyNewsCollector(Connection c) {
    this.conn = c;
  }

  public void collect() throws SQLException, ParseException, IOException {
    List<CompanyNewsCollector> collectors =
      BaseCompanyNewsCollector.getAllCollectors();
    for(CompanyNewsCollector coll : collectors) {
      try {
        coll.appendDb(conn);
      } catch(FailToScrapeException e) {
        e.printStackTrace();
      } catch(ParseNewsPageException e) {
        e.printStackTrace();
      } catch(NullPointerException e) {
        e.printStackTrace();
      }
    }
    try {
      PredictNikkeiNews pnn = new PredictNikkeiNews(conn);
      pnn.execPredict();
      pnn.insertDatabase();
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * ニュースデータ収集バッチ.
   */
  public static void main(String[] args) {
    try {
      Connection conn = Database.getConnection();
      HourlyNewsCollector collector = new HourlyNewsCollector(conn);
      if(args.length == 0) {
        collector.collect();
      } else {
        String command = args[0];
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
