package duke.hbase.sd;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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

  public static Method[] getTransformationMethods() {
    Class c = new TransformationMethods().getClass();
    Method[] m = c.getDeclaredMethods();
    return m;
  }

  public static Schema initSchema(String filename) {

    System.out.println("Parsing " + filename + " ...");

    Schema schema = null;
    try {

      ArrayList<Table> tables = new ArrayList<Table>();
      File f = new File(filename);
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse(f);

      NodeList tableList = doc.getElementsByTagName("table");
      for (int i = 0; i < tableList.getLength(); i++) {
        Table t = new Table();
        Node table = tableList.item(i);
        t.setName(((Element) table).getElementsByTagName("name").item(0).getTextContent());

        String[] pks =
            ((Element) table).getElementsByTagName("primarykey").item(0).getTextContent()
                .split("\\s*,\\s*");
        ArrayList<Column> rowkeys = new ArrayList<Column>();
        for (String pk : pks) {
          rowkeys.add(new Column(pk));
        }
        t.setRowkey(rowkeys);

        ArrayList<Column> cols = new ArrayList<Column>();

        NodeList columns = ((Element)((Element) table).getElementsByTagName("columns").item(0)).getElementsByTagName("column");
        for (int j = 0; j < columns.getLength(); j++) {
          cols.add(new Column(columns.item(j).getTextContent(), ((Element) columns.item(j))
              .getAttribute("type")));
        }

        NodeList relationList = doc.getElementsByTagName("relationship");



      }
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return schema;
  }
}
