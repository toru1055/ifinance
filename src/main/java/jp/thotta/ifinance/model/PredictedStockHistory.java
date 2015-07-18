package jp.thotta.ifinance.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.HashMap;
import java.text.ParseException;

import jp.thotta.ifinance.common.MyDate;

/**
 * 株価予測結果クラス.
 * @author toru1055
 */
public class PredictedStockHistory
  //extends AbstractStockModel 
  //implements DBModel 
  {
  public int stockId; // pk
  public MyDate predictedDate; //pk
  public Long predictedMarketCap;
  public Boolean isStableStock;

  public PredictedStockHistory(int stockId, MyDate predictedDate) {
    this.stockId = stockId;
    this.predictedDate = predictedDate.copy();
  }
}
