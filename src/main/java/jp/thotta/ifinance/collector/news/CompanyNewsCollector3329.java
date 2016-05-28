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
 * 企業名：【3329】東和フードサービス
 *
 * @author toru1055
 */
public class CompanyNewsCollector3329
        extends BaseCompanyNewsCollector
        implements CompanyNewsCollector {
    private static final int stockId = 3329;
    private static final String IR_URL = "http://www.towafood-net.co.jp/investor/whatsnew/tabid/333/Default.aspx";
    private static final String SHOP_URL = "http://www.towafood-net.co.jp/tabid/87/Default.aspx";

    @Override
    public void parseIRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        Document doc = Scraper.getHtml(IR_URL);
        Elements dtList = doc.select("div.DNN_ANN_DesignTable > span.SubDate");
        Elements ddList = doc.select("div.DNN_ANN_DesignTable > span.SubHead");
        for (int i = 0; i < dtList.size(); i++) {
            Element dt = dtList.get(i);
            Element dd = ddList.get(i);
            MyDate aDate = MyDate.parseYmd(dt.text());
            Element anchor = dd.select("a").first();
            String url = anchor.attr("abs:href");
            CompanyNews news = new CompanyNews(stockId, url, aDate);
            news.title = dd.text();
            news.createdDate = MyDate.getToday();
            news.type = CompanyNews.NEWS_TYPE_INVESTOR_RELATIONS;
            if (news.hasEnough() && aDate.compareTo(MyDate.getPast(90)) > 0) {
                newsList.add(news);
            }
        }
    }

    @Override
    public void parseShopList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        Document doc = Scraper.getHtml(SHOP_URL);
        Elements elements = doc.select("div.newsList > ul > li");
        for (Element elem : elements) {
            String aTxt = elem.select("p.newsDate").text();
            MyDate aDate = MyDate.parseYmd(aTxt,
                    new SimpleDateFormat("yyyy/MM/dd"));
            Element anchor = elem.select("div.newsTitle a").first();
            String title = elem.select("div.newsTitle").text();
            String url = SHOP_URL + "#" + aDate.toString();
            if (anchor != null) {
                url = anchor.attr("abs:href");
            }
            CompanyNews news = new CompanyNews(stockId, url, aDate);
            news.title = title;
            news.createdDate = MyDate.getToday();
            news.type = CompanyNews.NEWS_TYPE_SHOP_OPEN;
            if (news.hasEnough()
                    && news.announcementDate.compareTo(MyDate.getPast(90)) > 0) {
                newsList.add(news);
            }
        }

    }

}
