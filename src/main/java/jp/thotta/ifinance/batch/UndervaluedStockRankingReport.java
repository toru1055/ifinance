package jp.thotta.ifinance.batch;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.text.ParseException;

import jp.thotta.ifinance.utilizer.*;
import jp.thotta.ifinance.model.Database;
import jp.thotta.ifinance.model.PredictedStockPrice;
import jp.thotta.ifinance.common.MyDate;

/**
 * 割安銘柄のランキングレポート.
 * @author toru1055
 */
public class UndervaluedStockRankingReport {
  Connection conn;

  public UndervaluedStockRankingReport(Connection c) {
    this.conn = c;
  }

  /**
   * レポート実行.
   * @return レポートが成功したかどうか
   */
  public boolean report() 
    throws SQLException, ParseException {
    List<PredictedStockPrice> pspList = makePredictedStockPrices();
    Collections.sort(pspList, new Comparator<PredictedStockPrice>() {
      @Override
      public int compare(PredictedStockPrice p1, PredictedStockPrice p2) {
        return p1.undervaluedScore() > p2.undervaluedScore() ? -1 : 1;
      }
    });
    System.out.println("==== Undervalued Stock Ranking ====");
    int reportCount = 0;
    for(PredictedStockPrice psp : pspList) {
      if(psp.isStableStock && 
          psp.ownedCapitalRatioPercent() > 40.0 &&
          psp.undervaluedScore() > 1.3) {
        if(reportCount++ < 50) {
          String lstr = String.format("[%d] %s", reportCount, psp);
          System.out.println(lstr);
          //System.out.println(psp.jsi.corporatePerformance);
        }
      }
    }
    return (reportCount > 0);
  }

  public void showPredictedStockPrice(int stockId)
    throws SQLException, ParseException {
    Map<String, JoinedStockInfo> jsiMap = JoinedStockInfo.selectMap(conn);
    Map<String, JoinedStockInfo> jsiFil = JoinedStockInfo.filterMap(jsiMap);
    StockPricePredictor spp = new LinearStockPricePredictor();
    double rmse = spp.train(jsiFil);
    System.out.println("Train data size = " + jsiFil.size() + ", RMSE = " + rmse);
    StockStatsFilter filter = new StockStatsFilter(jsiMap, 50, 25, 50, 25);
    String k = String.format("%4d", stockId);
    JoinedStockInfo jsi = jsiMap.get(k);
    PredictedStockPrice psp = new PredictedStockPrice(jsi, spp, filter);
    System.out.println(psp);
  }

  private List<PredictedStockPrice> makePredictedStockPrices()
    throws SQLException, ParseException {
    Map<String, JoinedStockInfo> jsiMap = JoinedStockInfo.selectMap(conn);
    Map<String, JoinedStockInfo> jsiFil = JoinedStockInfo.filterMap(jsiMap);
    StockPricePredictor spp = new LinearStockPricePredictor();
    double rmse = spp.train(jsiFil);
    System.out.println("Train data size = " + jsiFil.size() + ", RMSE = " + rmse);
    StockStatsFilter filter = new StockStatsFilter(jsiMap, 75, 60, 75, 60); 
    List<PredictedStockPrice> l = new ArrayList<PredictedStockPrice>();
    for(String k : jsiFil.keySet()) {
      JoinedStockInfo jsi = jsiMap.get(k);
      PredictedStockPrice psp = new PredictedStockPrice(jsi, spp, filter);
      l.add(psp);
    }
    return l;
  }

  public static void main(String[] args) {
    try {
      Connection c = Database.getConnection();
      UndervaluedStockRankingReport reporter 
        = new UndervaluedStockRankingReport(c);
      if(args.length == 0) {
        reporter.report();
      } else {
        reporter.showPredictedStockPrice(Integer.parseInt(args[0]));
      }
    } catch(Exception e) {
      e.printStackTrace();
    } finally {
      try {
        Database.closeConnection();
      } catch(SQLException e) {
        e.printStackTrace();
      }
    }
  }
}
