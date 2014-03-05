package duke.hbase.cm.tdg;
import java.util.*;
public class Table {
	private String name = null;
	private int numRows =  0;
	private int numColumns = 0;
	private int rowkeySize = 0;
	private int columnSize = 0;
	private ArrayList<Column> rowkeyList = new ArrayList<Column> ();
    private ArrayList<Column> columnList = new ArrayList<Column> ();
    
	
	public Table(String name, int numRows, int numColumns, int rowkeySize,
			int columnSize, ArrayList<Column> rowkeyList,
			ArrayList<Column> columnList) {
		super();
		this.name = name;
		this.numRows = numRows;
		this.numColumns = numColumns;
		this.rowkeySize = rowkeySize;
		this.columnSize = columnSize;
		this.rowkeyList = rowkeyList;
		this.columnList = columnList;
	}


	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getNumRows() {
		return numRows;
	}
	public void setNumRows(int numRows) {
		this.numRows = numRows;
	}
	public int getNumColumns() {
		return numColumns;
	}
	public void setNumColumns(int numColumns) {
		this.numColumns = numColumns;
	}
	public int getRowkeySize() {
		return rowkeySize;
	}
	public void setRowkeySize(int rowkeySize) {
		this.rowkeySize = rowkeySize;
	}
	public int getColumnSize() {
		return columnSize;
	}
	public void setColumnSize(int columnSize) {
		this.columnSize = columnSize;
	}
	public ArrayList<Column> getRowkeyList() {
		return rowkeyList;
	}
	public void setRowkeyList(ArrayList<Column> rowkeyList) {
		this.rowkeyList = rowkeyList;
	}
	public ArrayList<Column> getColumnList() {
		return columnList;
	}
	public void setColumnList(ArrayList<Column> columnList) {
		this.columnList = columnList;
	}
	public void printTableInfo(){
		ArrayList<Column> rowkeyList = this.getRowkeyList();
		ArrayList<Column> columnList = this.getColumnList();	
		
		System.out.println("Table information\n"
				           + "\tname: " + this.getName() + ", "				           
		                   + "nRows: " + this.getNumRows());
		for(Column r:rowkeyList){		
			  System.out.println("\tRowkey");  
                          r.printColumnInfo();
		}		
		for(Column c:columnList){
			System.out.println("\tColumns");  
                        c.printColumnInfo();
		}
	}
		
}
