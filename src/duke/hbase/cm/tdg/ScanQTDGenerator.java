package duke.hbase.cm.tdg;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
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
  	      System.out.println("Executing query: " + query);
	      rset = statement.executeQuery();
	      long tf = System.currentTimeMillis();
          
	      latency = (tf-ts)/1e3 ;
	      System.out.println("Time elapsed: " + latency + " seconds");
	      
	      int count =0;
	      while (rset.next()) {
//	           System.out.println(rset.getString("id") + "\t " + 
//                                      rset.getString("username"));
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
    public void writeTD(Schema schema, Double latency){
        try {
			FileWriter writer = new FileWriter("scanTDG.csv",true);

			writer.append(Double.toString(latency));
                        writer.append(",");
			writer.append(Integer.toString(schema.numRows));
			writer.append(",");
			writer.append(Integer.toString(schema.numColumns));
			writer.append(",");
			writer.append(Integer.toString(schema.rowkeySize));
			writer.append(",");
			writer.append(Integer.toString(schema.columnSize));
			writer.append("\n");
			
                        writer.flush();
	                writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	  
  }
  public static void main(String[] args) {
	  
      ScanQTDGenerator tdg = new ScanQTDGenerator();  
      long startTime = System.currentTimeMillis(); 
      System.out.println("=============================================================");
      System.out.println("----Start ScanQTDGenerator-----------------------------------");
      String lhsFile = System.getenv("PROJECT_HOME") + "/src/duke/hbase/cm/tdg/LHS.csv"; 		
      System.out.println("LHS.csv directory = " + lhsFile);
      
      try{
          if (new File("scanTDG.csv").exists()){
              System.out.println("Warning scanTDG.csv exists." );
              System.exit(0);
           }
          else {
               FileWriter writer = new FileWriter("scanTDG.csv");
          }
       } catch (IOException e){
           e.printStackTrace();
      }
      for(int i=1; i<100;i++)
      {	     
          // int i = 1;
 	  // initialize a new schema 
           System.out.println("---------------------------------------------------------");
           System.out.println("Generating" + i +"th training data ");
           Schema schema = new Schema();
	  schema.initialSchema("ScanTDG"+i,lhsFile, i);
	  tdg.generate(schema);
	  double latency =  tdg.measureLatency("select *  from " + schema.name+"_z" + " where accbal > 9000");	  
          tdg.writeTD(schema, latency);
      }
      long endTime = System.currentTimeMillis();
      System.out.println("Total time used: " + (endTime - startTime)/1e3 + " seconds"); 
      System.out.println("============================================================");
      System.exit(0);
  }
}
