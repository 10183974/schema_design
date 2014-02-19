package duke.hbase.cm.tdg;
import java.util.*;
public class Table {
	private String tableName = null;
	private int nRows = 0; 
	private ArrayList<Column> rowkey = new ArrayList<Column> ();
        private ArrayList<Column> columns = new ArrayList<Column> ();
    
	public Table(){
		//defalut table constructor
	}
	
	public Table(String aTableName, int aNRows, ArrayList<Column> aRowkey, ArrayList<Column> aColumns) {
		    this.tableName = aTableName;
		    this.nRows = aNRows;
		    this.rowkey = aRowkey;
		    this.columns = aColumns;
       }
		
	public String getTableName(){
		return this.tableName;
	}
	public int getNRows(){
		return this.nRows;
	}
	public ArrayList<Column> getRowkey(){
		return this.rowkey;
	}
	public ArrayList<Column> getColumns(){
		return this.columns;
	}
	public void setTableName(String aTableName){
		this.tableName = aTableName;
	}
	public void setNRows(int aNRows){
		this.nRows = aNRows;
	}
	public void setRowKey(ArrayList<Column> aRowkey){
		this.rowkey = aRowkey;
	}
	public void setColumns(ArrayList<Column> aColumns){
		this.columns = aColumns;
	}
	public void printTableInfo(){
		ArrayList<Column> rowkey = this.getRowkey();
		ArrayList<Column> columns = this.getColumns();	
		
		System.out.println("Table information\n"
				           + "\tname: " + this.getTableName() + ", "				           
		                   + "nRows: " + this.getNRows());
		for(Column r:rowkey){		
			  System.out.println("\tRowkey");  
                          r.printColumnInfo();
		}		
		for(Column c:columns){
			System.out.println("\tColumns");  
                        c.printColumnInfo();
		}
	}
		
	public static void main(String[] args){
		Column id = new Column("ID", " ", "INTEGER", 10, true, true);
		Column comment1 = new Column("Comment#1", "f","VARCHAR", 10,false,false);
		Column comment2 = new Column("Comment#2", "f","VARCHAR", 10,false,false);
		
                ArrayList<Column> rowkey = new ArrayList<Column>();
                rowkey.add(id);
                ArrayList<Column> columns = new ArrayList<Column>();
                columns.add(comment1);
                columns.add(comment2);
       
		Table t = new Table("Z",20,rowkey,columns) ;

		t.printTableInfo();				
	}
}
