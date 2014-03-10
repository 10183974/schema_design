package duke.hbase.sd;

import java.util.ArrayList;
import java.util.Iterator;

public class Relation {

  private static int relation_count;
  private int id;
  private int schema_id;
  private Table t1;
  private Table t2;
  private ArrayList<Column> t1_jkey;
  private ArrayList<Column> t2_jkey;
  private String cardinality;

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("Relation: \n");
    sb.append("id= " + getId() + "\n");
    sb.append("schema_id= " + getSchema_id() + "\n");
    sb.append("table1 info \n" + getT1());
    sb.append("table2 info \n" + getT2());
    Iterator<Column> t1_jkey_itr = getT1_jkey().iterator();
    sb.append("table1 joinkeys \n");
    while (t1_jkey_itr.hasNext()) {
      sb.append(t1_jkey_itr.next() + "\n");
    }
    Iterator<Column> t2_jkey_itr = getT2_jkey().iterator();
    sb.append("table2 joinkeys \n");
    while (t2_jkey_itr.hasNext()) {
      sb.append(t2_jkey_itr.next() + "\n");
    }
    sb.append("cardinality: " + getCardinality() + "\n");
    return sb.toString();
  }

  public Relation() {
  }

  public Relation(Table t1, Table t2, ArrayList<Column> t1_jkey, ArrayList<Column> t2_jkey) {
    super();
    this.t1 = t1;
    this.t2 = t2;
    this.t1_jkey = t1_jkey;
    this.t2_jkey = t2_jkey;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getSchema_id() {
    return schema_id;
  }

  public void setSchema_id(int schema_id) {
    this.schema_id = schema_id;
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

  public String getCardinality() {
    return cardinality;
  }

  public void setCardinality(String cardinality) {
    this.cardinality = cardinality;
  }

  public Table getT1() {
    return t1;
  }

  public void setT1(Table t1) {
    this.t1 = t1;
  }

  public static int getRelationId() {
    return relation_count++;
  }

}
