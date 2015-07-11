package jp.thotta.ifinance.collector;

import jp.thotta.ifinance.model.CompanyProfile;
import java.util.Map;
import java.io.IOException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 企業固有情報を取得するInterface.
 * @author toru1055
 */
public interface CompanyProfileCollector {
  /**
   * 企業情報Mapに情報を追加.
   * @param profiles 追加するmap
   */
  public void append(Map<String, CompanyProfile> profiles) throws IOException;

  /**
   * DBの企業情報テーブルに取得結果を追加.
   */
  public void appendDb(Connection conn) throws SQLException, IOException;
}
