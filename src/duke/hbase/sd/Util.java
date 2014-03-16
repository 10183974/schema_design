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
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Util {

  private static final String CHAR_LIST = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

  public static String getRandomString() {
    StringBuffer randStr = new StringBuffer();
    for (int i = 0; i < 3; i++) {
      int number = getRandomNumber();
      char ch = CHAR_LIST.charAt(number);
      randStr.append(ch);
    }
    return randStr.toString();
  }

  public static int getRandomNumber() {
    int randomInt = 0;
    Random randomGenerator = new Random();
    randomInt = randomGenerator.nextInt(CHAR_LIST.length());
    if (randomInt - 1 == -1) {
      return randomInt;
    } else {
      return randomInt - 1;
    }
  }

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

  public static Application initApplication(String[] files) throws Exception {
	
	// argument checking  
    if(files==null) {
		throw new Exception("Error initApplication: null arguement");
	}
	if(files.length<2) {
		throw new Exception("Error initApplication: either schema.xml or workload.xml is missing");
	}

    Application app = new Application();
	
	String schema_xml = files[0];
	String query_workload_xml = files[1];
	
    System.out.println("Parsing " + schema_xml + " ...");
	initSchema(app, schema_xml); 
    System.out.println("Parsing " + query_workload_xml + " ...");
	initQueryWorkload(app, query_workload_xml);
	return app;
  }
	
  private static void initSchema(Application app, String schema_xml) throws Exception{

	app.setId(Application.getApplicationId());
	
	HashMap<String, Table> tables = new HashMap<String, Table>();
    HashMap<String, Relation> relations = new HashMap<String, Relation>();

    try {
      File f = new File(schema_xml);
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse(f);

      // parse table
      NodeList tableList = doc.getElementsByTagName("table");
      System.out.println("table count " + tableList.getLength());

      for (int i = 0; i < tableList.getLength(); i++) {

        Table t = new Table();
        t.setId(Table.getTableId());
        
        HashMap<String, Column> cols = new HashMap<String, Column>();

        Node table = tableList.item(i);
        NodeList tname = ((Element) table).getElementsByTagName("name");
        if (tname != null && tname.getLength() == 1) {
          //System.out.println("table name ->" + tname.item(0).getTextContent());
          t.setName(tname.item(0).getTextContent());
        } else {
          throw new Exception("Error: table name not found");
        }

        NodeList columnList = ((Element) table).getElementsByTagName("column");
        System.out.println("column count " + columnList.getLength());

        for (int j = 0; j < columnList.getLength(); j++) {
          Column col = new Column();
          col.setId(Column.getColumnId());
          String type = ((Element) columnList.item(j)).getAttribute("type");
          String name = columnList.item(j).getTextContent();
          int size = Integer.parseInt(((Element) columnList.item(j)).getAttribute("size"));
          int keysize = columnList.item(j).getTextContent().length();
          
          col.setKey(name);
          col.setType(type);
          col.setAverage_key_size(keysize);
          col.setAverage_value_size(size);
          cols.put(col.getFamily()+col.getKey(), col);
        }
        t.setColumns(cols);

        NodeList primaryKeyList = ((Element) table).getElementsByTagName("primarykey");
        String[] pks = null;
        if (primaryKeyList != null && primaryKeyList.getLength() == 1) {
          pks = primaryKeyList.item(0).getTextContent().split("\\s*,\\s*");
        }
        System.out.println("primary key count: " + pks.length);

        ArrayList<Column> rowkey = new ArrayList<Column>();
        for (String pk : pks) {
        	rowkey.add(t.getColumns().get(Column.DEFAULT_FAMILY+pk));
        }
        t.setRowkey(rowkey);
        
        NodeList rowcount = ((Element) table).getElementsByTagName("rowcount");
        if(rowcount!=null && rowcount.getLength()==1) {
          t.setRowcount(Integer.parseInt(((Element) rowcount.item(0)).getTextContent()));
        }

        tables.put(t.getName(), t);
      }
      app.setTables(tables);
      
      NodeList relationList = doc.getElementsByTagName("relationship");
      System.out.println("Relations count: " + relationList.getLength());

      for (int j = 0; j < relationList.getLength(); j++) {

        Relation rel = new Relation();
        rel.setId(Relation.getRelationId());
        
        Element relation = (Element) relationList.item(j);
        NodeList cardinality = relation.getElementsByTagName("cardinality");

        String table1 = "", table2 = "", card = "";
        if (cardinality != null && cardinality.getLength() == 1) {
          table1 = ((Element) cardinality.item(0)).getAttribute("table1");
          table2 = ((Element) cardinality.item(0)).getAttribute("table2");
          card = ((Element) cardinality.item(0)).getTextContent();
        }
        
        String[] table1_joinkey_l =
            ((Element) relation.getElementsByTagName("joinkey").item(0)).getTextContent().split(
              "\\s*,\\s*");
        String[] table2_joinkey_l =
            ((Element) relation.getElementsByTagName("joinkey").item(1)).getTextContent().split(
              "\\s*,\\s*");

        ArrayList<Column> t1_joinkeys = new ArrayList<Column>();
        for (String joinkey : table1_joinkey_l) {
          t1_joinkeys.add(app.getTables().get(table1).getColumns()
              .get(Column.DEFAULT_FAMILY + joinkey));
        }

        ArrayList<Column> t2_joinkeys = new ArrayList<Column>();
        for (String joinkey : table2_joinkey_l) {
          t2_joinkeys.add(app.getTables().get(table2).getColumns()
              .get(Column.DEFAULT_FAMILY + joinkey));
        }

        rel.setT1(app.getTables().get(table1));
        rel.setT2(app.getTables().get(table2));
        rel.setCardinality(card);
        rel.setT1_jkey(t1_joinkeys);
        rel.setT2_jkey(t2_joinkeys);

        relations.put(table1 + table2 + table1_joinkey_l + table2_joinkey_l, rel);
      }

      app.setRels(relations);
      app.setTables(tables);
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void initQueryWorkload(Application app, String query_workload_xml) {
    
    ArrayList<Query> queries = new ArrayList<Query>();

    try {
      File f = new File(query_workload_xml);
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse(f);

      NodeList queryList = doc.getElementsByTagName("query");
      System.out.println("Query count: " + queryList.getLength());
      
      if (queryList != null) {
        for (int i = 0; i < queryList.getLength(); i++) {
          Query q = new Query();
          q.setId(Query.getQueryId());
          
          Node query = queryList.item(i);
          if (query != null) {
            String stmt =
                ((Element) ((Element) query).getElementsByTagName("stmt").item(0)).getTextContent();
            String type =
                ((Element) ((Element) query).getElementsByTagName("type").item(0)).getTextContent();
            String desired_throughput =
                ((Element) ((Element) query).getElementsByTagName("desired_throughput").item(0))
                    .getTextContent();
            String desired_latency =
                ((Element) ((Element) query).getElementsByTagName("desired_latency").item(0))
                    .getTextContent();

            q.setQuerystr(stmt);
            q.setType(type);
            q.setDesired_latency(Double.parseDouble(desired_latency));
            q.setDesired_throughput(Integer.parseInt(desired_throughput));

            NodeList stats = ((Element) query).getElementsByTagName("stat");

            HashMap<String, String> s = new HashMap<String, String>();
            if (stats != null) {
              for (int j = 0; j < stats.getLength(); j++) {
                Element property = (Element) stats.item(j);
                String key = property.getAttribute("type");
                String value = property.getTextContent();

                s.put(key, value);
              }
            }
            q.setStats(s);

          }
          queries.add(q);
        }

      }

    } catch (Exception e) {

    }
    app.setQueries(queries);
  }

public static Application getRandomApplication(Application app_ini) {
	// TODO Auto-generated method stub
	return null;
}
}