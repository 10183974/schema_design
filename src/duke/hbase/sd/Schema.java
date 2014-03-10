package duke.hbase.sd;

import java.util.ArrayList;
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

  public static int getSchemaId() {
    return schema_count++;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("---------------------\n");
    sb.append("id= " + getId() + "\n");
    Iterator<String> t_itr = getTables().keySet().iterator();
    while (t_itr.hasNext()) {
      sb.append(getTables().get(t_itr.next()).toString());
    }
    Iterator<String> r_itr = getRels().keySet().iterator();
    while (r_itr.hasNext()) {
      sb.append(getRels().get(r_itr.next()).toString());
    }
    sb.append("---------------------\n");
    return sb.toString();
  }

  public Schema clone() {
    Schema s = new Schema();
    s.setId(Schema.getSchemaId());

    HashMap<String, Table> ct = new HashMap<String, Table>();
    HashMap<String, Relation> cr = new HashMap<String, Relation>();

    // cloning tables
    Iterator<String> _t_itr = this.getTables().keySet().iterator();
    while (_t_itr.hasNext()) {
      String tkey = _t_itr.next();
      ct.put(new String(tkey), getTables().get(tkey).clone());
    }

    // relation rewiring
    Iterator<String> _r_itr = this.getRels().keySet().iterator();
    while (_r_itr.hasNext()) {
      String rkey = _r_itr.next();
      Relation _r = this.getRels().get(rkey);

      Relation rel = new Relation();
      rel.setT1(ct.get(_r.getT1().getName()));
      rel.setT2(ct.get(_r.getT2().getName()));

      ArrayList<Column> ct1_jkey = new ArrayList<Column>();
      Iterator<Column> _t1_jkey_itr = _r.getT1_jkey().iterator();
      while (_t1_jkey_itr.hasNext()) {
        Column _c = _t1_jkey_itr.next();
        String _ckey = _c.getFamily() + _c.getKey();
        Column c = ct.get(_r.getT1().getName()).getColumns().get(_ckey);
        ct1_jkey.add(c);
      }
      rel.setT1_jkey(ct1_jkey);

      ArrayList<Column> ct2_jkey = new ArrayList<Column>();
      Iterator<Column> _t2_jkey_itr = _r.getT1_jkey().iterator();
      while (_t2_jkey_itr.hasNext()) {
        Column _c = _t2_jkey_itr.next();
        String _ckey = _c.getFamily() + _c.getKey();
        Column c = ct.get(_r.getT1().getName()).getColumns().get(_ckey);
        ct2_jkey.add(c);
      }
      rel.setT2_jkey(ct2_jkey);
      rel.setCardinality(new String(_r.getCardinality()));

      cr.put(new String(rkey), rel);
    }
    s.setTables(ct);
    s.setRels(cr);
    return s;
  }

  public static void main(String[] args) throws Exception {
    Schema schema = Util.initSchema("workdir/schema.xml");
    System.out.println("---------original schema------");
    System.out.println(schema.toString());
    Schema schema_c = schema.clone();
    // modify the cloned schema
    Iterator<String> ct_itr = schema_c.getTables().keySet().iterator();
    while (ct_itr.hasNext()) {
      Table t = schema_c.getTables().get(ct_itr.next());
      Iterator<String> cc_itr = (Iterator<String>) t.getColumns().keySet().iterator();
      while (cc_itr.hasNext()) {
        String ck = cc_itr.next();
        Column c = t.getColumns().get(ck);
        c.setKey(c.getKey() + "_mod");
      }
    }
    System.out.println("---------cloned schema--------");
    System.out.println(schema_c.toString());

  }


}
