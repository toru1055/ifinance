package jp.thotta.ifinance.common;

import junit.framework.TestCase;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.regex.Pattern;

public class ScraperTest extends TestCase {
    public void testGet() {
        String url = "http://profile.yahoo.co.jp/independent/3787";
        Document d = Scraper.get(url);
        Elements records = d.select("table.yjMt").select("tr");
        assertEquals(records.get(0).select("td").get(1).text(), "前期");
        url = "http://unknown.host.jp/";
        Document d2 = Scraper.get(url);
        assertEquals(d2, null);
    }

    public void testProfile() {
        String url = "http://profile.yahoo.co.jp/fundamental/1954";
        Document d = Scraper.get(url);
        Elements records = d.select("table").select("tr").select("table").select("tr");
        for (Element tr : records) {
            String td0 = tr.select("td").get(0).text();
            String td1 = tr.select("td").get(1).text();
            if (td0.equals("特色")) {
                System.out.println(td1);
            }
            System.out.println(td0);
        }
        System.out.println(d.select("div.selectFinTitle").select("h1 > strong.yjL").text());
    }

    public void testKmonos() {
        String url = "https://kmonos.jp/industry/";
        //String url = "https://github.com/";
        Document d = Scraper.get(url);
        Elements anchors = d.select("div#contents > div > ul > li > a");
        System.out.println(anchors.size());
        assertTrue(anchors.size() > 200);
        for (Element anchor : anchors) {
            assertTrue(anchor.attr("abs:href").length() > 0);
            assertTrue(anchor.text().length() > 0);
        }
        url = "https://kmonos.jp/industry/9230120100.html";
        d = Scraper.get(url);
        anchors = d.select("div#explanation > ul > li > a");
        assertTrue(anchors.size() > 30);
        for (Element anchor : anchors) {
            //System.out.println(anchor.attr("href"));
            assertTrue(anchor.attr("href").length() > 0);
            assertTrue(anchor.text().length() > 0);
        }
    }

    public void testGetJs() {
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);
        try {
            Document doc = Scraper.getJs("http://www.treasurefactory.co.jp/compainfo/publicity.html");
            Elements elems = doc.select("div.irp-press-listS > div.news");
            assertTrue(elems.size() > 0);
            System.out.println(elems);
        } catch (FailToScrapeException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testKabutan() {
        try {
            String url = "http://kabutan.jp/news/marketnews/?b=n201510270027";
            Document doc = Scraper.getHtml(url);
            Element p = doc.select("#shijyounews > p").first();
            List<Node> nodes = p.childNodes();
            for (Node n : nodes) {
                if (n instanceof Element) {
                    Element elem = (Element) n;
                    Element a = elem.select("a").first();
                    if (a != null) {
                        String stockId = elem.text();
                        String next1 = n.nextSibling().toString();
                        String next2 = n.nextSibling().nextSibling().toString();
                        String title = n.nextSibling().nextSibling().nextSibling().toString();
                        if (next2.equals("<br>")
                                && Pattern.compile("^[0-9]{4}$").matcher(stockId).find()
                                && title.replaceAll(" ", "").length() > 0) {
                            System.out.println(stockId + ": " + title);
                            //System.out.println("node: " + n);
                            //System.out.println("node.next1: " + n.nextSibling());
                            //System.out.println("node.next2: " + n.nextSibling().nextSibling());
                            //System.out.println("node.next3: " + n.nextSibling().nextSibling().nextSibling());
                        }
                    }
                }
            }
        } catch (FailToScrapeException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

}
