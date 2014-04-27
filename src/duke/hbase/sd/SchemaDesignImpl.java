package duke.hbase.sd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;

import duke.hbase.cm.CMDProxy;

public class SchemaDesignImpl {
	
  private static double COST_THRESOLD = 2;
  private static int MAX_LOOP_COUNT = 10;
  public static Connection conn = null;
  public static CMDProxy join_cm, scan_cm, read_cm, write_cm, update_cm;
  public static BufferedWriter join_bw, scan_bw, read_bw, write_bw, update_bw;
  
  public static void initCMs() throws IOException {
	  scan_cm = new CMDProxy("cm-1.2/bin/cm.sh", "workdir/zscan_150_trainData.txt");
	  scan_bw = scan_cm.GetInputWriter();
	  
	  join_cm = new CMDProxy("cm-1.2/bin/cm.sh", "workdir/zjoin_150_trainData.txt");
	  join_bw = join_cm.GetInputWriter();
	  
	  read_cm = new CMDProxy("cm-1.2/bin/cm.sh", "workload/zread_150_trainData.txt");
	  read_bw = read_cm.GetInputWriter();
	  
	  //write_cm = new CMDProxy("cm-1.2/bin/cm.sh", "write_training_data.csv");
	  //update_cm = new CMDProxy("cm-1.2/bin/cm.sh", "update_training_data.csv");
	  
  }
  
  public static void clean() {
	  scan_cm.stop();
	  //join_cm.stop();
	  //read_cm.stop();
	  //write_cm.stop();
	  //update_cm.stop();
  }
  
  public static double cost_estimate(Application app, Query q) throws Exception{
	  double latency = -1;
	  if("read".equals(q.getType())) {
		 
		 // not supported
		 String querystr =""; 
		  
		 read_bw.write(querystr, 0, querystr.length());
	     read_bw.flush();
	        
	     String estimate = null;
	     while (estimate == null) {
	       BufferedReader br = read_cm.GetOutputReader();
	       estimate = (String) br.readLine();
	       System.out.println("read query: " + querystr);
	       System.out.println("estimated latency: " + estimate);   
	     }
	     latency = Double.parseDouble(estimate);
	  }
	  else if("scan".equals(q.getType())) {
	    String querystr = "1" + "\t" + "4" + "\t" + q.getNumRows(app)[0] + "\t" + 
	    q.getNumColumns(app)[0] + "\t" + q.getRowkeySize(app)[0] + "\t" + q.getAvgColumnSizes(app)[0] + 
	    "\t" + q.getRetNumRows(app) * q.getAvgColumnSizes(app)[0]* q.getNumColumns(app)[0] + "\n";  

	    scan_bw.write(querystr, 0, querystr.length());
        scan_bw.flush();
        
        String estimate = null;
        while (estimate == null) {
        	BufferedReader br = scan_cm.GetOutputReader();
        	estimate = (String) br.readLine();
 	       	System.out.println("scan query: " + querystr);
        	System.out.println("estimated latency: " + estimate);   
        }
        latency = Double.parseDouble(estimate);
	  }
	  else if("join".equals(q.getType())) {
		  String querystr = "1" + "\t" + "4" + "\t" + 
				  q.getNumRows(app)[0] + "\t" + q.getNumColumns(app)[0] + "\t" + 
				  q.getRowkeySize(app)[0] + "\t" + q.getAvgColumnSizes(app)[0] + "\t" +
				  q.getNumRows(app)[1] + "\t" + q.getNumColumns(app)[1] + "\t" +
				  q.getRowkeySize(app)[1] + "\t" + q.getAvgColumnSizes(app)[1] + "\t" +
				  q.getRetNumRows(app) * (
						  q.getAvgColumnSizes(app)[0]* q.getNumColumns(app)[0] 
						  + q.getAvgColumnSizes(app)[1]* q.getNumColumns(app)[1]) + "\n";  

		  join_bw.write(querystr, 0, querystr.length());
		  join_bw.flush();
		            
		  String estimate = null;
		  while (estimate == null) {
		    BufferedReader br = join_cm.GetOutputReader();
		    estimate = (String) br.readLine();
		    System.out.println("join query: " + querystr);
		    System.out.println("estimated latency: " + estimate);   
		  }
	        latency = Double.parseDouble(estimate);
	  }
	  else if("write".equals(q.getType())) {
		  BufferedWriter bw = read_cm.GetInputWriter();  
	  }
	  else if("update".equals(q.getType())) {
		  BufferedWriter bw = update_cm.GetInputWriter();  
	  }
	  

//	    String querystr =
//	        "12" + "\t" + "4" + "\t" + "10000" + "\t" + "2.86" + "\t" + "10000" +
//	 "\t" + "2.86" + "\t"
//	            + "10000" + "\n";
//	    bw.write(querystr, 0, querystr.length());
//	    bw.flush();
//
//	    String a = null;
//
//	    while (a == null) {
//	      BufferedReader br = cm.GetOutputReader();
//	      a = (String) br.readLine();
//	      System.out.println("latency " + a);
	  
	  return latency;
  }
  
  public static double penalty(Application app) throws Exception{
    double max_penalty = 0;
    ArrayList<Query> queries = app.getQueries();
    Iterator<Query> itr = queries.iterator();
 
    while (itr.hasNext()) {
      Query q = (Query) itr.next();
      double latency_estimate = cost_estimate(app, q);
      double penalty = (latency_estimate - q.getDesired_latency()) / q.getDesired_latency();
      if (penalty > max_penalty) max_penalty = penalty;
    }
    return max_penalty;
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

	try{  
	//initiaze cost models
	initCMs();
	
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
    
	}
	catch (Exception ex) {
		clean();	
		throw ex;
	}
  }

}
