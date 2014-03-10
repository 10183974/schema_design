package duke.hbase.cm.tdg;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class Query {
	private Connection con = null;
	private double latency = 0;
	private int retNumRows = 0;
	private int numColumns = 0;
	private int retColumnSize = 0;	
	public Query(){
		 try {
				con = DriverManager.getConnection("jdbc:phoenix:yahoo005.nicl.cs.duke.edu:2181");
			  } catch (SQLException e) {
				e.printStackTrace();
			  }
	}
	public void execute(String queryStr, int n){	      	
	    try {
	        for(int i=0;i<n;i++){
		    	this.execute(queryStr);	
		    }
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	this.latency = this.latency/n;
    	this.retNumRows = this.retNumRows/n;
    	this.retColumnSize = this.retColumnSize/n;
    	  
    	System.out.println("----------------------------------------------------------------------");
	    System.out.println("Executing query [" + queryStr + "] for " + n + " times");
	    System.out.println("Average latency:  " + this.latency);
	    System.out.println("Average number of rows returned: " + this.retNumRows);
	    System.out.println("Number of columns: " + this.numColumns);
	    System.out.println("Average returned column size: " + this.retColumnSize);
	}	
	private void execute(String queryStr){
		try {   
			  long ts = System.currentTimeMillis();
			  
			  ResultSet rs = null;
		      PreparedStatement statement = this.con.prepareStatement(queryStr);	      
		      rs = statement.executeQuery();
		      //total number of columns 
		      ResultSetMetaData rsmd = rs.getMetaData();
		      int numColumns =rsmd.getColumnCount();
		      
		      int rowCount =0;
		      int totalColumnSize = 0;
			  while (rs.next()) {
			     rowCount++;
			     for(int i=1;i<=numColumns;i++){
			    	 String s = rs.getString(i);			    	 
			    	 
			    	 if( s != null){
//				    	 System.out.print(s + " (" + s.length() + ") | " );
				    	 totalColumnSize +=  s.length();			    		 
			    	 }	 

			     }
//			     System.out.println("");
			   }
		      statement.close();
		      long tf = System.currentTimeMillis();
			  
		      this.latency +=  (tf-ts);
		      this.retNumRows +=  rowCount;	
		      this.numColumns = numColumns;
		      if (rowCount !=0){
		    	     this.retColumnSize += totalColumnSize/rowCount;
		      }
		 
//		      System.out.println("-----------------------------------");
//			  System.out.println("Executing query [" + queryStr + "]");
//			  System.out.println("Latency:  " + (tf-ts));
//			  System.out.println("Number of rows: " + rowCount);
//			  System.out.println("Number of columns: " + numColumns);
//			  System.out.println("Total column size: " + totalColumnSize);
		      
		      } catch (SQLException e) {
			   e.printStackTrace();	   
	   	   }	
	}
	public double getLatency(){
		return this.latency;
	}
	public int getRetNumRows(){    
	    return this.retNumRows;
	}
	public int getRetColumnSize(){
		return this.retColumnSize;
	}

	
}
