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
 * 企業名：【7647】音通
 *
 * @author toru1055
 */
public class CompanyNewsCollector7647
        extends BaseCompanyNewsCollector
        implements CompanyNewsCollector {
    private static final int stockId = 7647;
    private static final String PR_URL = "http://www.ontsu.co.jp/news_release.html";

    @Override
    public void parsePRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        Document doc = Scraper.getHtml(PR_URL);
        Elements elements = doc.select("table:eq(1) td:eq(2) table:eq(1) tr:eq(0) table > tbody > tr:not(:has(hr))");
        for (Element elem : elements) {
            Element tdDate = elem.select("td").first();
            Element tdTitle = elem.select("td").last();
            MyDate aDate = MyDate.parseYmd(tdDate.text(),
                    new SimpleDateFormat("yyyy.MM.dd"));
            Element anchor = tdTitle.select("a").first();
            String url = PR_URL;
            if (anchor != null) {
                url = anchor.attr("abs:href");
            }
            CompanyNews news = new CompanyNews(stockId, url, aDate);
            news.title = tdTitle.text();
            news.createdDate = MyDate.getToday();
            news.type = CompanyNews.NEWS_TYPE_PRESS_RELEASE;
            if (news.hasEnough()) {
                newsList.add(news);
            }
        }
    }

}
