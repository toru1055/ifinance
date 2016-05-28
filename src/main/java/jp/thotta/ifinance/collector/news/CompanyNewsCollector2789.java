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
 * 企業名：【2789】カルラ
 *
 * @author toru1055
 */
public class CompanyNewsCollector2789
        extends BaseCompanyNewsCollector
        implements CompanyNewsCollector {
    private static final int stockId = 2789;
    private static final String IR_URL = "";
    private static final String PR_URL = "http://re-marumatu.co.jp/tousikajyouhou/What's%20New.html";
    private static final String SHOP_URL = "";
    private static final String PUBLICITY_URL = "";

    @Override
    public void parsePRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        Document doc = Scraper.getHtml(PR_URL);
        Elements elements = doc.select("#whatsnew > li");
        for (Element elem : elements) {
            String aTxt = elem.select("span").first().text();
            MyDate aDate = MyDate.parseYmd(aTxt,
                    new SimpleDateFormat("yyyy.MM.dd"));
            Element anchor = elem.select("a").first();
            String title = elem.text();
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
                    && news.announcementDate.compareTo(MyDate.getPast(60)) > 0) {
                newsList.add(news);
            }
        }
    }

}
