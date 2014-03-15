package duke.hbase.sd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Application {

  public static int application_count = 0;
  private int id = 0;
  private HashMap<String, Table> tables = new HashMap<String, Table>();
  private HashMap<String, Relation> rels = new HashMap<String, Relation>();
  private ArrayList<Query> queries = new ArrayList<Query>();

  public Application(HashMap<String, Table> tables) {
    super();
    this.tables = tables;
  }

  public Application() {
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

  public ArrayList<Query> getQueries() {
	return queries;
}

public void setQueries(ArrayList<Query> queries) {
	this.queries = queries;
}

public HashMap<String, Relation> getRels() {
    return rels;
  }

  public void setRels(HashMap<String, Relation> rels) {
    this.rels = rels;
  }

  public static int getApplicationId() {
    return application_count++;
  }
  
  public void removeTable(String table) {
	  this.getTables().remove(table);
  }
  
  public void addTable(Table t) {
	  this.getTables().put(t.getName(), t);
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
    Iterator<Query> q_itr = getQueries().iterator();
    while(q_itr.hasNext()) {
      sb.append(q_itr.next().toString());
    }
    sb.append("---------------------\n");
    return sb.toString();
  }
  
  public void printQueries() {
	  Iterator<Query> q_itr = getQueries().iterator();
	    while(q_itr.hasNext()) {
	      System.out.println(q_itr.next().toString());
	    }
  }

  public Application clone() {
    Application s = new Application();
    s.setId(Application.getApplicationId());

    HashMap<String, Table> ct = new HashMap<String, Table>();
    HashMap<String, Relation> cr = new HashMap<String, Relation>();
    ArrayList<Query> cq = new ArrayList<Query>();

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
    
    //cloning Queries
    Iterator<Query> q_itr = this.getQueries().iterator();
    while(q_itr.hasNext()) {
    	Query _q = q_itr.next();
    	cq.add(_q.clone());
    }
    
    s.setTables(ct);
    s.setRels(cr);
    s.setQueries(cq);
    return s;
  }

  public static void main(String[] args) throws Exception {
    Application app = Util.initApplication(new String[] {"workdir/schema.xml", "workdir/workload.xml"});
    Application app_c = app.clone();
    // modify the cloned schema
    Iterator<String> ct_itr = app_c.getTables().keySet().iterator();
    while (ct_itr.hasNext()) {
      Table t = app_c.getTables().get(ct_itr.next());
      Iterator<String> cc_itr = (Iterator<String>) t.getColumns().keySet().iterator();
      while (cc_itr.hasNext()) {
        String ck = cc_itr.next();
        Column c = t.getColumns().get(ck);
        c.setKey(c.getKey() + "_mod");
      }
    }
    //modify the cloned query
    Iterator<Query> cq_itr = app_c.getQueries().iterator();
    while(cq_itr.hasNext()) {
      Query cq = cq_itr.next();
      HashMap<String, String> cstats = cq.getStats();
      cstats.put("clone_check", "dummy");
      cstats.put("rows_scanned", "10000");
      cq.setStats(cstats);
    }

    System.out.println("---------cloned schema--------");
    System.out.println(app_c.toString());
    System.out.println("---------original schema------");
    System.out.println(app.toString());
  }
}
