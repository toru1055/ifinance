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
 * 企業名：【6444】サンデンホールディングス
 *
 * @author toru1055
 */
public class CompanyNewsCollector6444
        extends BaseCompanyNewsCollector
        implements CompanyNewsCollector {
    private static final int stockId = 6444;
    private static final String IR_URL = "http://www.sanden.co.jp/ir/news/index.html";
    private static final String PR_URL = "";
    private static final String SHOP_URL = "";
    private static final String PUBLICITY_URL = "";

    @Override
    public void parseIRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        String targetUrl = IR_URL;
        int type = CompanyNews.NEWS_TYPE_INVESTOR_RELATIONS;
        Document doc = Scraper.getHtml(targetUrl);
        Elements dtList = doc.select("#mainContents > div.section:nth-child(2) > dl.topicsList02 > dt");
        Elements ddList = doc.select("#mainContents > div.section:nth-child(2) > dl.topicsList02 > dd");
        for (int i = 0; i < dtList.size(); i++) {
            Element dt = dtList.get(i);
            Element dd = ddList.get(i);
            String aTxt = dt.select("span.date").first().text();
            Locale locale = new Locale("ja", "JP", "JP");
            MyDate aDate = MyDate.parseYmd(aTxt,
                    new SimpleDateFormat("GGGGy年MM月dd日", locale));
            Element anchor = dd.select("p.title > a").first();
            String title = dd.select("p.title").text();
            String url = targetUrl + "#" + aDate.toString();
            if (anchor != null) {
                url = anchor.attr("abs:href");
                title = anchor.text();
            }
            CompanyNews news = new CompanyNews(stockId, url, aDate);
            news.title = anchor.text();
            news.createdDate = MyDate.getToday();
            news.type = type;
            if (news.hasEnough() &&
                    news.announcementDate.compareTo(MyDate.getPast(90)) > 0) {
                newsList.add(news);
            }
        }
    }

}
