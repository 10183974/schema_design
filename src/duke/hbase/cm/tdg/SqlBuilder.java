package duke.hbase.cm.tdg;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
public class SqlBuilder {
	private String sqlFile = null;
	private String buildSqlStatement(ArrayList<Table> tableList){
		
		StringBuilder builder = new StringBuilder();
		for (Table table:tableList){
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
		    Iterator<Column> it = rowkey.iterator();
		    while(it.hasNext())
		    {		    	
		    	Column c = it.next();
		    	builder.append(c.getColumnName());
		    	if(it.hasNext())
		    		builder.append(", ");
		    }

		    builder.append(")\n");
		    builder.append(");\n");		
		}
				
		return builder.toString();
		
	}
	private void writeToSql(String s){
		PrintWriter writer;
		try {
			writer = new PrintWriter(sqlFile, "UTF-8");
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
	public void setOutFile(String name){
		this.sqlFile = name;
	}
	public void createSqlFile(ArrayList<Table> tableList){
		String sqlStatement = buildSqlStatement(tableList);
		System.out.println("-------------------------------------------");
		System.out.println("Writing create table sql statement to " + this.sqlFile);	
		System.out.println(sqlStatement);
		System.out.println("-------------------------------------------");
		writeToSql(sqlStatement);	
	}	
	public static void main(String[] args){
		ArrayList<Table> tableList = new ArrayList<Table>();
		
		//table 1
		Column id = new Column("ID", " ", "INTEGER", 10, true, true);
		Column userName = new Column("UserName"," ","VARCHAR",10,false,true);		
		Column address = new Column("Address", "f","VARCHAR", 10,false,false);
		Column accBal = new Column("AccBal","f","DECIMAL",10,false,false);
		Column comment = new Column("Comment", "f","VARCHAR", 10,false,false);
		
		ArrayList<Column> rowkey = new ArrayList<Column>();
		rowkey.add(id);
		rowkey.add(userName);
		ArrayList<Column> columns = new ArrayList<Column>();
		columns.add(address);
		columns.add(accBal);
		columns.add(comment);
       
		Table table1 = new Table("Z",20,rowkey,columns) ;
	
		tableList.add(table1);
		
		//table 2
		Column ip = new Column("IP", " ", "INTEGER", 10, true, true);
		Column message = new Column("Message", "f","VARCHAR", 10, false,false);
		
		ArrayList<Column> rowkey2 = new ArrayList<Column>();
		ArrayList<Column> columns2 = new ArrayList<Column>();
		
		rowkey2.add(ip);  
		columns2.add(message);     
		Table table2 = new Table("X",20,rowkey2,columns2) ;
		
		
		//tableList
		tableList.add(table2);
		
		SqlBuilder sqlBuilder = new SqlBuilder();
	        sqlBuilder.setOutFile("workdir/createTable.sql");
		sqlBuilder.createSqlFile(tableList);
	}

}
