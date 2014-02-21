package duke.hbase.cm.tdg;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.io.*;

public class HbaseTableGenerator{
	private static final String PROJECT_HOME = System.getenv("PROJECT_HOME");	
	private String dataDir = PROJECT_HOME + "/workdir";
	
	private String xmlFile = null;
	private String sqlFile = null;
	private String csvDir = null;
	private String hdfsDir = "/tdg";
    private String hdfsCsvDir = null;
	
	public void setXmlFile(String fileName){
		this.xmlFile = dataDir + "/" + fileName;
                System.out.println("Setting xmlFile = " + this.xmlFile);
	}
    public void setSqlFile(String fileName){
         	this.sqlFile = dataDir + "/" + fileName; 
                System.out.println("Setting sqlFile = " + this.sqlFile);
    }
    public void setcsvDir(String fileName){
        	this.csvDir = dataDir + "/" + fileName;
                this.hdfsCsvDir = hdfsDir + "/" + fileName;
                System.out.println("Setting local csv directory = " + this.csvDir);
                System.out.println("Setting hdfs csv directory = " + this.hdfsCsvDir);
    }
    public ArrayList<Table> createTableList(Schema schema){
        ArrayList<Table> tableList = new ArrayList<Table>();
		
 		//generate table 1
 		Column id       = new Column("ID",      " ", "INTEGER",  10,               true, true, true);
 		Column userName = new Column("UserName"," ", "VARCHAR",  schema.rowkeySize,false,true,true);	
 		Column address  = new Column("Address", "f", "VARCHAR",  schema.columnSize,false,false,false);
 		Column accBal   = new Column("AccBal",  "f", "DECIMAL",  schema.columnSize,false,false,false);
 		Column comment  = new Column("Comment", "f", "VARCHAR",  schema.columnSize,false,false,false);
 		
 		ArrayList<Column> rowkeyList = new ArrayList<Column>();
 		ArrayList<Column> columnList = new ArrayList<Column>();
 		
 		rowkeyList.add(id);
 		rowkeyList.add(userName);     
 		columnList.add(address);
 		columnList.add(accBal);
 		columnList.add(comment);
 	       
 		Table table = new Table("Z",schema.numRows,rowkeyList,columnList) ;
 		
 		tableList.add(table);
 		
		return tableList;
    	
    }
    
	public void createTableInHbase(Schema schema){	
		
		ArrayList<Table> tableList = createTableList(schema);
		
		//create sql file
	    SqlBuilder sqlBuilder = new SqlBuilder();
	    sqlBuilder.setOutFile(sqlFile);
        sqlBuilder.createSqlFile(tableList);
		
		//create xml file
	    XmlBuilder xmlBuilder = new XmlBuilder();
	    xmlBuilder.setXmlFile(xmlFile);
            xmlBuilder.setCsvDir(csvDir);	 
	    xmlBuilder.createXmlFile(tableList);
	    
	    //generate csv data using pdgf
	    PdgfDataGenerator pdgf = new PdgfDataGenerator();
            pdgf.setInFile(xmlFile);
   	    pdgf.generate( );
   	    
	    //copy csv data from local to hdfs

        try {
    	    HdfsCopier hdfsCopier = new HdfsCopier();
            hdfsCopier.copyFromLocal(csvDir,hdfsDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        //load table into Hbase
        HbaseLoader hLoader = new HbaseLoader();
        hLoader.createTableInHbase(sqlFile);
	hLoader.loadTableInHbase(tableList, hdfsCsvDir);         
	}

    public static void main(String[] agrs){
  
         long startTime = System.currentTimeMillis(); 
    	 String lhsFile = "/Users/Weizheng/git/schema_design/src/duke/hbase/cm/tdg/LHS.csv";
 		
		 Schema schema = new Schema();
		 schema.initializeFromLHS(lhsFile, 1);
		 
         HbaseTableGenerator gen = new HbaseTableGenerator();
         gen.setcsvDir("csvdir/");
         gen.setSqlFile("z.sql");
         gen.setXmlFile("z.xml");
         gen.createTableInHbase(schema);
      	 long endTime = System.currentTimeMillis();
         System.out.println("Total time used: " + (endTime - startTime)/1e3 + " seconds");
   }
} 

