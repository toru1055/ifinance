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
import jp.thotta.ifinance.utilizer.PredictedStockPrice;
import jp.thotta.ifinance.model.Database;
import jp.thotta.ifinance.model.CompanyNews;
import jp.thotta.ifinance.model.CompanyProfile;
import jp.thotta.ifinance.model.DailyStockPrice;
import jp.thotta.ifinance.common.MyDate;
import jp.thotta.ifinance.collector.CompanyNewsCollector;
import jp.thotta.ifinance.collector.BaseCompanyNewsCollector;

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
  public void report(String tmpl) throws SQLException, ParseException {
    Map<String, PredictedStockPrice> pspMap =
      PredictedStockPrice.selectLatestMap(conn);
    Map<String, List<CompanyNews>> cnMap =
      CompanyNews.selectMapByDate(conn, MyDate.getToday(), 7);
    Map<String, JoinedStockInfo> jsiMap = JoinedStockInfo.selectMap(conn);
    Map<String, CompanyProfile> prMap = CompanyProfile.selectAll(conn);
    Map<String, DailyStockPrice> dspMap = DailyStockPrice.selectLatests(conn);
    if(tmpl.equals("html")) {
      ReportPrinter.printHtmlHeader("銘柄ニュースリリース");
    }
    for(String k : cnMap.keySet()) {
      JoinedStockInfo jsi = jsiMap.get(k);
      CompanyProfile profile = prMap.get(k);
      DailyStockPrice dsp = dspMap.get(k);
      PredictedStockPrice psp = pspMap.get(k);
      List<CompanyNews> cnList = cnMap.get(k);
      if(tmpl.equals("text")) {
        System.out.println("======= " + k + " =======");
        ReportPrinter.printStockDescriptions(jsi, profile, null, dsp, psp, cnList, null);
      } else if(tmpl.equals("html")) {
        StockInfoPrinter sip = new StockInfoPrinter(jsi, profile, null, dsp, psp, cnList, null);
        sip.printStockElements();
      }
    }
    if(tmpl.equals("html")) {
      ReportPrinter.printHtmlFooter();
    }
  }

  /**
   * 話題の銘柄レポート.
   */
  public void reportHotTopics(String tmpl) throws SQLException, ParseException {
    Map<String, PredictedStockPrice> pspMap =
      PredictedStockPrice.selectLatestMap(conn);
    Map<String, CompanyNewsCollector> collMap =
      BaseCompanyNewsCollector.getStockCollectorMap();
    final Map<String, CompanyNews> cnMap =
      CompanyNews.selectMapLatestHotTopics(conn);
    Map<String, List<CompanyNews>> cnMapNews =
      CompanyNews.selectLatestMap(conn);
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
    if(tmpl.equals("html")) {
      ReportPrinter.printHtmlHeader("話題の銘柄ランキング");
    }
    for(String k : keys) {
      JoinedStockInfo jsi = jsiMap.get(k);
      CompanyProfile profile = prMap.get(k);
      CompanyNews cn = cnMap.get(k);
      DailyStockPrice dsp = dspMap.get(k);
      PredictedStockPrice psp = pspMap.get(k);
      List<CompanyNews> cnList = cnMapNews.get(k);
      CompanyNewsCollector coll = collMap.get(k);
      if(tmpl.equals("text")) {
        System.out.println("======= " + k + " =======");
        ReportPrinter.printStockDescriptions(jsi, profile, cn, dsp, psp, cnList, coll);
      } else if(tmpl.equals("html")) {
        StockInfoPrinter sip = new StockInfoPrinter(jsi, profile, cn, dsp, psp, cnList, coll);
        sip.printStockElements();
      }
    }
    if(tmpl.equals("html")) {
      ReportPrinter.printHtmlFooter();
    }
  }

  public static void main(String[] args) {
    try {
      Connection c = Database.getConnection();
      NewsReportBatch reporter = new NewsReportBatch(c);
      String tmpl = "text";
      if(args.length == 0) {
        reporter.report(tmpl);
      } else {
        if(args.length >= 2) {
          tmpl = args[1];
        }
        if(args[0].equals("HotTopics")) {
          reporter.reportHotTopics(tmpl);
        } else if(args[0].equals("NewsRelease")) {
          reporter.report(tmpl);
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
