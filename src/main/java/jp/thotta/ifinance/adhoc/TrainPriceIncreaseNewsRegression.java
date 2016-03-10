package jp.thotta.ifinance.adhoc;

import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.text.ParseException;

import jp.thotta.ifinance.model.Database;
import jp.thotta.ifinance.model.CompanyNews;

import jp.thotta.oml.client.OmlClient;
import jp.thotta.oml.client.io.Label;

/**
 * 値上り・値下りニュースの値上り率を線形回帰.
 */
public class TrainPriceIncreaseNewsRegression {
  public static final int modelId = 4;
  public static final String labelMode = "score";
  public static final String parserType = "ma";
  public static final String host = "localhost";
  Connection conn;
  OmlClient tr_client;
  OmlClient pr_client;

  public TrainPriceIncreaseNewsRegression(Connection c) {
    this.conn = c;
  }

  public void exec(String execMode)
    throws SQLException, ParseException, IOException, UnknownHostException {
    List<CompanyNews> p_news = CompanyNews.selectPriceIncrease(conn);
    List<CompanyNews> n_news = CompanyNews.selectPriceDecrease(conn);
    int counter = 0;
    if("train".equals(execMode)) {
      tr_client = OmlClient.createTrainBatchConnection(host);
      if(tr_client.configure(modelId, parserType, labelMode)) {
        while(p_news.size() > 100 && n_news.size() > 100) {
          CompanyNews p = p_news.remove(0);
          CompanyNews n = n_news.remove(0);
          String p_title = p.title.replaceAll("^【値上り】", "");
          String n_title = n.title.replaceAll("^【値下り】", "");
          tr_client.train("5.0", p_title);
          System.out.println((counter++) + ") positive: " + p_title);
          tr_client.train("-5.0", n_title);
          System.out.println((counter++) + ") negative: " + n_title);
        }
      }
    } else if("predict".equals(execMode)) {

      while(p_news.size() > 100 && n_news.size() > 100) {
        CompanyNews p = p_news.remove(0);
        CompanyNews n = n_news.remove(0);
      }
      pr_client = OmlClient.createPredictBatchConnection(host);
      counter = 0;
      int tp = 0, tn = 0, fp = 0, fn = 0;
      if(pr_client.configure(modelId, parserType, labelMode)) {
        while(p_news.size() > 0 && n_news.size() > 0) {
          CompanyNews p = p_news.remove(0);
          CompanyNews n = n_news.remove(0);
          String p_title = p.title.replaceAll("^【値上り】", "");
          String n_title = n.title.replaceAll("^【値下り】", "");
          Label p_label = pr_client.predictLabel(p_title);
          System.out.println((counter++) + ") positive: " + p_label.getScore() + ": " + p_title);
          Label n_label = pr_client.predictLabel(n_title);
          System.out.println((counter++) + ") negative: " + n_label.getScore() + ": " + n_title);
          if(p_label.isPositive()) { tp++; } else { fn++; }
          if(n_label.isPositive()) { fp++; } else { tn++; }
        }
      }

      System.out.println("tp = " + tp);
      System.out.println("fn = " + fn);
      System.out.println("fp = " + fp);
      System.out.println("tn = " + tn);
      double precision = (double)tp / (tp + fp);
      double recall = (double)tp / (tp + fn);
      System.out.println("precision = " + precision);
      System.out.println("recall = " + recall);
    } else {
      System.err.println("Syntax error. Args is 'train' or 'predict'.");
      System.exit(1);
    }
  }

  public static void main(String[] args) {
    try {
      Connection c = Database.getConnection();
      TrainPriceIncreaseNewsRegression trainer =
        new TrainPriceIncreaseNewsRegression(c);
      if(args.length > 0) {
        trainer.exec(args[0]);
      } else {
        System.err.println("Syntax error: Args needs 'train' or 'predict'");
        System.exit(1);
      }
    } catch(Exception e) {
      e.printStackTrace();
      System.exit(1);
    } finally {
      try {
        Database.closeConnection();
      } catch(SQLException e) {
        e.printStackTrace();
        System.exit(1);
      }
    }
  }
}
