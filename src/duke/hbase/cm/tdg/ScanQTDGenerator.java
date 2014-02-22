package duke.hbase.cm.tdg;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Statement;


public class ScanQTDGenerator {
  
  public void generate(Schema schema) {
      HbaseTableGenerator gen = new HbaseTableGenerator();
      
      gen.setSqlFile(schema);
      gen.setXmlFile(schema);
      gen.setcsvDir(schema);
      gen.setHdfsCsvDir(schema);
      gen.createTableInHbase(schema);
  

  }
  public double measureLatency(String query){
      double latency = 0;
      try {
    	
          ResultSet rset = null;
          Connection con = null;
		  con = DriverManager.getConnection("jdbc:phoenix:yahoo005.nicl.cs.duke.edu:2181");
	      PreparedStatement statement = con.prepareStatement(query);
	      
	      long ts = System.currentTimeMillis();
              System.out.println("---------------------------------------");
  	      System.out.println("Executing query ");
	      rset = statement.executeQuery();
	      long tf = System.currentTimeMillis();
          
	      latency = (tf-ts)/1e3 ;
	      System.out.println("Time elapsed: " + latency + " seconds");
	      
	      int count =0;
	      while (rset.next()) {
	           System.out.println(rset.getString("id") + "\t " + 
                                      rset.getString("username"));
                  count++;
	      }
	      System.out.println("Count = " + count);
	      
	      statement.close();	      
              con.close();	     

	      } catch (SQLException e) {
		// TODO Auto-generated catch block
		   e.printStackTrace();	   
   	   }
       return latency;
      
     
  }

  public static void main(String[] args) {
	  
	  ScanQTDGenerator tdg = new ScanQTDGenerator();  
      long startTime = System.currentTimeMillis(); 
      
      String lhsFile = System.getenv("PROJECT_HOME") + "/src/duke/hbase/cm/tdg/LHS.csv"; 		
      System.out.println("LHS.csv directory = " + lhsFile);
	     
      int i = 1;
 	  // initialize a new schema 
      Schema schema = new Schema();
	  schema.initialSchema("schema"+i,lhsFile, i);
	  tdg.generate(schema);
	  tdg.measureLatency("select *  from " + schema.name+"_z" + " where id < 10");
	  

      long endTime = System.currentTimeMillis();
      System.out.println("Total time used: " + (endTime - startTime)/1e3 + " seconds");
      System.exit(0);
  }
}
