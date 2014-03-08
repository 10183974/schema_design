package duke.hbase.sd;

import java.util.HashMap;

public class Query {

  static int id_counter = 0;

  private int id;
  private String querystr;
  private String type;
  private double desired_latency;
  private int desired_throughput;
  private HashMap<String, String> features;

  public Query() {
  }

  public int getDesired_throughput() {
    return desired_throughput;
  }

  public void setDesired_throughput(int desired_throughput) {
    this.desired_throughput = desired_throughput;
  }

  public HashMap<String, String> getFeatures() {
    return features;
  }

  public void setFeatures(HashMap<String, String> features) {
    this.features = features;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getQuerystr() {
    return querystr;
  }

  public void setQuerystr(String querystr) {
    this.querystr = querystr;
  }

  public double getDesired_latency() {
    return desired_latency;
  }

  public void setDesired_latency(double desired_latency) {
    this.desired_latency = desired_latency;
  }

  public int getNextId() {
    return id_counter++;
  }

}
