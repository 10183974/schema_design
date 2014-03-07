package duke.hbase.sd;

import java.util.HashMap;
import java.util.Iterator;

public class Schema {

  public static int schema_count = 0;
  private int id = 0;
  private HashMap<String, Table> tables = new HashMap<String, Table>();
  private HashMap<String, Relation> rels = new HashMap<String, Relation>();

  public Schema(HashMap<String, Table> tables) {
    super();
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

  public HashMap<String, Table> getTables() {
    return tables;
  }

  public void setTables(HashMap<String, Table> tables) {
    this.tables = tables;
  }

  public HashMap<String, Relation> getRels() {
    return rels;
  }

  public void setRels(HashMap<String, Relation> rels) {
    this.rels = rels;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("id=" + getId() + "\n");
    Iterator<String> t_itr = tables.keySet().iterator();
    while (t_itr.hasNext()) {
      sb.append(tables.get(t_itr.next()).toString());
    }
    Iterator<String> r_itr = rels.keySet().iterator();
    while (r_itr.hasNext()) {
      sb.append(tables.get(r_itr.next()).toString());
    }
    return sb.toString();
  }

}
