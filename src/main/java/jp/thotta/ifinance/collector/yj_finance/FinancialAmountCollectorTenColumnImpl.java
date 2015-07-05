package jp.thotta.ifinance.collector.yj_finance;

import jp.thotta.ifinance.collector.FinancialAmountCollector;
import jp.thotta.ifinance.model.CorporatePerformance;
import jp.thotta.ifinance.common.MyDate;

import java.util.Map;
import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

/**
 * Y!Financeの決算金額取得用実装の10列テーブル版.
 *
 * @author toru1055
 */
public abstract class FinancialAmountCollectorTenColumnImpl extends FinancialAmountCollectorImpl {

  public FinancialAmountCollectorTenColumnImpl(int kd) {
    super(kd);
  }

  @Override
  public CorporatePerformance parseTableRecord(Element tr) throws IOException {
    CorporatePerformance cp;
    Elements cols = tr.select("td");
    if(cols.size() == 10) {
      int stockId = TextParser.parseStockId(cols.get(1).text());
      MyDate settlingYM = TextParser.parseYearMonth(cols.get(8).text());
      long financialAmount = TextParser.parseFinancialAmount(cols.get(6).text());
      cp = new CorporatePerformance(
            stockId, 
            settlingYM.year, 
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
