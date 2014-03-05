package duke.hbase.cm.tdg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class HbaseTableGenerator{
		
	private Schema schema = null;
	private Configuration config = null;
	private String dataDir = null;
	private String xmlFile = null;
	private String sqlFile = null;
	private String csvDir = null;
    private String hdfsCsvDir = null;
    public HbaseTableGenerator(Schema schema, Configuration config){
    	//set schema
    	this.schema = schema;
    	this.config = config;
    	//prepare the working directories and files
    	try{
    		this.setWorkDir();
    	} catch (IOException e){
    		e.printStackTrace();
    	}  	
    }
    private void setWorkDir() throws IOException{
        this.dataDir = System.getenv("PROJECT_HOME") +"/" +  "workdir" + "/" + schema.getName();       

        File dataDir = new File(this.dataDir);
        if (!dataDir.exists()) {
       	     dataDir.mkdirs();
       	     new File(dataDir + "/csvDir").mkdir(); 
	         System.out.println("Making new data directory for " + schema.getName() + " = " + dataDir);
        }
        else {
       	      throw new IOException(dataDir + " already exists.");
        }	 
        this.xmlFile = this.dataDir + "/" + schema.getName() + ".xml";
        this.sqlFile = this.dataDir + "/" + schema.getName() + ".sql";
        this.csvDir  = this.dataDir + "/" + "csvDir";
        this.hdfsCsvDir = "/tdg" + "/" + schema.getName() + "/csvDir";
    }
 
	public void createTable(){	  	
    	try {	   		
			// create a new table list 
		    ArrayList<Table> tableList = this.schema.getTableList();        	
			//create sql file
		    SqlBuilder sqlBuilder = new SqlBuilder();
		    sqlBuilder.setOutFile(this.sqlFile);
	        sqlBuilder.createSqlFile(tableList);
			//create xml file
	        XmlBuilder xmlBuilder = new XmlBuilder();
		    xmlBuilder.setXmlFile(this.xmlFile);
	        xmlBuilder.setCsvDir(this.csvDir);	 
		    xmlBuilder.createXmlFile(tableList);
		    //generate csv data, 
		    PdgfDataGenerator pdgf = new PdgfDataGenerator();
	        pdgf.setXmlFile(this.xmlFile);
	   	    pdgf.generate();
	   	    //copy csv data from local to hdfs
	   	    HdfsCopier hdfsCopier = new HdfsCopier();
            hdfsCopier.copyFromLocal(this.csvDir,this.hdfsCsvDir);
            //load table into Hbase
            HbaseLoader hbaseLoader = new HbaseLoader();
            hbaseLoader.createTableInHbase(this.sqlFile);
   	        hbaseLoader.loadTableInHbase(tableList, this.hdfsCsvDir);      
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    				  
	}
	
} 

