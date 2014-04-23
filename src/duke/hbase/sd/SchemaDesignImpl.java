package duke.hbase.sd;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;

public class SchemaDesignImpl {

  private static double COST_THRESOLD = 0;
  private static int MAX_LOOP_COUNT = 10;
  public static Connection conn = null;

  public static double penalty(Application app) {
    double max_cost = 1;
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
    Class<?> tm = Class.forName("duke.hbase.sd.TransformationMethods");
    TRSelector tr_selector = new TRSelector();
    int loop_counter = 0;
    Application app = Util.getRandomApplication(app_ini);
    
    double penalty = penalty(app);
    
    while (penalty > COST_THRESOLD && loop_counter < MAX_LOOP_COUNT) {
      Transformation tr = tr_selector.select(app);	
      
      if(tr!=null) {
    	  System.out.println("No transformation rule is selected");
    	  break;
      }
      
      Method tr_method = tr.getTransformationRule();
      ArrayList<Object> tr_args = tr.getArguments();

      if ("join".equals(tr.getType()) || "nesting".equals(tr.getType())) {
        app =
            (Application) tr_method.invoke(tm.newInstance(), app, tr.getQ(), tr_args.get(0),
              tr_args.get(1), tr_args.get(2), tr_args.get(3));
      }
      
      loop_counter++;
    }
    
    System.out.println("printing application after iterations ends");
    System.out.println("penalty score of below app " + penalty );
    System.out.println(app.toShortString());
    
    System.exit(0);
  }

}
