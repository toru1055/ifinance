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
 * 企業名：【9842】アークランドサカモト
 *
 * @author toru1055
 */
public class CompanyNewsCollector9842
        extends BaseCompanyNewsCollector
        implements CompanyNewsCollector {
    private static final int stockId = 9842;
    private static final String IR_URL = "http://www.arcland.co.jp/ir_memory/memory.cgi";
    private static final String PR_URL = "http://www.arcland.co.jp/";
    private static final String PUBLICITY_URL = "";

    @Override
    public void parseIRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        Document doc = Scraper.getHtml(IR_URL);
        Elements elements = doc.select("div#container > ul > li");
        for (Element elem : elements) {
            String aTxt = elem.text().replaceAll("^.*（", "").replaceAll("Ｈ", "H");
            Locale locale = new Locale("ja", "JP", "JP");
            MyDate aDate = MyDate.parseYmd(aTxt,
                    new SimpleDateFormat("Gyyyy.MM.dd", locale));
            if (aDate == null) {
                continue;
            }
            Element anchor = elem.select("a").first();
            String title = elem.text();
            String url = IR_URL + "#" + aDate.toString();
            if (anchor != null) {
                url = anchor.attr("abs:href");
            }
            CompanyNews news = new CompanyNews(stockId, url, aDate);
            news.title = title;
            news.createdDate = MyDate.getToday();
            news.type = CompanyNews.NEWS_TYPE_INVESTOR_RELATIONS;
            if (news.hasEnough()
                    && news.announcementDate.compareTo(MyDate.getPast(7)) > 0) {
                newsList.add(news);
            }
        }
    }

    @Override
    public void parsePRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        Document doc = Scraper.getHtml(PR_URL);
        Elements elements = doc.select("dl#info > dd > ul#info > li");
        for (Element elem : elements) {
            String aTxt = elem.select("div.yymmdd").text();
            MyDate aDate = MyDate.parseYmd(aTxt,
                    new SimpleDateFormat("yyyy/MM/dd"));
            Element anchor = elem.select("a").first();
            String title = elem.ownText();
            if (aDate == null) {
                continue;
            }
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

}
