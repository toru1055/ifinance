package jp.thotta.ifinance.collector.news;

import jp.thotta.ifinance.collector.BaseCompanyNewsCollector;
import jp.thotta.ifinance.collector.CompanyNewsCollector;
import jp.thotta.ifinance.common.FailToScrapeException;
import jp.thotta.ifinance.common.ParseNewsPageException;
import jp.thotta.ifinance.model.CompanyNews;

import java.util.List;

/**
 * 個別企業のニュースコレクター.
 * 企業名：【3133】海帆
 *
 * @author toru1055
 */
public class CompanyNewsCollector3133
        extends BaseCompanyNewsCollector
        implements CompanyNewsCollector {
    private static final int stockId = 3133;
    private static final String PR_URL = "http://v4.eir-parts.net/V4Public/EIR/3133/ja/announcement/announcement_7.xml";

    @Override
    public void parsePRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        parseXml(newsList, stockId, PR_URL,
                CompanyNews.NEWS_TYPE_PRESS_RELEASE);
    }

}
