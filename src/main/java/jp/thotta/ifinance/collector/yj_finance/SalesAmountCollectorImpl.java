package jp.thotta.ifinance.collector.yj_finance;

import jp.thotta.ifinance.collector.FinancialAmountCollector;
import jp.thotta.ifinance.model.CorporatePerformance;

/**
 * Y!Financeの売上金額取得用実装.
 *
 * @author toru1055
 */
public class SalesAmountCollectorImpl extends FinancialAmountCollectorImpl {
  private static final int YJ_FINANCE_KD = 46;

  public SalesAmountCollectorImpl() {
    super(YJ_FINANCE_KD);
  }

  public void setFinancialAmount(CorporatePerformance cp, long financialAmount) {
    cp.salesAmount = financialAmount;
  }

  public long getFinancialAmount(CorporatePerformance cp) {
    return cp.salesAmount;
  }
}

