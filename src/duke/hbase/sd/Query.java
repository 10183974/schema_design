package duke.hbase.sd;

public class Query {

  int id;
  String querystr;
  double desired_latency;
  double current_estimate;

  public Query(int id, String querystr, double desired_latency, double current_estimate) {
    super();
    this.id = id;
    this.querystr = querystr;
    this.desired_latency = desired_latency;
    this.current_estimate = current_estimate;
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

  public double getCurrent_estimate() {
    return current_estimate;
  }

  public void setCurrent_estimate(double current_estimate) {
    this.current_estimate = current_estimate;
  }

}
