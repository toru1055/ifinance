package jp.thotta.ifinance.utilizer;

public class Accuracy {
  int tp = 0, tn = 0, fp = 0, fn = 0;
  double sqLoss = 0.0;
  int counter = 0;

  public void addResult(double predict, double actual) {
    counter++;
    double loss = predict - actual;
    sqLoss += loss * loss;
    if(predict > 0.0) {
      if(actual > 0.0) { tp++; } else { fp++; }
    } else {
      if(actual > 0.0) { fn++; } else { tn++; }
    }
  }

  public void show() {
    double rmse = Math.sqrt(sqLoss) / counter;
    double precision = (double)tp / (tp + fp);
    double recall = (double)tp / (tp + fn);
    if(counter > 0) {
      System.out.println("tp = " + tp);
      System.out.println("fn = " + fn);
      System.out.println("fp = " + fp);
      System.out.println("tn = " + tn);
      System.out.println("precision = " + precision);
      System.out.println("recall = " + recall);
      System.out.println("rmse = " + rmse);
    }
  }
}
