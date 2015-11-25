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
 * 各種期間で各種ランキング上位をレポート.
 * @author toru1055
 */
public class GeneralRankingReport {
  public static final int TYPE_RISE_DROP = 1;
  public static final int TYPE_DROP_ONLY = 2;
  public static final int TYPE_RISE_ONLY = 3;
  public static final int TYPE_DROP_FLOOR = 4;

  Connection conn;
  String tmpl = "text";
  boolean isGrow = false;
  int type = TYPE_RISE_DROP;
  Map<String, PredictedStockPrice> pspMap;
  Map<String, List<CompanyNews>> cnMap;
  Map<String, DailyStockPrice> dspMap;
  Map<String, JoinedStockInfo> jsiMap;
  Map<String, CompanyProfile> prMap;

  public GeneralRankingReport(Connection c,
                              String tmpl,
                              boolean isGrow,
                              int type) 
    throws SQLException, ParseException {
    this.conn = c;
    this.tmpl = tmpl;
    this.isGrow = isGrow;
    this.type = type;
    pspMap = PredictedStockPrice.selectLatestMap(conn);
    cnMap = CompanyNews.selectLatestMap(conn);
    dspMap = DailyStockPrice.selectLatests(conn);
    jsiMap = JoinedStockInfo.selectAllMap(conn);
    prMap = CompanyProfile.selectAll(conn);
  }

  Map<Integer, Double> getRankingMap(int days) throws SQLException, ParseException {
    switch(type) {
      case TYPE_RISE_DROP:
        return DailyStockPrice.selectRiseDropRanking(days, conn);
      case TYPE_DROP_ONLY:
        return DailyStockPrice.selectDropStockRanking(days, conn);
      case TYPE_RISE_ONLY:
        return DailyStockPrice.selectRiseStockRanking(days, conn);
      case TYPE_DROP_FLOOR:
        return DailyStockPrice.selectReachedFloorRanking(14, days, conn);
      default:
        return null;
    }
  }

  String getTitlePhrase() {
    switch(type) {
      case TYPE_RISE_DROP:
        return "行ってこい度ランキング";
      case TYPE_DROP_ONLY:
        return "株価下落率ランキング";
      case TYPE_RISE_ONLY:
        return "株価上昇率ランキング";
      case TYPE_DROP_FLOOR:
        return "下げ止まり度ランキング";
      default:
        return null;
    }
  }

  public void printOne(int days) throws SQLException, ParseException {
    Map<Integer, Double> rankMap = getRankingMap(days);
    String prefix = "";
    if(isGrow) {
      prefix = "成長企業の";
    }
    if(tmpl.equals("text")) {
      System.out.println("=== " + prefix + days +
          "日間の" + getTitlePhrase());
    } else if(tmpl.equals("html")) {
      System.out.println("<h2>" + prefix + days +
          "日間の" + getTitlePhrase() + "</h2>");
    }
    int counter = 0;
    for(Integer stockId : valueSortedKeys(rankMap)) {
      double score = rankMap.get(stockId);
      String k = String.format("%d", stockId);
      JoinedStockInfo jsi = jsiMap.get(k);
      PredictedStockPrice psp = pspMap.get(k);
      if(isGrow && !jsi.isGrowing()) { continue; }
      if(counter++ >= 10) { break; }
      CompanyProfile profile = prMap.get(k);
      DailyStockPrice dsp = dspMap.get(k);
      List<CompanyNews> cnList = cnMap.get(k);
      String message = String.format(
          "[%d日間のスコア: %.1f％]", days, score * 100);
      if(tmpl.equals("text")) {
        System.out.println("======= [" + counter + "]" + k + " =======");
        System.out.println(message);
        ReportPrinter.printStockDescriptions(
            jsi, profile, null, dsp, psp, cnList, null);
      } else if(tmpl.equals("html")) {
        StockInfoPrinter sip = new StockInfoPrinter(
            jsi, profile, null, dsp, psp, cnList, null, message);
        sip.rank = counter;
        sip.showChart = true;
        sip.printStockElements();
      }
    }
  }

  List<Integer> valueSortedKeys(Map<Integer, Double> rankMap) {
    final Map<Integer, Double> rank = new HashMap(rankMap);
    List<Integer> keys = new ArrayList<Integer>();
    for(Integer k : rank.keySet()) {
      keys.add(k);
    }
    Collections.sort(keys, new Comparator<Integer>() {
      @Override
      public int compare(Integer k1, Integer k2) {
        if(rank.get(k1) != rank.get(k2)) {
          switch(type) {
            case TYPE_DROP_ONLY:
              return ascending(rank.get(k1), rank.get(k2));
            case TYPE_RISE_DROP:
              return descending(rank.get(k1), rank.get(k2));
            case TYPE_RISE_ONLY:
              return descending(rank.get(k1), rank.get(k2));
            case TYPE_DROP_FLOOR:
              return descending(rank.get(k1), rank.get(k2));
            default:
              return ascending(rank.get(k1), rank.get(k2));
          }
        } else {
          return k1 < k2 ? -1 : 1;
        }
      }
    });
    return keys;
  }

  int ascending(double score1, double score2) {
    return score1 < score2 ? -1 : 1;
  }

  int descending(double score1, double score2) {
    return score1 > score2 ? -1 : 1;
  }

  int[] getDaysList() {
    switch(type) {
      case TYPE_DROP_ONLY:
        int a[] = {7, 14, 28};
        return a;
      case TYPE_RISE_DROP:
        int b[] = {30, 60, 90};
        return b;
      case TYPE_DROP_FLOOR:
        int c[] = {3, 5, 7};
        return c;
      default:
        int z[] = {7, 14, 28};
        return z;
    }
  }

  /**
   * レポート実行.
   */
  public void report() throws SQLException, ParseException {
    int daysList[] = getDaysList();
    String prefix = "";
    if(isGrow) {
      prefix = "成長企業の";
    }
    if(tmpl.equals("html")) {
      ReportPrinter.printHtmlHeader(prefix + getTitlePhrase());
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
      int type = GeneralRankingReport.TYPE_RISE_DROP;
      if(args.length >= 1) {
        tmpl = args[0];
        if(args.length >= 2) {
          isGrow = args[1].equals("grow");
        }
        if(args.length >= 3) {
          String sType = args[2];
          if("rise-drop".equals(sType)) {
            type = GeneralRankingReport.TYPE_RISE_DROP;
          } else if("drop-only".equals(sType)) {
            type = GeneralRankingReport.TYPE_DROP_ONLY;
          } else if("rise-only".equals(sType)) {
            type = GeneralRankingReport.TYPE_RISE_ONLY;
          } else if("drop-floor".equals(sType)) {
            type = GeneralRankingReport.TYPE_DROP_FLOOR;
          } else {
            type = GeneralRankingReport.TYPE_RISE_DROP;
          }
        }
      }
      GeneralRankingReport reporter = new GeneralRankingReport(c, tmpl, isGrow, type);
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
