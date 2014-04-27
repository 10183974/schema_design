package duke.hbase.sd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class SuperColumn implements Cloneable {
	private static int supercolumn_count = 0;

	private int id;
	private String name;
	private HashMap<String, Column> columns = new HashMap<String, Column>();
	private ArrayList<Column> columnkey = new ArrayList<Column>();
	private int numberOfSuperColumnPerRow = 1;
	
	public SuperColumn() {	
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public HashMap<String, Column> getColumns() {
		return columns;
	}
	public void setColumns(HashMap<String, Column> columns) {
		this.columns = columns;
	}
	public ArrayList<Column> getColumnkey() {
		return columnkey;
	}
	public void setColumnkey(ArrayList<Column> columnkey) {
		this.columnkey = columnkey;
	}
	public int getNumberOfSuperColumnPerRow() {
		return this.numberOfSuperColumnPerRow;
	}
	public void setNumberOfSuperColumnPerRow(int no) {
		this.numberOfSuperColumnPerRow = no;
	}
	
	public static int getSuperColumnId() {
		return supercolumn_count++;
	}
	
	public int getTotalColumnLen() {
        int totalColLen = 0;
		
		//not considering columnkey length
		Set<String> keys = getColumns().keySet();
		Iterator<String> kitr = keys.iterator();
		while (kitr.hasNext()) {
		  String key = kitr.next();
		  totalColLen += getColumns().get(key).getAverage_value_size();
		}
		
		return totalColLen;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("supercolumn: " + getName() + "\n");
		sb.append("Columns: \n");
		Iterator<String> c_itr =  getColumns().keySet().iterator();
		while(c_itr.hasNext()) {
			String key = c_itr.next();
			sb.append(getColumns().get(key));
		}
		System.out.println("columnkey: \n");
		Iterator<Column> ck_itr = getColumnkey().iterator();
		while(ck_itr.hasNext()) {
			sb.append(ck_itr.next());
		}
		
		sb.append("NumberOfSuperColumnPerRow: "+ getNumberOfSuperColumnPerRow() + "\n");
		return sb.toString();
	}
	
	public String toShortString() {
		StringBuffer sb = new StringBuffer();
		sb.append("supercolumn: " + getName() + "\n");
		sb.append("Columns:");
		Iterator<String> c_itr =  getColumns().keySet().iterator();
		while(c_itr.hasNext()) {
			String key = c_itr.next();
			sb.append(getColumns().get(key).getFamily() + getColumns().get(key).getName() + ", ");
		}
		System.out.println("\n" + "columnkey: ");
		Iterator<Column> ck_itr = getColumnkey().iterator();
		while(ck_itr.hasNext()) {
			Column col = ck_itr.next();
			sb.append(col.getFamily() + col.getName() + ", ");
		}
		
		sb.append("\n" + "NumberOfSuperColumnPerRow: "+ getNumberOfSuperColumnPerRow() + "\n");
		return sb.toString();
	}
	
	public SuperColumn clone() {
		SuperColumn sc = new SuperColumn();
		sc.setId(SuperColumn.getSuperColumnId());
		sc.setName(new String(this.getName()));
		HashMap<String, Column> columns_ = new HashMap<String, Column>(); 
		for(String key: getColumns().keySet()) {
			columns_.put(new String(key), getColumns().get(key).clone());
		}
		sc.setColumns(columns_);
		
		ArrayList<Column> columnkey_ = new ArrayList<Column>();
		for(Column column: getColumnkey()) {
			columnkey_.add(columns_.get(column.getFamily()+column.getName()));
		}
		sc.setColumnkey(columnkey_);
		
		sc.setNumberOfSuperColumnPerRow(this.getNumberOfSuperColumnPerRow());
		return sc;
	} 
}
