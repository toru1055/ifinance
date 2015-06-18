package jp.thotta.ifinance.collector;

import jp.thotta.ifinance.model.CorporatePerformance;
import java.util.Map;
import java.io.IOException;

/**
 * 売上高を取得するInterface.
 *
 * @author toru1055
 */
public interface SalesAmountCollector {
  /**
   * 企業業績リストに売上高を追加する.
   * 
   * @param performamceTable 企業業績クラスのMap
   * @return <code>true</code> on success
   */
  public void appendSalesAmounts(Map<String, CorporatePerformance> perfomanceTable) throws IOException;
}
