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
 * 企業名：【9835】ジュンテンドー
 *
 * @author toru1055
 */
public class CompanyNewsCollector9835
        extends BaseCompanyNewsCollector
        implements CompanyNewsCollector {
    private static final int stockId = 9835;
    private static final String IR_URL = "http://v4.eir-parts.net/V4Public/EIR/9835/ja/announcement/announcement_6.xml";
    private static final String PR_URL = "";
    private static final String SHOP_URL = "http://www.juntendo.co.jp/news/";
    private static final String PUBLICITY_URL = "";

    @Override
    public void parseIRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        parseXml(newsList, stockId, IR_URL,
                CompanyNews.NEWS_TYPE_INVESTOR_RELATIONS);
    }

    @Override
    public void parseShopList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        Document doc = Scraper.getHtml(SHOP_URL);
        Elements elements = doc.select("div#NewsListIntro ol > li");
        for (Element elem : elements) {
            String aTxt = elem.select("strong").first().text();
            MyDate aDate = MyDate.parseYmd(aTxt,
                    new SimpleDateFormat("yyyy.MM.dd"));
            Element anchor = elem.select("p > em > a").first();
            String title = elem.select("p > em").text();
            String url = SHOP_URL + "#" + aDate.toString();
            if (anchor != null) {
                url = anchor.attr("abs:href");
                title = anchor.text();
            }
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

}
