package jp.thotta.ifinance.batch;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.text.ParseException;

import jp.thotta.ifinance.utilizer.JoinedStockInfo;
import jp.thotta.ifinance.model.Database;
import jp.thotta.ifinance.model.CompanyNews;
import jp.thotta.ifinance.model.CompanyProfile;
import jp.thotta.ifinance.common.MyDate;

/**
 * 新着ニュースのあった銘柄をレポート
 * @author toru1055
 */
public class NewsReportBatch {
  Connection conn;

  public NewsReportBatch(Connection c) {
    this.conn = c;
  }

  /**
   * レポート実行.
   */
  public void report() throws SQLException, ParseException {
    Map<String, List<CompanyNews>> cnMap =
      CompanyNews.selectMapByDate(conn, MyDate.getToday(), 7);
    Map<String, JoinedStockInfo> jsiMap = JoinedStockInfo.selectMap(conn);
    Map<String, CompanyProfile> prMap = CompanyProfile.selectAll(conn);
    for(String k : cnMap.keySet()) {
      System.out.println("======= " + k + " =======");
      JoinedStockInfo jsi = jsiMap.get(k);
      CompanyProfile profile = prMap.get(k);
      List<CompanyNews> cnList = cnMap.get(k);
      if(jsi == null) {
        System.out.println(profile.getDescription());
      } else {
        System.out.println(jsi.getDescription());
      }
      for(CompanyNews news : cnList) {
        System.out.println(news.getDescription() + "\n");
      }
    }
  }

  public static void main(String[] args) {
    try {
      Connection c = Database.getConnection();
      NewsReportBatch reporter = new NewsReportBatch(c);
      if(args.length == 0) {
        reporter.report();
      } else {
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
