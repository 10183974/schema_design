package duke.hbase.sd;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class NeighbourSelector {

  ArrayList<Schema> neighbours = null;
  ArrayList<Method> appliedRules = null;
  ArrayList<Query> rewrittenQueries = null;

  public void doSelection(Schema schema, ArrayList<Query> queries, Method[] rules) {


  }

  public ArrayList<Query> getRewrittenQueries() {
    return rewrittenQueries;
  }

  public void setRewrittenQueries(ArrayList<Query> rewrittenQueries) {
    this.rewrittenQueries = rewrittenQueries;
  }

  public ArrayList<Schema> getNeighbours() {
    return neighbours;
  }

  public void setNeighbours(ArrayList<Schema> neighbours) {
    this.neighbours = neighbours;
  }

  public ArrayList<Method> getAppliedRules() {
    return appliedRules;
  }

  public void setAppliedRules(ArrayList<Method> appliedRules) {
    this.appliedRules = appliedRules;
  }


}
