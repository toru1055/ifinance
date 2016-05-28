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
 * 企業名：【6076】アメイズ
 *
 * @author toru1055
 */
public class CompanyNewsCollector6076
        extends BaseCompanyNewsCollector
        implements CompanyNewsCollector {
    private static final int stockId = 6076;
    private static final String IR_URL = "http://www.az-hotels.co.jp/company/ir.php";
    private static final String PR_URL = "";
    private static final String SHOP_URL = "http://www.az-hotels.co.jp/";


    @Override
    public void parseIRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        Document doc = Scraper.getXml(IR_URL);
        Elements elements = doc.select("div#IRfeeds_box > div.feed_content");
        for (Element elem : elements) {
            String aTxt = elem.select("p.ymd_date").first().text();
            MyDate aDate = MyDate.parseYmd(aTxt,
                    new SimpleDateFormat("yyyy年MM月dd日"));
            Element anchor = elem.select("p.feed_txt > a").first();
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
    public void parseShopList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        Document doc = Scraper.getHtml(SHOP_URL);
        Elements openedList = doc.select("div#newopen_box > div#newopen_inbox > div.open_box");
        Elements junbiList = doc.select("div#junbi_box > div#junbi_inbox > div.open_box");

        for (Element elem : openedList) {
            String aTxt = elem.select("h3").first().text();
            MyDate aDate = MyDate.parseYmd(aTxt,
                    new SimpleDateFormat("yyyy年MM月dd日"));
            Element anchor = elem.select("p > a").first();
            String url = anchor.attr("abs:href") + "#open";
            CompanyNews news = new CompanyNews(stockId, url, aDate);
            news.title = anchor.text() + " " + aTxt;
            news.createdDate = MyDate.getToday();
            news.type = CompanyNews.NEWS_TYPE_SHOP_OPEN;
            if (news.hasEnough() &&
                    news.announcementDate.compareTo(MyDate.getPast(90)) > 0) {
                newsList.add(news);
            }
        }

        for (Element elem : junbiList) {
            String aTxt = elem.select("h3").first().text();
            MyDate aDate = MyDate.parseYmd(aTxt,
                    new SimpleDateFormat("yyyy年MM月dd日"));
            Element anchor = elem.select("p > a").first();
            String url = anchor.attr("abs:href") + "#junbi";
            CompanyNews news = new CompanyNews(stockId, url, aDate);
            news.title = anchor.text() + " " + aTxt;
            news.createdDate = MyDate.getToday();
            news.type = CompanyNews.NEWS_TYPE_SHOP_OPEN;
            if (news.hasEnough() &&
                    news.announcementDate.compareTo(MyDate.getPast(90)) > 0) {
                newsList.add(news);
            }
        }

    }

}

