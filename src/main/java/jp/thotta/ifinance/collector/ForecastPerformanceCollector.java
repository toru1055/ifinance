package jp.thotta.ifinance.collector;

import jp.thotta.ifinance.model.PerformanceForecast;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * 会社予想の業績を取得するInterface.
 *
 * @author
 */
public interface ForecastPerformanceCollector {
    /**
     * 会社予想の業績リストにMapに追加.
     *
     * @param forecasts
     */
    public void append(
            Map<String, PerformanceForecast> forecasts) throws IOException;

    /**
     * DBの会社予想の業績テーブルに結果を追加.
     */
    public void appendDb(Connection conn) throws SQLException, IOException;
}
