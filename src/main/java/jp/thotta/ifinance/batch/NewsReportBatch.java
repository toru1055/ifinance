package jp.thotta.ifinance.batch;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.text.ParseException;

import jp.thotta.ifinance.utilizer.JoinedStockInfo;
import jp.thotta.ifinance.model.Database;
import jp.thotta.ifinance.model.CompanyNews;
import jp.thotta.ifinance.model.CompanyProfile;
import jp.thotta.ifinance.model.DailyStockPrice;
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
    Map<String, DailyStockPrice> dspMap = DailyStockPrice.selectLatests(conn);
    for(String k : cnMap.keySet()) {
      System.out.println("======= " + k + " =======");
      JoinedStockInfo jsi = jsiMap.get(k);
      CompanyProfile profile = prMap.get(k);
      DailyStockPrice dsp = dspMap.get(k);
      List<CompanyNews> cnList = cnMap.get(k);
      if(jsi == null) {
        System.out.println(profile.getDescription() + "\n");
        System.out.println(dsp.getDescription() + "\n");
      } else {
        System.out.println(jsi.getDescription());
      }
      for(CompanyNews news : cnList) {
        System.out.println(news.getDescription() + "\n");
      }
    }
  }

  /**
   * 話題の銘柄レポート.
   */
  public void reportHotTopics() throws SQLException, ParseException {
    final Map<String, CompanyNews> cnMap =
      CompanyNews.selectMapLatestHotTopics(conn);
    Map<String, List<CompanyNews>> cnMapNews =
      CompanyNews.selectMapByPast(conn, 7);
    Map<String, JoinedStockInfo> jsiMap = JoinedStockInfo.selectMap(conn);
    Map<String, CompanyProfile> prMap = CompanyProfile.selectAll(conn);
    Map<String, DailyStockPrice> dspMap = DailyStockPrice.selectLatests(conn);
    List<String> keys = new ArrayList<String>();
    for(String k : cnMap.keySet()) {
      keys.add(k);
    }
    Collections.sort(keys, new Comparator<String>() {
      @Override
      public int compare(String k1, String k2) {
        return cnMap.get(k1).url.compareTo(cnMap.get(k2).url);
      }
    });
    for(String k : keys) {
      System.out.println("======= " + k + " =======");
      JoinedStockInfo jsi = jsiMap.get(k);
      CompanyProfile profile = prMap.get(k);
      CompanyNews cn = cnMap.get(k);
      DailyStockPrice dsp = dspMap.get(k);
      List<CompanyNews> cnList = cnMapNews.get(k);
      if(jsi == null) {
        if(profile == null || dsp == null) {
          System.out.println("この銘柄はデータベースに存在しません");
          continue;
        } else {
          System.out.println(profile.getDescription() + "\n");
          System.out.println(dsp.getDescription() + "\n");
        }
      } else {
        System.out.println(jsi.getDescription());
      }
      System.out.println(cn.getDescription() + "\n");
      System.out.println("■この銘柄の直近ニュース");
      if(cnList != null && cnList.size() > 0) {
        for(CompanyNews news : cnList) {
          System.out.println(news.getDescription() + "\n");
        }
      } else {
        System.out.println("直近のニュースはありません or この銘柄はまだクロールしていません\n");
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
        if(args[0].equals("HotTopics")) {
          reporter.reportHotTopics();
        }
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
