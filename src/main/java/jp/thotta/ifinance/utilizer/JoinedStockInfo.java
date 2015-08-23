package jp.thotta.ifinance.utilizer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;
import java.text.ParseException;

import jp.thotta.ifinance.model.CorporatePerformance;
import jp.thotta.ifinance.model.DailyStockPrice;
import jp.thotta.ifinance.model.PerformanceForecast;
import jp.thotta.ifinance.model.CompanyProfile;

/**
 * キーに対する株価関連情報をJoinしたクラス.
 * キーは今のところ銘柄IDだけど、
 * 銘柄ID×決算年とかになる可能性あり.
 */
public class JoinedStockInfo {

  public static final int FEATURE_DIMENSION = 6;
  public DailyStockPrice dailyStockPrice;
  public CorporatePerformance corporatePerformance;
  public CorporatePerformance corporatePerformance1; // 1 year ago
  public CorporatePerformance corporatePerformance2; // 2 years ago
  public PerformanceForecast performanceForecast;
  public CompanyProfile companyProfile;
  public BusinessCategoryStats businessCategoryStats;
  public double psrInverse = 0.0;
  public double perInverse = 0.0;
  public double pbrInverse = 0.0;

  public JoinedStockInfo(DailyStockPrice dsp,
      CorporatePerformance cp,
      PerformanceForecast pf,
      CompanyProfile prof,
      BusinessCategoryStats bc) {
    this.dailyStockPrice = dsp;
    this.corporatePerformance = cp;
    this.performanceForecast = pf;
    this.companyProfile = prof;
    this.businessCategoryStats = bc;
    if(cp.salesAmount != null) {
      this.psrInverse = (double)cp.salesAmount / dsp.marketCap;
    }
    if(cp.netProfit != null) {
      this.perInverse = (double)cp.netProfit / dsp.marketCap;
    }
    if(cp.totalAssets != null) {
      this.pbrInverse = (double)cp.totalAssets / dsp.marketCap;
    }
  }

  /**
   * 全ての要素が取得できたか.
   */
  public boolean hasEnough() {
    return dailyStockPrice != null &&
      corporatePerformance != null &&
      companyProfile != null &&
      businessCategoryStats != null &&
      dailyStockPrice.hasEnough() &&
      corporatePerformance.hasEnough() &&
      companyProfile.hasEnough() &&
      businessCategoryStats.hasEnough();
  }

  /**
   * Map用のキー取得.
   */
  public String getKeyString() {
    return dailyStockPrice.getJoinKey();
  }

  @Override
  public String toString() {
    return String.format(
        "key=%s, CompanyProfile={%s}, DailyStockPrice={%s}, CorporatePerformance={%s}, PerformanceForecast={%s}, BusinessCategoryStats={%s}", 
        getKeyString(), companyProfile, dailyStockPrice, corporatePerformance, performanceForecast, businessCategoryStats);
  }

  public String getDescription() {
    return String.format(
        "%s（%4d）[%s > %s]\n" +
        "現在株価[%.1f円], PER[%.2f倍], 配当利回り[%.2f％]\n" +
        "自己資本比率[%.2f％], 売上高[%d百万円]\n" +
        "営業利益[%d百万円], 1年成長率[%.2f％], 2年成長率[%.2f％] \n" +
        "純利益[%d百万円], 今期純利益(会社予想)[%d百万円], 今期予想成長率[%.2f％]\n" +
        "平均年齢[%.4f歳], 平均年収[%.4f万円], 設立年月日[%s]\n" +
        "企業特色：%s\n" +
        "決算推移：http://minkabu.jp/stock/%4d/consolidated \n" +
        "決算発表日[%s]\n",
        companyProfile.companyName,
        dailyStockPrice.stockId,
        companyProfile.businessCategory,
        companyProfile.smallBusinessCategory,
        actualStockPrice(), per(), dividendYieldPercent(),
        ownedCapitalRatioPercent(), corporatePerformance.salesAmount,
        corporatePerformance.operatingProfit,
        100 * growthRateOperatingProfit1(),
        100 * growthRateOperatingProfit2(),
        corporatePerformance.netProfit,
        estimateNetProfit(),
        100 * estimateNetGrowthRate(),
        companyProfile.averageAge,
        averageAnnualIncome(),
        companyProfile.foundationDate,
        companyProfile.companyFeature,
        dailyStockPrice.stockId,
        corporatePerformance.announcementDate);
  }

  public Double averageAnnualIncome() {
    if(companyProfile.averageAnnualIncome == null) {
      return null;
    } else {
      return companyProfile.averageAnnualIncome / 10000;
    }
  }

  /**
   * 自己資本比率を出力.
   */
  public double ownedCapitalRatioPercent() {
    return corporatePerformance.ownedCapitalRatio() * 100;
  }


