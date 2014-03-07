package duke.hbase.sd;

import java.util.ArrayList;
import java.util.HashMap;

public class Table {

  static int table_count = 0;

  String name;
  HashMap<String, Column> columns = new HashMap<String, Column>();
  ArrayList<Column> rowkey = new ArrayList<Column>();
  int rowcount = 0;

  public Table() {
    // TODO Auto-generated constructor stub
  }

  public Table(String name, HashMap<String, Column> columns, int rowcount) {
    super();
    this.name = name;
    this.columns = columns;
    this.rowcount = rowcount;
  }

  // initialize table object from hbase table
  public Table init(String tablename) {
    Table table = new Table();
    return table;
  }

  public String getName() {
    return name;
  }

  public ArrayList<Column> getRowkey() {
    return rowkey;
  }

  public void setRowkey(ArrayList<Column> rowkey) {
    this.rowkey = rowkey;
  }

  public int getRowcount() {
    return rowcount;
  }

  public void setRowcount(int rowcount) {
    this.rowcount = rowcount;
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

}
