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
 * 企業名：【3075】銚子丸
 *
 * @author toru1055
 */
public class CompanyNewsCollector3075
        extends BaseCompanyNewsCollector
        implements CompanyNewsCollector {
    private static final int stockId = 3075;
    private static final String IR_URL = "http://v4.eir-parts.net/V4Public/EIR/3075/ja/announcement/announcement_7.xml";
    private static final String PR_URL = "";
    private static final String SHOP_URL = "http://www.choushimaru.co.jp/new_open/shinchaku/index.html";
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
        Elements elements = doc.select("div.iframeBK > p");
        //System.out.println(doc);
        for (Element elem : elements) {
            String aTxt = elem.select("b").first().text().replaceAll("^.*［", "");
            Locale locale = new Locale("ja", "JP", "JP");
            MyDate aDate = MyDate.parseYmd(aTxt,
                    new SimpleDateFormat("GGGGy年MM月dd日", locale));
            String title = elem.select("b").text().replaceAll("［.*］", "") + "OPEN";
            String url = SHOP_URL + "#" + aDate.toString();
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
