package duke.hbase.sd;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TJoin;
import gudusoft.gsqlparser.nodes.TJoinItem;
import gudusoft.gsqlparser.nodes.TJoinItemList;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class Query {

  static int query_counter = 0;
  private int id;

  private String querystr;
  private String type;
  private double desired_latency;
  private int desired_throughput;
  private HashMap<String, String> stats;

  public Query() {
  }

  public int getDesired_throughput() {
    return desired_throughput;
  }

  public void setDesired_throughput(int desired_throughput) {
    this.desired_throughput = desired_throughput;
  }

  public HashMap<String, String> getStats() {
    return stats;
  }

  public void setStats(HashMap<String, String> features) {
    this.stats = features;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getQuerystr() {
    return querystr;
  }

  public void setQuerystr(String querystr) {
    this.querystr = querystr;
  }

  public double getDesired_latency() {
    return desired_latency;
  }

  public void setDesired_latency(double desired_latency) {
    this.desired_latency = desired_latency;
  }

  public static int getQueryId() {
    return query_counter++;
  }
  
  public int getRetNumRows(Application app) {
	  int retnumrows = 0;
	  //int retrowsize = 0;
	  if("join".equals(this.getType())) {
		  ArrayList<Table> tables = this.getTables(app);
		  //assume only two tables
		  //assume both tables are simple
		  retnumrows = tables.get(0).getRowcount() > tables.get(1).getRowcount() ? 
			  tables.get(0).getRowcount() : tables.get(1).getRowcount();  
	  }
	  else if("scan".equals(this.getType())) {
		  ArrayList<Table> tables = this.getTables(app);
		  //assume no filter
		  retnumrows = tables.get(0).getRowcount();
	  }
	  return retnumrows;
  }

  public ArrayList<Table> getTables(Application app) {
	ArrayList<Table> tables = new ArrayList<Table>();
	
    TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
	sqlparser.sqltext = this.getQuerystr();
	int ret = sqlparser.parse();
	if(ret==0) {
	  TSelectSqlStatement qstmt = (TSelectSqlStatement) sqlparser.getSqlstatements().get(0);
	  TJoin full_join_clause = qstmt.joins.getJoin(0);
	  TTable first_t = full_join_clause.getTable();
	  //System.out.println("first table " + first_t.getName() + " " + first_t.getAliasClause());
	  tables.add(app.getTables().get(first_t.getName()));
	  //System.out.println(tables.get(0).toShortString());
	  TJoinItemList join_items = full_join_clause.getJoinItems();
	  //System.out.println("join item count " + join_items.size());
	  //System.out.println(join_items);
	  for(int i=0; i<join_items.size(); i++) {
		  TJoinItem jItem = join_items.getJoinItem(i);
		  //System.out.println(jItem);
		  // System.out.println(jItem.getTable().getName());
		  tables.add(app.getTables().get(jItem.getTable().getName()));
	  }	
    }
	return tables;
  }
			
  public Integer[] getNumRows(Application app) throws Exception {
	  Integer[] numOfRows = null;
	  
	  ArrayList<Table> tables = getTables(app);
	  
	  if(tables==null) {
		  throw new Exception("Error: Query does not have any table");
	  }
	  
	  numOfRows = new Integer[tables.size()];
	  
	  for(int i=0; i< tables.size(); i++) {
		  numOfRows[i] = tables.get(i).getRowcount();
	  }
	  
	  return numOfRows;
  }
  
  public Integer[] getRowkeySize(Application app) throws Exception {
	  Integer[] rowkeysizes = null;
	  
	  ArrayList<Table> tables = getTables(app);
	  
	  if(tables==null) {
		  throw new Exception("Error: Query does not have any table");
	  }
	  
	  rowkeysizes = new Integer[tables.size()];
	  
	  for(int i=0; i< tables.size(); i++) {
		  int totalrowkeysize = 0;
		  ArrayList<Column> rowkeys = tables.get(i).getRowkey();
		  Iterator<Column> rkeysitr = rowkeys.iterator();
		  while(rkeysitr.hasNext()) {
			  Column c = rkeysitr.next();
			  totalrowkeysize += c.getAverage_value_size();
		  }
		  rowkeysizes[i] = totalrowkeysize;
	  }
	  
	  return rowkeysizes;
  }  
  
  public Integer[] getNumColumns(Application app) throws Exception {
	  Integer[] numOfColumns = null;
	  
	  ArrayList<Table> tables = getTables(app);
	  
	  if(tables==null) {
		  throw new Exception("Error: Query does not have any table");
	  }
	  
	  numOfColumns = new Integer[tables.size()];
	  
	  for(int i=0; i < tables.size(); i++) {
		  int totalNumCol = 0;
		  // counting number of columns
		  totalNumCol += tables.get(i).getColumns().size();  
		  //counting number of supercolumns
		  HashMap<String, SuperColumn> superCols = tables.get(i).getSuperColumns();
		  Set<String> superColSet = superCols.keySet();
		  for(String scKey : superColSet) {
			  totalNumCol += superCols.get(scKey).getNumberOfSuperColumnPerRow();		  
		  }
		  numOfColumns[i] = totalNumCol;
	  }
	  
	  return numOfColumns;
  }

  public Integer[] getAvgColumnSizes(Application app) throws Exception {
	  Integer[] colsizes = null;
	  
	  ArrayList<Table> tables = getTables(app);
	  
	  if(tables==null) {
		  throw new Exception("Error: Query does not have any table");
	  }
	  
	  colsizes = new Integer[tables.size()];
	  
	  for(int i=0; i < tables.size(); i++) {
		  //System.out.println("my name: " + tables.get(i).getName());
		  colsizes[i] = tables.get(i).getAverageColumnLen();
	  }
	  return colsizes;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("id: " + getId() + "\n");
    sb.append("stmt: " + getQuerystr() + "\n");
    sb.append("desired latency: " + getDesired_latency() + "\n");
    sb.append("desired throughput: " + getDesired_throughput() + "\n");
    
    Set<String> stats = this.getStats().keySet();
    Iterator<String> fitr = stats.iterator();
    while(fitr.hasNext()) {
      String prop = fitr.next();
      sb.append(prop + ": " + this.getStats().get(prop) + "\n");
    }
    return sb.toString();
  }

  
  @SuppressWarnings("unchecked")
  public Query clone() {
	  Query q = new Query();
	  q.setId(Query.getQueryId());
	  q.setQuerystr(new String(this.getQuerystr()));
	  q.setType(new String(this.getType()));
	  q.setDesired_latency(this.getDesired_latency());
	  q.setDesired_throughput(this.getDesired_throughput());
	  Iterator<String> prop_itr = this.getStats().keySet().iterator();
	  HashMap<String, String> stats = new HashMap<String, String>();
	  while(prop_itr.hasNext()) {
		  String key = prop_itr.next();
		  stats.put(new String(key), new String(this.getStats().get(key)));
	  }
	  q.setStats(stats);
	  return q;
  }
  
  public static void main(String[] args) throws Exception {
	    Application app_ini = 
	    		Util.initApplication(new String[] {"workdir/schema.xml", "workdir/workload.xml"});

	    System.out.println(app_ini.getQueries().get(0).getQuerystr());
	    Integer[] tablesizes = app_ini.getQueries().get(0).getNumRows(app_ini);
	    System.out.println("number of rows in tables");
	    for(int i=0; i<tablesizes.length; i++) {
	    	System.out.println(tablesizes[i]);
	    }
	    Integer[] colCounts = app_ini.getQueries().get(0).getNumColumns(app_ini);
	    System.out.println("number of columns in tables");
	    for(int i=0; i<colCounts.length; i++) {
	    	System.out.println(colCounts[i]);
	    }
	    System.out.println("average column size");
	    Integer[] avgcolsizes = app_ini.getQueries().get(0).getAvgColumnSizes(app_ini);
	    for(int i=0; i<avgcolsizes.length; i++) {
	    	System.out.println(avgcolsizes[i]);
	    }
	    
	    System.out.println("rowkey size");
	    Integer[] rowkeySizes = app_ini.getQueries().get(0).getRowkeySize(app_ini);
	    for(int i=0; i<rowkeySizes.length; i++) {
	    	System.out.println(rowkeySizes[i]);
	    }
  }
  
}
