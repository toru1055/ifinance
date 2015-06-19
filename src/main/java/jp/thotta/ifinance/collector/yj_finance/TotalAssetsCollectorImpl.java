package jp.thotta.ifinance.collector.yj_finance;

import jp.thotta.ifinance.collector.FinancialAmountCollector;
import jp.thotta.ifinance.model.CorporatePerformance;

/**
 * Y!Financeの総資産取得用実装.
 *
 * @author toru1055
 */
public class TotalAssetsCollectorImpl 
  extends FinancialAmountCollectorTenColumnImpl {
  private static final int YJ_FINANCE_KD = 51;

  public TotalAssetsCollectorImpl() {
    super(YJ_FINANCE_KD);
  }

  public void setFinancialAmount(CorporatePerformance cp, long financialAmount) {
    cp.totalAssets = financialAmount;
  }

  public long getFinancialAmount(CorporatePerformance cp) {
    return cp.totalAssets;
  }
}
