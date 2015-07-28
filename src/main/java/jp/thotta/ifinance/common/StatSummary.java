package jp.thotta.ifinance.common;

import java.util.Arrays;
import java.util.List;

/**
 * 統計情報のサマリ.
 * @author toru1055
 */
public class StatSummary {
  public double[] data;

  public StatSummary(double[] d) {
    this.data = Arrays.copyOf(d, d.length);
    Arrays.sort(this.data);
  }

  public StatSummary(List<Double> dl) {
    this.data = new double[dl.size()];
    int i = 0;
    for(Double d : dl) {
      this.data[i++] = d;
    }
    Arrays.sort(this.data);
  }

  public double min() {
    return data[0];
  }

  public double max() {
    return data[data.length - 1];
  }
  
  public double mean() {
    double mean = 0.0;
    for(int i = 0; i < data.length; i++) {
      mean += data[i] / data.length;
    }
    return mean;
  }

  public double median() {
    return percentile(50);
  }

  /**
   * パーセンタイル計算.
   * p=25の時、25percentileの値を返す.
   * @param p the percentile value to compute. 0~100
   */
  public double percentile(double p) {
    int p_index = (int)((p / 100) * (data.length - 1));
    return data[p_index];
  }

  @Override
  public String toString() {
    return String.format(
        "min[%.2f], 1st Qu.[%.2f], median[%.2f], " +
        "mean[%.2f], 3rd Qu.[%.2f], max[%.2f]",
        min(), percentile(25), percentile(50),
        mean(), percentile(75), max());
  }
}
