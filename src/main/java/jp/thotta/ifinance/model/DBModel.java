package jp.thotta.ifinance.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;

/**
 * モデルクラスのInterface.
 *
 * @author toru1055
 */
public interface DBModel {
  /**
   * データが充分揃っているかをチェック.
   * @return データが充分揃っているか
   */
  public boolean hasEnough();

  /**
   * Map用のキー取得.
   * @return キーになる文字列
   */
  public String getKeyString();

  /**
   * Join用のキー取得.
   * @return Join用のキー
   */
  public String getJoinKey();

  /**
   * 同じキーのレコードがDB内に存在するかをチェック.
   * @param st SQL実行オブジェクト
   * @return 存在するか否か
   */
  public boolean exists(Statement st) throws SQLException;

  /**
   * 同じキーのレコードをDBから取得し、インスタンスを上書き.
   * @param st SQL実行オブジェクト
   */
  public void readDb(Statement st) throws SQLException, ParseException;

  /**
   * このインスタンスをDBにインサート.
   * @param st SQL実行オブジェクト
   */
  public void insert(Statement st) throws SQLException;

  /**
   * このインスタンスでDBを更新.
   * @param st SQL実行オブジェクト
   */
  public void update(Statement st) throws SQLException;
}
