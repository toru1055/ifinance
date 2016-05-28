package jp.thotta.ifinance.collector.news;

import jp.thotta.ifinance.collector.BaseCompanyNewsCollector;
import jp.thotta.ifinance.collector.CompanyNewsCollector;
import jp.thotta.ifinance.common.FailToScrapeException;
import jp.thotta.ifinance.common.ParseNewsPageException;
import jp.thotta.ifinance.model.CompanyNews;

import java.util.List;

/**
 * 個別企業のニュースコレクター.
 * 企業名：【7412】アトム
 *
 * @author toru1055
 */
public class CompanyNewsCollector7412
        extends BaseCompanyNewsCollector
        implements CompanyNewsCollector {
    private static final int stockId = 7412;
    private static final String IR_URL = "http://www.atom-corp.co.jp/corpo/ir/ir_rss.php";
    private static final String PR_URL = "";
    private static final String SHOP_URL = "http://www.atom-corp.co.jp/open/open_rss.php";
    private static final String PUBLICITY_URL = "";


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

}
