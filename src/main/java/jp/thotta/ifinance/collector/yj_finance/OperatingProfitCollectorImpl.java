package jp.thotta.ifinance.collector.yj_finance;

import jp.thotta.ifinance.model.CorporatePerformance;

/**
 * Y!Finance営業利益取得用実装.
 *
 * @author toru1055
 */
public class OperatingProfitCollectorImpl extends FinancialAmountCollectorImpl {
    private static final int YJ_FINANCE_KD = 47;

    public OperatingProfitCollectorImpl() {
        super(YJ_FINANCE_KD);
    }

    public void setFinancialAmount(CorporatePerformance cp, long financialAmount) {
        cp.operatingProfit = financialAmount;
    }

    public long getFinancialAmount(CorporatePerformance cp) {
        return cp.operatingProfit;
    }
}
