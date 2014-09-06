/*
This test program takes the property and workload from the properties.xml file, prepare the 
JDBC prepared statement accordingly, build connection to server and execute the prepared queries
*/

import java.io.*;
import java.sql.*;
import java.util.*;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Test {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Connection conn;
		PreparedStatement st;
		PreparedStatement createSt;
		PreparedStatement insertSt;
		Properties prop = new Properties();
		Properties prop2 = new Properties();
		//read workload from properties.xml, directory locations as shown
		try{
	    FileInputStream fis = new FileInputStream("/home/meizhen/workspace/phoenixTest/src/properties.xml");
	    prop2.loadFromXML(fis);
		}
		catch (FileNotFoundException e){
			System.out.println("Error in reading from properties.xml "+e);
		}
		catch(IOException e){
			System.out.println("Error in the format of properties.xml "+e);
		}
		
		//build JDCB connection between YCSB client and HBase server
		try{
		 Class.forName("com.salesforce.phoenix.jdbc.PhoenixDriver");
		 conn = DriverManager.getConnection("jdbc:phoenix:yahoo029.nicl.cs.duke.edu,yahoo030.nicl.cs.duke.edu,yahoo031.nicl.cs.duke.edu:2181",prop);
		 System.out.println("Finish connection");
		 //String stmt="SELECT * FROM STOCK_SYMBOL WHERE SYMBOL = ?";
		 
		 //prepare statement accordingly
		 String create=prop2.getProperty("create");
		 String insert=prop2.getProperty("insert");
		 String stmt=prop2.getProperty("read");
		 createSt=conn.prepareStatement(create);
		 insertSt=conn.prepareStatement(insert);
		 st=conn.prepareStatement(stmt);
		 st.setString(1,"key1");
		 insertSt.setString(1,"key1");
		 insertSt.setString(2,"field1");
		 System.out.println("Finish preparing statement");
		 
		 // execute prepared statements
		 createSt.executeUpdate();
		 insertSt.executeUpdate();
		 conn.commit();
		 ResultSet resultSet = st.executeQuery();
		 System.out.println("Finish excuting statement");
		 while (resultSet.next()){
		 String rs=resultSet.getString(2);
		 System.out.println("COMPANY "+rs);
		 }
		 st.close();
		 conn.close();
		}
		 catch (ClassNotFoundException e){
			  System.err.println("Error in initializing the JDBS driver: " + e);
		 }
		 catch (SQLException e){
			System.err.println("Error in database operation: " + e);
		}	

	}
}
