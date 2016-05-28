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
 * 企業名：【___STOCK_ID___】___COMPANY_NAME___
 *
 * @author toru1055
 */
public class CompanyNewsCollector___STOCK_ID___
        extends BaseCompanyNewsCollector
        implements CompanyNewsCollector {
    private static final int stockId = ___STOCK_ID___;
    private static final String IR_URL = "";
    private static final String PR_URL = "";
    private static final String SHOP_URL = "";
    private static final String PUBLICITY_URL = "";

    @Override
    public void parseIRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        String targetUrl = IR_URL;
        int type = CompanyNews.NEWS_TYPE_INVESTOR_RELATIONS;
        Document doc = Scraper.getHtml(targetUrl);
        Elements dtList = doc.select("___DT_LIST___");
        Elements ddList = doc.select("___DD_LIST___");
        for (int i = 0; i < dtList.size(); i++) {
            Element dt = dtList.get(i);
            Element dd = ddList.get(i);
            String aTxt = dt.select("___DATE___").first().text();
            MyDate aDate = MyDate.parseYmd(aTxt,
                    new SimpleDateFormat("yyyy.MM.dd"));
            Element anchor = dd.select("___ANCHOR___").first();
            String title = dd.select("___TITLE___").text();
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

    @Override
    public void parsePRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        String targetUrl = PR_URL;
        int type = CompanyNews.NEWS_TYPE_PRESS_RELEASE;
        Document doc = Scraper.getHtml(targetUrl);
        Elements dtList = doc.select("___DT_LIST___");
        Elements ddList = doc.select("___DD_LIST___");
        for (int i = 0; i < dtList.size(); i++) {
            Element dt = dtList.get(i);
            Element dd = ddList.get(i);
            String aTxt = dt.select("___DATE___").first().text();
            MyDate aDate = MyDate.parseYmd(aTxt,
                    new SimpleDateFormat("yyyy.MM.dd"));
            Element anchor = dd.select("___ANCHOR___").first();
            String title = dd.select("___TITLE___").text();
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

    @Override
    public void parseShopList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        String targetUrl = SHOP_URL;
        int type = CompanyNews.NEWS_TYPE_SHOP_OPEN;
        Document doc = Scraper.getHtml(targetUrl);
        Elements dtList = doc.select("___DT_LIST___");
        Elements ddList = doc.select("___DD_LIST___");
        for (int i = 0; i < dtList.size(); i++) {
            Element dt = dtList.get(i);
            Element dd = ddList.get(i);
            String aTxt = dt.select("___DATE___").first().text();
            MyDate aDate = MyDate.parseYmd(aTxt,
                    new SimpleDateFormat("yyyy.MM.dd"));
            Element anchor = dd.select("___ANCHOR___").first();
            String title = dd.select("___TITLE___").text();
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

    @Override
    public void parsePublicityList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        String targetUrl = PUBLICITY_URL;
        int type = CompanyNews.NEWS_TYPE_PUBLICITY;
        Document doc = Scraper.getHtml(targetUrl);
        Elements dtList = doc.select("___DT_LIST___");
        Elements ddList = doc.select("___DD_LIST___");
        for (int i = 0; i < dtList.size(); i++) {
            Element dt = dtList.get(i);
            Element dd = ddList.get(i);
            String aTxt = dt.select("___DATE___").first().text();
            MyDate aDate = MyDate.parseYmd(aTxt,
                    new SimpleDateFormat("yyyy.MM.dd"));
            Element anchor = dd.select("___ANCHOR___").first();
            String title = dd.select("___TITLE___").text();
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
