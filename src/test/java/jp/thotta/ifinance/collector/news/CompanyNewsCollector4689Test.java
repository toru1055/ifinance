package jp.thotta.ifinance.collector.news;

import jp.thotta.ifinance.common.MyDate;
import jp.thotta.ifinance.model.CompanyNews;
import jp.thotta.ifinance.model.Database;
import junit.framework.TestCase;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CompanyNewsCollector4689Test extends TestCase {
    Connection c;

    protected void setUp() {
        try {
            Database.setDbUrl("jdbc:sqlite:test.db");
            c = Database.getConnection();
            CompanyNews.dropTable(c);
            CompanyNews.createTable(c);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void testParsePRList() {
        try {
            CompanyNewsCollector4689 coll = new CompanyNewsCollector4689();
            List<CompanyNews> prList = new ArrayList<CompanyNews>();
            coll.parsePRList(prList);
            assertTrue(prList.size() > 0);
            for (CompanyNews pr : prList) {
                assertTrue(pr.hasEnough());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void testParseIRList() {
        try {
            CompanyNewsCollector4689 coll = new CompanyNewsCollector4689();
            List<CompanyNews> prList = new ArrayList<CompanyNews>();
            coll.parseIRList(prList);
            assertTrue(prList.size() > 0);
            for (CompanyNews pr : prList) {
                assertTrue(pr.hasEnough());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void testAppend() {
        try {
            CompanyNewsCollector4689 coll = new CompanyNewsCollector4689();
            List<CompanyNews> prList = new ArrayList<CompanyNews>();
            coll.append(prList);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void testAppendDb() {
        CompanyNewsCollector4689 coll = new CompanyNewsCollector4689();
        try {
            coll.appendDb(c);
            List<CompanyNews> newsList = CompanyNews.selectByDate(c, MyDate.getToday());
            assertTrue(newsList.size() > 0);
            for (CompanyNews news : newsList) {
                assertTrue(news.hasEnough());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    protected void tearDown() {
        try {
            Database.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
