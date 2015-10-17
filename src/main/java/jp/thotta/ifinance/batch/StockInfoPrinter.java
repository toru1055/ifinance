package jp.thotta.ifinance.batch;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.text.ParseException;

import jp.thotta.ifinance.utilizer.JoinedStockInfo;
import jp.thotta.ifinance.utilizer.PredictedStockPrice;
import jp.thotta.ifinance.model.Database;
import jp.thotta.ifinance.model.CompanyNews;
import jp.thotta.ifinance.model.CompanyProfile;
import jp.thotta.ifinance.model.DailyStockPrice;
import jp.thotta.ifinance.common.MyDate;
import jp.thotta.ifinance.collector.CompanyNewsCollector;
import jp.thotta.ifinance.collector.BaseCompanyNewsCollector;

public class StockInfoPrinter {
  JoinedStockInfo joinedStockInfo;
  CompanyProfile companyProfile;
  CompanyNews rankingNews;
  DailyStockPrice dailyStockPrice;
  PredictedStockPrice predictedStockPrice;
  List<CompanyNews> companyNewsList;
  CompanyNewsCollector companyNewsCollector;
  String message;
  public Integer rank;

  public StockInfoPrinter(
      JoinedStockInfo jsi,
      CompanyProfile profile,
      CompanyNews rankingNews,
      DailyStockPrice dsp,
      PredictedStockPrice psp,
      List<CompanyNews> cnList,
      CompanyNewsCollector coll,
      String message) {
    this.joinedStockInfo = jsi;
    this.companyProfile = profile;
    this.rankingNews = rankingNews;
    this.dailyStockPrice = dsp;
    this.predictedStockPrice = psp;
    this.companyNewsList = cnList;
    this.companyNewsCollector = coll;
    this.message = message;
    if(psp != null && psp.joinedStockInfo != null) {
      if(jsi == null) {
        this.joinedStockInfo = psp.joinedStockInfo;
      }
      if(profile == null) {
        this.companyProfile = psp.joinedStockInfo.companyProfile;
      }
      if(dsp == null) {
        this.dailyStockPrice = psp.joinedStockInfo.dailyStockPrice;
      }
    }
  }

  public StockInfoPrinter(
      JoinedStockInfo jsi,
      CompanyProfile profile,
      CompanyNews rankingNews,
      DailyStockPrice dsp,
      PredictedStockPrice psp,
      List<CompanyNews> cnList,
      CompanyNewsCollector coll) {
    this(jsi, profile, rankingNews, dsp, psp, cnList, coll, null);
  }

  private String getRank() {
    if(rank == null) {
      return "";
    } else {
      return String.format("[%d] ", rank);
    }
  }

  private String getNewsListHtml() {
    String newsListHtml = "";
    if(companyNewsList != null && companyNewsList.size() > 0) {
      for(CompanyNews news : companyNewsList) {
        newsListHtml += String.format(
            "・<a href='%s'>%s (%s)</a><br>\n",
            news.url, news.title, news.announcementDate.toString());
      }
    } else {
      if(companyNewsCollector == null) {
        newsListHtml = "この銘柄はまだクロールしていません\n";
      } else {
        newsListHtml = "直近のニュースはありません\n";
      }
    }
    return newsListHtml;
  }

  private Integer getStockId() {
    if(dailyStockPrice != null) {
      return dailyStockPrice.stockId;
    } else {
      return null;
    }
  }

  private String getCompanyName() {
    if(companyProfile != null) {
      return companyProfile.companyName;
    } else {
      return null;
    }
  }

  private String getBusinessCategory() {
    if(companyProfile != null) {
      return companyProfile.businessCategory;
    } else {
      return null;
    }
  }

  private String getSmallBusinessCategory() {
    if(companyProfile != null) {
      return companyProfile.smallBusinessCategory;
    } else {
      return null;
    }
  }

  private String getCompanyFeature() {
    if(companyProfile != null) {
      return companyProfile.companyFeature;
    } else {
      return null;
    }
  }

  private Double getActualStockPrice() {
    if(dailyStockPrice != null) {
      return dailyStockPrice.actualStockPrice();
    } else {
      return null;
    }
  }

