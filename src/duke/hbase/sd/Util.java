package duke.hbase.sd;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class Util {

  public static BufferedWriter getFileWriter(String filename) {
    BufferedWriter bw = null;
    File file = new File(filename);

    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException e) {
        System.out.println("Error in creating new file " + filename);
        e.printStackTrace();
      }
    }

    FileWriter fw;
    try {
      fw = new FileWriter(file.getAbsoluteFile());
      bw = new BufferedWriter(fw);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return bw;
  }

  public static void closeFileWriter(BufferedWriter bw) {
    try {
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static Connection getConnection(Properties prop) {
    Connection conn = null;
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
}
