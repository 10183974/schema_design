package duke.hbase.cm.tdg;

import java.util.ArrayList;

abstract public class TDGenerator {
	private String tdName = null;
	
	private int numSamples = 0;
	private boolean isUpdate = false;
	
	private String trainData = null;
	private String testData = null;
	private String testPara = null;
	private String testRealLatency = null;
	private String testEstimateLatency = null;

	private ArrayList<Schema> schemaList = new ArrayList<Schema>();
	private ArrayList<Configuration> configList = new ArrayList<Configuration>();
	
	
	public TDGenerator(String lhsFile, String tdName, String prefixTrainFile, String prefixTestFile){
		this.tdName = tdName;
		this.setWorkEnv(prefixTrainFile,prefixTestFile);
		this.parseLHSFile(lhsFile);
	}

	public void setWorkEnv(String prefixTrainFile, String prefixTestFile){
		String projWorkDir =  System.getenv("PROJECT_HOME") + "/workdir";
		this.trainData = projWorkDir + "/" + prefixTrainFile + "trainData.txt";
		this.testData = projWorkDir + "/" +  prefixTestFile+ "testData.txt";
		this.testPara = projWorkDir + "/" +  prefixTestFile+ "testPara.txt";
		this.testRealLatency = projWorkDir + "/" + prefixTestFile + "testRealLatency.txt";
		this.testEstimateLatency = projWorkDir + "/" + prefixTrainFile + "testEstimateLatency.txt";	
	}
	

    public Table nextCustomerTable(String tdName, int numRows, int numColumns, int rowkeySize, int columnSize){
    	//create columns
        Column c_CustKey  = new Column(" ", "C_CUSTKEY",       "INTEGER",  10,         true, true, true);
    	Column c_Name     = new Column(" ", "C_NAME", "VARCHAR",  rowkeySize, false,true,true);	
    	Column c_AccBal   = new Column("f", "C_ACCBAL" ,  "DECIMAL",  columnSize, false,false,false);
    	Column c_Address  = new Column("f", "C_ADDRESS",  "VARCHAR",  columnSize, false,false,false);   	 		
    	//create row key list and column list
    	ArrayList<Column> c_RowkeyList = new ArrayList<Column>();
    	ArrayList<Column> c_ColumnList = new ArrayList<Column>();
    	c_RowkeyList.add(c_CustKey);
    	c_RowkeyList.add(c_Name);     
    	c_ColumnList.add(c_AccBal);
    	c_ColumnList.add(c_Address); 		
    	 		
    	//create rest of columns automatically
    	for(int i=0; i<numColumns-4; i++){
    	 	c_ColumnList.add(new Column("f","C_COMMENT" + (i+1), "VARCHAR",  columnSize, false,false,false));		
    	}
    	//create table		
    	String c_TableName = tdName + "_customer";
    	Table c_Table =  new Table(c_TableName, numRows, numColumns, rowkeySize, columnSize, c_RowkeyList, c_ColumnList);   	
    	return c_Table;
    }
    public Table nextOrdersTable(String tdName, int numRows, int numColumns, int rowkeySize, int columnSize, int c_numRows){
    	Column o_OrderKey  = new Column(" ", "O_ORDERKEY",  "INTEGER",  10, true, true, true);
    	Column o_OrderName = new Column(" ", "O_ORDERNAME", "VARCHAR",  rowkeySize, false,true,true);	
 		Column o_CustKey   = new Column("f", "O_CUSTKEY",   "INTEGER",  10, false,false,false, 1, c_numRows);
 		Column o_Value     = new Column("f", "O_VALUE" ,    "DECIMAL",  columnSize, false,false,false);
 		//create row key list and column list
 		ArrayList<Column> o_RowkeyList = new ArrayList<Column>();
 		ArrayList<Column> o_ColumnList = new ArrayList<Column>();
 		o_RowkeyList.add(o_OrderKey);
 		o_RowkeyList.add(o_OrderName);
 		o_ColumnList.add(o_CustKey);   
 		o_ColumnList.add(o_Value);
 		//create rest of columns automatically
 		for(int i=0; i<numColumns-4; i++){
 			o_ColumnList.add(new Column("f","O_COMMENT" + (i+1), "VARCHAR",  columnSize, false,false,false));		
 		}
 		//create table		
 		String o_TableName = tdName + "_orders";
 		Table o_Table =  new Table(o_TableName, numRows, numColumns, rowkeySize, columnSize, o_RowkeyList, o_ColumnList);				
 		return o_Table;
    }

