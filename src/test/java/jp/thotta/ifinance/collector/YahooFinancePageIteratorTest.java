package jp.thotta.ifinance.collector.yj_finance;

import junit.framework.TestCase;
import org.jsoup.nodes.Document;

/**
 * Unit test for PageIterator.
 */
public class YahooFinancePageIteratorTest
        extends TestCase {

    /**
     * 正常系のテスト.
     */
    public void testNormal() {
        System.out.println("Start testNormal");
        PageIterator iter
                = new PageIterator(46); // kd=46: SalesAmount
        Document doc = null;

        // Check first page
        String firstPageTop = "0";
        if (iter.hasNext()) {
            doc = iter.next();
            firstPageTop = doc.select("table.rankingTable")
                    .select("tr.rankingTabledata.yjM")
                    .first()
                    .select("td").first().text();
        }
        assertEquals(firstPageTop, "1");

        // Check second page
        String secondPageTop = "0";
        if (iter.hasNext()) {
            doc = iter.next();
            secondPageTop = doc.select("table.rankingTable")
                    .select("tr.rankingTabledata.yjM")
                    .first()
                    .select("td").first().text();
        }
        assertEquals(secondPageTop, "51");

        // Check 100th page.
        iter.setCurrentPage(100);
        assertEquals(iter.hasNext(), false);

        // Check other url.
        iter.setTargetUrl("http://www.yahoo.co.jp/");
        assertEquals(iter.hasNext(), false);
    }

    /**
     * リトライのテスト.
     */
    public void testRetry() {
        System.out.println("Start testRetry");
        PageIterator iter
                = new PageIterator(46); // kd=46: SalesAmount
        iter.setTargetUrl("http://can.not.access/");
        assertEquals(iter.hasNext(), false);
    }
}
