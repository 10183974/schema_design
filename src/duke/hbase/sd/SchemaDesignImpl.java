package duke.hbase.sd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
public class SchemaDesignImpl {

  private static double COST_THRESOLD = 2;
  private static int MAX_LOOP_COUNT = 10;

  public static Connection conn = null;

  public static Connection getConnection(Properties prop) {
    try {
      if (conn == null) {
        conn = DriverManager.getConnection("jdbc:phoenix:yahoo005.nicl.cs.duke.edu:2181", prop);
        ResultSet rs = conn.prepareStatement("select count(*) from nation").executeQuery();
        while (rs.next()) {
          System.out.println("row count " + rs.getInt(1));
        }
        System.out.println("Connection established successfully");
      }
    } catch (SQLException e) {
      System.err.println("Error in getting zookeeper connection");
      e.printStackTrace();
    }
    return conn;
  }

  private static double cost(ArrayList<Query> queries) {
    double max_cost = 0;
    Iterator<Query> itr = queries.iterator();

    while(itr.hasNext()) {
      Query q = (Query) itr.next();
      double cost = (q.getCurrent_estimate() - q.getDesired_latency()) / q.getDesired_latency();
      if(cost>max_cost) max_cost = cost;
    }
    return max_cost;
  }

  public static void main(String[] args) {
    // System.setProperty("user.name", "hadoop");

    Properties prop = new Properties();

    try {
      Class.forName("com.salesforce.phoenix.jdbc.PhoenixDriver");
    } catch (ClassNotFoundException e) {
      System.err.println("Error in loading the phoenix driver: " + e);
    }

    conn = getConnection(prop);

    Schema schema = new Schema();

    ArrayList<Query> queries = new ArrayList<Query>();

    ArrayList<TransformationRule> rules = new ArrayList<TransformationRule>();
    
    int loop_counter = 0;
    while (cost(queries) > COST_THRESOLD || loop_counter < MAX_LOOP_COUNT) {
      loop_counter++;
      System.out.println(loop_counter);
    }


    System.exit(0);
  }

}
