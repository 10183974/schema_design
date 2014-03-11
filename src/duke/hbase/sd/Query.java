package duke.hbase.sd;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class Query {

  static int query_counter = 0;
  private int id;

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

  public static int getQueryId() {
    return query_counter++;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("id: " + getId() + "\n");
    sb.append("stmt: " + getQuerystr() + "\n");
    sb.append("desired latency: " + getDesired_latency() + "\n");
    sb.append("desired throughput: " + getDesired_throughput() + "\n");
    
    Set<String> stats = this.getStats().keySet();
    Iterator<String> fitr = stats.iterator();
    while(fitr.hasNext()) {
      String prop = fitr.next();
      sb.append(prop + ": " + this.getStats().get(prop) + "\n");
    }
    return sb.toString();
  }

  
  @SuppressWarnings("unchecked")
  public Query clone() {
	  Query q = new Query();
	  q.setId(Query.getQueryId());
	  q.setQuerystr(new String(this.getQuerystr()));
	  q.setType(new String(this.getType()));
	  q.setDesired_latency(this.getDesired_latency());
	  q.setDesired_throughput(this.getDesired_throughput());
	  Iterator<String> prop_itr = this.getStats().keySet().iterator();
	  HashMap<String, String> stats = new HashMap<String, String>();
	  while(prop_itr.hasNext()) {
		  String key = prop_itr.next();
		  stats.put(new String(key), new String(this.getStats().get(key)));
	  }
	  q.setStats(stats);
	  return q;
  }
  
}
