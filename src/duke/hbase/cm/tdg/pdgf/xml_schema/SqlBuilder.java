import java.util.*;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
public class SqlBuilder {
	
	public String buildCreateTableSql(Table table){
		StringBuilder builder = new StringBuilder();
		builder.append("create table if not exists ");
		builder.append(table.getTableName() + " (\n");
		
        ArrayList<Column> rowkey = table.getRowkey();   
 
	    for(Column r:rowkey){
	    	//format
	    	builder.append("\t");
	    	builder.append(r.getColumnName() + " ");
	    	builder.append(r.getColumnType());
	    	if(r.getIsNotNull()){
	    		builder.append(" not null");
	    	}
	    	builder.append(",\n");
	    }
	    	

	    ArrayList<Column> columns = table.getColumns();  
		for(Column c:columns){
		    	//format
		    	builder.append("\t");
		    	builder.append(c.getFamilyName() + ".");
		  
		    	builder.append(c.getColumnName() + " ");
		    	builder.append(c.getColumnType());
		    	if(c.getIsNotNull()){
		    		builder.append(" not null");
		    	}
		    	builder.append(",\n");
		}
		    		    	
	    builder.append("\t");
	    builder.append("constraint pk primary key (");
	    for(Column r:rowkey){
	    	builder.append(r.getColumnName() + " ");
	    }
	    builder.append(")\n");
	    builder.append(");\n");
		
		return builder.toString();
		
	}
	private void writeToSql(String s, String fileName){
		PrintWriter writer;
		try {
			writer = new PrintWriter(fileName, "UTF-8");
			writer.println(s);
	
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void createTableSql(Table table){
		String s = buildCreateTableSql(table);
		writeToSql(s,"createTable.sql");	
	}
	
	public static void main(String[] args){
		Column id = new Column("ID", " ", "INTEGER", 10, true, true);
		Column userName = new Column("UserName"," ","VARCHAR",10,false,true);
		
		Column address = new Column("Address", "_0","VARCHAR", 10,false,false);
		Column accBal = new Column("AccBal","_0","DECIMAL",10,false,false);
		Column comment = new Column("Comment", "_0","VARCHAR", 10,false,false);
		
        ArrayList<Column> rowkey = new ArrayList<Column>();
        rowkey.add(id);
        rowkey.add(userName);
        ArrayList<Column> columns = new ArrayList<Column>();
        columns.add(address);
        columns.add(accBal);
        columns.add(comment);
       
		Table t = new Table("Z",20,rowkey,columns) ;
		t.printTableInfo();
		SqlBuilder sqlBuilder = new SqlBuilder();
//		System.out.println(sqlBuilder.buildCreateTableSql(t));	
		sqlBuilder.createTableSql(t);
	}

}
