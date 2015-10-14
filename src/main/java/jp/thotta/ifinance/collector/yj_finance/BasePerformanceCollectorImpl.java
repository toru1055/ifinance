package jp.thotta.ifinance.collector.yj_finance;

import jp.thotta.ifinance.collector.FinancialAmountCollector;
import jp.thotta.ifinance.model.CorporatePerformance;
import jp.thotta.ifinance.common.MyDate;
import jp.thotta.ifinance.common.Scraper;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.IOException;

import java.sql.Connection;
import java.sql.SQLException;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

/**
 * Y!Financeの個別銘柄の決算情報を取得.
 * @author toru1055
 */
public class BasePerformanceCollectorImpl
  implements FinancialAmountCollector {
  private static final String CONSOLIDATE_URL_FORMAT
    = "http://profile.yahoo.co.jp/consolidate/%4d";
  private static final String INDEPEND_URL_FORMAT
    = "http://profile.yahoo.co.jp/independent/%4d";
  private List<Integer> stockIdList;

  public BasePerformanceCollectorImpl(List<Integer> stockIdList) {
    this.stockIdList = stockIdList;
  }

  /**
   * コンストラクタで設定されたstockIdのリストから、
   * 個別銘柄の決算情報を取得し、DBに登録.
   */
  public void appendDb(Connection conn) 
    throws SQLException, IOException {
    Map<String, CorporatePerformance> m;
    for(Integer stockId : stockIdList) {
      m = new HashMap<String, CorporatePerformance>();
      List<CorporatePerformance> cpListI = 
        parseIndependentPerformance(stockId);
      for(CorporatePerformance cp : cpListI) {
        m.put(cp.getKeyString(), cp);
      }
      List<CorporatePerformance> cpListC = 
        parseConsolidatePerformance(stockId);
      for(CorporatePerformance cp : cpListC) {
        CorporatePerformance cp_old = m.get(cp.getKeyString());
        if(cp_old == null) {
          m.put(cp.getKeyString(), cp);
        } else {
          overwrite(cp_old, cp);
        }
      }
      CorporatePerformance.updateMap(m, conn);
    }
  }

  /**
   * コンストラクタで設定されたstockIdのリストから、
   * 個別銘柄の決算情報を取得.
   */
  public void append(Map<String, CorporatePerformance> m)
    throws IOException {
    for(Integer stockId : stockIdList) {
      List<CorporatePerformance> cpListI = parseIndependentPerformance(stockId);
      for(CorporatePerformance cp : cpListI) {
        m.put(cp.getKeyString(), cp);
      }
      List<CorporatePerformance> cpListC =  parseConsolidatePerformance(stockId);
      for(CorporatePerformance cp : cpListC) {
        CorporatePerformance cp_old = m.get(cp.getKeyString());
        if(cp_old == null) {
          m.put(cp.getKeyString(), cp);
        } else {
          overwrite(cp_old, cp);
        }
      }
    }
  }

  private void overwrite(
      CorporatePerformance cp_old, CorporatePerformance cp_new) {
    if(cp_new.announcementDate != null) {
      cp_old.announcementDate = cp_new.announcementDate;
    }
    if(cp_new.salesAmount != null) {
      cp_old.salesAmount = cp_new.salesAmount;
    }
    if(cp_new.operatingProfit != null) {
      cp_old.operatingProfit = cp_new.operatingProfit;
    }
    if(cp_new.ordinaryProfit != null) {
      cp_old.ordinaryProfit = cp_new.ordinaryProfit;
    }
    if(cp_new.netProfit != null) {
      cp_old.netProfit = cp_new.netProfit;
    }
    if(cp_new.totalAssets != null) {
      cp_old.totalAssets = cp_new.totalAssets;
    }
    if(cp_new.debtWithInterest != null) {
      cp_old.debtWithInterest = cp_new.debtWithInterest;
    }
    if(cp_new.capitalFund != null) {
      cp_old.capitalFund = cp_new.capitalFund;
    }
    if(cp_new.ownedCapital != null) {
      cp_old.ownedCapital = cp_new.ownedCapital;
    }
    if(cp_new.dividend != null) {
      cp_old.dividend = cp_new.dividend;
    }
  }

  /**
   * 指定した銘柄の単独決算情報を取得.
   * @param stockId 銘柄コード
   * @return 決算情報クラスのリスト
   */
  public List<CorporatePerformance> parseIndependentPerformance(int stockId) {
    List<CorporatePerformance> cpList = new ArrayList<CorporatePerformance>();
    String url = String.format(INDEPEND_URL_FORMAT, stockId);
    Document doc = Scraper.get(url);
    if(doc != null) {
      Elements records = doc.select("table.yjMt").select("tr");
      if(records.size() > 18) {
        for(int col = 1; col <= 3; col++) {
          MyDate ymd = TextParser.parseYearMonthJp(
              records.get(1).select("td").get(col).text());
          if(ymd != null) {
            CorporatePerformance cp = 
              new CorporatePerformance(stockId, ymd.year, ymd.month);
            cp.announcementDate = TextParser.parseYmdJapan(
                records.get(3).select("td").get(col).text());
            cp.salesAmount = TextParser.parseMillionMoney(
                records.get(5).select("td").get(col).text());
            cp.operatingProfit = TextParser.parseMillionMoney(
                records.get(6).select("td").get(col).text());
            cp.ordinaryProfit = TextParser.parseMillionMoney(
                records.get(7).select("td").get(col).text());
            cp.netProfit = TextParser.parseMillionMoney(
                records.get(8).select("td").get(col).text());
            cp.dividend = TextParser.parseWithDecimal(
                records.get(11).select("td").get(col).text());
            cp.totalAssets = TextParser.parseMillionMoney(
                records.get(15).select("td").get(col).text());
            cp.ownedCapital = TextParser.parseMillionMoney(
                records.get(16).select("td").get(col).text());
            cp.capitalFund = TextParser.parseMillionMoney(
                records.get(17).select("td").get(col).text());
            cp.debtWithInterest = TextParser.parseMillionMoney(
                records.get(18).select("td").get(col).text());
            cpList.add(cp);
          }
        }
      }
    }
    return cpList;
  }

  /**
   * 指定した銘柄の連結決算情報を取得.
   * @param stockId 銘柄コード
   * @return 決算情報クラスのリスト
   */
  public List<CorporatePerformance> parseConsolidatePerformance(int stockId) {
    List<CorporatePerformance> cpList = new ArrayList<CorporatePerformance>();
    String url = String.format(CONSOLIDATE_URL_FORMAT, stockId);
    Document doc = Scraper.get(url);
    if(doc != null) {
      Elements records = doc.select("table.yjMt").select("tr");
      if(records.size() > 15) {
        for(int col = 1; col <= 3; col++) {
          MyDate ymd = TextParser.parseYearMonthJp(
              records.get(1).select("td").get(col).text());
          if(ymd != null) {
            CorporatePerformance cp = 
              new CorporatePerformance(stockId, ymd.year, ymd.month);
            cp.announcementDate = TextParser.parseYmdJapan(
                records.get(3).select("td").get(col).text());
            cp.salesAmount = TextParser.parseMillionMoney(
                records.get(5).select("td").get(col).text());
            cp.operatingProfit = TextParser.parseMillionMoney(
                records.get(6).select("td").get(col).text());
            cp.ordinaryProfit = TextParser.parseMillionMoney(
                records.get(7).select("td").get(col).text());
            cp.netProfit = TextParser.parseMillionMoney(
                records.get(8).select("td").get(col).text());
            cp.totalAssets = TextParser.parseMillionMoney(
                records.get(12).select("td").get(col).text());
            cp.ownedCapital = TextParser.parseMillionMoney(
                records.get(13).select("td").get(col).text());
            cp.capitalFund = TextParser.parseMillionMoney(
                records.get(14).select("td").get(col).text());
            cp.debtWithInterest = TextParser.parseMillionMoney(
                records.get(15).select("td").get(col).text());
            cpList.add(cp);
          }
        }
      }
    }
    return cpList;
  }

}
