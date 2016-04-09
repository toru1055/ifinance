package jp.thotta.ifinance.adhoc;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.sql.Connection;

import jp.thotta.oml.client.OmlClient;
import jp.thotta.oml.client.io.Label;

public abstract class BaseStockScoreEstimator {

  public String host = "localhost";
  public String labelMode = "score";
  public String parserType = "ma";
  public int modelId = 0;

  Connection c;
  Map<String, Double> responseVariables;
  Map<String, String> predictorVariables;
  Map<String, Double> predictedScores;
  OmlClient tr_client;
  OmlClient pr_client;

  public BaseStockScoreEstimator(Connection c, int modelId)
    throws Exception {
    this.c = c;
    this.modelId = modelId;
    setResponseVariables();
    setPredictorVariables();
  }

  public static void centering(Map<String, Double> m) {
    double mean = 0.0;
    for(String k : m.keySet()) {
      mean += m.get(k) / m.size();
    }
    for(String k : m.keySet()) {
      m.put(k, m.get(k) - mean);
    }
  }

  public Set<String> keySet() {
    return predictedScores.keySet();
  }

  public Double getPredicted(String k) {
    return predictedScores.get(k);
  }

  public Double getActual(String k) {
    return responseVariables.get(k);
  }


  public void execTraining() {
    try {
      tr_client = OmlClient.createTrainBatchConnection(host);
      if(tr_client.configure(modelId, parserType, labelMode)) {
        for(String k : responseVariables.keySet()) {
          if(predictorVariables.containsKey(k)) {
            String feature = predictorVariables.get(k);
            Double score = responseVariables.get(k);
            tr_client.train(String.valueOf(score), feature);
          }
        }
      }
    } catch(Exception e) {
      e.printStackTrace();
      System.exit(1);
    } finally {
      try {
        tr_client.close();
      } catch(Exception e) {
        e.printStackTrace();
        System.exit(1);
      }
    }
  }

  public void execPrediction() {
    try {
      predictedScores = new HashMap<String, Double>();
      pr_client = OmlClient.createPredictBatchConnection(host);
      if(pr_client.configure(modelId, parserType, labelMode)) {
        for(String k : responseVariables.keySet()) {
          if(predictorVariables.containsKey(k) && isPrediction(k)) {
            String feature = predictorVariables.get(k);
            Double score = responseVariables.get(k);
            Label label = pr_client.predictLabel(feature);
            predictedScores.put(k, label.getScore());
          }
        }
      }
    } catch(Exception e) {
      e.printStackTrace();
      System.exit(1);
    } finally {
      try {
        pr_client.close();
      } catch(Exception e) {
        e.printStackTrace();
        System.exit(1);
      }
    }
  }

  protected boolean isPrediction(String k) {
    return true;
  }

  abstract protected void setResponseVariables() throws Exception;
  abstract protected void setPredictorVariables() throws Exception;

}
