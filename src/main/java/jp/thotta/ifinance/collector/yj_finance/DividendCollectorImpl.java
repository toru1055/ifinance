package jp.thotta.ifinance.collector.yj_finance;

import jp.thotta.ifinance.model.CorporatePerformance;
import jp.thotta.ifinance.common.MyDate;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

/**
 * Y!Financeの1株配当を取得.
 * @author
 */
public class DividendCollectorImpl 
  extends FinancialAmountCollectorImpl {
  private static final int YJ_FINANCE_KD = 8;

  public DividendCollectorImpl() {
    super(YJ_FINANCE_KD);
  }

  @Override
  public void setFinancialAmount(
      CorporatePerformance cp, long financialAmount) {
    cp.dividend = (double)financialAmount / 100;
  }

  @Override
  public long getFinancialAmount(CorporatePerformance cp) {
    return (long)(cp.dividend * 100);
  }
 
  @Override
  public CorporatePerformance parseTableRecord(Element tr) throws IOException {
    CorporatePerformance cp;
    Elements cols = tr.select("td");
    if(cols.size() == 10) {
      int stockId = TextParser.parseStockId(cols.get(1).text());
      MyDate settlingYM = TextParser.parseYearMonth(cols.get(6).text());
      long financialAmount = TextParser.parseDecimal100(cols.get(7).text());
      cp = new CorporatePerformance(
            stockId, 
            settlingYM.year - 1, 
            settlingYM.month);
      setFinancialAmount(cp, financialAmount);
    } else {
      throw new IOException(
          "Table column number was changed: tr.size[" +
          cols.size()+"]\n" + tr);
    }
    return cp;
  }
}
