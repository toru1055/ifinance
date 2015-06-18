package jp.thotta.ifinance.collector;

import jp.thotta.ifinance.model.CorporatePerformance;
import java.util.Map;
import java.io.IOException;

/**
 * 決算金額を取得するInterface.
 *
 * @author toru1055
 */
public interface FinancialAmountCollector {
  /**
   * 企業業績リストに決算金額を追加する.
   * どの決算金額に追加するかは実装による
   * 
   * @param performamceTable 企業業績クラスのMap
   * @return <code>true</code> on success
   */
  public void append(Map<String, CorporatePerformance> perfomanceTable) throws IOException;
}
