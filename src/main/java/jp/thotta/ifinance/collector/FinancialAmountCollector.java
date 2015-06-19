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
  public void append(
      Map<String, CorporatePerformance> perfomanceTable) throws IOException;

  /**
   * 読み込んだ決算金額をセット.
   * @param cp 企業業績クラス
   * @param financialAmount 読み込んだ決算金額
   */
  public void setFinancialAmount(CorporatePerformance cp, long financialAmount);

  /**
   * このクラスに対応する決算金額を返す.
   * @param cp 企業業績クラス
   * @return このクラスに対応する企業業績クラスの決算金額
   */
  public long getFinancialAmount(CorporatePerformance cp);
}
