package jp.thotta.ifinance.batch;

import jp.thotta.ifinance.common.FailToScrapeException;
import jp.thotta.ifinance.common.MyDate;
import jp.thotta.ifinance.common.ParseNewsPageException;
import jp.thotta.ifinance.common.Scraper;
import jp.thotta.ifinance.model.Database;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.Connection;

/**
 * 化学工業日報の銘柄予測.
 */
public class PredictChemicalDailyNews extends BasePredictNewsCategory {

    static final String BASE_URL = "http://www.chemicaldaily.co.jp/headline/";

    public PredictChemicalDailyNews(Connection conn) {
        super(conn);
    }

    @Override
    public void setTodayNewsUrls()
            throws FailToScrapeException, ParseNewsPageException {
        String url = BASE_URL;
        Document doc = Scraper.getHtml(url);
        Elements list = doc.select("#headline_contents > p > a");
        MyDate today = MyDate.getToday();
        for (Element li : list) {
            BatchNews bn = new BatchNews();
            bn.url = li.attr("abs:href");
            bn.title = li.text();
            if (bn.url.contains(
                    today.toFormat("%4d/%02d/%02d"))) {
                batchNewsMap.put(bn.url, bn);
            }
        }
    }

    public static void main(String[] args) {
        try {
            Connection c = Database.getConnection();
            PredictChemicalDailyNews p = new PredictChemicalDailyNews(c);
            p.execPredict();
            p.insertDatabase();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
