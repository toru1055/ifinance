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
 * モデルの抽象クラス実装.
 * @author toru1055
 */
public abstract class AbstractStockModel implements DBModel {
  public int stockId;

  public String getJoinKey() {
    return String.format("%4d", stockId);
  }

  /**
   * このインスタンスを検索するSQLを取得.
   * @return PrimaryKeyを指定して1レコードselectするSQL
   */
  abstract protected String getFindSql();

  /**
   * このインスタンスにDBの結果をセットする.
   */
  abstract protected void setResultSet(ResultSet rs) 
    throws SQLException, ParseException;

  public boolean exists(Statement st) throws SQLException {
    String sql = this.getFindSql();
    ResultSet rs = st.executeQuery(sql);
    return rs.next();
  }

  public void readDb(Statement st) throws SQLException, ParseException {
    String sql = this.getFindSql();
    ResultSet rs = st.executeQuery(sql);
    if(rs.next()) {
      this.setResultSet(rs);
    }
  }

  @Override
  public boolean equals(Object o) {
    return o.toString().equals(this.toString());
  }
}
