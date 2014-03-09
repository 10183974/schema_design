package duke.hbase.sd;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

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

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("------------------------\n");
    sb.append("id: " + getId() + "\n");
    sb.append("stmt: " + getQuerystr() + "\n");
    sb.append("desired latency: " + getDesired_latency() + "\n");
    sb.append("desired throughput: " + getDesired_throughput() + "\n");
    
    Set<String> features = getFeatures().keySet();
    Iterator<String> fitr = features.iterator();
    if(fitr.hasNext()) {
      String prop = fitr.next();
      sb.append(prop + ": " + getFeatures().get(prop) + "\n");
    }
    sb.append("------------------------\n");
    return sb.toString();

  }

}