  /**
   * 配当利回り（会社予想）を出力.
   */
  public double dividendYieldPercent() {
    if(performanceForecast == null ||
        performanceForecast.dividendYield == null) {
      return 0.0;
    } else {
      return performanceForecast.dividendYield * 100;
    }
  }

  public double per() {
    if(perInverse > 0.0) {
      return 1.0 / perInverse;
    } else {
      return -1.0;
    }
  }

  public double actualStockPrice() {
    return (double)(dailyStockPrice.marketCap * 1000000) /
      dailyStockPrice.stockNumber;
  }

  /**
   * 銘柄の説明変数ベクトルを返す.
   * @return 説明変数ベクトル x
   */
  public double[] getRegressors() {
    double[] x = new double[FEATURE_DIMENSION];
    //x[0] = (double)corporatePerformance.salesAmount;
    x[1] = estimateByBusinessCategoryOperatingPer();
    x[2] = estimateByBusinessCategoryNetPer();
    //x[1] = (double)corporatePerformance.operatingProfit;
    //x[2] = (double)corporatePerformance.netProfit;
    x[3] = getTotalDividend();
    x[4] = (double)corporatePerformance.ownedCapital;
    //x[5] = (double)corporatePerformance.otherCapital();
    x[5] = diffWithCategoryOperatingPer();
    x[0] = (double)estimateNetDiff();
    //x[6] = diffWithCategoryOrdinaryPer();
    //x[6] = (double)operatingProfitDiff2();
    //x[6] = (double)ordinaryProfitDiff2();
    //x[4] = (double)ordinaryProfitDiff1();
    //x[4] = (double)operatingProfitDiff1();
    //x[6] = estimateByBusinessCategoryOrdinaryPer();
    return x;
  }

  public long estimateNetProfit() {
    return performanceForecast.netEps * dailyStockPrice.stockNumber / 1000000;
  }

  public long estimateNetDiff() {
    return estimateNetProfit() - corporatePerformance.netProfit;
  }

  public double estimateNetGrowthRate() {
    if(corporatePerformance.netProfit < 0) {
      return 0.0;
    } else {
      return (double)estimateNetDiff() /
        corporatePerformance.netProfit;
    }
  }

  public double growthRate1() {
    return (double)ordinaryProfitDiff1() /
      corporatePerformance.ordinaryProfit;
  }

  public double growthRate2() {
    return (double)ordinaryProfitDiff2() /
      corporatePerformance.ordinaryProfit;
  }

  public double growthRateOperatingProfit1() {
    return (double)operatingProfitDiff1() /
      corporatePerformance.operatingProfit;
  }

  public double growthRateOperatingProfit2() {
    return (double)operatingProfitDiff2() /
      corporatePerformance.operatingProfit;
  }

  /**
   * 経常利益の一昨年からの差分
   */
  public long ordinaryProfitDiff2() {
    if(corporatePerformance2 != null &&
        corporatePerformance2.ordinaryProfit != null) {
      return corporatePerformance.ordinaryProfit -
        corporatePerformance2.ordinaryProfit;
    } else {
      return 0;
    }
  }

  /**
   * 経常利益の昨年からの差分
   */
  public long ordinaryProfitDiff1() {
    if(corporatePerformance1 != null &&
        corporatePerformance1.ordinaryProfit != null) {
      return corporatePerformance.ordinaryProfit -
        corporatePerformance1.ordinaryProfit;
    } else {
      return 0;
    }
  }

  /**
   * 営業利益の昨年からの差分
   */
  public long operatingProfitDiff1() {
    if(corporatePerformance1 != null &&
        corporatePerformance1.operatingProfit != null) {
      return corporatePerformance.operatingProfit -
        corporatePerformance1.operatingProfit;
    } else {
      return 0;
    }
  }

  /**
   * 営業利益の一昨年からの差分
   */
  public long operatingProfitDiff2() {
    if(corporatePerformance2 != null &&
        corporatePerformance2.operatingProfit != null) {
      return corporatePerformance.operatingProfit -
        corporatePerformance2.operatingProfit;
    } else {
      return 0;
    }
  }

  /**
   * 業種PERと営業利益増加分で時価総額増加分を推定.
   */
  public double diffWithCategoryOperatingPer() {
    double i_per = businessCategoryStats.operatingPerInverse.percentile(75);
    if(i_per <= 0) {
      return 0.0;
    } else {
      return (double)operatingProfitDiff2() / i_per;
    }
  }

  /**
   * 業種PERと経常利益増加分で時価総額増加分を推定.
   */
  public double diffWithCategoryOrdinaryPer() {
    double i_per = businessCategoryStats.ordinaryPerInverse.percentile(75);
    if(i_per <= 0) {
      return 0.0;
    } else {
      return (double)ordinaryProfitDiff2() / i_per;
    }
  }

