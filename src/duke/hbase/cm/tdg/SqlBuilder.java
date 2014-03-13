package duke.hbase.cm.tdg;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
public class SqlBuilder {
	private String sqlFile = null;
	private String buildCreateTableSql(ArrayList<Table> tableList){
		
		StringBuilder builder = new StringBuilder();
		for (Table table:tableList){
			builder.append("create table if not exists ");
			builder.append(table.getName() + " (\n");
			
			ArrayList<Column> rowkey = table.getRowkeyList();    
		    for(Column r:rowkey){
		    	//format
		    	builder.append("\t");
		    	builder.append(r.getName() + " ");
		    	builder.append(r.getType());
		    	if(r.isNotNull()){
		    		builder.append(" not null");
		    	}
		    	builder.append(",\n");
		    }
		    	
		    ArrayList<Column> columns = table.getColumnList();  
			for(Column c:columns){
			    	//format
			    	builder.append("\t");
			    	builder.append(c.getfName() + ".");
			  
			    	builder.append(c.getName() + " ");
			    	builder.append(c.getType());
			    	if(c.isNotNull()){
			    		builder.append(" not null");
			    	}
			    	builder.append(",\n");
			}
			    		    	
		    builder.append("\t");
		    builder.append("constraint pk primary key (");
		    Iterator<Column> it = rowkey.iterator();
		    while(it.hasNext())
		    {		    	
		    	Column c = it.next();
		    	builder.append(c.getName());
		    	if(it.hasNext())
		    		builder.append(", ");
		    }
		    builder.append(")\n");
		    
		    builder.append(");\n");		
		}				
		return builder.toString();
		
	}
	private void write(String s){
		PrintWriter writer;
		try {
			writer = new PrintWriter(sqlFile, "UTF-8");
			writer.println(s);
			writer.close();
		} catch (FileNotFoundException e) {	
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	public void setOutFile(String name){
		this.sqlFile = name;
	}
	public void createSqlFile(ArrayList<Table> tableList){
		String sqlStatement = buildCreateTableSql(tableList);
		System.out.println("-------------------------------------------");
		System.out.println("Writing create table sql statement to " + this.sqlFile);	
		System.out.println(sqlStatement);
		System.out.println("-------------------------------------------");
		write(sqlStatement);	
	}	



}
