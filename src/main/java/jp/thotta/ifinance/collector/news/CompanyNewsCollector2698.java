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
 * 企業名：【2698】キャンドゥ
 *
 * @author toru1055
 */
public class CompanyNewsCollector2698
        extends BaseCompanyNewsCollector
        implements CompanyNewsCollector {
    private static final int stockId = 2698;
    private static final String IR_URL = "http://www.cando-web.co.jp/corporate/ir/IRNews/";
    private static final String PR_URL = "http://www.cando-web.co.jp/info/";

    @Override
    public void parseIRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        Document doc = Scraper.getHtml(IR_URL);
        Elements elements = doc.select(".irbNewsBlockByYear > ul > li");
        for (Element elem : elements) {
            String aTxt = elem.select(".irbTopPRListDate").first().text();
            MyDate aDate = MyDate.parseYmd(aTxt);
            Element anchor = elem.select(".irbTopPRListTitle > a").first();
            String url = anchor.attr("abs:href");
            CompanyNews news = new CompanyNews(stockId, url, aDate);
            news.title = elem.select(".irbTopPRListTitle").first().text();
            news.createdDate = MyDate.getToday();
            news.type = CompanyNews.NEWS_TYPE_INVESTOR_RELATIONS;
            if (news.hasEnough() && aDate.compareTo(MyDate.getPast(90)) > 0) {
                newsList.add(news);
            }
        }
    }

    @Override
    public void parsePRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        Document doc = Scraper.getHtml(PR_URL);
        Elements elements = doc.select("div#MainContent div.Inner > dl.clearfix");
        Elements dateList = elements.select("dt");
        Elements anchorList = elements.select("dd > a");
        for (int i = 0; i < dateList.size(); i++) {
            Element anchor = anchorList.get(i);
            Element dt = dateList.get(i);
            String aTxt = dt.text();
            MyDate aDate = MyDate.parseYmd(aTxt);
            String url = anchor.attr("abs:href");
            CompanyNews news = new CompanyNews(stockId, url, aDate);
            news.title = anchor.text();
            news.createdDate = MyDate.getToday();
            news.type = CompanyNews.NEWS_TYPE_PRESS_RELEASE;
            if (news.hasEnough() && aDate.compareTo(MyDate.getPast(90)) > 0) {
                newsList.add(news);
            }
        }
    }

}
