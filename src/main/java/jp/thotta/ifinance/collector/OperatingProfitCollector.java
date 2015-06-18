package jp.thotta.ifinance.collector;

import jp.thotta.ifinance.model.CorporatePerformance;
import java.util.Map;
import java.io.IOException;

/**
 * 営業利益を取得するInterface.
 *
 * @author toru1055
 */
public interface OperatingProfitCollector {
  /**
   * 企業業績リストに営業利益を追加する.
   * 
   * @param performamceTable 企業業績クラスのMap
   * @return <code>true</code> on success
   */
  public void appendOperatingProfit(Map<String, CorporatePerformance> perfomanceTable) throws IOException;
}
