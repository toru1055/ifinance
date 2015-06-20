package jp.thotta.ifinance.collector;

import jp.thotta.ifinance.model.DailyStockPrice;
import java.util.Map;
import java.io.IOException;

/**
 * 日次株価を取得するInterface.
 *
 * @author toru1055
 */
public interface StockPriceCollector {
  /**
   * 企業業績リストに株価を追加する.
   * 
   * @param stockTable 日次株価クラスのMap
   * @throws IOException
   * @return <code>true</code> on success
   */
  public void append(
      Map<String, DailyStockPrice> stockTable) 
    throws IOException;
}
