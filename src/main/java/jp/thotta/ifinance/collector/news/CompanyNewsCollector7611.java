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
 * 企業名：【7611】ハイデイ日高
 *
 * @author toru1055
 */
public class CompanyNewsCollector7611
        extends BaseCompanyNewsCollector
        implements CompanyNewsCollector {
    private static final int stockId = 7611;
    private static final String IR_URL = "http://v3.eir-parts.net/EIR/Rss.aspx?code=7611";
    private static final String SHOP_URL = "http://hidakaya.hiday.co.jp/";

    @Override
    public void parseIRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        Document doc = Scraper.getXml(IR_URL);
        Elements elements = doc.select("item");
        for (Element elem : elements) {
            String aTxt = elem.select("pubDate").first().text();
            MyDate aDate = MyDate.parseYmd(aTxt,
                    new SimpleDateFormat("EEE, dd MMM yyyy", Locale.ENGLISH));
            String url = elem.select("link").text();
            CompanyNews news = new CompanyNews(stockId, url, aDate);
            news.title = elem.select("title").text();
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
        Elements dtList = doc.select("div#topWhatsNewBody > h3");
        Elements ddList = doc.select("div#topWhatsNewBody > div.topWhatsNewItem");
        for (int i = 0; i < dtList.size(); i++) {
            Element dt = dtList.get(i);
            Element dd = ddList.get(i);
            String aTxt = dt.text();
            MyDate aDate = MyDate.parseYmd(aTxt,
                    new SimpleDateFormat("yy.MM.dd"));
            Element anchor = dd.select("p > a").first();
            String url;
            String title;
            if (anchor == null) {
                url = SHOP_URL + "#" + aDate.toString();
                title = dd.text();
            } else {
                url = anchor.attr("abs:href");
                title = dd.text().replaceAll("^： ", "");
            }
            CompanyNews news = new CompanyNews(stockId, url, aDate);
            news.title = title;
            news.createdDate = MyDate.getToday();
            news.type = CompanyNews.NEWS_TYPE_SHOP_OPEN;
            if (news.hasEnough() &&
                    news.announcementDate.compareTo(MyDate.getPast(90)) > 0) {
                newsList.add(news);
            }
        }
    }

}
