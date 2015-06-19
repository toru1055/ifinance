package jp.thotta.ifinance.collector.yj_finance;

import jp.thotta.ifinance.collector.FinancialAmountCollector;
import jp.thotta.ifinance.model.CorporatePerformance;

/**
 * Y!Financeの有利子負債取得用実装.
 *
 * @author toru1055
 */
public class DebtWithInterestCollectorImpl 
  extends FinancialAmountCollectorTenColumnImpl {
  private static final int YJ_FINANCE_KD = 52;

  public DebtWithInterestCollectorImpl() {
    super(YJ_FINANCE_KD);
  }

  public void setFinancialAmount(CorporatePerformance cp, long financialAmount) {
    cp.debtWithInterest = financialAmount;
  }

  public long getFinancialAmount(CorporatePerformance cp) {
    return cp.debtWithInterest;
  }
}
