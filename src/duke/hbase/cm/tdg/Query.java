package duke.hbase.cm.tdg;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;


public class Query {
	
	private Connection con = null;
	private double latency = 0;
	private int numRetRows = 0;
	private int aveRetColumnSize = 0;
	
	public Query(){
		 try {
				con = DriverManager.getConnection("jdbc:phoenix:yahoo005.nicl.cs.duke.edu:2181");
			  } catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			  }
	}
	public void execute(String queryStr){	      
	    try {   
	          ResultSet rset = null;
		      PreparedStatement statement = this.con.prepareStatement(queryStr);	      
		      long ts = System.currentTimeMillis();
		      rset = statement.executeQuery();
		      long tf = System.currentTimeMillis();
		      latency = (tf-ts);
		      //number of rows returned 
		      int rCount =0;
			  while (rset.next()) {
			     rCount++;
			   }
			  this.numRetRows = rCount;
			  
		      statement.close();	      
	          this.con.close();	     
		      } catch (SQLException e) {
			   e.printStackTrace();	   
	   	   }
	    System.out.println("Executing query: " + queryStr);
	    System.out.println("Number of rows returned: " + numRetRows);
	    System.out.println("Latency:  " + latency);
	 }	
	public double getLatency(){
		return this.latency;
	}
	
	public int getNumRetRows(){    
	    return this.numRetRows;
	}
	public static void main(String[] args) throws SQLException {
    }
	
}