    public Schema nextSchema(String[] sampledValues, int k) {
		//parse sampled values 
	    int c_NumRows = Integer.parseInt(sampledValues[0]);
		int c_NumColumns =  Integer.parseInt(sampledValues[1]);
		int c_RowkeySize = Integer.parseInt(sampledValues[2]);
		int c_ColumnSize = Integer.parseInt(sampledValues[3]);
						
		int o_NumRows =  Integer.parseInt(sampledValues[4]);
		int o_NumColumns =  Integer.parseInt(sampledValues[5]);
		int o_RowkeySize =  Integer.parseInt(sampledValues[6]);
		int o_ColumnSize = Integer.parseInt(sampledValues[7]);
						
		//create customer table
		String c_TableName = this.tdName + k;
	    Table c_Table = this.nextCustomerTable(c_TableName, c_NumRows, c_NumColumns, c_RowkeySize, c_ColumnSize);
				
		String o_TableName = this.tdName + k;
	    Table o_Table = this.nextOrdersTable(o_TableName, o_NumRows, o_NumColumns, o_RowkeySize, o_ColumnSize,c_NumRows);
		//crate table list
		ArrayList<Table> tableList = new ArrayList<Table>();
		tableList.add(c_Table);	
		tableList.add(o_Table);
						
		//create schema
		String schemaName = this.tdName + k;
	    Schema schema = new Schema(schemaName,tableList); 
	    return schema;
	}
	public Configuration nextConfig(String[] sampledValues, int k) {
		Configuration config = new Configuration();
		return config;
	}
	public void parseLHSFile(String lhsFile){
		LHSReader lhsReader = new LHSReader();
		this.numSamples = lhsReader.getTotalNumRows(lhsFile);
		for(int k=0;k<numSamples;k++){
			String[] sampledValues = null;
			sampledValues = lhsReader.readKthLine(lhsFile,k+1);	
			Schema schema = this.nextSchema(sampledValues,k+1);
			Configuration config = this.nextConfig(sampledValues, k+1);
            schemaList.add(schema);
            configList.add(config);

		}	
	}
	public void prepareTables(){
		for(int k=0;k<numSamples;k++){
			Schema schema = schemaList.get(k);
			Configuration config = configList.get(k);
		    HbaseTableGenerator hbaseGen = new HbaseTableGenerator(schema,config);
			hbaseGen.createTable();		
		}	
	}
	public abstract String prepareQuery(Schema schema);
	public abstract String prepareTDOutput(Schema schema, Configuration config, Query query);
	
	public void generateTrainData(){
		this.generate(this.trainData);
	}	
	public void generateTestData(){
		//split tempTrainData into testData and realLatency
		this.generate(this.testData);
		TrainDataSpliter spliter = new TrainDataSpliter();
		spliter.splitTrainData(this.testData, this.testPara, this.testRealLatency);	
	}
	private void generate(String dataFile){
		DataWriter dw = new DataWriter(dataFile);	
		for(int k=0;k<this.numSamples;k++){		
			Schema schema = schemaList.get(k);
			Configuration config = configList.get(k);
			
			Query query = new Query();
			String queryStr = this.prepareQuery(schema);
			
			query.execute(queryStr,isUpdate,1);
			
			String line = this.prepareTDOutput(schema, config, query);
			dw.write(line);
		}	
	}

	public void setUpdate(boolean isUpsert) {
		this.isUpdate = isUpsert;
	}

	public double getStat(){
		  ErrorEstimator es = new ErrorEstimator();
	      es.estimate(this.trainData, this.testPara, this.testRealLatency, this.testEstimateLatency);
	      
	      System.out.println("Train data = " + this.trainData);
	      System.out.println("Test  para = " + this.testPara);
	      System.out.println("Train testRealLatency = " + this.testRealLatency);
	      System.out.println("Train testEstimateLatency = " + this.testEstimateLatency);
	      System.out.println("Latency = " + es.getMean() + " +- " + es.getSigma() + " (ms)");
	      System.out.println("===============================================================");
	      return es.getSigma() / es.getMean();
	}
	public void dropTables(String name){	
		String dropSql = System.getenv("PROJECT_HOME")  + "/workdir/drop_" + name + ".sql" ;
		StringBuilder builder = new StringBuilder();
		DataWriter dw = new DataWriter(dropSql);
		for(int k=0;k<numSamples;k++){
			Schema schema = schemaList.get(k);
	        ArrayList<Table> tableList = schema.getTableList();
			for(Table t:tableList){
				builder.append("drop table " + t.getName() + ";\n");
			}	             
		}	
		dw.write(builder.toString());
		
		PhoenixCMDExecutor pcmd = new PhoenixCMDExecutor();
		pcmd.dropTablesInHbase(dropSql);
				
	}
	
}
