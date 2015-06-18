package jp.thotta.ifinance.collector;

import jp.thotta.ifinance.model.CorporatePerformance;
import java.util.Map;
import java.io.IOException;

/**
 * 経常利益を取得するInterface.
 *
 * @author toru1055
 */
public interface OrdinaryProfitCollector 
  extends FinancialAmountCollector {
  /**
   * 企業業績リストに経常利益を追加する.
   * 
   * @param performamceTable 企業業績クラスのMap
   * @return <code>true</code> on success
   */
  public void appendOrdinaryProfit(Map<String, CorporatePerformance> perfomanceTable) throws IOException;
}
