package duke.hbase.cm.tdg;

import java.util.ArrayList;

abstract public class TDGenerator {
	private String name = null;
	private String queryName = null;
	private int numSamples = 0;
	
	private String trainData = null;
	private String testData = null;
	private String testPara = null;
	private String testRealLatency = null;
	private String testEstimateLatency = null;

	private ArrayList<Schema> schemaList = new ArrayList<Schema>();
	private ArrayList<Configuration> configList = new ArrayList<Configuration>();
	
	public String getName() {
		return name;
	}
	public void setName(String name){
		this.name = name;
	}
	
	public TDGenerator(String name, String queryName){
		this.name = name;
		this.queryName = queryName;
		this.setWorkEnv();
	}

	public void setWorkEnv(){
		String projWorkDir =  System.getenv("PROJECT_HOME") + "/workdir";
		this.trainData = projWorkDir + "/" + this.queryName + "_trainData.txt";
		this.testData = projWorkDir + "/" + this.queryName + "_testData.txt";
		this.testPara = projWorkDir + "/" + this.queryName + "_testPara.txt";
		this.testRealLatency = projWorkDir + "/" + this.queryName + "_testRealLatency.txt";
		this.testEstimateLatency = projWorkDir + "/" + this.queryName + "_testEstimateLatency.txt";	
	}
	

    public Table nextCustomerTable(String name, int numRows, int numColumns, int rowkeySize, int columnSize){
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
    	String c_TableName = name + "_customer";
    	Table c_Table =  new Table(c_TableName, numRows, numColumns, rowkeySize, columnSize, c_RowkeyList, c_ColumnList);   	
    	return c_Table;
    }
    public Table nextOrdersTable(String name, int numRows, int numColumns, int rowkeySize, int columnSize){
    	Column o_OrderKey  = new Column(" ", "O_ORDERKEY",  "INTEGER",  10, true, true, true);
 		Column o_CustKey   = new Column("f",  "O_CUSTKEY",  "INTEGER",  10, false,false,false, 1, numRows);
 		//create row key list and column list
 		ArrayList<Column> o_RowkeyList = new ArrayList<Column>();
 		ArrayList<Column> o_ColumnList = new ArrayList<Column>();
 		o_RowkeyList.add(o_OrderKey);
 		o_ColumnList.add(o_CustKey);     	
 		//create rest of columns automatically
 		for(int i=0; i<numColumns-2; i++){
 			o_ColumnList.add(new Column("f","O_COMMENT" + (i+1), "VARCHAR",  columnSize, false,false,false));		
 		}
 		//create table		
 		String o_TableName = name + "_orders";
 		Table o_Table =  new Table(o_TableName, numRows, numColumns, rowkeySize, columnSize, o_RowkeyList, o_ColumnList);				
 		return o_Table;
    }

	public abstract Schema nextSchema(String[] sampledValues, int k);
	public abstract Configuration nextConfig(String[] sampledValues, int k);
	
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
		TrainDataWriter tdw = new TrainDataWriter(dataFile);	
		for(int k=0;k<this.numSamples;k++){		
			Schema schema = schemaList.get(k);
			Configuration config = configList.get(k);
			
			Query query = new Query();
			String queryStr = this.prepareQuery(schema);
			query.execute(queryStr,1);
			
			String line = this.prepareTDOutput(schema, config, query);
			tdw.write(line);
		}	
	}
	public void getStat(){
		  ErrorEstimator es = new ErrorEstimator();
	      es.estimate(this.trainData, this.testPara,
	    		      this.testRealLatency, this.testEstimateLatency);
	      System.out.println("Latency = " + es.getMean() + " +- " + es.getSigma() + " (ms)");
	}
	public void dropTables(){
		
	}



}
