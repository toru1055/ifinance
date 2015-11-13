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
 * 過去一週間の株価上昇率とニュースを送信.
 * @author toru1055
 */
public class WeeklyNewsReport {
  Connection conn;

  public WeeklyNewsReport(Connection c) {
    this.conn = c;
  }

  /**
   * レポート実行.
   */
  public void report(int past, String tmpl) throws SQLException, ParseException {
    Map<String, PredictedStockPrice> pspMap =
      PredictedStockPrice.selectLatestMap(conn);
    Map<String, List<CompanyNews>> cnMap =
      CompanyNews.selectLatestMap(conn);
    final Map<String, DailyStockPrice> pastDspMap =
      DailyStockPrice.selectPasts(conn, past);
    final Map<String, DailyStockPrice> latestDspMap =
      DailyStockPrice.selectLatests(conn);
    Map<String, JoinedStockInfo> jsiMap = JoinedStockInfo.selectAllMap(conn);
    Map<String, CompanyProfile> prMap = CompanyProfile.selectAll(conn);
    List<String> keys = new ArrayList<String>();
    for(String k : cnMap.keySet()) {
      keys.add(k);
    }
    Collections.sort(keys, new Comparator<String>() {
      @Override
      public int compare(String k1, String k2) {
        double liftRatio1 = getLiftRatio(k1, pastDspMap, latestDspMap);
        double liftRatio2 = getLiftRatio(k2, pastDspMap, latestDspMap);
        return liftRatio1 > liftRatio2 ? -1 : 1;
      }
    });
    if(tmpl.equals("html")) {
      ReportPrinter.printHtmlHeader("週刊まとめニュース");
    }
    System.out.println(
        String.format(
          "【過去%d日間での全銘柄株価上昇率】%.1f％\n",
          past, getTotalLiftRatio(pastDspMap, latestDspMap) * 100)
        );

    if(tmpl.equals("text")) {
      System.out.println("= 値上り率ランキング");
    } else if(tmpl.equals("html")) {
      System.out.println("<h2>値上り率ランキング</h2>");
    }

    int counter = 0;
    for(String k : keys) {
      if(counter++ >= 20) { break; }
      JoinedStockInfo jsi = jsiMap.get(k);
      CompanyProfile profile = prMap.get(k);
      DailyStockPrice dsp = latestDspMap.get(k);
      PredictedStockPrice psp = pspMap.get(k);
      List<CompanyNews> cnList = cnMap.get(k);
      double liftRatio = getLiftRatio(k, pastDspMap, latestDspMap);
      String message = String.format(
          "[過去%d日間の株価上昇率: %.1f％]", past, liftRatio * 100);
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

    if(tmpl.equals("text")) {
      System.out.println("= 値下り率ランキング");
    } else if(tmpl.equals("html")) {
      System.out.println("<h2>値下り率ランキング</h2>");
    }

    counter = 0;
    Collections.reverse(keys);
    for(String k : keys) {
      if(counter++ >= 20) { break; }
      JoinedStockInfo jsi = jsiMap.get(k);
      CompanyProfile profile = prMap.get(k);
      DailyStockPrice dsp = latestDspMap.get(k);
      PredictedStockPrice psp = pspMap.get(k);
      List<CompanyNews> cnList = cnMap.get(k);
      double liftRatio = getLiftRatio(k, pastDspMap, latestDspMap);
      String message = String.format(
          "[過去%d日間の株価上昇率: %.1f％]", past, liftRatio * 100);
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

    if(tmpl.equals("html")) {
      ReportPrinter.printHtmlFooter();
    }
  }

  static double getTotalLiftRatio(
      Map<String, DailyStockPrice> pastDspMap,
      Map<String, DailyStockPrice> latestDspMap) {
    double pastTotal = 0.0;
    double latestTotal = 0.0;
    for(String k : pastDspMap.keySet()) {
      DailyStockPrice pastDsp = pastDspMap.get(k);
      DailyStockPrice latestDsp = latestDspMap.get(k);
      if(pastDsp != null && pastDsp.hasEnough() &&
         latestDsp != null && latestDsp.hasEnough()) {
        pastTotal += (double)pastDsp.marketCap;
        latestTotal += (double)latestDsp.marketCap;
      }
    }
    return (latestTotal - pastTotal) / pastTotal;
  }

  static double getLiftRatio(String k, 
      Map<String, DailyStockPrice> pastDspMap, 
      Map<String, DailyStockPrice> latestDspMap) {
    DailyStockPrice pastDsp = pastDspMap.get(k);
    DailyStockPrice latestDsp = latestDspMap.get(k);
    if(pastDsp != null && latestDsp != null) {
      double liftRatio = (double)(latestDsp.marketCap - pastDsp.marketCap) / pastDsp.marketCap;
      return liftRatio;
    } else {
      return 0.0;
    }
  }

  public static void main(String[] args) {
    try {
      Connection c = Database.getConnection();
      WeeklyNewsReport reporter = new WeeklyNewsReport(c);
      String tmpl = "text";
      if(args.length == 0) {
        reporter.report(7, tmpl);
      } else {
        if(args.length >= 2) {
          tmpl = args[1];
        }
        int past = Integer.parseInt(args[0]);
        reporter.report(past, tmpl);
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
