package jp.thotta.ifinance.batch;

import jp.thotta.ifinance.adhoc.StockPriceIncreaseEstimator;
import jp.thotta.ifinance.adhoc.StockPriceIncreaseEstimatorValidation;
import jp.thotta.ifinance.model.CompanyNews;
import jp.thotta.ifinance.model.Database;
import jp.thotta.ifinance.utilizer.Utility;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * 直近の値上り率を銘柄の特徴で学習させる.
 */
public class TrainPriceIncreaseCompany extends BaseRankingReport {
    public static double p_threshold = 0.00;
    public static double a_threshold = 0.00;
    Connection c;
    StockPriceIncreaseEstimator estimator;
    StockPriceIncreaseEstimatorValidation validator;

    public TrainPriceIncreaseCompany(Connection c, String tmpl) throws Exception {
        super(c, tmpl, "【週刊】予想値上り銘柄ランキング");
        this.c = c;
        estimator = new StockPriceIncreaseEstimator(c);
        validator = new StockPriceIncreaseEstimatorValidation(c);
    }

    public void execTraining() {
        System.out.println("=== Training ===");
        estimator.execTraining();
        validator.execTraining();
    }

    public void execValidation() {
        System.err.println("=== Validation Result ===");
        Map<String, Double> m = new HashMap<String, Double>();
        validator.execPrediction();
        for (String k : validator.keySet()) {
            Double p = validator.getPredicted(k);
            Double a = validator.getActual(k);
            m.put(k, p - a);
        }
        double maxScore = 0.00;
        for (double pt = -0.20; pt <= 0.20; pt += 0.01) {
            for (double at = -0.20; at <= 0.20; at += 0.01) {
                double score = validate(pt, at, m);
                if (score > maxScore) {
                    maxScore = score;
                    p_threshold = pt;
                    a_threshold = at;
                }
            }
        }
        System.err.println("p_threshold=" + p_threshold +
                ", a_threshold=" + a_threshold +
                ", score=" + maxScore);
    }

    double validate(double pt, double at, final Map<String, Double> m) {
        int tp = 0, fp = 0;
        double score = 0.0;
        for (String k : Utility.sortedKeys(m)) {
            Double p = validator.getPredicted(k);
            Double a = validator.getActual(k);
            Double v = validator.getValidation(k);
            if (p > pt && a > at) {
                if (v > 0.0) {
                    tp++;
                } else {
                    fp++;
                }
                score += validator.getValidation(k);
            }
            if (tp + fp >= 30) {
                break;
            }
        }
        score = score / (tp + fp);
        double precision = (double) tp / (tp + fp);
        if ((tp + fp) <= 20) {
            return -1.0;
        } else {
            //return precision;
            return score;
        }
    }

    public void execPrediction() {
        System.out.println("=== Prediction Result ===");
        Map<String, Double> m = new HashMap<String, Double>();
        estimator.execPrediction();
        for (String k : estimator.keySet()) {
            Double p = estimator.getPredicted(k);
            Double a = estimator.getActual(k);
            if (p > p_threshold && a > a_threshold && (p - a) > 0.0) {
                m.put(k, p - a);
            }
        }
    }

    @Override
    protected void setCompanyNewsMap()
            throws SQLException, ParseException {
        this.cnMap = CompanyNews.selectMapByPast(this.conn, 30);
    }

    @Override
    protected Map<String, Double> estimatePriceIncreaseRatio() {
        Map<String, Double> m = new HashMap<String, Double>();
        estimator.execPrediction();
        for (String k : estimator.keySet()) {
            Double p = estimator.getPredicted(k);
            Double a = estimator.getActual(k);
            if (p > p_threshold && a > a_threshold && (p - a) > 0.0) {
                m.put(k, (p - a) * 100);
            }
        }
        return m;
    }

    @Override
    protected boolean isWeeklyChart() {
        return false;
    }

    @Override
    protected boolean isShowChart() {
        return true;
    }

    public static void main(String[] args) {
        String mode = "valid-predict";
        String tmpl = "text";
        if (args.length >= 1) {
            mode = args[0];
        }
        if (args.length >= 2) {
            tmpl = args[1];
        }
        try {
            TrainPriceIncreaseCompany train =
                    new TrainPriceIncreaseCompany(Database.getConnection(), tmpl);
            if ("train".equals(mode)) {
                train.execTraining();
            } else if ("predict".equals(mode)) {
                train.report();
            } else if ("validate".equals(mode)) {
                train.execValidation();
            } else if ("valid-predict".equals(mode)) {
                train.execValidation();
                train.report();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
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
