package duke.hbase.cm.tdg;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class Query {
	private Connection con = null;
	public Query(){
		  try {
			con = DriverManager.getConnection("jdbc:phoenix:yahoo005.nicl.cs.duke.edu:2181");
		  } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		  }		
	}
	  public double measureLatency(String query){
	      double latency = 0;
	      try {    	
	          ResultSet rset = null;	          
		      PreparedStatement statement = this.con.prepareStatement(query);
		      
		      long ts = System.currentTimeMillis();
	          System.out.println("---------------------------------------");
	  	      System.out.println("Executing query: " + query);
		      rset = statement.executeQuery();
		      long tf = System.currentTimeMillis();
	          
		      latency = (tf-ts)/1e3 ;
		      System.out.println("Time elapsed: " + latency + " seconds");
		      
		      int count =0;
		      while (rset.next()) {
	                  count++;
		      }
		      System.out.println("Count = " + count);
		      
		      statement.close();	      
	          this.con.close();	     

		      } catch (SQLException e) {
			   e.printStackTrace();	   
	   	   }
	       return latency;
	           
	   }
	    public static void main(String[] args) throws SQLException {

	    }
	
}
