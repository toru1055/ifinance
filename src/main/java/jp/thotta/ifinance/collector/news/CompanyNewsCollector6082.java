package jp.thotta.ifinance.collector.news;

import jp.thotta.ifinance.collector.BaseCompanyNewsCollector;
import jp.thotta.ifinance.collector.CompanyNewsCollector;
import jp.thotta.ifinance.common.FailToScrapeException;
import jp.thotta.ifinance.common.ParseNewsPageException;
import jp.thotta.ifinance.model.CompanyNews;

import java.util.List;

/**
 * 個別企業のニュースコレクター.
 * 企業名：【6082】ライドオン・エクスプレス
 *
 * @author toru1055
 */
public class CompanyNewsCollector6082
        extends BaseCompanyNewsCollector
        implements CompanyNewsCollector {
    private static final int stockId = 6082;
    private static final String IR_URL = "http://ir.rideonexpress.co.jp/contentFeeds/content/news/ja/irnews";
    private static final String PR_URL = "";
    private static final String SHOP_URL = "http://www.rideonexpress.co.jp/news/shop/rss.xml";
    private static final String PUBLICITY_URL = "http://www.rideonexpress.co.jp/news/media/rss.xml";

    @Override
    public void parseIRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        parseXml(newsList, stockId, IR_URL,
                CompanyNews.NEWS_TYPE_INVESTOR_RELATIONS);
    }

    @Override
    public void parseShopList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        parseXml(newsList, stockId, SHOP_URL,
                CompanyNews.NEWS_TYPE_SHOP_OPEN);
    }

    @Override
    public void parsePublicityList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        parseXml(newsList, stockId, PUBLICITY_URL,
                CompanyNews.NEWS_TYPE_PUBLICITY);
    }

}
