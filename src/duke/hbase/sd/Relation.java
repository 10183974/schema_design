package duke.hbase.sd;

import java.util.ArrayList;

public class Relation {

  Table t1;
  Table t2;

  ArrayList<Column> t1_jkey;

  public String getCardinality() {
    return cardinality;
  }

  public void setCardinality(String cardinality) {
    this.cardinality = cardinality;
  }

  ArrayList<Column> t2_jkey;

  String cardinality;

  public Table getT1() {
    return t1;
  }

  public void setT1(Table t1) {
    this.t1 = t1;
  }

  public Relation(Table t1, Table t2, ArrayList<Column> t1_jkey, ArrayList<Column> t2_jkey) {
    super();
    this.t1 = t1;
    this.t2 = t2;
    this.t1_jkey = t1_jkey;
    this.t2_jkey = t2_jkey;
  }

  public Table getT2() {
    return t2;
  }

  public void setT2(Table t2) {
    this.t2 = t2;
  }

  public ArrayList<Column> getT1_jkey() {
    return t1_jkey;
  }

  public void setT1_jkey(ArrayList<Column> t1_jkey) {
    this.t1_jkey = t1_jkey;
  }

  public ArrayList<Column> getT2_jkey() {
    return t2_jkey;
  }

  public void setT2_jkey(ArrayList<Column> t2_jkey) {
    this.t2_jkey = t2_jkey;
  }

}
