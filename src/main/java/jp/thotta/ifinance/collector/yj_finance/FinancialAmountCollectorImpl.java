package jp.thotta.ifinance.collector.yj_finance;

import jp.thotta.ifinance.collector.FinancialAmountCollector;
import jp.thotta.ifinance.model.CorporatePerformance;

import java.util.Map;
import java.util.Calendar;
import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

/**
 * {@link FinancialAmountCollector}の
 * Y!Financeデータ取得用実装.
 *
 * @author toru1055
 */
public abstract class FinancialAmountCollectorImpl implements FinancialAmountCollector {
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
      Calendar settlingYM = TextParser.parseYearMonth(cols.get(7).text());
      int settlingYear = settlingYM.get(Calendar.YEAR);
      int settlingMonth = settlingYM.get(Calendar.MONTH) + 1;
      long financialAmount = TextParser.parseFinancialAmount(cols.get(6).text());
      cp = new CorporatePerformance(
            stockId, 
            settlingYear, 
            settlingMonth);
      setFinancialAmount(cp, financialAmount);
    } else {
      throw new IOException("Table column number was changed: " + tr);
    }
    return cp;
  }

  abstract public void setFinancialAmount(
      CorporatePerformance cp, long financialAmount);

  abstract public long getFinancialAmount(
      CorporatePerformance cp);
}
