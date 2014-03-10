package duke.hbase.cm.tdg;

import java.util.ArrayList;

public class JoinQTDGenerator extends TDGenerator {
	
	public JoinQTDGenerator(String name, String queryName) {
		super(name, queryName);
	}
	@Override
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
	    String c_TableName = this.getName() + k;
	    Table c_Table = super.nextCustomerTable(c_TableName, c_NumRows, c_NumColumns, c_RowkeySize, c_ColumnSize);
		
	    String o_TableName = this.getName() + k;
	    Table o_Table = super.nextOrdersTable(o_TableName, o_NumRows, o_NumColumns, o_RowkeySize, o_ColumnSize);
		//crate table list
		ArrayList<Table> tableList = new ArrayList<Table>();
		tableList.add(c_Table);	
		tableList.add(o_Table);
				
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
		Table o_table = schema.getTableList().get(1);
		String queryStr = "select * from " +
		                  c_table.getName() + " c " +
				          " inner join " + 
		                  o_table.getName() + " o " + 
				          " on c.c_custkey = o.o_custkey" +
                          " and o.o_custkey<10 where c.c_custkey<10";;
		return queryStr;	
	}
	@Override
	public String prepareTDOutput(Schema schema, Configuration config, Query query) {
		Table c_table = schema.getTableList().get(0);
		Table o_table = schema.getTableList().get(1);
		StringBuilder builder = new StringBuilder();	
        //(1). latency
		builder.append(Double.toString(query.getLatency()) + "\t");         
        //(2). number of concurrent queries 
        builder.append(Integer.toString(1) + "\t");
		//(3).  number of server side threads	
		builder.append(Integer.toString(4) + "\t");
		//(4). customer table number of rows 
		builder.append(Integer.toString(c_table.getNumRows()) + "\t");
		//(5). customer table number of columns
		builder.append(Integer.toString(c_table.getNumColumns()) + "\t");
		//(6). orders table number of rows 
		builder.append(Integer.toString(o_table.getNumRows()) + "\t");
		//(7). orders table number of columns
		builder.append(Integer.toString(o_table.getNumColumns()) + "\t");		
		//(8).  number of returned rows 
		builder.append(Integer.toString(query.getRetNumRows()) + "\t");	
		//(9). returned column size
		builder.append(Integer.toString(query.getRetColumnSize()));
		
		builder.append("\n");	
		return builder.toString();
	}




}
