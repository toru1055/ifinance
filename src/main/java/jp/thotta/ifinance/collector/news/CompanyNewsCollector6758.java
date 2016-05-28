package jp.thotta.ifinance.collector.news;

import jp.thotta.ifinance.collector.BaseCompanyNewsCollector;
import jp.thotta.ifinance.collector.CompanyNewsCollector;
import jp.thotta.ifinance.common.FailToScrapeException;
import jp.thotta.ifinance.common.ParseNewsPageException;
import jp.thotta.ifinance.model.CompanyNews;

import java.util.List;

/**
 * 個別企業のニュースコレクター.
 * 企業名：【6758】ソニー
 *
 * @author toru1055
 */
public class CompanyNewsCollector6758
        extends BaseCompanyNewsCollector
        implements CompanyNewsCollector {
    private static final int stockId = 6758;
    private static final String IR_URL = "http://www.sony.co.jp/SonyInfo/IR/rss/rss.xml";
    private static final String PR_URL = "http://www.sony.co.jp/SonyInfo/News/Press/data/pressrelease_group.xml";
    private static final String SHOP_URL = "";
    private static final String PUBLICITY_URL = "http://www.sony.co.jp/SonyInfo/News/Press/data/pressrelease_corp.xml";

    @Override
    public void parseIRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        parseXml(newsList, stockId, IR_URL,
                CompanyNews.NEWS_TYPE_INVESTOR_RELATIONS);
    }

    @Override
    public void parsePRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        parseXml(newsList, stockId, PR_URL,
                CompanyNews.NEWS_TYPE_PRESS_RELEASE);
    }

    @Override
    public void parsePublicityList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        parseXml(newsList, stockId, PUBLICITY_URL,
                CompanyNews.NEWS_TYPE_INFORMATION);
    }

}
