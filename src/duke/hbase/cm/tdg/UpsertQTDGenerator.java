package duke.hbase.cm.tdg;

import java.util.ArrayList;

public class UpsertQTDGenerator extends TDGenerator {

	public UpsertQTDGenerator(String lhsFile, String tdName, String prefixTrainFile, String prefixTestFile) {
		super(lhsFile,tdName,prefixTrainFile, prefixTestFile);
		this.setUpdate(true);
	}

	@Override
	public String prepareQuery(Schema schema) {
		ArrayList<Table> tableList = schema.getTableList();
		
		Table o_table = tableList.get(1);
	    int numRows = o_table.getNumRows();
		int numColumns = o_table.getNumColumns();

		StringBuilder builder = new StringBuilder();
		builder.append("upsert into " + o_table.getName() + " values(");
		builder.append(Integer.toString(numRows + 1) + 
				       "," + "\'insertedNname\'" +  
				       "," + Integer.toString(1000) + 
				       "," + Double.toString(99.99) 
		               );	
 		for(int i=0; i<numColumns-4; i++){
 			builder.append("," + "\'testComment" + (i+1) + "\'");	
 		}
 		builder.append(")");
 		
		return builder.toString();
		
	}

	@Override
	public String prepareTDOutput(Schema schema, Configuration config, Query query) {
	
		Table table = schema.getTableList().get(1);	
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
		//(6). row key size
		builder.append(Integer.toString(table.getRowkeySize()) + "\t");
		//(7). column size
		builder.append(Integer.toString(table.getColumnSize()));					

		//new line
		builder.append("\n");	
		
		return builder.toString();
		
	}
	

}
