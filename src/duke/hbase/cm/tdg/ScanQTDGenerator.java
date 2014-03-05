package duke.hbase.cm.tdg;

import java.util.ArrayList;

public class ScanQTDGenerator {
	private String name = null;
	private ArrayList<Schema> schemaList = new ArrayList<Schema>();
	private ArrayList<Configuration> configList = new ArrayList<Configuration>();
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private void addSchema(String[] sampledValues, int k){
	
		//parse sampled values 
		int numRows = Integer.parseInt(sampledValues[0]);
		int numColumns = Integer.parseInt(sampledValues[1]);
		int rowkeySize = Integer.parseInt(sampledValues[2]);
		int columnSize = Integer.parseInt(sampledValues[3]);
		//create columns

		Column id       = new Column(" ", "ID",       "INTEGER",  10,         true, true, true);
 		Column userName = new Column(" ", "USERNAME", "VARCHAR",  rowkeySize, false,true,true);	
 		Column accBal   = new Column("f", "ACCBAL" ,  "DECIMAL",  columnSize, false,false,false);
 		Column address  = new Column("f", "ADDRESS",  "VARCHAR",  columnSize, false,false,false);
 		//create rowkey list and column list
 		ArrayList<Column> rowkeyList = new ArrayList<Column>();
 		ArrayList<Column> columnList = new ArrayList<Column>();
 		rowkeyList.add(id);
 		rowkeyList.add(userName);     
 		columnList.add(accBal);
 		columnList.add(address); 		
 		//create rest of columns automatically
 		for(int i=0; i<numColumns-4; i++){
 			columnList.add(new Column("f","COMMENT" + (i+1), "VARCHAR",  columnSize, false,false,false));		
 		}
 		//create table
		
 		String tableName = this.getName() + k + "";
 		Table table =  new Table(tableName, numRows, numColumns, rowkeySize, columnSize, rowkeyList, columnList);
 		//crate table list
		ArrayList<Table> tableList = new ArrayList<Table>();
		tableList.add(table);	
		//create schema
		String schemaName = this.getName()+k;
		Schema schema = new Schema(schemaName,tableList);
		//append schema to schema list
		this.schemaList.add(schema);
	}
	private void addConfig(String[] sampledValues, int k){	
		Configuration config = new Configuration();
		this.configList.add(config);			
	}
	public int parseLHS(String lhsFile){	
		LHSReader lhsReader = new LHSReader();
		int numSamples = lhsReader.getTotalNumRows(lhsFile);
		for(int k=0;k<numSamples;k++){
			String[] sampledValues = null;
			sampledValues = lhsReader.readKthLine(lhsFile,k+1);
			this.addSchema(sampledValues,k+1);
			this.addConfig(sampledValues,k+1);
		}
		return numSamples;
	}

	public String praperTrainData(Schema schema, Configuration config, Query query){
		Table table = schema.getTableList().get(0);
		StringBuilder builder = new StringBuilder();	
        //(1). latency
		builder.append(Double.toString(query.getLatency()) + "\t");         
        //(2). number of concurrent queries 
        builder.append(Integer.toString(1) + "\t");
		//(3).  number of server side threads	
		builder.append(Integer.toString(4) + "\t");
		//(4). number of rows 
		builder.append(Integer.toString(table.getNumRows()) + "\t");
		//(5). row key size
		builder.append(Integer.toString(table.getRowkeySize()) + "\t");
		//(6). sum of rest of column size 
		builder.append(Integer.toString(table.getColumnSize()) + "\t");				
		builder.append("\n");	
		return builder.toString();
	}	
	public String praperScanQuery(Schema schema){
		String queryStr = "select *  from " + schema.getName() + " where accbal > 9000";
		return queryStr;		
	}

	public void generate(String lhsFile,String trainData){
		int numSamples = this.parseLHS(lhsFile);
		Query query = new Query();
		TrainDataWriter tdw = new TrainDataWriter(trainData);
		
		for(int k=0;k<numSamples;k++){
			
			Schema schema = this.schemaList.get(k);
			Configuration config = this.configList.get(k);
			
			HbaseTableGenerator hbaseGen = new HbaseTableGenerator(schema,config);
			hbaseGen.createTable();		
			
			System.out.println("----------------------------------------------------------------------");
			System.out.println("Executing query");

			String queryStr = this.praperScanQuery(schema);
			query.execute(queryStr);
			
			System.out.println("----------------------------------------------------------------------");
			System.out.println("Writing training data");
			String line = this.praperTrainData(schema, config, query);

			tdw.write(line);
		}	
	}

	public void generateTestData(String lhsTestFile, String tempTrainData, String testData, String testRealLatency){
		//store training data at tempTrainData
		this.generate(lhsTestFile,tempTrainData);
		TrainDataSpliter spliter = new TrainDataSpliter();
		//split tempTrainData into testData, which contain parameters only,
		//and testRealLatency, which contain the real latency
		spliter.splitTrainData(tempTrainData, testData, testRealLatency);	
	}
    public static void main(String[] args) { 
      long startTime = System.currentTimeMillis();  
      
      String lhsTrainFile = System.getenv("PROJECT_HOME") + "/workdir/lhs_train.csv"; 		 
      String trainData = System.getenv("PROJECT_HOME") + "/workdir/scan_train_data.txt";
      
      ScanQTDGenerator trainGen = new  ScanQTDGenerator();
      trainGen.setName("scan_train");
      trainGen.generate(lhsTrainFile, trainData);
      
      System.out.println("----------------------------------------------------------------------");
      System.out.println("Start test data generation");
      
      String lhsTestFile = System.getenv("PROJECT_HOME") + "/workdir/lhs_test.csv"; 	
      String tempTrainData = System.getenv("PROJECT_HOME") + "/workdir/scan_temp_train_data.txt";
      String testData = System.getenv("PROJECT_HOME") + "/workdir/scan_test_data.txt";
      String testRealLatency = System.getenv("PROJECT_HOME") + "/workdir/scan_test_real_latency.txt";
      String testEstimateLatency = System.getenv("PROJECT_HOME") + "/workdir/scan_test_estimate_latency.txt";
      
      ScanQTDGenerator testGen = new  ScanQTDGenerator();
      testGen.setName("scan_test");
      testGen.generateTestData(lhsTestFile, tempTrainData,testData,testRealLatency);
      
      ErrorEstimator es = new ErrorEstimator();
      double mse = es.estimate(trainData, testData, testRealLatency, testEstimateLatency);
      System.out.println("The MSE = " + mse);
       
      long endTime = System.currentTimeMillis();
      System.out.println("Total time used: " + (endTime - startTime)/1e3 + " seconds");   
      System.exit(0);
  }
}
