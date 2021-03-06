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
 * 企業名：【6045】レントラックス
 *
 * @author toru1055
 */
public class CompanyNewsCollector6045
        extends BaseCompanyNewsCollector
        implements CompanyNewsCollector {
    private static final int stockId = 6045;
    private static final String IR_URL = "https://www.rentracks.co.jp/ir/news/";
    private static final String PR_URL = "";
    private static final String SHOP_URL = "";
    private static final String PUBLICITY_URL = "";

    @Override
    public void parseIRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        Document doc = Scraper.getHtml(IR_URL);
        Elements elements = doc.select("div.menu > dl");
        for (Element elem : elements) {
            String aTxt = elem.select("dt").first().text();
            MyDate aDate = MyDate.parseYmd(aTxt,
                    new SimpleDateFormat("yyyy年MM月dd日"));
            Element anchor = elem.select("dd.txt > a").first();
            String title = elem.select("dd.txt").text();
            String url = IR_URL + "#" + aDate.toString();
            if (anchor != null) {
                url = anchor.attr("abs:href");
                title = anchor.text();
            }
            CompanyNews news = new CompanyNews(stockId, url, aDate);
            news.title = title;
            news.createdDate = MyDate.getToday();
            news.type = CompanyNews.NEWS_TYPE_INVESTOR_RELATIONS;
            if (news.hasEnough()
                    && news.announcementDate.compareTo(MyDate.getPast(90)) > 0) {
                newsList.add(news);
            }
        }
    }

}
