package duke.hbase.cm.tdg;

public class ScanQTDGenerator extends TDGenerator {
	public ScanQTDGenerator(String lhsFile, String tdName, String prefixTrainFile, String prefixTestFile) {
		super(lhsFile,tdName,prefixTrainFile, prefixTestFile);
		this.setUpdate(false);
	}

	@Override
	public String prepareQuery(Schema schema) {
//		Table c_table = schema.getTableList().get(0);
		Table o_table = schema.getTableList().get(1);
		
		double max_value = 9999.99;
	    double value = Math.random() * max_value;
		String queryStr = "select * from " + o_table.getName() + " where o_value > " + Double.toString(value);
		return queryStr;	
	}	
	@Override
	public String prepareTDOutput(Schema schema, Configuration config, Query query) {
		Table table = schema.getTableList().get(1);	
		StringBuilder builder = new StringBuilder();	
        //(1). latency	
		builder.append(String.format( "%.2f", query.getLatency()) + "\t");         
        //(2). number of concurrent queries 
        builder.append(Integer.toString(1) + "\t");
		//(3).  number of server side threads	
		builder.append(Integer.toString(4) + "\t");
		//(4). number of rows 
		builder.append(Integer.toString(table.getNumRows()) + "\t");
		//(5). number of columns
//		builder.append(Integer.toString(table.getNumColumns()) + "\t");
		//(6). row key size
//		builder.append(Integer.toString(table.getRowkeySize()) + "\t");
		//(7). column size
//		builder.append(Integer.toString(table.getColumnSize()) + "\t");					
		//(8). returned size
		builder.append(Integer.toString(query.getRetNumRows() * query.getRetColumnSize()) );	
		//new line
		builder.append("\n");			
		return builder.toString();
	}

}