  // TODO: 業種Perがマイナスになる時の対応を真面目に考えておく
  public double estimateByBusinessCategoryOperatingPer() {
    double d = (double)corporatePerformance.operatingProfit /
      //businessCategoryStats.operatingPerInverse.median();
      //businessCategoryStats.operatingPerInverse.max();
      businessCategoryStats.operatingPerInverse.percentile(75);
      //businessCategoryStats.operatingPerInverse.mean();
    return d > 0.0 ? d : 0.0;
  }

  public double estimateByBusinessCategoryOrdinaryPer() {
    double d = (double)corporatePerformance.ordinaryProfit /
      //businessCategoryStats.ordinaryPerInverse.median();
      //businessCategoryStats.ordinaryPerInverse.max();
      businessCategoryStats.ordinaryPerInverse.percentile(75);
      //businessCategoryStats.ordinaryPerInverse.mean();
    return d > 0.0 ? d : 0.0;
  }

  public double estimateByBusinessCategoryNetPer() {
    double d = (double)corporatePerformance.netProfit /
      //businessCategoryStats.netPerInverse.median();
      //businessCategoryStats.netPerInverse.max();
      businessCategoryStats.netPerInverse.percentile(75);
      //businessCategoryStats.netPerInverse.mean();
    return d > 0.0 ? d : 0.0;
  }

  public double debtWithInterest() {
    if(corporatePerformance.debtWithInterest != null) {
      return (double)corporatePerformance.debtWithInterest;
    } else {
      return 0.0;
    }
  }

  public double getDividend() {
    if(corporatePerformance.dividend != null) {
      return corporatePerformance.dividend;
    } else {
      return 0.0;
    }
  }

  /**
   * 配当金額の合計.
   * @return 合計配当金額(会社予想)
   */
  private double getTotalDividend() {
    return getDividend() * dailyStockPrice.stockNumber / 1000000;
  }

  /**
   * 銘柄の株価(目的変数)を返す.
   * @return 株価(目的変数)
   */
  public double getRegressand() {
    return (double)dailyStockPrice.marketCap;
  }

  /**
   * 紐付け対象のDBテーブルをJoinして、Mapを生成する.
   * 今はCorporatePerformance, DailyStockPrice, PerformanceForecast
   * @param c dbコネクション
   */
  public static Map<String, JoinedStockInfo> selectMap(Connection c) 
    throws SQLException, ParseException {
    Map<String, JoinedStockInfo> m = new HashMap<String, JoinedStockInfo>();
    Map<String, CorporatePerformance> cpMap = CorporatePerformance.selectLatests(c);
    Map<String, CorporatePerformance> cpMap1 = CorporatePerformance.selectPasts(c, 1);
    Map<String, CorporatePerformance> cpMap2 = CorporatePerformance.selectPasts(c, 2);
    Map<String, DailyStockPrice> dspMap = DailyStockPrice.selectLatests(c);
    Map<String, PerformanceForecast> pfMap = PerformanceForecast.selectLatests(c);
    Map<String, BusinessCategoryStats> bcMap = BusinessCategoryStats.selectMap(c);
    Map<String, CompanyProfile> profMap = CompanyProfile.selectAll(c);
    for(String key : dspMap.keySet()) {
      DailyStockPrice dsp = dspMap.get(key);
      CorporatePerformance cp = cpMap.get(key);
      CorporatePerformance cp1 = cpMap1.get(key);
      CorporatePerformance cp2 = cpMap2.get(key);
      PerformanceForecast pf = pfMap.get(key);
      CompanyProfile prof = profMap.get(key);
      if(cp != null && dsp != null &&
          prof != null && prof.smallBusinessCategory != null &&
          cp1 != null && cp2 != null &&
          pf != null && pf.netEps != null) {
        BusinessCategoryStats bc = bcMap.get(prof.smallBusinessCategory);
        JoinedStockInfo jsi = new JoinedStockInfo(dsp, cp, pf, prof, bc);
        jsi.corporatePerformance1 = cp1;
        jsi.corporatePerformance2 = cp2;
        m.put(jsi.getKeyString(), jsi);
      } else {
        //System.out.println(prof);
      }
    }
    return m;
  }

  /**
   * 全ての情報が取得できた銘柄だけに絞り込む.
   */
  public static Map<String, JoinedStockInfo> filterMap(Map<String, JoinedStockInfo> jsiMap) {
    Map<String, JoinedStockInfo> m = new HashMap<String, JoinedStockInfo>();
    for(String k : jsiMap.keySet()) {
      JoinedStockInfo jsi = jsiMap.get(k);
      if(jsi.hasEnough()) {
        m.put(jsi.getKeyString(), jsi);
      } else {
//        System.out.println(jsi);
      }
    }
    return m;
  }
}
