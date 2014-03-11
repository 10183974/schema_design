package duke.hbase.sd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class Table implements Cloneable {

  private static int table_count = 0;

  private int id;
  private String name;
  private HashMap<String, Column> columns = new HashMap<String, Column>();
  private ArrayList<Column> rowkey = new ArrayList<Column>();
  private int rowcount = 0;


  public Table() {
    // TODO Auto-generated constructor stub
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public ArrayList<Column> getRowkey() {
    return rowkey;
  }

  public void setRowkey(ArrayList<Column> rowkey) {
    this.rowkey = rowkey;
  }

  public int getRowcount() {
    return rowcount;
  }

  public void setRowcount(int rowcount) {
    this.rowcount = rowcount;
  }

  public void setName(String name) {
    this.name = name;
  }

  public HashMap<String, Column> getColumns() {
	return columns;
}

public void setColumns(HashMap<String, Column> columns) {
	this.columns = columns;
}

public static int getTableId() {
    return table_count++;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("table name: " + getName() + "\n");
    sb.append("table columns: \n");
    Set<String> keys = getColumns().keySet();
    Iterator<String> kitr = keys.iterator();
    while (kitr.hasNext()) {
      String key = kitr.next();
      sb.append(getColumns().get(key) + "\n");
    }
    sb.append("rowkeys: \n");
    Iterator<Column> rowkeys = getRowkey().iterator();
    while (rowkeys.hasNext()) {
      Column col = rowkeys.next();
      sb.append(col + "\n");
      sb.append(col.getFamily() + col.getKey() + "\n");
    }
    sb.append("row count: " + getRowcount() + "\n");
    return sb.toString();
  }

  @SuppressWarnings("unchecked")
  public Table clone() {
    Table t = new Table();
    t.setId(Table.getTableId());
    t.setName(new String(this.getName()));

    // cloning columns
    Iterator<String> ckeyitr = this.getColumns().keySet().iterator();
    HashMap<String, Column> cols = new HashMap<String, Column>();
    while (ckeyitr.hasNext()) {
      String ckey = ckeyitr.next();
      Column c = this.getColumns().get(ckey);
      cols.put(new String(ckey), c.clone());
    }
    t.setColumns(cols);

    // cloning rowkeys
    Iterator<Column> citr = this.getRowkey().iterator();
    ArrayList<Column> rowkey = new ArrayList<Column>();
    while (citr.hasNext()) {
      Column c = citr.next();
      Column col = t.getColumns().get(c.getFamily() + c.getKey());
      rowkey.add(col);
    }
    t.setRowkey(rowkey);
    t.setRowcount(this.getRowcount());
    return t;
  }
  
  @SuppressWarnings("unchecked")
  public static void main(String[] args) throws Exception {
    Application app = Util.initApplication(new String[] {"workdir/schema.xml", "workdir/workload.xml"});
    HashMap<String, Table> tables = app.getTables();
    HashMap<String, Table> tables_cloned = new HashMap<String, Table>();
    //cloning tables
    Iterator<String> t_itr = tables.keySet().iterator();
    while(t_itr.hasNext()) {
      String tkey = t_itr.next();
      tables_cloned.put(new String(tkey), tables.get(tkey).clone());
    }

    // modifying cloned table
    Iterator<String> ct_itr = tables_cloned.keySet().iterator();
    while (ct_itr.hasNext()) {
      Table t = tables_cloned.get(ct_itr.next());
      Iterator<String> cc_itr = (Iterator<String>) t.getColumns().keySet().iterator();
      while (cc_itr.hasNext()) {
        String ck = cc_itr.next();
        Column c = t.getColumns().get(ck);
        c.setKey(c.getKey() + "_mod");
      }
    }

    System.out.println("------cloned table------");
    ct_itr = tables_cloned.keySet().iterator();
    while (ct_itr.hasNext()) {
      System.out.println(tables_cloned.get(ct_itr.next()).toString());
    }

    System.out.println("------original table----");
    Iterator<String> ot_itr = app.getTables().keySet().iterator();
    while (ot_itr.hasNext()) {
      System.out.println(app.getTables().get(ot_itr.next()).toString());
    }

  }
}
