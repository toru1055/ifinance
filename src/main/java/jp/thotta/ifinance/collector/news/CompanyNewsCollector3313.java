package jp.thotta.ifinance.collector.news;

import jp.thotta.ifinance.collector.BaseCompanyNewsCollector;
import jp.thotta.ifinance.collector.CompanyNewsCollector;
import jp.thotta.ifinance.common.FailToScrapeException;
import jp.thotta.ifinance.common.ParseNewsPageException;
import jp.thotta.ifinance.model.CompanyNews;

import java.util.List;

/**
 * 個別企業のニュースコレクター.
 * 企業名：【3313】ブックオフコーポレーション
 *
 * @author toru1055
 */
public class CompanyNewsCollector3313
        extends BaseCompanyNewsCollector
        implements CompanyNewsCollector {
    private static final int stockId = 3313;
    private static final String IR_URL = "http://v4.eir-parts.net/V4Public/EIR/3313/ja/announcement/announcement_3.xml";
    private static final String PR_URL = "http://www.bookoff.co.jp/info/news/news.xml";
    private static final String SHOP_URL = "http://www.bookoff.co.jp/shop/news/shopopen.xml";

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
    public void parseShopList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        parseXml(newsList, stockId, SHOP_URL,
                CompanyNews.NEWS_TYPE_SHOP_OPEN);
    }

}
