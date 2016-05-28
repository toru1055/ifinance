package jp.thotta.ifinance.collector.yj_finance;

import jp.thotta.ifinance.collector.StockPriceCollector;
import jp.thotta.ifinance.common.MyDate;
import jp.thotta.ifinance.model.DailyStockPrice;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link StockPriceCollector}の
 * Y!Financeデータ取得用実装.
 *
 * @author toru1055
 */
public class StockPriceCollectorImpl
        implements StockPriceCollector {
    private static final int YJ_FINANCE_KD = 4;
    private PageIterator iter;

    /**
     * コンストラクタ.
     */
    public StockPriceCollectorImpl() {
        iter = new PageIterator(YJ_FINANCE_KD);
    }

    /**
     * イテレーションを始めるページIDを指定.
     *
     * @param page ページID
     */
    public void setStartPage(int page) {
        if (page < 1) {
            page = 1;
        }
        iter.setCurrentPage(page);
    }

    public void append(
            Map<String, DailyStockPrice> m) throws IOException {
        while (iter.hasNext()) {
            Document doc = iter.next();
            Elements records = doc
                    .select("table.rankingTable")
                    .select("tr.rankingTabledata.yjM");
            for (Element tr : records) {
                DailyStockPrice sp = parseTableRecord(tr);
                String k = sp.getKeyString();
                m.put(k, sp);
            }
        }
    }

    public void appendDb(Connection conn)
            throws SQLException, IOException {
        Map<String, DailyStockPrice> m =
                new HashMap<String, DailyStockPrice>();
        append(m);
        DailyStockPrice.updateMap(m, conn);
    }

    public DailyStockPrice parseTableRecord(Element tr)
            throws IOException {
        DailyStockPrice sp;
        Elements cols = tr.select("td");
        if (cols.size() == 10) {
            int stockId = TextParser.parseStockId(cols.get(1).text());
            MyDate date = MyDate.getToday();
            sp = new DailyStockPrice(stockId, date);
            sp.stockNumber = TextParser.parseNumberWithComma(cols.get(6).text());
            sp.marketCap = TextParser.parseNumberWithComma(cols.get(7).text());
        } else {
            throw new IOException("Table column number was changed: " + tr);
        }
        return sp;
    }
}