  private Long getMarketCap() {
    if(dailyStockPrice != null) {
      return dailyStockPrice.marketCap;
    } else {
      return null;
    }
  }

  private Long getStockNumber() {
    if(dailyStockPrice != null) {
      return dailyStockPrice.stockNumber;
    } else {
      return null;
    }
  }

  private Long getTradingVolume() {
    if(dailyStockPrice != null) {
      return dailyStockPrice.tradingVolume;
    } else {
      return null;
    }
  }

  private Double getTradingVolumeGrowthRatio() {
    if(dailyStockPrice != null) {
      return dailyStockPrice.tradingVolumeGrowthRatio();
    } else {
      return null;
    }
  }

  private Double getPredStockPrice() {
    if(predictedStockPrice != null) {
      return predictedStockPrice.predStockPrice();
    } else {
      return null;
    }
  }

  private String getUndervaluedRate() {
    if(predictedStockPrice != null) {
      if(predictedStockPrice.undervaluedRate() >= 0) {
        return String.format("+%.1f",
            predictedStockPrice.undervaluedRate() * 100);
      } else {
        return String.format("%.1f",
            predictedStockPrice.undervaluedRate() * 100);
      }
    } else {
      return null;
    }
  }

  private Double getNetPer() {
    if(joinedStockInfo != null) {
      return joinedStockInfo.per();
    } else {
      return null;
    }
  }

  private Long getSalesAmount() {
    if(joinedStockInfo != null && joinedStockInfo.corporatePerformance != null) {
      return joinedStockInfo.corporatePerformance.salesAmount;
    } else {
      return null;
    }
  }

  private Long getOperatingProfit() {
    if(joinedStockInfo != null && joinedStockInfo.corporatePerformance != null) {
      return joinedStockInfo.corporatePerformance.operatingProfit;
    } else {
      return null;
    }
  }

  private Long getOrdinaryProfit() {
    if(joinedStockInfo != null && joinedStockInfo.corporatePerformance != null) {
      return joinedStockInfo.corporatePerformance.ordinaryProfit;
    } else {
      return null;
    }
  }

  private Long getNetProfit() {
    if(joinedStockInfo != null && joinedStockInfo.corporatePerformance != null) {
      return joinedStockInfo.corporatePerformance.netProfit;
    } else {
      return null;
    }
  }

  private Long getSalesAmount1() {
    if(joinedStockInfo != null && joinedStockInfo.corporatePerformance1 != null) {
      return joinedStockInfo.corporatePerformance1.salesAmount;
    } else {
      return null;
    }
  }

  private Long getOperatingProfit1() {
    if(joinedStockInfo != null && joinedStockInfo.corporatePerformance1 != null) {
      return joinedStockInfo.corporatePerformance1.operatingProfit;
    } else {
      return null;
    }
  }

  private Long getOrdinaryProfit1() {
    if(joinedStockInfo != null && joinedStockInfo.corporatePerformance1 != null) {
      return joinedStockInfo.corporatePerformance1.ordinaryProfit;
    } else {
      return null;
    }
  }

  private Long getNetProfit1() {
    if(joinedStockInfo != null && joinedStockInfo.corporatePerformance1 != null) {
      return joinedStockInfo.corporatePerformance1.netProfit;
    } else {
      return null;
    }
  }

  private Long getSalesAmount2() {
    if(joinedStockInfo != null && joinedStockInfo.corporatePerformance2 != null) {
      return joinedStockInfo.corporatePerformance2.salesAmount;
    } else {
      return null;
    }
  }

  private Long getOperatingProfit2() {
    if(joinedStockInfo != null && joinedStockInfo.corporatePerformance2 != null) {
      return joinedStockInfo.corporatePerformance2.operatingProfit;
    } else {
      return null;
    }
  }

  private Long getOrdinaryProfit2() {
    if(joinedStockInfo != null && joinedStockInfo.corporatePerformance2 != null) {
      return joinedStockInfo.corporatePerformance2.ordinaryProfit;
    } else {
      return null;
    }
  }

