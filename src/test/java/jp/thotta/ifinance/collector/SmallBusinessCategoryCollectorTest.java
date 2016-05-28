package jp.thotta.ifinance.collector.kmonos;

import jp.thotta.ifinance.model.CompanyProfile;
import jp.thotta.ifinance.model.Database;
import junit.framework.TestCase;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmallBusinessCategoryCollectorTest
        extends TestCase {
    Map<String, CompanyProfile> profiles;
    Connection c;

    protected void setUp() {
        profiles = new HashMap<String, CompanyProfile>();
        try {
            Database.setDbUrl("jdbc:sqlite:test.db");
            c = Database.getConnection();
            CompanyProfile.dropTable(c);
            CompanyProfile.createTable(c);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void testMakeCategoryCompanies() {
        SmallBusinessCategoryCollectorImpl coll =
                new SmallBusinessCategoryCollectorImpl(2);
        Map<String, List<Integer>> m = coll.makeCategoryCompanies();
        for (String categoryName : m.keySet()) {
            List<Integer> l = m.get(categoryName);
            assertTrue(l.size() > 0);
            System.out.println("categoryName: " + categoryName + ", size = " + l.size());
            //System.out.println(l);
        }
    }

    public void testAppend() {
        SmallBusinessCategoryCollectorImpl coll =
                new SmallBusinessCategoryCollectorImpl(2);
        Map<String, CompanyProfile> profiles =
                new HashMap<String, CompanyProfile>();
        try {
            coll.append(profiles);
            for (String k : profiles.keySet()) {
                CompanyProfile cp = profiles.get(k);
                assertTrue(cp.stockId > 0 && cp.stockId < 100000);
                assertTrue(!cp.smallBusinessCategory.equals(""));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void testDirectDb() {
        SmallBusinessCategoryCollectorImpl coll =
                new SmallBusinessCategoryCollectorImpl(2);
        try {
            coll.appendDb(c);
            Map<String, CompanyProfile> cm = CompanyProfile.selectAll(c);
            assertTrue(cm.size() > 0);
            for (String k : cm.keySet()) {
                CompanyProfile cp = cm.get(k);
                assertTrue(cp.stockId > 0 && cp.stockId < 100000);
                assertTrue(!cp.smallBusinessCategory.equals(""));
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
