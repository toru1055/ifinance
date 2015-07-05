package jp.thotta.ifinance.collector.yj_finance;

import jp.thotta.ifinance.collector.FinancialAmountCollector;
import jp.thotta.ifinance.model.CorporatePerformance;
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
 * {@link FinancialAmountCollector}の
 * Y!Financeデータ取得用実装.
 *
 * @author toru1055
 */
public abstract class FinancialAmountCollectorImpl 
  implements FinancialAmountCollector {
  private PageIterator iter;

  /**
   * コンストラクタ.
   */
  public FinancialAmountCollectorImpl(int kd) {
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
    Map<String, CorporatePerformance> m =
      new HashMap<String, CorporatePerformance>();
    append(m);
    CorporatePerformance.updateMap(m, conn);
  }

  public void append(
      Map<String, CorporatePerformance> m) throws IOException {
    while(iter.hasNext()) {
      Document doc = iter.next();
      Elements records = doc
        .select("table.rankingTable")
        .select("tr.rankingTabledata.yjM");
      for(Element tr : records) {
        CorporatePerformance cp = parseTableRecord(tr);
        String k = cp.getKeyString();
        if(m.containsKey(k)) {
          CorporatePerformance existCP = m.get(k);
          setFinancialAmount(existCP, getFinancialAmount(cp));
          m.put(k, existCP);
        } else {
          m.put(k, cp);
        }
      }
    }
  }

  public CorporatePerformance parseTableRecord(Element tr) throws IOException {
    CorporatePerformance cp;
    Elements cols = tr.select("td");
    if(cols.size() == 9) {
      int stockId = TextParser.parseStockId(cols.get(1).text());
      MyDate settlingYM = TextParser.parseYearMonth(cols.get(7).text());
      long financialAmount = TextParser.parseFinancialAmount(cols.get(6).text());
      cp = new CorporatePerformance(
            stockId, 
            settlingYM.year, 
            settlingYM.month);
      setFinancialAmount(cp, financialAmount);
    } else {
      throw new IOException("Table column number was changed: " + tr);
    }
    return cp;
  }

  /**
   * 読み込んだ金額を実装クラスに対応するフィールドにセット.
   * @param cp 企業業績クラス
   * @param financialAmount 読み込んだ決算金額
   */
  abstract public void setFinancialAmount(
      CorporatePerformance cp, long financialAmount);

  /**
   * 実装クラスに対応する金額フィールドを返す.
   * @param cp 企業業績クラス
   * @return 実装クラスに対応する企業業績クラスのフィールド
   */
  abstract public long getFinancialAmount(
      CorporatePerformance cp);
}
