package jp.thotta.ifinance.collector.yj_finance;

import jp.thotta.ifinance.collector.CompanyProfileCollector;
import jp.thotta.ifinance.model.CompanyProfile;
import jp.thotta.ifinance.common.MyDate;
import jp.thotta.ifinance.common.Scraper;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.IOException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

/**
 * Y!Financeの会社概要ページから企業プロファイルを取得.
 */
public class BaseProfileCollectorImpl
  implements CompanyProfileCollector {
  private static final String PROFILE_URL_FORMAT
    = "http://profile.yahoo.co.jp/fundamental/%4d";
  private List<Integer> stockIdList;

  public BaseProfileCollectorImpl(List<Integer> stockIdList) {
    this.stockIdList = stockIdList;
  }

  /**
   * コンストラクタで設定されたstockIdのリストから、
   * 個別銘柄の会社概要を取得し、DBに登録.
   */
  public void appendDb(Connection conn) 
    throws SQLException, IOException {
    Statement st = conn.createStatement();
    for(Integer stockId : stockIdList) {
      CompanyProfile profile = parseCompanyProfile(stockId);
      if(profile == null) {
        continue;
      }
      if(profile.exists(st)) {
        profile.update(st);
      } else {
        profile.insert(st);
      }
    }
  }

  public void append(Map<String, CompanyProfile> m) {
    for(Integer stockId : stockIdList) {
      CompanyProfile profile = parseCompanyProfile(stockId);
      m.put(profile.getKeyString(), profile);
    }
  }

  public CompanyProfile parseCompanyProfile(int stockId) {
    CompanyProfile profile = new CompanyProfile(stockId);
    String url = String.format(PROFILE_URL_FORMAT, stockId);
    Document doc = Scraper.get(url);
    if(doc == null) { return null; }
    profile.companyName = TextParser.parseCompanyName(
      doc.select("div.selectFinTitle").select("h1 > strong.yjL").text());
    Elements records = doc.select("table").select("tr.yjMt");
    for(Element tr : records) {
      Elements cols = tr.select("td");
      String td0 = cols.get(0).text();
      if(td0.equals("特色")) {
        profile.companyFeature = cols.get(1).text();
      } else if(td0.equals("連結事業")) {
        profile.businessDescription = cols.get(1).text();
      } else if(td0.equals("業種分類")) {
        profile.businessCategory = cols.get(1).text();
      } else if(td0.equals("設立年月日")) {
        profile.foundationDate = TextParser.parseYmdJapan(cols.get(1).text());
      } else if(td0.equals("上場年月日")) {
        profile.listingDate = TextParser.parseYearMonthJapan(cols.get(1).text());
      } else if(td0.equals("単元株数")) {
        profile.shareUnitNumber = 
          TextParser.parseIntWithUnit(cols.get(1).text(), "株");
      } else if(td0.equals("従業員数（単独）")) {
        profile.independentEmployee =
          TextParser.parseIntWithUnit(cols.get(1).text(), "人");
        profile.consolidateEmployee = 
          TextParser.parseIntWithUnit(cols.get(3).text(), "人");
      } else if(td0.equals("平均年齢")) {
        profile.averageAge = TextParser.parseDoubleWithUnit(
            cols.get(1).text(), "歳");
        profile.averageAnnualIncome = TextParser.parseDoubleWithUnit(
            cols.get(3).text(), "千円");
        if(profile.averageAnnualIncome != null) {
          profile.averageAnnualIncome = 1000 * profile.averageAnnualIncome;
        }
      } else {
      }
    }
    return profile;
  }

}
