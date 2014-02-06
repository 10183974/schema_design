package duke.hbase.sd;

import java.util.ArrayList;

public class Schema {

  static int SCHEMA_COUNT = 0;

  int id = 0;
  ArrayList<Table> tables = new ArrayList<Table>();

  // to-do adding the cardinality table



  public Schema(ArrayList<Table> tables) {
    super();
    this.id = SCHEMA_COUNT++;
    this.tables = tables;
  }

  public Schema() {
    // TODO Auto-generated constructor stub
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public ArrayList<Table> getTables() {
    return tables;
  }

  public void setTables(ArrayList<Table> tables) {
    this.tables = tables;
  }

  // initialize the schema
  public Schema init(ArrayList<String> table_list) {
    Schema schema = new Schema();
    this.id = SCHEMA_COUNT++;
    return schema;

  }

}
