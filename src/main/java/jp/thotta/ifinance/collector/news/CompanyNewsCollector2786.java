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
 * 企業名：【2786】サッポロドラッグストアー
 *
 * @author toru1055
 */
public class CompanyNewsCollector2786
        extends BaseCompanyNewsCollector
        implements CompanyNewsCollector {
    private static final int stockId = 2786;
    private static final String IR_URL = "";
    private static final String PR_URL = "http://www.sapporo-drug.co.jp/";
    private static final String SHOP_URL = "http://www.sapporo-drug.co.jp/hansoku/info/";
    private static final String PUBLICITY_URL = "";

    @Override
    public void parsePRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        Document doc = Scraper.getHtml(PR_URL);
        Elements elements = doc.select(".news-box1 > tbody > tr");
        for (Element elem : elements) {
            String aTxt = elem.select("td.news-ymd").first().text();
            MyDate aDate = MyDate.parseYmd(aTxt,
                    new SimpleDateFormat("yyyy/MM/dd"));
            Element anchor = elem.select("td > h4.news-title").first();
            String title = elem.select("td > h4.news-title").text();
            String url = PR_URL + "#" + aDate.toString();
            if (anchor != null) {
                url = anchor.attr("abs:href");
                title = anchor.text();
            }
            CompanyNews news = new CompanyNews(stockId, url, aDate);
            news.title = title;
            news.createdDate = MyDate.getToday();
            news.type = CompanyNews.NEWS_TYPE_PRESS_RELEASE;
            if (news.hasEnough()
                    && news.announcementDate.compareTo(MyDate.getPast(30)) > 0) {
                newsList.add(news);
            }
        }
    }

    @Override
    public void parseShopList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        Document doc = Scraper.getHtml(SHOP_URL);
        Elements elements = doc.select(".box3 > tbody > tr");
        int shopNumber = elements.size();
        MyDate aDate = new MyDate(2100, 1, 1);
        String title = "サッポロドラッグストアーの店舗数が【" + shopNumber + "】になりました";
        String url = SHOP_URL + "#shopNum/" + shopNumber;
        CompanyNews news = new CompanyNews(stockId, url, aDate);
        news.title = title;
        news.createdDate = MyDate.getToday();
        news.type = CompanyNews.NEWS_TYPE_SHOP_OPEN;
        if (news.hasEnough()
                && news.announcementDate.compareTo(MyDate.getPast(30)) > 0) {
            newsList.add(news);
        }
    }

}
