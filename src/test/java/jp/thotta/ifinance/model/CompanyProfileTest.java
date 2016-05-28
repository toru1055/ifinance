package jp.thotta.ifinance.model;

import jp.thotta.ifinance.common.MyDate;
import junit.framework.TestCase;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompanyProfileTest extends TestCase {
    Connection c;
    Map<String, CompanyProfile> m;
    Statement st;
    CompanyProfile pr, prc;
    CompanyProfile pm1, pm2;

    protected void setUp() {
        MyDate d1 = new MyDate(2000, 1, 1);
        MyDate d2 = new MyDate(2011, 3, 5);
        MyDate d3 = new MyDate(1996, 5, 30);
        MyDate d4 = new MyDate(2013, 11, 3);
        pr = new CompanyProfile(1001);
        pr.companyName = "（株）段取り商事";
        pr.businessCategory = "業種大分類２";
        prc = new CompanyProfile(1001);
        prc.foundationDate = new MyDate(d1);
        prc.smallBusinessCategory = "業種小分類１";
        pm1 = new CompanyProfile(1003);
        pm1.companyName = "トレニー";
        pm1.businessCategory = "業種大分類２";
        pm1.smallBusinessCategory = "業種小分類１";
        pm1.foundationDate = new MyDate(d3);
        pm2 = new CompanyProfile(1004);
        pm2.companyName = "シキターリ";
        pm2.businessCategory = "業種大分類４";
        pm2.smallBusinessCategory = "業種小分類３";
        pm2.foundationDate = new MyDate(d4);
        m = new HashMap<String, CompanyProfile>();
        m.put(pm1.getKeyString(), pm1);
        m.put(pm2.getKeyString(), pm2);
        try {
            Database.setDbUrl("jdbc:sqlite:test.db");
            c = Database.getConnection();
            st = c.createStatement();
            CompanyProfile.dropTable(c);
            CompanyProfile.createTable(c);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void testGetKeyString() {
        CompanyProfile profile = new CompanyProfile(9999);
        assertEquals(profile.getKeyString(), "9999");
    }

    public void testExists() {
        try {
            assertFalse(pr.exists(st));
            pr.insert(st);
            assertTrue(pr.exists(st));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void testInsertUpdate() {
        try {
            pr.insert(st);
            prc.readDb(st);
            assertEquals(pr.companyName, prc.companyName);
            prc.update(st);
            pr.readDb(st);
            assertEquals(pr, prc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testUpdateMap() {
        try {
            CompanyProfile.updateMap(m, c);
            Map<String, CompanyProfile> fromDbMap = CompanyProfile.selectAll(c);
            for (String k : m.keySet()) {
                CompanyProfile pr_map = m.get(k);
                CompanyProfile pr_db = fromDbMap.get(k);
                assertEquals(pr_map, pr_db);
            }
            CompanyProfile prof3 = CompanyProfile.selectByStockId(1003, c);
            CompanyProfile prof4 = CompanyProfile.selectByStockId(2454, c);
            assertEquals(prof3, pm1);
            assertEquals(prof4, null);
            List<CompanyProfile> prList =
                    CompanyProfile.selectByQuery("シキタ", c);
            assertEquals(prList.size(), 1);
            assertEquals(prList.get(0), pm2);
        } catch (Exception e) {
            e.printStackTrace();
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
