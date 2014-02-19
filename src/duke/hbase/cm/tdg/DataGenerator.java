package duke.hbase.cm.tdg;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


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
        private void executePdgfCommand(String[] command){
                       try {
		         ProcessBuilder pb = new ProcessBuilder(command);
			 pb.directory(new File("/home/hadoop/git/schema_design/pdgf"));
			 pb.redirectErrorStream(true);
		         Process p;
			 p = pb.start();
                         p.waitFor();
			 BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			 String line = null;
			 while ((line = reader.readLine()) != null)
			 {
			    System.out.println(line);
			 }

		  } catch (IOException e) {
			e.printStackTrace();
		 } catch (InterruptedException e){
                        e.printStackTrace();
                 }	

         }
	public void generateCSV(String xmlFilePath){
			this.executePdgfCommand(new String[] {"java", "-jar", "pdgf.jar","-load",xmlFilePath});	
  
  
                         this.executePdgfCommand(new String[] {"help"});
                         this.executePdgfCommand(new String[] {"start"});
                        this.executePdgfCommand(new String[] {"exit"});

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
	       
		Table table1 = new Table("Z",20,rowkey,columns) ;
		
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
		dg.generateSql(tableList);
    	        dg.generateCSV("config/simple.xml");
    	
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

