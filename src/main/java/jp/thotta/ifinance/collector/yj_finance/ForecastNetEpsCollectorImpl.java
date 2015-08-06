package jp.thotta.ifinance.collector.yj_finance;

import jp.thotta.ifinance.collector.ForecastPerformanceCollector;
import jp.thotta.ifinance.model.PerformanceForecast;
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
 * Y!Financeの1株あたり当期利益を取得.
 */
public class ForecastNetEpsCollectorImpl
  implements ForecastPerformanceCollector {
  private static final int YJ_FINANCE_KD = 50;
  private PageIterator iter;

  public ForecastNetEpsCollectorImpl() {
    iter = new PageIterator(YJ_FINANCE_KD);
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
    Map<String, PerformanceForecast> m =
      new HashMap<String, PerformanceForecast>();
    append(m);
    PerformanceForecast.updateMap(m, conn);
  }

  public void append(
      Map<String, PerformanceForecast> m) throws IOException {
    while(iter.hasNext()) {
      Document doc = iter.next();
      Elements records = doc
        .select("table.rankingTable")
        .select("tr.rankingTabledata.yjM");
      for(Element tr : records) {
        PerformanceForecast pf = parseTableRecord(tr);
        String k = pf.getKeyString();
        if(m.containsKey(k)) {
          PerformanceForecast existPF = m.get(k);
          existPF.netEps = pf.netEps;
          m.put(k, existPF);
        } else {
          m.put(k, pf);
        }
      }
    }
  }

  public PerformanceForecast parseTableRecord(Element tr)
    throws IOException {
    PerformanceForecast pf;
    Elements cols = tr.select("td");
    if(cols.size() == 9) {
      int stockId = TextParser.parseStockId(cols.get(1).text());
      MyDate settlingYM = TextParser.parseYearMonth(cols.get(7).text());
      long netEps = TextParser.parseFinancialAmount(cols.get(6).text());
      pf = new PerformanceForecast(
            stockId, 
            settlingYM.year, 
            settlingYM.month);
      pf.netEps = netEps;
    } else {
      throw new IOException("Table column number was changed: " + tr);
    }
    return pf;
  }
}
