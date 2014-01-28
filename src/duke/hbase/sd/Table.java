package duke.hbase.sd;

import java.util.ArrayList;

public class Table {

  String name;
  ArrayList<Column> columns = new ArrayList<Column>();
  int rowcount = 0;

  public Table() {
    // TODO Auto-generated constructor stub
  }

  public Table(String name, ArrayList<Column> columns, int rowcount) {
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

  public void setName(String name) {
    this.name = name;
  }

  public ArrayList<Column> getColumns() {
    return columns;
  }

  public void setColumns(ArrayList<Column> columns) {
    this.columns = columns;
  }

}
