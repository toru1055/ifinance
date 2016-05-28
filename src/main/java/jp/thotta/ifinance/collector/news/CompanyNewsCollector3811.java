package jp.thotta.ifinance.collector.news;

import jp.thotta.ifinance.collector.BaseCompanyNewsCollector;
import jp.thotta.ifinance.collector.CompanyNewsCollector;
import jp.thotta.ifinance.common.FailToScrapeException;
import jp.thotta.ifinance.common.ParseNewsPageException;
import jp.thotta.ifinance.model.CompanyNews;

import java.util.List;

/**
 * 個別企業のニュースコレクター.
 * 企業名：【3811】ビットアイル
 *
 * @author toru1055
 */
public class CompanyNewsCollector3811
        extends BaseCompanyNewsCollector
        implements CompanyNewsCollector {
    private static final int stockId = 3811;
    private static final String IR_URL = "http://xml.irpocket.com/3811/XML/release-all2-latest-12m.rdf";
    private static final String PR_URL = "http://www.bit-isle.jp/corp/news/atom.xml";
    private static final String SHOP_URL = "";
    private static final String PUBLICITY_URL = "";

    @Override
    public void parseIRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        parseXjStorageUrl(newsList, stockId, IR_URL,
                CompanyNews.NEWS_TYPE_INVESTOR_RELATIONS);
    }

    @Override
    public void parsePRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        parseXmlElement(newsList, stockId, PR_URL,
                CompanyNews.NEWS_TYPE_PRESS_RELEASE);
    }

}
