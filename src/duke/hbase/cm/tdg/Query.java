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
	public void execute(String queryStr, boolean isUpdate, int n){	
	   	System.out.println("----------------------------------------------------------------------");
	    System.out.println("Executing query [ " + queryStr + " ] for " + n + " times");
	    try {
	        for(int i=0;i<n;i++){
		    	this.execute(queryStr, isUpdate);	
		    }
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	this.latency = this.latency/n;
    	this.retNumRows = this.retNumRows/n;
    	this.retColumnSize = this.retColumnSize/n;
    	  
	    System.out.println("Average latency:  " + this.latency);
	    System.out.println("Average number of rows returned: " + this.retNumRows);
	    System.out.println("Number of columns: " + this.numColumns);
	    System.out.println("Average returned column size: " + this.retColumnSize);
	}	
	private void execute(String queryStr, boolean isUpdate){
		try {   		 			  
			  ResultSet rs = null;
		      PreparedStatement statement = this.con.prepareStatement(queryStr);
		      long ts = 0;
		      long tf = 0;

		      if (isUpdate){
			      ts = System.currentTimeMillis();
		    	  int a = statement.executeUpdate();
		    	  con.commit();
		    	  tf = System.currentTimeMillis();	
		    	  System.out.println("Update status : " + a );
		    	  System.out.println("Affected rows " + statement.getUpdateCount());
		    	  this.latency +=  (tf-ts);
		    	  statement.close();
		      }
		      else {
		    	  ts = System.currentTimeMillis();
		    	  rs = statement.executeQuery();		      
			      //number of columns 
			      ResultSetMetaData rsmd = rs.getMetaData();
			      int numColumns =rsmd.getColumnCount();
			      
			      int rowCount =0;
			      int totalColumnSize = 0;
				  while (rs.next()) {
				     rowCount++;
				     for(int i=1;i<=numColumns;i++){
				    	 String s = rs.getString(i);			    	 
				    	 if( s != null){
					    	 totalColumnSize +=  s.length();			    		 
				    	 }	 
				     }
				   }
			      statement.close();			      
			      tf = System.currentTimeMillis();				      
			      this.latency +=  (tf-ts);
			      this.retNumRows +=  rowCount;	
			      this.numColumns = numColumns;
			      if (rowCount !=0){
			    	     this.retColumnSize += totalColumnSize/rowCount;
			      }	
			      System.out.println("----------------------------------");
				  System.out.println("Average latency:  " + (tf-ts));
				  System.out.println("Average number of rows returned: " + rowCount);
				  System.out.println("Number of columns: " + numColumns);
				  System.out.println("Average returned column size: " + retColumnSize);
		      }
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
