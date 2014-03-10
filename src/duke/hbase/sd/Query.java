package duke.hbase.sd;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class Query {

  static int id_counter = 0;

  private int id;
  private int schema_id;

  public int getSchema_id() {
    return schema_id;
  }

  public void setSchema_id(int schema_id) {
    this.schema_id = schema_id;
  }

  private String querystr;
  private String type;
  private double desired_latency;
  private int desired_throughput;
  private HashMap<String, String> stats;

  public Query() {
  }

  public int getDesired_throughput() {
    return desired_throughput;
  }

  public void setDesired_throughput(int desired_throughput) {
    this.desired_throughput = desired_throughput;
  }

  public HashMap<String, String> getStats() {
    return stats;
  }

  public void setStats(HashMap<String, String> features) {
    this.stats = features;
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
    
    Set<String> stats = getStats().keySet();
    Iterator<String> fitr = stats.iterator();
    if(fitr.hasNext()) {
      String prop = fitr.next();
      sb.append(prop + ": " + getStats().get(prop) + "\n");
    }
    sb.append("------------------------\n");
    return sb.toString();

  }

}
