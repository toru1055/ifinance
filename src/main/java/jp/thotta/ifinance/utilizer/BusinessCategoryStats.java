package jp.thotta.ifinance.utilizer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;
import java.text.ParseException;

import jp.thotta.ifinance.common.StatSummary;
import jp.thotta.ifinance.model.CorporatePerformance;
import jp.thotta.ifinance.model.DailyStockPrice;
import jp.thotta.ifinance.model.PerformanceForecast;
import jp.thotta.ifinance.model.CompanyProfile;

/**
 * 業種ごとの統計情報を管理.
 */
public class BusinessCategoryStats {

  public String categoryName;
  public StatSummary iPsrSummary;
  public StatSummary iOperatingPerSummary;
  public StatSummary iOrdinaryPerSummary;
  public StatSummary iNetPerSummary;
  public StatSummary iPbrSummary;
}
