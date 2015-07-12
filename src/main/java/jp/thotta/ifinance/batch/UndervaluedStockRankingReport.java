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
    Map<String, JoinedStockInfo> jsiMap = JoinedStockInfo.selectMap(conn);
    Map<String, JoinedStockInfo> jsiFil = JoinedStockInfo.filterMap(jsiMap);
    System.out.println("train data size: " + jsiFil.size());
    List<PredictedStockPrice> pspList = makePredictedStockPrices(jsiFil);
    Collections.sort(pspList, new Comparator<PredictedStockPrice>() {
      @Override
      public int compare(PredictedStockPrice p1, PredictedStockPrice p2) {
        return p1.undervaluedScore() > p2.undervaluedScore() ? -1 : 1;
      }
    });
    System.out.println("==== Undervalued Stock Ranking ====");
    int reportCount = 0;
    for(PredictedStockPrice psp : pspList) {
      if(psp.isStableStock && psp.undervaluedScore() > 2.5) {
        if(reportCount++ < 50) {
          String lstr = String.format("[%d] %s", reportCount, psp);
          System.out.println(lstr);
        }
      }
    }
    return (reportCount > 0);
  }

  /**
   * TODO: PredictedStockPriceにJoinedStockPriceも持たせる
   */
  private List<PredictedStockPrice> makePredictedStockPrices(
      Map<String, JoinedStockInfo> jsiMap) {
    List<PredictedStockPrice> l = new ArrayList<PredictedStockPrice>();
    StockPricePredictor spp = new LinearStockPricePredictor();
    double rmse = spp.train(jsiMap);
    System.out.println("RMSE = " + rmse);
    StockStatsFilter filter = new StockStatsFilter(jsiMap, 50, 25, 50, 25); 
    for(String k : jsiMap.keySet()) {
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
      reporter.report();
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
