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
 * 企業名：【9927】ワットマン
 *
 * @author toru1055
 */
public class CompanyNewsCollector9927
        extends BaseCompanyNewsCollector
        implements CompanyNewsCollector {
    private static final int stockId = 9927;
    //private static final String IR_URL = "http://ir.wattmann.co.jp/tagIR%E3%83%8B%E3%83%A5%E3%83%BC%E3%82%B9";
    private static final String IR_URL = "http://ir.wattmann.co.jp/";
    private static final String PR_URL = "http://ir.wattmann.co.jp/c24808.html";

    @Override
    public void parseIRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        Document doc = Scraper.getHtml(IR_URL);
        Elements elements = doc.select("div#contents > div.section");
        for (Element elem : elements) {
            String aTxt = elem.select("div.posted > p.date").text().replaceAll(
                    "日時：", "");
            MyDate aDate = MyDate.parseYmd(aTxt, new SimpleDateFormat("yyyy年MM月dd日"));
            Element anchor = elem.select("div.title_selector > h2.title > a").first();
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
        Elements elements = doc.select("div#contents > div.section");
        for (Element elem : elements) {
            String aTxt = elem.select("div.posted > p.date").text().replaceAll(
                    "日時：", "");
            MyDate aDate = MyDate.parseYmd(aTxt, new SimpleDateFormat("yyyy年MM月dd日"));
            Element anchor = elem.select("div.title_selector > h2.title > a").first();
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
