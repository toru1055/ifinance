package jp.thotta.ifinance.batch;

import jp.thotta.ifinance.common.FailToScrapeException;
import jp.thotta.ifinance.common.ParseNewsPageException;
import jp.thotta.ifinance.common.Scraper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

/**
 * ヤフー経済ニュースを分類.
 */
public class TrainYahooNewsClassifier extends TrainCompanyNewsClassifier {
    static final String BASE_LIST_URL =
            "http://news.yahoo.co.jp/hl?c=biz&d=";
    static final int modelId = 5;
    static final String parserType = "ma";
    static final String labelMode = "multi";
    static final String host = "localhost";

    public TrainYahooNewsClassifier(int past) {
        super(past, BASE_LIST_URL);
    }

    public TrainYahooNewsClassifier() {
        this(1);
    }

    @Override
    public int getPageSize()
            throws FailToScrapeException, ParseNewsPageException {
        Document doc = Scraper.getHtml(topUrl);
        Elements pager = doc.select("div.ftPager > ul > li:not(.prev):not(.next)");
        return pager.size();
    }

    @Override
    public Map<String, String> getNewsUrlMap()
            throws FailToScrapeException, ParseNewsPageException {
        Map<String, String> newsUrlMap = new HashMap<String, String>();
        int pageSize = this.getPageSize();
        for (int p = 1; p <= pageSize; p++) {
            String pageUrl = topUrl + "&p=" + String.valueOf(p);
            Document doc = Scraper.getHtml(pageUrl);
            Elements newsAnchorList = doc.select(
                    "ul.listBd > li > p.ttl > a"
            );
            for (Element newsAnchor : newsAnchorList) {
                String newsUrl = newsAnchor.attr("abs:href");
                String newsTitle = newsAnchor.text();
                newsUrlMap.put(newsUrl, newsTitle);
            }
        }
        return newsUrlMap;
    }

    @Override
    public void setTrainData()
            throws FailToScrapeException, ParseNewsPageException {
        Map<String, String> newsUrlMap = this.getNewsUrlMap();
        for (String url : newsUrlMap.keySet()) {
            Document doc = Scraper.getHtml(url);
            Element p = doc.select("p.ynDetailText").first();
            Elements stockAnchors = p.select("a[href*=/stocks/detail/?code=]");
            String stockId = "";
            String newsDocument = "";
            if (stockAnchors != null && stockAnchors.size() == 1) {
                stockId = stockAnchors.text().replace("<", "").replace(">", "");
                //newsDocument = newsUrlMap.get(url);
                newsDocument = newsUrlMap.get(url) + " " + p.ownText();
                newsDocument = newsDocument.replaceAll("\\s", " ");
                newsDocument = newsDocument.substring(0,
                        (int) Math.min(500, newsDocument.length()));
                stockIdNews.put(stockId, newsDocument);
            }
        }
    }
}
