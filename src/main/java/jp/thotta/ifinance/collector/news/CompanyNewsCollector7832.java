package jp.thotta.ifinance.collector.news;

import jp.thotta.ifinance.collector.BaseCompanyNewsCollector;
import jp.thotta.ifinance.collector.CompanyNewsCollector;
import jp.thotta.ifinance.common.FailToScrapeException;
import jp.thotta.ifinance.common.ParseNewsPageException;
import jp.thotta.ifinance.model.CompanyNews;

import java.util.List;

/**
 * 個別企業のニュースコレクター.
 * 企業名：【7832】バンダイナムコホールディングス
 *
 * @author toru1055
 */
public class CompanyNewsCollector7832
        extends BaseCompanyNewsCollector
        implements CompanyNewsCollector {
    private static final int stockId = 7832;
    private static final String IR_URL = "";
    private static final String PR_URL = "http://www.bandainamco.co.jp/releases/index.rdf";
    private static final String SHOP_URL = "";
    private static final String PUBLICITY_URL = "";

    @Override
    public void parsePRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        parseXjStorageUrl(newsList, stockId, PR_URL,
                CompanyNews.NEWS_TYPE_PRESS_RELEASE);
    }

}
