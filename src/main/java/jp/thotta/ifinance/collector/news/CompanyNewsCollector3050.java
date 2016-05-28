package jp.thotta.ifinance.collector.news;

import jp.thotta.ifinance.collector.BaseCompanyNewsCollector;
import jp.thotta.ifinance.collector.CompanyNewsCollector;
import jp.thotta.ifinance.common.FailToScrapeException;
import jp.thotta.ifinance.common.ParseNewsPageException;
import jp.thotta.ifinance.model.CompanyNews;

import java.util.List;

/**
 * 個別企業のニュースコレクター.
 * 企業名：【3050】ＤＣＭホールディングス
 *
 * @author toru1055
 */
public class CompanyNewsCollector3050
        extends BaseCompanyNewsCollector
        implements CompanyNewsCollector {
    private static final int stockId = 3050;
    private static final String IR_URL = "";
    private static final String PR_URL = "http://v4.eir-parts.net/V4Public/EIR/3050/ja/announcement/announcement_2.xml";
    private static final String SHOP_URL = "http://v4.eir-parts.net/V4Public/EIR/3050/ja/announcement/announcement_4.xml";
    private static final String PUBLICITY_URL = "";

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
