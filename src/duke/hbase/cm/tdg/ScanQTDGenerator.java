package duke.hbase.cm.tdg;

import java.util.ArrayList;
public class ScanQTDGenerator extends TDGenerator {
	public ScanQTDGenerator(String name, String queryName) {
		super(name, queryName);
	}
	@Override
	public Schema nextSchema(String[] sampledValues, int k) {
		//parse sampled values 
		int numRows = Integer.parseInt(sampledValues[0]);
  	    int numColumns = Integer.parseInt(sampledValues[1]);
		int rowkeySize = Integer.parseInt(sampledValues[2]);
		int columnSize = Integer.parseInt(sampledValues[3]);
		
		//create customer table
		String tableName = this.getName() + k;
		Table table = super.nextCustomerTable(tableName, numRows, numColumns, rowkeySize, columnSize);
		
		//crate table list
		ArrayList<Table> tableList = new ArrayList<Table>();
		tableList.add(table);	
		
		//create schema
	    String schemaName = this.getName()+k;
		Schema schema = new Schema(schemaName,tableList); 
		return schema;
	}
	@Override
	public Configuration nextConfig(String[] sampledValues, int k) {
		Configuration config = new Configuration();
		return config;
	}
	@Override
	public String prepareQuery(Schema schema) {
		Table c_table = schema.getTableList().get(0);
		String queryStr = "select *  from " + c_table.getName() + " where c_accbal > 9000";
		return queryStr;	
	}	
	@Override
	public String prepareTDOutput(Schema schema, Configuration config, Query query) {
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
		//(5). number of columns
		builder.append(Integer.toString(table.getNumColumns()) + "\t");
		//(6). number of returned rows 
		builder.append(Integer.toString(query.getRetNumRows()) + "\t");	
		//(7). returned column size
		builder.append(Integer.toString(query.getRetColumnSize()));
		//new line
		builder.append("\n");	
		
		return builder.toString();
	}

}
