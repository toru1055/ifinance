package jp.thotta.ifinance.collector;

import com.google.gson.Gson;
import jp.thotta.ifinance.collector.json.ItemV4Eir;
import jp.thotta.ifinance.collector.json.V4Eir;
import jp.thotta.ifinance.collector.news.*;
import jp.thotta.ifinance.common.FailToScrapeException;
import jp.thotta.ifinance.common.MyDate;
import jp.thotta.ifinance.common.ParseNewsPageException;
import jp.thotta.ifinance.common.Scraper;
import jp.thotta.ifinance.model.CompanyNews;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

public abstract class BaseCompanyNewsCollector
        implements CompanyNewsCollector {

    public void appendDb(Connection conn)
            throws SQLException, FailToScrapeException, ParseNewsPageException {
        Statement st = conn.createStatement();
        List<CompanyNews> newsList = new ArrayList<CompanyNews>();
        append(newsList);
        for (CompanyNews news : newsList) {
            if (!news.exists(st)) {
                news.insert(st);
            }
        }
    }

    public void append(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);
        int newsOriginalSize = newsList.size();
        parsePRList(newsList);
        parseIRList(newsList);
        parseAppList(newsList);
        parseShopList(newsList);
        parsePublicityList(newsList);
        parseInfomation(newsList);
        if (newsList.size() == newsOriginalSize) {
            throw new ParseNewsPageException("No news: " + getClass().getSimpleName());
        }
    }

    public void parsePRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
    }

    public void parseIRList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
    }

    public void parseAppList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
    }

    public void parseShopList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
    }

    public void parsePublicityList(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
    }

    public void parseInfomation(List<CompanyNews> newsList)
            throws FailToScrapeException, ParseNewsPageException {
    }

    public void parseV4EirJson(List<CompanyNews> newsList,
                               int stockId,
                               String parseUrl,
                               int newsType)
            throws FailToScrapeException, ParseNewsPageException {
        String rawText = Scraper.getRaw(parseUrl);
        String[] rawLines = rawText.split("\n");
        String jsonText = "";
        for (int i = 1; i < rawLines.length - 1; i++) {
            jsonText += rawLines[i] + "\n";
        }
        Gson gson = new Gson();
        V4Eir eir = gson.fromJson(jsonText, V4Eir.class);
        for (ItemV4Eir elem : eir.item) {
            MyDate aDate = MyDate.parseYmd(elem.format_date);
            if (aDate == null) {
                aDate = MyDate.parseYmd(elem.format_date,
                        new SimpleDateFormat("yyyy.MM.dd"));
            }
            if (aDate == null) {
                aDate = MyDate.parseYmd(elem.format_date,
                        new SimpleDateFormat("yyyy年MM月dd日"));
            }
            CompanyNews news = new CompanyNews(stockId, elem.link, aDate);
            news.title = elem.title;
            news.createdDate = MyDate.getToday();
            news.type = newsType;
            if (news.hasEnough() &&
                    news.announcementDate.compareTo(MyDate.getPast(90)) > 0) {
                newsList.add(news);
            }
        }
    }

    public void parseXjStorageId(List<CompanyNews> newsList,
                                 int stockId,
                                 String companyId,
                                 int newsType)
            throws FailToScrapeException, ParseNewsPageException {
        String parseUrl = "http://www.xj-storage.jp/public-list/GetList.aspx?len=5&output=rss&company=" + companyId;
        parseXjStorageUrl(newsList, stockId, parseUrl, newsType);
    }

    public void parseXjStorageUrl(List<CompanyNews> newsList,
                                  int stockId,
                                  String parseUrl,
                                  int newsType)
            throws FailToScrapeException, ParseNewsPageException {
        Document doc = Scraper.getXml(parseUrl);
        Elements elements = doc.select("item");
        for (Element elem : elements) {
            String aTxt = elem.select("dc|date").first().text();
            MyDate aDate = MyDate.parseYmd(aTxt,
                    new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH));
            Element anchor = elem.select("link").first();
            String url = anchor.text();
            CompanyNews news = new CompanyNews(stockId, url, aDate);
            news.title = elem.select("title").text();
            news.createdDate = MyDate.getToday();
            news.type = newsType;
            if (news.hasEnough() &&
                    news.announcementDate.compareTo(MyDate.getPast(90)) > 0) {
                newsList.add(news);
            }
        }
    }

    public void parseXml(List<CompanyNews> newsList,
                         int stockId,
                         String parseUrl,
                         int newsType)
            throws FailToScrapeException, ParseNewsPageException {
        Document doc = Scraper.getXml(parseUrl);
        Elements elements = doc.select("item");
        for (Element elem : elements) {
            String aTxt = elem.select("pubDate").first().text();
            MyDate aDate = MyDate.parseYmd(aTxt,
                    new SimpleDateFormat("EEE, dd MMM yyyy", Locale.ENGLISH));
            if (aDate == null) {
                aDate = MyDate.parseYmd(aTxt,
                        new SimpleDateFormat("EEE,dd MMM yyyy", Locale.ENGLISH));
            }
            Element anchor = elem.select("link").first();
            String url;
            if (anchor != null && !anchor.text().equals("")) {
                url = anchor.text();
            } else {
                url = parseUrl + "#" + aDate.toString();
            }
            CompanyNews news = new CompanyNews(stockId, url, aDate);
            news.title = elem.select("title").text();
            news.createdDate = MyDate.getToday();
            news.type = newsType;
            if (news.hasEnough() &&
                    news.announcementDate.compareTo(MyDate.getPast(100)) > 0) {
                newsList.add(news);
            }
        }
    }

    public void parseXmlElement(List<CompanyNews> newsList,
                                int stockId,
                                String parseUrl,
                                int newsType)
            throws FailToScrapeException, ParseNewsPageException {
        Document doc = Scraper.getXml(parseUrl);
        Elements elements = doc.select("entry");
        for (Element elem : elements) {
            String aTxt = elem.select("published").first().text();
            MyDate aDate = MyDate.parseYmd(aTxt,
                    new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH));
            Element anchor = elem.select("link").first();
            String url = anchor.attr("abs:href");
            CompanyNews news = new CompanyNews(stockId, url, aDate);
            news.title = elem.select("title").text();
            news.createdDate = MyDate.getToday();
            news.type = newsType;
            if (news.hasEnough() &&
                    news.announcementDate.compareTo(MyDate.getPast(90)) > 0) {
                newsList.add(news);
            }
        }
    }

    /**
     * 銘柄コードに対応するクローラーを取得.
     */
    public static Map<String, CompanyNewsCollector> getStockCollectorMap() {
        List<CompanyNewsCollector> collectorList = getAllCollectors();
        Map<String, CompanyNewsCollector> collectorMap = new HashMap<String, CompanyNewsCollector>();
        for (CompanyNewsCollector collector : collectorList) {
            String collectorName = collector.getClass().getSimpleName();
            String stockCode = collectorName.replaceAll("[^0-9]", "");
            if (stockCode.length() > 0) {
                collectorMap.put(stockCode, collector);
            }
        }
        return collectorMap;
    }

    public static List<CompanyNewsCollector> getAllCollectors() {
        List<CompanyNewsCollector> collectors = new ArrayList<CompanyNewsCollector>();
        collectors.add(new CompanyNewsCollectorHotTopic());
        collectors.add(new CompanyNewsCollectorKabutan41());
        collectors.add(new CompanyNewsCollectorKabutanRanking());
        collectors.add(new CompanyNewsCollector4689());
/*
    collectors.add(new CompanyNewsCollector3668());
    collectors.add(new CompanyNewsCollector2705());
    collectors.add(new CompanyNewsCollector3093());
    collectors.add(new CompanyNewsCollector3395());
    collectors.add(new CompanyNewsCollector3091());
    collectors.add(new CompanyNewsCollector9853());
    collectors.add(new CompanyNewsCollector2780());
    collectors.add(new CompanyNewsCollector3181());
    collectors.add(new CompanyNewsCollector2735());
    collectors.add(new CompanyNewsCollector7647());
    collectors.add(new CompanyNewsCollector3094());
    collectors.add(new CompanyNewsCollector2698());
    collectors.add(new CompanyNewsCollector2674());
    collectors.add(new CompanyNewsCollector9927());
    collectors.add(new CompanyNewsCollector3021());
    collectors.add(new CompanyNewsCollector3177());
    collectors.add(new CompanyNewsCollector7610());
    collectors.add(new CompanyNewsCollector3313());
    collectors.add(new CompanyNewsCollector6076());
    collectors.add(new CompanyNewsCollector8273());
    collectors.add(new CompanyNewsCollector7611());
    collectors.add(new CompanyNewsCollector9990());
    collectors.add(new CompanyNewsCollector3169());
    collectors.add(new CompanyNewsCollector3133());
    collectors.add(new CompanyNewsCollector3329());
    collectors.add(new CompanyNewsCollector9899());
    collectors.add(new CompanyNewsCollector3175());
    collectors.add(new CompanyNewsCollector9842());
    collectors.add(new CompanyNewsCollector3077());
    collectors.add(new CompanyNewsCollector3082());
    collectors.add(new CompanyNewsCollector3224());
    collectors.add(new CompanyNewsCollector3221());
    collectors.add(new CompanyNewsCollector7412());
    collectors.add(new CompanyNewsCollector3366());
    collectors.add(new CompanyNewsCollector9994());
    collectors.add(new CompanyNewsCollector2686());
    collectors.add(new CompanyNewsCollector9640());
    collectors.add(new CompanyNewsCollector3075());
    collectors.add(new CompanyNewsCollector8230());
    collectors.add(new CompanyNewsCollector2753());
    collectors.add(new CompanyNewsCollector7618());
    collectors.add(new CompanyNewsCollector8167());
    collectors.add(new CompanyNewsCollector8252());
    collectors.add(new CompanyNewsCollector8218());
    collectors.add(new CompanyNewsCollector2659());
    collectors.add(new CompanyNewsCollector7606());
    collectors.add(new CompanyNewsCollector8008());
    collectors.add(new CompanyNewsCollector3050());
    collectors.add(new CompanyNewsCollector3372());
    collectors.add(new CompanyNewsCollector8215());
    collectors.add(new CompanyNewsCollector2675());
    collectors.add(new CompanyNewsCollector2373());
    collectors.add(new CompanyNewsCollector6082());
    collectors.add(new CompanyNewsCollector3223());
    collectors.add(new CompanyNewsCollector9835());
    collectors.add(new CompanyNewsCollector3032());
    collectors.add(new CompanyNewsCollector3346());
    collectors.add(new CompanyNewsCollector3317());
    collectors.add(new CompanyNewsCollector7577());
    collectors.add(new CompanyNewsCollector2789());
    collectors.add(new CompanyNewsCollector3318());
    collectors.add(new CompanyNewsCollector7571());
    collectors.add(new CompanyNewsCollector3147());
    collectors.add(new CompanyNewsCollector2796());
    collectors.add(new CompanyNewsCollector3134());
    collectors.add(new CompanyNewsCollector2662());
    collectors.add(new CompanyNewsCollector3067());
    collectors.add(new CompanyNewsCollector2786());
    collectors.add(new CompanyNewsCollector3370());
    collectors.add(new CompanyNewsCollector3136());
    collectors.add(new CompanyNewsCollector3083());
    collectors.add(new CompanyNewsCollector2138());
    collectors.add(new CompanyNewsCollector4565());
    collectors.add(new CompanyNewsCollector3845());
    collectors.add(new CompanyNewsCollector3664());
    collectors.add(new CompanyNewsCollector7415());
    collectors.add(new CompanyNewsCollector7623());
    collectors.add(new CompanyNewsCollector7462());
    collectors.add(new CompanyNewsCollector3358());
    collectors.add(new CompanyNewsCollector6630());
    collectors.add(new CompanyNewsCollector3811());
    collectors.add(new CompanyNewsCollector3909());
    collectors.add(new CompanyNewsCollector2120());
    collectors.add(new CompanyNewsCollector2342());
    collectors.add(new CompanyNewsCollector2345());
    collectors.add(new CompanyNewsCollector2440());
    collectors.add(new CompanyNewsCollector3665());
    collectors.add(new CompanyNewsCollector7844());
    collectors.add(new CompanyNewsCollector6444());
    collectors.add(new CompanyNewsCollector2454());
    collectors.add(new CompanyNewsCollector3760());
    collectors.add(new CompanyNewsCollector3639());
    collectors.add(new CompanyNewsCollector9836());
    collectors.add(new CompanyNewsCollector2655());
    collectors.add(new CompanyNewsCollector2668());
    collectors.add(new CompanyNewsCollector2751());
    collectors.add(new CompanyNewsCollector3069());
    collectors.add(new CompanyNewsCollector6070());
    collectors.add(new CompanyNewsCollector7448());
    collectors.add(new CompanyNewsCollector6918());
    collectors.add(new CompanyNewsCollector8274());
    collectors.add(new CompanyNewsCollector9651());
    collectors.add(new CompanyNewsCollector3035());
    collectors.add(new CompanyNewsCollector3196());
    collectors.add(new CompanyNewsCollector2929());
    collectors.add(new CompanyNewsCollector6276());
    collectors.add(new CompanyNewsCollector4316());
    collectors.add(new CompanyNewsCollector3678());
    collectors.add(new CompanyNewsCollector2338());
    collectors.add(new CompanyNewsCollector3420());
    collectors.add(new CompanyNewsCollector9424());
    collectors.add(new CompanyNewsCollector7820());
    collectors.add(new CompanyNewsCollector4752());
    collectors.add(new CompanyNewsCollector7502());
    collectors.add(new CompanyNewsCollector6473());
    collectors.add(new CompanyNewsCollector6432());
    collectors.add(new CompanyNewsCollector4506());
    collectors.add(new CompanyNewsCollector9533());
    collectors.add(new CompanyNewsCollector7832());
    collectors.add(new CompanyNewsCollector4202());
    collectors.add(new CompanyNewsCollector6367());
    collectors.add(new CompanyNewsCollector6118());
    collectors.add(new CompanyNewsCollector6315());
    collectors.add(new CompanyNewsCollector4092());
    collectors.add(new CompanyNewsCollector4681());
    collectors.add(new CompanyNewsCollector2453());
    collectors.add(new CompanyNewsCollector8789());
    collectors.add(new CompanyNewsCollector5918());
    collectors.add(new CompanyNewsCollector6069());
    collectors.add(new CompanyNewsCollector2181());
    collectors.add(new CompanyNewsCollector5381());
    collectors.add(new CompanyNewsCollector4674());
    collectors.add(new CompanyNewsCollector4847());
    collectors.add(new CompanyNewsCollector3193());
    collectors.add(new CompanyNewsCollector2667());
    collectors.add(new CompanyNewsCollector3689());
    collectors.add(new CompanyNewsCollector6095());
    collectors.add(new CompanyNewsCollector6067());
    collectors.add(new CompanyNewsCollector3691());
    collectors.add(new CompanyNewsCollector3900());
    collectors.add(new CompanyNewsCollector4704());
    collectors.add(new CompanyNewsCollector3692());
    collectors.add(new CompanyNewsCollector3356());
    collectors.add(new CompanyNewsCollector3042());
    collectors.add(new CompanyNewsCollector3782());
    collectors.add(new CompanyNewsCollector3394());
    collectors.add(new CompanyNewsCollector3697());
    collectors.add(new CompanyNewsCollector2158());
    collectors.add(new CompanyNewsCollector2326());
    collectors.add(new CompanyNewsCollector3916());
    collectors.add(new CompanyNewsCollector3857());
    collectors.add(new CompanyNewsCollector6050());
    collectors.add(new CompanyNewsCollector6675());
    collectors.add(new CompanyNewsCollector3774());
    collectors.add(new CompanyNewsCollector3040());
    collectors.add(new CompanyNewsCollector3682());
    collectors.add(new CompanyNewsCollector4288());
    collectors.add(new CompanyNewsCollector3393());
    collectors.add(new CompanyNewsCollector4662());
    collectors.add(new CompanyNewsCollector3800());
    collectors.add(new CompanyNewsCollector3656());
    collectors.add(new CompanyNewsCollector9759());
    collectors.add(new CompanyNewsCollector4736());
    collectors.add(new CompanyNewsCollector4344());
    collectors.add(new CompanyNewsCollector2467());
    collectors.add(new CompanyNewsCollector4557());
    collectors.add(new CompanyNewsCollector2370());
    collectors.add(new CompanyNewsCollector7420());
    collectors.add(new CompanyNewsCollector2191());
    collectors.add(new CompanyNewsCollector2931());
    collectors.add(new CompanyNewsCollector4570());
    collectors.add(new CompanyNewsCollector4588());
    collectors.add(new CompanyNewsCollector6090());
    collectors.add(new CompanyNewsCollector3689());
    collectors.add(new CompanyNewsCollector3069());
    collectors.add(new CompanyNewsCollector4572());
    collectors.add(new CompanyNewsCollector7777());
    collectors.add(new CompanyNewsCollector2425());
    collectors.add(new CompanyNewsCollector6077());
    collectors.add(new CompanyNewsCollector2374());
    collectors.add(new CompanyNewsCollector7545());
    collectors.add(new CompanyNewsCollector2398());
    collectors.add(new CompanyNewsCollector7621());
    collectors.add(new CompanyNewsCollector9707());
    collectors.add(new CompanyNewsCollector3777());
    collectors.add(new CompanyNewsCollector2468());
    collectors.add(new CompanyNewsCollector3796());
    collectors.add(new CompanyNewsCollector3815());
    collectors.add(new CompanyNewsCollector7608());
    collectors.add(new CompanyNewsCollector8281());
    collectors.add(new CompanyNewsCollector4656());
    collectors.add(new CompanyNewsCollector6060());
    collectors.add(new CompanyNewsCollector6479());
    collectors.add(new CompanyNewsCollector6857());
    collectors.add(new CompanyNewsCollector2196());
    collectors.add(new CompanyNewsCollector4722());
    collectors.add(new CompanyNewsCollector4310());
    collectors.add(new CompanyNewsCollector7979());
    collectors.add(new CompanyNewsCollector8914());
    collectors.add(new CompanyNewsCollector6205());
    collectors.add(new CompanyNewsCollector9422());
    collectors.add(new CompanyNewsCollector6731());
    collectors.add(new CompanyNewsCollector7261());
    collectors.add(new CompanyNewsCollector7717());
    collectors.add(new CompanyNewsCollector7203());
    collectors.add(new CompanyNewsCollector6758());
    collectors.add(new CompanyNewsCollector2359());
    collectors.add(new CompanyNewsCollector6862());
    collectors.add(new CompanyNewsCollector9984());
    collectors.add(new CompanyNewsCollector3627());
    collectors.add(new CompanyNewsCollector3753());
    collectors.add(new CompanyNewsCollector2372());
    collectors.add(new CompanyNewsCollector6837());
    collectors.add(new CompanyNewsCollector2432());
    collectors.add(new CompanyNewsCollector3839());
    collectors.add(new CompanyNewsCollector1433());
    collectors.add(new CompanyNewsCollector3917());
    collectors.add(new CompanyNewsCollector3799());
    collectors.add(new CompanyNewsCollector2321());
    collectors.add(new CompanyNewsCollector4829());
    collectors.add(new CompanyNewsCollector1896());
    collectors.add(new CompanyNewsCollector3765());
    collectors.add(new CompanyNewsCollector4667());
    collectors.add(new CompanyNewsCollector6093());
    collectors.add(new CompanyNewsCollector4080());
    collectors.add(new CompanyNewsCollector3444());
    collectors.add(new CompanyNewsCollector3319());
    collectors.add(new CompanyNewsCollector6769());
    collectors.add(new CompanyNewsCollector3816());
    collectors.add(new CompanyNewsCollector3521());
    collectors.add(new CompanyNewsCollector3775());
    collectors.add(new CompanyNewsCollector9417());
    collectors.add(new CompanyNewsCollector3135());
    collectors.add(new CompanyNewsCollector6094());
    collectors.add(new CompanyNewsCollector3918());
    collectors.add(new CompanyNewsCollector3187());
    collectors.add(new CompanyNewsCollector6045());
    collectors.add(new CompanyNewsCollector9878());
    collectors.add(new CompanyNewsCollector4814());
    collectors.add(new CompanyNewsCollector3625());
    collectors.add(new CompanyNewsCollector2743());
    collectors.add(new CompanyNewsCollector4031());
    collectors.add(new CompanyNewsCollector9385());
    collectors.add(new CompanyNewsCollector3920());
    collectors.add(new CompanyNewsCollector3623());
    collectors.add(new CompanyNewsCollector8585());
    collectors.add(new CompanyNewsCollector2330());
    collectors.add(new CompanyNewsCollector8201());
    collectors.add(new CompanyNewsCollector4564());
    collectors.add(new CompanyNewsCollector4317());
    collectors.add(new CompanyNewsCollector6368());
    collectors.add(new CompanyNewsCollector6048());
    collectors.add(new CompanyNewsCollector3896());
    collectors.add(new CompanyNewsCollector3669());
    collectors.add(new CompanyNewsCollector3209());
    collectors.add(new CompanyNewsCollector6323());
    collectors.add(new CompanyNewsCollector8093());
    collectors.add(new CompanyNewsCollector6171());
    collectors.add(new CompanyNewsCollector4043());
    collectors.add(new CompanyNewsCollector2170());
    collectors.add(new CompanyNewsCollector8260());
    collectors.add(new CompanyNewsCollector3103());
    collectors.add(new CompanyNewsCollector8101());
    collectors.add(new CompanyNewsCollector5019());
    collectors.add(new CompanyNewsCollector8020());
    collectors.add(new CompanyNewsCollector3107());
    collectors.add(new CompanyNewsCollector5020());
    collectors.add(new CompanyNewsCollector9810());
    collectors.add(new CompanyNewsCollector5352());
    collectors.add(new CompanyNewsCollector5706());
    collectors.add(new CompanyNewsCollector5017());
    collectors.add(new CompanyNewsCollector7280());
    collectors.add(new CompanyNewsCollector5741());
    collectors.add(new CompanyNewsCollector9107());
    collectors.add(new CompanyNewsCollector8002());
    collectors.add(new CompanyNewsCollector7516());
    collectors.add(new CompanyNewsCollector9624());
    collectors.add(new CompanyNewsCollector1407());
    collectors.add(new CompanyNewsCollector5703());
    collectors.add(new CompanyNewsCollector2730());
    collectors.add(new CompanyNewsCollector5801());
    collectors.add(new CompanyNewsCollector8090());
    collectors.add(new CompanyNewsCollector7818());
    collectors.add(new CompanyNewsCollector3724());
*/
        return collectors;
    }

    public static List<CompanyNewsCollector> getTestCollectors() {
        List<CompanyNewsCollector> collectors = new ArrayList<CompanyNewsCollector>();
        collectors.add(new CompanyNewsCollectorKabutan41());
        return collectors;
    }

}
