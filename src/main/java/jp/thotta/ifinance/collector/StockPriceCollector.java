package jp.thotta.ifinance.collector;

import jp.thotta.ifinance.model.DailyStockPrice;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * 日次株価を取得するInterface.
 *
 * @author toru1055
 */
public interface StockPriceCollector {
    /**
     * 日次株価リストに株価を追加する.
     *
     * @param stockTable 日次株価クラスのMap
     * @return <code>true</code> on success
     * @throws IOException
     */
    public void append(
            Map<String, DailyStockPrice> stockTable)
            throws IOException;

    /**
     * DBの日次株価テーブルに株価を登録.
     *
     * @param conn DBのコネクション
     */
    public void appendDb(Connection conn)
            throws SQLException, IOException;
}