  private Long getNetProfit2() {
    if(joinedStockInfo != null && joinedStockInfo.corporatePerformance2 != null) {
      return joinedStockInfo.corporatePerformance2.netProfit;
    } else {
      return null;
    }
  }

  private Double getOwnedCapitalRatioPercent() {
    if(joinedStockInfo != null && joinedStockInfo.corporatePerformance != null) {
      return joinedStockInfo.corporatePerformance.ownedCapitalRatio() * 100;
    } else {
      return null;
    }
  }

  private Double getDividendYieldPercent() {
    if(joinedStockInfo != null) {
      return joinedStockInfo.dividendYieldPercent();
    } else {
      return null;
    }
  }

  private Double getOperatingProfitRate() {
    if(joinedStockInfo != null && joinedStockInfo.corporatePerformance != null) {
      return joinedStockInfo.corporatePerformance.operatingProfitRate() * 100;
    } else {
      return null;
    }
  }

  private Long getEstimateNetProfit() {
    if(joinedStockInfo != null) {
      return joinedStockInfo.estimateNetProfit();
    } else {
      return null;
    }
  }

  private String getAnnouncementDate() {
    if(joinedStockInfo != null && joinedStockInfo.corporatePerformance != null
        && joinedStockInfo.corporatePerformance.announcementDate != null) {
      return joinedStockInfo.corporatePerformance.announcementDate.toString();
    } else {
      return null;
    }
  }

  private String getRankingNewsUrl() {
    if(rankingNews != null) {
      return rankingNews.url;
    } else {
      return null;
    }
  }

  private String getRankingNewsTitle() {
    if(rankingNews != null) {
      return "<p>" + rankingNews.title + "</p>";
    } else {
      return "";
    }
  }
  
  private String getMessage() {
    if(message == null) {
      return "";
    } else {
      return "<p>" + message + "</p>";
    }
  }

