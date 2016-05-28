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
 * 企業名：【3091】ブロンコビリー
 *
 * @author toru1055
 */
public class CompanyNewsCollector3091
        extends BaseCompanyNewsCollector
        implements CompanyNewsCollector {
    private static final int stockId = 3091;
    private static final String IR_URL = "http://custom.xj-serve.com/bronco/news_bk.html";
    private static final String PR_URL = "http://custom.xj-serve.com/bronco/data7.html";
    private static final String SHOP_URL = "http://www.bronco.co.jp/news/";

    @Override
    public void parseShopList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        Document doc = Scraper.getHtml(SHOP_URL);
        Elements elements = doc.select("div#inner_contents > div#list_content > dl");
        for (Element elem : elements) {
            MyDate aDate = MyDate.parseYmd(elem.select("dt").text(), new SimpleDateFormat("yyyy.MM.dd"));
            Element anchor = elem.select("dd > a").first();
            String url = anchor.attr("abs:href");
            CompanyNews news = new CompanyNews(stockId, url, aDate);
            news.title = anchor.text();
            news.createdDate = MyDate.getToday();
            news.type = CompanyNews.NEWS_TYPE_SHOP_OPEN;
            if (news.hasEnough() && aDate.compareTo(MyDate.getPast(90)) > 0) {
                newsList.add(news);
            }
        }
    }
}
