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


  public static void main(String[] args) {
	  
      //start time
      long startTime = System.currentTimeMillis(); 
      
      //welcome informations
      System.out.println("/n/n=============================================================");
      System.out.println("----Start ScanQTDGenerator-----------------------------------");
      
      //LHS.csv file 
      String lhsFile = System.getenv("PROJECT_HOME") + "/src/duke/hbase/cm/tdg/LHS.csv"; 		
      System.out.println("LHS.csv directory = " + lhsFile);
      
      String outputFile = "scanTD.txt";  
      TDWriter tdWriter = new  TDWriter(outputFile);
      
      double latency =0;
      for(int i=1; i<10;i++)
      {	     
           System.out.println("---------------------------------------------------------");
           System.out.println("Generating the " + i +"th training data ");

           //parse schema from LHS.csv
           Schema schema = new Schema();
	   schema.parseLHS("scanTD"+i,lhsFile, i);
	       
	   HbaseTableGenerator generator = new HbaseTableGenerator();
	   generator.createTable(schema);
	       	       
	   Query query = new Query();
	   String statement = "select *  from " + schema.name+"_z" + " where accbal > 9000";
	   latency =  query.measureLatency(statement);	  

           tdWriter.write(outputFile,schema,latency);
      }
      
      long endTime = System.currentTimeMillis();
      System.out.println("Total time used: " + (endTime - startTime)/1e3 + " seconds"); 
      System.out.println("============================================================");
      System.exit(0);
  }
}
