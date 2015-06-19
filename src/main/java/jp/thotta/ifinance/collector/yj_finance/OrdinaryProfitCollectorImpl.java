package jp.thotta.ifinance.collector.yj_finance;

import jp.thotta.ifinance.collector.FinancialAmountCollector;
import jp.thotta.ifinance.model.CorporatePerformance;

/**
 * Y!Financeの経常利益取得用実装.
 *
 * @author toru1055
 */
public class OrdinaryProfitCollectorImpl extends FinancialAmountCollectorImpl {
  private static final int YJ_FINANCE_KD = 48;

  public OrdinaryProfitCollectorImpl() {
    super(YJ_FINANCE_KD);
  }

  public void setFinancialAmount(CorporatePerformance cp, long financialAmount) {
    cp.ordinaryProfit = financialAmount;
  }

  public long getFinancialAmount(CorporatePerformance cp) {
    return cp.ordinaryProfit;
  }
}
