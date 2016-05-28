package jp.thotta.ifinance.collector.news;

import jp.thotta.ifinance.collector.BaseCompanyNewsCollector;
import jp.thotta.ifinance.collector.CompanyNewsCollector;
import jp.thotta.ifinance.common.FailToScrapeException;
import jp.thotta.ifinance.common.MyDate;
import jp.thotta.ifinance.common.ParseNewsPageException;
import jp.thotta.ifinance.common.Scraper;
import jp.thotta.ifinance.model.CompanyNews;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 個別企業のニュースコレクター.
 * 企業名：【3224】ヒューマンウェブ
 *
 * @author toru1055
 */
public class CompanyNewsCollector3224
        extends BaseCompanyNewsCollector
        implements CompanyNewsCollector {
    private static final int stockId = 3224;
    private static final String IR_URL = "AS96031";
    private static final String PR_URL = "";
    private static final String SHOP_URL = "http://www.oysterbar.co.jp/news/index.php?news_kbn=2";
    private static final String PUBLICITY_URL = "";

    @Override
    public void parseIRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        parseXjStorageId(newsList, stockId, IR_URL,
                CompanyNews.NEWS_TYPE_INVESTOR_RELATIONS);
    }

    @Override
    public void parseShopList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        Document doc = Scraper.getHtml(SHOP_URL);
        Elements elements = doc.select("ul.newsList > li.cf");
        for (Element elem : elements) {
            String aTxt = elem.select("time").first().text();
            MyDate aDate = MyDate.parseYmd(aTxt,
                    new SimpleDateFormat("yyyy.MM.dd"));
            Element anchor = elem.select("div > h3 > a").first();
            String type = elem.select("div > h3 > a > span").text();
            if (!type.equals("OPEN")) {
                continue;
            }
            String title = anchor.ownText();
            String url = SHOP_URL + "#" + aDate.toString();
            if (anchor != null) {
                url = anchor.attr("abs:href");
            }
            CompanyNews news = new CompanyNews(stockId, url, aDate);
            news.title = title;
            news.createdDate = MyDate.getToday();
            news.type = CompanyNews.NEWS_TYPE_PRESS_RELEASE;
            if (news.hasEnough()
                    && news.announcementDate.compareTo(MyDate.getPast(90)) > 0) {
                newsList.add(news);
            }
        }
    }

}
