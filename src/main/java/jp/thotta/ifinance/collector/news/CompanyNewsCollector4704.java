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
 * 企業名：【4704】トレンドマイクロ
 *
 * @author toru1055
 */
public class CompanyNewsCollector4704
        extends BaseCompanyNewsCollector
        implements CompanyNewsCollector {
    private static final int stockId = 4704;
    private static final String IR_URL = "http://www.trendmicro.co.jp/jp/about-us/investor-relations/financial-releases/";
    private static final String PR_URL = "http://www.trendmicro.co.jp/jp/about-us/press-releases/";
    private static final String SHOP_URL = "";
    private static final String PUBLICITY_URL = "http://www.trendmicro.co.jp/jp/about-us/topics/";

    @Override
    public void parseIRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        String targetUrl = IR_URL;
        int type = CompanyNews.NEWS_TYPE_INVESTOR_RELATIONS;
        Document doc = Scraper.getHtml(targetUrl);
        Elements dtList = doc.select("#container > section.content-slim > div > div.slim-box-content > div > section > div > section > section > section:nth-child(2) > dl > dt");
        Elements ddList = doc.select("#container > section.content-slim > div > div.slim-box-content > div > section > div > section > section > section:nth-child(2) > dl > dd");
        for (int i = 0; i < dtList.size(); i++) {
            Element dt = dtList.get(i);
            Element dd = ddList.get(i);
            String aTxt = dt.text();
            MyDate aDate = MyDate.parseYmd(aTxt,
                    new SimpleDateFormat("yyyy年MM月dd日"));
            Element anchor = dd.select("a").first();
            String title = dd.select("a").text();
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
                    news.announcementDate.compareTo(MyDate.getPast(180)) > 0) {
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
        Elements dtList = doc.select("#container > section.content-slim > div > div.slim-box-content > div > section > div > section > dl > dt");
        Elements ddList = doc.select("#container > section.content-slim > div > div.slim-box-content > div > section > div > section > dl > dd");
        for (int i = 0; i < dtList.size(); i++) {
            Element dt = dtList.get(i);
            Element dd = ddList.get(i);
            String aTxt = dt.text();
            MyDate aDate = MyDate.parseYmd(aTxt,
                    new SimpleDateFormat("yyyy年MM月dd日"));
            Element anchor = dd.select("a").first();
            String title = dd.select("a").text();
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
        Elements dtList = doc.select("#container > section.content-slim > div > div.slim-box-content > div > section > div > section > dl > dt");
        Elements ddList = doc.select("#container > section.content-slim > div > div.slim-box-content > div > section > div > section > dl > dd");
        for (int i = 0; i < dtList.size(); i++) {
            Element dt = dtList.get(i);
            Element dd = ddList.get(i);
            String aTxt = dt.text();
            MyDate aDate = MyDate.parseYmd(aTxt,
                    new SimpleDateFormat("yyyy年MM月dd日"));
            Element anchor = dd.select("a").first();
            String title = dd.select("a").text();
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