  public void printStockElements() {
    String elementHtmlTemplate =
        "<div>\n" +
        "<h3 style='background-color:#cccccc'>%s%s(%4d)</h3>\n" +
        "%s\n" +

        "<table border='1' cellspacing='0'><tbody>\n" +
        "<tr><th style='background-color:#cccccc'>業種</th>\n" +
        "<td>%s &gt; %s</td></tr>\n" +
        "<tr><th style='background-color:#cccccc'>特色</th>\n" +
        "<td>%s</td></tr>\n" +
        "<tr><th style='background-color:#cccccc'>決算</th>\n" +
        "<td>発表: %s</td></tr>\n" +
        "</tbody></table>\n" + 

        "<table border='1' cellspacing='0'><tbody>\n" +
        "<tr>\n" +
          "<th style='background-color:#cccccc'>前日株価</th>\n" +
          "<th style='background-color:#cccccc'>推定株価</th>\n" +
          "<th style='background-color:#cccccc'>割安度</th>\n" +
          "<th style='background-color:#cccccc'>PER</th>\n" +
        "</tr>\n" +
        "<tr>\n" +
          "<td>%.1f円</td>\n" +
          "<td>%.1f円</td>\n" +
          "<td>%s％</td>\n" +
          "<td>%.2f倍</td>\n" +
        "</tr>\n" +
        "<tr>\n" +
          "<th style='background-color:#cccccc'>時価総額</th>\n" +
          "<th style='background-color:#cccccc'>発行株数</th>\n" +
          "<th style='background-color:#cccccc'>前日出高</th>\n" +
          "<th style='background-color:#cccccc'>出高増率</th>\n" +
        "</tr>\n" +
        "<tr>\n" +
          "<td>%,3d百万円</td>\n" +
          "<td>%,3d株</td>\n" +
          "<td>%,3d株</td>\n" +
          "<td>%.2f倍</td>\n" +
        "</tr>\n" +

        "<tr>\n" +
          "<th style='background-color:#cccccc'>自資比率</th>\n" +
          "<th style='background-color:#cccccc'>配当利回</th>\n" +
          "<th style='background-color:#cccccc'>営業利率</th>\n" +
          "<th style='background-color:#cccccc'>予想純利</th>\n" +
        "</tr>\n" +
        "<tr>\n" +
          "<td>%.2f％</td>\n" +
          "<td>%.2f％</td>\n" +
          "<td>%.1f％</td>\n" +
          "<td>%,3d百万円</td>\n" +
        "</tr>\n" +
        "<tr>\n" +
          "<th style='background-color:#cccccc'>前期売上</th>\n" +
          "<th style='background-color:#cccccc'>前期営利</th>\n" +
          "<th style='background-color:#cccccc'>前期経利</th>\n" +
          "<th style='background-color:#cccccc'>前期純利</th>\n" +
        "</tr>\n" +
        "<tr>\n" +
          "<td>%,3d百万円</td>\n" +
          "<td>%,3d百万円</td>\n" +
          "<td>%,3d百万円</td>\n" +
          "<td>%,3d百万円</td>\n" +
        "</tr>\n" +
        "<tr>\n" +
          "<th style='background-color:#cccccc'>2前売上</th>\n" +
          "<th style='background-color:#cccccc'>2前営利</th>\n" +
          "<th style='background-color:#cccccc'>2前経利</th>\n" +
          "<th style='background-color:#cccccc'>2前純利</th>\n" +
        "</tr>\n" +
        "<tr>\n" +
          "<td>%,3d百万円</td>\n" +
          "<td>%,3d百万円</td>\n" +
          "<td>%,3d百万円</td>\n" +
          "<td>%,3d百万円</td>\n" +
        "</tr>\n" +
        "<tr>\n" +
          "<th style='background-color:#cccccc'>3前売上</th>\n" +
          "<th style='background-color:#cccccc'>3前営利</th>\n" +
          "<th style='background-color:#cccccc'>3前経利</th>\n" +
          "<th style='background-color:#cccccc'>3前純利</th>\n" +
        "</tr>\n" +
        "<tr>\n" +
          "<td>%,3d百万円</td>\n" +
          "<td>%,3d百万円</td>\n" +
          "<td>%,3d百万円</td>\n" +
          "<td>%,3d百万円</td>\n" +
        "</tr>\n" +
        "<tr>\n" +
          "<th style='background-color:#cccccc'>リンク</th>\n" +
          "<td><a href='http://stocks.finance.yahoo.co.jp/stocks/chart/?code=%4d&ct=w'>株価推移</a></td>\n" +
          "<td><a href='http://textream.yahoo.co.jp/search?query=%4d'>掲示板</a></td>\n" +
          "<td><a href='http://kabuyoho.ifis.co.jp/index.php?action=tp1&sa=report&bcode=%4d'>決算予想</a></td>\n" +
        "</tr>\n" +

        "</tbody></table>\n" + 
        "%s\n" +
        "<p><b>■この銘柄の直近ニュース</b><br>\n" +
        "%s</p>\n" +
        "</div>\n";

    String elementHtml = String.format(
        elementHtmlTemplate,
        getRank(),
        getCompanyName(),
        getStockId(),
        getMessage(),
        getBusinessCategory(),
        getSmallBusinessCategory(),
        getCompanyFeature(),
        getAnnouncementDate(),
        getActualStockPrice(),
        getPredStockPrice(),
        getUndervaluedRate(),
        getNetPer(),
        getMarketCap(),
        getStockNumber(),
        getTradingVolume(),
        getTradingVolumeGrowthRatio(),
        getOwnedCapitalRatioPercent(),
        getDividendYieldPercent(),
        getOperatingProfitRate(),
        getEstimateNetProfit(),
        getSalesAmount(),
        getOperatingProfit(),
        getOrdinaryProfit(),
        getNetProfit(),
        getSalesAmount1(),
        getOperatingProfit1(),
        getOrdinaryProfit1(),
        getNetProfit1(),
        getSalesAmount2(),
        getOperatingProfit2(),
        getOrdinaryProfit2(),
        getNetProfit2(),
        getStockId(),
        getStockId(),
        getStockId(),
        getRankingNewsTitle(),
        getNewsListHtml()
        );
    System.out.println(elementHtml);
  }

}