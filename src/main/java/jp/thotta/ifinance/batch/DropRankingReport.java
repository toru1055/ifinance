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

/**
 * 各種期間で株価下落率上位をレポート.
 * @author toru1055
 */
public class DropRankingReport {
  Connection conn;
  String tmpl = "text";
  Map<String, PredictedStockPrice> pspMap;
  Map<String, List<CompanyNews>> cnMap;
  Map<String, DailyStockPrice> dspMap;
  Map<String, JoinedStockInfo> jsiMap;
  Map<String, CompanyProfile> prMap;

  public DropRankingReport(Connection c, String tmpl) 
    throws SQLException, ParseException {
    this.conn = c;
    this.tmpl = tmpl;
    pspMap = PredictedStockPrice.selectLatestMap(conn);
    cnMap = CompanyNews.selectLatestMap(conn);
    dspMap = DailyStockPrice.selectLatests(conn);
    jsiMap = JoinedStockInfo.selectMap(conn);
    prMap = CompanyProfile.selectAll(conn);
  }

  public void printOne(int days) throws SQLException, ParseException {
    Map<Integer, Double> dropRank =
      DailyStockPrice.selectDropStockRanking(days, conn);
    if(tmpl.equals("text")) {
      System.out.println("=== " + days + "日間の下落率ランキング");
    } else if(tmpl.equals("html")) {
      System.out.println("<h2>" + days + "日間の下落率ランキング</h2>");
    }
    int counter = 0;
    for(Integer stockId : dropRank.keySet()) {
      double dropRatio = dropRank.get(stockId);
      String k = String.format("%d", stockId);
      if(counter++ >= 10) { break; }
      JoinedStockInfo jsi = jsiMap.get(k);
      CompanyProfile profile = prMap.get(k);
      DailyStockPrice dsp = dspMap.get(k);
      PredictedStockPrice psp = pspMap.get(k);
      List<CompanyNews> cnList = cnMap.get(k);
      String message = String.format(
          "[%d日間の下落率: %.1f％]", days, dropRatio * 100);
      if(tmpl.equals("text")) {
        System.out.println("======= [" + counter + "]" + k + " =======");
        System.out.println(message);
        ReportPrinter.printStockDescriptions(jsi, profile, null, dsp, psp, cnList, null);
      } else if(tmpl.equals("html")) {
        StockInfoPrinter sip = new StockInfoPrinter(jsi, profile, null, dsp, psp, cnList, null, message);
        sip.rank = counter;
        sip.printStockElements();
      }
    }
  }

  /**
   * レポート実行.
   */
  public void report() throws SQLException, ParseException {
    int daysList[] = {7, 14, 28};
    if(tmpl.equals("html")) {
      ReportPrinter.printHtmlHeader("株価下降率ランキング");
    }
    for(int i = 0; i < daysList.length; i++) {
      printOne(daysList[i]);
    }
    if(tmpl.equals("html")) {
      ReportPrinter.printHtmlFooter();
    }
  }

  public static void main(String[] args) {
    try {
      Connection c = Database.getConnection();
      String tmpl = "text";
      if(args.length > 0) {
        tmpl = args[0];
      }
      DropRankingReport reporter = new DropRankingReport(c, tmpl);
      reporter.report();
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
