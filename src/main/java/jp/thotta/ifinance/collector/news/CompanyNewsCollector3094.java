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

import java.util.List;

/**
 * 個別企業のニュースコレクター.
 * 企業名：【3094】スーパーバリュー
 *
 * @author toru1055
 */
public class CompanyNewsCollector3094
        extends BaseCompanyNewsCollector
        implements CompanyNewsCollector {
    private static final int stockId = 3094;
    private static final String IR_URL = "http://www.supervalue.jp/ir/backnumber.html";
    private static final String PR_URL = "http://www.supervalue.jp/ir/ir08_01.html";

    @Override
    public void parseIRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        Document doc = Scraper.getHtml(IR_URL);
        Elements elements = doc.select("table table table table.f11l").first().select("tr:not(:has(img,h1))");
        for (Element elem : elements) {
            String aTxt = elem.select("td").first().text();
            MyDate aDate = MyDate.parseYmd(aTxt);
            Element anchor = elem.select("td > a").first();
            String url = anchor.attr("abs:href");
            CompanyNews news = new CompanyNews(stockId, url, aDate);
            news.title = anchor.text();
            news.createdDate = MyDate.getToday();
            news.type = CompanyNews.NEWS_TYPE_INVESTOR_RELATIONS;
            if (news.hasEnough() &&
                    news.announcementDate.compareTo(MyDate.getPast(90)) > 0) {
                newsList.add(news);
            }
        }
    }

    @Override
    public void parsePRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        Document doc = Scraper.getHtml(PR_URL);
        Elements elements = doc.select("table table table.col").first().select("tr:not(:has(td > img,h1))");
        for (Element elem : elements) {
            String aTxt = elem.select("td").first().text();
            MyDate aDate = MyDate.parseYmd(aTxt);
            Element anchor = elem.select("td > a").first();
            String url = anchor.attr("abs:href");
            CompanyNews news = new CompanyNews(stockId, url, aDate);
            news.title = anchor.text();
            news.createdDate = MyDate.getToday();
            news.type = CompanyNews.NEWS_TYPE_PRESS_RELEASE;
            if (news.hasEnough() &&
                    news.announcementDate.compareTo(MyDate.getPast(90)) > 0) {
                newsList.add(news);
            }
        }
    }

}
