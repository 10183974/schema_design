package duke.hbase.cm.tdg;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.io.*;

public class DataGenerator{
	private String xmlFile = null;
	private String sqlFile = null;
	private String csvFile = null;

	public void generateXml(ArrayList<Table> tableList){		
	    XmlBuilder builder = new XmlBuilder();
	 	builder.setOutFilePath(xmlFile);	 
	    builder.addTableToXML(tableList);
	}
	public void generateSql(ArrayList<Table> tableList){
		//build sql file
		SqlBuilder sqlBuilder = new SqlBuilder();
		sqlBuilder.setOutFile(sqlFile);
		sqlBuilder.createTableSql(tableList);
	}
	public void generateCSV(String xmlFilePath){
                try {
                          //load xml file to pdgf     
                          //ProcessBuilder pb = new ProcessBuilder(new String[]{"pdgf/mypdgfscript.sh",xmlFilePath});
                          ProcessBuilder pb = new ProcessBuilder(new String[]{
                                   "java","-jar","pdgf.jar","-load",xmlFilePath});
                          pb.directory(new File("./pdgf"));
                          pb.redirectErrorStream(true);
                          
                          Process p;
			  p = pb.start(); 
                          BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                          BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
			  
                          System.out.println("Starting pdgf...");
                          String a = null;
                          //start generating
                          String cmdStr = "start"; 
    			  bw.write(cmdStr, 0, cmdStr.length());
                          bw.newLine();
    			  bw.flush();
    			  
                          while ((a = reader.readLine()) !=null) {
                              System.out.println("pdgf: " + a);
    		          }
                          //exit automatically, since closeWhenDone is set to be true in pdgf Controller


		  } catch (IOException e) {
			e.printStackTrace();
                  } 


	}
	public void setXmlFile(String fileName){
		this.xmlFile = fileName;
	}
        public void setSqlFile(String fileName){
    	this.sqlFile = fileName;
        }
        public void setCsvFile(String fileName){
    	this.csvFile = fileName;
        }
        public static void main(String[] agrs){
                ArrayList<Table> tableList = new ArrayList<Table>();
		
		//generate table 1
		Column id = new Column("ID", " ", "INTEGER", 10, true, true);
		Column userName = new Column("UserName"," ","VARCHAR",10,false,true);		
		Column address = new Column("Address", "f","VARCHAR", 10,false,false);
		Column accBal = new Column("AccBal","f","DECIMAL",10,false,false);
		Column comment = new Column("Comment", "f","VARCHAR", 10,false,false);
		
		ArrayList<Column> rowkey = new ArrayList<Column>();
		ArrayList<Column> columns = new ArrayList<Column>();
		rowkey.add(id);
		rowkey.add(userName);
        
		columns.add(address);
		columns.add(accBal);
		columns.add(comment);
	       
		Table table1 = new Table("Z",20000,rowkey,columns) ;
		
		//generate table 2
		Column ip = new Column("IP", " ", "INTEGER", 10, true, true);
		Column message = new Column("Message", "f","VARCHAR", 10, false,false);
		
		ArrayList<Column> rowkey2 = new ArrayList<Column>();
		ArrayList<Column> columns2 = new ArrayList<Column>();
        
		rowkey2.add(ip);  
		columns2.add(message);     
		Table table2 = new Table("X",20,rowkey2,columns2) ;
		
		//tableList
		tableList.add(table1);
		tableList.add(table2);
	
		DataGenerator dg = new DataGenerator();
		dg.setSqlFile("workdir/createTable.sql");
		dg.setXmlFile("workdir/z.xml");
		dg.generateXml(tableList);
//		dg.generateSql(tableList);
    	        dg.generateCSV("../workdir/z.xml");
    	
    	//
    // HdfsCopier hdfsCopier = new HdfsCopier();
    // String localDir = "/home/hadoop/git/schema_design/src/duke/hbase/cm/tdg/pdgf/output";
    // String hdfsDir = "/tdg";
    // try {
    // hdfsCopier.copyFromLocal(localDir,hdfsDir);
    // } catch (IOException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
  
   }
} 

