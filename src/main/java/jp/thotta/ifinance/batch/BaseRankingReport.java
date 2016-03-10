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
 * 値上り予想ランキングレポートの抽象クラス.
 */
public abstract class BaseRankingReport {
  Connection conn;
  String tmpl = "text";
  String title = null;
  Map<String, PredictedStockPrice> pspMap;
  Map<String, List<CompanyNews>> cnMap;
  Map<String, DailyStockPrice> dspMap;
  Map<String, JoinedStockInfo> jsiMap;
  Map<String, CompanyProfile> prMap;

  public BaseRankingReport(Connection c, String tmpl, String title)
    throws SQLException, ParseException {
    this.conn = c;
    this.tmpl = tmpl;
    this.title = title;
    this.pspMap = PredictedStockPrice.selectLatestMap(conn);
    this.dspMap = DailyStockPrice.selectLatests(conn);
    this.jsiMap = JoinedStockInfo.selectAllMap(conn);
    this.prMap = CompanyProfile.selectAll(conn);
    setCompanyNewsMap();
  }

  /**
   * 予測対象ニュースを取得(cnMap).
   */
  abstract protected void setCompanyNewsMap() throws SQLException, ParseException;

  /**
   * 銘柄に対して値上り率予想を付ける(scoreMap).
   */
  abstract protected Map<String, Double> estimatePriceIncreaseRatio();

  public void report() {
    Map<String, Double> scoreMap = estimatePriceIncreaseRatio();
    if(tmpl.equals("html")) {
      ReportPrinter.printHtmlHeader(title);
    }
    int counter = 0;
    for(String k : valueSortedKeys(scoreMap)) {
      if(counter++ >= 30) { break; }
      double score = scoreMap.get(k);
      String message = String.format(
          "[予想値上り率スコア: %.1f％]", score);
      if(tmpl.equals("text")) {
        printText(k, counter, message);
      } else if(tmpl.equals("html")) {
        printHtml(k, counter, message);
      }
    }
    if(tmpl.equals("html")) {
      ReportPrinter.printHtmlFooter();
    }
  }

  List<String> valueSortedKeys(Map<String, Double> scoreMap) {
    final Map<String, Double> score = new HashMap(scoreMap);
    List<String> keys = new ArrayList<String>(scoreMap.keySet());
    Collections.sort(keys, new Comparator<String>() {
      @Override
      public int compare(String k1, String k2) {
        if(score.get(k1) != score.get(k2)) {
          return score.get(k1) > score.get(k2) ? -1 : 1;
        } else {
          return k1.compareTo(k2);
        }
      }
    });
    return keys;
  }

  void printHtml(String stockId, int counter, String message) {
    String k = stockId;
    JoinedStockInfo jsi = jsiMap.get(k);
    CompanyProfile profile = prMap.get(k);
    DailyStockPrice dsp = dspMap.get(k);
    PredictedStockPrice psp = pspMap.get(k);
    List<CompanyNews> cnList = cnMap.get(k);
    StockInfoPrinter sip = new StockInfoPrinter(
        jsi, profile, null, dsp, psp, cnList, null, message);
    sip.rank = counter;
    sip.isWeeklyChart = true;
    sip.printStockElements();
  }

  void printText(String stockId, int counter, String message) {
    String k = stockId;
    JoinedStockInfo jsi = jsiMap.get(k);
    CompanyProfile profile = prMap.get(k);
    DailyStockPrice dsp = dspMap.get(k);
    PredictedStockPrice psp = pspMap.get(k);
    List<CompanyNews> cnList = cnMap.get(k);
    System.out.println("======= [" + counter + "]" + k + " =======");
    System.out.println(message);
    ReportPrinter.printStockDescriptions(
        jsi, profile, null, dsp, psp, cnList, null);
  }
}
