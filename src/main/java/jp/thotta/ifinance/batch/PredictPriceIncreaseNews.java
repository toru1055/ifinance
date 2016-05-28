package jp.thotta.ifinance.batch;

import jp.thotta.ifinance.adhoc.TrainPriceIncreaseNewsRegression;
import jp.thotta.ifinance.common.MyDate;
import jp.thotta.ifinance.model.CompanyNews;
import jp.thotta.ifinance.model.Database;
import jp.thotta.oml.client.OmlClient;
import jp.thotta.oml.client.io.Label;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PredictPriceIncreaseNews extends BaseRankingReport {
    OmlClient pr_client;

    public PredictPriceIncreaseNews(Connection c, String tmpl)
            throws SQLException, ParseException {
        super(c, tmpl, "本日の値上りニュース予想ランキング");
        try {
            pr_client = OmlClient.createPredictBatchConnection(TrainPriceIncreaseNewsRegression.host);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    protected void setCompanyNewsMap()
            throws SQLException, ParseException {
        this.cnMap = CompanyNews.selectMapByDate(this.conn, MyDate.getToday(), 0);
    }

    @Override
    protected Map<String, Double> estimatePriceIncreaseRatio() {
        Map<String, Double> scoreMap = new HashMap<String, Double>();
        int modelId = TrainPriceIncreaseNewsRegression.modelId;
        String parserType = TrainPriceIncreaseNewsRegression.parserType;
        String labelMode = TrainPriceIncreaseNewsRegression.labelMode;
        try {
            if (pr_client.configure(modelId, parserType, labelMode)) {
                for (String k : cnMap.keySet()) {
                    double score = 0.0;
                    List<CompanyNews> cnList = cnMap.get(k);
                    for (CompanyNews news : cnList) {
                        if (news.url.contains("kabutan")) {
                            Label label = pr_client.predictLabel(news.title);
                            score += label.getScore() / cnList.size();
                        }
                    }
                    if (score > 0.0) {
                        scoreMap.put(k, score);
                    }
                }
                return scoreMap;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    public static void main(String[] args) {
        try {
            Connection c = Database.getConnection();
            String tmpl = "text";
            if (args.length >= 1) {
                tmpl = args[0];
            }
            PredictPriceIncreaseNews predictor =
                    new PredictPriceIncreaseNews(c, tmpl);
            predictor.report();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                Database.closeConnection();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
}

