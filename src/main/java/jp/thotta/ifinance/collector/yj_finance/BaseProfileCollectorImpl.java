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
    profile.companyFeature = records.get(0).select("td").get(1).text();
    profile.businessDescription = records.get(1).select("td").get(1).text();
    profile.businessCategory = records.get(4).select("td").get(1).text();
    profile.foundationDate = TextParser.parseYmdJapan(
        records.get(7).select("td").get(1).text());
    profile.listingDate = TextParser.parseYearMonthJapan(
        records.get(9).select("td").get(1).text());
    profile.shareUnitNumber = TextParser.parseIntWithUnit(
        records.get(11).select("td").get(1).text(), "株");
    profile.independentEmployee = TextParser.parseIntWithUnit(
        records.get(12).select("td").get(1).text(), "人");
    profile.consolidateEmployee = TextParser.parseIntWithUnit(
        records.get(12).select("td").get(3).text(), "人");
    profile.averageAge = TextParser.parseDoubleWithUnit(
        records.get(13).select("td").get(1).text(), "歳");
    profile.averageAnnualIncome = 1000 * TextParser.parseDoubleWithUnit(
        records.get(13).select("td").get(3).text(), "千円");
    return profile;
  }

}
