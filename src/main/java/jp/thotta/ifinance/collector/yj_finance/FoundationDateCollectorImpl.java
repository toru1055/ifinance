package jp.thotta.ifinance.collector.yj_finance;

import jp.thotta.ifinance.collector.CompanyProfileCollector;
import jp.thotta.ifinance.model.CompanyProfile;
import jp.thotta.ifinance.common.MyDate;

/**
 * Y!Financeランキングの設立年月日取得.
 * @author toru1055
 */
public class FoundationDateCollectorImpl extends CompanyProfileCollectorImpl {
  private static final int YJ_FINANCE_KD = 40;

  public FoundationDateCollectorImpl() {
    super(YJ_FINANCE_KD);
  }

  public void overwriteParsedProfile(CompanyProfile cp, String profile) {
    try {
      MyDate md = TextParser.parseYMD(profile);
      cp.foundationDate = md;
    } catch(Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public String getProfileAsText(CompanyProfile cp) {
    if(cp.foundationDate == null) {
      return "-";
    } else {
      return String.format("%4d/%02d/%02d",
          cp.foundationDate.year,
          cp.foundationDate.month,
          cp.foundationDate.day);
    }
  }
}
