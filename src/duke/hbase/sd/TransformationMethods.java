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

  public Application join(Application app, Query q, Table t1, 
		  ArrayList<Column> t1_jkeys, Table t2, ArrayList<Column> t2_jkeys) {
    Application new_app = new Application();
	  //new_app.removeTable(((Table)tr_args.get(0)).getName());
	  //new_app.removeTable(((Table)tr_args.get(2)).getName());
	  //new_app.addTable(new_table)
    return new_app;
  }

  public ArrayList<Table> split(Table t) {
    ArrayList<Table> ts = new ArrayList<Table>();
    return ts;
  }

  public Application nesting(Application app, Query q, Table t1,
		  ArrayList<Column> t1_jkeys, Table t2, ArrayList<Column> t2_jkeys) {
	    Application new_app = new Application();
	    return new_app;
  }

  public ArrayList<Table> denesting(Table t) {
    return null;
  }

}
