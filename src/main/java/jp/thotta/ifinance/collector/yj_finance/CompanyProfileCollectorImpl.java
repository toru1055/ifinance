package jp.thotta.ifinance.collector.yj_finance;

import jp.thotta.ifinance.collector.CompanyProfileCollector;
import jp.thotta.ifinance.model.CompanyProfile;
import jp.thotta.ifinance.common.MyDate;

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

import java.sql.Connection;
import java.sql.SQLException;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

/**
 * 企業の固有情報をY!Financeランキングから取得する.
 * @author toru1055
 */
public abstract class CompanyProfileCollectorImpl
  implements CompanyProfileCollector {
  private PageIterator iter;

  /**
   * コンストラクタ.
   * @param kd どのランキングを使うかを指定
   */
  public CompanyProfileCollectorImpl(int kd) {
    iter = new PageIterator(kd);
  }

  /**
   * イテレーションを始めるページIDを指定.
   * @param page ページID
   */
  public void setStartPage(int page) {
    if(page < 1) { page = 1; }
    iter.setCurrentPage(page);
  }

  public void appendDb(Connection conn) 
    throws SQLException, IOException {
    Map<String, CompanyProfile> m =
      new HashMap<String, CompanyProfile>();
    append(m);
    CompanyProfile.updateMap(m, conn);
  }

  public void append(Map<String, CompanyProfile> m) throws IOException {
    while(iter.hasNext()) {
      Document doc = iter.next();
      Elements records = doc
        .select("table.rankingTable")
        .select("tr.rankingTabledata.yjM");
      for(Element tr : records) {
        CompanyProfile cp = parseTableRecord(tr);
        String k = cp.getKeyString();
        if(m.containsKey(k)) {
          CompanyProfile existCP = m.get(k);
          overwriteParsedProfile(existCP, getProfileAsText(cp));
          m.put(k, existCP);
        } else {
          m.put(k, cp);
        }
      }
    }
  }

  public CompanyProfile parseTableRecord(Element tr) throws IOException {
    CompanyProfile cp;
    Elements cols = tr.select("td");
    if(cols.size() == 10) {
      int stockId = TextParser.parseStockId(cols.get(1).text());
      String companyName = cols.get(3).text();
      String profile = cols.get(6).text();
      cp = new CompanyProfile(stockId);
      cp.companyName = companyName;
      overwriteParsedProfile(cp, profile);
    } else {
      throw new IOException("Table column number was changed: " + tr);
    }
    return cp;
  }

  /**
   * 読み込んだ情報を、実装クラスに対応するフィールドにセット.
   * @param cp 企業情報
   * @param profile 読み込んだ企業情報
   */
  abstract public void overwriteParsedProfile(CompanyProfile cp, String profile);

  /**
   * 実装クラスに対応するプロファイルフィールドを返す.
   * @param cp 企業情報
   * @return 実装クラスに対応する情報のテキスト
   */
  abstract public String getProfileAsText(CompanyProfile cp);
}
