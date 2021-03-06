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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 個別企業のニュースコレクター.
 * 企業名：【2705】大戸屋ホールディングス
 *
 * @author toru1055
 */
public class CompanyNewsCollector2705
        extends BaseCompanyNewsCollector
        implements CompanyNewsCollector {
    private static final int stockId = 2705;
    private static final String IR_URL = "http://www.ootoya.jp/ir/";
    private static final String SHOP_URL = "http://www.ootoya.com/news.asp";

    @Override
    public void parseIRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        Document doc = Scraper.getHtml(IR_URL);
        Elements dlList = doc.select("div.onebox > div.newswrap > div.newsrecord > dl");
        for (Element dl : dlList) {
            String aTxt = dl.select("dt").text();
            MyDate aDate = MyDate.parseYmd(aTxt, new SimpleDateFormat("yyyy年MM月dd日"));
            Element anchor = dl.select("dd > a").first();
            String url = anchor.attr("abs:href");
            CompanyNews news = new CompanyNews(stockId, url, aDate);
            news.title = anchor.text();
            news.createdDate = MyDate.getToday();
            news.type = CompanyNews.NEWS_TYPE_INVESTOR_RELATIONS;
            if (news.hasEnough()) {
                newsList.add(news);
            }
        }
    }

    @Override
    public void parseShopList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        Document doc = Scraper.getHtml(SHOP_URL);
        Elements anchors = doc.select("table span.news a.blk_link");
        for (Element anchor : anchors) {
            String url = anchor.attr("abs:href");
            String anchorTxt = anchor.text();
            String regex = "^(\\d{4}/\\d{2}/\\d{2})(.+)$";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(anchorTxt);
            if (m.find()) {
                MyDate aDate = MyDate.parseYmd(m.group(1));
                CompanyNews news = new CompanyNews(stockId, url, aDate);
                news.title = m.group(2);
                news.createdDate = MyDate.getToday();
                news.type = CompanyNews.NEWS_TYPE_SHOP_OPEN;
                if (news.hasEnough()) {
                    newsList.add(news);
                }
            }
        }
    }

}
