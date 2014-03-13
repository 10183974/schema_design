package duke.hbase.sd;

import java.util.ArrayList;

public class TransformationMethods {

  public Table singleColf2multiColf(Table _t) {
    Table t = (Table) _t.clone();

    return null;
  }

  public Table multiColf2singleColf() {
    return null;
  }

  public Table colValuePromo2rowkey() {
    return null;
  }

  public Table uniqueValPromo2rowkey() {
    return null;
  }

  public Table colValuePromo2colkey() {
    return null;
  }

  public Table keySalting() {
    return null;
  }

  public Table join(Table t1, Table t2, ArrayList<Column> t1_jkey, ArrayList<Column> t2_jkey) {
    Table t = new Table();
    return t;
  }

  public ArrayList<Table> split(Table t) {
    ArrayList<Table> ts = new ArrayList<Table>();
    return ts;
  }

  public Table nesting(Table t1, Table t2) {
    return null;
  }

  public ArrayList<Table> denesting(Table t) {
    return null;
  }

}
