package duke.hbase.sd;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.ArrayList;

public class SchemaDesignImpl {

  private static double COST_THRESOLD = 2;
  private static int MAX_LOOP_COUNT = 10;
  public static Connection conn = null;

  private static double cost(ArrayList<Query> queries) {
    double max_cost = 0;
    // Iterator<Query> itr = queries.iterator();
    //
    // while (itr.hasNext()) {
    // Query q = (Query) itr.next();
    // double cost = (q.getCurrent_estimate() - q.getDesired_latency()) / q.getDesired_latency();
    // if (cost > max_cost) max_cost = cost;
    // }
    return max_cost;
  }

  public static void main(String[] args) throws Exception {
    // try {
    // Class.forName("com.salesforce.phoenix.jdbc.PhoenixDriver");
    // } catch (ClassNotFoundException e) {
    // System.err.println("Error in loading the phoenix driver: " + e);
    // } HashMap<String, Relation> relations = new HashMap<String,
    // Relation>();
    // conn = Util.getConnection(prop);

    // initialize schema from xml

    Schema schema = Util.initSchema("workdir/schema.xml");
    // System.out.println(schema.toString());
    ArrayList<Query> queries = Util.initQueryWorkload("workdir/workload.xml");
    // Iterator<Query> qitr = queries.iterator();
    // while (qitr.hasNext()) {
    // System.out.println(qitr.next());
    // }
    Method[] transformationRules = Util.getTransformationMethods();
    
    NeighbourSelector ns = new NeighbourSelector();
    int loop_counter = 0;
    while (cost(queries) > COST_THRESOLD || loop_counter < MAX_LOOP_COUNT) {
      loop_counter++;
      
      ArrayList<Schema> schemas = ns.getNeighbours();
      // assume it returns only one schema
      // schema = schemas.get(0);
      // queries = ns.getRewrittenQueries();

    }
    System.exit(0);
  }

}
