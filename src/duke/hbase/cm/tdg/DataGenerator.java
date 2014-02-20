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
	public void generateCSV(){
                try {
                          //load xml file to pdgf     
                          //ProcessBuilder pb = new ProcessBuilder(new String[]{"pdgf/mypdgfscript.sh",xmlFilePath});
                          String relativeXmlFilePath = "../" + xmlFile;
                          System.out.println("Starting pdgf...");
                        
                          System.out.println("Loading xml file " + relativeXmlFilePath + " to pdgf");
                          ProcessBuilder pb = new ProcessBuilder(new String[]{
                                   "java","-jar","pdgf.jar","-load",relativeXmlFilePath});
                          pb.directory(new File("./pdgf"));
                          pb.redirectErrorStream(true);
                          
                          Process p;
			  p = pb.start(); 
                          BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                          BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
			  
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
                          System.out.println("Exiting pdgf");

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
	       
		Table table1 = new Table("Z",100,rowkey,columns) ;
		
		//generate table 2
		Column ip = new Column("IP", " ", "INTEGER", 10, true, true);
		Column message = new Column("Message", "f","VARCHAR", 10, false,false);
		
		ArrayList<Column> rowkey2 = new ArrayList<Column>();
		ArrayList<Column> columns2 = new ArrayList<Column>();
        
		rowkey2.add(ip);  
		columns2.add(message);     
		Table table2 = new Table("X",20,rowkey2,columns2) ;
		
		//add table to tableList
		tableList.add(table1);
		tableList.add(table2);
	       


                long startTime = System.currentTimeMillis(); 
                //start data generation and loading
		DataGenerator dg = new DataGenerator();
                //create sql file
		dg.setSqlFile("workdir/createTable.sql");
	        dg.generateSql(tableList);
                //create xml file
          	dg.setXmlFile("./workdir/z.xml");
		dg.generateXml(tableList);
    	        //generate data, write to csv file
                dg.generateCSV();
    	        
                //copy data from local to hdfs
                HdfsCopier hdfsCopier = new HdfsCopier();
                String localDir = "./workdir/csvdir/";
                String hdfsDir = "/tdg";
                try {
                    hdfsCopier.copyFromLocal(localDir,hdfsDir);
                } catch (IOException e) {
	        // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
                
                //load table into Hbase
                HbaseLoader hLoader = new HbaseLoader();
                hLoader.createTableInHbase("$PROJECT_HOME/workdir/createTable.sql");
                hLoader.loadTableInHbase(tableList,"/tdg/csvdir/"); 

                long endTime = System.currentTimeMillis();
                System.out.println("Total time used: " + (endTime - startTime)/1e3 + " seconds");
   }
} 

