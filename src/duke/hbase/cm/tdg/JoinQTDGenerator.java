package duke.hbase.cm.tdg;

public class JoinQTDGenerator extends TDGenerator {
	

	public JoinQTDGenerator(String lhsFile, String tdName, String prefixTrainFile, String prefixTestFile) {
		super(lhsFile,tdName,prefixTrainFile, prefixTestFile);
		this.setUpdate(false);
	}

	@Override
	public String prepareQuery(Schema schema) {
		Table c_table = schema.getTableList().get(0);
		Table o_table = schema.getTableList().get(1);
		String queryStr = "select c_custkey,c_name,c_accbal,c_address from " +
				           o_table.getName() + " o " + 
		                   " inner join " +
                           c_table.getName() + " c " +
		                  " on c.c_custkey = o.o_custkey";
		return queryStr;	
	}
	@Override
	public String prepareTDOutput(Schema schema, Configuration config, Query query) {
		Table c_table = schema.getTableList().get(0);
		Table o_table = schema.getTableList().get(1);
		StringBuilder builder = new StringBuilder();	
        //(1). latency
		builder.append(String.format( "%.2f", query.getLatency()) + "\t");           
        //(2). number of concurrent queries 
        builder.append(Integer.toString(1) + "\t");
		//(3).  number of server side threads	
		builder.append(Integer.toString(4) + "\t");
		
		//(4). customer table number of rows 
		builder.append(Integer.toString(c_table.getNumRows()) + "\t");
		//(5). customer table number of columns
//		builder.append(Integer.toString(c_table.getNumColumns()) + "\t");
		//(6). customer table row key size
		builder.append(Integer.toString(c_table.getRowkeySize()) + "\t");
		//(7). customer table column size
//		builder.append(Integer.toString(c_table.getColumnSize()) + "\t");
		
		//(8). orders table number of rows 
		builder.append(Integer.toString(o_table.getNumRows()) + "\t");
		//(9). orders table number of columns
//		builder.append(Integer.toString(o_table.getNumColumns()) + "\t");
		//(10). order table row key size
		builder.append(Integer.toString(o_table.getRowkeySize()) + "\t");
		//(11). order table column size
//		builder.append(Integer.toString(o_table.getColumnSize()) + "\t");	
		
		//(12). returned size
		builder.append(Integer.toString(query.getRetNumRows() * query.getRetColumnSize()) );	
		
		builder.append("\n");	
		return builder.toString();
	}




}
