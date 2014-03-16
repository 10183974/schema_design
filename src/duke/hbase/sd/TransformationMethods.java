package duke.hbase.sd;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TSourceTokenList;
import gudusoft.gsqlparser.nodes.TJoin;
import gudusoft.gsqlparser.nodes.TJoinItem;
import gudusoft.gsqlparser.nodes.TJoinItemList;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

import java.util.ArrayList;
import java.util.Iterator;

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
 
 private String substitute(String token, String t1name, String t1alias, 
		 String t2name, String t2alias, String tname, String talias) {
	 if(token.equals(t1name) || token.equals(t2name)) {
		 return tname;
	 }
	 else if(token.equals(t1alias) || token.equals(t2alias)) {
		 return talias;
	 }
	return token;
 }

  // replace t1 and t2 in query by t
  private String renameTable(TGSqlParser sqlparser, String t1, String t2, String t) {
    System.out.println("Before rewrite: " + sqlparser.sqltext);
    TSelectSqlStatement qstmt = (TSelectSqlStatement) sqlparser.getSqlstatements().get(0);
    TSourceTokenList stl = qstmt.sourcetokenlist;
    StringBuffer q_r = new StringBuffer();
    while (stl.hasNext()) {
      String token = stl.next().toString();
      if (token.equals(t1) || token.equals(t2)) {
        q_r.append(t);
      }
 else {
        q_r.append(token);
      }

    }
    System.out.println("After rewrite: " + q_r);
    return q_r.toString();
 }

  // replace t1 and t2 in query by t
  // remove the join clause between these two table
  // rewrite the alias
  private void renameTablesAndRemoveJoinItem(TGSqlParser sqlparser, Query q, Table t1, Table t2,
      String t) {
    System.out.println("Before rewrite: " + sqlparser.sqltext);
    String talias = Util.getRandomString();
    String _t1alias = null, _t2alias = null;
    TSelectSqlStatement qstmt = (TSelectSqlStatement) sqlparser.getSqlstatements().get(0);
    TJoin full_join_clause = qstmt.joins.getJoin(0);
    TTable first_t = full_join_clause.getTable();
    if(first_t.toString().equals(t1.getName())) {
      _t1alias = first_t.getAliasClause().toString();
    }
    if(first_t.toString().equals(t2.getName())) {
      _t2alias = first_t.getAliasClause().toString();
    }
    TJoinItemList join_items = full_join_clause.getJoinItems();
    for(int i=0; i<join_items.size(); i++) {
      TJoinItem join_item = (TJoinItem)join_items.elementAt(i);
      TTable next_t = join_item.getTable();
      if(next_t.getTableName().toString().equals(t1.getName())) {
        _t1alias = next_t.getAliasClause().toString();
      }
      if(next_t.getTableName().toString().equals(t2.getName())) {
        _t2alias = next_t.getAliasClause().toString();
        }
    }
    
    TSourceTokenList stl = qstmt.sourcetokenlist;
    StringBuffer q_r = new StringBuffer();
    ArrayList<StringBuffer> sq_r = new ArrayList<StringBuffer>();
    boolean joinItemStarted = false;
    boolean whereStarted = false;
    int joinItemIndex = -1;
    while(stl.hasNext())
    {
      String token = stl.next().toString();
      
      if("join".equals(token)) {
        joinItemStarted = true;
        joinItemIndex++;
      }
      if("where".equals(token)) {
        joinItemStarted = false;
        whereStarted = true;
        for(StringBuffer ji : sq_r) {
          //System.out.println("debug " + ji.toString().split("\\s"));
          String tname = ji.toString().split("\\s")[1];
          if(!t.equals(tname)) {
              q_r.append(ji);
          }
        }
      }
      if(joinItemStarted) { 
        if(sq_r.size() - 1 < joinItemIndex) {
          sq_r.add(new StringBuffer());
        }
        sq_r.get(joinItemIndex).append(substitute(token, t1.getName(),  _t1alias, t2.getName(), _t2alias, t, talias));
      }
      else {
        q_r.append(substitute(token, t1.getName(), _t1alias, t2.getName(), _t2alias, t, talias));
      }
      //System.out.println("--->" + q_r);
    }
    if(!whereStarted) {
      for(StringBuffer ji : sq_r) {
        //System.out.println("debug " + ji.toString().split("\\s"));
        String tname = ji.toString().split("\\s")[1];
      if(!t.equals(tname)) {
        q_r.append(ji);
      }
      }
    }
    
    if(sq_r.size()==0) {
      q.setType("scan");
    }
    System.out.println("After rewrite: " + q_r.toString());
    q.setQuerystr(q_r.toString());
  }
  
  private String updateNameAndFields(TGSqlParser sqlparser, Table t1, Table t2,
      ArrayList<Column> t1_jkeys, ArrayList<Column> t2_jkeys, String t) {
    System.out.println("Before rewrite: " + sqlparser.sqltext);
    StringBuffer sb = new StringBuffer();
    sb.append("insert into " + t + " values (");
    for (int i = 0; i < t1.getColumns().size() + t2.getColumns().size() - t1_jkeys.size(); i++) {
      sb.append("?, ");
    }
    sb.delete(sb.length() - 2, sb.length());
    sb.append(")");
    System.out.println("After rewrite: " + sb.toString());
    return sb.toString();
  }

  public Application join(Application _app, Query tr_q, Table t1, 
		  ArrayList<Column> t1_jkeys, Table t2, ArrayList<Column> t2_jkeys) throws Exception{
    
    System.out.println("Tables being joined: table1=" + t1.getName() + " table2: " + t2.getName());
    Application app = _app.clone();

    // 1. Rewrite the queries
    Iterator<Query> _q_itr = _app.getQueries().iterator();
    app.setQueries(new ArrayList<Query>());

    while(_q_itr.hasNext()) {
    	Query _q = _q_itr.next();
      Query q = _q.clone();

      boolean ist1matched = _q.getQuerystr().matches("^.*\\s" + t1.getName() + "\\s.*$");
      boolean ist2matched = _q.getQuerystr().matches("^.*\\s" + t2.getName() + "\\s.*$");

    	String t = t1.getName()+"-"+t2.getName();

      // rewrite the Query q
      if (ist1matched || ist2matched) {
    		TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = q.getQuerystr();
        int ret = sqlparser.parse();

        if (ret != 0) {
          throw new Exception("Error: parsing error " + q);
        }

    		switch(_q.getType()) {
    		case "read":
            q.setQuerystr(renameTable(sqlparser, t1.getName(), t2.getName(), t));
          break;
    		case "scan":
            q.setQuerystr(renameTable(sqlparser, t1.getName(), t2.getName(), t));
    			break;	
    		case "write":
          q.setQuerystr(updateNameAndFields(sqlparser, t1, t2, t1_jkeys, t2_jkeys, t));
    			break;
    		case "update":
          // not handled correctly
          q.setQuerystr(updateNameAndFields(sqlparser, t1, t2, t1_jkeys, t2_jkeys, t));
    			break;
    		case "join":
          renameTablesAndRemoveJoinItem(sqlparser, q, t1, t2, t);
    			break;
    		default:
    			System.out.println("Error: invalid query type " + _q);
    		}
    	}
    	app.getQueries().add(q);
    }
    
    //find the relation that lead to this join operation and remove this relation
    Iterator<String> r_itr = app.getRels().keySet().iterator();
    String cardinalityOfJoin = "";
    Relation _joinrel = null;
    String _joinrelkey = null;
    while(r_itr.hasNext()) {
    	_joinrelkey = r_itr.next();
    	_joinrel = app.getRels().get(_joinrelkey);
    	if((_joinrel.getT1().getName().equals(t1.getName()) &&
    			_joinrel.getT2().getName().equals(t2.getName())) ||
    	  (_joinrel.getT2().getName().equals(t1.getName()) &&
    	   _joinrel.getT1().getName().equals(t2.getName()))) {
    		cardinalityOfJoin = _joinrel.getCardinality();
    	}
    }
    
    app.getRels().remove(_joinrelkey);
    
    int _t1card = Integer.parseInt(cardinalityOfJoin.split(":")[0]);
    int _t2card = Integer.parseInt(cardinalityOfJoin.split(":")[1]);
    
    if(_t2card==1) {
    	Table temp = t1;
    	t1 = t2;
    	t2 = temp;
    	
    	ArrayList<Column> temp_jkeys = t1_jkeys;
    	t1_jkeys = t2_jkeys;
    	t2_jkeys = temp_jkeys;
    }
    
    
    //1. adding new table
    Table t = t2.clone();
    t.setName(t1.getName()+"-"+t2.getName());
    for(String colkey: app.getTables().get(t1.getName()).getColumns().keySet()) {
    	if(!t1_jkeys.contains(app.getTables().get(t1.getName()).getColumns().get(colkey))) {
    		t.getColumns().put(colkey, t1.getColumns().get(colkey).clone());
    	}
    }
    
    app.getTables().put(t.getName(), t);

    //2. rewiring other relationships
    for (String _rel : _app.getRels().keySet()) {
      if (!_rel.equals(_joinrelkey)) {
        Relation _relation = app.getRels().get(_rel);
        if (_relation.getT1().getName().equals(t1.getName())) {
          app.getRels().get(_rel).setT1(t);
        } else if (_relation.getT1().getName().equals(t2.getName())) {
          app.getRels().get(_rel).setT1(t);
        }
        if (_relation.getT2().getName().equals(t1.getName())) {
          app.getRels().get(_rel).setT2(t);
        } else if (_relation.getT2().getName().equals(t2.getName())) {
          app.getRels().get(_rel).setT2(t);
        }
      }
    }
    
    //3.deleting old tables
    app.getTables().remove(t1.getName());
    app.getTables().remove(t2.getName());
    
    return app;
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
