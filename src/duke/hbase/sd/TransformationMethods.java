package duke.hbase.sd;

import java.util.ArrayList;

public class TransformationMethods {

  public Table join(Table t1, Table t2) {
    return new Table();
  }

  public ArrayList<Table> split(Table t) {
    ArrayList<Table> tables = new ArrayList<Table>();
    return tables;
  }

  public Table promoteAttribute(Table t, Column c) {
    return new Table();
  }
}
