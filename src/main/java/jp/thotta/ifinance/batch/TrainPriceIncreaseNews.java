package jp.thotta.ifinance.batch;

import jp.thotta.ifinance.adhoc.TrainPriceIncreaseNewsRegression;
import jp.thotta.ifinance.common.FailToScrapeException;
import jp.thotta.ifinance.common.MyDate;
import jp.thotta.ifinance.common.ParseNewsPageException;
import jp.thotta.ifinance.common.Scraper;
import jp.thotta.oml.client.OmlClient;
import jp.thotta.oml.client.io.Label;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 株探：「朝刊」ニュース銘柄で値上り率を学習.
 */
public class TrainPriceIncreaseNews {
    static final String BASE_URL = "http://kabutan.jp/warning/?mode=4_1";
    Map<String, Double> scoreMap;
    Map<String, List<String>> newsMap;
    OmlClient tr_client;
    OmlClient pr_client;

    public TrainPriceIncreaseNews() {
        try {
            scoreMap = new HashMap<String, Double>();
            newsMap = new HashMap<String, List<String>>();
            setTrainData();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public Set<String> keySet() {
        return scoreMap.keySet();
    }

    public Double getScore(String k) {
        return scoreMap.get(k);
    }

    public List<String> getNewsList(String k) {
        return newsMap.get(k);
    }

    void setTrainData()
            throws FailToScrapeException, ParseNewsPageException {
        Document doc = Scraper.getHtml(BASE_URL);
        String aText = doc.select("div.meigara_count > ul > li:nth-child(1)").text();
        MyDate aDate = MyDate.parseYmd(aText, new SimpleDateFormat("yyyy年MM月dd日"));
        if (!MyDate.getToday().equals(aDate)) {
            System.err.println("Date is not match: pageDate=" + aDate + ", today=" + MyDate.getToday());
            return;
        }
        Map<String, Boolean> pageUrlMap = new HashMap<String, Boolean>();
        Elements pageAnchors = doc.select("div.pagination > ul > li > a");
        for (Element pageAnchor : pageAnchors) {
            String pageUrl = pageAnchor.attr("abs:href");
            pageUrlMap.put(pageUrl, true);
        }
        pageUrlMap.put(BASE_URL, true);
        for (String pageUrl : pageUrlMap.keySet()) {
            Document d = Scraper.getHtml(pageUrl);
            Elements trList = d.select("#main > div.warning_contents > table > tbody > tr");
            for (Element tr : trList) {
                String stockId = tr.select("td:nth-child(1) > a").first().text();
                String newsTitle = tr.select("td:nth-child(5) > a").first().text();
                Element scoreElem = tr.select("td:nth-child(9) > span").first();
                String scoreText = tr.select("td:nth-child(9)").first().text().replaceAll("%$", "");
                Double score = 0.0;
                if (!"－".equals(scoreText)) {
                    score = Double.parseDouble(scoreText);
                }
                scoreMap.put(stockId, score);
                List<String> newsList = null;
                if (!newsMap.containsKey(stockId)) {
                    newsList = new ArrayList<String>();
                } else {
                    newsList = newsMap.get(stockId);
                }
                newsList.add(newsTitle);
                newsMap.put(stockId, newsList);
            }
        }
    }

    public void execPrediction() {
        int modelId = TrainPriceIncreaseNewsRegression.modelId;
        String parserType = TrainPriceIncreaseNewsRegression.parserType;
        String labelMode = TrainPriceIncreaseNewsRegression.labelMode;
        try {
            pr_client = OmlClient.createPredictBatchConnection(TrainPriceIncreaseNewsRegression.host);
            if (pr_client.configure(modelId, parserType, labelMode)) {
                Accuracy acc = new Accuracy();
                for (String k : newsMap.keySet()) {
                    List<String> newsList = newsMap.get(k);
                    Double score = scoreMap.get(k);
                    for (String newsTitle : newsList) {
                        Label label = pr_client.predictLabel(newsTitle);
                        acc.addResult(label.getScore(), score);
                    }
                }
                acc.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            try {
                pr_client.close();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    class Accuracy {
        int tp = 0, tn = 0, fp = 0, fn = 0;
        double sqLoss = 0.0;
        int counter = 0;

        public void addResult(double predict, double actual) {
            counter++;
            double loss = predict - actual;
            sqLoss += loss * loss;
            if (predict > 0.0) {
                if (actual > 0.0) {
                    tp++;
                } else {
                    fp++;
                }
            } else {
                if (actual > 0.0) {
                    fn++;
                } else {
                    tn++;
                }
            }
        }

        public void show() {
            double rmse = Math.sqrt(sqLoss) / counter;
            double precision = (double) tp / (tp + fp);
            double recall = (double) tp / (tp + fn);
            if (counter > 0) {
                System.out.println("tp = " + tp);
                System.out.println("fn = " + fn);
                System.out.println("fp = " + fp);
                System.out.println("tn = " + tn);
                System.out.println("precision = " + precision);
                System.out.println("recall = " + recall);
                System.out.println("rmse = " + rmse);
            }
        }
    }

    public void execTrain() {
        int modelId = TrainPriceIncreaseNewsRegression.modelId;
        String parserType = TrainPriceIncreaseNewsRegression.parserType;
        String labelMode = TrainPriceIncreaseNewsRegression.labelMode;
        try {
            tr_client = OmlClient.createTrainBatchConnection(TrainPriceIncreaseNewsRegression.host);
            if (tr_client.configure(modelId, parserType, labelMode)) {
                for (String k : newsMap.keySet()) {
                    List<String> newsList = newsMap.get(k);
                    Double score = scoreMap.get(k);
                    for (String newsTitle : newsList) {
                        tr_client.train(String.valueOf(score), newsTitle);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            try {
                tr_client.close();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    public static void main(String[] args) {
        boolean trainFlag = true;
        if (args.length >= 1 && "not-train".equals(args[0])) {
            trainFlag = false;
        }
        TrainPriceIncreaseNews train = new TrainPriceIncreaseNews();
        System.out.println("== 学習前の精度 ==");
        train.execPrediction();
        if (trainFlag) {
            System.out.println("");
            train.execTrain();
        }
    }
}
