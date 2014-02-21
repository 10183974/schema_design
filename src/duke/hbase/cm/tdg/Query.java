package duke.hbase.cm.tdg;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class Query {
	    public static void main(String[] args) throws SQLException {
	        Statement stmt = null;
	        ResultSet rset = null;
	        Connection con = null;
	        con = DriverManager.getConnection("jdbc:phoenix:yahoo005.nicl.cs.duke.edu:2181");
	        PreparedStatement statement = con.prepareStatement("select * from customer where c_nationkey=1");
	        double ts = System.currentTimeMillis();
	        rset = statement.executeQuery();
	        double tf = System.currentTimeMillis();
	        
	        System.out.println("Time elapsed: " + (tf-ts)/1e3 + " seconds");
	        int count =0;
	        while (rset.next()) {
	            //System.out.println(rset.getString("N_NATIONKEY"));
	            count++;
	        }
	        System.out.println("Count = " + count);
	        statement.close();
	        con.close();
	        System.exit(0);
	    }
	
}
