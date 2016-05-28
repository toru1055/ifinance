package jp.thotta.ifinance.collector.yj_finance;

import jp.thotta.ifinance.common.MyDate;
import jp.thotta.ifinance.model.CorporatePerformance;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Y!Financeの総資産取得用実装.
 *
 * @author toru1055
 */
public class TotalAssetsCollectorImpl
        extends FinancialAmountCollectorTenColumnImpl {
    private static final int YJ_FINANCE_KD = 54;

    public TotalAssetsCollectorImpl() {
        super(YJ_FINANCE_KD);
    }

    @Override
    public void setFinancialAmount(
            CorporatePerformance cp, long financialAmount) {
        cp.totalAssets = financialAmount;
    }

    @Override
    public long getFinancialAmount(CorporatePerformance cp) {
        return cp.totalAssets;
    }

    @Override
    public CorporatePerformance parseTableRecord(Element tr) throws IOException {
        CorporatePerformance cp;
        Elements cols = tr.select("td");
        if (cols.size() == 10) {
            int stockId = TextParser.parseStockId(cols.get(1).text());
            MyDate settlingYM = TextParser.parseYearMonth(cols.get(8).text());
            long financialAmount = TextParser.parseFinancialAmount(cols.get(7).text());
            cp = new CorporatePerformance(
                    stockId,
                    settlingYM.year,
                    settlingYM.month);
            setFinancialAmount(cp, financialAmount);
        } else {
            throw new IOException(
                    "Table column number was changed: tr.size[" +
                            cols.size() + "]\n" + tr);
        }
        return cp;
    }
}
