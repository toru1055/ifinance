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
import java.util.Locale;

/**
 * 個別企業のニュースコレクター.
 * 企業名：【7610】テイツー
 *
 * @author toru1055
 */
public class CompanyNewsCollector7610
        extends BaseCompanyNewsCollector
        implements CompanyNewsCollector {
    private static final int stockId = 7610;
    private static final String IR_URL = "http://www.tay2.co.jp/rss.xml";
    private static final String PR_URL = "http://www.tay2.co.jp/ir/index.html";
    private static final String SHOP_URL = "http://www.furu1.net/shop/";


    @Override
    public void parsePRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        Document doc = Scraper.getXml(IR_URL);
        Elements elements = doc.select("item");
        for (Element elem : elements) {
            String aTxt = elem.select("pubDate").first().text();
            MyDate aDate = MyDate.parseYmd(aTxt,
                    new SimpleDateFormat("EEE, dd MMM yyyy", Locale.ENGLISH));
            Element anchor = elem.select("link").first();
            String url = anchor.text();
            CompanyNews news = new CompanyNews(stockId, url, aDate);
            news.title = elem.select("description").text();
            news.createdDate = MyDate.getToday();
            news.type = CompanyNews.NEWS_TYPE_PRESS_RELEASE;
            if (news.hasEnough() &&
                    news.announcementDate.compareTo(MyDate.getPast(90)) > 0) {
                newsList.add(news);
            }
        }
    }

    @Override
    public void parseShopList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        Document doc = Scraper.getHtml(SHOP_URL);
        Elements elements = doc.select("div.furuShopCateTable > table.pbWhatsnewTable > tbody > tr");
        for (Element elem : elements) {
            String aTxt = elem.select("td.furuTopCateDay").first().text();
            MyDate aDate = MyDate.parseYmd(aTxt,
                    new SimpleDateFormat("yyyy/MM/dd"));
            Element anchor = elem.select("td.furuShopCateDay > a").first();
            String url = anchor.attr("abs:href");
            CompanyNews news = new CompanyNews(stockId, url, aDate);
            news.title = anchor.text();
            news.createdDate = MyDate.getToday();
            news.type = CompanyNews.NEWS_TYPE_SHOP_OPEN;
            if (news.hasEnough() &&
                    news.announcementDate.compareTo(MyDate.getPast(90)) > 0) {
                newsList.add(news);
            }
        }
    }

}
