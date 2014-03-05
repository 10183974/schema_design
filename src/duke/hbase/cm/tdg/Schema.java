package duke.hbase.cm.tdg;
import java.util.ArrayList;

public class Schema {
	 private String name = null;
     private ArrayList<Table> tableList = null;
      
     public Schema() {
	 }
     public Schema(String name, ArrayList<Table> tableList) {
    	this.name = name;
		this.tableList = tableList;
	 }

	 public String getName() {
		return name;
	 }

	 public void setName(String name) {
		this.name = name;
	 } 
	
	 public ArrayList<Table> getTableList() {
		return tableList;
	 }

	 public void setTableList(ArrayList<Table> tableList) {
		this.tableList = tableList;
	 }


}
