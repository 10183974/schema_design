package duke.hbase.cm.tdg;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.io.*;

public class HbaseTableGenerator{
	private static String dataDir = null;
	
	private String xmlFile = null;
	private String sqlFile = null;
	private String csvDir = null;
        private String hdfsCsvDir = null;
	
	public void setXmlFile(Schema schema){
            this.xmlFile = schema.dataDir + "/" + schema.name + ".xml";
            System.out.println("Setting xmlFile = " + this.xmlFile);
	}
        public void setSqlFile(Schema schema){
            this.sqlFile = schema.dataDir + "/" + schema.name + ".sql";
            System.out.println("Setting sqlFile = " + this.sqlFile);
        }
        public void setcsvDir(Schema schema){
            this.csvDir = schema.dataDir;
            System.out.println("Setting local csv directory = " + this.csvDir);
        }
        public void setHdfsCsvDir(Schema schema){          
           this.hdfsCsvDir = "/tdg" + "/" + schema.name + "/csvDir";
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
 	       
 		Table table = new Table(schema.name.toUpperCase() +"_Z",schema.numRows,rowkeyList,columnList) ;
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
              hdfsCopier.copyFromLocal(csvDir,hdfsCsvDir);
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
    	          	      
         String lhsFile = System.getenv("PROJECT_HOME") + "/src/duke/hbase/cm/tdg/LHS.csv"; 		
         System.out.println("LHS.csv directory = " + lhsFile);
	       
         for(int i =1; i<2;i++){		 
           
	       // initialize a new schema 
               Schema schema = new Schema();
	       schema.initialSchema("schema"+i,lhsFile, i);
                  
               //
               HbaseTableGenerator gen = new HbaseTableGenerator();
                  
               gen.setSqlFile(schema);
               gen.setXmlFile(schema);
               gen.setcsvDir(schema);
               gen.setHdfsCsvDir(schema);
               gen.createTableInHbase(schema);
      	 }
         long endTime = System.currentTimeMillis();
         System.out.println("Total time used: " + (endTime - startTime)/1e3 + " seconds");
   }
} 

