package jp.thotta.ifinance.collector.yj_finance;

import jp.thotta.ifinance.collector.FinancialAmountCollector;
import jp.thotta.ifinance.model.CorporatePerformance;

/**
 * Y!Financeの当期利益取得用実装.
 *
 * @author toru1055
 */
public class NetProfitCollectorImpl extends FinancialAmountCollectorImpl {
  private static final int YJ_FINANCE_KD = 49;

  public NetProfitCollectorImpl() {
    super(YJ_FINANCE_KD);
  }

  public void setFinancialAmount(CorporatePerformance cp, long financialAmount) {
    cp.netProfit = financialAmount;
  }

  public long getFinancialAmount(CorporatePerformance cp) {
    return cp.netProfit;
  }
}
