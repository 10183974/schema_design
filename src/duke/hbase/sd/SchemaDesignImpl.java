package duke.hbase.sd;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;

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

  public void printQueries(ArrayList<Query> queries) {
	  Iterator<Query> qitr = queries.iterator();
	  while(qitr.hasNext()) {
	    System.out.println(qitr.next());
	  }
  }
  
  public static void main(String[] args) throws Exception {
    // try {
    // Class.forName("com.salesforce.phoenix.jdbc.PhoenixDriver");
    // } catch (ClassNotFoundException e) {
    // System.err.println("Error in loading the phoenix driver: " + e);
    // }

    Application app_ini = Util.initApplication(new String[] {"workdir/schema.xml", "workdir/workload.xml"});
    TransformationMethods tr_methods = new TransformationMethods();   
    TRSelector tr_selector = new TRSelector();
    int loop_counter = 0;
    Application app = Util.getRandomApplication(app_ini);
    
    while (cost(app_ini.getQueries()) > COST_THRESOLD || loop_counter < MAX_LOOP_COUNT) {
      Transformation tr = tr_selector.select(app);	
      app = (Application) tr.getTransformationRule().invoke(tr_methods, tr.getArguments());
      loop_counter++;
    }
    System.exit(0);
  }

}
