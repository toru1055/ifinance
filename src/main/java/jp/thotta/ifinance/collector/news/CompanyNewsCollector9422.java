package jp.thotta.ifinance.collector.news;

import jp.thotta.ifinance.collector.BaseCompanyNewsCollector;
import jp.thotta.ifinance.collector.CompanyNewsCollector;
import jp.thotta.ifinance.common.FailToScrapeException;
import jp.thotta.ifinance.common.ParseNewsPageException;
import jp.thotta.ifinance.model.CompanyNews;

import java.util.List;

/**
 * 個別企業のニュースコレクター.
 * 企業名：【9422】コネクシオ
 *
 * @author toru1055
 */
public class CompanyNewsCollector9422
        extends BaseCompanyNewsCollector
        implements CompanyNewsCollector {
    private static final int stockId = 9422;
    private static final String IR_URL = "http://www.conexio.co.jp/irnews.xml";
    private static final String PR_URL = "";
    private static final String SHOP_URL = "";
    private static final String PUBLICITY_URL = "";

    @Override
    public void parseIRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        parseXjStorageUrl(newsList, stockId, IR_URL,
                CompanyNews.NEWS_TYPE_INVESTOR_RELATIONS);
    }

}
