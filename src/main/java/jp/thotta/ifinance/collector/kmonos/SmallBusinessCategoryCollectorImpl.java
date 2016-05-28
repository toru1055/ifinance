package jp.thotta.ifinance.collector.kmonos;

import jp.thotta.ifinance.collector.CompanyProfileCollector;
import jp.thotta.ifinance.common.Scraper;
import jp.thotta.ifinance.model.CompanyProfile;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * kmonos.jpの業界一覧取得.
 *
 * @author toru1055
 */
public class SmallBusinessCategoryCollectorImpl
        implements CompanyProfileCollector {
    private static final String CATEGORY_LIST_URL = "https://kmonos.jp/industry/";

    private Integer testNum = null;

    public SmallBusinessCategoryCollectorImpl() {
        this(null);
    }

    public SmallBusinessCategoryCollectorImpl(Integer testNum) {
        this.testNum = testNum;
    }

    public void append(Map<String, CompanyProfile> profiles)
            throws IOException {
        Map<String, List<Integer>> categoryCompanies = makeCategoryCompanies();
        Map<Integer, String> companyCategory = new HashMap<Integer, String>();
        Map<Integer, Integer> companyCategorySize = new HashMap<Integer, Integer>();
        for (String categoryName : categoryCompanies.keySet()) {
            List<Integer> companyIdList = categoryCompanies.get(categoryName);
            for (Integer companyId : companyIdList) {
                if (companyCategorySize.containsKey(companyId)) {
                    if (companyIdList.size() < companyCategorySize.get(companyId)) {
                        companyCategory.put(companyId, categoryName);
                        companyCategorySize.put(companyId, companyIdList.size());
                    }
                } else {
                    companyCategory.put(companyId, categoryName);
                    companyCategorySize.put(companyId, companyIdList.size());
                }
            }
        }
        for (Integer companyId : companyCategory.keySet()) {
            String stockId = String.valueOf(companyId);
            if (profiles.containsKey(stockId)) {
                CompanyProfile prof = profiles.get(stockId);
                prof.smallBusinessCategory = companyCategory.get(companyId);
            } else {
                CompanyProfile prof = new CompanyProfile(companyId);
                prof.smallBusinessCategory = companyCategory.get(companyId);
                profiles.put(stockId, prof);
            }
        }
    }

    public Map<String, List<Integer>> makeCategoryCompanies() {
        Map<String, List<Integer>> categoryCompanies =
                new HashMap<String, List<Integer>>();
        Document doc = Scraper.get(CATEGORY_LIST_URL);
        Elements categoryAnchors =
                doc.select("div#contents > div > ul > li > a");
        for (Element categoryAnchor : categoryAnchors) {
            String categoryUrl = categoryAnchor.attr("abs:href");
            String categoryName = categoryAnchor.text();
            List<Integer> companyIdList = new ArrayList<Integer>();
            Document categoryDocument = Scraper.get(categoryUrl);
            Elements companyAnchors =
                    categoryDocument.select("div#explanation > ul > li > a");
            for (Element companyAnchor : companyAnchors) {
                String companyHref = companyAnchor.attr("href");
                int companyId = parseStockId(companyHref);
                companyIdList.add(companyId);
            }
            categoryCompanies.put(categoryName, companyIdList);
            if (testNum != null && testNum-- == 0) {
                break;
            }
        }
        return categoryCompanies;
    }

    public void appendDb(Connection conn)
            throws SQLException, IOException {
        Map<String, CompanyProfile> m =
                new HashMap<String, CompanyProfile>();
        append(m);
        CompanyProfile.updateMap(m, conn);
    }


    /**
     * 銘柄コードのパーサー.
     *
     * @param s 銘柄コードの文字列: "/6819.html"
     * @return 銘柄コードの数値
     */
    public static int parseStockId(String s) {
        String regex = "^/[0-9]{4,5}\\.html$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(s);
        if (m.find()) {
            return Integer.parseInt(s.replaceAll("[^0-9]", ""));
        } else {
            throw new IllegalArgumentException(
                    "Expected Regex[" + regex + "], " +
                            "Input[" + s + "]");
        }
    }
}
