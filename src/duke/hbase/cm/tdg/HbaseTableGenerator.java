package duke.hbase.cm.tdg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class HbaseTableGenerator{
		
	private Schema schema = null;
	private String dataDir = null;
	private String xmlFile = null;
	private String sqlFile = null;
	private String csvDir = null;
    private String hdfsCsvDir = null;
   
	private void setXmlFile(){
		//xml file stored at $PROJECT_HOME/workdir/schema.name
       this.xmlFile = this.dataDir + "/" + schema.name + ".xml";
       System.out.println("Setting xml File = " + this.xmlFile);
	}
    private void setSqlFile(){
    	//sql file stored at $PROJECT_HOME/workdir/schema.name
       this.sqlFile = this.dataDir + "/" + schema.name + ".sql";
       System.out.println("Setting sql File = " + this.sqlFile);
    }
    private void setcsvDir(){
    	//csv file stored at $PROJECT_HOME/workdir/schema.name/csvDir
       this.csvDir = this.dataDir + "/" + "csvDir";
       System.out.println("Setting local csv directory = " + this.csvDir);
    }
    private void setHdfsCsvDir(){    
        //csv file stored at /tdg/schema.name/csvDir
       this.hdfsCsvDir = "/tdg" + "/" + schema.name + "/csvDir";
       System.out.println("Setting hdfs csv directory = " + this.hdfsCsvDir); 
    }
    private void makeDataDir() throws IOException{
        this.dataDir = System.getenv("PROJECT_HOME") +"/" +  "workdir" + "/" + schema.name;       
        // make new directory
        File dataDir = new File(this.dataDir);
        if (!dataDir.exists())
        {
       	  dataDir.mkdir();
             System.out.println("Making data directory for " + schema.name + " = " + dataDir);
	          new File(dataDir + "/csvDir").mkdir(); 
             System.out.println("Making csv directory for "  + schema.name + " = " + dataDir);
         }
        else {
       	 throw new IOException(dataDir + " already exists.");
        }	 
    }
    private ArrayList<Table> createTableList(){
        
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
 		
 		String tableName = schema.name.toUpperCase() +"_Z";
 	       
 		Table table = new Table(tableName,schema.numRows,rowkeyList,columnList) ;
 		tableList.add(table);
		return tableList;
		
  }
    
    
	public void createTable(Schema aSchema){	
    	
		//set schema
    	this.schema = aSchema;
    	
    	//make new working directory if not exist
    	try {
			this.makeDataDir();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	//set the working directories and files
    	this.setXmlFile();
    	this.setSqlFile();
    	this.setcsvDir();
        this.setHdfsCsvDir();   	
		
		// create a new table list 
	    ArrayList<Table> tableList = this.createTableList();
		
		//create sql file
	    SqlBuilder sqlBuilder = new SqlBuilder();
	    sqlBuilder.setOutFile(this.sqlFile);
        sqlBuilder.createSqlFile(tableList);
		
		//create xml file
        XmlBuilder xmlBuilder = new XmlBuilder();
	    xmlBuilder.setXmlFile(this.xmlFile);
        xmlBuilder.setCsvDir(this.csvDir);	 
	    xmlBuilder.createXmlFile(tableList);
	    
	    //generate csv data, using pdgf
	    PdgfDataGenerator pdgf = new PdgfDataGenerator();
        pdgf.setInFile(this.xmlFile);
   	    pdgf.generate( );
   	    
	    //copy csv data from local to hdfs
         try {
    	      HdfsCopier hdfsCopier = new HdfsCopier();
              hdfsCopier.copyFromLocal(this.csvDir,this.hdfsCsvDir);
         } catch (IOException e) {
              e.printStackTrace();
         }
        
         //load table into Hbase
         HbaseLoader hLoader = new HbaseLoader();
         hLoader.createTableInHbase(this.sqlFile);
	     hLoader.loadTableInHbase(tableList, this.hdfsCsvDir);         
	}
	
    public static void main(String[] agrs){
         
   }
} 

