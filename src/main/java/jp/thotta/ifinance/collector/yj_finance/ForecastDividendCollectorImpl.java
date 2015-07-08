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
 * Y!Financeから配当金を取得.
 *
 * @author toru1055
 */
public class ForecastDividendCollectorImpl
  implements ForecastPerformanceCollector {
  private static final int YJ_FINANCE_KD = 8;
  private PageIterator iter;

  /**
   * コンストラクタ.
   */
  public ForecastDividendCollectorImpl() {
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
          existPF.dividend = pf.dividend;
          existPF.dividendYield = pf.dividendYield;
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
    if(cols.size() == 10) {
      int stockId = TextParser.parseStockId(cols.get(1).text());
      MyDate settlingYM = TextParser.parseYearMonth(cols.get(6).text());
      double dividend = TextParser.parseWithDecimal(cols.get(7).text());
      double dividendYield = TextParser.parsePercent(cols.get(8).text());
      pf = new PerformanceForecast(
            stockId, 
            settlingYM.year, 
            settlingYM.month);
      pf.dividend = dividend;
      pf.dividendYield = dividendYield;
    } else {
      throw new IOException("Table column number was changed: " + tr);
    }
    return pf;
  }
}
