package jp.thotta.ifinance.collector.yj_finance;

import jp.thotta.ifinance.model.CorporatePerformance;

/**
 * Y!Financeの資本金取得用実装.
 *
 * @author toru1055
 */
public class CapitalFundCollectorImpl
        extends FinancialAmountCollectorImpl {
    private static final int YJ_FINANCE_KD = 53;

    public CapitalFundCollectorImpl() {
        super(YJ_FINANCE_KD);
    }

    public void setFinancialAmount(CorporatePerformance cp, long financialAmount) {
        cp.capitalFund = financialAmount;
    }

    public long getFinancialAmount(CorporatePerformance cp) {
        return cp.capitalFund;
    }
}
