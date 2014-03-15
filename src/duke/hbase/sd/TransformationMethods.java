package duke.hbase.sd;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TSourceTokenList;
import gudusoft.gsqlparser.nodes.IExpressionVisitor;
import gudusoft.gsqlparser.nodes.TAliasClause;
import gudusoft.gsqlparser.nodes.TJoin;
import gudusoft.gsqlparser.nodes.TJoinItem;
import gudusoft.gsqlparser.nodes.TJoinItemList;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TParseTreeNode;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.nodes.TParseTreeVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

public class TransformationMethods {
	
    private static final String CHAR_LIST = 
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
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
  
  private String getRandomString() {
      StringBuffer randStr = new StringBuffer();
      for(int i=0; i<3; i++){
          int number = getRandomNumber();
          char ch = CHAR_LIST.charAt(number);
          randStr.append(ch);
      }
      return randStr.toString();
  }
  
 private int getRandomNumber() {
     int randomInt = 0;
     Random randomGenerator = new Random();
     randomInt = randomGenerator.nextInt(CHAR_LIST.length());
     if (randomInt - 1 == -1) {
         return randomInt;
     } else {
         return randomInt - 1;
     }
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

  public Application join(Application _app, Query tr_q, Table t1, 
		  ArrayList<Column> t1_jkeys, Table t2, ArrayList<Column> t2_jkeys) throws Exception{
    
	 System.out.println("table1: " + t1.getName() + "table2: " + t2.getName()); 
	Application app = _app.clone();

    //Query rewrite
    Iterator<Query> _q_itr = _app.getQueries().iterator();
    app.setQueries(new ArrayList<Query>());
    while(_q_itr.hasNext()) {
    	Query _q = _q_itr.next();
		Query q = _q.clone();
    	boolean _t1matched = _q.getQuerystr().matches("^.*\\s" + t1.getName() + "\\s.*$");
    	boolean _t2matched = _q.getQuerystr().matches("^.*\\s" + t2.getName() + "\\s.*$");
    	String t = t1.getName()+"-"+t2.getName();
    	String talias = getRandomString();
		String _t1alias=null, _t2alias=null;
    	if(_t1matched || _t2matched) {
    		TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
			sqlparser.sqltext = q.getQuerystr();
			int ret = sqlparser.parse();
    		switch(_q.getType()) {
    		case "read":
    		case "scan":
    			if(ret==0) {
    				TSelectSqlStatement qstmt = (TSelectSqlStatement) sqlparser.getSqlstatements().get(0);
    				TSourceTokenList stl = qstmt.sourcetokenlist;
    				StringBuffer q_r = new StringBuffer();
    				while(stl.hasNext()) {
    					String token = stl.next().toString();
    					if(token.equals(t1.getName()) || token.equals(t2.getName())) {
        					q_r.append(t);
    					}
    				}
    				System.out.println("Before: " + q.getQuerystr());
        			q.setQuerystr(q_r.toString());
        			System.out.println("After: " + q.getQuerystr());
    			}
    			else {
    				throw new Exception("Error: parsing error " + q);
    			}
    			break;	
    		case "write":
    			break;
    		case "update":
    			break;
    		case "join":
    			if(ret==0) {
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
        			
    				System.out.println("Before " + q.getQuerystr());
    				q.setQuerystr(q_r.toString());
    				System.out.println("After " + q.getQuerystr());
    			}
    			
    			else {
    				throw new Exception("Error: parsing error " + q);
    			}
    			break;
    		default:
    			System.out.println("Error: invalid query type " + _q);
    		}
    	}
    	app.getQueries().add(q);
    }
    
    
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
