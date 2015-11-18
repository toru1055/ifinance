package jp.thotta.ifinance.batch;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;
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
  boolean isGrow = false;
  Map<String, PredictedStockPrice> pspMap;
  Map<String, List<CompanyNews>> cnMap;
  Map<String, DailyStockPrice> dspMap;
  Map<String, JoinedStockInfo> jsiMap;
  Map<String, CompanyProfile> prMap;

  public DropRankingReport(Connection c, String tmpl, boolean isGrow) 
    throws SQLException, ParseException {
    this.conn = c;
    this.tmpl = tmpl;
    this.isGrow = isGrow;
    pspMap = PredictedStockPrice.selectLatestMap(conn);
    cnMap = CompanyNews.selectLatestMap(conn);
    dspMap = DailyStockPrice.selectLatests(conn);
    jsiMap = JoinedStockInfo.selectAllMap(conn);
    prMap = CompanyProfile.selectAll(conn);
  }

  public void printOne(int days) throws SQLException, ParseException {
    Map<Integer, Double> dropRank =
      DailyStockPrice.selectDropStockRanking(days, conn);
    String prefix = "";
    if(isGrow) {
      prefix = "成長企業の";
    }
    if(tmpl.equals("text")) {
      System.out.println("=== " + prefix + days + "日間の下落率ランキング");
    } else if(tmpl.equals("html")) {
      System.out.println("<h2>" + prefix + days + "日間の下落率ランキング</h2>");
    }
    int counter = 0;
    for(Integer stockId : valueSortedKeys(dropRank)) {
      double dropRatio = dropRank.get(stockId);
      String k = String.format("%d", stockId);
      JoinedStockInfo jsi = jsiMap.get(k);
      PredictedStockPrice psp = pspMap.get(k);
      if(isGrow && !jsi.isGrowing()) { continue; }
      //if(isGrow && psp != null && psp.undervaluedRate() < -0.5) { continue; }
      if(counter++ >= 10) { break; }
      CompanyProfile profile = prMap.get(k);
      DailyStockPrice dsp = dspMap.get(k);
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

  List<Integer> valueSortedKeys(Map<Integer, Double> dropRank) {
    final Map<Integer, Double> rank = new HashMap(dropRank);
    List<Integer> keys = new ArrayList<Integer>();
    for(Integer k : rank.keySet()) {
      keys.add(k);
    }
    Collections.sort(keys, new Comparator<Integer>() {
      @Override
      public int compare(Integer k1, Integer k2) {
        if(rank.get(k1) != rank.get(k2)) {
          return rank.get(k1) < rank.get(k2) ? -1 : 1;
        } else {
          return k1 < k2 ? -1 : 1;
        }
      }
    });
    return keys;
  }

  /**
   * レポート実行.
   */
  public void report() throws SQLException, ParseException {
    int daysList[] = {7, 14, 28};
    if(tmpl.equals("html")) {
      if(isGrow) {
        ReportPrinter.printHtmlHeader("成長企業の株価下降率");
      } else {
        ReportPrinter.printHtmlHeader("株価下降率ランキング");
      }
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
      boolean isGrow = false;
      if(args.length >= 1) {
        tmpl = args[0];
        if(args.length >= 2) {
          isGrow = args[1].equals("grow");
        }
      }
      DropRankingReport reporter = new DropRankingReport(c, tmpl, isGrow);
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
